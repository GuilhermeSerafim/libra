# Design: Backend REST API Do Gerenciador De Biblioteca Pessoal

## Objetivo

Implementar primeiro o backend do projeto oficial **Gerenciador de Biblioteca Pessoal** como uma API REST em Spring Boot. O frontend moderno sera criado depois, consumindo esta API por JSON.

Esta decisao preserva o foco inicial em regras de negocio, persistencia, testes, cobertura, SonarQube, CI e rastreabilidade, sem acoplar o backend a uma tecnologia de interface.

## Decisao Aprovada

- Backend primeiro.
- Spring Boot REST API.
- Frontend separado e moderno em etapa posterior.
- Sem Thymeleaf nesta fase.
- MVC aplicado no backend como separacao `Controller -> Service -> Repository`.

## Escopo Desta Fase

Inclui:

- Cadastro de usuario.
- Login, logout e consulta do usuario autenticado.
- Gerenciamento de sessao.
- CRUD de livros do usuario autenticado.
- Restricao para cada usuario acessar apenas seus proprios livros.
- Importacao de metadados por ISBN usando Open Library API.
- VCR em Java com WireMock para a Open Library.
- MongoDB com Testcontainers nos testes de persistencia.
- JaCoCo com meta minima de 80%.
- SonarQube.
- GitHub Actions.
- README com execucao, testes e evidencias.

Fica fora desta fase:

- Frontend moderno.
- Tela responsiva.
- Deploy em producao.
- JWT.
- Recomendacoes de livros.
- Emprestimos, reservas, multas ou funcionalidades de biblioteca publica.

## Arquitetura

O backend sera uma aplicacao Spring Boot organizada por camadas.

```text
Controller -> Service -> Repository
              |
              v
            Client externo
```

Responsabilidades:

| Camada | Responsabilidade |
| --- | --- |
| Controller | Expor endpoints REST, validar entrada basica e retornar DTOs. |
| Service | Aplicar regras de negocio, autorizacao por usuario e orquestracao. |
| Repository | Persistir e consultar documentos no MongoDB. |
| Client | Isolar chamada HTTP para Open Library. |
| DTO | Separar contrato da API do modelo persistido. |
| Config | Configurar seguranca, sessao, MongoDB, HTTP client, CORS futuro e tratamento de erros. |

## Estrutura De Pacotes

```text
src/main/java/.../biblioteca/
  config/
  controller/
  dto/
    request/
    response/
  exception/
  model/
  repository/
  service/
  client/
```

## Endpoints REST

### Autenticacao

| Metodo | Rota | Objetivo |
| --- | --- | --- |
| POST | `/api/auth/register` | Cadastrar usuario. |
| POST | `/api/auth/login` | Autenticar usuario e criar sessao. |
| POST | `/api/auth/logout` | Encerrar sessao. |
| GET | `/api/auth/me` | Retornar usuario autenticado atual. |

### Livros

| Metodo | Rota | Objetivo |
| --- | --- | --- |
| GET | `/api/books` | Listar livros do usuario autenticado. |
| POST | `/api/books` | Criar livro manualmente. |
| GET | `/api/books/{id}` | Consultar livro proprio por ID. |
| PUT | `/api/books/{id}` | Atualizar livro proprio. |
| DELETE | `/api/books/{id}` | Excluir livro proprio. |

### Importacao Por ISBN

| Metodo | Rota | Objetivo |
| --- | --- | --- |
| GET | `/api/books/import/isbn/{isbn}` | Buscar metadados na Open Library e retornar uma pre-visualizacao. |

A importacao por ISBN nao salva automaticamente. Ela retorna metadados para o frontend revisar e depois chamar `POST /api/books`.

## Autenticacao E Sessao

A autenticacao deve usar Spring Security com sessao/cookie, porque o enunciado cobra gerenciamento de sessao.

Decisoes:

- Senhas armazenadas com hash seguro, como BCrypt.
- Login cria sessao server-side.
- Logout invalida a sessao.
- Endpoints de livros exigem usuario autenticado.
- Toda consulta de livro filtra por `userId`.
- O frontend futuro consumira a API usando cookies de sessao.
- CORS sera configurado apenas quando o frontend separado existir.

JWT fica fora do escopo inicial porque adiciona complexidade e enfraquece a conexao com o requisito de sessao.

## Modelo De Dados

### User

Campos previstos:

- `id`
- `name`
- `email`
- `passwordHash`
- `createdAt`
- `updatedAt`

Regras:

- Email unico.
- Senha nunca retorna em response.
- Senha nunca fica em texto puro.

### Book

Campos previstos:

- `id`
- `userId`
- `title`
- `authors`
- `isbn`
- `publisher`
- `publishDate`
- `pageCount`
- `coverUrl`
- `status`
- `rating`
- `notes`
- `tags`
- `metadataSource`
- `createdAt`
- `updatedAt`

