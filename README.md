# Gerenciador de Biblioteca Pessoal

Backend REST API em Spring Boot para organizar livros de usuarios autenticados.

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
- CI: GitHub Actions.
- Evidencias SonarQube: `docs/evidencias/sonarqube/`.

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
