# Guia Para Prova Oral

## Roteiro De 3 A 5 Minutos

1. Apresentar o dominio: o projeto e um Gerenciador de Biblioteca Pessoal para organizar livros do usuario.
2. Explicar as funcionalidades principais: cadastro de usuarios, autenticacao, gerenciamento de sessao, CRUD de livros, restricao dos livros ao usuario autenticado atual e pre-visualizacao por ISBN com Open Library.
3. Mostrar requisitos e RTM: cada requisito possui relacao com caso de uso, caso de teste, evidencia e conceito aplicado.
4. Explicar os testes: unidade, integracao, caixa branca, caixa preta, cenarios com entradas validas, invalidas e limites, Testcontainers e VCR em Java com WireMock.
5. Abrir evidencias: JaCoCo, GitHub Actions, Sonar, Testcontainers, VCR/WireMock, RTM, casos de teste e diagramas UML.
6. Fechar com qualidade: mostrar que a qualidade vai dos requisitos ate a entrega, com rastreabilidade, verificacao, validacao e metricas.

## Mapa Conceito -> Aplicacao -> Evidencia -> Como Explicar

| Conceito | Aplicacao | Evidencia | Como Explicar |
| --- | --- | --- | --- |
| Qualidade de software | Garantir que o sistema atende requisitos funcionais e nao funcionais. | RTM, testes, cobertura, Sonar e pipeline. | Qualidade e demonstrada por funcionamento, rastreabilidade e metricas. |
| Verificacao | Conferir se o produto esta sendo construido corretamente. | Testes automatizados, revisao de codigo, Sonar e GitHub Actions. | Verificacao compara artefatos e execucoes com o que foi especificado. |
| Validacao | Confirmar se o produto atende a necessidade do usuario. | Casos de uso, fluxos principais e prints da aplicacao. | Validacao pergunta se estamos construindo o produto certo para o usuario. |
| Caixa branca | Testes baseados na estrutura interna do codigo. | Testes unitarios, cobertura JaCoCo e cenarios de branches. | Olha para caminhos internos, condicoes e regras implementadas. |
| Caixa preta | Testes baseados em entrada e saida sem depender do codigo interno. | Casos de teste funcionais e validacoes de campos. | Avalia comportamento esperado a partir dos requisitos. |
| Variacao de entradas | Tecnica para exercitar varios conjuntos de dados, como entradas validas, invalidas e limites. | Casos de teste e cenarios documentados. | Ajuda a ampliar cenarios equivalentes sem afirmar uma estrategia especifica de implementacao. |
| Testcontainers | Teste de integracao com dependencia real em container. | Logs e testes usando banco ou servico em container. | Aproxima o teste do ambiente real sem depender de instalacao manual. |
| Open Library API | Integracao externa para buscar metadados de livros por ISBN. | UC-08, RF-09 e testes TC-022 a TC-025. | Melhora o cadastro sem salvar automaticamente: o endpoint faz preview e a persistencia continua no CRUD de livros do usuario. |
| VCR/WireMock | Gravacao, declaracao e reproducao de chamadas HTTP externas em Java. | Fixture/resposta VCR e testes do cliente Open Library. | WireMock e a forma Java indicada pela aula para cumprir VCR sem depender da internet real no CI. |
| RTM | Rastrear requisito, caso de uso, teste, evidencia e conceito aplicado. | docs/03-rtm.md. | Ajuda a provar que nenhum requisito importante ficou sem teste. |
| CI | Execucao automatica da verificacao a cada push ou pull request. | GitHub Actions. | Reduz risco de entregar codigo sem testes passando. |
| Cobertura | Percentual do codigo exercitado pelos testes. | Relatorio JaCoCo. | Ajuda a medir alcance dos testes, mas nao garante qualidade sozinho. |
| SonarQube | Analise estatica de qualidade do codigo. | Dashboard SonarQube. | Encontra bugs, vulnerabilidades, duplicacoes e code smells. |
| Erro/defeito/falha | Classificar problemas de qualidade. | Registro de bug com evidencia. | Erro e humano, defeito fica no artefato e falha aparece na execucao. |
| Severidade/prioridade | Classificar impacto e urgencia de bugs. | Registro de bug, backlog ou evidencia de correcao. | Severidade e impacto; prioridade e ordem de correcao. |

