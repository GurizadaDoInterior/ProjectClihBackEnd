# Como contribuir

## Branches

Crie branches curtas a partir de `main`:

```text
feature/sleep-records
feature/journal-entry
fix/duplicate-completion
```

## Antes de abrir um pull request

Execute:

```powershell
.\mvnw.cmd test
```

Confirme também:

- A regra de produto está documentada.
- Toda nova tabela ou coluna possui migração Flyway.
- O usuário autenticado é usado para filtrar os dados.
- Entidades JPA não são retornadas diretamente pela API.
- Casos de uso em `application` não importam classes de `api` ou de Spring Data.
- Novas consultas privadas filtram por `user_id` na própria consulta.
- Operações que alteram vários registros usam transação.
- Mudanças de contrato aparecem no Swagger/OpenAPI.
- Erros novos possuem um código estável em `ErrorCode`.
- Testes cobrem a regra de negócio principal.

## Commits

Use mensagens objetivas:

```text
feat: add sleep record endpoints
fix: prevent duplicate habit completion
test: cover streak calculation
docs: describe journal privacy rules
```

## Migrações

Migrações aplicadas são imutáveis. Use o próximo número disponível:

```text
V4__add_sleep_records.sql
V5__add_journal_entries.sql
```

Não use `ddl-auto=update`. O banco deve evoluir apenas pelas migrações versionadas.
