import type { ApiErrorResponse } from '../api/types';

export function toErrorMessage(error: unknown): string {
  if (error instanceof Error && error.message) {
    return error.message;
  }

  if (typeof error === 'object' && error !== null && 'message' in error) {
    return String((error as ApiErrorResponse).message);
  }

  return 'Nao foi possivel concluir a operacao.';
}
