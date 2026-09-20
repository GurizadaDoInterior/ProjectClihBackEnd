# Arquitetura do backend

## Decisão principal

O backend começa como um monólito modular. Há um único processo e um único banco PostgreSQL, mas o código é dividido por funcionalidades do produto.

Isso reduz o custo operacional enquanto as regras ainda estão mudando. Se algum módulo precisar ser separado no futuro, as fronteiras existentes tornam a extração mais segura.

## Camadas dentro de um contexto

- **domain:** agregados, entidades, políticas e interfaces de repositório. Não depende da API nem do Spring Data.
- **application:** comandos, casos de uso, consultas e fronteiras transacionais.
- **infrastructure:** implementação das portas; atualmente adaptadores Spring Data/JPA.
- **api:** controllers, validação de entrada e DTOs HTTP.

Uma entidade JPA não deve ser enviada diretamente ao mobile. Alterações internas do banco não podem mudar a API por acidente.

As entidades de domínio ainda carregam anotações JPA e herdam a auditoria comum. Essa é uma concessão consciente do MVP: separar uma segunda classe de persistência para cada agregado aumentaria bastante o custo sem regra de negócio suficiente para justificar. As portas dos repositórios já isolam os casos de uso do Spring Data e permitem essa separação no futuro.

Fluxo de uma escrita:

```text
HTTP DTO → command → application service → domain → repository port
                                                ↑
                                    JPA adapter ┘
```

## Dependências entre módulos

Dependências aceitáveis neste estágio:

```text
today → user, area, habit, completion, reward
completion → habit, reward, user
habit → area, user
area → user
identity → user
```

Evite dependências circulares. Quando duas funcionalidades começarem a depender uma da outra, extraia uma política ou um serviço de leitura com responsabilidade clara.

## Identidade e propriedade

O provedor de login emite um JWT. `CurrentUserProvider` lê o claim `sub` e encontra o usuário interno correspondente.

Todas as consultas privadas incluem `user_id`. Encontrar um recurso somente pelo ID e depois compará-lo é mais sujeito a falhas; prefira métodos como `findByIdAndUserId`. A V3 também usa chaves estrangeiras compostas `(resource_id, user_id)`, impedindo relações entre proprietários diferentes mesmo se uma falha futura escapar da aplicação.

O modo `local` existe para desenvolvimento e falha ao iniciar sem o perfil `local`. O modo padrão é `deny-all`, e produção usa `jwt`. Swagger só é habilitado localmente.

## Datas e fuso horário

- `Instant` representa momentos absolutos, como `completedAt`.
- `LocalDate` representa o dia percebido pelo usuário, como `activityDate`.
- O perfil armazena um fuso IANA, como `America/Sao_Paulo`.
- O servidor usa UTC internamente.

Nunca calcule a sequência usando simplesmente a data do servidor.

## Idempotência

O mobile pode concluir uma atividade e perder a conexão antes de receber a resposta. Por isso, cada ação gera uma `Idempotency-Key`.

O backend possui uma restrição única por usuário e chave. Repetir a mesma operação retorna o resultado existente e não concede XP duas vezes.

## Pontuação

XP é permanente e define o nível. Uma futura moeda de loja deve ter uma tabela de movimentações separada; gastar moeda não deve reduzir o nível.

Toda concessão de XP cria um registro em `point_transactions`, nome preservado da primeira migração publicada. Não mantenha apenas um total sem histórico auditável.

## Compatibilidade com a primeira versão

O namespace oficial é `com.gurizadadointerior.clih`. A migração V1 original permanece imutável; a V2 adiciona o modelo mais completo sem remover colunas antigas e a V3 reforça isolamento de proprietário, constraints de domínio e versionamento otimista. Essa escolha permite atualizar bancos existentes e mantém o histórico do Git e do Flyway coerentes.

Áreas possuem `slug` estável e posição, e hábitos também possuem posição manual. O endpoint `/api/v1/today` fornece a rotina, a semana real e o progresso por área, sem estados simulados.

## Exclusão

Áreas e hábitos são arquivados, preservando o histórico. Exclusão definitiva da conta deve ser um fluxo próprio, com regras de privacidade e retenção definidas antes da implementação.

## Evolução esperada

Adicione módulos somente quando uma funcionalidade entrar na próxima entrega. Não crie microserviços, cache distribuído ou filas antes de existir um problema medido que os justifique.
