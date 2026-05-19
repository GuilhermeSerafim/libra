import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../../components/Button';
import { Field, TextInput } from '../../components/Field';
import { LoadingState } from '../../components/LoadingState';
import { Toast } from '../../components/Toast';
import { toErrorMessage } from '../../lib/errors/messages';
import { useAuth } from './auth-context';

const MIN_PASSWORD_LENGTH = 8;

export function RegisterPage() {
  const navigate = useNavigate();
  const { register, status } = useAuth();
  const [name, setName] = useState('');
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

    if (password.length < MIN_PASSWORD_LENGTH) {
      setError('A senha deve ter pelo menos 8 caracteres.');
      return;
    }

    setIsSubmitting(true);

    try {
      await register({ name: name.trim(), email: email.trim(), password });
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
      <section className="auth-panel" aria-labelledby="register-title">
        <header className="auth-panel__header">
          <h1 id="register-title">Criar conta</h1>
          <p>Informe seus dados para comecar a usar a biblioteca.</p>
        </header>

        <Toast message={error} tone="error" onClose={() => setError(null)} />

        <form className="auth-form" onSubmit={handleSubmit}>
          <Field label="Nome">
            <TextInput
              type="text"
              name="name"
              autoComplete="name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              required
            />
          </Field>

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

          <Field label="Senha" hint="Use pelo menos 8 caracteres.">
            <TextInput
              type="password"
              name="password"
              autoComplete="new-password"
              minLength={MIN_PASSWORD_LENGTH}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </Field>

          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Criando conta...' : 'Criar conta'}
          </Button>
        </form>

        <p className="auth-panel__footer">
          Ja tem conta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </main>
  );
}
