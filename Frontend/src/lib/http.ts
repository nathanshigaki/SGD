import axios from 'axios'
import type { AxiosError } from 'axios'

import { env } from '@/config/env'
import { getStoredToken, setStoredToken } from '@/lib/auth-storage'

export interface ApiErrorResponse {
  timestamp: string
  status: number
  erro: string
  mensagem: string
  caminho: string
}

const api = axios.create({
  baseURL: env.apiUrl,
})

api.interceptors.request.use((config) => {
  const token = getStoredToken()

  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      setStoredToken(null)
    }

    return Promise.reject(error)
  },
)

export function getApiErrorMessage(error: unknown, fallback = 'Ocorreu um erro inesperado. Tente novamente.'): string {
  if (axios.isAxiosError(error)) {
    const apiError = (error as AxiosError<ApiErrorResponse>).response?.data
    if (apiError?.mensagem) return apiError.mensagem
  }

  return fallback
}

export { api }
