# RTM - Matriz De Rastreabilidade

## Objetivo

A RTM conecta requisito, caso de uso, caso de teste e evidencia. O objetivo e manter **100% de rastreabilidade funcional**, garantindo que todo requisito funcional tenha validacao planejada e evidencia associada.

## Matriz

| Requisito | Caso de uso | Casos de teste | Evidencia esperada | Conceito aplicado |
|---|---|---|---|---|
| RF-01 | UC-01 | TC-001, TC-002, TC-003 | Evidencia de cadastro de usuario valido, email duplicado e senha invalida | Validacao, caso positivo, caso negativo |
| RF-02 | UC-02 | TC-004, TC-005, TC-006 | Evidencia de login valido, credenciais invalidas e campos obrigatorios | Caixa preta/autenticacao |
| RF-03 | UC-02, UC-03 | TC-007, TC-008 | Evidencia de criacao de sessao, manutencao de acesso autenticado e logout | Gerenciamento de sessao |
| RF-04 | UC-04 | TC-009, TC-010, TC-011 | Evidencia de criacao de livro com MongoDB Testcontainers, validacao de dados e persistencia real | Integracao/persistencia real |
| RF-05 | UC-05 | TC-012, TC-013 | Evidencia de listagem de livros do usuario e estado vazio | Caixa preta, comportamento observavel |
| RF-06 | UC-06 | TC-014, TC-015, TC-016 | Evidencia de atualizacao de livro proprio, livro inexistente e regra de autorizacao | Regressao/regra de negocio |
| RF-07 | UC-07 | TC-017, TC-018 | Evidencia de exclusao confirmada e cancelamento da exclusao | Fluxo alternativo |
| RF-08 | UC-04, UC-05, UC-06, UC-07 | TC-012, TC-019, TC-020, TC-021 | Evidencia de listagem isolada por usuario, acesso negado, isolamento entre usuarios e bloqueio de operacoes indevidas | Seguranca/nao conformidade evitada |
| RF-09 | UC-08 | TC-022, TC-023, TC-024, TC-025 | Evidencia de busca por ISBN com Open Library, replay VCR, ISBN nao encontrado e falha externa controlada | Integracao HTTP externa/VCR |

## Como Manter A RTM

1. Criar ou alterar o requisito funcional.
2. Atualizar o caso de uso correspondente.
3. Criar pelo menos um teste positivo e um teste negativo quando aplicavel.
4. Executar os testes e registrar a evidencia gerada.
5. Atualizar a matriz com requisito, caso de uso, caso de teste, evidencia e conceito aplicado.

## Aplicacao na prova oral

A RTM e a prova de que cada requisito tem relacao direta com casos de uso, testes e evidencias. Na prova oral, ela deve ser explicada como o mapa que mostra que nenhum requisito funcional ficou sem validacao.
