# Plano De Testes

## Escopo

O plano de testes cobre a validacao dos requisitos funcionais RF-01 a RF-09 do Gerenciador de Biblioteca Pessoal, incluindo cadastro de usuarios, autenticacao, sessao, cadastro, listagem, atualizacao, exclusao de livros, restricao das operacoes aos dados do usuario autenticado e pre-visualizacao de metadados por ISBN via Open Library API.

Tambem fazem parte do escopo os testes automatizados de unidade, integracao, regressao, caixa branca, caixa preta e VCR em Java com WireMock para chamada HTTP externa, com evidencias ligadas aos casos de teste TC-001 a TC-025 e a RTM.

Ficam fora do escopo deste plano pagamentos, recomendacoes de livros, outras integracoes externas alem da Open Library, publicacao em producao, testes manuais exploratorios extensos, testes de carga, testes de compatibilidade profunda entre navegadores e validacoes de infraestrutura de producao que nao sejam exigidas pelo projeto academico.

## Objetivos

- Validar que todos os RFs definidos em `docs/01-requisitos.md` possuem testes planejados e rastreaveis.
- Demonstrar cobertura minima de 80% conforme RNF-05.
- Reduzir regressao em funcionalidades criticas de autenticacao, sessao e CRUD de livros.
- Validar persistencia real com MongoDB usando Testcontainers, evitando dependencia de banco local.
- Validar a consulta externa de ISBN com VCR em Java usando WireMock, evitando dependencia de internet real no CI.
- Registrar evidencias de execucao, cobertura, pipeline e rastreabilidade para apoiar a prova oral.

## Abordagem

| Tecnica | Aplicacao no projeto | Exemplos esperados |
|---|---|---|
| Unitario | Validar regras isoladas de servicos, validadores e componentes de dominio. | Validacao de campos obrigatorios, senha invalida e regra de autorizacao. |
| Integracao | Validar comunicacao entre camadas e persistencia real. | Cadastro de livro persistido no MongoDB com Testcontainers. |
| Caixa branca | Validar caminhos internos e decisoes de codigo conhecidas pela equipe. | Regra que impede atualizacao de livro de outro usuario. |
| Caixa preta | Validar entradas e saidas observaveis sem depender da implementacao interna. | Login valido, credenciais invalidas, listagem vazia e mensagens esperadas. |
| Regressao | Reexecutar cenarios principais apos alteracoes para detectar quebras. | Cadastro, login, CRUD de livros e restricoes por usuario autenticado. |
| VCR/WireMock | Gravar, declarar ou reproduzir respostas HTTP externas de forma controlada. | Busca de livro por ISBN na Open Library sem depender de rede real no CI. |

## Ambiente

Os testes devem ser executados em ambiente Java/Spring Boot, com MongoDB em Testcontainers para testes de persistencia e integracao. A execucao automatizada deve ocorrer em GitHub Actions, com medicao de cobertura por JaCoCo e analise de qualidade por SonarQube.

A integracao com Open Library API deve usar VCR em Java com WireMock para gravar, declarar ou reproduzir respostas HTTP seguras nos testes automatizados, reduzindo instabilidade e evitando dependencia direta da rede durante a validacao automatizada.

## Criterios De Entrada

- Requisitos funcionais, casos de uso e RTM revisados.
- Casos de teste TC-001 a TC-025 definidos e vinculados aos RFs e UCs.
- Ambiente local ou de CI preparado para executar testes Java/Spring Boot.
- Dependencias de Testcontainers configuradas para testes com MongoDB.
- Dependencias do WireMock e fixture/resposta VCR configuradas para testes da Open Library API.
- Segredos e credenciais fora do codigo-fonte versionado.
- Dados de teste definidos sem secrets reais.

## Criterios De Suspensao

- Falha de ambiente que impeca a execucao confiavel dos testes.
- Build quebrado antes da execucao dos testes.
- Testcontainers ou MongoDB indisponivel no ambiente de execucao.
- Pipeline sem acesso as dependencias necessarias.
- Taxa de falha critica acima de 30% por problema de ambiente.
- Exposicao de segredo, token ou credencial em log, arquivo versionado ou configuracao.
- Divergencia critica entre requisitos, casos de uso, casos de teste e RTM.

## Criterios De Saida

- Todos os testes planejados para RF-01 a RF-09 passam.
- Pipeline principal executado com testes aprovados.
- Cobertura minima de 80% demonstrada por JaCoCo.
- Defeitos criticos e altos zerados ou formalmente justificados.
- RTM completa, com todos os requisitos funcionais ligados a casos de uso, casos de teste e evidencias.
- Evidencias registradas para apoiar apresentacao e prova oral.

## Riscos E Mitigacoes

| Risco | Impacto | Mitigacao |
|---|---|---|
| Testes muito acoplados a implementacao | Dificulta manutencao e gera quebras sem mudanca real de comportamento. | Priorizar assercoes de comportamento e usar caixa branca apenas em regras internas relevantes. |
| Diferenca entre Mongo local e CI | Testes passam localmente e falham no pipeline. | Usar MongoDB em Testcontainers como fonte padronizada para integracao. |
| Cobertura abaixo de 80% | Nao atendimento ao RNF-05 e fragilidade na defesa oral. | Monitorar JaCoCo no pipeline e incluir testes para fluxos criticos e alternativos. |
| Secrets vazados | Risco de seguranca e nao atendimento ao RNF-08. | Usar variaveis de ambiente, secrets do GitHub Actions e revisao de arquivos versionados. |
| Cassete ou resposta WireMock desatualizada | Teste deixa de refletir o contrato atual da API externa. | Regravar ou atualizar o replay de forma controlada quando o contrato da Open Library mudar e revisar o diff. |
| RTM desatualizada | Perda de rastreabilidade entre requisito, teste e evidencia. | Atualizar RTM sempre que RF, UC ou TC for criado, alterado ou removido. |
| Testes acoplados entre si | Falhas intermitentes | Limpar dados entre testes e isolar cenarios. |

## Metricas

- Percentual de cobertura de testes medido por JaCoCo.
- Quantidade e percentual de testes passando no pipeline.
- Defeitos por severidade: critico, alto, medio e baixo.
- Tempo total de execucao do pipeline.
- Percentual de requisitos com rastreabilidade completa na RTM.

## Aplicacao na prova oral

Na prova oral, o plano de testes deve ser explicado por criterios mensuraveis. A qualidade nao e defendida apenas dizendo que o sistema foi testado: ela e demonstrada com cobertura minima de 80%, RTM completa ligando RFs, UCs, TCs e evidencias, pipeline executado e defeitos criticos iguais a zero. Esses criterios mostram que a equipe validou comportamento, persistencia, regressao e integracao HTTP externa de forma objetiva. Para a Open Library, a defesa deve destacar que VCR em Java usa WireMock para replay HTTP, enquanto Testcontainers valida MongoDB real.
