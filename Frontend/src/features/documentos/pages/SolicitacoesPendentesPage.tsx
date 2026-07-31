import { useState } from 'react'
import { toast } from 'sonner'

import { getApiErrorMessage } from '@/lib/http'
import { useValidarSolicitacao } from '@/features/documentos/api/queries'
import { HISTORICO_SITUACAO, SolicitacoesPendentesTable, useHistoricoQuery } from '@/features/historico'
import { PageHeader } from '@/components/common/PageHeader'
import { DataTablePagination } from '@/components/common/DataTablePagination'
import { Skeleton } from '@/components/ui/skeleton'

const PAGE_SIZE = 10

export function SolicitacoesPendentesPage() {
  const [page, setPage] = useState(0)
  const [processandoId, setProcessandoId] = useState<string | null>(null)

  const { data, isLoading, isError } = useHistoricoQuery(
    { situacao: HISTORICO_SITUACAO.PENDENTE },
    { page, size: PAGE_SIZE },
  )
  const validar = useValidarSolicitacao()

  const onValidar = (historicoId: string, aprovado: boolean) => {
    setProcessandoId(historicoId)
    validar.mutate(
      { historicoId, aprovado },
      {
        onSuccess: () => {
          toast.success(aprovado ? 'Solicitação aprovada.' : 'Solicitação rejeitada.')
          setProcessandoId(null)
        },
        onError: (error) => {
          toast.error(getApiErrorMessage(error))
          setProcessandoId(null)
        },
      },
    )
  }

  return (
    <div className="flex flex-col gap-6">
      <PageHeader
        title="Solicitações pendentes"
        description="Aprove ou rejeite as alterações de documentos enviadas para validação."
      />

      {isLoading && <Skeleton className="h-64 w-full" />}

      {isError && (
        <p className="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
          Não foi possível carregar as solicitações pendentes.
        </p>
      )}

      {data && (
        <>
          <SolicitacoesPendentesTable
            solicitacoes={data.content}
            processandoId={processandoId}
            onAprovar={(id) => onValidar(id, true)}
            onRejeitar={(id) => onValidar(id, false)}
          />
          <DataTablePagination page={data} onPageChange={setPage} />
        </>
      )}
    </div>
  )
}
