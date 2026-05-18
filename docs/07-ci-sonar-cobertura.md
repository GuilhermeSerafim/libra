# CI, Sonar E Cobertura

## Objetivo

O objetivo do CI, Sonar e cobertura e mover a qualidade da maquina local para o repositorio. Cada mudanca relevante deve ser validada automaticamente, com testes, relatorio de cobertura e analise estatica antes de ser considerada pronta.

No Gerenciador de Biblioteca Pessoal, essa estrategia ajuda a demonstrar que a entrega nao depende apenas de execucao manual. Ela cria evidencias objetivas para o desenvolvimento, para revisao em pull request e para a prova oral.

## Pipeline Esperado

Pipeline Java/Maven esperado no GitHub Actions:

```yaml
name: quality

on:
  push:
    branches: [main]
  pull_request:

permissions:
  contents: read

jobs:
  verify:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          persist-credentials: false
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - name: Run tests and coverage
        run: mvn -B verify
      - name: Upload JaCoCo report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: target/site/jacoco/
          if-no-files-found: error
      - name: SonarQube analysis
        if: github.event_name != 'pull_request' || github.event.pull_request.head.repo.full_name == github.repository
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        run: mvn -B sonar:sonar
```

Esse pipeline faz checkout do codigo, instala Java 21, usa cache do Maven, executa `mvn -B verify`, publica o relatorio de cobertura como artefato mesmo quando a execucao falha e depois roda a analise SonarQube com `mvn -B sonar:sonar`. O token e a URL do SonarQube devem ficar em secrets do GitHub Actions, como `SONAR_TOKEN` e `SONAR_HOST_URL`, nunca no repositorio. Tambem podem ser necessarias configuracoes como chave do projeto e URL do servidor no `pom.xml` ou nas propriedades do Maven. Os testes da Open Library devem rodar com VCR em Java usando WireMock em modo replay, sem depender de internet real durante o CI normal.

## Rotina Semanal De Atualizacao VCR

Alem do pipeline normal de qualidade, o projeto deve ter uma rotina semanal para atualizar o VCR/WireMock da Open Library.

| Fluxo | Gatilho | Comportamento |
| --- | --- | --- |
| CI normal | Push, pull request e execucao local de `mvn verify`. | Usa replay WireMock; nao chama a Open Library real. |
| Atualizacao VCR | Agendada uma vez por semana e acionavel manualmente. | Chama a Open Library real, normaliza e atualiza a fixture/resposta VCR, roda testes em replay e permite revisar diff. |

Exemplo conceitual de agendamento:

```yaml
on:
  schedule:
    - cron: '0 9 * * 1'
  workflow_dispatch:
```

O comando exato depende da implementacao dos testes, mas a separacao deve ser preservada: replay no fluxo normal; record/update apenas na rotina semanal.

## Cobertura

A cobertura deve ser medida com JaCoCo. A meta minima obrigatoria e 80%, com relatorio HTML para leitura humana e XML para integracao com SonarQube.

Cobertura e uma metrica de apoio, nao uma garantia completa de qualidade. Ela mostra o quanto do codigo foi exercitado, mas nao substitui rastreabilidade de requisitos, casos de teste bem escritos, validacao das regras de negocio e analise dos cenarios criticos.

Evidencias esperadas:

| Evidencia | Uso |
| --- | --- |
| Relatorio HTML JaCoCo | Consulta visual da cobertura por pacote, classe e metodo. |
| Relatorio XML JaCoCo | Integracao com SonarQube. |
| Meta minima obrigatoria de 80% | Criterio objetivo para acompanhar evolucao da qualidade. |
| Testes associados a requisitos | Confirmar que cobertura nao substitui rastreabilidade. |
| Fixture/resposta VCR em replay | Confirmar que a integracao Open Library e testada como VCR Java de forma reproduzivel no CI normal. |
| Rotina semanal de atualizacao VCR | Confirmar que o projeto combate cassete eterno chamando a Open Library real de forma controlada. |

## SonarQube

SonarQube complementa os testes automatizados com analise estatica. A ferramenta procura problemas que podem nao aparecer diretamente em um teste funcional.

| Indicador | O que observa |
| --- | --- |
| Bugs | Possiveis falhas de comportamento detectadas por analise estatica. |
| Code smells | Problemas de manutencao, legibilidade ou complexidade. |
| Duplicacao | Trechos repetidos que aumentam custo de evolucao. |
| Coverage | Percentual de codigo coberto pelos testes, geralmente importado do JaCoCo. |
| Quality gate | Conjunto de criterios minimos para considerar a mudanca saudavel. |

## Quality Gate No Pull Request

O Quality Gate no pull request deve impedir que codigo novo entre no projeto com problemas relevantes. Um PR e considerado saudavel quando:

1. Todos os testes passam no GitHub Actions.
2. A cobertura fica igual ou acima da meta minima obrigatoria de 80%.
3. O Sonar nao aponta blockers relevantes no codigo novo.
4. Nao ha degradacao importante de manutencao, duplicacao ou confiabilidade.

O Quality Gate nao substitui revisao humana, mas cria uma barreira automatica para problemas objetivos e repetitivos.

A aplicacao efetiva do Quality Gate depende da configuracao do projeto no SonarQube e da decoracao do pull request. Portanto, o pipeline executa os testes, gera cobertura e envia a analise; a configuracao do SonarQube define as regras, interpreta o relatorio JaCoCo e integra o resultado ao PR para permitir ou bloquear a mudanca conforme os criterios definidos.

## Aplicacao na prova oral

Resposta sugerida:

"O CI leva a qualidade da maquina local para o repositorio. A cada push ou pull request, o GitHub Actions executa `mvn -B verify`, roda os testes, gera cobertura com JaCoCo e envia a analise com `sonar:sonar` usando secrets como `SONAR_TOKEN` e `SONAR_HOST_URL`. A meta minima obrigatoria e 80%, com relatorios HTML e XML. O SonarQube complementa os testes analisando bugs, code smells, duplicacao e cobertura. Para Open Library, o fluxo normal usa VCR em Java com WireMock em replay, entao o CI nao depende da internet real. Para evitar cassete eterno, existe uma rotina semanal que chama a Open Library real, normaliza a resposta, atualiza a fixture VCR, roda os testes em replay e commita a fixture se houver mudanca. No pull request, o Quality Gate depende da configuracao do projeto SonarQube e da decoracao de PR para indicar se o codigo novo esta saudavel antes de entrar na branch principal."
