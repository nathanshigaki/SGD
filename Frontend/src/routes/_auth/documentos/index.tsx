import { createFileRoute, redirect } from '@tanstack/react-router'

import { getCurrentUser } from '@/lib/auth'
import { DocumentosListPage } from '@/features/documentos'

export const Route = createFileRoute('/_auth/documentos/')({
  beforeLoad: () => {
    const user = getCurrentUser()
    const podeLer = user?.permissoes.includes('DOCUMENTO:LER') || user?.permissoes.includes('*:*')

    if (!podeLer) {
      throw redirect({ to: '/dashboard' })
    }
  },
  component: DocumentosListPage,
})
