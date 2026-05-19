import { useCallback, useEffect, useMemo, useState } from 'react';
import { api } from '../../lib/api/client';
import type { LoginRequest, RegisterRequest, UserResponse } from '../../lib/api/types';
import { AuthContext } from './auth-context';
import type { AuthStatus } from './auth-context';

type AuthProviderProps = {
  children: React.ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [status, setStatus] = useState<AuthStatus>('loading');

  useEffect(() => {
    let isActive = true;

    api
      .me()
      .then((currentUser) => {
        if (!isActive) {
          return;
        }

        setUser(currentUser);
        setStatus('authenticated');
      })
      .catch(() => {
        if (!isActive) {
          return;
        }

        setUser(null);
        setStatus('anonymous');
      });

    return () => {
      isActive = false;
    };
  }, []);

  const login = useCallback(async (request: LoginRequest) => {
    const response = await api.login(request);
    setUser(response.user);
    setStatus('authenticated');
  }, []);

  const register = useCallback(async (request: RegisterRequest) => {
    await api.register(request);
    const response = await api.login({ email: request.email, password: request.password });
    setUser(response.user);
    setStatus('authenticated');
  }, []);

  const logout = useCallback(async () => {
    try {
      await api.logout();
    } catch {
      // Mesmo com sessao expirada, o estado local deve voltar para anonimo.
    }

    setUser(null);
    setStatus('anonymous');
  }, []);

  const markAnonymous = useCallback(() => {
    setUser(null);
    setStatus('anonymous');
  }, []);

  const value = useMemo(
    () => ({ user, status, login, register, logout, markAnonymous }),
    [user, status, login, register, logout, markAnonymous],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
