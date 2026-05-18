# Estrategia De Automacao

## Principio

A automacao de testes do Gerenciador de Biblioteca Pessoal deve provar comportamento com fidelidade, nao apenas executar codigo isolado. Por isso, a estrategia privilegia testes que se aproximam do ambiente real da aplicacao: Spring Boot, MongoDB, regras de negocio e integracoes controladas.

No projeto final, mocks tradicionais estao proibidos como estrategia de validacao. Eles podem dar uma falsa sensacao de seguranca quando substituem persistencia, HTTP ou fluxos que precisam ser verificados de ponta a ponta. A automacao deve gerar evidencias confiaveis para desenvolvimento, entrega continua e defesa na prova oral.

## Testcontainers

Testcontainers deve ser usado nos testes de integracao para iniciar um container real do MongoDB. Assim, repositories e services rodam contra um banco controlado, mas verdadeiro, em vez de uma simulacao.

Fluxo esperado:

1. A suite de integracao inicia um container MongoDB antes dos testes.
2. A aplicacao Spring Boot recebe a URI do banco criado pelo Testcontainers.
3. Repositories e services executam operacoes reais de persistencia, consulta, atualizacao e remocao.
4. Ao final da suite, o ambiente e destruido.

Essa abordagem reduz a diferenca entre a maquina local e o CI, porque ambos executam os testes contra uma dependencia real e descartavel. As evidencias esperadas sao logs da execucao, testes passando e pipeline confirmando que a persistencia foi validada em ambiente controlado.

## VCR Em Java Com WireMock

VCR deve ser usado para a dependencia HTTP externa do projeto: a busca de metadados de livros por ISBN na Open Library API. Como o projeto e em Java, a forma indicada pela aula para implementar esse VCR e o **WireMock**.

O objetivo e tornar respostas HTTP reproduziveis. Com WireMock, isso pode aparecer como stub programatico no teste ou como stub declarativo em arquivo JSON. Na pratica, esses arquivos funcionam como o replay/cassete do VCR: o teste aponta o cliente HTTP para o servidor WireMock local e nao depende da internet real.

Fluxo esperado:

1. O teste aciona o cliente `OpenLibraryClient` com um ISBN conhecido.
2. O WireMock responde com um stub programatico ou arquivo JSON versionado.
3. A resposta e revisada para remover dados sensiveis, se houver.
4. O CI executa os testes em modo replay, consumindo a resposta controlada.
5. Se o contrato da API mudar, a fixture/resposta VCR e atualizada de forma intencional e revisada.

## Execucao Do VCR/WireMock

O VCR/WireMock deve ter dois fluxos separados.

| Fluxo | Quando executa | Acessa internet? | Objetivo |
| --- | --- | --- | --- |
| Replay normal | A cada push, pull request ou `mvn verify`. | Nao. | Testar o codigo da aplicacao contra respostas HTTP controladas. |
| Atualizacao semanal | Uma vez por semana, em rotina agendada ou manual. | Sim, chama a Open Library real. | Atualizar fixture/resposta VCR para evitar cassete eterno. |

No replay normal, o teste sobe o WireMock local, aponta o `OpenLibraryClient` para esse servidor local e valida se a aplicacao trata corretamente a resposta. Esse fluxo testa o codigo do projeto, nao a disponibilidade da Open Library naquele momento.

Na atualizacao semanal, a suite autorizada chama a Open Library real, normaliza e atualiza a fixture/resposta VCR, executa os testes em replay e gera um diff para revisao. Esse fluxo combate o anti-pattern de cassete eterno sem transformar todo teste em dependencia de internet.

Cuidados obrigatorios:

1. Sanitizar tokens, emails, nomes reais, chaves de API e qualquer dado sensivel.
2. Manter secrets fora do repositorio.
3. Versionar apenas fixtures, respostas ou cassetes seguros.
4. Evitar chamadas reais para a internet no CI normal.
5. Executar atualizacao online do VCR/WireMock uma vez por semana.

