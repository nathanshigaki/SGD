import { useState } from 'react'
import { Link } from '@tanstack/react-router'
import { toast } from 'sonner'
import { PlusIcon } from 'lucide-react'

import { useCan } from '@/features/auth'
import { getApiErrorMessage } from '@/lib/http'
import { useDeleteDocumento, useDocumentosQuery, useUpdateDocumento } from '@/features/documentos/api/queries'
import type { DocumentoFiltros as DocumentoFiltrosType, Documento } from '@/features/documentos/types'
import { DocumentoFiltros } from '@/features/documentos/components/DocumentoFiltros'
import { DocumentosTable } from '@/features/documentos/components/DocumentosTable'
import { PageHeader } from '@/components/common/PageHeader'
import { DataTablePagination } from '@/components/common/DataTablePagination'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'

const PAGE_SIZE = 10

export function DocumentosListPage() {
  const podeCriar = useCan('DOCUMENTO:CRIAR')
  const ehAdmin = useCan('*:*')

  const [filtros, setFiltros] = useState<DocumentoFiltrosType>({})
  const [page, setPage] = useState(0)
  const [paraExcluir, setParaExcluir] = useState<Documento | null>(null)

  const { data, isLoading, isError } = useDocumentosQuery(filtros, { page, size: PAGE_SIZE })
  const excluir = useDeleteDocumento()

  const atualizar = useUpdateDocumento()

  const onConfirmarExclusao = () => {
    if (!paraExcluir?.id) return

    excluir.mutate(paraExcluir.id, {
      onSuccess: () => {
        toast.success(
          ehAdmin
            ? 'Documento excluído com sucesso.'
            : 'Solicitação de exclusão enviada para aprovação.',
        )
        setParaExcluir(null)
      },
      onError: (error) => {
        toast.error(getApiErrorMessage(error))
        setParaExcluir(null)
      },
    })
  }

  const onAtualizarSituacao = (documento: Documento, novaSituacao: string) => {
    
    const payload = {
      id: documento.id ?? undefined, 
      sigdoc: documento.sigdoc,
      orgaoId: documento.orgao.id, 
      emEspera: documento.emEspera ? 1 : 0, 
      situacao: novaSituacao,
      valor: documento.valor ?? undefined,
    }
    atualizar.mutate(
      payload,{
        onSuccess: () => {
          toast.success('Situação do documento atualizada com sucesso.')
        },
        onError: (error) => {
          toast.error(getApiErrorMessage(error))
        }
      }
    )
  }

  return (
    <div className="flex flex-col gap-6">
      <PageHeader
        title="Documentos"
        description="Gestão de documentos do SIGADOC."
        actions={
          <>
            {ehAdmin && (
              <Button
                variant="outline"
                nativeButton={false}
                render={<Link to="/documentos/solicitacoes" />}
              >
                Solicitações pendentes
              </Button>
            )}
            {podeCriar && (
              <Button nativeButton={false} render={<Link to="/documentos/novo" />}>
                <PlusIcon data-icon="inline-start" />
                Novo documento
              </Button>
            )}
          </>
        }
      />

      <DocumentoFiltros
        onBuscar={(novosFiltros) => {
          setFiltros(novosFiltros)
          setPage(0)
        }}
      />

      {isLoading && <Skeleton className="h-64 w-full" />}

      {isError && (
        <p className="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
          Não foi possível carregar os documentos.
        </p>
      )}

      {data && (
        <>
          <DocumentosTable
            documentos={data.content}
            onExcluir={setParaExcluir}
            excluindoId={excluir.isPending ? paraExcluir?.id ?? null : null}
            onAtualizarSituacao={onAtualizarSituacao}
          />
          <DataTablePagination page={data} onPageChange={setPage} />
        </>
      )}

      <Dialog open={!!paraExcluir} onOpenChange={(open) => !open && setParaExcluir(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Excluir documento</DialogTitle>
            <DialogDescription>
              Tem certeza que deseja excluir o documento{' '}
              <strong>{paraExcluir?.sigdoc}</strong>?{' '}
              {!ehAdmin &&
                'Esta ação gera uma solicitação de exclusão que precisa ser aprovada por um administrador.'}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter showCloseButton>
            <Button variant="destructive" onClick={onConfirmarExclusao} disabled={excluir.isPending}>
              {excluir.isPending ? 'Excluindo...' : 'Excluir'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
