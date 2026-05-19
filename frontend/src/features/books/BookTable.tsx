import { Edit3, Star, Trash2 } from 'lucide-react';
import { Button } from '../../components/Button';
import type { BookResponse, BookStatus, MetadataSource } from '../../lib/api/types';

type BookTableProps = {
  books: BookResponse[];
  onEdit: (book: BookResponse) => void;
  onDelete: (book: BookResponse) => void;
};

const statusLabel: Record<BookStatus, string> = {
  TO_READ: 'A ler',
  READING: 'Lendo',
  READ: 'Lido',
};

const sourceLabel: Record<MetadataSource, string> = {
  MANUAL: 'Manual',
  OPEN_LIBRARY: 'Open Library',
};

function formatAuthors(authors: string[]) {
  return authors.length > 0 ? authors.join(', ') : 'Sem autor';
}

function formatRating(rating: number | null) {
  return rating ? `${rating}/5` : 'Sem nota';
}

export function BookTable({ books, onEdit, onDelete }: BookTableProps) {
  return (
    <div className="book-table panel">
      <table>
        <thead>
          <tr>
            <th scope="col">Livro</th>
            <th scope="col">Autores</th>
            <th scope="col">Status</th>
            <th scope="col">Rating</th>
            <th scope="col">Origem</th>
            <th scope="col">Tags</th>
            <th scope="col" className="book-table__actions-heading">
              Acoes
            </th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.id}>
              <td>
                <div className="book-table__title-cell">
                  {book.coverUrl ? <img src={book.coverUrl} alt="" className="book-table__cover" /> : null}
                  <div>
                    <strong>{book.title}</strong>
                    <span>{book.isbn ?? 'Sem ISBN'}</span>
                  </div>
                </div>
              </td>
              <td>{formatAuthors(book.authors)}</td>
              <td>
                <span className={`book-status book-status--${book.status.toLowerCase()}`}>
                  {statusLabel[book.status]}
                </span>
              </td>
              <td>
                <span className="book-rating">
                  <Star size={14} aria-hidden="true" />
                  {formatRating(book.rating)}
                </span>
              </td>
              <td>{sourceLabel[book.metadataSource]}</td>
              <td>
                <div className="book-tags">
                  {book.tags.length > 0 ? book.tags.map((tag) => <span key={tag}>{tag}</span>) : 'Sem tags'}
                </div>
              </td>
              <td>
                <div className="book-table__actions">
                  <Button
                    type="button"
                    variant="ghost"
                    icon={<Edit3 size={16} />}
                    onClick={() => onEdit(book)}
                    aria-label={`Editar ${book.title}`}
                    title="Editar"
                  />
                  <Button
                    type="button"
                    variant="danger"
                    icon={<Trash2 size={16} />}
                    onClick={() => onDelete(book)}
                    aria-label={`Excluir ${book.title}`}
                    title="Excluir"
                  />
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
