# Base de Conhecimento do Projeto Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the initial `docs/` knowledge base for the official Qualidade de Software project and oral exam preparation.

**Architecture:** The work is documentation-first. Each Markdown file has one responsibility, stable IDs connect requirements to use cases, tests, evidence, and oral-exam talking points, and `referencias-aulas.md` records the source trail from local slides and online materials.

**Tech Stack:** Markdown, PowerShell validation commands, local HTML slide exports, online course material links, Spring Boot/MongoDB/Testcontainers/VCR/Sonar/GitHub Actions concepts.

---

## File Structure

Create these files:

- `docs/00-visao-geral.md`: project overview, scope, stack, deliverables, and oral explanation.
- `docs/01-requisitos.md`: functional requirements, non-functional requirements, technical restrictions, assumptions, and oral explanation.
- `docs/02-casos-de-uso.md`: use cases with main flow, alternatives, exceptions, and oral explanation.
- `docs/03-rtm.md`: requirements traceability matrix with links to use cases, tests, evidence targets, and oral explanation.
- `docs/04-plano-de-testes.md`: test plan with scope, objectives, approach, environment, criteria, risks, metrics, roles, and oral explanation.
- `docs/05-casos-de-teste.md`: planned positive, negative, parameterized, white-box, black-box, and integration test cases.
- `docs/06-estrategia-automacao.md`: automation strategy with Testcontainers, VCR/WireMock, coverage, no-mock rule, and oral explanation.
- `docs/07-ci-sonar-cobertura.md`: CI, JaCoCo coverage, SonarQube, quality gate, and oral explanation.
- `docs/08-gestao-bugs-e-evidencias.md`: bug registration, severity, priority, evidence, retest, regression, and oral explanation.
- `docs/09-checklist-entrega.md`: final delivery checklist aligned with the official project statement.
- `docs/10-guia-prova-oral.md`: central oral exam guide with concept-to-implementation-to-evidence map, likely questions, and concise answers.
- `docs/referencias-aulas.md`: source map for local slides, online written materials, images, SVGs, tables, and code blocks.

Existing files to read during execution:

- `docs/superpowers/specs/2026-05-16-base-conhecimento-projeto-design.md`
- `Descricao do Projeto - Qualidade de Software.html`
- `Introducao a Qualidade de Software - Qualidade de Software.html`
- `Fundamentos de Testes_ Caixa Branca vs. Caixa Preta - Qualidade de Software.html`
- `Planejamento de Testes e Casos de Teste - Qualidade de Software.html`
- `Gestao de Erros e Bugs - Qualidade de Software.html`
- `Testes Automatizados em CI - Qualidade de Software.html`
- `Jest no GitHub Actions + SonarCloud - Qualidade de Software.html`
- `VCR, API Seguras e TestContainers - Qualidade de Software.html`

Git note:

- The workspace is not currently a Git repository. For each task, run `git rev-parse --is-inside-work-tree`. If it prints `true`, commit the task. If it fails with `fatal: not a git repository`, skip the commit step and mention that in the task checkpoint.

---

### Task 1: Create The Source Reference Map

**Files:**
- Create: `docs/referencias-aulas.md`

- [ ] **Step 1: Confirm source availability**

Run:

```powershell
Get-ChildItem -File -Filter '*.html' | Sort-Object Name | Select-Object Name,Length
```

Expected: 8 HTML files, including `Descricao do Projeto`, `Introducao`, `Fundamentos`, `Planejamento`, `Gestao`, `Testes Automatizados em CI`, `Jest`, and `VCR`.

- [ ] **Step 2: Create `docs/referencias-aulas.md`**

Write the file with this structure and content:

```markdown
# Referencias Das Aulas

Esta base usa os slides HTML locais como fonte principal e os materiais escritos online como complemento. Quando houver conflito, a descricao oficial do projeto prevalece.

## Fontes Locais

| Fonte local | Uso na base | Tipo de conteudo aproveitado |
|---|---|---|
| `Descricao do Projeto - Qualidade de Software.html` | `00-visao-geral.md`, `01-requisitos.md`, `09-checklist-entrega.md`, `10-guia-prova-oral.md` | Requisitos oficiais, stack, cobertura minima, entrega, RTM, prazo |
| `Introducao a Qualidade de Software - Qualidade de Software.html` | `00-visao-geral.md`, `08-gestao-bugs-e-evidencias.md`, `10-guia-prova-oral.md` | Qualidade, avaliacao, custo da qualidade, V&V, SQA, modelo de qualidade |
| `Fundamentos de Testes_ Caixa Branca vs. Caixa Preta - Qualidade de Software.html` | `04-plano-de-testes.md`, `05-casos-de-teste.md`, `10-guia-prova-oral.md` | Caixa branca, caixa preta, testes unitarios, parametrizados, E2E, piramide |
| `Planejamento de Testes e Casos de Teste - Qualidade de Software.html` | `02-casos-de-uso.md`, `03-rtm.md`, `04-plano-de-testes.md`, `05-casos-de-teste.md` | Personas, casos de uso, casos de teste, RTM, criterios, riscos |
| `Gestao de Erros e Bugs - Qualidade de Software.html` | `08-gestao-bugs-e-evidencias.md`, `10-guia-prova-oral.md` | Erro, defeito, falha, severidade, prioridade, ciclo de vida do bug, evidencia |
| `Testes Automatizados em CI - Qualidade de Software.html` | `06-estrategia-automacao.md`, `07-ci-sonar-cobertura.md` | CI, GitHub Actions, piramide de testes em CI, boas praticas |
| `Jest no GitHub Actions + SonarCloud - Qualidade de Software.html` | `07-ci-sonar-cobertura.md`, `10-guia-prova-oral.md` | Quality gate, cobertura, exemplos Sonar/SonarCloud como complemento, checks de PR |
| `VCR, API Seguras e TestContainers - Qualidade de Software.html` | `06-estrategia-automacao.md`, `07-ci-sonar-cobertura.md`, `10-guia-prova-oral.md` | VCR em Java com WireMock, secrets, Testcontainers, pipeline com Docker |

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
| `Introducao a Qualidade de Software - Qualidade de Software_files/breakdown_topics_quality.png` | Mapa SWEBOK de Software Quality: fundamentos, gestao da qualidade, garantia da qualidade e ferramentas |
| `Planejamento de Testes e Casos de Teste - Qualidade de Software_files/use_case_diagram.png` | Exemplo de diagrama de caso de uso com ator, limite do sistema, casos de uso, include, extend e sistemas externos |
| SVGs/Mermaid em `Fundamentos de Testes_ Caixa Branca vs. Caixa Preta - Qualidade de Software.html` | Diagramas de sequencia usados como referencia para explicar fluxos caixa branca e caixa preta |

## Regras De Uso

- Slides locais sao fonte principal para requisitos oficiais, avaliacao e entrega.
- Materiais online sao complemento, nao substituto.
- Cada conceito citado na prova oral deve apontar para um documento da base e uma evidencia planejada.
```

