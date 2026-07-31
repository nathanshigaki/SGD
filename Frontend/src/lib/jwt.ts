export interface JwtClaims {
  sub: string
  scope?: string
  iss?: string
  iat?: number
  exp?: number
}

export function decodeJwt(token: string): JwtClaims | null {
  const payload = token.split('.')[1]

  if (!payload) return null

  try {
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(
      atob(base64)
        .split('')
        .map((char) => '%' + char.charCodeAt(0).toString(16).padStart(2, '0'))
        .join(''),
    )

    return JSON.parse(json) as JwtClaims
  } catch {
    return null
  }
}

export function isJwtExpired(claims: JwtClaims): boolean {
  if (!claims.exp) return false

  return claims.exp * 1000 <= Date.now()
}
