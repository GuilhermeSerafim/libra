# Gestao De Bugs E Evidencias

## Termos

| Termo | Significado |
| --- | --- |
| Erro | Acao humana incorreta durante analise, desenvolvimento, teste ou manutencao. |
| Defeito ou bug | Problema introduzido no artefato de software, como codigo, requisito, documentacao ou teste. |
| Falha | Comportamento incorreto observado quando o sistema executa e nao entrega o resultado esperado. |
| Evidencia objetiva | Registro verificavel que comprova uma execucao, resultado, decisao ou conclusao de qualidade. |

## Modelo De Registro De Bug

| Campo | Descricao |
| --- | --- |
| Titulo | Nome curto e claro do problema encontrado. |
| Ambiente | Sistema operacional, navegador, versao da aplicacao, banco, perfil de usuario e configuracoes relevantes. |
| Passos | Sequencia objetiva para reproduzir o problema. |
| Resultado esperado | Comportamento que deveria ocorrer segundo requisito, regra de negocio ou caso de teste. |
| Resultado obtido | Comportamento observado durante a execucao. |
| Evidencia | Print, video, log, relatorio de teste, pipeline, cobertura, RTM ou outro registro verificavel. |
| Severidade | Impacto tecnico ou de negocio causado pela falha. |
| Prioridade | Urgencia para corrigir o defeito dentro do planejamento do projeto. |
| Requisito afetado | Requisito funcional ou nao funcional relacionado ao problema. |

## Exemplo De Bug Com Evidencia

Este exemplo mostra o formato esperado de evidencia objetiva para defender um defeito na prova oral e orientar os registros reais do projeto.

| Campo | Exemplo |
| --- | --- |
| Titulo | Login com senha invalida retorna erro 500 em vez de mensagem controlada. |
| Ambiente | Aplicacao local, perfil de usuario cadastrado, banco de testes, navegador ou teste automatizado de autenticacao. |
| Passos | Acessar a tela de login, informar e-mail de usuario existente, preencher senha invalida e confirmar o login. |
| Resultado esperado | Sistema nega o acesso de forma controlada e exibe mensagem de credenciais invalidas, sem iniciar sessao. |
| Resultado obtido | Sistema retorna erro generico 500 e nao informa corretamente que o acesso foi negado. |
| Evidencia | Teste automatizado falhando, log da requisicao ou screenshot da tela com erro 500. |
| Severidade | Alta, pois afeta autenticacao e experiencia de acesso. |
| Prioridade | Alta, pois login e fluxo essencial para uso do sistema. |
| Requisito afetado | Requisito de autenticacao e controle de acesso do usuario. |

## Severidade X Prioridade

Severidade mede o impacto da falha no sistema ou no usuario. Uma falha que impede cadastrar livros, por exemplo, pode ter severidade alta porque bloqueia uma funcionalidade central do Gerenciador de Biblioteca Pessoal.

Prioridade mede a urgencia de correcao. Um defeito pode ser severo, mas ter prioridade menor se ocorre em um fluxo raro ou fora do escopo da entrega. Tambem pode haver um problema de baixa severidade com prioridade alta, como texto incorreto em uma tela principal da apresentacao.

Na prova oral, a explicacao principal e: severidade responde "qual e o impacto?", enquanto prioridade responde "quando devemos corrigir?".

## Ciclo De Vida

1. Aberto: o problema e registrado com titulo, ambiente, passos, resultado esperado, resultado obtido e evidencia inicial.
2. Validado: a equipe confirma se o problema e reproduzivel e se realmente representa um desvio.
3. Classificado: o defeito recebe severidade, prioridade e requisito afetado.
4. Corrigido: o codigo, teste, requisito ou documentacao e ajustado.
5. Retestado: o mesmo fluxo e executado novamente para confirmar a correcao.
6. Fechado com evidencia: o bug so e encerrado depois do reteste e da inclusao de evidencia objetiva.

## Evidencias Para O Projeto

- Relatorio JaCoCo demonstrando cobertura de testes.
- Execucao do GitHub Actions demonstrando pipeline automatizada.
- Dashboard do SonarQube com Quality Gate, bugs, vulnerabilidades, code smells, cobertura e duplicacao.
- Prints do SonarQube em `docs/evidencias/sonarqube/`, incluindo Quality Gate Passed.
- Prints das telas e dos fluxos principais funcionando.
- Logs e execucoes de testes com Testcontainers.
- RTM demonstrando rastreabilidade entre requisitos, casos de teste e evidencias.

## Aplicacao na prova oral

Uma boa resposta e diferenciar erro, defeito e falha em sequencia. O erro e uma acao humana, o defeito ou bug e o problema deixado no artefato, e a falha e o comportamento incorreto percebido durante a execucao do sistema.

Tambem e importante explicar que bug nao deve ser fechado apenas porque o codigo foi alterado. Ele deve ser retestado no mesmo fluxo, vinculado ao requisito afetado e encerrado somente com evidencia objetiva, como print, log, teste automatizado, pipeline, JaCoCo, Sonar ou registro na RTM.
