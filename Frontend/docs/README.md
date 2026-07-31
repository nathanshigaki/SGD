# Documentação de Implementação — SGD Frontend

Guias para implementar, no frontend, as funcionalidades expostas pelas
controllers do backend (`Backend/src/main/java/com/govmt/sgd/controller`).
Cada arquivo em [`features/`](features/) cobre um domínio e descreve os
endpoints consumidos, os tipos TypeScript sugeridos, a estrutura de pastas
(seguindo o padrão de [`ARCHITECTURE.md`](../ARCHITECTURE.md)) e os pontos de
atenção específicos daquele backend.

Leia [`api-conventions.md`](api-conventions.md) primeiro — ele define o que é
comum a todas as features (autenticação, paginação, modelo de permissões,
tratamento de erros) para não repetir em cada guia.

## Domínios

| Feature | Backend controller | Rota base | Guia |
|---|---|---|---|
| Autenticação | `AuthController` | `/login`, `/cadastrar` | [features/auth.md](features/auth.md) |
| Usuários | `UsuarioController` | `/usuarios` | [features/usuarios.md](features/usuarios.md) |
| Órgãos | `OrgaoController` | `/orgaos` | [features/orgaos.md](features/orgaos.md) |
| Documentos | `DocumentoController` | `/documentos` | [features/documentos.md](features/documentos.md) |
| Atribuições (Documento × Usuário) | `DocumentoUsuarioController` | `/documento-usuarios` | [features/documento-usuarios.md](features/documento-usuarios.md) |
| Histórico (Auditoria) | `HistoricoController` | `/historico` | [features/historico.md](features/historico.md) |
| Feriados | `FeriadoController` | `/feriados` | [features/feriados.md](features/feriados.md) |

## Status de implementação no frontend

- ✅ **Autenticação** — parcialmente implementada (`src/lib/auth.ts`,
  `src/lib/http.ts`, `src/routes/login.tsx`, `src/routes/_auth.tsx`). Falta
  cadastro (`POST /cadastrar`).
- ⬜ Usuários, Órgãos, Documentos, Atribuições, Histórico e Feriados — ainda
  não têm slice em `src/features/`. Os guias abaixo descrevem como criá-los.

## Ordem de implementação sugerida

1. **Órgãos** e **Feriados** — CRUDs simples, sem workflow, bons para
   validar o padrão de feature/CRUD antes de partir para telas mais
   complexas.
2. **Usuários** — CRUD + gestão de permissões (depende de `*:*` para a
   maioria das ações administrativas).
3. **Documentos** — a tela central do sistema; depende de Órgãos (select) e
   introduz o fluxo de aprovação (maker-checker).
4. **Atribuições (Documento × Usuário)** — geralmente embutida na tela de
   Documentos, mas documentada separadamente por ter tela/endpoint próprios.
5. **Histórico** — somente leitura; consome os IDs gerados pelo fluxo de
   Documentos (aprovação de solicitações).
</content>
