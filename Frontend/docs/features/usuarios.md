# Usuários — `UsuarioController`

Backend: `UsuarioController.java`, rota base `/usuarios`. Entidade `Usuario`
(tabela `usuarios`, soft delete via `deletado_em`, `permissoes` é um array de
strings no Postgres).

## Endpoints

| Verb | Rota | Authority | Descrição |
|---|---|---|---|
| GET | `/usuarios` | `USUARIO:LER` ou `*:*` | Lista paginada de usuários |
| GET | `/usuarios/{id}` | `USUARIO:LER` ou `*:*` | Detalhe de um usuário |
| PUT | `/usuarios` | `*:*` | Atualiza dados cadastrais (admin only) |
| POST | `/usuarios/{id}/permissoes` | `*:*` | Adiciona authorities a um usuário |
| DELETE | `/usuarios/{id}/permissoes` | `*:*` | Remove authorities de um usuário |
| DELETE | `/usuarios/{id}` | `USUARIO:EXCLUIR` ou `*:*` | Exclui usuário |

Cadastro de usuário é feito via `POST /cadastrar` (público, ver
[auth.md](auth.md)), não por este controller.

## Tipos TypeScript sugeridos

```ts
// src/features/usuarios/types.ts
export interface Usuario {
  id: string
  nome: string
  email: string
  permissoes: string[]
  criadoEm: string
  atualizadoEm: string
}

export interface UsuarioRequest {
  id?: string
  nome: string
  email: string
  senha: string
}
```

> ⚠️ `UsuarioRequest` tem `senha` obrigatória tanto para criar quanto para o
> `PUT /usuarios` de atualização (mesmo DTO no backend). Na tela de edição,
> **não** reenviar a senha atual em texto — ver nota de segurança abaixo.

## Pontos de atenção do backend (afetam a UI)

- **`PUT /usuarios` não valida e-mail duplicado.** Diferente da criação
  (`POST /cadastrar`), a atualização não verifica unicidade — um e-mail já
  existente estoura constraint no banco e vira erro genérico (500), não um
  400 amigável. Trate esse caso na UI com uma mensagem manual ("e-mail já em
  uso") até o backend adicionar a validação, e considere validar
  client-side contra a lista já carregada quando possível.
- **`senha` obrigatória no update.** Como o DTO de request é o mesmo para
  criar/editar, o formulário de edição de perfil precisa lidar com o campo
  senha — ideal é exigir que o usuário digite a senha atual (ou nova) para
  confirmar a alteração, nunca pré-preencher o campo com um valor
  mascarado que seria reenviado.
- **`DELETE /usuarios/{id}` pode retornar 500** se o usuário tiver
  documentos ou registros de histórico vinculados (FK constraint, sem
  tratamento amigável ainda no backend — ver `Backend/docs/notas-tecnicas-v2.md`
  §3.1). Mostrar confirmação de exclusão com aviso genérico de que a ação
  pode falhar se houver vínculos.
- **Gestão de permissões é granular por payload.** `POST`/`DELETE
  /usuarios/{id}/permissoes` recebem uma `List<String>` de authorities a
  adicionar/remover — não é "substituir a lista inteira". Na UI, um
  componente de "chips" com adicionar/remover individual mapeia bem para
  isso (cada toggle dispara uma chamada com a authority específica, ou
  agrupe múltiplas seleções numa única chamada em lote).

## Estrutura de pastas sugerida

```
src/features/usuarios/
├─ api/
│  ├─ usuarios.ts          # listUsuarios, getUsuario, updateUsuario,
│  │                        # addPermissoes, removePermissoes, deleteUsuario
│  └─ queries.ts           # hooks TanStack Query (useUsuariosQuery, ...)
├─ components/
│  ├─ UsuarioForm.tsx       # criar/editar (schema Zod)
│  ├─ PermissoesEditor.tsx  # chips de authorities
│  └─ UsuariosTable.tsx     # DataTable + paginação
├─ pages/
│  ├─ UsuariosListPage.tsx
│  └─ UsuarioDetailPage.tsx
├─ schema.ts                # Zod schema de UsuarioRequest
├─ types.ts
└─ index.ts
```

## Rotas sugeridas (TanStack Router)

- `src/routes/_auth/usuarios/index.tsx` → `UsuariosListPage` (exige
  `USUARIO:LER` ou `*:*`; esconder da navegação se não tiver).
- `src/routes/_auth/usuarios/$id.tsx` → `UsuarioDetailPage` (edição +
  permissões, exige `*:*` para os campos de escrita; exibir somente leitura
  para quem só tem `USUARIO:LER`).

Registrar em `src/config/navigation.ts` com `permission: 'USUARIO:LER'`.
</content>
