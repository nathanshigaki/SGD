const TOKEN_STORAGE_KEY = 'token'
const AUTH_CHANGE_EVENT = 'sgd:auth-change'

function getStoredToken(): string | null {
  return window.localStorage.getItem(TOKEN_STORAGE_KEY)
}

function setStoredToken(token: string | null) {
  if (token) {
    window.localStorage.setItem(TOKEN_STORAGE_KEY, token)
  } else {
    window.localStorage.removeItem(TOKEN_STORAGE_KEY)
  }

  window.dispatchEvent(new Event(AUTH_CHANGE_EVENT))
}

export { TOKEN_STORAGE_KEY, AUTH_CHANGE_EVENT, getStoredToken, setStoredToken }
