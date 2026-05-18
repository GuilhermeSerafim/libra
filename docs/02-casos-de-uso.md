# Casos De Uso

## Convencao

Os casos de uso usam IDs no formato **UC-XX**, em que XX e um numero sequencial. Cada caso de uso aponta para um ou mais requisitos funcionais (**RFs**) definidos em `docs/01-requisitos.md`.

Os fluxos principais representam o comportamento esperado do sistema. Fluxos alternativos, validacoes e excecoes indicam variacoes relevantes do uso e tambem servem como candidatos para casos de teste negativos.

## UC-01 Cadastrar Usuario

| Campo | Descricao |
|---|---|
| Requisito | RF-01 |
| Ator | Visitante |
| Pre-condicao | O visitante acessa a tela de cadastro. |
| Pos-condicao | O usuario e cadastrado e pode autenticar no sistema. |

**Fluxo principal**

1. O visitante acessa a opcao de cadastro.
2. O sistema exibe o formulario de cadastro.
3. O visitante informa nome, email e senha valida.
4. O sistema valida os dados informados.
5. O sistema verifica que o email ainda nao esta cadastrado.
6. O sistema registra o usuario no MongoDB.
7. O sistema informa que o cadastro foi concluido.

**Fluxos alternativos**

- Email ja cadastrado: o sistema recusa o cadastro e informa que o email ja esta em uso.
- Senha invalida: o sistema recusa o cadastro e informa a regra de senha nao atendida.

**Excecoes**

- MongoDB indisponivel: o sistema nao conclui o cadastro e apresenta mensagem de erro controlada.

## UC-02 Autenticar Usuario

| Campo | Descricao |
|---|---|
| Requisito | RF-02, RF-03 |
| Ator | Usuario cadastrado |
| Pre-condicao | O usuario possui cadastro ativo. |
| Pos-condicao | O usuario autenticado recebe uma sessao valida. |

**Fluxo principal**

1. O usuario acessa a tela de login.
2. O sistema exibe os campos de email e senha.
3. O usuario informa credenciais validas.
4. O sistema valida o preenchimento dos campos.
5. O sistema confere as credenciais cadastradas.
6. O sistema cria ou atualiza a sessao autenticada.
7. O sistema direciona o usuario para sua biblioteca pessoal.

**Fluxos alternativos**

- Credenciais invalidas: o sistema recusa a autenticacao e informa erro de login.
- Campos vazios: o sistema bloqueia o envio e solicita o preenchimento obrigatorio.

**Excecoes**

- Falha de persistencia: o sistema nao cria a sessao e apresenta mensagem de erro controlada.

## UC-03 Encerrar Sessao

| Campo | Descricao |
|---|---|
| Requisito | RF-03 |
| Ator | Usuario autenticado |
| Pre-condicao | O usuario possui sessao ativa. |
| Pos-condicao | A sessao e encerrada e o usuario deixa de acessar areas autenticadas. |

**Fluxo principal**

1. O usuario autenticado aciona a opcao de logout.
2. O sistema invalida a sessao ativa.
3. O sistema remove o acesso autenticado.
4. O sistema redireciona o usuario para a tela publica ou de login.

**Fluxos alternativos**

- Logout cancelado: o sistema mantem a sessao ativa e retorna para a area autenticada.
- Sessao ja expirada: o sistema redireciona o usuario para a tela publica ou de login.

**Excecoes**

- Armazenamento de sessao indisponivel: o sistema nao confirma o encerramento e apresenta mensagem de erro controlada.

## UC-04 Cadastrar Livro

| Campo | Descricao |
|---|---|
| Requisito | RF-04, RF-08 |
| Ator | Usuario autenticado |
| Pre-condicao | O usuario esta autenticado. |
| Pos-condicao | O livro e cadastrado e associado ao usuario autenticado. |

**Fluxo principal**

1. O usuario acessa a opcao de cadastrar livro.
2. O sistema exibe o formulario de livro.
3. O usuario informa titulo e demais dados do livro.
4. O sistema valida o titulo e os dados obrigatorios.
5. O sistema associa o livro ao usuario autenticado.
6. O sistema persiste o livro no MongoDB.
7. O sistema informa que o livro foi cadastrado.

**Fluxos alternativos**

- Titulo vazio: o sistema bloqueia o cadastro e solicita um titulo valido.
- Sessao expirada: o sistema bloqueia o cadastro e solicita nova autenticacao.

**Excecoes**

- Falha de persistencia no MongoDB: o sistema nao conclui o cadastro do livro e apresenta mensagem de erro controlada.

## UC-05 Consultar Livros

| Campo | Descricao |
|---|---|
| Requisito | RF-05, RF-08 |
| Ator | Usuario autenticado |
| Pre-condicao | O usuario esta autenticado. |
| Pos-condicao | O usuario visualiza apenas os livros de sua biblioteca pessoal. |

