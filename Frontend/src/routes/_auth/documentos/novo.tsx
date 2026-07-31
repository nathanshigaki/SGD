import { createFileRoute, redirect } from '@tanstack/react-router'

import { getCurrentUser } from '@/lib/auth'
import { DocumentoNovoPage } from '@/features/documentos'

export const Route = createFileRoute('/_auth/documentos/novo')({
  beforeLoad: () => {
    const user = getCurrentUser()
    const podeCriar =
      user?.permissoes.includes('DOCUMENTO:CRIAR') || user?.permissoes.includes('*:*')

    if (!podeCriar) {
      throw redirect({ to: '/documentos' })
    }
  },
  component: DocumentoNovoPage,
})