- [ ] **Step 3: Verify source map**

Run:

```powershell
Select-String -LiteralPath 'docs\referencias-aulas.md' -Pattern 'Descricao do Projeto','Testcontainers','prova oral','material_aula02','breakdown_topics_quality','use_case_diagram'
```

Expected: one or more matches for each pattern.

- [ ] **Step 4: Commit checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git add docs/referencias-aulas.md
git commit -m "docs: map course sources"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report this checkpoint as uncommitted.

---

### Task 2: Create Overview And Requirements Documents

**Files:**
- Create: `docs/00-visao-geral.md`
- Create: `docs/01-requisitos.md`

- [ ] **Step 1: Create `docs/00-visao-geral.md`**

Write the file with these sections and exact project facts:

```markdown
# Visao Geral Do Projeto

## Projeto

O projeto oficial da disciplina e o **Gerenciador de Biblioteca Pessoal**. O objetivo e desenvolver uma aplicacao completa para cadastro e gerenciamento de livros de uma biblioteca pessoal, incluindo cadastro de usuarios, autenticacao e gerenciamento de sessao.

## Objetivo De Qualidade

O foco do projeto nao e apenas entregar funcionalidades. O foco e demonstrar qualidade de software com requisitos rastreaveis, testes planejados, testes automatizados, cobertura minima de 80%, evidencia objetiva, CI no GitHub Actions e analise com SonarQube.

## Escopo Funcional

- Cadastro de usuarios.
- Autenticacao de usuarios.
- Gerenciamento de sessao na interface.
- CRUD de livros.
- Persistencia dos dados em MongoDB.
- Interface web funcional e responsiva.

## Stack Oficial

| Camada | Decisao |
|---|---|
| Backend | Spring Boot com Java |
| Arquitetura | MVC |
| Banco | MongoDB |
| Testes de persistencia | Testcontainers |
| Testes de API externa | VCR em Java com WireMock quando houver dependencia externa |
| Qualidade automatizada | SonarQube |
| CI | GitHub Actions |
| Cobertura minima | 80% |

## Entregaveis Principais

- Repositorio GitHub com codigo-fonte organizado.
- README detalhado.
- `RTM.md` ou matriz equivalente em `docs/03-rtm.md`.
- Diagramas UML de sequencia para requisitos funcionais.
- Relatorio de cobertura, preferencialmente via JaCoCo.
- Pipeline de CI no GitHub Actions.
- Integracao com SonarQube.

## Premissas

- O dominio nao sera adaptado: o tema e biblioteca pessoal.
- A base de conhecimento e a primeira etapa; o codigo sera planejado depois.
- Quando um requisito oficial conflitar com sugestoes de aula, o requisito oficial vence.

## Aplicacao na prova oral

Resposta curta sugerida: "Eu organizei o projeto como uma aplicacao de biblioteca pessoal, mas usei o projeto para demonstrar qualidade. Cada requisito tem rastreabilidade, casos de uso, casos de teste e evidencia. A qualidade aparece nos testes automatizados, na cobertura minima de 80%, no CI com GitHub Actions, no Sonar e nos testes de integracao com Testcontainers."
```

- [ ] **Step 2: Create `docs/01-requisitos.md`**

Write the file with stable IDs:

```markdown
# Requisitos Do Projeto

## Requisitos Funcionais

| ID | Requisito | Origem | Criterio de aceite |
|---|---|---|---|
| RF-01 | Permitir cadastro de novos usuarios. | Descricao oficial | Usuario informa dados validos e o sistema cria a conta sem duplicar email. |
| RF-02 | Permitir autenticacao de usuarios. | Descricao oficial | Usuario cadastrado acessa o sistema com credenciais validas. |
| RF-03 | Gerenciar sessao do usuario na interface. | Descricao oficial | Usuario autenticado permanece identificado e pode encerrar a sessao. |
| RF-04 | Permitir cadastro de livros. | Descricao oficial | Usuario autenticado cria livro com titulo e demais dados obrigatorios. |
| RF-05 | Permitir consulta e listagem de livros. | Descricao oficial | Usuario autenticado visualiza os livros cadastrados em sua biblioteca. |
| RF-06 | Permitir atualizacao de livros. | Descricao oficial | Usuario autenticado altera dados de um livro existente. |
| RF-07 | Permitir exclusao de livros. | Descricao oficial | Usuario autenticado remove um livro da biblioteca. |
| RF-08 | Restringir operacoes de livros ao usuario autenticado. | Premissa conservadora de seguranca | Usuario nao autenticado nao acessa CRUD e usuario autenticado nao altera dados de outro usuario. |

## Requisitos Nao Funcionais

| ID | Requisito | Criterio de aceite |
|---|---|---|
| RNF-01 | Usar backend Spring Boot com Java. | Projeto compila e executa com stack Java/Spring. |
| RNF-02 | Usar arquitetura MVC. | Controllers, servicos/modelos e camada de persistencia possuem responsabilidades separadas. |
| RNF-03 | Persistir dados em MongoDB. | Usuarios e livros sao armazenados em colecoes MongoDB. |
| RNF-04 | Entregar interface web funcional e responsiva. | Fluxos principais funcionam em tela desktop e mobile. |
| RNF-05 | Manter cobertura minima de 80%. | Relatorio de cobertura mostra valor igual ou maior que 80%. |
| RNF-06 | Executar validacoes automaticas no GitHub Actions. | Pipeline roda em push e pull request. |
| RNF-07 | Integrar SonarQube. | Analise de qualidade executa no pipeline ou em etapa documentada. |
| RNF-08 | Proteger secrets e configuracoes sensiveis. | Senhas, tokens e chaves nao aparecem no repositorio. |

## Restricoes Tecnicas

| ID | Restricao | Impacto |
|---|---|---|
| RT-01 | Usar Testcontainers para persistencia em testes. | Testes de integracao devem subir MongoDB real em container. |
| RT-02 | Usar VCR em Java com WireMock para API externa quando existir dependencia externa. | Chamadas externas devem ser reproduziveis e seguras. |
| RT-03 | Proibir mocks tradicionais no projeto final conforme orientacao oficial. | Usar Testcontainers para persistencia e VCR para API externa. |
| RT-04 | Documentar RTM com 100% de rastreabilidade funcional. | Cada RF deve apontar para caso de uso, caso de teste e evidencia. |

## Aplicacao na prova oral

Resposta curta sugerida: "Eu separei requisitos funcionais, nao funcionais e restricoes tecnicas para mostrar rastreabilidade. Por exemplo, RF-04 fala do cadastro de livros; ele aparece em caso de uso, em caso de teste, na RTM e depois em evidencia de teste. Isso mostra que nao testei aleatoriamente: testei contra requisitos."
```

