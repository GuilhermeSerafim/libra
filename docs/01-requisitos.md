# Requisitos Do Projeto

## Requisitos Funcionais

| ID | Requisito | Descricao |
|---|---|---|
| RF-01 | Cadastro de usuarios | O sistema deve permitir o cadastro de usuarios. |
| RF-02 | Autenticacao | O sistema deve permitir que usuarios cadastrados realizem autenticacao. |
| RF-03 | Sessao | O sistema deve gerenciar a sessao do usuario autenticado. |
| RF-04 | Cadastro de livros | O sistema deve permitir que o usuario autenticado cadastre livros em sua biblioteca pessoal. |
| RF-05 | Consulta/listagem | O sistema deve permitir que o usuario autenticado consulte e liste seus livros cadastrados. |
| RF-06 | Atualizacao | O sistema deve permitir que o usuario autenticado atualize os dados de seus livros. |
| RF-07 | Exclusao | O sistema deve permitir que o usuario autenticado exclua livros de sua biblioteca pessoal. |
| RF-08 | Restricao por usuario autenticado | O sistema deve restringir as operacoes aos dados do usuario autenticado. |
| RF-09 | Importacao por ISBN | O sistema deve permitir que o usuario autenticado busque metadados de livro por ISBN usando a Open Library API antes de salvar o livro. |

## Requisitos Nao Funcionais

| ID | Requisito | Descricao |
|---|---|---|
| RNF-01 | Spring Boot Java | O sistema deve ser desenvolvido com Spring Boot Java. |
| RNF-02 | MVC | O sistema deve seguir a arquitetura MVC. |
| RNF-03 | MongoDB | O sistema deve usar MongoDB como banco de dados. |
| RNF-04 | UI funcional/responsiva | O sistema deve oferecer uma interface web funcional e responsiva. |
| RNF-05 | Cobertura minima 80% | O projeto deve manter cobertura minima de 80%. |
| RNF-06 | GitHub Actions | O projeto deve executar validacoes automatizadas em GitHub Actions. |
| RNF-07 | SonarQube | O projeto deve usar SonarQube para analise de qualidade. |
| RNF-08 | Proteger secrets | O projeto deve proteger secrets e evitar credenciais versionadas. |

## Restricoes Tecnicas

| ID | Restricao | Descricao |
|---|---|---|
| RT-01 | Testcontainers | Testes de persistencia devem usar Testcontainers. |
| RT-02 | VCR em Java com WireMock | Integracoes com APIs externas devem ser testadas com VCR implementado com WireMock, sem depender da internet real no CI. |
| RT-03 | Proibicao de mocks tradicionais no projeto final | O projeto final nao deve usar mocks tradicionais como estrategia de validacao; deve usar Testcontainers para persistencia e VCR/WireMock para API externa. |
| RT-04 | RTM com 100% de rastreabilidade funcional | A RTM deve cobrir 100% dos requisitos funcionais. |

## Aplicacao na prova oral

Na prova oral, a separacao deve ser explicada assim: requisitos funcionais descrevem o que o sistema faz, requisitos nao funcionais descrevem qualidades e criterios de operacao, e restricoes tecnicas definem obrigacoes de implementacao e validacao. O RF-04, cadastro de livros, deve aparecer rastreado na RTM ate seu caso de uso, seus testes, suas evidencias de cobertura e sua validacao no pipeline. O RF-09 demonstra integracao externa controlada: Open Library melhora o cadastro por ISBN e VCR em Java com WireMock garante teste reproduzivel sem chamada real no CI.
