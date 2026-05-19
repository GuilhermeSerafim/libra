import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../../components/Button';
import { Field, TextInput } from '../../components/Field';
import { LoadingState } from '../../components/LoadingState';
import { Toast } from '../../components/Toast';
import { toErrorMessage } from '../../lib/errors/messages';
import { useAuth } from './auth-context';

export function LoginPage() {
  const navigate = useNavigate();
  const { login, status } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (status === 'authenticated') {
      navigate('/app', { replace: true });
    }
  }, [navigate, status]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await login({ email: email.trim(), password });
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  if (status === 'loading') {
    return <LoadingState label="Verificando sessao" />;
  }

  return (
    <main className="auth-page">
      <section className="auth-panel" aria-labelledby="login-title">
        <header className="auth-panel__header">
          <h1 id="login-title">Entrar</h1>
          <p>Acesse sua conta para gerenciar a biblioteca.</p>
        </header>

        <Toast message={error} tone="error" onClose={() => setError(null)} />

        <form className="auth-form" onSubmit={handleSubmit}>
          <Field label="Email">
            <TextInput
              type="email"
              name="email"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </Field>

          <Field label="Senha">
            <TextInput
              type="password"
              name="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </Field>

          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Entrando...' : 'Entrar'}
          </Button>
        </form>

        <p className="auth-panel__footer">
          Ainda nao tem conta? <Link to="/register">Criar conta</Link>
        </p>
      </section>
    </main>
  );
}
