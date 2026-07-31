import { Button } from '@/components/ui/button'

interface DataTablePaginationProps {
  page: {
    number: number
    totalPages: number
    totalElements: number
    first: boolean
    last: boolean
  }
  onPageChange: (page: number) => void
}

export function DataTablePagination({ page, onPageChange }: DataTablePaginationProps) {
  if (page.totalElements === 0) return null

  return (
    <div className="flex items-center justify-between gap-4">
      <p className="text-sm text-muted-foreground">
        Página {page.number + 1} de {Math.max(page.totalPages, 1)} · {page.totalElements}{' '}
        {page.totalElements === 1 ? 'registro' : 'registros'}
      </p>
      <div className="flex items-center gap-2">
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={page.first}
          onClick={() => onPageChange(page.number - 1)}
        >
          Anterior
        </Button>
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={page.last}
          onClick={() => onPageChange(page.number + 1)}
        >
          Próxima
        </Button>
      </div>
    </div>
  )
}
