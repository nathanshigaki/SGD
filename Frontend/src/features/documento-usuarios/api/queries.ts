import { useMutation, useQueryClient } from '@tanstack/react-query'

import {
  buscarDocumentoUsuarios,
  createDocumentoUsuario,
  deleteDocumentoUsuario,
} from '@/features/documento-usuarios/api/documento-usuarios'
import type { DocumentoUsuarioRequest } from '@/features/documento-usuarios/types'

export function useAdicionarResponsavel(documentoId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: Omit<DocumentoUsuarioRequest, 'documentoId'>) =>
      createDocumentoUsuario({ ...request, documentoId }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['documentos', documentoId] })
    },
  })
}

export function useRemoverResponsavel(documentoId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (usuarioId: string) => {
      const pagina = await buscarDocumentoUsuarios({ documentoId, usuarioId })
      const vinculo = pagina.content.at(0)

      if (!vinculo) {
        throw new Error('Vínculo de responsável não encontrado.')
      }

      await deleteDocumentoUsuario(vinculo.id)
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['documentos', documentoId] })
    },
  })
}
