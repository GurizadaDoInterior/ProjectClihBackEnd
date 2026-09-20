# Decisões de arquitetura

## Monólito modular

O MVP começa como uma única aplicação Spring Boot organizada por domínio. Isso reduz custo operacional para uma equipe de três pessoas e mantém claras as fronteiras que poderão ser separadas apenas se houver volume real.

## Banco como fonte oficial

Pontos e conquistas nunca são aceitos do aplicativo. Uma conclusão de hábito é o evento de entrada; o backend calcula a recompensa dentro da mesma transação.

## Idempotência

O aplicativo envia `Idempotency-Key` ao concluir uma atividade. A combinação usuário + chave é única no banco. Se uma fila offline repetir a requisição, a API devolve a conclusão existente sem duplicar pontos.

## Segurança

Swagger e saúde são públicos. O perfil `local` libera os endpoints do produto para desenvolvimento; outros perfis negam acesso por padrão até a integração OAuth/OpenID ser configurada.

## Migrações

Flyway é a única forma de alterar o esquema. `ddl-auto=validate` faz o Hibernate conferir o mapeamento sem tentar editar o banco.
