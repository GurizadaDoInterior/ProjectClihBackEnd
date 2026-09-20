# Regras iniciais do MVP

Estas regras evitam interpretações diferentes no mobile e no backend. Mudanças devem ser discutidas e registradas antes do código.

## Conceitos

- **Área:** parte da vida que o usuário quer acompanhar.
- **Hábito:** configuração recorrente, como estudar por 30 minutos.
- **Atividade do dia:** ocorrência esperada de um hábito em determinada data.
- **Conclusão:** registro imutável de que a meta daquela ocorrência foi atingida.
- **Objetivo:** resultado com começo e fim; será implementado posteriormente.

## Conclusão

- No MVP, um hábito pode ter no máximo uma conclusão por dia.
- O valor realizado precisa atingir a meta configurada.
- A data da atividade é calculada com o fuso do usuário.
- Uma conclusão pode ser sincronizada até 14 dias depois.
- Repetir a mesma chave de idempotência não cria outra conclusão.
- Desfazer conclusão ainda não está disponível porque exige uma regra explícita para estorno de XP e conquistas.

## XP e nível

- Cada conclusão concede inicialmente 10 XP.
- O nível começa em 1.
- A cada 100 XP o usuário avança um nível.
- XP não pode ser gasto.
- Os valores atuais são políticas simples e podem mudar antes do lançamento.

## Sequência

- Um dia ativo é um dia com pelo menos uma conclusão.
- A sequência continua quando existem dias ativos consecutivos.
- Se o último dia ativo for anterior a ontem, a sequência atual é zero.
- A maior sequência é preservada.
- A sequência não exige completar 100% da rotina, evitando punição excessiva.

## Arquivamento

- Arquivar uma área ou hábito remove o item das telas ativas.
- O histórico permanece disponível para relatórios.
- Uma área com hábitos ativos não pode ser arquivada até que eles sejam movidos ou arquivados.

## Decisões ainda pendentes

- Política para desfazer uma conclusão.
- XP variável por dificuldade ou duração.
- Proteção de sequência e dias de descanso.
- Regras de moedas e loja.
- Critérios das primeiras conquistas.
- Visibilidade pública de atividades e progresso.
