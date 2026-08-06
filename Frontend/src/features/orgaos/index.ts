import { useQuery } from '@tanstack/react-query'

import { useCan } from '@/features/auth'
import { listOrgaos } from '@/features/orgaos/api/orgaos'

export type { Orgao } from '@/features/orgaos/types'

/**
 * `GET /orgaos` exige a authority `*:*` no backend hoje (sem authority
 * granular tipo `ORGAO:LER`) — ver docs/features/orgaos.md. Usuários sem o
 * wildcard não conseguem listar órgãos; `podeListar` sinaliza esse caso para
 * a UI mostrar uma mensagem em vez de uma combo vazia sem explicação.
 */
export function useOrgaosOptions() {
  const isAdmin = useCan('*:*')
  const podeLerOrgao = useCan('ORGAO:LER')
  
  const podeListar = isAdmin || podeLerOrgao

  const query = useQuery({
    queryKey: ['orgaos', 'options'],
    queryFn: () => listOrgaos({ size: 200 }),
    enabled: podeListar,
  })

  return {
    orgaos: query.data?.content ?? [],
    isLoading: podeListar && query.isLoading,
    podeListar,
  }
}
