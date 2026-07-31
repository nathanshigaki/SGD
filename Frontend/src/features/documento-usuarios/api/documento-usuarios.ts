import { api } from '@/lib/http'
import type { Page } from '@/types/api'
import type {
  DocumentoUsuarioFiltros,
  DocumentoUsuarioRequest,
  DocumentoUsuarioResumo,
} from '@/features/documento-usuarios/types'

export async function createDocumentoUsuario(request: DocumentoUsuarioRequest): Promise<void> {
  await api.post('/documento-usuarios', request)
}

/**
 * `Documento.responsaveis` traz só `{ usuarioId, nome, cargo }` — sem o id do
 * vínculo em si. Para excluir um responsável é preciso localizar esse id via
 * `/documento-usuarios/buscar` antes de chamar o DELETE.
 */
export async function buscarDocumentoUsuarios(
  filtros: DocumentoUsuarioFiltros,
): Promise<Page<DocumentoUsuarioResumo>> {
  const { data } = await api.get<Page<DocumentoUsuarioResumo>>('/documento-usuarios/buscar', {
    params: filtros,
  })
  return data
}

export async function deleteDocumentoUsuario(id: string): Promise<void> {
  await api.delete(`/documento-usuarios/${id}`)
}
