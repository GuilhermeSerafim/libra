# Design: Frontend React Do Gerenciador De Biblioteca Pessoal

## Objetivo

Implementar o frontend moderno do **Gerenciador de Biblioteca Pessoal** como uma aplicacao React separada do backend Spring Boot. O frontend deve consumir a API REST existente, demonstrar os fluxos principais do usuario e servir como evidencia visual de validacao para a disciplina.

## Decisao Aprovada

- Frontend separado do backend.
- React + Vite + TypeScript.
- Sem Thymeleaf.
- Interface em formato de app de gestao com sidebar.
- Foco inicial em funcionalidade e defesa oral, nao em landing page.

## Justificativa Da Stack

React + Vite + TypeScript e uma escolha adequada porque entrega uma experiencia moderna com baixa complexidade operacional. Vite facilita desenvolvimento local rapido, React conversa bem com uma API REST JSON e TypeScript reduz erros de contrato entre frontend e backend.

Next.js foi considerado, mas fica fora do escopo inicial porque SSR, roteamento de servidor e convencoes de deploy adicionariam complexidade que nao e necessaria para demonstrar os requisitos da disciplina.

## Escopo

Inclui:

- Tela de login.
- Tela de cadastro.
- Sessao do usuario autenticado.
- Dashboard da biblioteca pessoal.
- Listagem de livros do usuario autenticado.
- Criacao manual de livro.
- Edicao de livro.
- Exclusao de livro com confirmacao.
- Filtros e busca local na lista de livros.
- Importacao por ISBN usando endpoint de preview da Open Library.
- Tratamento de CSRF exigido pelo Spring Security.
- Estados de carregamento, erro, sucesso e vazio.
- Tela ou secao de evidencias/qualidade para apoiar a apresentacao.

Fica fora desta primeira versao:

- Deploy em producao.
- Recomendacoes de livros.
- Emprestimos, reservas, multas ou funcionalidades de biblioteca publica.
- Leitor de PDF ou gerenciamento de arquivos.
- Modo offline.
- Tema escuro completo.

## Arquitetura Do Frontend

O frontend sera uma SPA com rotas client-side e modulos separados por responsabilidade.

```text
frontend/
  src/
    app/
    components/
    features/
      auth/
      books/
      quality/
    lib/
      api/
      csrf/
      errors/
    styles/
```

Responsabilidades:

| Area | Responsabilidade |
| --- | --- |
| `app` | Roteamento, layout principal e bootstrap da aplicacao. |
| `components` | Componentes reutilizaveis de UI, como botoes, campos, modal, tabela e estados vazios. |
| `features/auth` | Login, cadastro, usuario atual e logout. |
| `features/books` | Listagem, formulario, edicao, exclusao e importacao por ISBN. |
| `features/quality` | Tela informativa com evidencias de qualidade do projeto. |
| `lib/api` | Cliente HTTP para o backend. |
| `lib/csrf` | Obtencao e envio do token CSRF antes de chamadas inseguras. |
| `styles` | Tokens de cor, tipografia, espacamento e estilos globais. |

## Rotas

| Rota | Objetivo |
| --- | --- |
| `/login` | Autenticar usuario existente. |
| `/register` | Criar nova conta. |
| `/app` | Biblioteca pessoal autenticada. |
| `/app/books/:id` | Detalhe ou edicao dedicada de livro, se a tela lateral nao for suficiente. |
| `/app/quality` | Evidencias visuais do projeto: SonarQube, cobertura, CI e conceitos aplicados. |

Rotas autenticadas devem redirecionar para `/login` quando `GET /api/auth/me` indicar usuario nao autenticado.

## Contrato Com Backend

O frontend consumira os endpoints existentes:

| Fluxo | Endpoint |
| --- | --- |
| CSRF | `GET /api/auth/csrf` |
| Cadastro | `POST /api/auth/register` |
| Login | `POST /api/auth/login` |
| Logout | `POST /api/auth/logout` |
| Usuario atual | `GET /api/auth/me` |
| Listar livros | `GET /api/books` |
| Criar livro | `POST /api/books` |
| Consultar livro | `GET /api/books/{id}` |
| Atualizar livro | `PUT /api/books/{id}` |
| Excluir livro | `DELETE /api/books/{id}` |
| Preview por ISBN | `GET /api/books/import/isbn/{isbn}` |

Chamadas `POST`, `PUT` e `DELETE` devem buscar ou reutilizar token CSRF e enviar o header `X-XSRF-TOKEN`. O cliente HTTP deve usar cookies de sessao.

## Fluxos Principais

### Autenticacao

