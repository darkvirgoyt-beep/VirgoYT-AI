import jwt from 'jsonwebtoken';
import { randomUUID, randomBytes } from 'crypto';
import {
  createUser,
  findUserByEmail,
  findUserById,
  verifyPasswordSync,
  createSession as dbCreateSession,
} from '../db/Database.js';

const SECRET = process.env.JWT_SECRET ?? 'virgoyt-dev-secret-change-me';
const TOKEN_TTL = '7d';

export type AuthPayload = {
  userId: number;
  email: string;
  name: string;
};

export function signToken(payload: AuthPayload): string {
  return jwt.sign(payload, SECRET, { expiresIn: TOKEN_TTL });
}

export function verifyToken(token: string): AuthPayload | null {
  try {
    return jwt.verify(token, SECRET) as AuthPayload;
  } catch {
    return null;
  }
}

export function extractToken(header?: string): string | null {
  if (!header || !header.startsWith('Bearer ')) return null;
  return header.slice(7);
}

export function newSessionId(): string {
  return randomUUID().replace(/-/g, '').slice(0, 24);
}

export async function registerUser(email: string, password: string, name: string) {
  const existing = await findUserByEmail(email);
  if (existing) {
    const err = new Error('Email already registered') as Error & { status?: number };
    err.status = 409;
    throw err;
  }
  const user = await createUser(email, password, name);
  const sessionId = newSessionId();
  await dbCreateSession(user.id, sessionId);
  const token = signToken({ userId: user.id, email: user.email, name: user.name });
  return { token, user: { id: user.id, email: user.email, name: user.name }, sessionId };
}

export async function loginUser(email: string, password: string) {
  const user = await findUserByEmail(email);
  if (!user || !verifyPasswordSync(user, password)) {
    const err = new Error('Invalid email or password') as Error & { status?: number };
    err.status = 401;
    throw err;
  }
  const sessionId = newSessionId();
  await dbCreateSession(user.id, sessionId);
  const token = signToken({ userId: user.id, email: user.email, name: user.name });
  return { token, user: { id: user.id, email: user.email, name: user.name }, sessionId };
}

export function googleClientId(): string | null {
  return process.env.GOOGLE_CLIENT_ID ?? null;
}

// Log in (or auto-register) a user validated by a Google ID token.
export async function googleLogin(googleEmail: string, googleName: string) {
  const email = googleEmail.toLowerCase();
  let user = await findUserByEmail(email);
  if (!user) {
    const randomPass = randomBytes(24).toString('base64');
    user = await createUser(email, randomPass, googleName || email.split('@')[0] || 'Google User');
  }
  const sessionId = newSessionId();
  await dbCreateSession(user.id, sessionId);
  const token = signToken({ userId: user.id, email: user.email, name: user.name });
  return { token, user: { id: user.id, email: user.email, name: user.name }, sessionId, via: 'google' };
}

export async function getUserFromToken(token: string) {
  const payload = verifyToken(token);
  if (!payload) return null;
  const user = await findUserById(payload.userId);
  if (!user) return null;
  return { id: user.id, email: user.email, name: user.name };
}
