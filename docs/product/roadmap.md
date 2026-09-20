# Roadmap técnico do MVP

## 0. Fundação — concluída nesta base

- Projeto Spring Boot e Maven Wrapper.
- PostgreSQL, Docker Compose e Flyway.
- Configuração local e produção.
- Segurança JWT preparada.
- Estrutura DDD pragmática por contexto e proteção multiusuário no banco.
- Erros, OpenAPI, testes e CI.
- Usuário, áreas, hábitos, conclusões, XP, nível e sequência.
- Endpoint agregado da tela do dia, semana e progresso por área.

## 1. Identidade e onboarding

- Escolher o provedor OIDC usado pelo Expo.
- Configurar login Google e Apple.
- Validar issuer e audience em homologação.
- Modelar preferências escolhidas no onboarding.
- Adicionar exclusão e desativação da conta.

## 2. Sono e diário

- `sleep_records`: início, fim, duração, qualidade e observação.
- `journal_entries`: data, emoção e texto privado.
- Garantir no máximo um registro de cada tipo por dia.
- Definir política de criptografia e acesso ao texto do diário.

## 3. Objetivos

- Objetivos com início, fim, unidade e progresso.
- Etapas ordenadas.
- Templates de objetivos e hábitos.

## 4. Conquistas e economia

- Catálogo de conquistas versionado.
- Desbloqueio idempotente.
- Moeda separada de XP.
- Livro-razão de moedas.
- Cosméticos, inventário e itens equipados.

## 5. Relatórios

- Evoluir o progresso semanal básico já disponível.
- Calendário de consistência.
- Evoluir a distribuição diária por área para períodos maiores.
- Retrospectiva semanal.
- Consultas otimizadas conforme dados reais de uso.

## 6. Social

- Perfil público configurável.
- Seguir, deixar de seguir e solicitações.
- Bloqueio e denúncia antes do feed público.
- Feed paginado.
- Sugestões sem expor contatos ou dados privados indevidamente.

## Fora do MVP

- Microserviços.
- Redis.
- Filas externas.
- Ranking global.
- Inteligência artificial.
- Integrações com relógios e plataformas de saúde.
