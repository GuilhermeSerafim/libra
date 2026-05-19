# Frontend React Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a React + Vite + TypeScript frontend for the Personal Library Manager that consumes the existing Spring Boot REST API and demonstrates authentication, book CRUD, ISBN import, and quality evidence.

**Architecture:** The frontend is a separate SPA in `frontend/`, with Vite proxying `/api` to Spring Boot during local development so session cookies and CSRF work without adding CORS to the backend. The app is split into focused feature folders for auth, books, and quality evidence, with shared UI components and a small API client.

**Tech Stack:** React, Vite, TypeScript, React Router, Lucide React, CSS custom properties, Spring Boot session cookies, Spring Security CSRF token flow.

---

## Source Spec

- `docs/superpowers/specs/2026-05-19-frontend-react-design.md`
- Existing backend endpoints from `README.md` and controllers under `src/main/java/br/senac/biblioteca/controller/`.

## File Structure

Create a new frontend under `frontend/`.

```text
frontend/
  package.json
  index.html
  vite.config.ts
  tsconfig.json
  tsconfig.node.json
  src/
    main.tsx
    app/
      App.tsx
      routes.tsx
      AppLayout.tsx
      ProtectedRoute.tsx
    components/
      Button.tsx
      ConfirmDialog.tsx
      EmptyState.tsx
      Field.tsx
      LoadingState.tsx
      Toast.tsx
    features/
      auth/
        AuthContext.tsx
        LoginPage.tsx
        RegisterPage.tsx
      books/
        BookDashboard.tsx
        BookFilters.tsx
        BookForm.tsx
        BookTable.tsx
        ImportIsbnPanel.tsx
      quality/
        QualityEvidenceView.tsx
    lib/
      api/
        client.ts
        types.ts
      errors/
        messages.ts
    styles/
      global.css
```

Modify root files:

```text
README.md
.gitignore
```

No backend Java changes are planned for the first frontend implementation because Vite proxy removes the need for local CORS configuration.

---

## Task 1: Scaffold React Vite Frontend

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/index.html`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/tsconfig.node.json`
- Create: `frontend/src/main.tsx`
- Create: `frontend/src/app/App.tsx`
- Create: `frontend/src/styles/global.css`
- Modify: `.gitignore`

- [ ] **Step 1: Create Vite React TypeScript project**

Run:

```powershell
npm create vite@latest frontend -- --template react-ts
```

Expected: a new `frontend/` folder containing a Vite React TypeScript app.

- [ ] **Step 2: Install runtime dependencies**

Run:

```powershell
cd frontend
npm install react-router-dom lucide-react
```

Expected: `package.json` includes `react-router-dom` and `lucide-react`.

- [ ] **Step 3: Configure Vite proxy**

Replace `frontend/vite.config.ts` with:

```ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
});
```

This assumes the Spring Boot backend runs on `8081`, matching the current Swagger/manual testing setup. If backend runs on `8080`, change only the proxy `target`.

- [ ] **Step 4: Replace initial app shell**

Replace `frontend/src/app/App.tsx` with:

```tsx
export function App() {
  return (
    <main className="app-shell">
      <section className="welcome-panel">
        <p className="eyebrow">Biblioteca Pessoal</p>
        <h1>Frontend conectado ao backend REST</h1>
        <p>
          Esta tela inicial sera substituida pelas rotas autenticadas, login,
          CRUD de livros e importacao por ISBN.
        </p>
      </section>
    </main>
  );
}
```

Replace `frontend/src/main.tsx` with:

```tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from './app/App';
import './styles/global.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
```

- [ ] **Step 5: Add initial global styles**

Replace `frontend/src/styles/global.css` with:

```css
:root {
  font-family: "Plus Jakarta Sans", "Segoe UI", sans-serif;
  color: #111827;
  background: #f8fafc;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  --color-background: #f8fafc;
  --color-surface: #ffffff;
  --color-sidebar: #0f172a;
  --color-primary: #2563eb;
  --color-primary-hover: #1d4ed8;
  --color-text: #111827;
  --color-muted: #64748b;
  --color-border: #dbe3ee;
  --color-danger: #dc2626;
  --color-success: #15803d;
  --radius-sm: 6px;
  --radius-md: 8px;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
  background: var(--color-background);
}

button,
input,
select,
textarea {
  font: inherit;
}

button {
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
}

a {
  color: inherit;
}

.app-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.welcome-panel {
  width: min(680px, 100%);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  border-radius: var(--radius-md);
  padding: 32px;
}

.eyebrow {
  margin: 0 0 8px;
  color: var(--color-primary);
  font-weight: 700;
  text-transform: uppercase;
  font-size: 12px;
}
```

- [ ] **Step 6: Ignore frontend generated artifacts**

Append to `.gitignore`:

```gitignore
frontend/node_modules/
frontend/dist/
```

- [ ] **Step 7: Verify scaffold build**

Run:

```powershell
cd frontend
npm run build
```

Expected: TypeScript and Vite build finish without errors and generate `frontend/dist/`.

- [ ] **Step 8: Commit scaffold**

Run:

```powershell
git add .gitignore frontend
git commit -m "feat: scaffold react frontend"
```

---

## Task 2: Add API Types And HTTP Client

**Files:**
- Create: `frontend/src/lib/api/types.ts`
- Create: `frontend/src/lib/api/client.ts`
- Create: `frontend/src/lib/errors/messages.ts`
- Modify: `frontend/src/app/App.tsx`

- [ ] **Step 1: Define backend DTO types**

Create `frontend/src/lib/api/types.ts`:

```ts
export type BookStatus = 'TO_READ' | 'READING' | 'READ';
export type MetadataSource = 'MANUAL' | 'OPEN_LIBRARY';

export type UserResponse = {
  id: string;
  name: string;
  email: string;
};

export type AuthResponse = {
  user: UserResponse;
};

export type CsrfTokenResponse = {
  headerName: string;
  parameterName: string;
  token: string;
};

export type BookResponse = {
  id: string;
  title: string;
  authors: string[];
  isbn: string | null;
  publisher: string | null;
  publishDate: string | null;
  pageCount: number | null;
  coverUrl: string | null;
  status: BookStatus | null;
  rating: number | null;
  notes: string | null;
  tags: string[];
  metadataSource: MetadataSource | null;
  createdAt: string;
  updatedAt: string;
};

export type BookWritePayload = {
  title: string;
  authors: string[];
  isbn: string;
  publisher: string;
  publishDate: string;
  pageCount: number | null;
  coverUrl: string;
  status: BookStatus;
  rating: number | null;
  notes: string;
  tags: string[];
  metadataSource: MetadataSource;
};

export type BookImportPreviewResponse = {
  title: string;
  authors: string[];
  isbn: string;
  publisher: string | null;
  publishDate: string | null;
  pageCount: number | null;
  coverUrl: string | null;
};

export type ErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
};
```

