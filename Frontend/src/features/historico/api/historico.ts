import { api } from '@/lib/http'
import type { Page, PageParams } from '@/types/api'
import type { Historico, HistoricoFiltros } from '@/features/historico/types'

export async function listHistorico(params: PageParams = {}): Promise<Page<Historico>> {
  const { data } = await api.get<Page<Historico>>('/historico', { params })
  return data
}

export async function buscarHistorico(
  filtros: HistoricoFiltros,
  params: PageParams = {},
): Promise<Page<Historico>> {
  const { data } = await api.get<Page<Historico>>('/historico/buscar', {
    params: { ...filtros, ...params },
  })
  return data
}
