# Referencias Das Aulas

Esta base usa os slides HTML locais como fonte principal e os materiais escritos online como complemento. Quando houver conflito, a descricao oficial do projeto prevalece.

**Documentos de destino:** os arquivos `.md` citados na coluna "Uso na base" sao saidas desta base de conhecimento e ficam em `docs/`.

**Encoding:** os nomes das fontes locais preservam a grafia real dos arquivos no workspace e foram verificados em UTF-8.

## Fontes Locais

| Fonte local | Uso na base | Tipo de conteudo aproveitado |
|---|---|---|
| `Descrição do Projeto - Qualidade de Software.html` | `00-visao-geral.md`, `01-requisitos.md`, `09-checklist-entrega.md`, `10-guia-prova-oral.md` | Requisitos oficiais, stack, cobertura minima, entrega, RTM, prazo |
| `Introdução à Qualidade de Software - Qualidade de Software.html` | `00-visao-geral.md`, `08-gestao-bugs-e-evidencias.md`, `10-guia-prova-oral.md` | Qualidade, avaliacao, custo da qualidade, V&V, SQA, modelo de qualidade |
| `Fundamentos de Testes_ Caixa Branca vs. Caixa Preta - Qualidade de Software.html` | `04-plano-de-testes.md`, `05-casos-de-teste.md`, `10-guia-prova-oral.md` | Caixa branca, caixa preta, testes unitarios, parametrizados, E2E, piramide |
| `Planejamento de Testes e Casos de Teste - Qualidade de Software.html` | `02-casos-de-uso.md`, `03-rtm.md`, `04-plano-de-testes.md`, `05-casos-de-teste.md` | Personas, casos de uso, casos de teste, RTM, criterios, riscos |
| `Gestão de Erros e Bugs - Qualidade de Software.html` | `08-gestao-bugs-e-evidencias.md`, `10-guia-prova-oral.md` | Erro, defeito, falha, severidade, prioridade, ciclo de vida do bug, evidencia |
| `Testes Automatizados em CI - Qualidade de Software.html` | `06-estrategia-automacao.md`, `07-ci-sonar-cobertura.md` | CI, GitHub Actions, piramide de testes em CI, boas praticas |
| `Jest no GitHub Actions + SonarCloud - Qualidade de Software.html` | `07-ci-sonar-cobertura.md`, `10-guia-prova-oral.md` | Quality gate, cobertura, exemplos Sonar/SonarCloud como complemento, checks de PR |
| `VCR, API Seguras e TestContainers - Qualidade de Software.html` | `06-estrategia-automacao.md`, `07-ci-sonar-cobertura.md`, `10-guia-prova-oral.md` | VCR em Java com WireMock, API segura, secrets, Testcontainers, pipeline com Docker |

## Materiais Escritos Online

| Material online | Uso na base | Papel |
|---|---|---|
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_projeto-descricao.html> | `00-visao-geral.md`, `09-checklist-entrega.md` | Complemento da descricao oficial |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula01-introducao-qualidade.html> | `00-visao-geral.md`, `10-guia-prova-oral.md` | Complemento conceitual de qualidade |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula02-planejamento.html> | `02-casos-de-uso.md`, `03-rtm.md`, `04-plano-de-testes.md`, `05-casos-de-teste.md` | Complemento de planejamento e test design |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula03-fundamentos-caixa-branca-preta.html> | `05-casos-de-teste.md`, `06-estrategia-automacao.md` | Complemento de tipos e tecnicas de teste |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula05-gestao-erros-bugs.html> | `08-gestao-bugs-e-evidencias.md` | Complemento de gestao de defeitos |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula06-introducao-github-actions.html> | `07-ci-sonar-cobertura.md` | Complemento de CI |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula07-testes-unidade-jest.html> | `07-ci-sonar-cobertura.md` | Complemento de cobertura e quality gate |
| <https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula10-vcr-api-seguras.html> | `06-estrategia-automacao.md`, `07-ci-sonar-cobertura.md` | Complemento de VCR, secrets e Testcontainers |