- [ ] **Step 2: Add API error message helper**

Create `frontend/src/lib/errors/messages.ts`:

```ts
import type { ErrorResponse } from '../api/types';

export class ApiError extends Error {
  status: number;
  path?: string;

  constructor(message: string, status: number, path?: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.path = path;
  }
}

export function getErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Nao foi possivel concluir a operacao.';
}

export function toApiError(payload: ErrorResponse, fallbackStatus: number): ApiError {
  return new ApiError(payload.message || 'Erro na API.', payload.status || fallbackStatus, payload.path);
}
```

- [ ] **Step 3: Add CSRF-aware API client**

Create `frontend/src/lib/api/client.ts`:

```ts
import { ApiError, toApiError } from '../errors/messages';
import type {
  AuthResponse,
  BookImportPreviewResponse,
  BookResponse,
  BookWritePayload,
  CsrfTokenResponse,
  ErrorResponse,
  UserResponse,
} from './types';

let csrfToken: CsrfTokenResponse | null = null;

async function readJson<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(path, {
    ...init,
    credentials: 'include',
    headers: {
      Accept: 'application/json',
      ...(init.body ? { 'Content-Type': 'application/json' } : {}),
      ...(init.headers ?? {}),
    },
  });

  if (!response.ok) {
    const payload = await readJson<ErrorResponse>(response).catch(() => ({
      timestamp: new Date().toISOString(),
      status: response.status,
      error: response.statusText,
      message: 'Erro ao comunicar com a API.',
      path,
    }));
    throw toApiError(payload, response.status);
  }

  return readJson<T>(response);
}

async function ensureCsrf(): Promise<CsrfTokenResponse> {
  if (csrfToken) {
    return csrfToken;
  }

  csrfToken = await request<CsrfTokenResponse>('/api/auth/csrf');
  return csrfToken;
}

async function unsafeRequest<T>(path: string, init: RequestInit): Promise<T> {
  const csrf = await ensureCsrf();
  return request<T>(path, {
    ...init,
    headers: {
      [csrf.headerName]: csrf.token,
      ...(init.headers ?? {}),
    },
  });
}

export const api = {
  async me(): Promise<UserResponse> {
    return request<UserResponse>('/api/auth/me');
  },

  async register(payload: { name: string; email: string; password: string }): Promise<AuthResponse> {
    return unsafeRequest<AuthResponse>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async login(payload: { email: string; password: string }): Promise<AuthResponse> {
    return unsafeRequest<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async logout(): Promise<void> {
    await unsafeRequest<void>('/api/auth/logout', { method: 'POST' });
    csrfToken = null;
  },

  async listBooks(): Promise<BookResponse[]> {
    return request<BookResponse[]>('/api/books');
  },

  async createBook(payload: BookWritePayload): Promise<BookResponse> {
    return unsafeRequest<BookResponse>('/api/books', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async updateBook(id: string, payload: BookWritePayload): Promise<BookResponse> {
    return unsafeRequest<BookResponse>(`/api/books/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },

  async deleteBook(id: string): Promise<void> {
    await unsafeRequest<void>(`/api/books/${id}`, { method: 'DELETE' });
  },

  async previewByIsbn(isbn: string): Promise<BookImportPreviewResponse> {
    if (!isbn.trim()) {
      throw new ApiError('Informe um ISBN para buscar metadados.', 400);
    }
    return request<BookImportPreviewResponse>(`/api/books/import/isbn/${encodeURIComponent(isbn.trim())}`);
  },
};
```

- [ ] **Step 4: Verify type compilation**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes without TypeScript errors.

- [ ] **Step 5: Commit API client**

Run:

```powershell
git add frontend/src/lib
git commit -m "feat: add frontend api client"
```

---

## Task 3: Implement Authentication Routes

**Files:**
- Create: `frontend/src/app/routes.tsx`
- Create: `frontend/src/app/ProtectedRoute.tsx`
- Create: `frontend/src/features/auth/AuthContext.tsx`
- Create: `frontend/src/features/auth/LoginPage.tsx`
- Create: `frontend/src/features/auth/RegisterPage.tsx`
- Modify: `frontend/src/app/App.tsx`
- Modify: `frontend/src/main.tsx`
- Modify: `frontend/src/styles/global.css`

- [ ] **Step 1: Create auth context**

Create `frontend/src/features/auth/AuthContext.tsx`:

```tsx
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../../lib/api/client';
import type { UserResponse } from '../../lib/api/types';
import { getErrorMessage } from '../../lib/errors/messages';

type AuthContextValue = {
  user: UserResponse | null;
  loading: boolean;
  error: string;
  refreshUser: () => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  async function refreshUser() {
    setLoading(true);
    setError('');
    try {
      const current = await api.me();
      setUser(current);
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  }

  async function login(email: string, password: string) {
    setError('');
    try {
      const response = await api.login({ email, password });
      setUser(response.user);
    } catch (err) {
      const message = getErrorMessage(err);
      setError(message);
      throw err;
    }
  }

  async function register(name: string, email: string, password: string) {
    setError('');
    try {
      const response = await api.register({ name, email, password });
      setUser(response.user);
    } catch (err) {
      const message = getErrorMessage(err);
      setError(message);
      throw err;
    }
  }

  async function logout() {
    await api.logout();
    setUser(null);
  }

  useEffect(() => {
    void refreshUser();
  }, []);

  const value = useMemo(
    () => ({ user, loading, error, refreshUser, login, register, logout }),
    [user, loading, error],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider.');
  }
  return context;
}
```

- [ ] **Step 2: Create protected route**

Create `frontend/src/app/ProtectedRoute.tsx`:

```tsx
import { Navigate, Outlet } from 'react-router-dom';
import { LoadingState } from '../components/LoadingState';
import { useAuth } from '../features/auth/AuthContext';

export function ProtectedRoute() {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingState label="Verificando sessao..." />;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
```

- [ ] **Step 3: Create loading component used by auth**

Create `frontend/src/components/LoadingState.tsx`:

```tsx
export function LoadingState({ label = 'Carregando...' }: { label?: string }) {
  return (
    <div className="state state-loading" role="status" aria-live="polite">
      <span className="spinner" aria-hidden="true" />
      <span>{label}</span>
    </div>
  );
}
```

- [ ] **Step 4: Create login page**

Create `frontend/src/features/auth/LoginPage.tsx`:

```tsx
import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BookOpen } from 'lucide-react';
import { useAuth } from './AuthContext';

export function LoginPage() {
  const navigate = useNavigate();
  const { login, error } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await login(email, password);
      navigate('/app');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel" aria-labelledby="login-title">
        <div className="brand-mark">
          <BookOpen size={26} aria-hidden="true" />
        </div>
        <p className="eyebrow">Biblioteca Pessoal</p>
        <h1 id="login-title">Entrar na sua biblioteca</h1>
        <form onSubmit={handleSubmit} className="auth-form">
          <label>
            Email
            <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" autoComplete="email" required />
          </label>
          <label>
            Senha
            <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" autoComplete="current-password" required />
          </label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="primary-button" type="submit" disabled={submitting}>
            {submitting ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
        <p className="auth-switch">
          Ainda nao tem conta? <Link to="/register">Criar cadastro</Link>
        </p>
      </section>
    </main>
  );
}
```

- [ ] **Step 5: Create register page**

Create `frontend/src/features/auth/RegisterPage.tsx`:

```tsx
import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BookOpen } from 'lucide-react';
import { useAuth } from './AuthContext';

