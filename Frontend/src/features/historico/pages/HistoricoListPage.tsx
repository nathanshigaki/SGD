import { useState } from 'react'

import { useHistoricoQuery } from '@/features/historico/api/queries'
import type { HistoricoFiltros } from '@/features/historico/types'
import { HistoricoFiltrosForm } from '@/features/historico/components/HistoricoFiltrosForm'
import { HistoricoTable } from '@/features/historico/components/HistoricoTable'
import { PageHeader } from '@/components/common/PageHeader'
import { DataTablePagination } from '@/components/common/DataTablePagination'
import { Skeleton } from '@/components/ui/skeleton'

const PAGE_SIZE = 10

export function HistoricoListPage() {
  const [filtros, setFiltros] = useState<HistoricoFiltros>({})
  const [page, setPage] = useState(0)

  const { data, isLoading, isError } = useHistoricoQuery(filtros, { page, size: PAGE_SIZE })

  return (
    <div className="flex flex-col gap-6">
      <PageHeader title="Histórico" description="Trilha de auditoria de todas as ações do sistema." />

      <HistoricoFiltrosForm
        onBuscar={(novosFiltros) => {
          setFiltros(novosFiltros)
          setPage(0)
        }}
      />

      {isLoading && <Skeleton className="h-64 w-full" />}

      {isError && (
        <p className="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
          Não foi possível carregar o histórico.
        </p>
      )}

      {data && (
        <>
          <HistoricoTable historico={data.content} />
          <DataTablePagination page={data} onPageChange={setPage} />
        </>
      )}
    </div>
  )
}
