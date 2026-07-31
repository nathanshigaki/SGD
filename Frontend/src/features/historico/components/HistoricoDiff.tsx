import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

interface HistoricoDiffProps {
  antes: unknown
  depois: unknown
}

function paraRegistro(valor: unknown): Record<string, unknown> {
  if (valor && typeof valor === 'object' && !Array.isArray(valor)) {
    return valor as Record<string, unknown>
  }
  return {}
}

function formatarValor(valor: unknown): string {
  if (valor === null || valor === undefined || valor === '') return '-'
  if (typeof valor === 'object') return JSON.stringify(valor)
  return String(valor)
}

/**
 * `valores.antes`/`valores.depois` são JSON livre no backend — o shape muda
 * conforme a ação (snapshot de `DocumentoResponse` ou o `DocumentoRequest`
 * proposto), então o diff é genérico por chave, não tipado campo a campo.
 */
export function HistoricoDiff({ antes, depois }: HistoricoDiffProps) {
  const registroAntes = paraRegistro(antes)
  const registroDepois = paraRegistro(depois)
  const chaves = [...new Set([...Object.keys(registroAntes), ...Object.keys(registroDepois)])].sort()

  if (chaves.length === 0) {
    return <p className="text-sm text-muted-foreground">Sem dados registrados para esta ação.</p>
  }

  return (
    <div className="overflow-x-auto rounded-lg border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Campo</TableHead>
            <TableHead>Antes</TableHead>
            <TableHead>Depois</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {chaves.map((chave) => {
            const valorAntes = formatarValor(registroAntes[chave])
            const valorDepois = formatarValor(registroDepois[chave])
            const mudou = valorAntes !== valorDepois

            return (
              <TableRow key={chave}>
                <TableCell className="font-medium">{chave}</TableCell>
                <TableCell className={mudou ? 'text-muted-foreground line-through' : undefined}>
                  {valorAntes}
                </TableCell>
                <TableCell className={mudou ? 'font-medium' : 'text-muted-foreground'}>
                  {valorDepois}
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
    </div>
  )
}
