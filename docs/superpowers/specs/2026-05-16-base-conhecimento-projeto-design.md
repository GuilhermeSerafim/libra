# Design: Base de Conhecimento do Projeto Semestral de Qualidade de Software

## Objetivo

Criar uma base de conhecimento orientada a entregaveis para guiar a construcao do projeto oficial da disciplina: **Gerenciador de Biblioteca Pessoal**.

A base deve transformar os arquivos das aulas em uma referencia pratica para decidir o que desenvolver, quais documentos produzir, como comprovar qualidade, como preparar a entrega final e como explicar oralmente onde cada conceito da disciplina foi aplicado no projeto.

## Fontes

A base sera derivada principalmente dos arquivos HTML adicionados ao workspace. Os titulos abaixo estao normalizados sem acentos para manter este documento em ASCII; os arquivos reais no workspace preservam a grafia original quando aplicavel.

- `Descricao do Projeto - Qualidade de Software.html`
- `Introducao a Qualidade de Software - Qualidade de Software.html`
- `Fundamentos de Testes_ Caixa Branca vs. Caixa Preta - Qualidade de Software.html`
- `Planejamento de Testes e Casos de Teste - Qualidade de Software.html`
- `Gestao de Erros e Bugs - Qualidade de Software.html`
- `Testes Automatizados em CI - Qualidade de Software.html`
- `Jest no GitHub Actions + SonarCloud - Qualidade de Software.html`
- `VCR, API Seguras e TestContainers - Qualidade de Software.html`

Tambem serao usados os materiais escritos online referenciados pelos proprios slides, quando existirem e estiverem disponiveis. Esses materiais complementam os slides com explicacoes mais extensas e exemplos adicionais.

- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_projeto-descricao.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula01-introducao-qualidade.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula02-planejamento.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula03-fundamentos-caixa-branca-preta.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula05-gestao-erros-bugs.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula06-introducao-github-actions.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula07-testes-unidade-jest.html`
- `https://afonsolelis.github.io/aulas_senac/pages/qualidade/material/material_aula10-vcr-api-seguras.html`

Os nomes dos arquivos finais podem manter acentos quando ja existirem no material original, mas a estrutura criada para a base usara nomes simples em ASCII para facilitar navegacao e automacao. Quando houver diferenca entre slide local e material escrito online, o documento da base deve priorizar a descricao oficial do projeto e registrar a escolha em `referencias-aulas.md`.

## Escopo Do Projeto Oficial

O projeto sera o **Gerenciador de Biblioteca Pessoal**, conforme a descricao oficial da disciplina.

Funcionalidades centrais:

- Cadastro de usuarios.
- Autenticacao de usuarios.
- Gerenciamento de sessao na interface.
- CRUD de livros de uma biblioteca pessoal.
- Importacao opcional de metadados por ISBN com Open Library API.
- Persistencia em MongoDB.
- Interface web funcional e responsiva.

Tecnologias e praticas exigidas:

- Backend em Spring Boot com Java.
- Arquitetura MVC.
- MongoDB como banco NoSQL.
- Testcontainers para testes com persistencia real.
- VCR em Java com WireMock para chamadas da Open Library API.
- SonarQube como analise automatizada de qualidade.
- GitHub Actions com pipeline de CI completo.
- Cobertura minima de 80%.

## Estrutura Da Base

A base sera criada em `docs/` com os seguintes arquivos:

```text
docs/
  00-visao-geral.md
  01-requisitos.md
  02-casos-de-uso.md
  03-rtm.md
  04-plano-de-testes.md
  05-casos-de-teste.md
  06-estrategia-automacao.md
  07-ci-sonar-cobertura.md
  08-gestao-bugs-e-evidencias.md
  09-checklist-entrega.md
  10-guia-prova-oral.md
  referencias-aulas.md
```

## Conteudo Esperado

### `00-visao-geral.md`

Apresenta o projeto, objetivo, contexto da disciplina, stack, escopo funcional, integracao Open Library e entregaveis principais.

### `01-requisitos.md`

Organiza requisitos funcionais, requisitos nao funcionais e restricoes tecnicas. Cada requisito deve ter identificador estavel para uso na RTM.

### `02-casos-de-uso.md`

Descreve os principais casos de uso do sistema, incluindo fluxo principal, fluxos alternativos e excecoes.

### `03-rtm.md`

Define a matriz de rastreabilidade entre requisitos, casos de uso, casos de teste e evidencias. Deve orientar a meta de 100% de rastreabilidade dos requisitos funcionais.

### `04-plano-de-testes.md`

Consolida escopo, objetivos, abordagem, ambiente, criterios de entrada, criterios de saida, riscos, metricas e responsabilidades de teste.

### `05-casos-de-teste.md`

Lista casos de teste positivos, negativos, parametrizados, caixa branca, caixa preta, VCR/WireMock e de integracao.

### `06-estrategia-automacao.md`

Define como automatizar testes no projeto, incluindo niveis de teste, Testcontainers, VCR em Java com WireMock para Open Library, cobertura e proibicao de mocks tradicionais no projeto final.

