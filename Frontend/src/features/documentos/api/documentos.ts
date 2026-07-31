import { api } from '@/lib/http'
import type { Page, PageParams } from '@/types/api'
import type { Documento, DocumentoFiltros, DocumentoRequest } from '@/features/documentos/types'

export async function listDocumentos(params: PageParams = {}): Promise<Page<Documento>> {
  const { data } = await api.get<Page<Documento>>('/documentos', { params })
  return data
}

export async function buscarDocumentos(
  filtros: DocumentoFiltros,
  params: PageParams = {},
): Promise<Page<Documento>> {
  const { data } = await api.get<Page<Documento>>('/documentos/buscar', {
    params: { ...filtros, ...params },
  })
  return data
}

export async function getDocumento(id: string): Promise<Documento> {
  const { data } = await api.get<Documento>(`/documentos/${id}`)
  return data
}

export async function createDocumento(request: DocumentoRequest): Promise<Documento> {
  const { data } = await api.post<Documento>('/documentos', request)
  return data
}

export async function updateDocumento(request: DocumentoRequest): Promise<Documento> {
  const { data } = await api.put<Documento>('/documentos', request)
  return data
}

export async function deleteDocumento(id: string): Promise<void> {
  await api.delete(`/documentos/${id}`)
}

export async function validarSolicitacao(historicoId: string, aprovado: boolean): Promise<void> {
  await api.put(`/documentos/solicitacoes/${historicoId}/validar`, null, { params: { aprovado } })
}
