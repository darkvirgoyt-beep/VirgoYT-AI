// Verifies Google ID tokens using Google's public certificate JWKS endpoint.
// Secure Google Sign-In: browser sends a Google ID token; we validate its
// signature + audience + expiry here against Google's certificates. The client
// secret is never exposed to the browser.

import { createPublicKey, verify } from 'crypto';

const GOOGLE_CERTS = process.env.GOOGLE_CERTS_URL ?? 'https://www.googleapis.com/oauth2/v3/certs';

type JwkKey = { kty?: string; kid?: string; n?: string; e?: string; alg?: string };
type CertCache = { keys: JwkKey[]; fetchedAt: number };

let cache: CertCache = { keys: [], fetchedAt: 0 };

async function getKeys(): Promise<JwkKey[]> {
  if (cache.keys.length && Date.now() - cache.fetchedAt < 12 * 60 * 60 * 1000) return cache.keys;
  try {
    const res = await fetch(GOOGLE_CERTS, { signal: AbortSignal.timeout(10000) });
    const json = (await res.json()) as { keys?: JwkKey[] };
    cache = { keys: json.keys ?? [], fetchedAt: Date.now() };
  } catch {
    /* keep stale cache on network failure */
  }
  return cache.keys;
}

function base64UrlDecode(s: string): Buffer {
  return Buffer.from(s.replace(/-/g, '+').replace(/_/g, '/'), 'base64');
}

function parseJwt(token: string): { header: any; payload: any; sig: Buffer } | null {
  const parts = token.split('.');
  if (parts.length !== 3) return null;
  try {
    return {
      header: JSON.parse(base64UrlDecode(parts[0]).toString('utf8')),
      payload: JSON.parse(base64UrlDecode(parts[1]).toString('utf8')),
      sig: base64UrlDecode(parts[2]),
    };
  } catch {
    return null;
  }
}

// Convert a JWKS RSA key (n/e) into a PEM public key for signature verification.
function jwkToPem(key: JwkKey): string {
  const n = BigInt('0x' + Buffer.from(key.n ?? '', 'base64url').toString('hex'));
  const e = BigInt('0x' + Buffer.from(key.e ?? 'AQAB', 'base64url').toString('hex'));

  const toDER = (i: bigint): Buffer => {
    let hex = i.toString(16);
    if (hex.length % 2) hex = '0' + hex;
    const bytes = Buffer.from(hex, 'hex');
    return Buffer.concat([Buffer.from([0x02]), lengthEnc(bytes.length), bytes]);
  };

  const lengthEnc = (len: number): Buffer => {
    if (len < 128) return Buffer.from([len]);
    const bytes: number[] = [];
    let l = len;
    while (l > 0) {
      bytes.unshift(l & 0xff);
      l >>= 8;
    }
    return Buffer.from([0x81, bytes.length, ...bytes]);
  };

  const seq = (children: Buffer[]): Buffer => {
    const body = Buffer.concat(children);
    return Buffer.concat([Buffer.from([0x30]), lengthEnc(body.length), body]);
  };

  const der = seq([
    Buffer.from([
      0x30, 0x0d, 0x06, 0x09, 0x2a, 0x86, 0x48, 0x86, 0xf7, 0x0d, 0x01, 0x01,
      0x01, 0x05, 0x00,
    ]),
    seq([toDER(e), toDER(n)]),
  ]);

  const b64 = der.toString('base64');
  const lines = b64.match(/.{1,64}/g) ?? [b64];
  return `-----BEGIN PUBLIC KEY-----\n${lines.join('\n')}\n-----END PUBLIC KEY-----`;
}

export async function verifyGoogleIdToken(
  idToken: string,
  expectedAudience: string
): Promise<{ email: string; name: string; picture?: string } | null> {
  const parsed = parseJwt(idToken);
  if (!parsed) return null;
  const { header, payload, sig } = parsed;

  // Static checks: audience, issuer, expiry
  if (payload.aud !== expectedAudience) return null;
  if (payload.iss !== 'accounts.google.com' && payload.iss !== 'https://accounts.google.com') return null;
  if (typeof payload.exp !== 'number' || payload.exp * 1000 < Date.now()) return null;
  if (!payload.email) return null;

  const keys = await getKeys();
  const key = keys.find((k) => k.kid === header.kid);
  if (!key || !key.n || !key.e) return null;

  // Verify the RSA signature of the signed input (header.payload)
  const input = Buffer.from(idToken.split('.').slice(0, 2).join('.'), 'utf8');
  const publicKey = createPublicKey(jwkToPem(key));

  try {
    const ok = verify(null, input, publicKey, sig);
    if (!ok) return null;
  } catch {
    return null;
  }

  return { email: payload.email, name: payload.name ?? payload.email, picture: payload.picture };
}
