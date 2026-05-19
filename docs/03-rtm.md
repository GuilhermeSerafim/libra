# RTM - Matriz De Rastreabilidade

## Objetivo

A RTM conecta requisito, caso de uso, caso de teste e evidencia. O objetivo e manter **100% de rastreabilidade funcional**, garantindo que todo requisito funcional tenha validacao planejada e evidencia associada.

## Matriz

| Requisito | Caso de uso | Casos de teste | Evidencia esperada | Conceito aplicado |
|---|---|---|---|---|
| RF-01 | UC-01 | TC-001, TC-002, TC-003 | Testes `AuthControllerTest`, fluxo frontend de cadastro/login e diagrama UC-01 em `docs/11-diagramas-sequencia.md` | Validacao, caso positivo, caso negativo |
| RF-02 | UC-02 | TC-004, TC-005, TC-006 | Testes `AuthControllerTest`, evidencias `docs/evidencias/frontend/login*.png` e diagrama UC-02 | Caixa preta/autenticacao |
| RF-03 | UC-02, UC-03 | TC-007, TC-008 | Testes `AuthControllerTest`, fluxo de sessao/logout no frontend e diagrama login/logout | Gerenciamento de sessao, CSRF |
| RF-04 | UC-04 | TC-009, TC-010, TC-011 | Testes `BookControllerTest`, `BookRepositoryTest`, MongoDB Testcontainers e evidencias `books-created.png` | Integracao/persistencia real |
| RF-05 | UC-05 | TC-012, TC-013 | Testes `BookControllerTest`, evidencias `dashboard-empty.png` e `books-created.png` | Caixa preta, comportamento observavel |
| RF-06 | UC-06 | TC-014, TC-015, TC-016 | Testes `BookControllerTest`, regra em `BookService` e evidencia `books-edited.png` | Regressao/regra de negocio |
| RF-07 | UC-07 | TC-017, TC-018 | Testes `BookControllerTest` e evidencia `book-deleted.png` | Fluxo alternativo |
| RF-08 | UC-04, UC-05, UC-06, UC-07 | TC-012, TC-019, TC-020, TC-021 | Testes de autorizacao em `BookControllerTest`, filtro por `userId` e diagramas CRUD | Seguranca/nao conformidade evitada |
| RF-09 | UC-08 | TC-022, TC-023, TC-024, TC-025 | Testes `OpenLibraryClientTest` e `BookImportControllerTest`, fixtures WireMock e evidencias `import-preview.png`, `import-created.png` | Integracao HTTP externa/VCR |

## Evidencias Transversais

| Evidencia | Local |
| --- | --- |
| Pipeline GitHub Actions | Aba Actions do repositorio, workflow `quality`. |
| Cobertura JaCoCo | `target/site/jacoco/index.html` apos executar `mvn verify`; evidenciada tambem no SonarQube. |
| SonarQube | `docs/evidencias/sonarqube/`, com Quality Gate Passed e cobertura acima de 80%. |
| Frontend manual | `docs/evidencias/frontend/`, com prints de cadastro/login, CRUD, importacao e tela de qualidade. |
| VCR/WireMock | `src/test/resources/wiremock/openlibrary/`. |
| Testcontainers | `AbstractMongoIntegrationTest` e logs de execucao dos testes de integracao. |
| Diagramas UML | `docs/11-diagramas-sequencia.md`. |

## Como Manter A RTM

1. Criar ou alterar o requisito funcional.
2. Atualizar o caso de uso correspondente.
3. Criar pelo menos um teste positivo e um teste negativo quando aplicavel.
4. Executar os testes e registrar a evidencia gerada.
5. Atualizar a matriz com requisito, caso de uso, caso de teste, evidencia e conceito aplicado.

## Aplicacao na prova oral

A RTM e a prova de que cada requisito tem relacao direta com casos de uso, testes e evidencias. Na prova oral, ela deve ser explicada como o mapa que mostra que nenhum requisito funcional ficou sem validacao.