## Imagens E Diagramas

| Arquivo visual | Descricao textual registrada |
|---|---|
| `Introdução à Qualidade de Software - Qualidade de Software_files/breakdown_topics_quality.png` | Mapa SWEBOK de Software Quality: fundamentos, gestao da qualidade, garantia da qualidade e ferramentas |
| `Planejamento de Testes e Casos de Teste - Qualidade de Software_files/use_case_diagram.png` | Exemplo de diagrama de caso de uso com ator, limite do sistema, casos de uso, include, extend e sistemas externos |
| SVGs/Mermaid em `Fundamentos de Testes_ Caixa Branca vs. Caixa Preta - Qualidade de Software.html` | Diagramas de sequencia usados como referencia para explicar fluxos caixa branca e caixa preta |

## Tabelas E Blocos De Codigo

| Fonte | Tipo de conteudo | Como sustenta a base |
|---|---|---|
| `Planejamento de Testes e Casos de Teste - Qualidade de Software.html` | Tabelas e exemplos de especificacao de caso de uso, especificacao de caso de teste, RTM, criterios, riscos e exemplo PlantUML | Sustenta `02-casos-de-uso.md`, `03-rtm.md`, `04-plano-de-testes.md` e `05-casos-de-teste.md` com modelos rastreaveis de planejamento, desenho de testes e criterios de aceite |
| `Gestão de Erros e Bugs - Qualidade de Software.html` | Tabelas e conceitos de severity/severidade vs prioridade, metricas, triagem e ciclo de vida do bug | Sustenta `08-gestao-bugs-e-evidencias.md` e `10-guia-prova-oral.md` com vocabulario de defeitos, classificacao, decisao de triagem e evidencias |
| `Testes Automatizados em CI - Qualidade de Software.html` | Exemplos de codigo/configuracao para GitHub Actions, fluxo de CI e boas praticas de pipeline | Sustenta `06-estrategia-automacao.md` e `07-ci-sonar-cobertura.md` com referencia para automacao, validacao em pull request e organizacao dos estagios de CI |
| `Jest no GitHub Actions + SonarCloud - Qualidade de Software.html` | Exemplos de workflow, propriedades Sonar, cobertura e quality gate | Sustenta `07-ci-sonar-cobertura.md` e `10-guia-prova-oral.md` com referencia para checks de PR, limite de cobertura, analise estatica e decisao de aprovacao |
| `VCR, API Seguras e TestContainers - Qualidade de Software.html` | Exemplos de VCR em Java com WireMock, stubs/mappings, replay HTTP, comparacao VCR vs Testcontainers, secrets e pipeline de qualidade com Docker | Sustenta `06-estrategia-automacao.md`, `07-ci-sonar-cobertura.md` e `10-guia-prova-oral.md` com estrategias para testes de integracao, isolamento de APIs, seguranca de credenciais e ambiente reprodutivel |
| `Introdução à Qualidade de Software - Qualidade de Software.html` | Snippets de comandos para setup e repositorio do projeto | Sustenta `00-visao-geral.md`, `09-checklist-entrega.md` e `10-guia-prova-oral.md` com comandos de referencia para preparacao do ambiente, acesso ao projeto e alinhamento da entrega |

## Regras De Uso

- Slides locais sao fonte principal para requisitos oficiais, avaliacao e entrega.
- Materiais online sao complemento, nao substituto.
- Quando houver divergencia entre material complementar de SonarCloud e protocolo oficial, a entrega deve seguir SonarQube.
- Cada conceito citado na prova oral deve apontar para um documento da base e uma evidencia planejada.
- Tabelas, blocos de codigo e configuracoes devem ser citados quando sustentarem modelos, pipelines, evidencias ou decisoes descritas na base.
