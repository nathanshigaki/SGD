import { Link } from '@tanstack/react-router'

import { useCan } from '@/features/auth'
import { formatBRL, formatDate } from '@/lib/format'
import type { Documento } from '@/features/documentos/types'
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

interface DocumentosTableProps {
  documentos: Documento[]
  onExcluir: (documento: Documento) => void
  excluindoId: string | null
}

export function DocumentosTable({ documentos, onExcluir, excluindoId }: DocumentosTableProps) {
  const podeAtualizar = useCan('DOCUMENTO:ATUALIZAR')
  const podeExcluir = useCan('DOCUMENTO:EXCLUIR')

  if (documentos.length === 0) {
    return (
      <p className="rounded-lg border border-dashed p-8 text-center text-sm text-muted-foreground">
        Nenhum documento encontrado.
      </p>
    )
  }

  return (
    <div className="rounded-lg border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>SIGDOC</TableHead>
            <TableHead>Órgão</TableHead>
            <TableHead>Situação</TableHead>
            <TableHead>Chegou em</TableHead>
            <TableHead>Valor</TableHead>
            <TableHead>Responsáveis</TableHead>
            <TableHead className="text-right">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {documentos.map((documento) => (
            <TableRow key={documento.id ?? documento.sigdoc}>
              <TableCell className="font-medium">{documento.sigdoc}</TableCell>
              <TableCell>{documento.orgao.acronimo}</TableCell>
              <TableCell>
                {documento.situacao ? (
                  <Badge variant="outline">{documento.situacao}</Badge>
                ) : (
                  '-'
                )}
              </TableCell>
              <TableCell>{formatDate(documento.chegouEm)}</TableCell>
              <TableCell>{formatBRL(documento.valor)}</TableCell>
              <TableCell className="text-sm text-muted-foreground">
                {documento.responsaveis.length > 0
                  ? documento.responsaveis.map((responsavel) => responsavel.nome).join(', ')
                  : '-'}
              </TableCell>
              <TableCell className="text-right">
                <div className="flex justify-end gap-2">
                  {documento.id && (
                    <Button
                      variant="outline"
                      size="sm"
                      nativeButton={false}
                      render={<Link to="/documentos/$id" params={{ id: documento.id }} />}
                    >
                      {podeAtualizar ? 'Editar' : 'Ver'}
                    </Button>
                  )}
                  {podeExcluir && documento.id && (
                    <Button
                      variant="destructive"
                      size="sm"
                      disabled={excluindoId === documento.id}
                      onClick={() => onExcluir(documento)}
                    >
                      {excluindoId === documento.id ? 'Excluindo...' : 'Excluir'}
                    </Button>
                  )}
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
