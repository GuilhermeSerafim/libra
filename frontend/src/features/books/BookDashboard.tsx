import { useEffect, useMemo, useState } from 'react';
import { BookPlus } from 'lucide-react';
import { Button } from '../../components/Button';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { EmptyState } from '../../components/EmptyState';
import { LoadingState } from '../../components/LoadingState';
import { Toast } from '../../components/Toast';
import { ApiError, api } from '../../lib/api/client';
import type { BookResponse, BookStatus, BookWriteRequest } from '../../lib/api/types';
import { toErrorMessage } from '../../lib/errors/messages';
import { useAuth } from '../auth/auth-context';
import { BookFilters } from './BookFilters';
import { BookForm } from './BookForm';
import { BookTable } from './BookTable';
import { ImportIsbnPanel } from './ImportIsbnPanel';

type FormMode =
  | { type: 'closed' }
  | { type: 'create' }
  | { type: 'edit'; book: BookResponse };

type ToastState = {
  message: string;
  tone: 'success' | 'error' | 'info';
};

const statusMetric: Record<BookStatus, keyof DashboardMetrics> = {
  TO_READ: 'toRead',
  READING: 'reading',
  READ: 'read',
};

type DashboardMetrics = {
  total: number;
  read: number;
  reading: number;
  toRead: number;
};

function searchableText(book: BookResponse) {
  return [book.title, ...book.authors, book.isbn ?? '', ...book.tags].join(' ').toLowerCase();
}

function metricItems(metrics: DashboardMetrics) {
  return [
    { label: 'Total', value: metrics.total },
    { label: 'Lidos', value: metrics.read },
    { label: 'Lendo', value: metrics.reading },
    { label: 'A ler', value: metrics.toRead },
  ];
}

export function BookDashboard() {
  const { markAnonymous } = useAuth();
  const [books, setBooks] = useState<BookResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<BookStatus | 'ALL'>('ALL');
  const [formMode, setFormMode] = useState<FormMode>({ type: 'closed' });
  const [bookToDelete, setBookToDelete] = useState<BookResponse | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [toast, setToast] = useState<ToastState | null>(null);

  useEffect(() => {
    let isMounted = true;

    api
      .listBooks()
      .then((loadedBooks) => {
        if (isMounted) {
          setBooks(loadedBooks);
        }
      })
      .catch((error: unknown) => {
        if (isMounted) {
          setToast({ message: toErrorMessage(error), tone: 'error' });
          if (error instanceof ApiError && error.status === 401) {
            markAnonymous();
          }
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [markAnonymous]);

  const metrics = useMemo(
    () =>
      books.reduce<DashboardMetrics>(
        (current, book) => ({
          ...current,
          [statusMetric[book.status]]: current[statusMetric[book.status]] + 1,
        }),
        { total: books.length, read: 0, reading: 0, toRead: 0 },
      ),
    [books],
  );

  const filteredBooks = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();

    return books.filter((book) => {
      const matchesSearch = normalizedSearch.length === 0 || searchableText(book).includes(normalizedSearch);
      const matchesStatus = statusFilter === 'ALL' || book.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [books, searchTerm, statusFilter]);

  async function handleSave(request: BookWriteRequest) {
    try {
      if (formMode.type === 'edit') {
        const updatedBook = await api.updateBook(formMode.book.id, request);
        setBooks((current) => current.map((book) => (book.id === updatedBook.id ? updatedBook : book)));
        setToast({ message: 'Livro atualizado.', tone: 'success' });
      } else {
        const createdBook = await api.createBook(request);
        setBooks((current) => [createdBook, ...current]);
        setToast({ message: 'Livro criado.', tone: 'success' });
      }

      setFormMode({ type: 'closed' });
    } catch (error) {
      setToast({ message: toErrorMessage(error), tone: 'error' });
      if (error instanceof ApiError && error.status === 401) {
        markAnonymous();
      }
    }
  }

  async function handleDelete() {
    if (!bookToDelete) {
      return;
    }

    if (isDeleting) {
      return;
    }

    try {
      setIsDeleting(true);
      await api.deleteBook(bookToDelete.id);
      setBooks((current) => current.filter((book) => book.id !== bookToDelete.id));
      setToast({ message: 'Livro excluido.', tone: 'success' });
      setBookToDelete(null);
    } catch (error) {
      setToast({ message: toErrorMessage(error), tone: 'error' });
      if (error instanceof ApiError && error.status === 401) {
        markAnonymous();
      }
    } finally {
      setIsDeleting(false);
    }
  }

  async function handleImported(book: BookResponse) {
    setBooks((current) => [book, ...current]);
    setToast({ message: 'Livro importado pela Open Library.', tone: 'success' });
  }

  function clearFilters() {
    setSearchTerm('');
    setStatusFilter('ALL');
  }

  return (
    <div className="book-dashboard">
      <header className="page-header">
        <div>
          <h1>Livros</h1>
          <p>Gerencie sua biblioteca pessoal e acompanhe o status de leitura.</p>
        </div>
        <Button type="button" icon={<BookPlus size={18} />} onClick={() => setFormMode({ type: 'create' })}>
          Novo livro
        </Button>
      </header>

      <Toast message={toast?.message ?? null} tone={toast?.tone} onClose={() => setToast(null)} />

      <section className="book-metrics" aria-label="Metricas da biblioteca">
        {metricItems(metrics).map((metric) => (
          <article className="metric-card" key={metric.label}>
            <span>{metric.label}</span>
            <strong>{metric.value}</strong>
          </article>
        ))}
      </section>

      <div className="book-dashboard__tools">
        <BookFilters
          searchTerm={searchTerm}
          statusFilter={statusFilter}
          onSearchTermChange={setSearchTerm}
          onStatusFilterChange={setStatusFilter}
          onClear={clearFilters}
        />
        <ImportIsbnPanel onImported={handleImported} />
      </div>

      {formMode.type !== 'closed' ? (
        <BookForm
          book={formMode.type === 'edit' ? formMode.book : null}
          onSubmit={handleSave}
          onCancel={() => setFormMode({ type: 'closed' })}
        />
      ) : null}

      {isLoading ? (
        <LoadingState label="Carregando livros" />
      ) : filteredBooks.length > 0 ? (
        <BookTable
          books={filteredBooks}
          onEdit={(book) => setFormMode({ type: 'edit', book })}
          onDelete={(book) => setBookToDelete(book)}
        />
      ) : (
        <EmptyState
          title={books.length === 0 ? 'Nenhum livro cadastrado' : 'Nenhum livro encontrado'}
          description={
            books.length === 0
              ? 'Crie um livro manualmente ou importe por ISBN.'
              : 'Ajuste a busca ou limpe os filtros para ver mais resultados.'
          }
          action={
            books.length === 0 ? (
              <Button type="button" icon={<BookPlus size={16} />} onClick={() => setFormMode({ type: 'create' })}>
                Novo livro
              </Button>
            ) : (
              <Button type="button" variant="secondary" onClick={clearFilters}>
                Limpar filtros
              </Button>
            )
          }
        />
      )}

      <ConfirmDialog
        open={bookToDelete !== null}
        title="Excluir livro"
        description={
          bookToDelete
            ? `Deseja excluir "${bookToDelete.title}"? Esta acao nao pode ser desfeita.`
            : 'Deseja excluir este livro?'
        }
        confirmLabel={isDeleting ? 'Excluindo' : 'Excluir'}
        isConfirming={isDeleting}
        onCancel={() => setBookToDelete(null)}
        onConfirm={handleDelete}
      />
    </div>
  );
}
