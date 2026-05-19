# Diagramas UML De Sequencia

## Objetivo

Este documento registra os diagramas UML de sequencia dos fluxos principais do Gerenciador de Biblioteca Pessoal. Eles complementam os casos de uso, a RTM e os casos de teste, mostrando a ordem das interacoes entre usuario, frontend, backend, servicos, banco MongoDB e Open Library API.

Na prova oral, estes diagramas devem ser usados para explicar como cada requisito sai da acao do usuario, passa pelas camadas da aplicacao e termina em uma resposta verificavel.

## UC-01 - Cadastro De Usuario

Requisitos: RF-01, RF-03

```mermaid
sequenceDiagram
    actor Visitante
    participant Frontend as React Frontend
    participant AuthController
    participant AuthService
    participant UserRepository
    participant MongoDB

    Visitante->>Frontend: Preenche nome, email e senha
    Frontend->>AuthController: GET /api/auth/csrf
    AuthController-->>Frontend: Cookie XSRF-TOKEN e header X-XSRF-TOKEN
    Frontend->>AuthController: POST /api/auth/register
    AuthController->>AuthService: register(dados)
    AuthService->>UserRepository: existsByEmail(email)
    UserRepository->>MongoDB: Consulta usuario por email
    MongoDB-->>UserRepository: Usuario nao encontrado
    AuthService->>UserRepository: save(usuario com senha criptografada)
    UserRepository->>MongoDB: Persiste usuario
    MongoDB-->>UserRepository: Usuario salvo
    AuthService-->>AuthController: Usuario cadastrado
    AuthController-->>Frontend: 201 Created + usuario autenticado
    Frontend-->>Visitante: Redireciona para biblioteca
```

Como explicar: o cadastro valida dados, verifica duplicidade de email, salva o usuario no MongoDB e cria uma experiencia autenticada para o uso da biblioteca.

## UC-02 E UC-03 - Login, Sessao E Logout

Requisitos: RF-02, RF-03

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend as React Frontend
    participant AuthController
    participant SpringSecurity as Spring Security
    participant UserRepository
    participant MongoDB

    Usuario->>Frontend: Informa email e senha
    Frontend->>AuthController: GET /api/auth/csrf
    AuthController-->>Frontend: Cookie XSRF-TOKEN
    Frontend->>AuthController: POST /api/auth/login
    AuthController->>SpringSecurity: Autentica credenciais
    SpringSecurity->>UserRepository: findByEmail(email)
    UserRepository->>MongoDB: Busca usuario
    MongoDB-->>UserRepository: Usuario encontrado
    UserRepository-->>SpringSecurity: Dados do usuario
    SpringSecurity-->>AuthController: Sessao autenticada
    AuthController-->>Frontend: 200 OK + usuario atual
    Frontend-->>Usuario: Exibe dashboard autenticado

    Usuario->>Frontend: Solicita logout
    Frontend->>AuthController: POST /api/auth/logout com X-XSRF-TOKEN
    AuthController->>SpringSecurity: Invalida sessao
    SpringSecurity-->>Frontend: Sessao encerrada
    Frontend-->>Usuario: Retorna para login
```

Como explicar: a autenticacao usa Spring Security com sessao/cookie. O CSRF protege chamadas inseguras, e o logout invalida a sessao.

## UC-04 E UC-05 - Criar E Listar Livros

Requisitos: RF-04, RF-05, RF-08

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend as React Frontend
    participant BookController
    participant CurrentUserService
    participant BookService
    participant BookRepository
    participant MongoDB

    Usuario->>Frontend: Preenche dados do livro
    Frontend->>BookController: POST /api/books com X-XSRF-TOKEN
    BookController->>CurrentUserService: Obter usuario autenticado
    CurrentUserService-->>BookController: userId atual
    BookController->>BookService: create(userId, dados)
    BookService->>BookRepository: save(livro associado ao userId)
    BookRepository->>MongoDB: Persiste livro
    MongoDB-->>BookRepository: Livro salvo
    BookService-->>BookController: Livro criado
    BookController-->>Frontend: 201 Created

    Frontend->>BookController: GET /api/books
    BookController->>CurrentUserService: Obter usuario autenticado
    CurrentUserService-->>BookController: userId atual
    BookController->>BookService: listByUser(userId)
    BookService->>BookRepository: findByUserId(userId)
    BookRepository->>MongoDB: Consulta livros do usuario
    MongoDB-->>BookRepository: Lista filtrada por userId
    BookController-->>Frontend: 200 OK + livros do usuario
    Frontend-->>Usuario: Atualiza tabela de livros
```

Como explicar: todo livro e associado ao usuario autenticado. A listagem consulta por `userId`, o que implementa a restricao de acesso por usuario.

## UC-06 E UC-07 - Atualizar E Excluir Livros

