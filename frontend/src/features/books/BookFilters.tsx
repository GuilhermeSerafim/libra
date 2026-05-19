import { Search, X } from 'lucide-react';
import { Button } from '../../components/Button';
import { Field, SelectInput, TextInput } from '../../components/Field';
import type { BookStatus } from '../../lib/api/types';

type BookFiltersProps = {
  searchTerm: string;
  statusFilter: BookStatus | 'ALL';
  onSearchTermChange: (value: string) => void;
  onStatusFilterChange: (value: BookStatus | 'ALL') => void;
  onClear: () => void;
};

export function BookFilters({
  searchTerm,
  statusFilter,
  onSearchTermChange,
  onStatusFilterChange,
  onClear,
}: BookFiltersProps) {
  const hasActiveFilters = searchTerm.trim().length > 0 || statusFilter !== 'ALL';

  return (
    <section className="book-filters" aria-label="Filtros de livros">
      <Field label="Buscar" hint="Titulo, autor, ISBN ou tag">
        <div className="book-filters__search">
          <Search size={18} aria-hidden="true" />
          <TextInput
            type="search"
            value={searchTerm}
            onChange={(event) => onSearchTermChange(event.target.value)}
            placeholder="Buscar na biblioteca"
          />
        </div>
      </Field>

      <Field label="Status">
        <SelectInput
          value={statusFilter}
          onChange={(event) => onStatusFilterChange(event.target.value as BookStatus | 'ALL')}
        >
          <option value="ALL">Todos</option>
          <option value="TO_READ">A ler</option>
          <option value="READING">Lendo</option>
          <option value="READ">Lido</option>
        </SelectInput>
      </Field>

      <div className="book-filters__actions">
        <Button type="button" variant="ghost" icon={<X size={16} />} onClick={onClear} disabled={!hasActiveFilters}>
          Limpar
        </Button>
      </div>
    </section>
  );
}
