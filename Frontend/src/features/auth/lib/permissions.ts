import { useAuth } from '@/hooks/use-auth'

const SUPERADMIN_AUTHORITY = '*:*'

export function useCan(authority: string): boolean {
  const { user } = useAuth()

  if (!user) return false

  return user.permissoes.includes(SUPERADMIN_AUTHORITY) || user.permissoes.includes(authority)
}

export function useCanAny(authorities: string[]): boolean {
  const { user } = useAuth()

  if (!user) return false

  if (user.permissoes.includes(SUPERADMIN_AUTHORITY)) return true

  return authorities.some((authority) => user.permissoes.includes(authority))
}
