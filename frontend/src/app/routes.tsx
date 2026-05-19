import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppLayout } from './AppLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { LoginPage } from '../features/auth/LoginPage';
import { RegisterPage } from '../features/auth/RegisterPage';
import { BookDashboard } from '../features/books/BookDashboard';
import { QualityEvidenceView } from '../features/quality/QualityEvidenceView';

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          {
            path: '/',
            element: <Navigate to="/app" replace />,
          },
          {
            path: '/app',
            element: <BookDashboard />,
          },
          {
            path: '/app/quality',
            element: <QualityEvidenceView />,
          },
          {
            path: '/books',
            element: <Navigate to="/app" replace />,
          },
          {
            path: '/quality',
            element: <Navigate to="/app/quality" replace />,
          },
        ],
      },
    ],
  },
]);
