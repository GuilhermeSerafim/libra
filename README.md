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
- Importacao de metadados por ISBN com Open Library

## Executar Localmente

1. Suba MongoDB local ou configure `MONGODB_URI`.
2. Execute:

```bash
mvn spring-boot:run
```

## Testes

```bash
mvn verify
```

Os testes de persistencia usam MongoDB via Testcontainers. Os testes da Open Library usam VCR em Java com WireMock em modo replay, sem chamar a internet no fluxo normal.

## Atualizar VCR Da Open Library

```powershell
powershell -ExecutionPolicy Bypass -File scripts/update-openlibrary-vcr.ps1
```

A atualizacao online do VCR roda uma vez por semana no GitHub Actions para evitar cassete eterno.

## Qualidade

- Cobertura minima: 80% com JaCoCo.
- Analise estatica: SonarQube.
- CI: GitHub Actions.

## Endpoints

| Metodo | Rota | Descricao |
| --- | --- | --- |
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

Chamadas autenticadas que alteram estado usam sessao/cookie e CSRF habilitado. Inclua o token CSRF nas requisicoes inseguras, como POST, PUT e DELETE.