export function RegisterPage() {
  const navigate = useNavigate();
  const { register, error } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await register(name, email, password);
      navigate('/app');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel" aria-labelledby="register-title">
        <div className="brand-mark">
          <BookOpen size={26} aria-hidden="true" />
        </div>
        <p className="eyebrow">Biblioteca Pessoal</p>
        <h1 id="register-title">Criar conta</h1>
        <form onSubmit={handleSubmit} className="auth-form">
          <label>
            Nome
            <input value={name} onChange={(event) => setName(event.target.value)} autoComplete="name" required />
          </label>
          <label>
            Email
            <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" autoComplete="email" required />
          </label>
          <label>
            Senha
            <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" autoComplete="new-password" minLength={8} required />
          </label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="primary-button" type="submit" disabled={submitting}>
            {submitting ? 'Criando...' : 'Criar conta'}
          </button>
        </form>
        <p className="auth-switch">
          Ja tem conta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </main>
  );
}
```

- [ ] **Step 6: Wire router**

Create `frontend/src/app/routes.tsx`:

```tsx
import { Navigate, createBrowserRouter } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import { LoginPage } from '../features/auth/LoginPage';
import { RegisterPage } from '../features/auth/RegisterPage';

function AppPlaceholder() {
  return <main className="app-shell"><section className="welcome-panel"><h1>Biblioteca</h1></section></main>;
}

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/app" replace /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  {
    path: '/app',
    element: <ProtectedRoute />,
    children: [{ index: true, element: <AppPlaceholder /> }],
  },
]);
```

Replace `frontend/src/app/App.tsx`:

```tsx
import { RouterProvider } from 'react-router-dom';
import { AuthProvider } from '../features/auth/AuthContext';
import { router } from './routes';

export function App() {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  );
}
```

- [ ] **Step 7: Add auth styles**

Append to `frontend/src/styles/global.css`:

```css
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.auth-panel {
  width: min(420px, 100%);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 32px;
}

.brand-mark {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  color: var(--color-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  margin-bottom: 16px;
}

.auth-panel h1 {
  margin: 0 0 20px;
  font-size: 28px;
}

.auth-form {
  display: grid;
  gap: 14px;
}

.auth-form label {
  display: grid;
  gap: 6px;
  font-weight: 700;
  font-size: 14px;
}

.auth-form input {
  min-height: 44px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 0 12px;
}

.auth-form input:focus,
.primary-button:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.25);
  border-color: var(--color-primary);
}

.primary-button {
  min-height: 44px;
  border: 0;
  border-radius: var(--radius-sm);
  background: var(--color-primary);
  color: white;
  font-weight: 800;
  transition: background 160ms ease;
}

.primary-button:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.form-error {
  margin: 0;
  color: var(--color-danger);
  font-weight: 700;
}

.auth-switch {
  margin: 18px 0 0;
  color: var(--color-muted);
}

.auth-switch a {
  color: var(--color-primary);
  font-weight: 800;
}

.state {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--color-muted);
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 900ms linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (prefers-reduced-motion: reduce) {
  .spinner {
    animation: none;
  }
}
```

- [ ] **Step 8: Verify authentication build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 9: Commit authentication shell**

Run:

```powershell
git add frontend/src
git commit -m "feat: add frontend authentication routes"
```

---

## Task 4: Build App Layout And Shared UI

**Files:**
- Create: `frontend/src/app/AppLayout.tsx`
- Create: `frontend/src/components/Button.tsx`
- Create: `frontend/src/components/EmptyState.tsx`
- Create: `frontend/src/components/ConfirmDialog.tsx`
- Create: `frontend/src/components/Toast.tsx`
- Modify: `frontend/src/app/routes.tsx`
- Modify: `frontend/src/styles/global.css`

- [ ] **Step 1: Add shared button**

Create `frontend/src/components/Button.tsx`:

```tsx
import type { ButtonHTMLAttributes, ReactNode } from 'react';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  icon?: ReactNode;
};

export function Button({ variant = 'primary', icon, children, className = '', ...props }: ButtonProps) {
  return (
    <button className={`button button-${variant} ${className}`.trim()} {...props}>
      {icon}
      <span>{children}</span>
    </button>
  );
}
```

- [ ] **Step 2: Add empty state**

Create `frontend/src/components/EmptyState.tsx`:

```tsx
import type { ReactNode } from 'react';

export function EmptyState({ title, description, action }: { title: string; description: string; action?: ReactNode }) {
  return (
    <section className="empty-state">
      <h2>{title}</h2>
      <p>{description}</p>
      {action}
    </section>
  );
}
```

- [ ] **Step 3: Add confirm dialog**

Create `frontend/src/components/ConfirmDialog.tsx`:

```tsx
import { Button } from './Button';

export function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel,
  onCancel,
  onConfirm,
}: {
  open: boolean;
  title: string;
  description: string;
  confirmLabel: string;
  onCancel: () => void;
  onConfirm: () => void;
}) {
  if (!open) {
    return null;
  }

  return (
    <div className="dialog-backdrop" role="presentation">
      <section className="dialog" role="dialog" aria-modal="true" aria-labelledby="confirm-title">
        <h2 id="confirm-title">{title}</h2>
        <p>{description}</p>
        <div className="dialog-actions">
          <Button type="button" variant="ghost" onClick={onCancel}>Cancelar</Button>
          <Button type="button" variant="danger" onClick={onConfirm}>{confirmLabel}</Button>
        </div>
      </section>
    </div>
  );
}
```

- [ ] **Step 4: Add toast**

Create `frontend/src/components/Toast.tsx`:

```tsx
export function Toast({ message, tone }: { message: string; tone: 'success' | 'error' }) {
  if (!message) {
    return null;
  }

  return (
    <div className={`toast toast-${tone}`} role="status" aria-live="polite">
      {message}
    </div>
  );
}
```

- [ ] **Step 5: Add app layout**

Create `frontend/src/app/AppLayout.tsx`:

```tsx
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { BarChart3, BookOpen, Library, LogOut } from 'lucide-react';
import { useAuth } from '../features/auth/AuthContext';
import { Button } from '../components/Button';

