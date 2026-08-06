export interface HistoricoDocumentoInfo {
  id: string
  sigdoc: string
}

export interface HistoricoUsuarioInfo {
  id: string
  nome: string
}

export interface ValoresHistorico {
  antes: unknown
  depois: unknown
}

/** Valores conhecidos hoje no backend — `situacao`/`acao` chegam como String
 * livre, então o tipo em `Historico` permanece `string` (não um union) para
 * não quebrar se o backend introduzir um valor novo. */
export const HISTORICO_SITUACAO = {
  PENDENTE: 'PENDENTE_APROVACAO',
  APROVADO: 'APROVADO',
  REJEITADO: 'REJEITADO',
} as const

export const HISTORICO_ACAO = {
  CRIAR: 'CRIAR_DOCUMENTO',
  ATUALIZAR: 'ATUALIZAR_DOCUMENTO',
  DELETAR: 'DELETAR_DOCUMENTO',
} as const

export interface Historico {
  id: string
  // null quando a solicitação é de criação de um documento que ainda não existe
  documento: HistoricoDocumentoInfo | null
  usuario: HistoricoUsuarioInfo
  aprovador: HistoricoUsuarioInfo | null
  situacao: string
  documentoSigdoc: string
  acao: string
  valores: ValoresHistorico
  criadoEm: string
}

export interface HistoricoFiltros {
  documentoId?: string
  usuarioId?: string
  aprovadorId?: string
  situacao?: string
  dataInicio?: string
  dataFim?: string
}
