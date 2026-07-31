import { useQuery } from '@tanstack/react-query'

import type { PageParams } from '@/types/api'
import { buscarHistorico, listHistorico } from '@/features/historico/api/historico'
import type { HistoricoFiltros } from '@/features/historico/types'

function temFiltrosAtivos(filtros: HistoricoFiltros): boolean {
  return Object.values(filtros).some((valor) => valor !== undefined && valor !== '')
}

export function useHistoricoQuery(filtros: HistoricoFiltros, params: PageParams) {
  return useQuery({
    queryKey: ['historico', 'lista', filtros, params],
    queryFn: () =>
      temFiltrosAtivos(filtros) ? buscarHistorico(filtros, params) : listHistorico(params),
  })
}
