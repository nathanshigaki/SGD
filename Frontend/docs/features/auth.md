# Autenticação — `AuthController`

Backend: `AuthController.java` (sem `@RequestMapping` de classe — endpoints
na raiz). Ver [api-conventions.md](../api-conventions.md) para o modelo de
token/permissões.

## Status

✅ Login implementado. ⬜ Cadastro (`/cadastrar`) ainda não tem tela.

## Endpoints

| Verb | Rota | Auth | Body |
|---|---|---|---|
| POST | `/login` | Pública | `{ email: string, senha: string }` |
| POST | `/cadastrar` | Pública | `UsuarioRequest` (ver [usuarios.md](usuarios.md)) |

`POST /login` retorna o JWT como `string` crua no corpo — **não** é
`{ "token": "..." }`. `POST /cadastrar` retorna `UsuarioResponse` (201).

## Já implementado

- [`src/lib/jwt.ts`](../../src/lib/jwt.ts) — decode/validação de expiração do JWT.
- [`src/lib/auth-storage.ts`](../../src/lib/auth-storage.ts) — persistência do token
  + evento `AUTH_CHANGE_EVENT` para sincronizar abas/componentes.
- [`src/lib/auth.ts`](../../src/lib/auth.ts) — `login()`, `logout()`,
  `getCurrentUser()`, `isAuthenticated()`, `subscribeAuth()`.
- [`src/hooks/use-auth.ts`](../../src/hooks/use-auth.ts) — hook React
  (`useSyncExternalStore`) sobre o módulo acima.
- [`src/routes/login.tsx`](../../src/routes/login.tsx) — formulário de login.
- [`src/routes/_auth.tsx`](../../src/routes/_auth.tsx) — layout protegido,
  redireciona para `/login` se não autenticado (`beforeLoad` + guarda em
  runtime).

## Pendente

### 1. Cadastro (`POST /cadastrar`)

- Rota sugerida: `src/routes/cadastrar.tsx` (fora do grupo `_auth`, pública).
- Reusar o schema Zod de `UsuarioRequest` (ver [usuarios.md](usuarios.md)) —
  campos obrigatórios: `nome`, `email`, `senha`.
- Após 201, redirecionar para `/login` com uma mensagem de sucesso (ex.: via
  `search` param ou toast) — o endpoint não autentica automaticamente.

### 2. Matcher de permissões (`useCan`)

`ARCHITECTURE.md` já reserva `src/features/auth/lib/permissions.ts` para
isso; ainda não existe. Ver a implementação sugerida em
[api-conventions.md](../api-conventions.md#modelo-de-permissões-authorities).
Necessário antes de qualquer feature que precise esconder/mostrar ações por
authority (Usuários, Documentos, etc.).

### 3. Migrar `_auth.tsx`/`login.tsx`/`use-auth.ts` para o slice `features/auth`

O `ARCHITECTURE.md` define `src/features/auth/{api,context,lib,components,pages}`
como destino final, mas o código hoje está solto em `src/lib` e
`src/routes`. Não é bloqueante, mas ao adicionar o matcher de permissões e a
tela de cadastro é um bom momento para consolidar o slice (mover sem mudar
comportamento, ajustar imports, expor via `index.ts`).

### 4. `AuthProvider` / contexto

`ARCHITECTURE.md` prevê `auth-context.tsx` (`AuthProvider`, `useAuth`,
`useCan`) registrado em `src/app/providers.tsx`. Hoje `useAuth` já funciona
sem Provider (é puro `useSyncExternalStore` sobre módulo global), então o
Provider é opcional — só necessário se `useCan` precisar de estado adicional
que não caiba no módulo `auth.ts`.
</content>
