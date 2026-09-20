# Clih API

Backend do ProjectClih, aplicativo de organização pessoal, hábitos e evolução por áreas da vida.

## O que esta base já entrega

- Usuário criado automaticamente a partir da identidade autenticada.
- Perfil, fuso horário e estado do onboarding.
- Criação, edição, listagem e arquivamento de áreas.
- Criação, edição, listagem e arquivamento de hábitos recorrentes.
- Visão agregada das atividades do dia, recorte semanal e progresso por área.
- Conclusão idempotente, adequada para tentativas repetidas do aplicativo offline.
- XP, nível, sequência atual e maior sequência.
- Migrações de banco com Flyway.
- Autenticação local isolada e suporte a JWT/OIDC em produção.
- Isolamento de proprietário reforçado por consultas e chaves estrangeiras compostas.
- OpenAPI/Swagger, tratamento padronizado de erros e health check.
- Testes unitários e teste de contexto com PostgreSQL real via Testcontainers.

## Tecnologias

- Java 21
- Spring Boot 4
- Maven Wrapper
- PostgreSQL 17
- Spring Data JPA
- Spring Security Resource Server
- Flyway
- springdoc-openapi
- JUnit e Testcontainers

## Pré-requisitos

Instale no computador:

1. **JDK 21 LTS** — recomendado: Eclipse Temurin 21.
2. **Docker Desktop** — inclui Docker Compose.
3. **Git**.
4. **IntelliJ IDEA** ou outra IDE Java.

Não é necessário instalar Maven: use `mvnw.cmd`, incluído no projeto.

Confira a instalação:

```powershell
java -version
docker --version
docker compose version
```

Na IDE, selecione Java 21 como Project SDK, mesmo que exista uma versão mais nova instalada no sistema.

## Executando localmente

O perfil `local` libera uma identidade fixa apenas para desenvolvimento. Sem um perfil explícito, a API nega todas as rotas protegidas.

```powershell
docker compose up -d
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd spring-boot:run
```

O suporte do Spring também consegue iniciar o `compose.yml` ao subir com o perfil local, desde que o Docker esteja aberto.

Endereços úteis:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Saúde: `http://localhost:8080/actuator/health`

## Testes

```powershell
.\mvnw.cmd test
```

Sem Docker, os testes unitários executam e o teste de integração é ignorado. Com Docker, o Testcontainers inicia um PostgreSQL descartável e valida a aplicação e as migrações.

## Primeiro fluxo para testar

1. Chame `GET /api/v1/me` para criar o usuário local.
2. Crie uma área em `POST /api/v1/areas`.
3. Crie um hábito em `POST /api/v1/habits` usando o ID da área.
4. Consulte `GET /api/v1/today`.
5. Conclua o hábito em `POST /api/v1/habits/{habitId}/completions`.

Cada ação deve enviar uma chave nova, com até 120 caracteres, no cabeçalho. Uma UUID é a opção recomendada:

```http
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
```

Se o mobile repetir a mesma chamada por perda de conexão, deve repetir a mesma chave. O backend devolverá a conclusão existente sem conceder XP novamente.

## Endpoints atuais

| Método | Rota | Responsabilidade |
|---|---|---|
| GET | `/api/v1/me` | Consultar o usuário atual |
| PATCH | `/api/v1/me` | Atualizar perfil e onboarding |
| GET/POST | `/api/v1/areas` | Listar e criar áreas |
| PATCH/DELETE | `/api/v1/areas/{id}` | Editar ou arquivar uma área |
| GET/POST | `/api/v1/habits` | Listar e criar hábitos |
| PATCH/DELETE | `/api/v1/habits/{id}` | Editar ou arquivar um hábito |
| GET | `/api/v1/today` | Montar a tela principal do dia |
| POST | `/api/v1/habits/{id}/completions` | Concluir uma atividade |
| GET | `/api/v1/progress` | Consultar XP, nível e sequência |

## Organização do código

```text
com.gurizadadointerior.clih/
  identity/      identidade autenticada da requisição
  user/          perfil e onboarding
  area/          áreas da vida
  habit/         hábitos e recorrência
  completion/    registros concluídos e idempotência
  reward/        XP, nível e sequência
  today/         leitura agregada para a tela inicial
  shared/        configuração, erros e infraestrutura comum
```

Cada contexto segue um DDD pragmático:

```text
area/
  domain/          agregado e porta de repositório
  application/     comandos, casos de uso e transações
  infrastructure/  adaptador Spring Data/JPA
  api/             controller e DTOs HTTP
```

As dependências apontam de fora para dentro: `api` e `infrastructure` conhecem `application/domain`; o domínio não conhece controllers nem DTOs HTTP. As entidades ainda usam anotações JPA para evitar duplicação prematura no MVP.

## Onde alterar regras importantes

- XP por conclusão: `reward/domain/RewardPolicy.java`.
- Fórmula de níveis: `reward/domain/LevelPolicy.java`.
- Regra de sequência: `reward/domain/StreakCalculator.java`.
- Limite para sincronização offline: `completion/application/CompletionApplicationService.java`.
- Segurança local/JWT: `shared/config/SecurityConfig.java`.
- Configurações por ambiente: `application.yml`, `application-local.yml` e `application-prod.yml`.
- Estrutura do banco: crie uma nova migração em `src/main/resources/db/migration`.

`V1__initial_schema.sql` é a migração original publicada no repositório. `V2__evolve_mvp_core.sql` evolui esse esquema sem apagar os campos legados. `V3__enforce_tenant_integrity.sql` impede relações entre dados de usuários diferentes e adiciona controle de concorrência. Nunca edite migrações compartilhadas. A próxima deve ser, por exemplo, `V4__add_sleep_records.sql`.

## Produção

Produção deve usar:

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:5432/database
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
JWT_ISSUER_URI=https://...
JWT_AUDIENCE=clih-api
CORS_ALLOWED_ORIGINS=https://...
```

O backend valida JWTs emitidos pelo provedor escolhido. O aplicativo mobile não deve enviar um `userId` para decidir o proprietário dos dados; o usuário sempre é identificado pelo token.

Swagger fica desligado por padrão e em produção. O modo `local` só inicia junto do perfil `local`; origens CORS inseguras ou curingas fazem a aplicação falhar ao iniciar.

## Próximos passos

Leia, nesta ordem:

1. `docs/product/mvp-rules.md`
2. `docs/architecture/backend-architecture.md`
3. `docs/product/roadmap.md`
4. `CONTRIBUTING.md`
