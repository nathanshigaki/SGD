import { useState } from 'react'

import type { DocumentoFiltros as DocumentoFiltrosType } from '@/features/documentos/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'

interface DocumentoFiltrosProps {
  onBuscar: (filtros: DocumentoFiltrosType) => void
}

const filtrosVazios: DocumentoFiltrosType = {
  sigdoc: '',
  situacao: '',
  parecerFinal: '',
  condes: undefined,
}

export function DocumentoFiltros({ onBuscar }: DocumentoFiltrosProps) {
  const [filtros, setFiltros] = useState<DocumentoFiltrosType>(filtrosVazios)

  const onSubmit = (event: React.FormEvent) => {
    event.preventDefault()
    onBuscar({
      sigdoc: filtros.sigdoc || undefined,
      situacao: filtros.situacao || undefined,
      parecerFinal: filtros.parecerFinal || undefined,
      condes: filtros.condes,
    })
  }

  const onLimpar = () => {
    setFiltros(filtrosVazios)
    onBuscar({})
  }

  return (
    <form onSubmit={onSubmit} className="flex items-center gap-2 overflow-x-auto pb-1">
      <Input
        aria-label="SIGDOC"
        placeholder="SIGDOC"
        className="w-44 shrink-0"
        value={filtros.sigdoc}
        onChange={(event) => setFiltros((state) => ({ ...state, sigdoc: event.target.value }))}
      />
      <Input
        aria-label="Situação"
        placeholder="Situação"
        className="w-36 shrink-0"
        value={filtros.situacao}
        onChange={(event) => setFiltros((state) => ({ ...state, situacao: event.target.value }))}
      />
      <Input
        aria-label="Parecer final"
        placeholder="Parecer final"
        className="w-40 shrink-0"
        value={filtros.parecerFinal}
        onChange={(event) =>
          setFiltros((state) => ({ ...state, parecerFinal: event.target.value }))
        }
      />
      <div className="flex shrink-0 items-center gap-1.5">
        <Checkbox
          id="filtro-condes"
          checked={filtros.condes ?? false}
          onCheckedChange={(checked) =>
            setFiltros((state) => ({ ...state, condes: checked === true ? true : undefined }))
          }
        />
        <label htmlFor="filtro-condes" className="text-sm text-muted-foreground">
          Condes
        </label>
      </div>
      <div className="flex shrink-0 gap-2">
        <Button type="submit" size="sm">
          Buscar
        </Button>
        <Button type="button" variant="ghost" size="sm" onClick={onLimpar}>
          Limpar
        </Button>
      </div>
    </form>
  )
}