- [ ] **Step 3: Verify requirement IDs**

Run:

```powershell
Select-String -LiteralPath 'docs\01-requisitos.md' -Pattern 'RF-01','RF-08','RNF-05','RT-04','Aplicacao na prova oral'
```

Expected: matches for all IDs and the oral-exam section.

- [ ] **Step 4: Commit checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git add docs/00-visao-geral.md docs/01-requisitos.md
git commit -m "docs: define project overview and requirements"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report this checkpoint as uncommitted.

---

### Task 3: Create Use Cases And Traceability Matrix

**Files:**
- Create: `docs/02-casos-de-uso.md`
- Create: `docs/03-rtm.md`

- [ ] **Step 1: Create `docs/02-casos-de-uso.md`**

Write seven use cases:

```markdown
# Casos De Uso

## Convencao

- IDs de casos de uso usam o formato `UC-XX`.
- Cada caso de uso aponta para um ou mais requisitos funcionais.
- Fluxos alternativos e excecoes viram candidatos a casos de teste negativos.

## UC-01 - Cadastrar Usuario

| Campo | Valor |
|---|---|
| Requisitos | RF-01 |
| Ator principal | Visitante |
| Pre-condicao | Visitante nao autenticado esta na tela de cadastro. |
| Pos-condicao | Conta criada e pronta para autenticacao. |

Fluxo principal:

1. Visitante abre a tela de cadastro.
2. Sistema exibe formulario.
3. Visitante informa nome, email e senha validos.
4. Sistema valida dados obrigatorios e formato do email.
5. Sistema grava usuario no MongoDB.
6. Sistema informa sucesso.

Fluxos alternativos:

- Email ja cadastrado: sistema recusa o cadastro e informa erro claro.
- Senha invalida: sistema recusa o cadastro e informa regra violada.

Excecoes:

- MongoDB indisponivel: sistema retorna erro controlado e registra evidencia.

## UC-02 - Autenticar Usuario

| Campo | Valor |
|---|---|
| Requisitos | RF-02, RF-03 |
| Ator principal | Usuario cadastrado |
| Pre-condicao | Usuario possui conta ativa. |
| Pos-condicao | Sessao iniciada. |

Fluxo principal:

1. Usuario acessa tela de login.
2. Usuario informa email e senha.
3. Sistema valida credenciais.
4. Sistema cria sessao.
5. Interface redireciona para area autenticada.

Fluxos alternativos:

- Credenciais invalidas: sistema nega acesso.
- Campos vazios: sistema mostra validacao.

Excecoes:

- Falha de persistencia: login nao e concluido e erro e tratado.

## UC-03 - Encerrar Sessao

| Campo | Valor |
|---|---|
| Requisitos | RF-03 |
| Ator principal | Usuario autenticado |
| Pre-condicao | Usuario possui sessao ativa. |
| Pos-condicao | Sessao encerrada. |

Fluxo principal:

1. Usuario aciona sair.
2. Sistema invalida sessao.
3. Interface retorna para login.

## UC-04 - Cadastrar Livro

| Campo | Valor |
|---|---|
| Requisitos | RF-04, RF-08 |
| Ator principal | Usuario autenticado |
| Pre-condicao | Usuario esta autenticado. |
| Pos-condicao | Livro cadastrado na biblioteca do usuario. |

Fluxo principal:

1. Usuario abre formulario de novo livro.
2. Usuario informa titulo e dados complementares.
3. Sistema valida campos obrigatorios.
4. Sistema associa livro ao usuario autenticado.
5. Sistema persiste livro no MongoDB.
6. Sistema exibe livro na listagem.

Fluxos alternativos:

- Titulo vazio: sistema bloqueia cadastro.
- Sessao expirada: sistema redireciona para login.

## UC-05 - Consultar Livros

| Campo | Valor |
|---|---|
| Requisitos | RF-05, RF-08 |
| Ator principal | Usuario autenticado |
| Pre-condicao | Usuario esta autenticado. |
| Pos-condicao | Livros do usuario sao exibidos. |

Fluxo principal:

1. Usuario acessa biblioteca.
2. Sistema consulta livros associados ao usuario.
3. Sistema exibe lista.

Fluxos alternativos:

- Biblioteca vazia: sistema exibe estado vazio.
- Sessao invalida: sistema nega acesso.

## UC-06 - Atualizar Livro

| Campo | Valor |
|---|---|
| Requisitos | RF-06, RF-08 |
| Ator principal | Usuario autenticado |
| Pre-condicao | Livro existe e pertence ao usuario. |
| Pos-condicao | Livro atualizado. |

Fluxo principal:

1. Usuario seleciona livro.
2. Sistema exibe dados atuais.
3. Usuario altera informacoes.
4. Sistema valida dados.
5. Sistema salva alteracoes.

Fluxos alternativos:

- Livro inexistente: sistema informa que o recurso nao foi encontrado.
- Livro de outro usuario: sistema nega a operacao.

## UC-07 - Excluir Livro

| Campo | Valor |
|---|---|
| Requisitos | RF-07, RF-08 |
| Ator principal | Usuario autenticado |
| Pre-condicao | Livro existe e pertence ao usuario. |
| Pos-condicao | Livro removido. |

Fluxo principal:

1. Usuario solicita exclusao.
2. Sistema pede confirmacao.
3. Usuario confirma.
4. Sistema remove livro.
5. Sistema atualiza listagem.

Fluxos alternativos:

- Usuario cancela confirmacao: livro permanece cadastrado.
- Livro de outro usuario: sistema nega a operacao.

## Aplicacao na prova oral

Resposta curta sugerida: "Eu usei casos de uso para transformar requisitos em fluxos testaveis. O caso de uso nao e so desenho; ele descreve ator, pre-condicao, fluxo principal, excecoes e pos-condicao. Esses fluxos viram casos de teste e aparecem na RTM."
```

