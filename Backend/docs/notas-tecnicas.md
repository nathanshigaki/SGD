# Notas Técnicas — SGD

Registro das discussões e decisões técnicas da sessão de 2026-07-02.

---

## 1. Revisão geral do projeto

Estágio atual: models, DTOs, mappers (MapStruct), repositories e services para `Usuario`, `Orgao`, `Documento` e `DocumentoUsuario`. Ainda **sem camada Controller, sem configuração de segurança e sem migrations Flyway**, apesar de o DAS (`README.md`) definir JWT, RBAC e auditoria como escopo.

### Pontos positivos

- DTOs como `record`, Lombok nos models e MapStruct para mapeamento reduzem boilerplate.
- Separação request/response evita vazar entidade JPA na API.
- `@Transactional(readOnly = true)` usado corretamente nas leituras.

### Pendências identificadas (ainda não resolvidas)

- **Sem camada Controller**: nenhum `@RestController` no projeto — API não é consumível ainda.
- **Segurança ausente**: `spring-boot-starter-security` declarado no `pom.xml`, mas sem `SecurityFilterChain`, filtro JWT ou `PasswordEncoder`. `UsuarioService.createUsuario` grava a senha sem hash, e `UsuarioResponse` expõe o campo `senha` na resposta da API.
- **Sem Flyway migrations**: dependência declarada, mas nenhum `db/migration`; `application.properties` só tem `spring.application.name`.
- **`historico` (log de auditoria)** é escopo core do DAS mas não tem entidade/service implementados.
- **Soft delete inconsistente**: `Documento.deletadoEm` sugere soft delete, mas `DocumentoService.deleteDocumento` faz hard delete.
- **Exceptions genéricas**: "não encontrado" lança `RuntimeException` puro em vez de exceção de domínio — quando existir controller, isso vira 500 em vez de 404.
- **Validação manual** nos services (`if (x == null || x.isBlank())`) em vez de Bean Validation nos DTOs (ver seção 3).

---

## 2. Correções e features implementadas nesta sessão

### 2.1 Bug crítico corrigido: `update` não persistia alterações

**Onde:** `DocumentoService.updateDocumento` e `UsuarioService.updateUsuario`.

**Causa:** o fluxo buscava a entidade via `findById(id)` (que retorna um DTO `*Response`), convertia o DTO de volta pra entidade via mapper (`toXFromResponse`) — gerando uma instância **nova e detached**, não gerenciada pelo Hibernate — mutava essa instância e retornava, **sem nunca chamar `save()`**. Resultado: a API respondia 200 com os dados "atualizados", mas nada era gravado no banco.

**Fix:** os métodos de update agora buscam a entidade diretamente do repositório (`documentoRepository.findById(id)` / `usuarioRepository.findById(id)`), que fica **gerenciada** dentro do `@Transactional`. A mutação via mapper é suficiente — o Hibernate faz dirty-checking automático e persiste no commit, sem precisar de `.save()` explícito.

> Observação: `deleteDocumento`/`deleteUsuario` têm o mesmo padrão de round-trip via DTO, mas funcionam por acaso — `JpaRepository.delete()` faz `merge()` internamente em entidades detached. Não foi corrigido por não ser bug (só ineficiência); fica como pendência de limpeza.

### 2.2 Listagem de documentos com responsáveis (nome + cargo)

**Objetivo:** trazer, junto de cada documento, o array de usuários responsáveis (via tabela de relação `DocumentoUsuario`) com `nome` e `cargo`.

**Mudanças:**

- `Documento` ganhou o lado inverso da relação: `@OneToMany(mappedBy = "documento") List<DocumentoUsuario> usuarios` — sem alteração de schema, é o mesmo FK que já existia em `DocumentoUsuario`.
- Novo DTO enxuto `DocumentoResponsavelResponse(UUID usuarioId, String nome, String cargo)` — evita aninhar o `DocumentoResponse` inteiro dentro dele mesmo.
- `DocumentoResponse` ganhou `List<DocumentoResponsavelResponse> responsaveis`.
- `DocumentoMapper` converte `documento.usuarios` → `responsaveis` automaticamente.
- `DocumentoRepository.findAllWithResponsaveis()` — query com `LEFT JOIN FETCH` para `orgao`, `usuarios` e `usuario` numa única consulta, evitando N+1. `DocumentoService.getAll()` passou a usar esse método.

Exemplo de saída:

```json
{
  "id": "...",
  "orgao": { "id": "...", "nome": "...", "acronimo": "..." },
  "responsaveis": [
    { "usuarioId": "...", "nome": "Maria Silva", "cargo": "Relatora" },
    { "usuarioId": "...", "nome": "João Souza", "cargo": "Aprovador" }
  ],
  "sigdoc": "..."
}
```

---

## 3. Discussão: validação de campos na criação de documentos

Pergunta: como validar campos obrigatórios, tamanho mínimo e padrão (ex.: `"xxx-123-xxx"`) na criação de um `Documento`?

**Decisão recomendada (ainda não implementada):**

- **Validação de formato/presença** → Bean Validation (Jakarta) direto nos records de DTO, em vez dos `if` manuais espalhados pelos services:

  ```java
  public record DocumentoRequest(
      UUID id,
      @NotNull UUID orgaoId,
      @NotBlank @Size(min = 5) String sigdoc,
      @Pattern(regexp = "\\w{3}-\\d{3}-\\w{3}") String algumCodigo,
      ...
  ) {}
  ```

  Requer: dependência `spring-boot-starter-validation` (ainda não está no `pom.xml`), `@Valid` no parâmetro do controller (que ainda não existe) e um `@ControllerAdvice` tratando `MethodArgumentNotValidException` → 400, senão a violação vira 500.

- **Validação de existência (ex.: `orgaoId` que não existe no banco)** → **não** dá pra fazer com Bean Validation puro (é stateless, não consulta banco). Deve ser feita no service:

  ```java
  Orgao orgao = orgaoRepository.findById(request.orgaoId())
      .orElseThrow(() -> new OrgaoNaoEncontradoException(request.orgaoId()));
  ```

  lançando uma exceção de domínio mapeada pelo `@ControllerAdvice` para 404/400 com mensagem clara.

  Alternativa descartada: deixar o mapper setar só `orgao.id` e deixar a FK do Postgres estourar no `save()` — gera `DataIntegrityViolationException` genérica, ruim para o frontend identificar o campo problemático.

**Status:** discutido, implementação pendente — aguardando confirmação para: (1) adicionar `spring-boot-starter-validation`, (2) anotar `DocumentoRequest`, (3) criar exceção de domínio + `@ControllerAdvice`, (4) checagem de existência de `orgaoId` no `DocumentoService.createDocumento`.
