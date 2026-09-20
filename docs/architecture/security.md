# Modelo de segurança

## Fronteira de confiança

O backend confia somente no JWT validado pelo Spring Security e usa o claim `sub` para localizar o usuário interno. IDs enviados pelo cliente identificam recursos, nunca o proprietário. Toda operação privada deriva o `user_id` do usuário autenticado.

## Modos de execução

- Sem perfil: `deny-all`; rotas privadas permanecem fechadas e não há credenciais padrão de banco.
- `local`: identidade fixa, banco local e Swagger. O validador impede usar esse modo sem o perfil `local`.
- `prod`: JWT com issuer e audience obrigatórios; Swagger continua desligado.

As configurações ficam separadas em `application.yml`, `application-local.yml` e `application-prod.yml` para reduzir ativação acidental de opções de desenvolvimento.

## Proteções implementadas

- API stateless, sem sessão, form login ou HTTP Basic.
- CSRF desativado somente porque a autenticação usa Bearer token, não cookie.
- CORS com allowlist, sem wildcard e sem credenciais; HTTP é aceito apenas para localhost no perfil local.
- Respostas 401 e 403 em JSON padronizado, sem detalhe interno.
- Swagger desativado fora do perfil local.
- Health check público sem detalhes; demais endpoints do Actuator não são expostos.
- Limite de cabeçalho HTTP e validação restrita da `Idempotency-Key`.
- Avatar aceita somente URL HTTPS válida.
- Consultas de recursos usam `id + user_id` e retornam 404 quando não pertencem ao usuário.
- Chaves estrangeiras compostas na V3 impedem associação cruzada entre usuários.
- `@Version` detecta atualizações concorrentes em área, hábito e conclusão.
- Atualização de XP usa bloqueio pessimista por usuário e livro-razão idempotente.
- Mensagens inesperadas e stack traces nunca são enviados ao cliente.

## Responsabilidades de infraestrutura

Antes de produção, TLS, rate limiting, tamanho máximo do corpo, WAF e rotação de segredos devem ser configurados no gateway/plataforma. O banco deve aceitar conexões somente da aplicação, usar usuário sem permissão de superusuário e manter backup criptografado.

Tokens e dados privados não devem entrar em logs. O texto futuro do diário precisa de uma decisão explícita sobre criptografia, retenção, exportação e exclusão antes de ser implementado.

## Checklist de uma nova funcionalidade

1. O controller não recebe `userId` para definir propriedade.
2. O caso de uso consulta por recurso e proprietário em uma única operação.
3. A migração possui foreign keys e constraints equivalentes às invariantes.
4. DTOs validam tamanho, formato e limites numéricos.
5. Operações repetíveis pelo mobile possuem idempotência.
6. Erros públicos usam `ErrorCode` e não expõem exceções internas.
7. Há teste de acesso cruzado entre dois usuários quando o endpoint toca dados privados.
