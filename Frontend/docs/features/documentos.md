# Documentos — `DocumentoController`

Backend: `DocumentoController.java`, rota base `/documentos`. Entidade
`Documento` (tabela `documentos`, soft delete, `@ManyToOne` para `Orgao`,
`@OneToMany` para `DocumentoUsuario`). É a tela central do sistema (SGD =
gestão de documentos governamentais/sigadoc) e a única com fluxo de
aprovação (maker-checker).

## Endpoints

| Verb | Rota | Authority | Descrição |
|---|---|---|---|
| POST | `/documentos` | `DOCUMENTO:CRIAR` ou `*:*` | Cria documento (ou solicita criação) |
| GET | `/documentos` | `DOCUMENTO:LER` ou `*:*` | Lista paginada, sem filtro |
| GET | `/documentos/buscar` | `DOCUMENTO:LER` ou `*:*` | Busca filtrada + paginada |
| GET | `/documentos/{id}` | `DOCUMENTO:LER` ou `*:*` | Detalhe |
| PUT | `/documentos` | `DOCUMENTO:ATUALIZAR` ou `*:*` | Atualiza (sempre via aprovação) |
| DELETE | `/documentos/{id}` | `DOCUMENTO:EXCLUIR` ou `*:*` | Exclui (via aprovação) |
| PUT | `/documentos/solicitacoes/{historicoId}/validar` | `*:*` | Aprova/rejeita uma solicitação pendente |

### `GET /documentos/buscar` — filtros

Query params, todos opcionais, combináveis com `Pageable`:

| Param | Tipo | Descrição |
|---|---|---|
| `sigdoc` | string | Código do documento (regex no cadastro: `XXX-XXX-0000/00000[-sufixo]`) |
| `situacao` | string | Status do trâmite |
| `chegouEm` | `LocalDateTime` | Data de chegada |
| `condes` | boolean | Flag "condes" |
| `parecerFinal` | string | Parecer final |

## Fluxo de aprovação (maker-checker) — **o ponto mais importante desta feature**

`POST`, `PUT` e `DELETE` em `/documentos` **não aplicam a mudança
diretamente** para usuários sem `*:*`. Em vez disso, o backend cria um
registro pendente em `Historico` e retorna **202 Accepted** (não 200/201) —
a mudança só é persistida depois que um admin chama
`PUT /documentos/solicitacoes/{historicoId}/validar?aprovado=true|false`.

Implicações para a UI:

1. **Tratar 202 como sucesso "condicional"**, distinto de 200/201. Ao criar
   ou editar um documento e receber 202, mostrar algo como "Solicitação
   enviada para aprovação" em vez de "Documento salvo" — o registro pode
   nem existir ainda (`DocumentoResponse.id` vem `null` quando pendente,
   segundo o comportamento observado no `create`).
2. **Tela de "Solicitações pendentes"** para quem tem `*:*`: listar
   registros de `Historico` com `situacao` pendente (usar
   `GET /historico/buscar?situacao=...`, ver [historico.md](historico.md))
   e oferecer aprovar/rejeitar, chamando
   `PUT /documentos/solicitacoes/{historicoId}/validar?aprovado=<bool>`.
   Essa validação é **admin-only** (`*:*`) — some da navegação para
   qualquer outro usuário.
3. **`historicoId` na resposta de create/update**: confirme com o backend
   (ou inspecione a resposta em runtime) se o `HistoricoResponse.id` gerado
   pela solicitação é retornado em algum lugar acessível à UI logo após o
   `POST`/`PUT` — se não for, a tela "Minhas solicitações" precisará
   cruzar por `documentoId` + `usuarioId` via `GET /historico/buscar`.
4. **Usuários com `*:*` pulam o fluxo?** O texto do backend sugere que
   `create` "cria direto se admin" (201) — validar em runtime se `*:*`
   também bypassa `update`/`delete`, já que a doc do agente indica que
   `update` "sempre" retorna 202. Trate ambos os casos (200/201 direto ou
   202 pendente) no client de API.

## Tipos TypeScript sugeridos

