import type {
  AuthResponse,
  BookImportPreviewResponse,
  BookResponse,
  BookWriteRequest,
  CsrfTokenResponse,
  LoginRequest,
  RegisterRequest,
  UserResponse,
} from './types';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

let csrfToken: CsrfTokenResponse | null = null;

export class ApiError extends Error {
  readonly status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

async function parseResponse<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get('content-type') ?? '';
  const payload = contentType.includes('application/json') ? await response.json() : null;

  if (!response.ok) {
    const message = payload?.message ?? payload?.error ?? `Erro HTTP ${response.status}`;
    throw new ApiError(message, response.status);
  }

  return payload as T;
}

async function loadCsrfToken(): Promise<CsrfTokenResponse> {
  csrfToken = await apiRequest<CsrfTokenResponse>('/api/auth/csrf', { method: 'GET', skipCsrf: true });
  return csrfToken;
}

type RequestOptions = {
  method?: HttpMethod;
  body?: unknown;
  skipCsrf?: boolean;
};

async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const method = options.method ?? 'GET';
  const headers = new Headers();

  if (options.body !== undefined) {
    headers.set('Content-Type', 'application/json');
  }

  if (!options.skipCsrf && method !== 'GET') {
    const token = csrfToken ?? (await loadCsrfToken());
    headers.set(token.headerName, token.token);
  }

  const response = await fetch(path, {
    method,
    credentials: 'include',
    headers,
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  });

  return parseResponse<T>(response);
}

export const api = {
  csrf: () => loadCsrfToken(),
  register: (request: RegisterRequest) =>
    apiRequest<AuthResponse>('/api/auth/register', { method: 'POST', body: request }),
  login: (request: LoginRequest) =>
    apiRequest<AuthResponse>('/api/auth/login', { method: 'POST', body: request }),
  logout: () => apiRequest<void>('/api/auth/logout', { method: 'POST' }),
  me: () => apiRequest<UserResponse>('/api/auth/me'),
  listBooks: () => apiRequest<BookResponse[]>('/api/books'),
  createBook: (request: BookWriteRequest) =>
    apiRequest<BookResponse>('/api/books', { method: 'POST', body: request }),
  getBook: (id: string) => apiRequest<BookResponse>(`/api/books/${id}`),
  updateBook: (id: string, request: BookWriteRequest) =>
    apiRequest<BookResponse>(`/api/books/${id}`, { method: 'PUT', body: request }),
  deleteBook: (id: string) => apiRequest<void>(`/api/books/${id}`, { method: 'DELETE' }),
  previewByIsbn: (isbn: string) =>
    apiRequest<BookImportPreviewResponse>(`/api/books/import/isbn/${encodeURIComponent(isbn)}`),
};
