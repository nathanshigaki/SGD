# Histórico (Auditoria) — `HistoricoController`

Backend: `HistoricoController.java`, rota base `/historico`. Entidade
`Historico` (tabela `historico`, `@ManyToOne` para `Documento` e duas
relações `@ManyToOne` para `Usuario` — autor e aprovador — mais uma coluna
JSONB `valores` com o snapshot antes/depois). É a espinha dorsal do fluxo de
aprovação (maker-checker) descrito em [documentos.md](documentos.md).

## Endpoints

Somente leitura — não há `POST`/`PUT`/`DELETE` neste controller. Registros
são criados internamente pelos services (`DocumentoService`) como parte do
fluxo de solicitação/aprovação, nunca diretamente pela API.

| Verb | Rota | Authority | Descrição |
|---|---|---|---|
| GET | `/historico` | `HISTORICO:LER` ou `*:*` | Lista paginada, sem filtro |
| GET | `/historico/buscar` | `HISTORICO:LER` ou `*:*` | Busca filtrada + paginada |

### `GET /historico/buscar` — filtros

| Param | Tipo | Descrição |
|---|---|---|
| `documentoId` | UUID | Filtra por documento |
| `usuarioId` | UUID | Filtra por autor da ação |
| `aprovadorId` | UUID | Filtra por quem aprovou/rejeitou |
| `situacao` | string | Status do registro (ex.: pendente/aprovado/rejeitado) |
| `dataInicio` / `dataFim` | `LocalDateTime` | Intervalo de data |

## Tipos TypeScript sugeridos

```ts
// src/features/historico/types.ts
export interface HistoricoDocumentoInfo {
  id: string
  sigdoc: string
}

export interface HistoricoUsuarioInfo {
  id: string
  nome: string
}

export interface ValoresHistorico {
  antes: unknown | null
  depois: unknown | null
}

export interface Historico {
  id: string
  documento: HistoricoDocumentoInfo
  usuario: HistoricoUsuarioInfo
  aprovador: HistoricoUsuarioInfo | null
  situacao: string
  acao: string
  valores: ValoresHistorico
  criadoEm: string
}

export interface HistoricoFiltros {
  documentoId?: string
  usuarioId?: string
  aprovadorId?: string
  situacao?: string
  dataInicio?: string
  dataFim?: string
}
```

## Dois usos principais na UI

1. **Fila de solicitações pendentes** (admin, `*:*`) — `GET
   /historico/buscar?situacao=PENDENTE` (confirmar o valor exato do enum de
   `situacao` com o backend) alimenta a tela de aprovação descrita em
   [documentos.md](documentos.md#fluxo-de-aprovação-maker-checker--o-ponto-mais-importante-desta-feature).
   Cada linha aprovada/rejeitada chama
   `PUT /documentos/solicitacoes/{historicoId}/validar` (endpoint fica no
   `DocumentoController`, não aqui).
2. **Trilha de auditoria** (quem tem `HISTORICO:LER`) — tela somente
   leitura, tipicamente acessada a partir do detalhe de um `Documento`
   (`GET /historico/buscar?documentoId=<id>`) para mostrar o "diff" de cada
   alteração ao longo do tempo, usando `valores.antes`/`valores.depois`.

## Pontos de atenção

- **`valores` é JSON livre** (`Object`/`Object` no backend, tipado como
  JSONB) — o shape de `antes`/`depois` depende de qual entidade gerou o
  registro (hoje, só `Documento`). Não assumir um schema fixo; renderizar
  como diff genérico chave/valor (ex.: `Object.entries` + destaque do que
  mudou) em vez de tipar campo a campo.
- **`aprovador` pode ser `null`** enquanto o registro está pendente — só é
  preenchido após `validarSolicitacao`.
- Não existe endpoint de criação/edição aqui de propósito — é log de
  auditoria imutável. Não tente adicionar `POST /historico` no client de
  API.

## Estrutura de pastas sugerida

```
src/features/historico/
├─ api/
│  ├─ historico.ts     # listHistorico, buscarHistorico
│  └─ queries.ts
├─ components/
│  ├─ HistoricoTable.tsx
│  ├─ HistoricoDiff.tsx      # renderiza valores.antes / valores.depois
│  └─ SolicitacoesPendentesTable.tsx  # usado por documentos/pages/SolicitacoesPendentesPage
├─ types.ts
└─ index.ts
```

## Rotas sugeridas

- `src/routes/_auth/historico/index.tsx` — trilha de auditoria geral
  (`HISTORICO:LER`).
- A fila de aprovação vive em `documentos/solicitacoes` (ver
  [documentos.md](documentos.md)), mas reutiliza os componentes/hooks deste
  feature via `index.ts`.

Registrar em `src/config/navigation.ts` com `permission: 'HISTORICO:LER'`.
</content>
