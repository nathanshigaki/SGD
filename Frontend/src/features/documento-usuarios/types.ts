export interface DocumentoUsuarioRequest {
  documentoId: string
  usuarioId: string
  cargo?: string
}

export interface DocumentoUsuarioFiltros {
  documentoId?: string
  usuarioId?: string
  cargo?: string
}

/** Só o necessário para localizar o id do vínculo (para excluir) — o
 * endpoint também traz `documento`/`usuario` completos, que não usamos aqui. */
export interface DocumentoUsuarioResumo {
  id: string
  cargo: string | null
}
