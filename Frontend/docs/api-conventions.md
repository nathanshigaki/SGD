# Convenções de API — comuns a todas as features

Regras que se aplicam a todos os endpoints do backend (`Backend`). Definidas
uma vez aqui para não repetir em cada guia de feature.

## Base URL e cliente HTTP

- Não há prefixo de contexto no backend (`server.servlet.context-path` não
  configurado) — as rotas dos controllers (`/documentos`, `/usuarios`, ...)
  são exatamente o path final.
- `VITE_API_URL` (em `src/config/env.ts` / `.env`) aponta para a raiz da API.
- Toda chamada HTTP passa pela instância `api` de [`src/lib/http.ts`](../src/lib/http.ts)
  (axios com `Authorization: Bearer <token>` injetado automaticamente e
  logout automático em 401). **Nunca** chamar `fetch`/`axios` direto de uma
  feature.

## Autenticação

- `POST /login` é público, recebe `{ email, senha }` e retorna o JWT **como
  string crua** no corpo da resposta (não é um objeto `{ token: "..." }`).
- O token vai no header `Authorization: Bearer <token>` em toda chamada
  subsequente.
- As autorizações do usuário (`permissoes`) vêm decodificadas do claim
  `scope` do JWT (separado por espaço), não de uma chamada de API separada —
  ver [`src/lib/auth.ts`](../src/lib/auth.ts) e [`src/lib/jwt.ts`](../src/lib/jwt.ts).
- `POST /cadastrar` também é público (auto-cadastro) e retorna o usuário
  criado (201) — não autentica automaticamente, o fluxo esperado é
  cadastrar → redirecionar para login.

## Modelo de permissões (authorities)

O backend usa authorities no formato `RECURSO:AÇÃO` (ex.: `DOCUMENTO:LER`,
`USUARIO:EXCLUIR`), combinadas via OR com um coringa de superadmin `*:*`.
Nem todo recurso tem authorities granulares — **Órgãos, Feriados e boa parte
de Usuários só aceitam `*:*`** (isto é, são efetivamente "somente admin"
hoje). Ao decidir o que mostrar/esconder na UI (menus, botões de
ação), o matcher de permissão do frontend precisa: (1) tratar `*:*` como
"pode tudo" e (2) comparar authorities exatas para o resto — não existe
wildcard parcial tipo `DOCUMENTO:*` no backend atual.

| Authority | Usada em |
|---|---|
| `*:*` | Superadmin — libera qualquer ação, incluindo as sem authority granular (Órgãos, Feriados, atualização/permissões de Usuário) |
| `USUARIO:LER` | Listar/ver usuários |
| `USUARIO:EXCLUIR` | Excluir usuário |
| `DOCUMENTO:CRIAR` | Criar documento |
| `DOCUMENTO:LER` | Listar/ver/buscar documentos |
| `DOCUMENTO:ATUALIZAR` | Editar documento |
| `DOCUMENTO:EXCLUIR` | Excluir documento |
| `DOCUMENTO_USUARIO:CRIAR` | Atribuir usuário a documento |
| `DOCUMENTO_USUARIO:LER` | Listar/ver atribuições |
| `DOCUMENTO_USUARIO:EXCLUIR` | Remover atribuição |
| `HISTORICO:LER` | Ver auditoria |

> ⚠️ **Bug conhecido no backend:** `GET /documento-usuarios/buscar` exige a
> authority `LER_DOCUMENTO` em vez de `DOCUMENTO_USUARIO:LER` (usada pelos
> demais endpoints do mesmo controller) — provável erro de copiar-e-colar.
> Até corrigirem, um usuário com `DOCUMENTO_USUARIO:LER` mas sem `LER_DOCUMENTO`
> recebe 403 nesse endpoint específico. Vale checar as duas authorities na UI
> (mostrar o filtro se tiver qualquer uma) e reportar ao time de backend.

Modelagem sugerida no frontend (`src/features/auth/lib/permissions.ts`,
conforme `ARCHITECTURE.md`):

```ts
export function useCan(authority: string): boolean {
  const { user } = useAuth()
  if (!user) return false
  return user.permissoes.includes('*:*') || user.permissoes.includes(authority)
}
```

## Paginação

Todos os endpoints de listagem (`GET` sem `/{id}`) aceitam `Pageable` do
Spring e retornam `Page<T>`. Parâmetros de query relevantes:

- `page` (0-indexed, default `0`)
- `size` (default `10` em todos os controllers)
- `sort` (ex.: `sort=nome,desc`) — cada controller já define um sort padrão
  no backend, então é opcional no frontend a menos que o usuário troque a
  ordenação.

Formato de resposta (`Page<T>`, padrão Spring Data):

```ts
interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number   // página atual, 0-indexed
  size: number
  first: boolean
  last: boolean
}
```

Um hook genérico `usePaginatedQuery` (ou wrapper do TanStack Query) em
`src/lib/` evita reimplementar esse parsing em cada feature.

## Tratamento de erros

- 401 → o interceptor de `src/lib/http.ts` já limpa o token armazenado; a
  navegação para `/login` acontece via `beforeLoad` da rota `_auth` na
  próxima renderização.
- 403 → usuário autenticado mas sem a authority exigida. A UI deveria evitar
  chegar aqui escondendo ações não permitidas, mas trate como erro genérico
  ("Você não tem permissão para esta ação") caso aconteça.
- 404 → registro não encontrado (exceções de domínio mapeadas pelo
  `GlobalExceptionHandler`).
- 400 → validação de DTO (Bean Validation) ou regra de negócio.
- 500 → **atenção**: excluir um Órgão ou Usuário referenciado por
  Documentos/Histórico ainda pode estourar `DataIntegrityViolationException`
  não tratada (ver `Backend/docs/notas-tecnicas-v2.md`, seção 3.1). Até isso
  ser corrigido no backend, a UI de exclusão de Órgão/Usuário deve tratar erro
  genérico de servidor com uma mensagem amigável ("não é possível excluir:
  há documentos/usuários vinculados"), em vez de repassar o erro cru.

Um `ApiError` unificado (extraído do corpo de erro do `GlobalExceptionHandler`)
deve ser criado em `src/lib/http.ts` para tipar esses cenários de forma
consistente entre features.

## Datas

Campos vindos do backend como `LocalDateTime`/`LocalDate` chegam em formato
ISO 8601 (`"2026-07-31T10:00:00"` / `"2026-07-31"`), sem timezone. Formatar
no frontend com os helpers de `src/lib/format.ts` (ver `ARCHITECTURE.md`).
</content>
