# libra

Projeto acadêmico full stack para organizar os livros de usuários autenticados. Oferece cadastro e sessão, CRUD de livros e consulta de metadados por ISBN com a Open Library. O foco técnico é controle de acesso por usuário, integração entre SPA e cookies/CSRF, tratamento de falhas externas e testes automatizados com Testcontainers e WireMock.

## Demonstração e imagens

Capturas existentes: [livros cadastrados](docs/evidencias/frontend/books-created.png), [prévia de importação](docs/evidencias/frontend/import-preview.png). Estes arquivos já pertencem ao projeto.

## Minha contribuição e autoria

Este repositório reúne minha implementação e documentação de um projeto acadêmico. As decisões de controle de acesso, integração e testes podem ser acompanhadas nos serviços, testes e documentos versionados; os indicadores históricos não substituem uma execução nova.

## Arquitetura e implementação

O backend separa controllers, DTOs, services, repositories e clientes externos. Operações sobre livros consultam o proprietário autenticado junto com o identificador do registro. A SPA usa o proxy `/api` do Vite para enviar cookies e token CSRF. A Open Library fica isolada em um cliente com timeout e tratamento de falhas.

Veja [BookService](src/main/java/br/senac/biblioteca/service/BookService.java), [testes de autorização](src/test/java/br/senac/biblioteca/controller/BookControllerTest.java) e [testes do cliente externo](src/test/java/br/senac/biblioteca/client/OpenLibraryClientTest.java).

## Instalação e configuração

Java 21, Maven, MongoDB e Node 22. Os testes de integração usam Docker com Testcontainers. Configure `MONGODB_URI` para uma instância de desenvolvimento; `OPEN_LIBRARY_BASE_URL` permite configurar o serviço de consulta de ISBN.

Terminal 1, na raiz do repositório, com MongoDB disponível:

```text
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Terminal 2, começando na raiz do repositório:

```text
cd frontend
npm ci
npm run dev
```

## Testes e verificações

Os comandos abaixo documentam a suíte existente; não representam uma execução nova nem um resultado de aprovação.

Em um novo terminal, na raiz do repositório, com Docker em execução:

```text
mvn verify
```

Testes e build do frontend, começando novamente na raiz:

```text
cd frontend
npm test
npm run lint
npm run build
```

`mvn verify` requer Docker para o MongoDB de teste. O backend em desenvolvimento deve usar a porta 8081 para coincidir com o proxy do frontend.

## Limites atuais

- Métricas de cobertura/Sonar registradas nos documentos são evidências históricas, não promessa de estado atual.
- A suíte de frontend existente não é uma suíte completa de navegação ponta a ponta.
- O workflow quality executa lint/build frontend, mas não npm test; a atualização automática VCR está desativada por inatividade.

<details>
<summary>Referência de uso e documentação complementar</summary>

## Acessos locais

O frontend fica em `http://localhost:5173`. O proxy do Vite encaminha `/api` para `http://localhost:8081`, preservando os cookies de sessão e o fluxo CSRF.

## Swagger UI

Com a aplicacao rodando, a documentacao interativa fica em:

- `http://localhost:8081/swagger-ui/index.html`
- `http://localhost:8081/v3/api-docs`

Os endpoints do Swagger ficam publicos para facilitar a avaliacao e a apresentacao. As rotas da API continuam protegidas por sessao e CSRF conforme a regra abaixo.

## Endpoints

| Metodo | Rota | Descricao |
| --- | --- | --- |
| GET | `/api/auth/csrf` | Obter cookie/token CSRF |
| POST | `/api/auth/register` | Cadastrar usuario |
| POST | `/api/auth/login` | Login com sessao |
| POST | `/api/auth/logout` | Logout |
| GET | `/api/auth/me` | Usuario autenticado |
| GET | `/api/books` | Listar livros |
| POST | `/api/books` | Criar livro |
| GET | `/api/books/{id}` | Consultar livro |
| PUT | `/api/books/{id}` | Atualizar livro |
| DELETE | `/api/books/{id}` | Excluir livro |
| GET | `/api/books/import/isbn/{isbn}` | Pre-visualizar metadados por ISBN |

CSRF esta habilitado para chamadas inseguras. Antes de enviar POST, PUT ou DELETE, incluindo `/api/auth/register`, `/api/auth/login` e `/api/auth/logout`, chame `GET /api/auth/csrf`, obtenha o cookie `XSRF-TOKEN` e envie o valor no header `X-XSRF-TOKEN`.

## Atualizar VCR Da Open Library

```powershell
powershell -ExecutionPolicy Bypass -File scripts/update-openlibrary-vcr.ps1
```

## Documentacao Da Entrega

| Documento | Finalidade |
| --- | --- |
| [docs/00-visao-geral.md](docs/00-visao-geral.md) | Escopo, stack oficial, premissas e entregaveis. |
| [docs/01-requisitos.md](docs/01-requisitos.md) | Requisitos funcionais, nao funcionais e restricoes tecnicas. |
| [docs/02-casos-de-uso.md](docs/02-casos-de-uso.md) | Fluxos principais, alternativos e excecoes. |
| [docs/03-rtm.md](docs/03-rtm.md) | Rastreabilidade entre RFs, UCs, TCs, evidencias e conceitos. |
| [docs/04-plano-de-testes.md](docs/04-plano-de-testes.md) | Estrategia, criterios, riscos e metricas de teste. |
| [docs/05-casos-de-teste.md](docs/05-casos-de-teste.md) | Catalogo TC-001 a TC-025. |
| [docs/06-estrategia-automacao.md](docs/06-estrategia-automacao.md) | Testcontainers, VCR/WireMock, regressao e camadas automatizadas. |
| [docs/07-ci-sonar-cobertura.md](docs/07-ci-sonar-cobertura.md) | GitHub Actions, JaCoCo, SonarQube e Quality Gate. |
| [docs/08-gestao-bugs-e-evidencias.md](docs/08-gestao-bugs-e-evidencias.md) | Registro de bugs, severidade, prioridade e evidencias objetivas. |
| [docs/09-checklist-entrega.md](docs/09-checklist-entrega.md) | Checklist final antes da apresentacao. |
| [docs/10-guia-prova-oral.md](docs/10-guia-prova-oral.md) | Roteiro de defesa e perguntas provaveis. |
| [docs/11-diagramas-sequencia.md](docs/11-diagramas-sequencia.md) | Diagramas UML de sequencia dos fluxos principais. |

</details>
