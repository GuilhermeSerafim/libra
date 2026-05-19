# CI, Sonar E Cobertura

## Objetivo

O objetivo do CI, Sonar e cobertura e mover a qualidade da maquina local para o repositorio. Cada mudanca relevante deve ser validada automaticamente, com testes, relatorio de cobertura e analise estatica antes de ser considerada pronta.

No Gerenciador de Biblioteca Pessoal, essa estrategia ajuda a demonstrar que a entrega nao depende apenas de execucao manual. Ela cria evidencias objetivas para o desenvolvimento, para revisao em pull request e para a prova oral.

## Pipeline Esperado

Pipeline de qualidade esperado no GitHub Actions:

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
      - uses: actions/setup-node@v4
        with:
          node-version: '22'
          cache: npm
          cache-dependency-path: frontend/package-lock.json
      - name: Run backend tests and coverage
        run: mvn -B verify
      - name: Upload JaCoCo report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: target/site/jacoco/
          if-no-files-found: error
      - name: Install frontend dependencies
        working-directory: frontend
        run: npm ci
      - name: Lint frontend
        working-directory: frontend
        run: npm run lint
      - name: Build frontend
        working-directory: frontend
        run: npm run build
      - name: Upload frontend build
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: frontend-dist
          path: frontend/dist/
          if-no-files-found: error
      - name: SonarQube analysis
        if: github.event_name != 'pull_request' || github.event.pull_request.head.repo.full_name == github.repository
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        run: |
          if [ -z "$SONAR_TOKEN" ] || [ -z "$SONAR_HOST_URL" ]; then
            echo "SonarQube secrets not configured; skipping analysis."
            exit 0
          fi

          if [[ "$SONAR_HOST_URL" == http://localhost* || "$SONAR_HOST_URL" == http://127.0.0.1* ]]; then
            echo "Local SonarQube URLs are not reachable from GitHub-hosted runners; skipping analysis."
            exit 0
          fi

          mvn -B sonar:sonar -Dsonar.qualitygate.wait=true
```

Esse pipeline faz checkout do codigo, instala Java 21 e Node 22, usa cache do Maven e do npm, executa `mvn -B verify`, publica o relatorio de cobertura JaCoCo, instala as dependencias do frontend, executa `npm run lint`, executa `npm run build` e publica o build do frontend como artefato. Assim, o CI valida tanto o backend quanto a interface React antes da entrega.

A analise SonarQube tambem esta prevista no workflow, mas depende de um servidor acessivel pelo GitHub Actions. Como o SonarQube da entrega foi configurado localmente em Docker, uma URL `localhost` nao pode ser acessada por um runner hospedado no GitHub. Por isso, o workflow so envia a analise quando `SONAR_TOKEN` e `SONAR_HOST_URL` apontarem para um servidor real/acessivel; caso contrario, a etapa informa o motivo e nao derruba o CI. As evidencias do SonarQube local ficam registradas em `docs/evidencias/sonarqube/`.

O token e a URL do SonarQube devem ficar em secrets do GitHub Actions, como `SONAR_TOKEN` e `SONAR_HOST_URL`, nunca no repositorio. Tambem podem ser necessarias configuracoes como chave do projeto e URL do servidor no `pom.xml` ou nas propriedades do Maven. Os testes da Open Library devem rodar com VCR em Java usando WireMock em modo replay, sem depender de internet real durante o CI normal.

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

A aplicacao efetiva do Quality Gate depende da configuracao do projeto no SonarQube, da decoracao do pull request e de um servidor SonarQube acessivel ao runner. Quando o SonarQube esta local, o CI continua validando testes, cobertura e build do frontend, enquanto a evidencia local do dashboard demonstra a analise estatica exigida na entrega. Quando houver um servidor SonarQube acessivel, o mesmo workflow envia a analise e espera o Quality Gate com `-Dsonar.qualitygate.wait=true`.

## Aplicacao na prova oral

Resposta sugerida:

"O CI leva a qualidade da maquina local para o repositorio. A cada push ou pull request, o GitHub Actions executa `mvn -B verify`, roda os testes do backend, gera cobertura com JaCoCo, valida o frontend com `npm run lint` e `npm run build` e guarda relatorios como artefatos. A meta minima obrigatoria e 80%, com relatorios HTML e XML. O SonarQube complementa os testes analisando bugs, code smells, duplicacao e cobertura. Como o SonarQube desta entrega esta local em Docker, o GitHub Actions so consegue enviar analise se houver uma URL SonarQube acessivel pela internet ou por runner proprio; por isso mantemos as evidencias locais em `docs/evidencias/sonarqube/`. Para Open Library, o fluxo normal usa VCR em Java com WireMock em replay, entao o CI nao depende da internet real. Para evitar cassete eterno, existe uma rotina semanal que chama a Open Library real, normaliza a resposta, atualiza a fixture VCR, roda os testes em replay e commita a fixture se houver mudanca."