1. Usuario abre `/login`.
2. Frontend chama `GET /api/auth/csrf`.
3. Usuario informa email e senha.
4. Frontend envia `POST /api/auth/login` com CSRF.
5. Backend cria sessao.
6. Frontend redireciona para `/app`.

Cadastro segue o mesmo padrao, usando `POST /api/auth/register`.

### Biblioteca

1. Frontend chama `GET /api/auth/me`.
2. Se autenticado, chama `GET /api/books`.
3. Dashboard mostra lista, filtros, contadores e acoes.
4. Criacao e edicao usam formulario com os campos reais do backend.
5. Exclusao exige confirmacao antes de chamar `DELETE /api/books/{id}`.

### Importacao Por ISBN

1. Usuario abre painel "Importar ISBN".
2. Informa ISBN.
3. Frontend chama `GET /api/books/import/isbn/{isbn}`.
4. Preview mostra titulo, autores, editora, data, paginas e capa.
5. Usuario revisa os dados.
6. Ao confirmar, frontend envia `POST /api/books` com `metadataSource: "OPEN_LIBRARY"`.

O preview nao salva automaticamente. Essa decisao ajuda a explicar validacao e controle do usuario na prova oral.

## Direcao Visual

A interface sera utilitaria, clara e moderna, adequada para ferramenta de gestao:

- Fundo claro.
- Sidebar escura.
- Azul como cor principal de acao.
- Cards de raio pequeno, ate 8px.
- Tipografia limpa e legivel.
- Icones Lucide para acoes.
- Layout denso o suficiente para uso real, sem composicao de marketing.

Paleta inicial:

| Token | Valor |
| --- | --- |
| `--color-background` | `#f8fafc` |
| `--color-surface` | `#ffffff` |
| `--color-sidebar` | `#0f172a` |
| `--color-primary` | `#2563eb` |
| `--color-text` | `#111827` |
| `--color-muted` | `#64748b` |
| `--color-border` | `#dbe3ee` |
| `--color-danger` | `#dc2626` |
| `--color-success` | `#15803d` |

## Componentes Esperados

- `AppLayout`
- `Sidebar`
- `Topbar`
- `ProtectedRoute`
- `AuthForm`
- `BookList`
- `BookTable`
- `BookFilters`
- `BookForm`
- `BookDetailsPanel`
- `ImportIsbnPanel`
- `ConfirmDialog`
- `Toast`
- `EmptyState`
- `LoadingState`
- `ErrorState`
- `QualityEvidenceView`

## Estados E Erros

Todo fluxo assincromo deve ter estado de carregamento, sucesso e erro.

Erros de validacao devem aparecer perto do campo relacionado quando possivel. Erros gerais de API devem aparecer como alerta ou toast com mensagem clara. Se a sessao expirar, o usuario deve ser redirecionado para login.

## Acessibilidade E Responsividade

Requisitos minimos:

- Labels visiveis em todos os campos.
- Foco visivel em botoes, links e campos.
- Botoes com texto ou `aria-label`.
- Contraste adequado em textos e estados.
- Layout funcional em desktop e mobile.
- Em mobile, sidebar vira menu recolhivel ou navegacao superior compacta.
- Confirmacao antes de acoes destrutivas.

## Testes E Verificacao

Na primeira versao, a verificacao deve incluir:

- Build TypeScript.
- Teste manual dos fluxos via frontend.
- Evidencias visuais dos fluxos principais.
- Opcionalmente, testes de componentes ou fluxo com ferramenta apropriada em etapa posterior.

Fluxos manuais obrigatorios:

- Cadastro.
- Login.
- Logout.
- Listar livros.
- Criar livro manual.
- Editar livro.
- Excluir livro.
- Importar ISBN e salvar a partir do preview.

## Aplicacao Na Prova Oral

Resposta curta sugerida:

"O frontend foi criado separado do backend para consumir a API REST via JSON. Ele usa React, Vite e TypeScript, mantendo a responsabilidade visual fora do Spring Boot. A autenticacao respeita a sessao e o CSRF do backend. A tela principal mostra o CRUD de livros, isolamento por usuario e importacao por ISBN com preview antes de salvar. Assim, o frontend ajuda a validar os requisitos funcionais e tambem serve como evidencia visual na apresentacao."

## Criterios De Aceite

- Frontend criado com React + Vite + TypeScript.
- Rotas de login, cadastro e app autenticado implementadas.
- Cliente HTTP envia cookies e CSRF corretamente.
- CRUD de livros funciona pela interface.
- Importacao por ISBN mostra preview antes de salvar.
- Estados de carregamento, erro, vazio e sucesso aparecem na UI.
- Layout responsivo em desktop e mobile.
- README documenta como executar backend e frontend juntos.
- Evidencias visuais dos principais fluxos podem ser usadas na apresentacao.
