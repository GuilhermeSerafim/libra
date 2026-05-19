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
  status: BookStatus;
  rating: number | null;
  notes: string | null;
  tags: string[];
  metadataSource: MetadataSource;
  createdAt: string;
  updatedAt: string;
};

export type BookImportPreviewResponse = {
  title: string;
  authors: string[];
  isbn: string | null;
  publisher: string | null;
  publishDate: string | null;
  pageCount: number | null;
  coverUrl: string | null;
};

export type BookWriteRequest = {
  title: string;
  authors: string[];
  isbn?: string | null;
  publisher?: string | null;
  publishDate?: string | null;
  pageCount?: number | null;
  coverUrl?: string | null;
  status: BookStatus;
  rating?: number | null;
  notes?: string | null;
  tags: string[];
  metadataSource: MetadataSource;
};

export type RegisterRequest = {
  name: string;
  email: string;
  password: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type ApiErrorResponse = {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
};
