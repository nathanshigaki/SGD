# Órgãos — `OrgaoController`

Backend: `OrgaoController.java`, rota base `/orgaos`. Entidade `Orgao`
(tabela `orgao`, soft delete via `deletado_em`). CRUD simples, sem workflow
de aprovação — bom ponto de partida para implementar o padrão de feature no
frontend.

## Endpoints

| Verb | Rota | Authority | Descrição |
|---|---|---|---|
| POST | `/orgaos` | `*:*` | Cria órgão |
| GET | `/orgaos` | `*:*` | Lista paginada |
| GET | `/orgaos/{id}` | `*:*` | Detalhe |
| PUT | `/orgaos` | `*:*` | Atualiza |
| DELETE | `/orgaos/{id}` | `*:*` | Exclui |

Todas as ações exigem `*:*` (superadmin) — não existe authority granular
`ORGAO:*` no backend hoje. Na prática, esta tela inteira deve ficar
escondida/bloqueada para qualquer usuário sem o wildcard.

## Tipos TypeScript sugeridos

```ts
// src/features/orgaos/types.ts
export interface Orgao {
  id: string
  nome: string
  acronimo: string
  criadoEm: string
  atualizadoEm: string
}

export interface OrgaoRequest {
  id?: string
  nome: string
  acronimo: string
}
```

## Pontos de atenção do backend (afetam a UI)

- **Exclusão pode retornar 500** se houver documentos vinculados ao órgão
  (hard delete + FK constraint, sem tratamento amigável — ver
  `Backend/docs/notas-tecnicas-v2.md` §3.1). Exibir confirmação com aviso e
  tratar erro 500 genérico com mensagem própria ("não é possível excluir:
  há documentos vinculados a este órgão"), já que o backend não distingue
  esse caso de outro erro interno.
- **Sem authority granular.** Diferente de Documentos/Usuários, não faz
  sentido aqui checar `ORGAO:LER` etc. — é `*:*` ou nada. Ao construir o
  `useCan`, garanta que o caso "sem authority específica cadastrada, só
  wildcard" funcione (não hardcode a suposição de que todo recurso tem
  authorities `RECURSO:AÇÃO`).

## Onde este cadastro é consumido

`Orgao` é referenciado por `Documento` (campo `orgaoId` no
`DocumentoRequest`, objeto `orgao` completo no `DocumentoResponse`) — a tela
de Documentos precisa de um combobox/select de órgãos. Por isso vale expor
em `index.ts` um hook leve tipo `useOrgaosOptions()` (lista simplificada
`{ id, nome, acronimo }`, sem paginação visível — ex.: `size` grande ou um
endpoint dedicado no futuro) para reuso em `documentos`.

## Estrutura de pastas sugerida

```
src/features/orgaos/
├─ api/
│  ├─ orgaos.ts       # listOrgaos, getOrgao, createOrgao, updateOrgao, deleteOrgao
│  └─ queries.ts
├─ components/
│  ├─ OrgaoForm.tsx
│  └─ OrgaosTable.tsx
├─ pages/
│  ├─ OrgaosListPage.tsx
│  └─ OrgaoFormPage.tsx   # criar/editar (pode ser um Sheet/Dialog em vez de página)
├─ schema.ts
├─ types.ts
└─ index.ts               # exporta useOrgaosOptions() para o feature documentos
```

## Rotas sugeridas

- `src/routes/_auth/orgaos/index.tsx` → lista + criar/editar via `FormSheet`
  (ver `components/common` no `ARCHITECTURE.md`).

Registrar em `src/config/navigation.ts` com `permission: '*:*'`.
</content>
