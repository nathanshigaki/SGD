# Atribuições (Documento × Usuário) — `DocumentoUsuarioController`

Backend: `DocumentoUsuarioController.java`, rota base `/documento-usuarios`.
Entidade `DocumentoUsuario` (tabela de junção `documento_usuarios`,
`@ManyToOne` para `Documento` e `Usuario`, mais um campo `cargo` — o papel
do usuário naquele documento, ex.: "Relatora", "Aprovador").

Diferente de Órgãos/Feriados, esta feature normalmente **não tem tela
própria de listagem global** — ela é consumida embutida na tela de
Documentos (adicionar/remover responsáveis) e/ou na tela de perfil do
usuário ("documentos sob minha responsabilidade"). Documentada
separadamente porque o backend expõe endpoints e authorities próprios.

## Endpoints

| Verb | Rota | Authority | Descrição |
|---|---|---|---|
| POST | `/documento-usuarios` | `DOCUMENTO_USUARIO:CRIAR` ou `*:*` | Atribui um usuário a um documento com um cargo |
| GET | `/documento-usuarios` | `DOCUMENTO_USUARIO:LER` ou `*:*` | Lista paginada de todas as atribuições |
| GET | `/documento-usuarios/buscar` | ⚠️ ver nota abaixo | Busca filtrada por `documentoId`, `usuarioId` e/ou `cargo` |
| GET | `/documento-usuarios/{id}` | `DOCUMENTO_USUARIO:LER` ou `*:*` | Detalhe de uma atribuição |
| DELETE | `/documento-usuarios/{id}` | `DOCUMENTO_USUARIO:EXCLUIR` ou `*:*` | Remove a atribuição (hard delete) |

> ⚠️ **Bug no backend:** `GET /documento-usuarios/buscar` exige a authority
> `LER_DOCUMENTO`, não `DOCUMENTO_USUARIO:LER` como os demais endpoints
> deste controller. Um usuário com `DOCUMENTO_USUARIO:LER` (mas sem o
> literal `LER_DOCUMENTO`) recebe 403 nesse endpoint específico até o
> backend corrigir. **Workaround na UI:** ao decidir se mostra o filtro de
> busca, checar `useCan('LER_DOCUMENTO') || useCan('DOCUMENTO_USUARIO:LER')`
> — e reportar o bug ao time de backend em vez de depender do workaround
> permanentemente.

## Tipos TypeScript sugeridos

```ts
// src/features/documento-usuarios/types.ts
import type { Documento } from '@/features/documentos'
import type { Usuario } from '@/features/usuarios'

export interface DocumentoUsuario {
  id: string
  documento: Documento
  usuario: Usuario
  cargo: string | null
  criadoEm: string
  atualizadoEm: string
}

export interface DocumentoUsuarioRequest {
  id?: string
  documentoId: string
  usuarioId: string
  cargo?: string
}
```

## Pontos de atenção

- **`DELETE` é hard delete, irreversível** — sem soft delete e sem fluxo de
  aprovação (diferente de `Documento`). Confirmar antes de remover.
- **Resposta traz `documento` e `usuario` completos** (`DocumentoResponse` /
  `UsuarioResponse` aninhados), não apenas IDs — útil para renderizar a
  lista de atribuições de um documento sem round-trip extra, mas atenção
  ao payload maior em listagens grandes.
- Como a UI de "atribuir responsável" normalmente vive dentro da tela de
  Documentos (`documentos.ts` já expõe `responsaveis` no próprio
  `DocumentoResponse` para leitura), o uso mais comum deste controller pelo
  frontend é: `POST` para adicionar um novo responsável e `DELETE` para
  remover — o `GET /documento-usuarios` global e o `buscar` servem mais
  para uma eventual tela "documentos por usuário" (ex.: no perfil).

## Estrutura de pastas sugerida

```
src/features/documento-usuarios/
├─ api/
│  ├─ documento-usuarios.ts   # create, list, buscar, getById, delete
│  └─ queries.ts
├─ components/
│  └─ AtribuirResponsavelForm.tsx   # usado dentro de DocumentoDetailPage
├─ types.ts
└─ index.ts
```

Não precisa de `pages/` ou rota própria a menos que se decida criar uma
tela "Minhas atribuições" — nesse caso, `GET /documento-usuarios/buscar?usuarioId=<eu>`
(atenção ao bug de authority acima) alimenta essa listagem.
</content>