- [ ] **Step 2: Create `docs/03-rtm.md`**

Write the initial traceability matrix:

```markdown
# RTM - Matriz De Rastreabilidade

## Objetivo

A RTM conecta requisito, caso de uso, caso de teste e evidencia. A meta do projeto e manter 100% dos requisitos funcionais rastreados.

## Matriz

| Requisito | Caso de uso | Casos de teste planejados | Evidencia esperada | Conceito aplicado |
|---|---|---|---|---|
| RF-01 | UC-01 | TC-001, TC-002, TC-003 | Teste de cadastro, relatorio de cobertura, print/API response | Validacao, caso positivo, caso negativo |
| RF-02 | UC-02 | TC-004, TC-005, TC-006 | Teste de login, evidencia de sessao | Caixa preta, autenticacao |
| RF-03 | UC-02, UC-03 | TC-007, TC-008 | Teste de sessao e logout | Gerenciamento de sessao |
| RF-04 | UC-04 | TC-009, TC-010, TC-011 | Teste de criacao de livro com MongoDB via Testcontainers | Integracao, persistencia real |
| RF-05 | UC-05 | TC-012, TC-013 | Teste de listagem e estado vazio | Caixa preta, comportamento observavel |
| RF-06 | UC-06 | TC-014, TC-015, TC-016 | Teste de atualizacao e autorizacao | Regressao, regra de negocio |
| RF-07 | UC-07 | TC-017, TC-018 | Teste de exclusao e cancelamento | Fluxo alternativo |
| RF-08 | UC-04, UC-05, UC-06, UC-07 | TC-019, TC-020, TC-021 | Testes de acesso negado e isolamento por usuario | Seguranca, nao conformidade evitada |

## Como Manter A RTM

1. Criar ou alterar requisito.
2. Atualizar caso de uso afetado.
3. Criar caso de teste positivo e negativo.
4. Rodar teste automatizado ou registrar evidencia manual.
5. Atualizar evidencia na matriz.

## Aplicacao na prova oral

Resposta curta sugerida: "A RTM e a minha prova de que existe ligacao entre requisito e teste. Se alguem perguntar como eu sei que RF-04 foi validado, eu mostro a linha RF-04, o UC-04, os testes TC-009 a TC-011 e a evidencia de execucao."
```

- [ ] **Step 3: Verify traceability coverage**

Run:

```powershell
Select-String -LiteralPath 'docs\03-rtm.md' -Pattern 'RF-01','RF-02','RF-03','RF-04','RF-05','RF-06','RF-07','RF-08'
```

Expected: each `RF-XX` appears in the RTM.

- [ ] **Step 4: Commit checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git add docs/02-casos-de-uso.md docs/03-rtm.md
git commit -m "docs: add use cases and traceability"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report this checkpoint as uncommitted.

---

### Task 4: Create Test Plan And Test Case Catalog

**Files:**
- Create: `docs/04-plano-de-testes.md`
- Create: `docs/05-casos-de-teste.md`

- [ ] **Step 1: Create `docs/04-plano-de-testes.md`**

Write the file with this content:

```markdown
# Plano De Testes

## Escopo

Entram no escopo os fluxos de cadastro, login, sessao, CRUD de livros, persistencia em MongoDB, validacoes, autorizacao por usuario, cobertura e qualidade automatizada.

Ficam fora do escopo inicial pagamentos, recomendacoes de livros, integracoes externas obrigatorias e publicacao em producao.

## Objetivos

- Validar que os requisitos funcionais foram implementados.
- Demonstrar cobertura minima de 80%.
- Reduzir risco de regressao com testes automatizados.
- Validar persistencia real com Testcontainers.
- Registrar evidencia objetiva para entrega e prova oral.

## Abordagem

| Nivel | Aplicacao no projeto |
|---|---|
| Unitario | Validar regras de dominio, validacoes e servicos sem depender da interface. |
| Integracao | Validar repositories, services e controllers com MongoDB em Testcontainers. |
| Caixa branca | Cobrir decisoes internas, validacoes e regras de autorizacao. |
| Caixa preta | Validar entradas e saidas dos fluxos pelo comportamento esperado. |
| Parametrizado | Testar combinacoes de campos invalidos, credenciais e dados de livros. |
| Regressao | Reexecutar suite no CI a cada push ou pull request. |

## Ambiente

- Java e Spring Boot.
- MongoDB em Testcontainers para testes de integracao.
- GitHub Actions para execucao automatizada.
- JaCoCo para cobertura.
- SonarQube para qualidade.
- VCR em Java com WireMock quando houver API externa.

## Criterios De Entrada

- Requisitos documentados em `docs/01-requisitos.md`.
- Casos de uso documentados em `docs/02-casos-de-uso.md`.
- Ambiente local capaz de executar build e testes.
- Dados de teste definidos sem secrets reais.

## Criterios De Suspensao

- Banco de testes indisponivel por falha de Docker.
- Build quebrado antes da execucao dos testes.
- Taxa de falha critica acima de 30% por problema de ambiente.

## Criterios De Saida

- Todos os RFs aparecem na RTM.
- Cobertura total igual ou maior que 80%.
- Nenhum defeito critico aberto nos fluxos principais.
- CI executando com sucesso.
- Evidencias registradas para entrega.

## Riscos E Mitigacoes

| Risco | Impacto | Mitigacao |
|---|---|---|
| Testes acoplados entre si | Falhas intermitentes | Limpar dados entre testes e isolar cenarios. |
| MongoDB local diferente do CI | Regressao nao detectada | Usar Testcontainers. |
| Cobertura abaixo de 80% perto da entrega | Reprovacao de criterio | Medir cobertura desde o inicio. |
| Secrets vazados em fixtures ou VCR | Risco de seguranca | Sanitizar cassetes e usar GitHub Secrets. |
| RTM desatualizada | Falha de rastreabilidade | Atualizar RTM junto com requisitos e testes. |

## Metricas

- Cobertura percentual.
- Numero de testes passando.
- Numero de defeitos por severidade.
- Tempo de pipeline.
- Requisitos com rastreabilidade completa.

## Aplicacao na prova oral

Resposta curta sugerida: "Meu plano de testes define o que entra, o que fica fora, quando posso iniciar testes e quando considero a qualidade suficiente. Eu uso criterios mensuraveis, como cobertura acima de 80%, RTM completa e defeitos criticos zerados."
```

