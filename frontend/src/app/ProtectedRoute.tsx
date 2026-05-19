import { Navigate, Outlet } from 'react-router-dom';
import { LoadingState } from '../components/LoadingState';
import { useAuth } from '../features/auth/auth-context';

export function ProtectedRoute() {
  const { status } = useAuth();

  if (status === 'loading') {
    return <LoadingState label="Verificando sessao" />;
  }

  if (status === 'anonymous') {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