As evidencias esperadas sao a fixture/resposta VCR do WireMock, testes automatizados consumindo essa resposta e configuracao de secrets fora do repositorio quando houver necessidade de credenciais. A Open Library normalmente dispensa credencial para consultas publicas, mas a regra de seguranca continua valendo para qualquer integracao futura.

Na defesa, a formulacao correta e: **VCR em Java com WireMock**. Isso atende o pedido de VCR da descricao oficial e segue o slide que apresenta WireMock como a implementacao Java desse replay HTTP.

## Quando Usar VCR/WireMock E Quando Usar Testcontainers

A decisao deve seguir o alvo real do teste. VCR/WireMock controla chamadas HTTP externas; Testcontainers sobe servicos reais de infraestrutura, como banco de dados.

| Criterio | VCR/WireMock | Testcontainers |
| --- | --- | --- |
| Alvo do teste | APIs HTTP externas, como Open Library API. | Bancos, filas, cache e outros servicos de infraestrutura. |
| Uso no projeto | Busca de metadados por ISBN sem depender da internet real. | Persistencia de usuarios, sessoes e livros em MongoDB real. |
| Velocidade | Mais rapido, pois roda localmente sem Docker obrigatorio. | Mais lento, pois precisa iniciar container e aguardar o servico ficar pronto. |
| Fidelidade | Alta para payload, contrato e tratamento HTTP; baixa para a logica interna da API externa. | Alta para comportamento real do servico usado, como driver, conexao, query e persistencia. |
| Requer Docker | Nao necessariamente. | Sim. |
| Ideal para | Testes de unidade ou integracao de clientes HTTP. | Testes de integracao com persistencia e infraestrutura real. |
| Manutencao | Atualizar fixture/resposta VCR uma vez por semana ou quando o contrato da API mudar. | Manter imagem e configuracao do container alinhadas ao ambiente do projeto. |

No Gerenciador de Biblioteca Pessoal, isso significa: **Open Library usa VCR/WireMock**; **MongoDB usa Testcontainers**.

## Regra Sobre Mocks

A regra do projeto e nao usar mocks tradicionais como substitutos das dependencias relevantes na entrega final.

Preferencias:

| Situacao | Estrategia preferida |
| --- | --- |
| Persistencia MongoDB | Testcontainers |
| Dependencia HTTP externa | VCR em Java com WireMock |
| Logica de dominio pura | Testes unitarios sem infraestrutura |

Para a entrega final, o argumento tecnico mais forte e mostrar que as regras foram validadas com dependencias reais ou reproduziveis: MongoDB real em Testcontainers e respostas HTTP externas em VCR/WireMock.

## Camadas Automatizadas

| Camada | Ferramenta | Papel na estrategia |
| --- | --- | --- |
| Unitarios | JUnit 5 | Validar regras de dominio puras, validacoes e cenarios pequenos sem infraestrutura. |
| Integracao | Spring Boot Test + Testcontainers | Validar fluxo da aplicacao com contexto Spring e MongoDB real em container. |
| HTTP externo | VCR/WireMock | Reproduzir respostas externas gravadas ou declaradas, sem internet real no CI. |
| Cobertura | JaCoCo | Medir linhas e ramos exercitados pelos testes e gerar relatorios HTML/XML. |
| CI | GitHub Actions | Executar testes e cobertura automaticamente a cada push ou pull request. |

## Aplicacao na prova oral

Resposta sugerida:

"A automacao do projeto foi pensada para provar comportamento com fidelidade. Como mocks tradicionais estao proibidos no projeto final, para persistencia usamos Testcontainers, que sobe um MongoDB real durante os testes de integracao. Isso valida consultas, gravacoes e configuracao da aplicacao em um ambiente controlado e descartavel. Para a Open Library API, usamos VCR em Java com WireMock. No CI normal, ele roda em replay sem internet real; uma vez por semana, atualizamos a fixture/resposta VCR batendo na Open Library para evitar cassete eterno. Assim, os testes ficam confiaveis sem deixar o contrato externo abandonado."
