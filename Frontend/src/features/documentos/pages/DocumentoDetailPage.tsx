import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'

import { useCan } from '@/features/auth'
import { getApiErrorMessage } from '@/lib/http'
import { formatBRL, formatDateTime } from '@/lib/format'
import { useDocumentoQuery, useUpdateDocumento } from '@/features/documentos/api/queries'
import type { Documento, DocumentoRequest } from '@/features/documentos/types'
import { DocumentoForm } from '@/features/documentos/components/DocumentoForm'
import { ResponsaveisList } from '@/features/documentos/components/ResponsaveisList'
import { DocumentoHistorico } from '@/features/documentos/components/DocumentoHistorico'
import { PageHeader } from '@/components/common/PageHeader'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { Separator } from '@/components/ui/separator'

interface DocumentoDetailPageProps {
  id: string
}

export function DocumentoDetailPage({ id }: DocumentoDetailPageProps) {
  const navigate = useNavigate()
  const podeAtualizar = useCan('DOCUMENTO:ATUALIZAR')
  const podeVerHistorico = useCan('HISTORICO:LER')
  const ehAdmin = useCan('*:*')

  const { data: documento, isLoading, isError } = useDocumentoQuery(id)
  const atualizar = useUpdateDocumento()

  const onSubmit = (request: DocumentoRequest) => {
    atualizar.mutate(request, {
      onSuccess: () => {
        toast.success(
          ehAdmin
            ? 'Documento atualizado com sucesso.'
            : 'Solicitação de atualização enviada para aprovação.',
        )
        void navigate({ to: '/documentos' })
      },
      onError: (error) => {
        toast.error(getApiErrorMessage(error))
      },
    })
  }

  if (isLoading) {
    return <Skeleton className="h-96 w-full" />
  }

  if (isError || !documento) {
    return (
      <p className="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
        Documento não encontrado.
      </p>
    )
  }

  return (
    <div className="flex flex-col gap-6">
      <PageHeader
        title={documento.sigdoc}
        description={podeAtualizar ? 'Edite os dados do documento.' : 'Detalhes do documento.'}
      />

      <Card>
        <CardContent>
          {podeAtualizar ? (
            <DocumentoForm
              documento={documento}
              onSubmit={onSubmit}
              isSubmitting={atualizar.isPending}
            />
          ) : (
            <DocumentoDetailReadOnly documento={documento} />
          )}
        </CardContent>
      </Card>

      {documento.id && (
        <Card>
          <CardContent>
            <h2 className="mb-4 text-sm font-medium">Responsáveis</h2>
            <ResponsaveisList documentoId={documento.id} responsaveis={documento.responsaveis} />
          </CardContent>
        </Card>
      )}

      {documento.id && podeVerHistorico && (
        <Card>
          <CardContent>
            <h2 className="mb-4 text-sm font-medium">Histórico</h2>
            <DocumentoHistorico documentoId={documento.id} />
          </CardContent>
        </Card>
      )}
    </div>
  )
}

function DocumentoDetailReadOnly({ documento }: { documento: Documento }) {
  const campos: [string, string][] = [
    ['Órgão', `${documento.orgao.acronimo} — ${documento.orgao.nome}`],
    ['Situação', documento.situacao ?? '-'],
    ['Chegou em', formatDateTime(documento.chegouEm)],
    ['Concluiu em', formatDateTime(documento.concluiuEm)],
    ['Dias em espera', String(documento.emEspera)],
    ['Valor', formatBRL(documento.valor)],
    ['Caracterização TI', documento.caracterizacaoTi ?? '-'],
    ['Tipo de contratação', documento.tipoContratacao ?? '-'],
    ['Parecer final', documento.parecerFinal ?? '-'],
  ]

  return (
    <div className="flex flex-col gap-4 text-sm">
      <div className="grid gap-3 sm:grid-cols-2">
        {campos.map(([label, value]) => (
          <div key={label}>
            <p className="text-muted-foreground">{label}</p>
            <p className="font-medium">{value}</p>
          </div>
        ))}
      </div>
      <Separator />
      <div>
        <p className="text-muted-foreground">Resumo</p>
        <p>{documento.resumo ?? '-'}</p>
      </div>
      <div>
        <p className="text-muted-foreground">Objeto</p>
        <p>{documento.objeto ?? '-'}</p>
      </div>
      <div>
        <p className="text-muted-foreground">Recomendação</p>
        <p>{documento.recomendacao ?? '-'}</p>
      </div>
    </div>
  )
}