- [ ] **Step 2: Create `docs/05-casos-de-teste.md`**

Write the catalog using the IDs referenced by the RTM:

```markdown
# Casos De Teste

## Convencao

- IDs usam o formato `TC-XXX`.
- Cada teste aponta para requisito e caso de uso.
- Casos positivos confirmam o fluxo esperado.
- Casos negativos confirmam validacoes, excecoes e acesso negado.

## Catalogo

| ID | Tipo | Requisito | Caso de uso | Objetivo | Resultado esperado |
|---|---|---|---|---|---|
| TC-001 | Positivo | RF-01 | UC-01 | Cadastrar usuario com dados validos. | Conta criada. |
| TC-002 | Negativo | RF-01 | UC-01 | Recusar email ja cadastrado. | Sistema informa duplicidade. |
| TC-003 | Parametrizado | RF-01 | UC-01 | Validar combinacoes de nome, email e senha invalidos. | Sistema bloqueia dados invalidos. |
| TC-004 | Positivo | RF-02 | UC-02 | Login com credenciais validas. | Sessao criada. |
| TC-005 | Negativo | RF-02 | UC-02 | Login com senha incorreta. | Acesso negado. |
| TC-006 | Parametrizado | RF-02 | UC-02 | Validar campos vazios e email malformado. | Mensagens de validacao. |
| TC-007 | Positivo | RF-03 | UC-02 | Manter usuario autenticado em area protegida. | Usuario acessa area autenticada. |
| TC-008 | Positivo | RF-03 | UC-03 | Encerrar sessao. | Usuario volta para login. |
| TC-009 | Positivo | RF-04 | UC-04 | Cadastrar livro valido. | Livro persistido no MongoDB. |
| TC-010 | Negativo | RF-04 | UC-04 | Bloquear livro sem titulo. | Sistema informa campo obrigatorio. |
| TC-011 | Integracao | RF-04 | UC-04 | Persistir livro usando MongoDB em Testcontainers. | Registro existe no container de teste. |
| TC-012 | Positivo | RF-05 | UC-05 | Listar livros cadastrados. | Lista contem livros do usuario. |
| TC-013 | Caixa preta | RF-05 | UC-05 | Exibir estado vazio. | Interface informa ausencia de livros. |
| TC-014 | Positivo | RF-06 | UC-06 | Atualizar dados de livro. | Alteracoes sao salvas. |
| TC-015 | Negativo | RF-06 | UC-06 | Atualizar livro inexistente. | Sistema retorna nao encontrado. |
| TC-016 | Caixa branca | RF-06 | UC-06 | Cobrir regra interna de autorizacao na atualizacao. | Usuario nao altera livro alheio. |
| TC-017 | Positivo | RF-07 | UC-07 | Excluir livro existente. | Livro deixa de aparecer na lista. |
| TC-018 | Alternativo | RF-07 | UC-07 | Cancelar confirmacao de exclusao. | Livro permanece cadastrado. |
| TC-019 | Negativo | RF-08 | UC-04 | Tentar cadastrar livro sem sessao. | Acesso negado ou redirecionado. |
| TC-020 | Negativo | RF-08 | UC-06 | Tentar editar livro de outro usuario. | Operacao negada. |
| TC-021 | Negativo | RF-08 | UC-07 | Tentar excluir livro de outro usuario. | Operacao negada. |

## Aplicacao Das Tecnicas

| Tecnica | Exemplos no catalogo |
|---|---|
| Caixa branca | TC-016 cobre decisao interna de autorizacao. |
| Caixa preta | TC-013 observa comportamento sem conhecer implementacao. |
| Parametrizado | TC-003 e TC-006 testam multiplas entradas. |
| Integracao | TC-011 usa MongoDB real via Testcontainers. |
| Regressao | Todos os TCs automatizados rodam no CI. |

## Aplicacao na prova oral

Resposta curta sugerida: "Eu consigo explicar meus testes por tecnica. Teste caixa preta valida comportamento visivel, como estado vazio na lista. Caixa branca valida regra interna, como autorizacao. Teste de integracao valida MongoDB real com Testcontainers."
```

- [ ] **Step 3: Verify test IDs**

Run:

```powershell
Select-String -LiteralPath 'docs\05-casos-de-teste.md' -Pattern 'TC-001','TC-011','TC-016','TC-021','Aplicacao Das Tecnicas'
```

Expected: matches for all listed IDs and the technique section.

- [ ] **Step 4: Commit checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git add docs/04-plano-de-testes.md docs/05-casos-de-teste.md
git commit -m "docs: define test plan and test cases"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report this checkpoint as uncommitted.

---

### Task 5: Create Automation, CI, Sonar, And Coverage Documents

**Files:**
- Create: `docs/06-estrategia-automacao.md`
- Create: `docs/07-ci-sonar-cobertura.md`

