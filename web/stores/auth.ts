'use client';

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { API_URL } from '@/lib/api';

export type VirgoUser = { id: number; email: string; name: string };

type GoogleLoginResult = {
  token: string;
  user: VirgoUser;
  via: string;
};

type AuthState = {
  token: string | null;
  user: VirgoUser | null;
  error: string | null;
  busy: boolean;
  googleEnabled: boolean;
  setGoogleEnabled: (enabled: boolean) => void;
  loginWithGoogle: (idToken: string) => Promise<boolean>;
  logout: () => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      error: null,
      busy: false,
      googleEnabled: false,
      setGoogleEnabled: (enabled) => set({ googleEnabled: enabled }),
      loginWithGoogle: async (idToken) => {
        if (get().busy) return false;
        set({ busy: true, error: null });
        try {
          const res = await fetch(`${API_URL}/api/auth/google`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idToken }),
          });
          const body = (await res.json()) as GoogleLoginResult | { error?: string };
          if (!res.ok) {
            const msg = (body as { error?: string }).error ?? 'Google sign-in failed';
            set({ busy: false, error: msg });
            return false;
          }
          const data = body as GoogleLoginResult;
          set({ token: data.token, user: data.user, busy: false, error: null });
          return true;
        } catch {
          set({ busy: false, error: 'Cannot reach the backend. Is the server deployed?' });
          return false;
        }
      },
      logout: () => set({ token: null, user: null, error: null }),
    }),
    { name: 'virgo-auth', partialize: (s) => ({ token: s.token, user: s.user }) }
  )
);
