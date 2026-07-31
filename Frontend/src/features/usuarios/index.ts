import { useQuery } from '@tanstack/react-query'

import { useCan } from '@/features/auth'
import { listUsuarios } from '@/features/usuarios/api/usuarios'

export type { Usuario } from '@/features/usuarios/types'

export function useUsuariosOptions() {
  const podeListar = useCan('USUARIO:LER')

  const query = useQuery({
    queryKey: ['usuarios', 'options'],
    queryFn: () => listUsuarios({ size: 200 }),
    enabled: podeListar,
  })

  return {
    usuarios: query.data?.content ?? [],
    isLoading: podeListar && query.isLoading,
    podeListar,
  }
}
