import { createFileRoute, redirect } from '@tanstack/react-router'

import { getCurrentUser } from '@/lib/auth'
import { DocumentoDetailPage } from '@/features/documentos'

export const Route = createFileRoute('/_auth/documentos/$id')({
  beforeLoad: () => {
    const user = getCurrentUser()
    const podeLer = user?.permissoes.includes('DOCUMENTO:LER') || user?.permissoes.includes('*:*')

    if (!podeLer) {
      throw redirect({ to: '/dashboard' })
    }
  },
  component: RouteComponent,
})

function RouteComponent() {
  const { id } = Route.useParams()
  return <DocumentoDetailPage id={id} />
}
