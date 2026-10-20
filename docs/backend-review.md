# Revisão de lógica e regras de negócio — 20/10/2026

## Calendário semanal e mensal

O total de ocorrências do dia é a união entre as ocorrências atualmente programadas
e as conclusões confirmadas desse dia, identificadas por hábito e turno.
Uma conclusão de hábito arquivado ou de turno removido continua no histórico,
mas não substitui outra atividade ainda pendente.

Exemplo: A concluída e depois arquivada, B programada e pendente.
Antes, `max(1 programada, 1 concluída)` produzia 100%. Agora são duas ocorrências,
uma concluída, com 50% e estado parcial. Uma conclusão ainda programada é contada
uma única vez.

## Consulta de progresso

`GET /api/v1/progress` usa as datas das conclusões confirmadas e o dia atual no fuso
do usuário para calcular as sequências, como as consultas de Hoje e Evolução.
Assim, a sequência expira após um dia inteiro sem atividade, mesmo que não haja
nova conclusão ou cancelamento para atualizar o progresso persistido. XP e nível
continuam vindo do saldo persistido.

## Reativação de hábitos

Reativar um hábito exige uma área pertencente ao usuário e não arquivada.
É permitido reativar e transferir para outra área válida na mesma atualização.
Uma tentativa recusada não modifica o hábito.

## Custo do cálculo de sequências

O cálculo em memória deixou de ordenar as datas. Usa um conjunto de dias para
identificar o início de cada sequência e percorrer cada dia uma vez. O custo
esperado passa de O(n log n) para O(n), com espaço O(n), incluindo eliminação de
duplicatas e descarte de datas futuras. Não é uma medição de tempo de resposta:
as consultas ao banco ainda carregam o histórico de datas e mantêm sua ordenação.

## Escopo

Foram inspecionados os fluxos de hábitos, áreas, conclusões/cancelamentos, XP,
sequências, calendário, sono e perfil, além das consultas e restrições do banco.
As mudanças preservam os contratos HTTP e não exigem migrações de banco.

## Validação

- Todos os 100 arquivos de produção e os 19 arquivos de testes foram compilados
  para Java 21 com Eclipse ECJ 3.39.0, sem erros.
- A suíte JUnit foi executada com Maven Surefire 3.5.6 e JDK 21:
  45 testes encontrados, 43 aprovados, zero falhas e zero erros.
- Os dois testes de integração com PostgreSQL/Testcontainers foram ignorados,
  pois o Docker não estava em execução. Concorrência e persistência real não
  foram validadas nesta execução.
- Seis testes foram adicionados, incluindo regressões para os três erros
  de negócio e casos de datas duplicadas, desordenadas e futuras no cálculo
  de sequências.
- O `javac` deste ambiente não conseguiu resolver os caminhos dos arquivos
  por `AccessDeniedException` do Windows. Por isso, a compilação foi feita
  separadamente com ECJ e depois foi executado `mvn surefire:test`.
  O `pom.xml` não foi modificado.

Branch local: `codex/backend-business-review`. Nenhuma mudança foi publicada.
