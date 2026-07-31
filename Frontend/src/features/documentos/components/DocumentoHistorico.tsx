import { useState } from 'react'

import { HistoricoTable, useHistoricoQuery } from '@/features/historico'
import { DataTablePagination } from '@/components/common/DataTablePagination'
import { Skeleton } from '@/components/ui/skeleton'

const PAGE_SIZE = 5

interface DocumentoHistoricoProps {
  documentoId: string
}

export function DocumentoHistorico({ documentoId }: DocumentoHistoricoProps) {
  const [page, setPage] = useState(0)

  const { data, isLoading, isError } = useHistoricoQuery(
    { documentoId },
    { page, size: PAGE_SIZE },
  )

  if (isLoading) return <Skeleton className="h-40 w-full" />

  if (isError) {
    return (
      <p className="text-sm text-destructive">Não foi possível carregar o histórico deste documento.</p>
    )
  }

  if (!data) return null

  return (
    <div className="flex flex-col gap-4">
      <HistoricoTable historico={data.content} mostrarDocumento={false} />
      <DataTablePagination page={data} onPageChange={setPage} />
    </div>
  )
}
