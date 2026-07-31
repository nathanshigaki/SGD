import { toast } from 'sonner'

import { useCan } from '@/features/auth'
import { getApiErrorMessage } from '@/lib/http'
import { AtribuirResponsavelForm, useRemoverResponsavel } from '@/features/documento-usuarios'
import type { DocumentoResponsavel } from '@/features/documentos/types'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'

interface ResponsaveisListProps {
  documentoId: string
  responsaveis: DocumentoResponsavel[]
}

export function ResponsaveisList({ documentoId, responsaveis }: ResponsaveisListProps) {
  const podeCriar = useCan('DOCUMENTO_USUARIO:CRIAR')
  const podeExcluir = useCan('DOCUMENTO_USUARIO:EXCLUIR')
  const remover = useRemoverResponsavel(documentoId)

  const onRemover = (usuarioId: string) => {
    remover.mutate(usuarioId, {
      onSuccess: () => toast.success('Responsável removido.'),
      onError: (error) => toast.error(getApiErrorMessage(error)),
    })
  }

  return (
    <div className="flex flex-col gap-4">
      {responsaveis.length === 0 ? (
        <p className="text-sm text-muted-foreground">Nenhum responsável atribuído.</p>
      ) : (
        <ul className="flex flex-col gap-2">
          {responsaveis.map((responsavel) => (
            <li
              key={responsavel.usuarioId}
              className="flex items-center justify-between rounded-lg border px-3 py-2"
            >
              <div>
                <p className="text-sm font-medium">{responsavel.nome}</p>
                {responsavel.cargo && (
                  <p className="text-xs text-muted-foreground">{responsavel.cargo}</p>
                )}
              </div>
              {podeExcluir && (
                <Button
                  variant="ghost"
                  size="sm"
                  disabled={remover.isPending}
                  onClick={() => onRemover(responsavel.usuarioId)}
                >
                  Remover
                </Button>
              )}
            </li>
          ))}
        </ul>
      )}

      {podeCriar && (
        <>
          <Separator />
          <AtribuirResponsavelForm
            documentoId={documentoId}
            usuarioIdsAtuais={responsaveis.map((responsavel) => responsavel.usuarioId)}
          />
        </>
      )}
    </div>
  )
}