Requisitos: RF-06, RF-07, RF-08

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend as React Frontend
    participant BookController
    participant CurrentUserService
    participant BookService
    participant BookRepository
    participant MongoDB

    Usuario->>Frontend: Edita livro proprio
    Frontend->>BookController: PUT /api/books/{id} com X-XSRF-TOKEN
    BookController->>CurrentUserService: Obter usuario autenticado
    CurrentUserService-->>BookController: userId atual
    BookController->>BookService: update(id, userId, dados)
    BookService->>BookRepository: findById(id)
    BookRepository->>MongoDB: Consulta livro
    MongoDB-->>BookRepository: Livro encontrado
    BookService->>BookService: Verifica se book.userId == userId
    BookService->>BookRepository: save(livro atualizado)
    BookRepository->>MongoDB: Atualiza documento
    BookController-->>Frontend: 200 OK

    Usuario->>Frontend: Confirma exclusao
    Frontend->>BookController: DELETE /api/books/{id} com X-XSRF-TOKEN
    BookController->>CurrentUserService: Obter usuario autenticado
    CurrentUserService-->>BookController: userId atual
    BookController->>BookService: delete(id, userId)
    BookService->>BookRepository: findById(id)
    BookRepository->>MongoDB: Consulta livro
    MongoDB-->>BookRepository: Livro encontrado
    BookService->>BookService: Verifica propriedade do livro
    BookService->>BookRepository: delete(livro)
    BookRepository->>MongoDB: Remove documento
    BookController-->>Frontend: 204 No Content
```

Como explicar: antes de atualizar ou excluir, o backend confirma que o livro pertence ao usuario autenticado. Essa regra evita acesso indevido entre usuarios.

## UC-08 - Pre-Visualizar Metadados Por ISBN

Requisitos: RF-09, RF-04, RF-08

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend as React Frontend
    participant BookImportController
    participant CurrentUserService
    participant OpenLibraryService
    participant OpenLibraryClient
    participant OpenLibraryAPI as Open Library API

    Usuario->>Frontend: Informa ISBN
    Frontend->>BookImportController: GET /api/books/import/isbn/{isbn}
    BookImportController->>CurrentUserService: Validar sessao autenticada
    CurrentUserService-->>BookImportController: Usuario autenticado
    BookImportController->>OpenLibraryService: previewByIsbn(isbn)
    OpenLibraryService->>OpenLibraryClient: fetchByIsbn(isbn)
    OpenLibraryClient->>OpenLibraryAPI: GET /isbn/{isbn}.json
    OpenLibraryAPI-->>OpenLibraryClient: Metadados do livro
    OpenLibraryClient-->>OpenLibraryService: DTO externo normalizado
    OpenLibraryService-->>BookImportController: Preview sem persistir
    BookImportController-->>Frontend: 200 OK + preview
    Frontend-->>Usuario: Exibe dados para revisao
```

Como explicar: a Open Library e usada apenas para pre-visualizar dados. O livro nao e salvo automaticamente; o usuario revisa e decide criar pelo fluxo normal de cadastro.

## VCR/WireMock No Teste Da Open Library

Requisitos: RF-09, RT-02

```mermaid
sequenceDiagram
    participant Teste as OpenLibraryClientTest
    participant WireMock
    participant OpenLibraryClient
    participant Fixture as JSON versionado

    Teste->>WireMock: Sobe servidor local com mappings
    WireMock->>Fixture: Carrega resposta declarativa
    Teste->>OpenLibraryClient: fetchByIsbn(isbn)
    OpenLibraryClient->>WireMock: GET /isbn/{isbn}.json
    WireMock-->>OpenLibraryClient: Resposta HTTP em replay
    OpenLibraryClient-->>Teste: Metadados normalizados
    Teste->>Teste: Valida titulo, autores, editora e tratamento de erro
```

Como explicar: no CI normal, o teste nao chama a internet. WireMock representa o VCR em Java, reproduzindo uma resposta HTTP controlada e versionada.

## Pipeline De Qualidade

Requisitos: RNF-05, RNF-06, RNF-07

```mermaid
sequenceDiagram
    actor Desenvolvedor
    participant GitHub
    participant Actions as GitHub Actions
    participant Maven
    participant Testcontainers
    participant JaCoCo
    participant NPM
    participant SonarQube

    Desenvolvedor->>GitHub: Push ou pull request
    GitHub->>Actions: Dispara workflow quality
    Actions->>Maven: mvn -B verify
    Maven->>Testcontainers: Sobe MongoDB para testes de integracao
    Testcontainers-->>Maven: Banco pronto para testes
    Maven->>JaCoCo: Gera relatorio de cobertura
    Actions->>NPM: npm ci, npm run lint, npm run build
    Actions->>SonarQube: Envia analise se URL/token estiverem configurados
    SonarQube-->>Actions: Quality Gate
    Actions-->>GitHub: Status da verificacao
```

Como explicar: a pipeline automatiza verificacao de backend, frontend, cobertura e qualidade. Quando o SonarQube esta local, as evidencias ficam registradas nos prints; quando estiver acessivel ao runner, a mesma pipeline envia a analise.