- [ ] **Step 1: Create `docs/06-estrategia-automacao.md`**

Write:

```markdown
# Estrategia De Automacao

## Principio

A automacao deve provar comportamento com o maximo de fidelidade possivel. Como o projeto final proibe mocks tradicionais, a estrategia privilegia Testcontainers para persistencia real e VCR em Java com WireMock para dependencias HTTP externas quando elas existirem.

## Testcontainers

Aplicacao no projeto:

- Subir MongoDB em container durante testes de integracao.
- Rodar repositories e services contra banco real controlado.
- Destruir ambiente ao final da suite.
- Reduzir diferenca entre maquina local e CI.

Evidencia esperada:

- Logs do teste mostrando container iniciado.
- Testes de integracao passando.
- Pipeline do GitHub Actions executando testes com Docker disponivel.

## VCR Em Java Com WireMock

Aplicacao no projeto:

- Usar somente se houver chamada a API externa.
- Gravar ou criar respostas HTTP reproduziveis.
- Sanitizar tokens, emails reais e dados sensiveis.
- Evitar dependencia de internet no CI.

Evidencia esperada:

- Arquivos de mapping seguros.
- Testes sem chamada real a terceiros.
- Secrets fora do repositorio.

## Regra Sobre Mocks

O projeto final nao deve usar mocks tradicionais como estrategia de validacao. Quando a necessidade for persistencia, usar Testcontainers. Quando a necessidade for HTTP externo, usar VCR em Java com WireMock. Para regras puras de dominio, usar testes unitarios sem simular infraestrutura.

## Camadas Automatizadas

| Camada | Ferramenta esperada | Finalidade |
|---|---|---|
| Unitarios | JUnit 5 | Validar regras rapidas. |
| Integracao | Spring Boot Test + Testcontainers | Validar MongoDB real. |
| HTTP externo | VCR/WireMock | Evitar rede real e proteger secrets. |
| Cobertura | JaCoCo | Medir 80% minimo. |
| CI | GitHub Actions | Executar a suite automaticamente. |

## Aplicacao na prova oral

Resposta curta sugerida: "Eu apliquei Testcontainers porque a aula mostrou que mock de banco pode esconder problemas reais. Com Testcontainers, o teste usa MongoDB real em Docker. Para API externa, eu explicaria VCR em Java com WireMock como replay seguro de respostas HTTP, sem depender da internet e sem vazar secrets."
```

- [ ] **Step 2: Create `docs/07-ci-sonar-cobertura.md`**

Write:

```markdown
# CI, Sonar E Cobertura

## Objetivo

O pipeline deve executar validacoes automaticas do repositorio, gerar cobertura minima de 80% e publicar analise de qualidade via SonarQube.

## Pipeline Esperado

```yaml
name: quality

on:
  push:
    branches: [main]
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B verify
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: coverage-report
          path: target/site/jacoco/
```

## Cobertura

- Ferramenta recomendada: JaCoCo.
- Meta minima: 80%.
- Evidencia: relatorio HTML ou XML gerado no build.
- Uso na RTM: cobertura mostra amplitude da suite, mas nao substitui rastreabilidade por requisito.

## SonarQube

| Item | Aplicacao |
|---|---|
| Bugs | Indicam possiveis defeitos no codigo. |
| Code smells | Indicam divida tecnica e manutencao dificil. |
| Duplicacao | Indica repeticao que aumenta risco de erro. |
| Coverage | Confirma leitura da cobertura pelo Sonar. |
| Quality gate | Define criterio automatico de aceite. |

## Quality Gate No Pull Request

Um PR deve ser considerado saudavel quando testes passam, cobertura fica acima da meta e o Sonar nao aponta falhas bloqueantes no codigo novo.

## Aplicacao na prova oral

Resposta curta sugerida: "O CI tira a qualidade da minha maquina e coloca no repositorio. Cada push roda testes e gera cobertura. O Sonar complementa os testes porque avalia bugs, smells, duplicacao e quality gate. Teste responde se funciona; Sonar ajuda a responder se esta sustentavel."
```

- [ ] **Step 3: Verify CI and automation content**

Run:

```powershell
Select-String -LiteralPath 'docs\06-estrategia-automacao.md','docs\07-ci-sonar-cobertura.md' -Pattern 'Testcontainers','VCR','mvn -B verify','JaCoCo','Quality Gate','Aplicacao na prova oral'
```

Expected: matches across both files.

- [ ] **Step 4: Commit checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git add docs/06-estrategia-automacao.md docs/07-ci-sonar-cobertura.md
git commit -m "docs: define automation and quality pipeline"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report this checkpoint as uncommitted.

---

### Task 6: Create Bug Management, Delivery Checklist, And Oral Exam Guide

**Files:**
- Create: `docs/08-gestao-bugs-e-evidencias.md`
- Create: `docs/09-checklist-entrega.md`
- Create: `docs/10-guia-prova-oral.md`

- [ ] **Step 1: Create `docs/08-gestao-bugs-e-evidencias.md`**

Write:

```markdown
# Gestao De Bugs E Evidencias

## Termos

| Termo | Definicao operacional |
|---|---|
| Erro | Acao humana incorreta que pode introduzir problema. |
| Defeito ou bug | Problema existente no codigo, requisito, configuracao ou documentacao. |
| Falha | Manifestacao observavel do defeito em execucao. |
| Evidencia objetiva | Print, log, teste, video curto, relatorio ou resultado reproduzivel que comprova o comportamento. |

## Modelo De Registro De Bug

| Campo | Conteudo esperado |
|---|---|
| Titulo | Resumo claro do problema. |
| Ambiente | Local, CI, navegador, versao, banco. |
| Passos | Sequencia reproduzivel. |
| Resultado esperado | Comportamento correto segundo requisito. |
| Resultado obtido | Comportamento observado. |
| Evidencia | Link ou arquivo de teste, print, log ou relatorio. |
| Severidade | Impacto tecnico ou de negocio. |
| Prioridade | Ordem de correcao. |
| Requisito afetado | ID da RTM. |

## Severidade X Prioridade

Severidade mede impacto. Prioridade mede urgencia. Um bug visual pode ter baixa severidade e alta prioridade se afeta apresentacao; uma falha rara pode ter alta severidade e prioridade menor se nao bloqueia a entrega.

## Ciclo De Vida

1. Aberto.
2. Validado.
3. Classificado.
4. Corrigido.
5. Retestado.
6. Fechado com evidencia.

## Evidencias Para O Projeto

- Relatorio JaCoCo.
- Execucao do GitHub Actions.
- Resultado do Sonar.
- Prints dos fluxos principais.
- Logs de Testcontainers.
- RTM atualizada.

## Aplicacao na prova oral

Resposta curta sugerida: "Eu diferencio erro, defeito e falha. Se um teste de login falha, a falha e o comportamento observado; o defeito e a causa no codigo; o erro pode ter sido uma decisao humana de implementacao. Eu fecho bug apenas depois de reteste e evidencia."
```