## Perguntas Provaveis

| Pergunta | Resposta curta |
| --- | --- |
| Por que comecar pelo backend REST? | Porque o nucleo avaliado esta em Spring Boot, MongoDB, sessao, testes, VCR, Testcontainers, JaCoCo, SonarQube e CI; o frontend moderno pode consumir a API depois sem alterar as regras de negocio. |
| Por que usar RTM? | Para provar rastreabilidade entre requisitos, casos de uso, casos de teste, evidencias e conceitos aplicados. |
| Qual e a diferenca entre verificacao e validacao? | Verificacao confere se foi construido corretamente; validacao confere se atende a necessidade do usuario. |
| Onde voce aplicou caixa branca? | Nos testes que analisam regras internas, branches, excecoes e cobertura do codigo. |
| Onde voce aplicou caixa preta? | Nos testes funcionais baseados em entradas, saidas e requisitos, sem depender da estrutura interna. |
| Por que usar Testcontainers? | Para testar integracao com dependencia realista em container, reduzindo diferenca entre teste e execucao real. |
| Por que usar VCR? | Porque a descricao oficial exige VCR para chamadas externas; em Java, a aula apresenta WireMock como a forma de fazer esse replay HTTP. |
| Quando usar VCR/WireMock e quando usar Testcontainers? | VCR/WireMock para API HTTP externa, como Open Library; Testcontainers para servico real de infraestrutura, como MongoDB. |
| Como o VCR executa no projeto? | No fluxo normal ele roda em replay a cada push, PR ou `mvn verify`; uma rotina semanal bate na Open Library real para atualizar a fixture/resposta VCR. |
| WireMock contradiz a proibicao de mocks? | Nao. A proibicao e contra mocks tradicionais substituindo regras, banco ou dependencias internas; WireMock controla uma API HTTP externa, que e justamente o caso permitido pelo VCR. |
| Por que usar Sonar se ja existem testes? | Porque testes verificam comportamento, enquanto Sonar analisa qualidade estatica, riscos, duplicacao e manutencao. |
| Como saber que a qualidade e suficiente? | Pela combinacao de requisitos rastreados, testes passando, cobertura minima, CI, Sonar e evidencias dos fluxos principais. |

## Evidencias Para Abrir Na Apresentacao

- docs/03-rtm.md.
- docs/05-casos-de-teste.md.
- Relatorio JaCoCo.
- GitHub Actions.
- SonarQube.
- Logs ou testes com Testcontainers.
- Fixture/resposta VCR e testes da Open Library API.
- Evidencia da rotina semanal de atualizacao VCR.
- Diagramas UML de sequencia.
- Secao "Exemplo De Bug Com Evidencia" em docs/08-gestao-bugs-e-evidencias.md como exemplo de registro e evidencia objetiva.

## Aplicacao na prova oral

Use este guia como trilha principal da apresentacao: comece pelo problema, conecte requisitos e RTM, abra as evidencias de teste e finalize explicando como cada conceito de qualidade aparece no projeto.

## Pontos Para Nao Confundir

- Cobertura alta nao e suficiente sozinha; ela mostra alcance, mas nao garante bons asserts nem todos os cenarios de negocio.
- Mocks tradicionais estao proibidos no projeto final; a defesa deve citar Testcontainers para persistencia e VCR em Java com WireMock para API externa.
- Testcontainers nao precisa aparecer em todos os testes; ele e mais adequado para integracao.
- VCR/WireMock nao substitui o MongoDB nem as regras do dominio; ele controla apenas a chamada HTTP externa.
- A pergunta correta nao e "qual ferramenta e melhor", mas "qual dependencia estou testando": HTTP externo pede VCR/WireMock; infraestrutura real pede Testcontainers.
- VCR em replay roda em toda alteracao; atualizacao online do VCR roda uma vez por semana para evitar cassete eterno.
- Sonar nao e teste funcional; ele faz analise estatica de qualidade.
- Severidade nao e prioridade; severidade mede impacto e prioridade mede urgencia de correcao.
