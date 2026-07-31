# Feriados — `FeriadoController`

Backend: `FeriadoController.java`, rota base `/feriados`. Entidade `Feriado`
(tabela `feriados`, coluna `data_feriado`, sem soft delete nem
`atualizadoEm`). CRUD simples, mesmo padrão de [orgaos.md](orgaos.md).

## Endpoints

| Verb | Rota | Authority | Descrição |
|---|---|---|---|
| POST | `/feriados` | `*:*` | Cria feriado |
| GET | `/feriados` | `*:*` | Lista paginada |
| GET | `/feriados/{id}` | `*:*` | Detalhe |
| PUT | `/feriados` | `*:*` | Atualiza |
| DELETE | `/feriados/{id}` | `*:*` | Exclui |

Todas as ações exigem `*:*` — sem authority granular `FERIADO:*`.

## Tipos TypeScript sugeridos

```ts
// src/features/feriados/types.ts
export interface Feriado {
  id: string
  data: string        // LocalDate, formato "YYYY-MM-DD"
  criadoEm: string
}

export interface FeriadoRequest {
  id?: string
  data: string
}
```

## Pontos de atenção

- `Feriado` não tem `atualizadoEm` nem soft delete — é o cadastro mais
  simples do sistema (só `data` + timestamps de criação). O `PUT` ainda
  existe (permite corrigir a data de um feriado cadastrado errado).
- Provável uso interno: cálculo de dias úteis para `emEspera` em
  `Documento` (SLA de tramitação). Hoje não há endpoint que exponha esse
  cálculo pronto — se a UI precisar de "dias úteis entre X e Y", terá que
  buscar a lista de feriados e calcular no frontend, ou aguardar um
  endpoint dedicado no backend.
- Tela de baixo tráfego/baixa prioridade — normalmente um cadastro anual em
  lote (ex.: importar feriados nacionais do ano) mais do que uso do dia a
  dia. Vale considerar uma ação de "importar" no futuro, mas não é escopo
  do backend atual (só CRUD unitário).

## Estrutura de pastas sugerida

```
src/features/feriados/
├─ api/
│  ├─ feriados.ts     # listFeriados, getFeriado, createFeriado, updateFeriado, deleteFeriado
│  └─ queries.ts
├─ components/
│  ├─ FeriadoForm.tsx
│  └─ FeriadosTable.tsx
├─ pages/
│  └─ FeriadosListPage.tsx
├─ schema.ts
├─ types.ts
└─ index.ts
```

## Rotas sugeridas

- `src/routes/_auth/feriados/index.tsx` → lista + criar/editar via
  `FormSheet`.

Registrar em `src/config/navigation.ts` com `permission: '*:*'`.
</content>
