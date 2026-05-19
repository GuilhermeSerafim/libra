# Frontend Biblioteca Pessoal

SPA em React + Vite + TypeScript para consumir o backend REST do projeto de Qualidade de Software.

## Executar

Com o backend Spring Boot rodando em `http://localhost:8081`:

```bash
npm install
npm run dev
```

Acesse:

- `http://localhost:5173`

## Scripts

- `npm run dev`: inicia o servidor Vite.
- `npm run build`: valida TypeScript e gera build de producao.
- `npm run lint`: executa ESLint.

## Integracao Com Backend

O proxy em `vite.config.ts` encaminha `/api` para `http://localhost:8081`, permitindo usar cookies de sessao e CSRF sem configurar CORS no backend.

Fluxos principais:

- Cadastro e login com Spring Security.
- CRUD de livros autenticado.
- Importacao por ISBN usando Open Library via backend.
- Tela de evidencias para SonarQube, JaCoCo, Testcontainers, WireMock/VCR e CI.

