import { useState } from 'react';
import { Download, Search } from 'lucide-react';
import { Button } from '../../components/Button';
import { Field, TextInput } from '../../components/Field';
import { api } from '../../lib/api/client';
import type { BookImportPreviewResponse, BookResponse, BookWriteRequest } from '../../lib/api/types';
import { toErrorMessage } from '../../lib/errors/messages';

type ImportIsbnPanelProps = {
  onImported: (book: BookResponse) => Promise<void> | void;
};

function previewToRequest(preview: BookImportPreviewResponse): BookWriteRequest {
  return {
    title: preview.title,
    authors: preview.authors,
    isbn: preview.isbn,
    publisher: preview.publisher,
    publishDate: preview.publishDate,
    pageCount: preview.pageCount,
    coverUrl: preview.coverUrl,
    status: 'TO_READ',
    rating: null,
    notes: null,
    tags: [],
    metadataSource: 'OPEN_LIBRARY',
  };
}

export function ImportIsbnPanel({ onImported }: ImportIsbnPanelProps) {
  const [isbn, setIsbn] = useState('');
  const [preview, setPreview] = useState<BookImportPreviewResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isPreviewing, setIsPreviewing] = useState(false);
  const [isImporting, setIsImporting] = useState(false);

  async function handlePreview(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedIsbn = isbn.trim();
    if (!normalizedIsbn) {
      setError('Informe um ISBN.');
      return;
    }

    try {
      setError(null);
      setIsPreviewing(true);
      setPreview(await api.previewByIsbn(normalizedIsbn));
    } catch (caughtError) {
      setPreview(null);
      setError(toErrorMessage(caughtError));
    } finally {
      setIsPreviewing(false);
    }
  }

  async function handleImport() {
    if (!preview) {
      return;
    }

    try {
      setError(null);
      setIsImporting(true);
      const createdBook = await api.createBook(previewToRequest(preview));
      await onImported(createdBook);
      setIsbn('');
      setPreview(null);
    } catch (caughtError) {
      setError(toErrorMessage(caughtError));
    } finally {
      setIsImporting(false);
    }
  }

  return (
    <section className="import-isbn panel" aria-labelledby="import-isbn-title">
      <div className="import-isbn__header">
        <div>
          <h2 id="import-isbn-title">Importar por ISBN</h2>
          <p>Consulte a Open Library antes de criar o registro.</p>
        </div>
      </div>

      <form className="import-isbn__form" onSubmit={handlePreview}>
        <Field label="ISBN">
          <TextInput
            value={isbn}
            onChange={(event) => setIsbn(event.target.value)}
            placeholder="978..."
            inputMode="numeric"
          />
        </Field>
        <Button type="submit" variant="secondary" icon={<Search size={16} />} disabled={isPreviewing}>
          {isPreviewing ? 'Consultando' : 'Consultar'}
        </Button>
      </form>

      {error ? (
        <p className="import-isbn__error" role="alert">
          {error}
        </p>
      ) : null}

      {preview ? (
        <article className="import-isbn__preview">
          {preview.coverUrl ? <img src={preview.coverUrl} alt="" className="import-isbn__cover" /> : null}
          <div className="import-isbn__details">
            <h3>{preview.title}</h3>
            <dl>
              <div>
                <dt>Autores</dt>
                <dd>{preview.authors.length > 0 ? preview.authors.join(', ') : 'Sem autor'}</dd>
              </div>
              <div>
                <dt>ISBN</dt>
                <dd>{preview.isbn ?? 'Nao informado'}</dd>
              </div>
              <div>
                <dt>Editora</dt>
                <dd>{preview.publisher ?? 'Nao informada'}</dd>
              </div>
              <div>
                <dt>Publicacao</dt>
                <dd>{preview.publishDate ?? 'Nao informada'}</dd>
              </div>
              <div>
                <dt>Paginas</dt>
                <dd>{preview.pageCount ?? 'Nao informado'}</dd>
              </div>
            </dl>
            <Button type="button" icon={<Download size={16} />} onClick={handleImport} disabled={isImporting}>
              {isImporting ? 'Importando' : 'Criar livro'}
            </Button>
          </div>
        </article>
      ) : null}
    </section>
  );
}