- [ ] **Step 2: Create `docs/09-checklist-entrega.md`**

Write:

```markdown
# Checklist De Entrega

## Itens Obrigatorios

| Item | Status inicial | Evidencia esperada |
|---|---|---|
| Repositorio GitHub | Planejado | URL do repositorio. |
| Codigo-fonte organizado | Planejado | Estrutura do projeto Spring Boot. |
| README detalhado | Planejado | `README.md` com execucao, testes e stack. |
| RTM | Planejado | `docs/03-rtm.md` ou `RTM.md`. |
| Diagramas UML de sequencia | Planejado | Diagramas por requisito funcional. |
| Cobertura minima de 80% | Planejado | Relatorio JaCoCo. |
| CI no GitHub Actions | Planejado | Aba Actions com pipeline verde. |
| SonarQube | Planejado | Dashboard ou resultado de quality gate. |
| Testcontainers | Planejado | Testes de integracao com container MongoDB. |
| VCR/WireMock quando houver API externa | Condicional | Stubs/mappings seguros e sanitizados. |

## Checklist Antes Da Apresentacao

- Rodar testes localmente.
- Conferir pipeline remoto.
- Abrir relatorio de cobertura.
- Abrir dashboard do Sonar.
- Abrir RTM.
- Abrir guia de prova oral.
- Separar evidencias dos fluxos principais.

## Aplicacao na prova oral

Resposta curta sugerida: "Meu checklist mostra que a entrega nao e so codigo. Eu preciso demonstrar codigo, documentacao, rastreabilidade, testes, cobertura, CI e qualidade automatizada."
```

- [ ] **Step 3: Create `docs/10-guia-prova-oral.md`**

Write:

```markdown
# Guia Para Prova Oral

## Roteiro De 3 A 5 Minutos

1. Apresentar o dominio: Gerenciador de Biblioteca Pessoal.
2. Explicar funcionalidades principais: usuario, autenticacao, sessao e CRUD de livros.
3. Mostrar requisitos e RTM.
4. Explicar estrategia de testes: unitarios, integracao, caixa branca, caixa preta e parametrizados.
5. Mostrar evidencias: cobertura, CI, Sonar, Testcontainers e bugs/evidencias.
6. Fechar explicando como qualidade foi pensada desde requisitos ate entrega.

## Mapa Conceito -> Aplicacao -> Evidencia -> Como Explicar

| Conceito | Onde foi aplicado | Evidencia | Como explicar em 30 segundos |
|---|---|---|---|
| Qualidade de software | Requisitos, testes, CI, Sonar e evidencias | `docs/01-requisitos.md`, `docs/03-rtm.md`, CI, Sonar | Qualidade aqui e conformidade com requisito e capacidade de demonstrar isso com evidencia. |
| Verificacao | Testes e CI conferem se o produto foi construido corretamente. | Testes automatizados e pipeline | Verificacao responde se implementamos certo conforme especificacao. |
| Validacao | Fluxos do sistema confirmam se atende ao uso esperado. | Casos de uso e testes de fluxo | Validacao responde se o sistema atende a necessidade do usuario. |
| Caixa branca | Regras internas como autorizacao e validacao. | TC-016 | Eu conheco a logica interna e testo decisoes do codigo. |
| Caixa preta | Login, listagem e mensagens observadas pelo usuario. | TC-013 | Eu testo entrada e saida sem depender da implementacao interna. |
| Teste parametrizado | Combinacoes invalidas de cadastro e login. | TC-003, TC-006 | Um mesmo teste roda com varias entradas para cobrir cenarios. |
| Testcontainers | MongoDB real nos testes de integracao. | TC-011 e logs do container | Evita mock de banco e aumenta fidelidade. |
| VCR/WireMock | Replay seguro de API externa quando houver integracao. | Stubs/mappings sanitizados | Evita rede real no CI e protege secrets. |
| RTM | Ligacao entre requisito, caso de uso, teste e evidencia. | `docs/03-rtm.md` | Mostra que cada requisito funcional foi coberto. |
| CI | GitHub Actions roda testes automaticamente. | Aba Actions | Impede depender apenas da maquina local. |
| Cobertura | JaCoCo mede o quanto a suite exercita o codigo. | Relatorio de cobertura | Cobertura ajuda, mas nao substitui teste bem desenhado. |
| Sonar | Analise de bugs, smells, duplicacao e quality gate. | Dashboard Sonar | Complementa os testes com qualidade estatica. |
| Erro, defeito e falha | Gestao de bugs. | Registro de bug e reteste | Erro e humano, defeito fica no produto, falha aparece na execucao. |
| Severidade e prioridade | Triagem de bugs. | Registro de bug | Severidade e impacto; prioridade e urgencia. |

## Perguntas Provaveis

### Por que usar RTM?

Resposta: "Porque a RTM prova rastreabilidade. Ela mostra que cada requisito funcional tem caso de uso, caso de teste e evidencia. Sem RTM, eu poderia ter muitos testes e ainda assim deixar requisito sem cobertura."

### Qual a diferenca entre verificacao e validacao?

Resposta: "Verificacao confere se construimos corretamente conforme especificacao. Validacao confere se o sistema atende a necessidade do usuario. No projeto, os testes e CI ajudam na verificacao; os casos de uso e fluxos completos ajudam na validacao."

### Onde voce aplicou caixa branca?

Resposta: "Apliquei em regras internas, como autorizacao para impedir que um usuario altere livro de outro. Esse tipo de teste conhece a decisao interna do codigo."

### Onde voce aplicou caixa preta?

Resposta: "Apliquei nos fluxos observaveis, como login invalido ou lista vazia. Eu olho entrada e saida esperada sem depender de como o codigo foi implementado."

### Por que Testcontainers?

Resposta: "Porque o projeto proibe mocks tradicionais na entrega final. Testcontainers sobe MongoDB real em Docker para testes de integracao, entao eu valido driver, configuracao e persistencia de forma mais fiel."

### Para que serve o Sonar se ja existem testes?

Resposta: "Testes mostram comportamento. Sonar ajuda a avaliar qualidade estatica, como bugs potenciais, code smells, duplicacao e cobertura. Eles se complementam."

### Como voce sabe que tem qualidade suficiente?

Resposta: "Eu combino criterios: RTM completa, testes passando, cobertura minima de 80%, CI verde, Sonar sem bloqueios relevantes e evidencias dos fluxos principais."

## Evidencias Para Abrir Na Apresentacao

- `docs/03-rtm.md`
- `docs/05-casos-de-teste.md`
- Relatorio JaCoCo.
- GitHub Actions.
- SonarQube.
- Logs ou testes de Testcontainers.
- Diagramas UML de sequencia.
- Registro de bug ou exemplo de evidencia objetiva.

## Pontos Para Nao Confundir

- Cobertura alta nao garante qualidade sozinha.
- Mock nao e a escolha principal deste projeto final.
- Testcontainers nao substitui todos os testes; ele cobre integracao com infraestrutura.
- Sonar nao substitui teste funcional.
- Severidade nao e a mesma coisa que prioridade.
```

