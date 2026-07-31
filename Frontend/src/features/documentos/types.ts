import type { Orgao } from '@/features/orgaos'

export interface DocumentoResponsavel {
  usuarioId: string
  nome: string
  cargo: string | null
}

export interface Documento {
  id: string | null // null quando a resposta é uma solicitação pendente (202)
  orgao: Orgao
  responsaveis: DocumentoResponsavel[]
  sigdoc: string
  chegouEm: string | null
  concluiuEm: string | null
  emEspera: number
  valor: string | null // BigDecimal → string para não perder precisão
  situacao: string | null
  caracterizacaoTi: string | null
  iniciado: boolean | null
  condes: boolean | null
  resumo: string | null
  tipoContratacao: string | null
  objeto: string | null
  recomendacao: string | null
  parecerFinal: string | null
  criadoEm: string
  atualizadoEm: string
}

export interface DocumentoRequest {
  id?: string
  orgaoId: string
  sigdoc: string
  chegouEm?: string
  concluiuEm?: string
  emEspera: number
  valor?: string
  situacao?: string
  caracterizacaoTi?: string
  iniciado?: boolean
  condes?: boolean
  resumo?: string
  tipoContratacao?: string
  objeto?: string
  recomendacao?: string
  parecerFinal?: string
}

export interface DocumentoFiltros {
  sigdoc?: string
  situacao?: string
  chegouEm?: string
  condes?: boolean
  parecerFinal?: string
}
