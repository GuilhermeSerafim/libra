# Evidencias Do Frontend

Capturas geradas durante a verificacao manual do frontend React em `http://localhost:5173`.

## Arquivos

- `login.png`: tela publica de login.
- `dashboard-empty.png`: dashboard autenticado sem livros.
- `books-created.png`: dashboard autenticado apos criar um livro manualmente.
- `books-edited.png`: dashboard apos editar rating/notas de um livro.
- `import-preview.png`: preview de ISBN retornado pela Open Library via backend.
- `import-created.png`: livro criado a partir do preview ISBN com origem Open Library.
- `book-deleted.png`: dashboard apos exclusao confirmada de um livro.
- `login-after-logout.png`: redirecionamento para login apos logout.
- `login-success.png`: login explicito retornando para `/app`.
- `quality.png`: tela de evidencias de qualidade e apoio para prova oral.

## Fluxo Verificado

1. Cadastro de usuario de teste pelo frontend.
2. Login automatico apos cadastro, chamando o fluxo real do backend.
3. Acesso ao dashboard autenticado em `/app`.
4. Criacao manual de livro.
5. Edicao de livro existente.
6. Consulta de ISBN pela Open Library via backend.
7. Criacao de livro importado com `metadataSource: OPEN_LIBRARY`.
8. Exclusao de livro com dialogo de confirmacao.
9. Logout e login explicito.
10. Navegacao para `/app/quality`.
