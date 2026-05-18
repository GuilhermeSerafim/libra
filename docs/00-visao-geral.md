# Visao Geral Do Projeto

## Projeto

O projeto oficial e o **Gerenciador de Biblioteca Pessoal**, uma aplicacao para organizar livros de usuarios autenticados em uma interface web funcional e responsiva.

## Objetivo De Qualidade

O objetivo de qualidade e demonstrar que o sistema atende aos requisitos funcionais e nao funcionais com rastreabilidade, testes automatizados, analise estatica e evidencias de entrega. A meta minima de cobertura e **80%**, medida com JaCoCo, acompanhada por SonarQube e validada em GitHub Actions.

## Escopo Funcional

O escopo funcional contempla cadastro de usuarios, autenticacao, gerenciamento de sessao, CRUD de livros e pre-visualizacao opcional de metadados por ISBN usando a Open Library API. As operacoes sobre livros devem ficar restritas ao usuario autenticado, garantindo que cada pessoa consulte, cadastre, atualize e exclua apenas os proprios registros.

## Stack Oficial

- Spring Boot Java.
- Arquitetura MVC.
- MongoDB para persistencia.
- Interface web responsiva.
- Testcontainers para testes de persistencia.
- VCR para chamadas de API externas, implementado em Java com WireMock.
- JaCoCo para cobertura.
- SonarQube para analise estatica.
- GitHub Actions para integracao continua.

## Entregaveis Principais

- Aplicacao Gerenciador de Biblioteca Pessoal.
- README com instrucoes de execucao, testes e evidencias.
- RTM com rastreabilidade dos requisitos funcionais.
- Diagramas UML de sequencia para fluxos principais.
- Pipeline GitHub Actions executando testes, cobertura e analise de qualidade.
- Evidencias de cobertura minima de 80% com JaCoCo.
- Evidencias de SonarQube.

## Premissas

- A descricao oficial do projeto prevalece em caso de conflito com materiais complementares.
- O banco oficial e MongoDB.
- Testes de persistencia devem usar Testcontainers.
- A integracao externa escolhida e a Open Library API para buscar metadados de livros por ISBN.
- Chamadas externas devem ser testadas com VCR em Java usando WireMock, gravando ou declarando respostas HTTP de forma controlada.
- Mocks tradicionais estao proibidos no projeto final; para persistencia usar Testcontainers e para API externa usar VCR com WireMock.
- Secrets nao devem ser versionados e devem ser protegidos no ambiente de CI.

## Aplicacao na prova oral

Uma resposta esperada na prova oral deve explicar que qualidade no projeto significa provar, com evidencias, que o Gerenciador de Biblioteca Pessoal cumpre seus requisitos. A rastreabilidade liga requisito, caso de uso, teste, evidencia de cobertura, analise Sonar e pipeline CI, permitindo justificar tecnicamente que funcionalidades como cadastro de livros, autenticacao e restricao por usuario foram planejadas, testadas e validadas.
