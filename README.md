# Gerenciador de Biblioteca Pessoal

Backend REST API em Spring Boot para organizar livros de usuarios autenticados.
Frontend SPA em React + Vite para consumir a API durante a demonstracao.

## Stack

- Java 21
- Spring Boot
- Spring Security com sessao/cookie
- MongoDB
- Testcontainers
- VCR em Java com WireMock
- JaCoCo
- SonarQube
- GitHub Actions
- React + Vite + TypeScript

## Funcionalidades

- Cadastro de usuario
- Login, logout e sessao
- CRUD de livros do usuario autenticado
- Restricao de acesso por usuario
- Pre-visualizacao de metadados por ISBN com Open Library

## Pre-requisitos

- Java 21
- Maven
- Docker em execucao para testes com Testcontainers
- MongoDB local ou `MONGODB_URI` para executar a aplicacao

## Executar Localmente

Execute:

```bash
mvn spring-boot:run
```

Se a porta `8080` estiver ocupada, execute em outra porta:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Executar Frontend

Com o backend rodando em `8081`, abra outro terminal:

```bash
cd frontend
npm install
npm run dev
```

O frontend fica em:

- `http://localhost:5173`

O Vite encaminha chamadas `/api` para `http://localhost:8081`, preservando cookies de sessao e o fluxo CSRF do Spring Security.

## Swagger UI

Com a aplicacao rodando, a documentacao interativa fica em:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

Os endpoints do Swagger ficam publicos para facilitar a avaliacao e a apresentacao. As rotas da API continuam protegidas por sessao e CSRF conforme a regra abaixo.

## Testes

```bash
mvn verify
```

Os testes de persistencia usam MongoDB via Testcontainers. Os testes da Open Library usam VCR em Java com WireMock em modo replay, sem chamar a internet no fluxo normal.

## Atualizar VCR Da Open Library

```powershell
powershell -ExecutionPolicy Bypass -File scripts/update-openlibrary-vcr.ps1
```

A atualizacao online da fixture/resposta VCR roda uma vez por semana no GitHub Actions para evitar cassete eterno.

## Qualidade

- Cobertura minima: 80% com JaCoCo.
- Analise estatica: SonarQube.
- CI: GitHub Actions executando backend (`mvn verify`) e frontend (`npm run lint` + `npm run build`).
- Evidencias SonarQube: `docs/evidencias/sonarqube/`.
- Evidencias do frontend: `docs/evidencias/frontend/`.
- Diagramas UML de sequencia: `docs/11-diagramas-sequencia.md`.

Ultima analise SonarQube registrada:

- Quality Gate: Passed.
- Coverage on New Code: 100%.
- Overall Coverage: 90.1%.
- Duplications on New Code: 0.0%.
- Bugs, vulnerabilidades e code smells: 0.
- Testes importados: 32.

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

## Documentacao Da Entrega

| Documento | Finalidade |
| --- | --- |
| `docs/00-visao-geral.md` | Escopo, stack oficial, premissas e entregaveis. |
| `docs/01-requisitos.md` | Requisitos funcionais, nao funcionais e restricoes tecnicas. |
| `docs/02-casos-de-uso.md` | Fluxos principais, alternativos e excecoes. |
| `docs/03-rtm.md` | Rastreabilidade entre RFs, UCs, TCs, evidencias e conceitos. |
| `docs/04-plano-de-testes.md` | Estrategia, criterios, riscos e metricas de teste. |
| `docs/05-casos-de-teste.md` | Catalogo TC-001 a TC-025. |
| `docs/06-estrategia-automacao.md` | Testcontainers, VCR/WireMock, regressao e camadas automatizadas. |
| `docs/07-ci-sonar-cobertura.md` | GitHub Actions, JaCoCo, SonarQube e Quality Gate. |
| `docs/08-gestao-bugs-e-evidencias.md` | Registro de bugs, severidade, prioridade e evidencias objetivas. |
| `docs/09-checklist-entrega.md` | Checklist final antes da apresentacao. |
| `docs/10-guia-prova-oral.md` | Roteiro de defesa e perguntas provaveis. |
| `docs/11-diagramas-sequencia.md` | Diagramas UML de sequencia dos fluxos principais. |
