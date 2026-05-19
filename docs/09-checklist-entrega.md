# Checklist De Entrega

## Itens Obrigatorios

| Item | Como verificar |
| --- | --- |
| Repositorio GitHub | Projeto versionado, com historico de commits e estrutura compreensivel. |
| Codigo-fonte organizado | Pacotes, classes, nomes e responsabilidades coerentes com o dominio. |
| README | Descricao do projeto, tecnologias, como executar, como testar e principais evidencias. |
| RTM | Matriz ligando requisitos, casos de uso, casos de teste, evidencias e conceitos aplicados. |
| Diagramas UML de sequencia | Diagramas de sequencia para os fluxos principais do sistema. |
| Cobertura minima de 80% | Relatorio JaCoCo mostrando cobertura minima exigida. |
| GitHub Actions | Pipeline configurada e executando testes do backend, cobertura, lint e build do frontend. |
| SonarQube | Analise estatica com Quality Gate Passed, 0 bugs, 0 vulnerabilidades, 0 code smells e evidencias em `docs/evidencias/sonarqube/`. |
| Testcontainers | Testes de integracao usando ambiente realista, como banco em container. |
| VCR/WireMock para Open Library API | Testes da busca por ISBN executando em replay, sem depender de internet real no CI. |
| Atualizacao semanal VCR | Rotina semanal configurada para atualizar fixture/resposta VCR da Open Library e evitar cassete eterno. |

## Checklist Antes Da Apresentacao

- Rodar todos os testes localmente.
- Rodar `npm run lint` e `npm run build` no frontend.
- Conferir a ultima execucao da pipeline no GitHub Actions.
- Abrir o relatorio de cobertura JaCoCo e confirmar a meta de 80%.
- Revisar o dashboard do SonarQube e os prints em `docs/evidencias/sonarqube/`.
- Conferir README com comandos `mvn spring-boot:run`, `mvn verify`, atualizacao VCR, JaCoCo, SonarQube e GitHub Actions.
- Conferir se a RTM esta atualizada com requisitos, testes e evidencias.
- Conferir evidencia da rotina semanal de atualizacao VCR.
- Revisar o guia da prova oral e treinar respostas curtas.
- Separar evidencias dos principais fluxos do sistema, como cadastro, listagem, edicao, exclusao, pre-visualizacao por ISBN e validacoes.

## Aplicacao na prova oral

A entrega nao e apenas codigo funcionando. A defesa deve demonstrar codigo, documentacao, rastreabilidade, testes, cobertura, integracao continua e qualidade automatizada.

Uma resposta forte e explicar que o projeto foi construido com controle de versao, requisitos rastreados pela RTM, casos de teste planejados, testes automatizados, cobertura medida pelo JaCoCo, pipeline no GitHub Actions e analise de qualidade pelo Sonar. Para a integracao Open Library, a equipe deve mostrar VCR em Java com WireMock como evidencia de teste reproduzivel de API externa. Assim, a qualidade foi acompanhada desde os requisitos ate a entrega.
