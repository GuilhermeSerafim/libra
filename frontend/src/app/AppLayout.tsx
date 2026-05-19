import { BookOpen, Gauge, Library, LogOut } from 'lucide-react';
import { NavLink, Outlet } from 'react-router-dom';
import { Button } from '../components/Button';
import { useAuth } from '../features/auth/auth-context';

export function AppLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="app-frame">
      <aside className="sidebar">
        <div className="brand">
          <Library size={26} aria-hidden="true" />
          <div>
            <strong>Biblioteca</strong>
            <span>Qualidade de Software</span>
          </div>
        </div>

        <nav className="sidebar-nav" aria-label="Principal">
          <NavLink to="/app" end>
            <BookOpen size={18} aria-hidden="true" />
            Livros
          </NavLink>
          <NavLink to="/app/quality">
            <Gauge size={18} aria-hidden="true" />
            Evidencias QA
          </NavLink>
        </nav>

        <div className="sidebar-user">
          <span>{user?.name}</span>
          <small>{user?.email}</small>
          <Button type="button" variant="ghost" icon={<LogOut size={16} />} onClick={logout}>
            Sair
          </Button>
        </div>
      </aside>

      <main className="workspace">
        <Outlet />
      </main>
    </div>
  );
}
