import { api } from '@/lib/http'
import type { Page, PageParams } from '@/types/api'
import type { Usuario } from '@/features/usuarios/types'

export async function listUsuarios(params: PageParams = {}): Promise<Page<Usuario>> {
  const { data } = await api.get<Page<Usuario>>('/usuarios', { params })
  return data
}