export function AppLayout() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  async function handleLogout() {
    await logout();
    navigate('/login');
  }

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <BookOpen size={24} aria-hidden="true" />
          <span>Biblioteca</span>
        </div>
        <nav className="sidebar-nav" aria-label="Navegacao principal">
          <NavLink to="/app" end><Library size={18} aria-hidden="true" /> Biblioteca</NavLink>
          <NavLink to="/app/quality"><BarChart3 size={18} aria-hidden="true" /> Evidencias</NavLink>
        </nav>
      </aside>
      <div className="main-frame">
        <header className="topbar">
          <div>
            <p className="topbar-label">Usuario autenticado</p>
            <strong>{user?.name}</strong>
          </div>
          <Button type="button" variant="secondary" icon={<LogOut size={16} aria-hidden="true" />} onClick={handleLogout}>
            Sair
          </Button>
        </header>
        <Outlet />
      </div>
    </div>
  );
}
```

- [ ] **Step 6: Wire layout route**

Replace `frontend/src/app/routes.tsx` with:

```tsx
import { Navigate, createBrowserRouter } from 'react-router-dom';
import { AppLayout } from './AppLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { LoginPage } from '../features/auth/LoginPage';
import { RegisterPage } from '../features/auth/RegisterPage';

function BookDashboardPlaceholder() {
  return <main className="content-page"><h1>Biblioteca</h1></main>;
}

function QualityPlaceholder() {
  return <main className="content-page"><h1>Evidencias</h1></main>;
}

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/app" replace /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  {
    path: '/app',
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { index: true, element: <BookDashboardPlaceholder /> },
          { path: 'quality', element: <QualityPlaceholder /> },
        ],
      },
    ],
  },
]);
```

- [ ] **Step 7: Add layout CSS**

Append to `frontend/src/styles/global.css`:

```css
.app-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 240px 1fr;
}

.sidebar {
  background: var(--color-sidebar);
  color: white;
  padding: 20px;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 900;
  margin-bottom: 28px;
}

.sidebar-nav {
  display: grid;
  gap: 8px;
}

.sidebar-nav a {
  min-height: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  border-radius: var(--radius-sm);
  color: #cbd5e1;
  text-decoration: none;
  font-weight: 700;
}

.sidebar-nav a.active,
.sidebar-nav a:hover {
  background: #1e293b;
  color: white;
}

.main-frame {
  min-width: 0;
}

.topbar {
  min-height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-surface);
}

.topbar-label {
  margin: 0 0 3px;
  color: var(--color-muted);
  font-size: 12px;
  font-weight: 700;
}

.content-page {
  padding: 24px;
}

.button {
  min-height: 40px;
  border: 1px solid transparent;
  border-radius: var(--radius-sm);
  padding: 0 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 800;
  transition: background 160ms ease, border-color 160ms ease, color 160ms ease;
}

.button-primary { background: var(--color-primary); color: white; }
.button-primary:hover:not(:disabled) { background: var(--color-primary-hover); }
.button-secondary { background: white; color: var(--color-text); border-color: var(--color-border); }
.button-danger { background: var(--color-danger); color: white; }
.button-ghost { background: transparent; color: var(--color-text); border-color: var(--color-border); }

.empty-state {
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  background: white;
  padding: 28px;
  text-align: center;
}

.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  display: grid;
  place-items: center;
  padding: 20px;
  z-index: 40;
}

.dialog {
  width: min(420px, 100%);
  background: white;
  border-radius: var(--radius-md);
  padding: 24px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.toast {
  position: fixed;
  right: 20px;
  bottom: 20px;
  max-width: 360px;
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  color: white;
  font-weight: 800;
  z-index: 50;
}

.toast-success { background: var(--color-success); }
.toast-error { background: var(--color-danger); }

@media (max-width: 820px) {
  .app-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: sticky;
    top: 0;
    z-index: 20;
    display: flex;
    justify-content: space-between;
    gap: 16px;
    padding: 14px;
  }

  .sidebar-nav {
    display: flex;
  }

  .sidebar-nav a {
    padding: 0 10px;
  }
}
```

- [ ] **Step 8: Verify layout build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 9: Commit layout**

Run:

```powershell
git add frontend/src
git commit -m "feat: add frontend app layout"
```

---

## Task 5: Implement Books Dashboard And Listing

**Files:**
- Create: `frontend/src/features/books/BookDashboard.tsx`
- Create: `frontend/src/features/books/BookFilters.tsx`
- Create: `frontend/src/features/books/BookTable.tsx`
- Modify: `frontend/src/app/routes.tsx`
- Modify: `frontend/src/styles/global.css`

- [ ] **Step 1: Create filters component**

Create `frontend/src/features/books/BookFilters.tsx`:

```tsx
import type { BookStatus } from '../../lib/api/types';

export type BookFilterState = {
  query: string;
  status: 'ALL' | BookStatus;
};

export function BookFilters({
  filters,
  onChange,
}: {
  filters: BookFilterState;
  onChange: (filters: BookFilterState) => void;
}) {
  return (
    <section className="toolbar" aria-label="Filtros de livros">
      <label>
        Buscar
        <input
          value={filters.query}
          onChange={(event) => onChange({ ...filters, query: event.target.value })}
          placeholder="Titulo, autor, ISBN ou tag"
        />
      </label>
      <label>
        Status
        <select
          value={filters.status}
          onChange={(event) => onChange({ ...filters, status: event.target.value as BookFilterState['status'] })}
        >
          <option value="ALL">Todos</option>
          <option value="TO_READ">Quero ler</option>
          <option value="READING">Lendo</option>
          <option value="READ">Lido</option>
        </select>
      </label>
    </section>
  );
}
```

- [ ] **Step 2: Create table component**

Create `frontend/src/features/books/BookTable.tsx`:

```tsx
import { Edit3, Trash2 } from 'lucide-react';
import { Button } from '../../components/Button';
import type { BookResponse } from '../../lib/api/types';

const statusLabel = {
  TO_READ: 'Quero ler',
  READING: 'Lendo',
  READ: 'Lido',
} as const;

