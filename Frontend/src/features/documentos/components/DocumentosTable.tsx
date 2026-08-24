import { Link } from '@tanstack/react-router'

import { useCan } from '@/features/auth'
import { formatBRL, formatDate } from '@/lib/format'
import { SITUACAO_OPTIONS } from '@/features/documentos/constants'
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

interface DocumentosTableProps {
  documentos: Documento[]
  onExcluir: (documento: Documento) => void
  excluindoId: string | null
  onAtualizarSituacao?: (documento: Documento, novaSituacao: string) => void
}

export function DocumentosTable({ documentos, onExcluir, excluindoId, onAtualizarSituacao }: DocumentosTableProps) {
  const podeAtualizar = useCan('DOCUMENTO:ATUALIZAR')
  const podeExcluir = useCan('DOCUMENTO:EXCLUIR')
  const isAdmin = useCan('*:*')

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
              {isAdmin ? (
                <Select
                  value={documento.situacao || ''} // 1. Mudamos de defaultValue para value
                 onValueChange={(novoStatus) => {
                  if (onAtualizarSituacao && novoStatus) {
                    onAtualizarSituacao(documento, novoStatus)
                  }
                }}
                >
                  <SelectTrigger
                    className={`h-8 w-[180px] font-medium transition-colors ${
                      documento.situacao === 'APROVADO'
                        ? '!bg-green-600 !text-white border-green-700'
                        : documento.situacao === 'DEVOLVIDO'
                        ? '!bg-red-600 !text-white border-red-700'
                        : ''
                    }`}
                  >
                    {/* 2. Deixe o SelectValue vazio! O shadcn descobre o texto sozinho */}
                    <SelectValue placeholder="Situação" />
                  </SelectTrigger>
                  
                  <SelectContent>
                    {SITUACAO_OPTIONS.filter((opcao) => opcao.value !== '').map((opcao) => (
                      <SelectItem key={opcao.value} value={opcao.value}>
                        {opcao.label} {/* O SelectValue vai copiar este texto automaticamente */}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              ) : (
                documento.situacao ? (
                  <Badge
                    className={
                      documento.situacao === 'APROVADO'
                        ? 'bg-green-600 hover:bg-green-700 text-white'
                        : documento.situacao === 'DEVOLVIDO'
                        ? 'bg-red-600 hover:bg-red-700 text-white'
                        : ''
                    }
                  >
                    {SITUACAO_OPTIONS.find((opt) => opt.value === documento.situacao)?.label || documento.situacao}
                  </Badge>
                ) : (
                  '-' 
                )
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
