import bcrypt from 'bcryptjs';
import { join, dirname } from 'path';
import { mkdirSync, existsSync, readFileSync, writeFileSync } from 'fs';
import { Pool } from 'pg';

// Dual-mode lightweight DB.
//   - When DATABASE_URL is set: Postgres (Neon/Supabase/Railway).
//   - Otherwise: JSON file fallback (no native deps) for local dev.
// Both modes expose the same async API so callers never worry about the backend.

type StoredUser = {
  id: number;
  email: string;
  password: string;
  name: string;
  created_at: number;
};

type StoredSession = {
  id: number;
  user_id: number;
  session_id: string;
  container_id: string | null;
  status: string;
  created_at: number;
  last_active: number;
};

type DbShape = {
  users: StoredUser[];
  sessions: StoredSession[];
};

let mode: 'json' | 'postgres' = 'json';
let pool: Pool | null = null;

let dbData: DbShape = { users: [], sessions: [] };
let dbPath: string;

const SCHEMA = `
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL,
  name TEXT NOT NULL,
  created_at BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS sessions (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  session_id TEXT UNIQUE NOT NULL,
  container_id TEXT,
  status TEXT NOT NULL DEFAULT 'active',
  created_at BIGINT NOT NULL,
  last_active BIGINT NOT NULL
);
`;

export type UserRow = {
  id: number;
  email: string;
  password: string;
  name: string;
  created_at: number;
};

export async function initDb(filePath?: string): Promise<DbShape> {
  if (process.env.DATABASE_URL) {
    mode = 'postgres';
    pool = new Pool({ connectionString: process.env.DATABASE_URL });
    pool.on('error', (err) => console.error('[db] postgres pool error:', err.message));
    try {
      await pool.query(SCHEMA);
    } catch (err) {
      console.error('[db] failed to run Postgres schema:', (err as Error).message);
    }
    return { users: [], sessions: [] };
  }

  mode = 'json';
  dbPath = filePath ?? process.env.DB_PATH ?? join(process.cwd(), 'data', 'virgoyt.json');
  mkdirSync(dirname(dbPath), { recursive: true });
  if (existsSync(dbPath)) {
    try {
      dbData = JSON.parse(readFileSync(dbPath, 'utf8'));
    } catch {
      dbData = { users: [], sessions: [] };
    }
  } else {
    dbData = { users: [], sessions: [] };
    persist();
  }
  return dbData;
}

export function dbMode(): 'json' | 'postgres' {
  return mode;
}

function persist() {
  if (!dbPath) return;
  writeFileSync(dbPath, JSON.stringify(dbData, null, 2), 'utf8');
}

export function getDb(): DbShape {
  if (!dbData.users) dbData = { users: [], sessions: [] };
  return dbData;
}

let idCounter = 1;

export async function createUser(email: string, password: string, name: string): Promise<UserRow> {
  const hashed = await bcrypt.hash(password, 10);

  if (mode === 'postgres' && pool) {
    const { rows } = await pool.query<UserRow>(
      'INSERT INTO users (email, password, name, created_at) VALUES ($1, $2, $3, $4) RETURNING id, email, password, name, created_at',
      [email.toLowerCase(), hashed, name, Date.now()]
    );
    return rows[0];
  }

  const user: StoredUser = {
    id: nextId(),
    email: email.toLowerCase(),
    password: hashed,
    name,
    created_at: Date.now(),
  };
  dbData.users.push(user);
  persist();
  return user;
}

export function verifyPasswordSync(user: UserRow, password: string): boolean {
  return bcrypt.compareSync(password, user.password);
}

export async function findUserByEmail(email: string): Promise<UserRow | undefined> {
  if (mode === 'postgres' && pool) {
    const { rows } = await pool.query<UserRow>(
      'SELECT id, email, password, name, created_at FROM users WHERE email = lower($1) LIMIT 1',
      [email]
    );
    return rows[0];
  }
  return dbData.users.find((u) => u.email.toLowerCase() === email.toLowerCase());
}

export async function findUserById(id: number): Promise<UserRow | undefined> {
  if (mode === 'postgres' && pool) {
    const { rows } = await pool.query<UserRow>(
      'SELECT id, email, password, name, created_at FROM users WHERE id = $1 LIMIT 1',
      [id]
    );
    return rows[0];
  }
  return dbData.users.find((u) => u.id === id);
}

export async function createSession(userId: number, sessionId: string, containerId?: string) {
  if (mode === 'postgres' && pool) {
    await pool.query(
      'INSERT INTO sessions (user_id, session_id, container_id, status, created_at, last_active) VALUES ($1, $2, $3, $4, $5, $5)',
      [userId, sessionId, containerId ?? null, 'active', Date.now()]
    );
    return;
  }

  const session: StoredSession = {
    id: nextId(),
    user_id: userId,
    session_id: sessionId,
    container_id: containerId ?? null,
    status: 'active',
    created_at: Date.now(),
    last_active: Date.now(),
  };
  dbData.sessions.push(session);
  persist();
}

export async function updateSessionActivity(sessionId: string) {
  if (mode === 'postgres' && pool) {
    await pool.query('UPDATE sessions SET last_active = $1 WHERE session_id = $2', [
      Date.now(),
      sessionId,
    ]);
    return;
  }

  const s = dbData.sessions.find((x) => x.session_id === sessionId);
  if (s) {
    s.last_active = Date.now();
    persist();
  }
}

function nextId(): number {
  return ++idCounter;
}