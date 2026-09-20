# Clih API

Backend Java responsável por usuários, rotina, conclusões, pontos e as regras oficiais do produto.

## Tecnologias

- Java 21
- Spring Boot 4.1
- Spring Web MVC e Spring Security
- Spring Data JPA
- PostgreSQL e Flyway
- OpenAPI/Swagger
- Maven Wrapper

## Rodar localmente

Requisitos: Java 21+ e Docker Desktop (ou um PostgreSQL local).

```bash
docker compose up -d
copy .env.example .env
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Depois de iniciar:

- API: `http://localhost:8080/api/v1/today`
- documentação: `http://localhost:8080/swagger-ui.html`
- saúde: `http://localhost:8080/actuator/health`

O perfil `local` libera os endpoints `/api/v1/**` para desenvolvimento. Fora dele, a API fica fechada até a equipe configurar o provedor OAuth/OpenID escolhido; isso evita publicar um modo de demonstração inseguro.

## Fluxo implementado

1. `GET /api/v1/today` combina usuário, atividades e conclusões do dia.
2. `POST /api/v1/habits/{habitId}/completions` exige `Idempotency-Key`.
3. A mesma transação registra a conclusão, calcula pontos e grava a movimentação.
4. Repetir a mesma chave retorna a conclusão anterior e não concede pontos outra vez.

## Testes

```bash
mvnw.cmd test
```

## Estrutura

```text
areas/       áreas da vida
routines/    hábitos e resumo do dia
progress/    conclusões
rewards/     política e movimentações de pontos
users/       identidade interna do usuário
shared/      erros e utilidades comuns
config/      segurança e CORS
```

## Próximas entregas

- Integrar o provedor de identidade e substituir o usuário local pelo usuário autenticado.
- Adicionar conquistas, cosméticos e inventário.
- Cobrir idempotência e transações com Testcontainers/PostgreSQL no CI.
- Adicionar rotas de áreas, hábitos, perfil e relatórios semanais.
