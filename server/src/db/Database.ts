import bcrypt from 'bcryptjs';
import { join, dirname } from 'path';
import { mkdirSync, existsSync, readFileSync, writeFileSync } from 'fs';

// Lightweight JSON-file database (no native deps).
// Replace with a real DB engine (Postgres/MySQL) in production.

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

let dbData: DbShape = { users: [], sessions: [] };
let dbPath: string;

export function initDb(filePath?: string): DbShape {
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

function persist() {
  if (!dbPath) return;
  writeFileSync(dbPath, JSON.stringify(dbData, null, 2), 'utf8');
}

export function getDb(): DbShape {
  if (!dbData.users) dbData = { users: [], sessions: [] };
  return dbData;
}

export type UserRow = {
  id: number;
  email: string;
  password: string;
  name: string;
  created_at: number;
};

let idCounter = 1;

export async function createUser(email: string, password: string, name: string): Promise<UserRow> {
  const hashed = await bcrypt.hash(password, 10);
  const user: StoredUser = {
    id: nextId(),
    email,
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

export function findUserByEmail(email: string): UserRow | undefined {
  return dbData.users.find((u) => u.email.toLowerCase() === email.toLowerCase());
}

export function findUserById(id: number): UserRow | undefined {
  return dbData.users.find((u) => u.id === id);
}

export function createSession(userId: number, sessionId: string, containerId?: string) {
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

export function updateSessionActivity(sessionId: string) {
  const s = dbData.sessions.find((x) => x.session_id === sessionId);
  if (s) {
    s.last_active = Date.now();
    persist();
  }
}

function nextId(): number {
  return ++idCounter;
}