export function BookTable({
  books,
  onEdit,
  onDelete,
}: {
  books: BookResponse[];
  onEdit: (book: BookResponse) => void;
  onDelete: (book: BookResponse) => void;
}) {
  return (
    <div className="table-wrap">
      <table className="book-table">
        <thead>
          <tr>
            <th>Livro</th>
            <th>Autores</th>
            <th>Status</th>
            <th>Avaliacao</th>
            <th>Origem</th>
            <th>Acoes</th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.id}>
              <td>
                <strong>{book.title}</strong>
                <span>{book.isbn || 'ISBN nao informado'}</span>
              </td>
              <td>{book.authors.length ? book.authors.join(', ') : 'Sem autor'}</td>
              <td>{book.status ? statusLabel[book.status] : 'Sem status'}</td>
              <td>{book.rating ? `${book.rating}/5` : 'Sem nota'}</td>
              <td>{book.metadataSource === 'OPEN_LIBRARY' ? 'Open Library' : 'Manual'}</td>
              <td>
                <div className="row-actions">
                  <Button type="button" variant="ghost" icon={<Edit3 size={15} aria-hidden="true" />} onClick={() => onEdit(book)}>
                    Editar
                  </Button>
                  <Button type="button" variant="ghost" icon={<Trash2 size={15} aria-hidden="true" />} onClick={() => onDelete(book)}>
                    Excluir
                  </Button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

- [ ] **Step 3: Create dashboard**

Create `frontend/src/features/books/BookDashboard.tsx`:

```tsx
import { useEffect, useMemo, useState } from 'react';
import { Plus } from 'lucide-react';
import { Button } from '../../components/Button';
import { EmptyState } from '../../components/EmptyState';
import { LoadingState } from '../../components/LoadingState';
import { api } from '../../lib/api/client';
import type { BookResponse } from '../../lib/api/types';
import { getErrorMessage } from '../../lib/errors/messages';
import { BookFilters, type BookFilterState } from './BookFilters';
import { BookTable } from './BookTable';

export function BookDashboard() {
  const [books, setBooks] = useState<BookResponse[]>([]);
  const [filters, setFilters] = useState<BookFilterState>({ query: '', status: 'ALL' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  async function loadBooks() {
    setLoading(true);
    setError('');
    try {
      setBooks(await api.listBooks());
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadBooks();
  }, []);

  const filteredBooks = useMemo(() => {
    const query = filters.query.trim().toLowerCase();
    return books.filter((book) => {
      const matchesStatus = filters.status === 'ALL' || book.status === filters.status;
      const searchable = [book.title, book.isbn, book.publisher, book.notes, ...book.authors, ...book.tags]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();
      return matchesStatus && (!query || searchable.includes(query));
    });
  }, [books, filters]);

  if (loading) {
    return <LoadingState label="Carregando livros..." />;
  }

  return (
    <main className="content-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Biblioteca</p>
          <h1>Seus livros</h1>
        </div>
        <Button type="button" icon={<Plus size={16} aria-hidden="true" />}>
          Novo livro
        </Button>
      </header>

      <section className="metric-grid">
        <article><span>{books.length}</span><p>Total</p></article>
        <article><span>{books.filter((book) => book.status === 'READING').length}</span><p>Lendo</p></article>
        <article><span>{books.filter((book) => book.metadataSource === 'OPEN_LIBRARY').length}</span><p>Importados</p></article>
      </section>

      <BookFilters filters={filters} onChange={setFilters} />

      {error && <p className="form-error" role="alert">{error}</p>}
      {!error && filteredBooks.length === 0 && (
        <EmptyState title="Nenhum livro encontrado" description="Cadastre manualmente ou importe metadados por ISBN." />
      )}
      {!error && filteredBooks.length > 0 && (
        <BookTable books={filteredBooks} onEdit={() => undefined} onDelete={() => undefined} />
      )}
    </main>
  );
}
```

- [ ] **Step 4: Route dashboard**

In `frontend/src/app/routes.tsx`, import and use `BookDashboard`:

```tsx
import { BookDashboard } from '../features/books/BookDashboard';
```

Replace the `/app` index route element:

```tsx
{ index: true, element: <BookDashboard /> },
```

- [ ] **Step 5: Add dashboard CSS**

Append to `frontend/src/styles/global.css`:

```css
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 30px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.metric-grid article {
  background: white;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 16px;
}

.metric-grid span {
  display: block;
  font-size: 28px;
  font-weight: 900;
}

.metric-grid p {
  margin: 4px 0 0;
  color: var(--color-muted);
  font-weight: 700;
}

.toolbar {
  display: grid;
  grid-template-columns: 1fr 180px;
  gap: 12px;
  margin-bottom: 16px;
}

.toolbar label {
  display: grid;
  gap: 6px;
  font-weight: 800;
}

.toolbar input,
.toolbar select {
  min-height: 42px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 0 12px;
  background: white;
}

.table-wrap {
  overflow-x: auto;
  background: white;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.book-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 840px;
}

.book-table th,
.book-table td {
  padding: 14px;
  border-bottom: 1px solid var(--color-border);
  text-align: left;
  vertical-align: top;
}

.book-table th {
  color: var(--color-muted);
  font-size: 12px;
  text-transform: uppercase;
}

.book-table td span {
  display: block;
  margin-top: 4px;
  color: var(--color-muted);
  font-size: 13px;
}

.row-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 720px) {
  .metric-grid,
  .toolbar {
    grid-template-columns: 1fr;
  }

  .page-header {
    align-items: stretch;
    flex-direction: column;
  }
}
```

- [ ] **Step 6: Verify listing build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 7: Commit listing**

Run:

```powershell
git add frontend/src
git commit -m "feat: add books dashboard"
```

---

## Task 6: Implement Book Form, Create, Edit, Delete

**Files:**
- Create: `frontend/src/features/books/BookForm.tsx`
- Modify: `frontend/src/features/books/BookDashboard.tsx`
- Modify: `frontend/src/styles/global.css`

- [ ] **Step 1: Create book form component**

Create `frontend/src/features/books/BookForm.tsx`:

```tsx
import { FormEvent, useEffect, useState } from 'react';
import { Button } from '../../components/Button';
import type { BookResponse, BookStatus, BookWritePayload, MetadataSource } from '../../lib/api/types';

const defaultPayload: BookWritePayload = {
  title: '',
  authors: [],
  isbn: '',
  publisher: '',
  publishDate: '',
  pageCount: null,
  coverUrl: '',
  status: 'TO_READ',
  rating: null,
  notes: '',
  tags: [],
  metadataSource: 'MANUAL',
};

function splitList(value: string): string[] {
  return value.split(',').map((item) => item.trim()).filter(Boolean);
}

export function toPayload(book?: BookResponse | null): BookWritePayload {
  if (!book) {
    return defaultPayload;
  }

  return {
    title: book.title,
    authors: book.authors,
    isbn: book.isbn ?? '',
    publisher: book.publisher ?? '',
    publishDate: book.publishDate ?? '',
    pageCount: book.pageCount,
    coverUrl: book.coverUrl ?? '',
    status: book.status ?? 'TO_READ',
    rating: book.rating,
    notes: book.notes ?? '',
    tags: book.tags,
    metadataSource: book.metadataSource ?? 'MANUAL',
  };
}

export function BookForm({
  book,
  onCancel,
  onSubmit,
}: {
  book: BookResponse | null;
  onCancel: () => void;
  onSubmit: (payload: BookWritePayload) => Promise<void>;
}) {
  const [payload, setPayload] = useState<BookWritePayload>(toPayload(book));
  const [authorsText, setAuthorsText] = useState('');
  const [tagsText, setTagsText] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const next = toPayload(book);
    setPayload(next);
    setAuthorsText(next.authors.join(', '));
    setTagsText(next.tags.join(', '));
  }, [book]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await onSubmit({
        ...payload,
        authors: splitList(authorsText),
        tags: splitList(tagsText),
      });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <aside className="side-panel" aria-labelledby="book-form-title">
      <form onSubmit={handleSubmit} className="book-form">
        <header>
          <h2 id="book-form-title">{book ? 'Editar livro' : 'Novo livro'}</h2>
          <p>{book ? 'Atualize os dados do livro selecionado.' : 'Cadastre um livro manualmente.'}</p>
        </header>
        <label>
          Titulo
          <input value={payload.title} onChange={(event) => setPayload({ ...payload, title: event.target.value })} required />
        </label>
        <label>
          Autores
          <input value={authorsText} onChange={(event) => setAuthorsText(event.target.value)} placeholder="Autor 1, Autor 2" />
        </label>
        <label>
          ISBN
          <input value={payload.isbn} onChange={(event) => setPayload({ ...payload, isbn: event.target.value })} />
        </label>
        <label>
          Editora
          <input value={payload.publisher} onChange={(event) => setPayload({ ...payload, publisher: event.target.value })} />
        </label>
        <div className="form-grid">
          <label>
            Publicacao
            <input value={payload.publishDate} onChange={(event) => setPayload({ ...payload, publishDate: event.target.value })} />
          </label>
          <label>
            Paginas
            <input
              type="number"
              min={1}
              value={payload.pageCount ?? ''}
              onChange={(event) => setPayload({ ...payload, pageCount: event.target.value ? Number(event.target.value) : null })}
            />
          </label>
        </div>
        <div className="form-grid">
          <label>
            Status
            <select value={payload.status} onChange={(event) => setPayload({ ...payload, status: event.target.value as BookStatus })}>
              <option value="TO_READ">Quero ler</option>
              <option value="READING">Lendo</option>
              <option value="READ">Lido</option>
            </select>
          </label>
          <label>
            Nota
            <input
              type="number"
              min={1}
              max={5}
              value={payload.rating ?? ''}
              onChange={(event) => setPayload({ ...payload, rating: event.target.value ? Number(event.target.value) : null })}
            />
          </label>
        </div>
        <label>
          URL da capa
          <input value={payload.coverUrl} onChange={(event) => setPayload({ ...payload, coverUrl: event.target.value })} />
        </label>
        <label>
          Tags
          <input value={tagsText} onChange={(event) => setTagsText(event.target.value)} placeholder="qualidade, estudo" />
        </label>
        <label>
          Notas
          <textarea value={payload.notes} onChange={(event) => setPayload({ ...payload, notes: event.target.value })} rows={4} />
        </label>
        <input type="hidden" value={payload.metadataSource as MetadataSource} readOnly />
        <div className="panel-actions">
          <Button type="button" variant="ghost" onClick={onCancel}>Cancelar</Button>
          <Button type="submit" disabled={submitting}>{submitting ? 'Salvando...' : 'Salvar'}</Button>
        </div>
      </form>
    </aside>
  );
}
```

- [ ] **Step 2: Connect create, edit, delete in dashboard**

Update `frontend/src/features/books/BookDashboard.tsx` imports:

```tsx
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { Toast } from '../../components/Toast';
import type { BookResponse, BookWritePayload } from '../../lib/api/types';
import { BookForm } from './BookForm';
```

Add state inside `BookDashboard`:

```tsx
const [editingBook, setEditingBook] = useState<BookResponse | null>(null);
const [formOpen, setFormOpen] = useState(false);
const [deleteTarget, setDeleteTarget] = useState<BookResponse | null>(null);
const [toast, setToast] = useState<{ message: string; tone: 'success' | 'error' }>({ message: '', tone: 'success' });
```

Add handlers:

```tsx
async function saveBook(payload: BookWritePayload) {
  try {
    if (editingBook) {
      await api.updateBook(editingBook.id, payload);
      setToast({ message: 'Livro atualizado.', tone: 'success' });
    } else {
      await api.createBook(payload);
      setToast({ message: 'Livro cadastrado.', tone: 'success' });
    }
    setFormOpen(false);
    setEditingBook(null);
    await loadBooks();
  } catch (err) {
    setToast({ message: getErrorMessage(err), tone: 'error' });
  }
}

async function confirmDelete() {
  if (!deleteTarget) {
    return;
  }

  try {
    await api.deleteBook(deleteTarget.id);
    setToast({ message: 'Livro excluido.', tone: 'success' });
    setDeleteTarget(null);
    await loadBooks();
  } catch (err) {
    setToast({ message: getErrorMessage(err), tone: 'error' });
  }
}
```

Replace the "Novo livro" button:

```tsx
<Button type="button" icon={<Plus size={16} aria-hidden="true" />} onClick={() => { setEditingBook(null); setFormOpen(true); }}>
  Novo livro
</Button>
```

Replace the `BookTable` callbacks:

```tsx
<BookTable
  books={filteredBooks}
  onEdit={(book) => { setEditingBook(book); setFormOpen(true); }}
  onDelete={setDeleteTarget}
/>
```

Render panels at the end of the `<main>`:

```tsx
{formOpen && (
  <BookForm
    book={editingBook}
    onCancel={() => { setFormOpen(false); setEditingBook(null); }}
    onSubmit={saveBook}
  />
)}
<ConfirmDialog
  open={Boolean(deleteTarget)}
  title="Excluir livro"
  description={`Deseja excluir "${deleteTarget?.title}" da sua biblioteca?`}
  confirmLabel="Excluir"
  onCancel={() => setDeleteTarget(null)}
  onConfirm={confirmDelete}
/>
<Toast message={toast.message} tone={toast.tone} />
```

- [ ] **Step 3: Add form CSS**

Append to `frontend/src/styles/global.css`:

```css
.side-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: min(460px, 100%);
  height: 100vh;
  overflow-y: auto;
  background: white;
  border-left: 1px solid var(--color-border);
  padding: 24px;
  z-index: 30;
}

.book-form {
  display: grid;
  gap: 14px;
}

.book-form header h2 {
  margin: 0 0 6px;
}

.book-form header p {
  margin: 0;
  color: var(--color-muted);
}

.book-form label {
  display: grid;
  gap: 6px;
  font-size: 14px;
  font-weight: 800;
}

.book-form input,
.book-form select,
.book-form textarea {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 10px 12px;
  min-height: 42px;
  background: white;
}

.book-form textarea {
  resize: vertical;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.panel-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 8px;
}
```

- [ ] **Step 4: Verify CRUD build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 5: Commit CRUD UI**

Run:

```powershell
git add frontend/src
git commit -m "feat: add book form crud actions"
```

---

## Task 7: Implement ISBN Import Preview

**Files:**
- Create: `frontend/src/features/books/ImportIsbnPanel.tsx`
- Modify: `frontend/src/features/books/BookDashboard.tsx`
- Modify: `frontend/src/styles/global.css`

- [ ] **Step 1: Create ISBN import panel**

Create `frontend/src/features/books/ImportIsbnPanel.tsx`:

```tsx
import { FormEvent, useState } from 'react';
import { Search } from 'lucide-react';
import { Button } from '../../components/Button';
import { api } from '../../lib/api/client';
import type { BookImportPreviewResponse, BookWritePayload } from '../../lib/api/types';
import { getErrorMessage } from '../../lib/errors/messages';

function previewToPayload(preview: BookImportPreviewResponse): BookWritePayload {
  return {
    title: preview.title,
    authors: preview.authors,
    isbn: preview.isbn,
    publisher: preview.publisher ?? '',
    publishDate: preview.publishDate ?? '',
    pageCount: preview.pageCount,
    coverUrl: preview.coverUrl ?? '',
    status: 'TO_READ',
    rating: null,
    notes: '',
    tags: [],
    metadataSource: 'OPEN_LIBRARY',
  };
}

export function ImportIsbnPanel({
  onSave,
}: {
  onSave: (payload: BookWritePayload) => Promise<void>;
}) {
  const [isbn, setIsbn] = useState('');
  const [preview, setPreview] = useState<BookImportPreviewResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  async function handlePreview(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError('');
    setPreview(null);
    try {
      setPreview(await api.previewByIsbn(isbn));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleSave() {
    if (!preview) {
      return;
    }

    setSaving(true);
    try {
      await onSave(previewToPayload(preview));
      setPreview(null);
      setIsbn('');
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="import-panel" aria-labelledby="import-title">
      <div>
        <p className="eyebrow">Open Library</p>
        <h2 id="import-title">Importar por ISBN</h2>
        <p>Busque metadados externos, revise o preview e confirme antes de salvar.</p>
      </div>
      <form onSubmit={handlePreview} className="isbn-form">
        <label>
          ISBN
          <input value={isbn} onChange={(event) => setIsbn(event.target.value)} placeholder="9780132350884" />
        </label>
        <Button type="submit" icon={<Search size={16} aria-hidden="true" />} disabled={loading}>
          {loading ? 'Buscando...' : 'Buscar'}
        </Button>
      </form>
      {error && <p className="form-error" role="alert">{error}</p>}
      {preview && (
        <article className="preview-card">
          {preview.coverUrl && <img src={preview.coverUrl} alt={`Capa de ${preview.title}`} />}
          <div>
            <h3>{preview.title}</h3>
            <p>{preview.authors.join(', ') || 'Autor nao informado'}</p>
            <dl>
              <div><dt>ISBN</dt><dd>{preview.isbn}</dd></div>
              <div><dt>Editora</dt><dd>{preview.publisher || 'Nao informada'}</dd></div>
              <div><dt>Publicacao</dt><dd>{preview.publishDate || 'Nao informada'}</dd></div>
              <div><dt>Paginas</dt><dd>{preview.pageCount ?? 'Nao informado'}</dd></div>
            </dl>
            <Button type="button" onClick={handleSave} disabled={saving}>
              {saving ? 'Salvando...' : 'Salvar na biblioteca'}
            </Button>
          </div>
        </article>
      )}
    </section>
  );
}
```

- [ ] **Step 2: Render import panel in dashboard**

In `frontend/src/features/books/BookDashboard.tsx`, import:

```tsx
import { ImportIsbnPanel } from './ImportIsbnPanel';
```

Add handler:

```tsx
async function saveImportedBook(payload: BookWritePayload) {
  try {
    await api.createBook(payload);
    setToast({ message: 'Livro importado e salvo.', tone: 'success' });
    await loadBooks();
  } catch (err) {
    setToast({ message: getErrorMessage(err), tone: 'error' });
  }
}
```

Render after metrics and before filters:

```tsx
<ImportIsbnPanel onSave={saveImportedBook} />
```

- [ ] **Step 3: Add import CSS**

Append to `frontend/src/styles/global.css`:

```css
.import-panel {
  display: grid;
  gap: 14px;
  background: white;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 18px;
  margin-bottom: 16px;
}

.import-panel h2 {
  margin: 0 0 6px;
}

.import-panel p {
  margin: 0;
  color: var(--color-muted);
}

.isbn-form {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: end;
}

.isbn-form label {
  display: grid;
  gap: 6px;
  font-weight: 800;
}

.isbn-form input {
  min-height: 42px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 0 12px;
}

.preview-card {
  display: grid;
  grid-template-columns: 104px 1fr;
  gap: 16px;
  border-top: 1px solid var(--color-border);
  padding-top: 16px;
}

.preview-card img {
  width: 104px;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
}

.preview-card h3 {
  margin: 0 0 6px;
}

.preview-card dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin: 14px 0;
}

.preview-card dt {
  color: var(--color-muted);
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.preview-card dd {
  margin: 2px 0 0;
  font-weight: 700;
}

@media (max-width: 680px) {
  .isbn-form,
  .preview-card {
    grid-template-columns: 1fr;
  }
}
```

- [ ] **Step 4: Verify ISBN import build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 5: Commit ISBN import**

Run:

```powershell
git add frontend/src
git commit -m "feat: add isbn import preview"
```

---

## Task 8: Add Quality Evidence View

**Files:**
- Create: `frontend/src/features/quality/QualityEvidenceView.tsx`
- Modify: `frontend/src/app/routes.tsx`
- Modify: `frontend/src/styles/global.css`

- [ ] **Step 1: Create quality view**

Create `frontend/src/features/quality/QualityEvidenceView.tsx`:

```tsx
import { CheckCircle2, ShieldCheck, TestTube2 } from 'lucide-react';

const items = [
  { label: 'Quality Gate', value: 'Passed', icon: CheckCircle2 },
  { label: 'Cobertura geral', value: '90.1%', icon: TestTube2 },
  { label: 'Bugs / Vulnerabilidades / Code Smells', value: '0 / 0 / 0', icon: ShieldCheck },
  { label: 'Duplicacao em codigo novo', value: '0.0%', icon: CheckCircle2 },
];

export function QualityEvidenceView() {
  return (
    <main className="content-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Qualidade Automatizada</p>
          <h1>Evidencias do projeto</h1>
        </div>
      </header>
      <section className="quality-grid">
        {items.map((item) => {
          const Icon = item.icon;
          return (
            <article key={item.label}>
              <Icon size={24} aria-hidden="true" />
              <span>{item.value}</span>
              <p>{item.label}</p>
            </article>
          );
        })}
      </section>
      <section className="quality-note">
        <h2>Como explicar na prova oral</h2>
        <p>
          O SonarQube complementa os testes automatizados. Os testes verificam comportamento,
          enquanto a analise estatica procura bugs, vulnerabilidades, code smells, duplicacao
          e acompanha cobertura. A ultima analise passou no Quality Gate.
        </p>
      </section>
    </main>
  );
}
```

- [ ] **Step 2: Route quality view**

In `frontend/src/app/routes.tsx`, import:

```tsx
import { QualityEvidenceView } from '../features/quality/QualityEvidenceView';
```

Replace the quality route element:

```tsx
{ path: 'quality', element: <QualityEvidenceView /> },
```

- [ ] **Step 3: Add quality CSS**

Append to `frontend/src/styles/global.css`:

```css
.quality-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.quality-grid article,
.quality-note {
  background: white;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 18px;
}

.quality-grid svg {
  color: var(--color-success);
}

.quality-grid span {
  display: block;
  margin-top: 14px;
  font-size: 26px;
  font-weight: 900;
}

.quality-grid p {
  margin: 6px 0 0;
  color: var(--color-muted);
  font-weight: 700;
}

.quality-note h2 {
  margin: 0 0 8px;
}

.quality-note p {
  margin: 0;
  max-width: 840px;
  line-height: 1.6;
}

@media (max-width: 920px) {
  .quality-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .quality-grid {
    grid-template-columns: 1fr;
  }
}
```

- [ ] **Step 4: Verify quality build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 5: Commit quality view**

Run:

```powershell
git add frontend/src
git commit -m "feat: add quality evidence view"
```

---

## Task 9: Add Documentation And Local Run Instructions

**Files:**
- Modify: `README.md`
- Create: `frontend/README.md`

- [ ] **Step 1: Create frontend README**

Create `frontend/README.md`:

```md
# Frontend - Biblioteca Pessoal

Aplicacao React + Vite + TypeScript que consome o backend Spring Boot do Gerenciador de Biblioteca Pessoal.

## Executar

Em um terminal, rode o backend:

```powershell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Em outro terminal, rode o frontend:

```powershell
cd frontend
npm install
npm run dev
```

Abra:

```text
http://localhost:5173
```

O Vite encaminha `/api` para `http://localhost:8081`, preservando cookies de sessao e o fluxo CSRF do Spring Security.

## Build

```powershell
npm run build
```

## Fluxos Para Testar

- Criar conta.
- Fazer login.
- Listar biblioteca.
- Criar livro manualmente.
- Editar livro.
- Excluir livro.
- Importar ISBN pela Open Library e salvar o preview.
- Abrir evidencias de qualidade.
```

- [ ] **Step 2: Update root README**

Add after the existing "Executar Localmente" section in `README.md`:

```md
## Executar Frontend

O frontend fica em `frontend/` e consome a API via proxy do Vite.

Terminal 1:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Terminal 2:

```bash
cd frontend
npm install
npm run dev
```

Acesse `http://localhost:5173`.
```

- [ ] **Step 3: Verify docs and build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 4: Commit docs**

Run:

```powershell
git add README.md frontend/README.md
git commit -m "docs: add frontend run instructions"
```

---

## Task 10: End-To-End Manual Verification

**Files:**
- No source files required.
- Capture screenshots under `docs/evidencias/frontend/` if requested during execution.

- [ ] **Step 1: Start backend**

Run:

```powershell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Expected: Spring Boot starts on `http://localhost:8081`.

- [ ] **Step 2: Start frontend**

Run in a second terminal:

```powershell
cd frontend
npm run dev
```

Expected: Vite starts on `http://localhost:5173`.

- [ ] **Step 3: Verify auth flow in browser**

Manual steps:

1. Open `http://localhost:5173`.
2. Confirm redirect to `/login`.
3. Open `/register`.
4. Register a user with a unique email and password with at least 8 characters.
5. Confirm redirect to `/app`.
6. Click logout.
7. Login with the same user.

Expected: user can register, logout, and login. Protected `/app` is inaccessible when unauthenticated.

- [ ] **Step 4: Verify CRUD flow**

Manual steps:

1. Click "Novo livro".
2. Create a manual book with title, author, ISBN, status and notes.
3. Confirm it appears in the table.
4. Use the search filter by title.
5. Edit the book status and rating.
6. Delete the book and confirm the dialog.

Expected: each operation updates the visible list without refreshing the page.

- [ ] **Step 5: Verify ISBN import flow**

Manual steps:

1. Enter ISBN `9780132350884`.
2. Click "Buscar".
3. Confirm preview data appears.
4. Click "Salvar na biblioteca".
5. Confirm the imported book appears in the table with origin "Open Library".

Expected: preview does not save automatically; save happens only after confirmation.

- [ ] **Step 6: Verify responsive layout**

Manual steps:

1. Open browser responsive mode at 375px width.
2. Check login form.
3. Check `/app` layout.
4. Check book table horizontal behavior.
5. Check side panel width.

Expected: no horizontal page scroll outside intentional table scroll; controls remain usable.

- [ ] **Step 7: Final build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build passes.

- [ ] **Step 8: Commit verification evidence**

If screenshots were captured:

```powershell
git add docs/evidencias/frontend
git commit -m "docs: add frontend evidence"
```

If no screenshots were captured, do not create an empty commit.

---

## Self-Review

Spec coverage:

- React + Vite + TypeScript: Task 1.
- Separate frontend: Task 1 and Task 9.
- Login/register/session: Task 3 and Task 10.
- Dashboard/listing: Task 5.
- Create/edit/delete: Task 6.
- Filters/search: Task 5.
- ISBN import preview: Task 7.
- CSRF/cookies: Task 2.
- Loading/error/empty/success states: Tasks 3, 5, 6, 7 and 8.
- Quality evidence view: Task 8.
- Responsiveness/accessibility: Tasks 3, 4, 5, 6, 7 and Task 10.
- README instructions: Task 9.

Placeholder scan:

- No task contains `TBD`, `TODO`, "implement later", "similar to", or unnamed files.
- Each code-changing step includes the concrete file and code or exact replacement target.

Type consistency:

- `BookStatus`, `MetadataSource`, DTO names and payload fields match backend Java records and enums.
- `metadataSource: "OPEN_LIBRARY"` is used for ISBN imports.
- Unsafe API methods consistently use CSRF via `unsafeRequest`.
