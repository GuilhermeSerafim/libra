# Casos De Teste

## Convencao

Os casos de teste usam IDs no formato **TC-XXX**, em que XXX e um numero sequencial. Cada caso aponta para um requisito funcional (**RF**) e um caso de uso (**UC**) definidos nos documentos do projeto.

O campo tipo indica a tecnica principal aplicada ao caso, sem impedir que o mesmo teste tambem contribua para regressao, cobertura ou evidencia da RTM.

## Catalogo

| ID | Tipo | Requisito | Caso de uso | Objetivo | Resultado esperado |
|---|---|---|---|---|---|
| TC-001 | Positivo | RF-01 | UC-01 | Validar cadastro de usuario com nome, email e senha validos. | Usuario cadastrado com sucesso e disponivel para autenticacao. |
| TC-002 | Negativo | RF-01 | UC-01 | Validar bloqueio de cadastro com email duplicado. | Sistema recusa o cadastro e informa que o email ja esta em uso. |
| TC-003 | Negativo | RF-01 | UC-01 | Validar combinacoes invalidas de nome, email e senha. | Sistema rejeita cada combinacao invalida e apresenta validacao adequada. |
| TC-004 | Positivo | RF-02 | UC-02 | Validar login com credenciais validas. | Usuario autenticado e direcionado para sua biblioteca pessoal. |
| TC-005 | Negativo | RF-02 | UC-02 | Validar login com senha incorreta. | Sistema recusa autenticacao e informa erro de login. |
| TC-006 | Negativo | RF-02 | UC-02 | Validar campos vazios e email malformado no login. | Sistema bloqueia envio ou recusa autenticacao com mensagem de validacao. |
| TC-007 | Positivo | RF-03 | UC-02 | Validar acesso a area protegida com sessao autenticada. | Area protegida e exibida apenas para usuario autenticado. |
| TC-008 | Positivo | RF-03 | UC-03 | Validar logout de usuario autenticado. | Sessao encerrada e acesso autenticado removido. |
| TC-009 | Positivo | RF-04 | UC-04 | Validar cadastro de livro com dados validos. | Livro cadastrado e associado ao usuario autenticado. |
| TC-010 | Negativo | RF-04 | UC-04 | Validar cadastro de livro sem titulo. | Sistema bloqueia cadastro e solicita titulo valido. |
| TC-011 | Integracao | RF-04 | UC-04 | Validar persistencia de livro usando MongoDB Testcontainers. | Livro persistido e recuperavel no MongoDB iniciado pelo teste. |
| TC-012 | Positivo | RF-05, RF-08 | UC-05 | Validar que a listagem mostra apenas livros pertencentes ao usuario autenticado. | Sistema exibe apenas os livros do usuario autenticado e nao expoe livros de outro usuario. |
| TC-013 | Caixa preta | RF-05 | UC-05 | Validar comportamento quando a biblioteca esta vazia. | Sistema exibe estado vazio sem erro. |
| TC-014 | Positivo | RF-06 | UC-06 | Validar atualizacao de livro proprio com dados validos. | Livro atualizado e alteracoes persistidas. |
| TC-015 | Negativo | RF-06 | UC-06 | Validar atualizacao de livro inexistente. | Sistema informa que o livro nao foi encontrado e nao altera registros. |
| TC-016 | Caixa branca | RF-06 | UC-06 | Validar regra interna de autorizacao para atualizar livro. | Sistema permite atualizar apenas livro pertencente ao usuario autenticado. |
| TC-017 | Positivo | RF-07 | UC-07 | Validar exclusao confirmada de livro proprio. | Livro removido da biblioteca do usuario. |
| TC-018 | Alternativo | RF-07 | UC-07 | Validar cancelamento da confirmacao de exclusao. | Livro permanece cadastrado e usuario retorna para a listagem. |
| TC-019 | Negativo | RF-08 | UC-04 | Validar tentativa de criar livro sem sessao autenticada. | Sistema bloqueia criacao e solicita autenticacao. |
| TC-020 | Negativo | RF-08 | UC-06 | Validar tentativa de editar livro de outro usuario. | Sistema nega edicao e nao altera o livro. |
| TC-021 | Negativo | RF-08 | UC-07 | Validar tentativa de excluir livro de outro usuario. | Sistema nega exclusao e mantem o livro cadastrado. |
| TC-022 | VCR/WireMock | RF-09 | UC-08 | Validar busca de metadados por ISBN com resposta gravada ou declarada da Open Library. | Sistema preenche dados do livro a partir do replay WireMock sem chamada real de internet. |
| TC-023 | Negativo | RF-09 | UC-08 | Validar ISBN inexistente ou sem retorno na Open Library. | Sistema informa que nao encontrou metadados e permite cadastro manual. |
| TC-024 | Negativo | RF-09 | UC-08 | Validar falha externa controlada na busca por ISBN. | Sistema apresenta mensagem controlada e nao salva livro incompleto automaticamente. |
| TC-025 | Integracao | RF-09, RF-08 | UC-08 | Validar que a pre-visualizacao por ISBN nao persiste livro automaticamente. | Sistema retorna metadados da Open Library e a base MongoDB permanece sem novo livro ate o usuario executar o cadastro normal. |

## Aplicacao Das Tecnicas

| Tecnica | Como sera aplicada | Exemplos |
|---|---|---|
| Caixa branca | Validacao de caminhos internos e regras conhecidas pela equipe. | TC-016 verifica a regra de autorizacao antes da atualizacao. |
| Caixa preta | Validacao do comportamento observavel por entrada e saida. | TC-004, TC-005 e TC-013 validam respostas do sistema sem depender da implementacao interna. |
| Cenarios negativos | Cobertura de entradas invalidas por casos dedicados e fixtures de teste. | TC-003 cobre combinacoes invalidas de cadastro e TC-006 cobre entradas invalidas de login. |
| Integracao | Validacao entre aplicacao, camadas e banco de dados real em container. | TC-011 valida persistencia com MongoDB Testcontainers. |
| VCR/WireMock | Reproducao de respostas HTTP externas gravadas ou declaradas em arquivos seguros. | TC-022 valida Open Library sem rede real no CI. |
| Regressao | Reexecucao dos cenarios principais apos alteracoes. | TC-001, TC-004, TC-009, TC-014 e TC-017 compoem fluxo critico de regressao. |

## Aplicacao na prova oral

Na prova oral, os casos de teste devem ser explicados como a transformacao dos requisitos e casos de uso em validacoes objetivas. A equipe pode citar caixa preta para validar comportamento visivel, caixa branca para regras internas como autorizacao, cenarios negativos dedicados para entradas invalidas, integracao com Testcontainers para persistencia real, VCR em Java com WireMock para Open Library e regressao para garantir que cadastro, login, CRUD e pre-visualizacao por ISBN continuem funcionando apos mudancas.
