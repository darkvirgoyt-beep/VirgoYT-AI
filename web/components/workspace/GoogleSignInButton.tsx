'use client';

import { useEffect, useRef, useState } from 'react';
import { useAuthStore } from '@/stores/auth';

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (cfg: { client_id: string; callback: (r: { credential: string }) => void }) => void;
          renderButton: (el: HTMLElement, opts: object) => void;
          prompt: () => void;
        };
      };
    };
  }
}

// Script origin constants valid for Google Identity Services
const GIS_SCRIPT = 'https://accounts.google.com/gsi/client';

export default function GoogleSignInButton() {
  const { googleEnabled, setGoogleEnabled, loginWithGoogle, busy, error } = useAuthStore();
  const [clientId] = useState(() => process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID ?? '');
  const [state, setState] = useState<'loading' | 'ready' | 'missing'>('loading');
  const [failMsg, setFailMsg] = useState<string | null>(null);

  // Load Google Identity Services once
  useEffect(() => {
    const load = async () => {
      if (!clientId) {
        setState('missing');
        return;
      }
      if (window.google?.accounts?.id) {
        setState('ready');
        setGoogleEnabled(true);
        return;
      }
      const el = document.createElement('script');
      el.src = GIS_SCRIPT;
      el.async = true;
      el.defer = true;
      el.onload = () => {
        try {
          window.google!.accounts.id.initialize({
            client_id: clientId,
            callback: async (resp) => {
              if (!resp?.credential) {
                setFailMsg('Google did not return a token.');
                return;
              }
              const ok = await loginWithGoogle(resp.credential);
              if (ok) setFailMsg(null);
            },
          });
          setState('ready');
          setGoogleEnabled(true);
        } catch {
          setState('missing');
        }
      };
      el.onerror = () => setState('missing');
      document.head.appendChild(el);
    };
    load();
  }, [clientId, loginWithGoogle, setGoogleEnabled]);

  if (state === 'loading') {
    return (
      <button disabled className="w-full px-4 py-3 rounded-lg border border-white/15 text-gray-400 text-sm opacity-60">
        Loading Google Sign-In…
      </button>
    );
  }

  if (state === 'missing') {
    return (
      <div className="w-full px-4 py-3 rounded-lg border border-amber-400/30 bg-amber-400/5 text-amber-200/80 text-xs leading-relaxed">
        Google login is not configured. Set{' '}
        <code className="text-amber-200">NEXT_PUBLIC_GOOGLE_CLIENT_ID</code> on the web app to enable sign-in.
      </div>
    );
  }

  return (
    <div className="w-full flex flex-col gap-2">
      <GoogleButton clientId={clientId} />
      {busy && <p className="text-xs text-gray-400">Verifying with Google…</p>}
      {(error || failMsg) && (
        <p className="text-xs text-rose-400">{error ?? failMsg}</p>
      )}
    </div>
  );
}

function GoogleButton({ clientId }: { clientId: string }) {
  const ref = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (ref.current && window.google?.accounts?.id) {
      try {
        window.google.accounts.id.renderButton(ref.current, {
          type: 'standard',
          theme: 'filled_black',
          size: 'large',
          width: 280,
          shape: 'pill',
          text: 'continue_with',
        });
      } catch {
        /* ignore */
      }
    }
  }, [clientId]);
  return <div ref={ref} className="flex justify-center" />;
}
