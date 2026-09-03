export const API_URL =
  process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

export type ApiError = {
  error: string;
  message?: string;
};

async function handle<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const body = (await res.json().catch(() => ({}))) as ApiError;
    throw new Error(body.message ?? body.error ?? `Request failed (${res.status})`);
  }
  return res.json() as Promise<T>;
}

export const api = {
  get: <T>(path: string, token?: string) =>
    fetch(`${API_URL}${path}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    }).then((r) => handle<T>(r)),

  post: <T>(path: string, body: unknown, token?: string) =>
    fetch(`${API_URL}${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(body),
    }).then((r) => handle<T>(r)),

  put: <T>(path: string, body: unknown, token?: string) =>
    fetch(`${API_URL}${path}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(body),
    }).then((r) => handle<T>(r)),

  del: <T>(path: string, token?: string) =>
    fetch(`${API_URL}${path}`, {
      method: 'DELETE',
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    }).then((r) => handle<T>(r)),
};
