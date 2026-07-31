import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'

import { getApiErrorMessage } from '@/lib/http'
import { useCreateDocumento } from '@/features/documentos/api/queries'
import type { DocumentoRequest } from '@/features/documentos/types'
import { DocumentoForm } from '@/features/documentos/components/DocumentoForm'
import { PageHeader } from '@/components/common/PageHeader'
import { Card, CardContent } from '@/components/ui/card'

export function DocumentoNovoPage() {
  const navigate = useNavigate()
  const criar = useCreateDocumento()

  const onSubmit = (request: DocumentoRequest) => {
    criar.mutate(request, {
      onSuccess: (documento) => {
        toast.success(
          documento.id === null
            ? 'Solicitação de criação enviada para aprovação.'
            : 'Documento criado com sucesso.',
        )
        void navigate({ to: '/documentos' })
      },
      onError: (error) => {
        toast.error(getApiErrorMessage(error))
      },
    })
  }

  return (
    <div className="flex flex-col gap-6">
      <PageHeader title="Novo documento" description="Cadastre um novo documento no SIGADOC." />
      <Card>
        <CardContent>
          <DocumentoForm onSubmit={onSubmit} isSubmitting={criar.isPending} />
        </CardContent>
      </Card>
    </div>
  )
}
