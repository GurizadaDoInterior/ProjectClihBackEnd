# Decisões de arquitetura

A documentação arquitetural detalhada está em [`architecture/backend-architecture.md`](architecture/backend-architecture.md).

Resumo das decisões:

- Monólito modular organizado por contexto e camadas DDD pragmáticas.
- PostgreSQL como fonte oficial dos dados.
- Flyway como única forma de evoluir o esquema.
- V1 publicada preservada, evolução compatível na V2 e integridade multiusuário na V3.
- Conclusões idempotentes e recompensas calculadas no backend.
- Segurança fechada por padrão, perfil local explícito e JWT/OIDC em produção.
- XP permanente e futura moeda da loja separada.
