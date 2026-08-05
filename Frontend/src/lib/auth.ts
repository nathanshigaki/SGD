import { api } from '@/lib/http'
import { AUTH_CHANGE_EVENT, getStoredToken, setStoredToken } from '@/lib/auth-storage'
import { decodeJwt, isJwtExpired } from '@/lib/jwt'

export interface AuthUser {
  email: string
  permissoes: Array<string>
  nome?: string 
  id?: string
}

interface LoginResponseData {
  token: string
  expiresIn: number
  id: string
  nome: string
  email: string
  permissoes: string[]
}

function userFromToken(token: string | null): AuthUser | null {
  if (!token) return null

  const claims = decodeJwt(token)

  if (!claims || isJwtExpired(claims)) return null

  return {
    email: claims.sub,
    permissoes: claims.scope ? claims.scope.split(' ').filter(Boolean) : [],
  }
}

let cachedToken: string | null = null
let cachedUser: AuthUser | null = null

// Returns a stable reference while the token is unchanged, which
// useSyncExternalStore requires to avoid re-rendering on every call.
export function getCurrentUser(): AuthUser | null {
  const token = getStoredToken()

  if (token !== cachedToken) {
    cachedToken = token
    cachedUser = userFromToken(token)
  }

  return cachedUser
}

export function isAuthenticated(): boolean {
  return getCurrentUser() !== null
}

export async function login(email: string, senha: string): Promise<AuthUser> {
  const response = await api.post<LoginResponseData>('/login', { email, senha })
  
  const token = response.data.token
  
  const user = userFromToken(token)

  if (!user) {
    throw new Error('Token inválido recebido do servidor.')
  }

  setStoredToken(token)

  return user
}

export function logout() {
  setStoredToken(null)
}

export function subscribeAuth(listener: () => void): () => void {
  window.addEventListener(AUTH_CHANGE_EVENT, listener)

  return () => window.removeEventListener(AUTH_CHANGE_EVENT, listener)
}
