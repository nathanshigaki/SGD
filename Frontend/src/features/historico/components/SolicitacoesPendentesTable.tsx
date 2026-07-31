import { useState } from 'react'
import { Link } from '@tanstack/react-router'

import { formatDateTime } from '@/lib/format'
import type { Historico } from '@/features/historico/types'
import { HistoricoDiff } from '@/features/historico/components/HistoricoDiff'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'

const acaoLabels: Record<string, string> = {
  CRIAR_DOCUMENTO: 'Criação',
  ATUALIZAR_DOCUMENTO: 'Atualização',
  DELETAR_DOCUMENTO: 'Exclusão',
}

interface SolicitacoesPendentesTableProps {
  solicitacoes: Historico[]
  onAprovar: (historicoId: string) => void
  onRejeitar: (historicoId: string) => void
  processandoId: string | null
}

export function SolicitacoesPendentesTable({
  solicitacoes,
  onAprovar,
  onRejeitar,
  processandoId,
}: SolicitacoesPendentesTableProps) {
  const [selecionado, setSelecionado] = useState<Historico | null>(null)

  if (solicitacoes.length === 0) {
    return (
      <p className="rounded-lg border border-dashed p-8 text-center text-sm text-muted-foreground">
        Nenhuma solicitação pendente.
      </p>
    )
  }

  return (
    <div className="rounded-lg border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Data</TableHead>
            <TableHead>Documento</TableHead>
            <TableHead>Ação</TableHead>
            <TableHead>Solicitante</TableHead>
            <TableHead className="text-right">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {solicitacoes.map((solicitacao) => {
            const processando = processandoId === solicitacao.id

            return (
              <TableRow key={solicitacao.id}>
                <TableCell>{formatDateTime(solicitacao.criadoEm)}</TableCell>
                <TableCell>
                  {solicitacao.documento ? (
                    <Button
                      variant="link"
                      size="sm"
                      className="h-auto p-0"
                      nativeButton={false}
                      render={
                        <Link to="/documentos/$id" params={{ id: solicitacao.documento.id }} />
                      }
                    >
                      {solicitacao.documento.sigdoc}
                    </Button>
                  ) : (
                    <span className="text-muted-foreground">Novo documento</span>
                  )}
                </TableCell>
                <TableCell>{acaoLabels[solicitacao.acao] ?? solicitacao.acao}</TableCell>
                <TableCell>{solicitacao.usuario.nome}</TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    <Button variant="outline" size="sm" onClick={() => setSelecionado(solicitacao)}>
                      Ver alterações
                    </Button>
                    <Button
                      size="sm"
                      disabled={processando}
                      onClick={() => onAprovar(solicitacao.id)}
                    >
                      Aprovar
                    </Button>
                    <Button
                      variant="destructive"
                      size="sm"
                      disabled={processando}
                      onClick={() => onRejeitar(solicitacao.id)}
                    >
                      Rejeitar
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>

      <Dialog open={!!selecionado} onOpenChange={(open) => !open && setSelecionado(null)}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>
              {selecionado && (acaoLabels[selecionado.acao] ?? selecionado.acao)}
              {selecionado?.documento && ` — ${selecionado.documento.sigdoc}`}
            </DialogTitle>
          </DialogHeader>
          {selecionado && (
            <HistoricoDiff antes={selecionado.valores.antes} depois={selecionado.valores.depois} />
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