```ts
// src/features/documentos/types.ts
import type { Orgao } from '@/features/orgaos'

export interface DocumentoResponsavel {
  usuarioId: string
  nome: string
  cargo: string
}

export interface Documento {
  id: string | null   // null quando a resposta é uma solicitação pendente (202)
  orgao: Orgao
  responsaveis: DocumentoResponsavel[]
  sigdoc: string
  chegouEm: string | null
  concluiuEm: string | null
  emEspera: number
  valor: string | null        // BigDecimal → string para não perder precisão
  situacao: string | null
  caracterizacaoTi: string | null
  iniciado: boolean | null
  condes: boolean | null
  resumo: string | null
  tipoContratacao: string | null
  objeto: string | null
  recomendacao: string | null
  parecerFinal: string | null
  criadoEm: string
  atualizadoEm: string
}

export interface DocumentoRequest {
  id?: string
  orgaoId: string
  sigdoc: string          // valida client-side com o mesmo regex do backend
  chegouEm?: string
  concluiuEm?: string
  emEspera?: number
  valor?: string
  situacao?: string
  caracterizacaoTi?: string
  iniciado?: boolean
  condes?: boolean
  resumo?: string
  tipoContratacao?: string
  objeto?: string
  recomendacao?: string
  parecerFinal?: string
}

export interface DocumentoFiltros {
  sigdoc?: string
  situacao?: string
  chegouEm?: string
  condes?: boolean
  parecerFinal?: string
}
```

Regex de `sigdoc` a replicar no Zod schema (validação client-side antes de
bater no backend):

```ts
const sigdocRegex = /^[a-zA-Z]{3,5}-[a-zA-Z]{3}-\d{4}\/\d{5}(-[a-zA-Z0-9]+)?$/
```

## Pontos de atenção do backend (afetam a UI)

- **`valor` é `BigDecimal`** — trafegar/exibir como string ou usar uma lib
  decimal-safe (não `parseFloat` direto) para não perder precisão em
  valores monetários; formatar exibição com o helper BRL de
  `src/lib/format.ts`.
- **Exclusão também passa por aprovação** (202), não é imediata — a UI de
  "excluir documento" deve avisar "solicitação de exclusão enviada", não
  remover a linha da tabela otimisticamente.
- **`responsaveis` vem populado no próprio `GET`** (join otimizado no
  backend, sem N+1) — não é necessário buscar atribuições separadamente só
  para exibir a lista de responsáveis na tela de detalhe/listagem.

## Estrutura de pastas sugerida

```
src/features/documentos/
├─ api/
│  ├─ documentos.ts        # listDocumentos, buscarDocumentos, getDocumento,
│  │                        # createDocumento, updateDocumento, deleteDocumento,
│  │                        # validarSolicitacao
│  └─ queries.ts
├─ components/
│  ├─ DocumentoForm.tsx     # cria/edita, seleciona órgão (useOrgaosOptions)
│  ├─ DocumentoFiltros.tsx  # form de busca (/documentos/buscar)
│  ├─ DocumentosTable.tsx
│  ├─ SolicitacoesPendentes.tsx  # admin-only: aprovar/rejeitar
│  └─ ResponsaveisList.tsx
├─ pages/
│  ├─ DocumentosListPage.tsx
│  ├─ DocumentoDetailPage.tsx
│  └─ SolicitacoesPendentesPage.tsx  # exige *:*
├─ schema.ts
├─ types.ts
└─ index.ts
```

## Rotas sugeridas

- `src/routes/_auth/documentos/index.tsx` — lista + filtros (`DOCUMENTO:LER`).
- `src/routes/_auth/documentos/$id.tsx` — detalhe/edição (`DOCUMENTO:ATUALIZAR`
  para editar; leitura para quem só tem `DOCUMENTO:LER`).
- `src/routes/_auth/documentos/novo.tsx` — criação (`DOCUMENTO:CRIAR`).
- `src/routes/_auth/documentos/solicitacoes.tsx` — fila de aprovação
  (`*:*` apenas).

Registrar em `src/config/navigation.ts` com as permissions acima; o item
"Solicitações" só deve aparecer para `*:*`.
</content>
