import { createFileRoute, redirect } from '@tanstack/react-router'

import { getCurrentUser } from '@/lib/auth'
import { SolicitacoesPendentesPage } from '@/features/documentos'

export const Route = createFileRoute('/_auth/documentos/solicitacoes')({
  beforeLoad: () => {
    const user = getCurrentUser()

    if (!user?.permissoes.includes('*:*')) {
      throw redirect({ to: '/documentos' })
    }
  },
  component: SolicitacoesPendentesPage,
})
