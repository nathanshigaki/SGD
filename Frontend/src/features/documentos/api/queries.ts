import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import type { PageParams } from '@/types/api'
import {
  buscarDocumentos,
  createDocumento,
  deleteDocumento,
  getDocumento,
  listDocumentos,
  updateDocumento,
  validarSolicitacao,
} from '@/features/documentos/api/documentos'
import type { DocumentoFiltros, DocumentoRequest } from '@/features/documentos/types'

function temFiltrosAtivos(filtros: DocumentoFiltros): boolean {
  return Object.values(filtros).some((valor) => valor !== undefined && valor !== '')
}

export function useDocumentosQuery(filtros: DocumentoFiltros, params: PageParams) {
  return useQuery({
    queryKey: ['documentos', 'lista', filtros, params],
    queryFn: () =>
      temFiltrosAtivos(filtros) ? buscarDocumentos(filtros, params) : listDocumentos(params),
  })
}

export function useDocumentoQuery(id: string) {
  return useQuery({
    queryKey: ['documentos', id],
    queryFn: () => getDocumento(id),
    enabled: !!id,
  })
}

export function useCreateDocumento() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: DocumentoRequest) => createDocumento(request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['documentos', 'lista'] })
    },
  })
}

export function useUpdateDocumento() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: DocumentoRequest) => updateDocumento(request),
    onSuccess: (documento) => {
      void queryClient.invalidateQueries({ queryKey: ['documentos', 'lista'] })
      if (documento.id) {
        void queryClient.invalidateQueries({ queryKey: ['documentos', documento.id] })
      }
    },
  })
}

export function useDeleteDocumento() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => deleteDocumento(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['documentos', 'lista'] })
    },
  })
}

export function useValidarSolicitacao() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ historicoId, aprovado }: { historicoId: string; aprovado: boolean }) =>
      validarSolicitacao(historicoId, aprovado),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['documentos', 'lista'] })
      void queryClient.invalidateQueries({ queryKey: ['historico'] })
    },
  })
}