**Fluxo principal**

1. O usuario acessa a listagem de livros.
2. O sistema valida a sessao ativa.
3. O sistema consulta os livros associados ao usuario autenticado.
4. O sistema exibe a lista de livros encontrados.

**Fluxos alternativos**

- Biblioteca vazia: o sistema exibe estado vazio sem erro.
- Sessao invalida: o sistema bloqueia a consulta e solicita autenticacao.

**Excecoes**

- Falha de consulta ou banco de dados: o sistema nao exibe a listagem e apresenta mensagem de erro controlada.

## UC-06 Atualizar Livro

| Campo | Descricao |
|---|---|
| Requisito | RF-06, RF-08 |
| Ator | Usuario autenticado |
| Pre-condicao | O usuario esta autenticado e possui livro cadastrado. |
| Pos-condicao | O livro proprio e atualizado com os novos dados validos. |

**Fluxo principal**

1. O usuario acessa a opcao de editar um livro de sua biblioteca.
2. O sistema valida a sessao ativa.
3. O sistema verifica que o livro pertence ao usuario autenticado.
4. O usuario altera os dados do livro.
5. O sistema valida os dados informados.
6. O sistema persiste a atualizacao no MongoDB.
7. O sistema informa que o livro foi atualizado.

**Fluxos alternativos**

- Livro inexistente: o sistema informa que o livro nao foi encontrado.
- Livro de outro usuario: o sistema nega a atualizacao e nao altera o registro.

**Excecoes**

- Conflito de persistencia ou falha de banco de dados: o sistema nao conclui a atualizacao e apresenta mensagem de erro controlada.

## UC-07 Excluir Livro

| Campo | Descricao |
|---|---|
| Requisito | RF-07, RF-08 |
| Ator | Usuario autenticado |
| Pre-condicao | O usuario esta autenticado e possui livro cadastrado. |
| Pos-condicao | O livro proprio e excluido ou permanece inalterado se a acao for cancelada. |

**Fluxo principal**

1. O usuario acessa a opcao de excluir um livro de sua biblioteca.
2. O sistema valida a sessao ativa.
3. O sistema verifica que o livro pertence ao usuario autenticado.
4. O sistema solicita confirmacao da exclusao.
5. O usuario confirma a exclusao.
6. O sistema remove o livro do MongoDB.
7. O sistema informa que o livro foi excluido.

**Fluxos alternativos**

- Exclusao cancelada: o sistema mantem o livro cadastrado e retorna para a listagem.
- Livro de outro usuario: o sistema nega a exclusao e nao remove o registro.

**Excecoes**

- Falha de exclusao ou banco de dados: o sistema nao remove o livro e apresenta mensagem de erro controlada.

## UC-08 Pre-visualizar Metadados Por ISBN

| Campo | Descricao |
|---|---|
| Requisito | RF-09, RF-04, RF-08 |
| Ator | Usuario autenticado |
| Pre-condicao | O usuario esta autenticado e informa um ISBN valido. |
| Pos-condicao | Os metadados retornados pela Open Library sao exibidos para revisao, sem salvar automaticamente um livro na biblioteca do usuario. |

**Fluxo principal**

1. O usuario acessa a opcao de buscar metadados por ISBN.
2. O sistema valida a sessao ativa.
3. O usuario informa o ISBN.
4. O sistema consulta a Open Library API por meio de um cliente HTTP isolado.
5. O sistema exibe titulo, autores, editora, data, capa ou outros metadados disponiveis.
6. O usuario revisa ou complementa os dados antes de decidir cadastrar.
7. Se decidir salvar, o usuario confirma os dados pelo fluxo normal de cadastro de livro (UC-04).
8. O sistema nao persiste nenhum livro automaticamente durante a pre-visualizacao por ISBN.

**Fluxos alternativos**

- ISBN invalido: o sistema bloqueia a consulta e informa o formato esperado.
- ISBN nao encontrado: o sistema informa que nao localizou metadados e permite cadastro manual.
- Usuario cancela a pre-visualizacao: o sistema nao salva o livro.

**Excecoes**

- API externa indisponivel: o sistema apresenta erro controlado, nao chama a rede real nos testes automatizados e permite cadastro manual.

## Aplicacao na prova oral

Casos de uso transformam requisitos em fluxos testaveis. Para explicar na prova oral, cada caso deve citar ator, pre-condicao, fluxo principal, excecoes e pos-condicao. Esses fluxos viram casos de teste positivos e negativos e depois aparecem ligados aos requisitos na RTM. O UC-08 e o exemplo de dependencia externa: a aplicacao consome Open Library em producao, mas os testes automatizados usam VCR para reproduzir respostas gravadas.
