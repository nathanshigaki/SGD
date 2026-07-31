import { useState } from 'react'

import { HISTORICO_SITUACAO } from '@/features/historico/types'
import type { HistoricoFiltros } from '@/features/historico/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

interface HistoricoFiltrosFormProps {
  onBuscar: (filtros: HistoricoFiltros) => void
}

const situacaoItems = [
  { label: 'Todas as situações', value: null },
  { label: 'Pendente', value: HISTORICO_SITUACAO.PENDENTE },
  { label: 'Aprovado', value: HISTORICO_SITUACAO.APROVADO },
  { label: 'Rejeitado', value: HISTORICO_SITUACAO.REJEITADO },
]

const filtrosVazios = {
  situacao: null as string | null,
  dataInicio: '',
  dataFim: '',
}

export function HistoricoFiltrosForm({ onBuscar }: HistoricoFiltrosFormProps) {
  const [filtros, setFiltros] = useState(filtrosVazios)

  const onSubmit = (event: React.FormEvent) => {
    event.preventDefault()
    onBuscar({
      situacao: filtros.situacao ?? undefined,
      dataInicio: filtros.dataInicio || undefined,
      dataFim: filtros.dataFim || undefined,
    })
  }

  const onLimpar = () => {
    setFiltros(filtrosVazios)
    onBuscar({})
  }

  return (
    <form onSubmit={onSubmit} className="flex items-center gap-2 overflow-x-auto pb-1">
      <Select
        items={situacaoItems}
        value={filtros.situacao}
        onValueChange={(value: string | null) =>
          setFiltros((state) => ({ ...state, situacao: value }))
        }
      >
        <SelectTrigger className="w-44 shrink-0">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            {situacaoItems.map((item) => (
              <SelectItem key={item.value ?? ''} value={item.value}>
                {item.label}
              </SelectItem>
            ))}
          </SelectGroup>
        </SelectContent>
      </Select>
      <Input
        aria-label="De"
        type="datetime-local"
        className="w-48 shrink-0"
        value={filtros.dataInicio}
        onChange={(event) =>
          setFiltros((state) => ({ ...state, dataInicio: event.target.value }))
        }
      />
      <Input
        aria-label="Até"
        type="datetime-local"
        className="w-48 shrink-0"
        value={filtros.dataFim}
        onChange={(event) => setFiltros((state) => ({ ...state, dataFim: event.target.value }))}
      />
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
