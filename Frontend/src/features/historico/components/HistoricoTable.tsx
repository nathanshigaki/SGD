import { useState } from 'react'
import { Link } from '@tanstack/react-router'

import { formatDateTime } from '@/lib/format'
import { HISTORICO_SITUACAO } from '@/features/historico/types'
import type { Historico } from '@/features/historico/types'
import { HistoricoDiff } from '@/features/historico/components/HistoricoDiff'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'

const acaoLabels: Record<string, string> = {
  CRIAR_DOCUMENTO: 'Criação',
  ATUALIZAR_DOCUMENTO: 'Atualização',
  DELETAR_DOCUMENTO: 'Exclusão',
}

const situacaoVariants: Record<string, 'default' | 'secondary' | 'destructive'> = {
  [HISTORICO_SITUACAO.APROVADO]: 'default',
  [HISTORICO_SITUACAO.REJEITADO]: 'destructive',
  [HISTORICO_SITUACAO.PENDENTE]: 'secondary',
}

const situacaoLabels: Record<string, string> = {
  [HISTORICO_SITUACAO.APROVADO]: 'Aprovado',
  [HISTORICO_SITUACAO.REJEITADO]: 'Rejeitado',
  [HISTORICO_SITUACAO.PENDENTE]: 'Pendente',
}

interface HistoricoTableProps {
  historico: Historico[]
  mostrarDocumento?: boolean
}

export function HistoricoTable({ historico, mostrarDocumento = true }: HistoricoTableProps) {
  const [selecionado, setSelecionado] = useState<Historico | null>(null)

  if (historico.length === 0) {
    return (
      <p className="rounded-lg border border-dashed p-8 text-center text-sm text-muted-foreground">
        Nenhum registro de histórico encontrado.
      </p>
    )
  }

  return (
    <div className="rounded-lg border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Data</TableHead>
            {mostrarDocumento && <TableHead>Documento</TableHead>}
            <TableHead>Ação</TableHead>
            <TableHead>Situação</TableHead>
            <TableHead>Autor</TableHead>
            <TableHead>Aprovador</TableHead>
            <TableHead className="text-right">Detalhes</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {historico.map((registro) => (
            <TableRow key={registro.id}>
              <TableCell>{formatDateTime(registro.criadoEm)}</TableCell>
              {mostrarDocumento && (
                <TableCell>
                  {registro.documento ? (
                    <Button
                      variant="link"
                      size="sm"
                      className="h-auto p-0"
                      nativeButton={false}
                      render={
                        <Link to="/documentos/$id" params={{ id: registro.documento.id }} />
                      }
                    >
                      {registro.documento.sigdoc}
                    </Button>
                  ) : (
                    <span className="text-muted-foreground">Novo documento</span>
                  )}
                </TableCell>
              )}
              <TableCell>{acaoLabels[registro.acao] ?? registro.acao}</TableCell>
              <TableCell>
                <Badge variant={situacaoVariants[registro.situacao] ?? 'outline'}>
                  {situacaoLabels[registro.situacao] ?? registro.situacao}
                </Badge>
              </TableCell>
              <TableCell>{registro.usuario.nome}</TableCell>
              <TableCell>{registro.aprovador?.nome ?? '-'}</TableCell>
              <TableCell className="text-right">
                <Button variant="outline" size="sm" onClick={() => setSelecionado(registro)}>
                  Ver alterações
                </Button>
              </TableCell>
            </TableRow>
          ))}
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
