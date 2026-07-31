import { createFileRoute, redirect } from '@tanstack/react-router'

import { getCurrentUser } from '@/lib/auth'
import { HistoricoListPage } from '@/features/historico'

export const Route = createFileRoute('/_auth/historico/')({
  beforeLoad: () => {
    const user = getCurrentUser()
    const podeLer = user?.permissoes.includes('HISTORICO:LER') || user?.permissoes.includes('*:*')

    if (!podeLer) {
      throw redirect({ to: '/dashboard' })
    }
  },
  component: HistoricoListPage,
})
