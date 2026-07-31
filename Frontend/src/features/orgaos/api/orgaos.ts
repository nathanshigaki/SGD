import { api } from '@/lib/http'
import type { Page, PageParams } from '@/types/api'
import type { Orgao } from '@/features/orgaos/types'

export async function listOrgaos(params: PageParams = {}): Promise<Page<Orgao>> {
  const { data } = await api.get<Page<Orgao>>('/orgaos', { params })
  return data
}