Regras:

- `title` obrigatorio.
- Livro sempre pertence a um usuario.
- Operacoes de leitura, edicao e exclusao sempre validam `userId`.
- Campos vindos da Open Library podem ser ajustados antes de salvar.

## Open Library API

A Open Library sera usada para buscar metadados de livro por ISBN.

Fluxo:

1. Usuario autenticado informa ISBN.
2. Backend chama `GET /api/books/import/isbn/{isbn}`.
3. `BookImportController` chama `OpenLibraryService`.
4. `OpenLibraryService` chama `OpenLibraryClient`.
5. `OpenLibraryClient` consulta Open Library.
6. Backend transforma a resposta externa em DTO interno.
7. Frontend futuro exibe a pre-visualizacao.
8. Usuario confirma e salva pelo endpoint `POST /api/books`.

Falhas previstas:

- ISBN invalido.
- ISBN nao encontrado.
- Open Library fora do ar.
- Timeout.
- Payload sem campos esperados.

Essas falhas devem retornar erro controlado, nao stack trace.

## VCR Em Java Com WireMock

WireMock sera usado como implementacao Java do VCR para a Open Library.

Fluxos:

| Fluxo | Quando roda | Internet | Objetivo |
| --- | --- | --- | --- |
| Replay normal | A cada `mvn verify`, push ou pull request. | Nao. | Testar nosso codigo contra resposta HTTP controlada. |
| Atualizacao semanal | Uma vez por semana, por rotina agendada ou manual. | Sim. | Atualizar stubs/mappings e evitar cassete eterno. |

Padrao recomendado:

- Usar stubs declarativos em JSON como evidencia principal.
- Usar stub programatico apenas para casos pontuais, como timeout ou erro especifico.
- Versionar mappings seguros.
- Revisar diff quando o replay for atualizado.
- Nao chamar Open Library real no CI normal.

## Testes

Camadas de teste:

| Tipo | Ferramenta | Objetivo |
| --- | --- | --- |
| Unitario | JUnit 5 | Validar regras puras de service e validadores. |
| Web/controller | Spring Boot Test / MockMvc | Validar status HTTP, contrato de DTO e autorizacao. |
| Persistencia | Testcontainers MongoDB | Validar repositories e queries reais. |
| Integracao externa | WireMock | Validar cliente Open Library sem internet no CI normal. |
| Regressao | Maven verify | Reexecutar suite completa a cada mudanca. |
| Cobertura | JaCoCo | Comprovar cobertura minima de 80%. |
| Qualidade estatica | SonarQube | Bugs, vulnerabilidades, duplicacao e code smells. |

Mocks tradicionais nao devem ser usados como estrategia de validacao final. Dependencias relevantes devem ser reais ou reproduziveis: MongoDB via Testcontainers e HTTP externo via VCR/WireMock.

## CI E Qualidade

O GitHub Actions deve ter pelo menos:

- Pipeline normal em push e pull request.
- Execucao de `mvn -B verify`.
- Relatorio JaCoCo.
- Analise SonarQube com `SONAR_TOKEN` e `SONAR_HOST_URL` em secrets.
- Rotina semanal para atualizacao controlada do VCR/WireMock.

## Frontend Futuro

O frontend moderno sera implementado depois como consumidor da API.

Implicacoes para o backend:

- Respostas devem ser JSON consistentes.
- Erros devem ter formato padronizado.
- Rotas devem ser estaveis.
- Sessao por cookie deve ser pensada para consumo cross-origin quando o frontend separado existir.
- O backend nao deve depender de templates server-side.

Na fase de UI, a decisao de tecnologia pode considerar React, Next.js, Vue ou outra alternativa moderna. Essa decisao nao precisa ser tomada para iniciar o backend.

## Aplicacao Na Prova Oral

Resposta curta sugerida:

"Comecamos pelo backend como API REST em Spring Boot para separar regras de negocio da interface. O MVC aparece nas camadas Controller, Service e Repository. A persistencia usa MongoDB e e testada com Testcontainers. A integracao externa com Open Library usa VCR em Java com WireMock, rodando em replay no CI normal e atualizando os stubs uma vez por semana. A qualidade e comprovada com testes, JaCoCo, SonarQube, GitHub Actions e RTM."

## Criterios De Aceite

- Projeto Spring Boot criado.
- Endpoints de auth e livros implementados.
- Sessao funcionando.
- Restricao por usuario aplicada em todas as operacoes de livro.
- Open Library integrada por ISBN.
- WireMock cobrindo fluxo externo em replay.
- Atualizacao semanal do VCR documentada/configurada.
- MongoDB testado com Testcontainers.
- JaCoCo com meta minima de 80%.
- SonarQube configurado.
- GitHub Actions executando pipeline.
- README explica execucao, testes e evidencias.