- [ ] **Step 4: Verify oral guide and checklist**

Run:

```powershell
Select-String -LiteralPath 'docs\08-gestao-bugs-e-evidencias.md','docs\09-checklist-entrega.md','docs\10-guia-prova-oral.md' -Pattern 'Erro','Severidade','GitHub Actions','Mapa Conceito','Perguntas Provaveis','Testcontainers'
```

Expected: matches across the three files.

- [ ] **Step 5: Commit checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git add docs/08-gestao-bugs-e-evidencias.md docs/09-checklist-entrega.md docs/10-guia-prova-oral.md
git commit -m "docs: add bug evidence checklist and oral guide"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report this checkpoint as uncommitted.

---

### Task 7: Run Cross-Document Verification

**Files:**
- Verify: `docs/*.md`

- [ ] **Step 1: Verify all required files exist**

Run:

```powershell
$required = @(
  'docs\00-visao-geral.md',
  'docs\01-requisitos.md',
  'docs\02-casos-de-uso.md',
  'docs\03-rtm.md',
  'docs\04-plano-de-testes.md',
  'docs\05-casos-de-teste.md',
  'docs\06-estrategia-automacao.md',
  'docs\07-ci-sonar-cobertura.md',
  'docs\08-gestao-bugs-e-evidencias.md',
  'docs\09-checklist-entrega.md',
  'docs\10-guia-prova-oral.md',
  'docs\referencias-aulas.md'
)
$missing = $required | Where-Object { -not (Test-Path $_) }
if ($missing) { $missing; exit 1 } else { 'All required docs exist' }
```

Expected: `All required docs exist`.

- [ ] **Step 2: Verify oral exam section in each principal document**

Run:

```powershell
Select-String -Path 'docs\0*.md','docs\10-guia-prova-oral.md' -Pattern 'Aplicacao na prova oral'
```

Expected: matches in `00` through `09`. `10-guia-prova-oral.md` is the central guide and does not need that exact section heading.

- [ ] **Step 3: Verify no unresolved planning markers were left in docs**

Run:

```powershell
$patterns = @(
  ('T' + 'BD'),
  ('TO' + 'DO'),
  '\?\?\?',
  ('place' + 'holder'),
  ('a ' + 'definir')
)
Select-String -Path 'docs\*.md' -Pattern $patterns
```

Expected: no output.

- [ ] **Step 4: Verify RF and TC consistency**

Run:

```powershell
Select-String -Path 'docs\*.md' -Pattern 'RF-01','RF-02','RF-03','RF-04','RF-05','RF-06','RF-07','RF-08','TC-001','TC-011','TC-021'
```

Expected: each requirement and representative test ID appears in at least one document, with RFs appearing in requirements and RTM.

- [ ] **Step 5: Final checkpoint when Git is available**

Run:

```powershell
git rev-parse --is-inside-work-tree
```

Expected if Git exists: `true`. Then run:

```powershell
git status --short
```

Expected after prior commits: clean or only unrelated untracked source slide files. If documentation files are still uncommitted, commit them:

```powershell
git add docs
git commit -m "docs: complete knowledge base verification"
```

Expected if Git is not initialized: `fatal: not a git repository`. Skip commit and report that documentation was verified without Git commits.

---

## Self-Review Checklist

After executing this plan:

- Confirm every file listed in the spec exists.
- Confirm `docs/01-requisitos.md` has stable RF, RNF, and RT IDs.
- Confirm `docs/03-rtm.md` maps every RF from RF-01 to RF-08.
- Confirm `docs/05-casos-de-teste.md` includes positive, negative, parameterized, white-box, black-box, and integration examples.
- Confirm `docs/06-estrategia-automacao.md` covers Testcontainers, VCR/WireMock, no-mock rule, and coverage.
- Confirm `docs/07-ci-sonar-cobertura.md` uses Java/Maven/JaCoCo rather than copying Node/Jest examples directly.
- Confirm `docs/10-guia-prova-oral.md` includes concept-to-implementation-to-evidence mapping and short answers.
- Confirm `docs/referencias-aulas.md` records local slides, online materials, images, SVGs, tables, and code blocks.
- Confirm no document contradicts the official project: Spring Boot, MongoDB, MVC, user registration, session management, book CRUD, 80% coverage, GitHub Actions, Sonar, RTM, Testcontainers, and VCR when external APIs exist.
