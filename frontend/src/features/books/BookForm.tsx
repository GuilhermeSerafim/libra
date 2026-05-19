import { useMemo, useState } from 'react';
import { Save, X } from 'lucide-react';
import { Button } from '../../components/Button';
import { Field, SelectInput, TextArea, TextInput } from '../../components/Field';
import type { BookResponse, BookStatus, BookWriteRequest, MetadataSource } from '../../lib/api/types';

type BookFormProps = {
  book?: BookResponse | null;
  onSubmit: (request: BookWriteRequest) => Promise<void> | void;
  onCancel: () => void;
};

type FormState = {
  title: string;
  authors: string;
  isbn: string;
  publisher: string;
  publishDate: string;
  pageCount: string;
  coverUrl: string;
  status: BookStatus;
  rating: string;
  tags: string;
  notes: string;
  metadataSource: MetadataSource;
};

type FormErrors = Partial<Record<keyof FormState, string>>;

function toCsv(values: string[]) {
  return values.join(', ');
}

function splitCsv(value: string) {
  return value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

function nullableText(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
}

function nullableNumber(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? Number(trimmed) : null;
}

function initialState(book?: BookResponse | null): FormState {
  return {
    title: book?.title ?? '',
    authors: book ? toCsv(book.authors) : '',
    isbn: book?.isbn ?? '',
    publisher: book?.publisher ?? '',
    publishDate: book?.publishDate ?? '',
    pageCount: book?.pageCount?.toString() ?? '',
    coverUrl: book?.coverUrl ?? '',
    status: book?.status ?? 'TO_READ',
    rating: book?.rating?.toString() ?? '',
    tags: book ? toCsv(book.tags) : '',
    notes: book?.notes ?? '',
    metadataSource: book?.metadataSource ?? 'MANUAL',
  };
}

export function BookForm({ book, onSubmit, onCancel }: BookFormProps) {
  const [form, setForm] = useState<FormState>(() => initialState(book));
  const [errors, setErrors] = useState<FormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const title = useMemo(() => (book ? 'Editar livro' : 'Novo livro'), [book]);

  function updateField<Key extends keyof FormState>(field: Key, value: FormState[Key]) {
    setForm((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: undefined }));
  }

  function validate() {
    const nextErrors: FormErrors = {};
    const pageCount = nullableNumber(form.pageCount);
    const rating = nullableNumber(form.rating);

    if (!form.title.trim()) {
      nextErrors.title = 'Informe o titulo.';
    }

    if (splitCsv(form.authors).length === 0) {
      nextErrors.authors = 'Informe ao menos um autor.';
    }

    if (pageCount !== null && (!Number.isInteger(pageCount) || pageCount < 1)) {
      nextErrors.pageCount = 'Use um numero inteiro maior ou igual a um.';
    }

    if (rating !== null && (!Number.isInteger(rating) || rating < 1 || rating > 5)) {
      nextErrors.rating = 'Use uma nota de 1 a 5.';
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!validate()) {
      return;
    }

    const request: BookWriteRequest = {
      title: form.title.trim(),
      authors: splitCsv(form.authors),
      isbn: nullableText(form.isbn),
      publisher: nullableText(form.publisher),
      publishDate: nullableText(form.publishDate),
      pageCount: nullableNumber(form.pageCount),
      coverUrl: nullableText(form.coverUrl),
      status: form.status,
      rating: nullableNumber(form.rating),
      notes: nullableText(form.notes),
      tags: splitCsv(form.tags),
      metadataSource: form.metadataSource,
    };

    try {
      setIsSubmitting(true);
      await onSubmit(request);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <section className="book-form-panel panel" aria-labelledby="book-form-title">
      <div className="book-form-panel__header">
        <div>
          <h2 id="book-form-title">{title}</h2>
          <p>Preencha os dados principais do acervo.</p>
        </div>
        <Button type="button" variant="ghost" icon={<X size={16} />} onClick={onCancel}>
          Fechar
        </Button>
      </div>

      <form className="book-form" onSubmit={handleSubmit}>
        <div className="book-form__grid">
          <Field label="Titulo" error={errors.title}>
            <TextInput
              value={form.title}
              onChange={(event) => updateField('title', event.target.value)}
              placeholder="Nome do livro"
              required
            />
          </Field>

          <Field label="Autores" hint="Separe por virgula" error={errors.authors}>
            <TextInput
              value={form.authors}
              onChange={(event) => updateField('authors', event.target.value)}
              placeholder="Machado de Assis, Clarice Lispector"
              required
            />
          </Field>

          <Field label="ISBN">
            <TextInput
              value={form.isbn}
              onChange={(event) => updateField('isbn', event.target.value)}
              placeholder="978..."
            />
          </Field>

          <Field label="Editora">
            <TextInput
              value={form.publisher}
              onChange={(event) => updateField('publisher', event.target.value)}
              placeholder="Editora"
            />
          </Field>

          <Field label="Data publicacao" hint="Use o formato conhecido pelo livro">
            <TextInput
              value={form.publishDate}
              onChange={(event) => updateField('publishDate', event.target.value)}
              placeholder="2024-05-10 ou 2024"
            />
          </Field>

          <Field label="Paginas" error={errors.pageCount}>
            <TextInput
              type="number"
              min="1"
              step="1"
              value={form.pageCount}
              onChange={(event) => updateField('pageCount', event.target.value)}
              placeholder="320"
            />
          </Field>

          <Field label="Capa">
            <TextInput
              type="url"
              value={form.coverUrl}
              onChange={(event) => updateField('coverUrl', event.target.value)}
              placeholder="https://..."
            />
          </Field>

          <Field label="Status">
            <SelectInput
              value={form.status}
              onChange={(event) => updateField('status', event.target.value as BookStatus)}
            >
              <option value="TO_READ">A ler</option>
              <option value="READING">Lendo</option>
              <option value="READ">Lido</option>
            </SelectInput>
          </Field>

          <Field label="Rating" hint="Nota de 1 a 5" error={errors.rating}>
            <TextInput
              type="number"
              min="1"
              max="5"
              step="1"
              value={form.rating}
              onChange={(event) => updateField('rating', event.target.value)}
              placeholder="4"
            />
          </Field>

          <Field label="Origem metadata">
            <SelectInput
              value={form.metadataSource}
              onChange={(event) => updateField('metadataSource', event.target.value as MetadataSource)}
            >
              <option value="MANUAL">Manual</option>
              <option value="OPEN_LIBRARY">Open Library</option>
            </SelectInput>
          </Field>

          <Field label="Tags" hint="Separe por virgula">
            <TextInput
              value={form.tags}
              onChange={(event) => updateField('tags', event.target.value)}
              placeholder="romance, favorito, estudo"
            />
          </Field>
        </div>

        <Field label="Notas">
          <TextArea
            value={form.notes}
            onChange={(event) => updateField('notes', event.target.value)}
            placeholder="Observacoes pessoais"
          />
        </Field>

        <div className="book-form__actions">
          <Button type="button" variant="ghost" onClick={onCancel}>
            Cancelar
          </Button>
          <Button type="submit" icon={<Save size={16} />} disabled={isSubmitting}>
            {isSubmitting ? 'Salvando' : 'Salvar'}
          </Button>
        </div>
      </form>
    </section>
  );
}
