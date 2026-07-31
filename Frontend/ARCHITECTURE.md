# Arquitetura

Projeto **somente front-end**. O back-end é um serviço **separado** (outro
repositório) — este projeto apenas consome a API REST externa. Não há código de
servidor aqui.

## Camadas

```
src/
├─ app/                     # Bootstrap da aplicação
│  ├─ App.tsx               #   composição de rotas (<Routes>)
│  └─ providers.tsx         #   todos os providers (Query, Router, Auth, Toaster)
│
├─ config/                  # Configuração transversal
│  ├─ env.ts                #   ÚNICO ponto que lê import.meta.env
│  └─ navigation.ts         #   estrutura da navegação (áreas + permissões)
│
├─ lib/                     # Infra genérica, sem regra de negócio
│  ├─ http.ts               #   instância axios (withCredentials) + erros
│  ├─ query-client.ts       #   TanStack Query client
│  ├─ format.ts             #   BRL, datas, máscaras (CPF/CNPJ, telefone)
│  └─ utils.ts              #   cn() (tailwind-merge)
│
├─ types/                   # Tipos de domínio compartilhados
│
├─ components/              # UI reutilizável (cross-feature)
│  ├─ ui/                   #   design system (shadcn: button, card, sheet...)
│  ├─ common/               #   DataTable, PageHeader, StatCard, FormSheet...
│  └─ layout/               #   AppShell, Sidebar (rail + painel), Header
│
└─ features/                # Slices verticais por domínio
   ├─ auth/
   │  ├─ api/               #   sessions.ts, mock.ts (dev)
   │  ├─ context/           #   auth-context.tsx (AuthProvider, useAuth, useCan)
   │  ├─ lib/               #   permissions.ts (matcher de curingas)
   │  ├─ components/        #   ProtectedRoute.tsx
   │  ├─ pages/             #   LoginPage.tsx
   │  └─ index.ts           #   barrel (superfície pública do feature)
   ├─ dashboard/
   │  ├─ data.ts            #   dados de exemplo (trocar por chamadas à API)
   │  └─ pages/DashboardPage.tsx
   └─ system/
      └─ pages/NotFoundPage.tsx
```

## Regras de dependência

- `features/*` podem usar `components/*`, `lib/*`, `config/*`, `types/*`.
- `components/*` e `lib/*` **não** importam de `features/*` (fluxo de fora p/ dentro).
- Import **entre features** só pelo **barrel** (`@/features/x`), nunca por caminho
  interno. Dentro do próprio feature, use caminhos diretos (evita ciclos com o barrel).
- **Nada** lê `import.meta.env` direto — só `config/env.ts`.
- Toda chamada HTTP passa pela instância `http` de `lib/http.ts`.

## Como adicionar um novo módulo (ex.: Documentos)

1. Crie `src/features/documentos/` com `api/`, `pages/`, `components/`,
   `schema.ts` (Zod) e `types.ts` conforme necessário.
2. Exponha o que for público em `src/features/documentos/index.ts`.
3. Registre a rota em `src/app/App.tsx` e o item em `src/config/navigation.ts`
   (com a `permission` correspondente).