### `07-ci-sonar-cobertura.md`

Define o pipeline esperado no GitHub Actions, integracao com SonarQube, quality gate e relatorio de cobertura.

### `08-gestao-bugs-e-evidencias.md`

Define como registrar bugs, severidade, prioridade, evidencias objetivas, reteste e regressao.

### `09-checklist-entrega.md`

Lista os itens finais de entrega: repositorio GitHub, codigo organizado, README, RTM, diagramas UML de sequencia, relatorio de cobertura, CI e Sonar configurados.

### `10-guia-prova-oral.md`

Consolida a preparacao para a prova oral. Deve mapear cada conceito relevante da disciplina para sua aplicacao no projeto, indicar evidencias objetivas e sugerir respostas curtas para perguntas provaveis.

Estrutura minima:

- Roteiro de apresentacao do projeto em 3 a 5 minutos.
- Mapa `conceito -> onde foi aplicado -> evidencia -> como explicar`.
- Perguntas provaveis da prova oral com respostas de 30 a 60 segundos.
- Pontos de atencao para nao confundir termos, como erro, defeito, falha, verificacao, validacao, severidade e prioridade.
- Lista de evidencias para abrir durante a apresentacao, como RTM, relatorio de cobertura, GitHub Actions, Sonar, testes e diagramas UML.

### `referencias-aulas.md`

Mapeia cada arquivo de aula e cada material escrito online aos documentos da base que eles sustentam. Deve indicar tambem quando um conteudo veio de imagem, SVG, tabela, bloco de codigo ou material online.

## Regras De Qualidade Da Base

- A base deve ser acionavel, nao apenas teorica.
- Cada documento deve conectar conceitos da aula a decisoes concretas do projeto.
- Cada documento principal deve conter uma secao curta chamada `Aplicacao na prova oral`, explicando como defender aquele tema verbalmente.
- A preparacao para prova oral deve sempre ligar tres elementos: conceito aprendido, implementacao no projeto e evidencia verificavel.
- Os slides locais devem ser tratados como fonte principal quando o assunto for requisito oficial, entrega ou avaliacao.
- Os materiais escritos online devem complementar e aprofundar os slides, especialmente em planejamento de testes, gestao de bugs, CI, Sonar, VCR e Testcontainers.
- Os identificadores de requisitos e testes devem ser consistentes entre documentos.
- Onde a descricao oficial for explicita, ela prevalece sobre inferencias.
- Onde houver lacuna, a base deve escolher uma opcao conservadora e marcar a decisao como premissa.
- Quando um conteudo depender de imagem ou diagrama, a base deve registrar uma descricao textual suficiente para nao depender apenas do arquivo visual.
- A documentacao deve evitar dependencia de ferramentas ainda nao configuradas no projeto.

## Premissas

- O projeto seguira o tema oficial, sem adaptacao de dominio.
- A Open Library API sera usada como integracao externa controlada para enriquecer o cadastro por ISBN.
- A primeira versao da base sera documental; o codigo do sistema sera planejado depois.
- A pasta atual ainda nao possui um repositorio Git inicializado.
- A data de entrega citada no material e a semana de 18 de maio de 2026 para as turmas NA e NB.
- Os materiais escritos online foram verificados como acessiveis em 17 de maio de 2026. Se algum link ficar indisponivel, a base deve continuar usando os HTMLs locais como fonte minima suficiente.
- Havera uma prova oral sobre os conceitos da disciplina e sobre como eles foram implementados no projeto. A base deve preparar o aluno para responder com exemplos concretos do proprio sistema.

## Fora Do Escopo Nesta Etapa

- Implementar o backend Spring Boot.
- Implementar o frontend.
- Configurar GitHub Actions.
- Configurar SonarQube.
- Criar testes automatizados reais.
- Publicar ou subir repositorio remoto.
- Ensaiar a prova oral com avaliacao em tempo real.

Essas tarefas pertencem a uma etapa posterior de plano e implementacao do projeto.

## Criterios De Aceite

A base inicial sera considerada pronta quando:

- Todos os arquivos listados na estrutura existirem em `docs/`.
- O documento de requisitos tiver IDs rastreaveis.
- A RTM tiver uma estrutura pronta para relacionar requisito, caso de uso, teste e evidencia.
- A estrategia de testes cobrir Testcontainers, VCR em Java com WireMock para Open Library, testes parametrizados, caixa branca, caixa preta e cobertura minima.
- O checklist de entrega refletir a descricao oficial do projeto.
- `10-guia-prova-oral.md` existir e conter roteiro, perguntas provaveis, respostas curtas e mapa conceito-aplicacao-evidencia.
- Cada documento principal em `docs/` conter uma secao `Aplicacao na prova oral`.
- `referencias-aulas.md` indicar quais aulas, materiais escritos online, imagens e diagramas sustentam cada parte da base.
- Os pontos extraidos de materiais online estiverem marcados como complemento, sem substituir requisitos oficiais dos slides locais.
