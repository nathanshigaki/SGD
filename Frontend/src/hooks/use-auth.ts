import { useSyncExternalStore } from 'react'

import { getCurrentUser, subscribeAuth } from '@/lib/auth'

export function useAuth() {
  const user = useSyncExternalStore(
    subscribeAuth,
    getCurrentUser,
    getCurrentUser,
  )

  return {
    user,
    isAuthenticated: user !== null,
  }
}
