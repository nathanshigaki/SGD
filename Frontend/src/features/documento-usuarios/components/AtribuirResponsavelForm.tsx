import { useState } from 'react'
import { toast } from 'sonner'

import { getApiErrorMessage } from '@/lib/http'
import { useUsuariosOptions } from '@/features/usuarios'
import { useAdicionarResponsavel } from '@/features/documento-usuarios/api/queries'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Field, FieldLabel } from '@/components/ui/field'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

interface AtribuirResponsavelFormProps {
  documentoId: string
  usuarioIdsAtuais: string[]
}

export function AtribuirResponsavelForm({
  documentoId,
  usuarioIdsAtuais,
}: AtribuirResponsavelFormProps) {
  const { usuarios, podeListar } = useUsuariosOptions()
  const adicionar = useAdicionarResponsavel(documentoId)

  const [usuarioId, setUsuarioId] = useState<string | null>(null)
  const [cargo, setCargo] = useState('')

  const disponiveis = usuarios.filter((usuario) => !usuarioIdsAtuais.includes(usuario.id))
  const items = [
    { label: 'Selecione um usuário', value: null },
    ...disponiveis.map((usuario) => ({ label: usuario.nome, value: usuario.id })),
  ]

  if (!podeListar) {
    return (
      <p className="text-sm text-muted-foreground">
        Você não tem permissão para listar usuários e atribuir responsáveis.
      </p>
    )
  }

  const onSubmit = (event: React.FormEvent) => {
    event.preventDefault()

    if (!usuarioId) return

    adicionar.mutate(
      { usuarioId, cargo: cargo || undefined },
      {
        onSuccess: () => {
          toast.success('Responsável atribuído com sucesso.')
          setUsuarioId(null)
          setCargo('')
        },
        onError: (error) => {
          toast.error(getApiErrorMessage(error))
        },
      },
    )
  }

  return (
    <form onSubmit={onSubmit} className="flex flex-wrap items-end gap-3">
      <Field className="min-w-48">
        <FieldLabel htmlFor="responsavel-usuario">Usuário</FieldLabel>
        <Select items={items} value={usuarioId} onValueChange={setUsuarioId}>
          <SelectTrigger id="responsavel-usuario" className="w-full">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              {items.map((item) => (
                <SelectItem key={item.value ?? ''} value={item.value}>
                  {item.label}
                </SelectItem>
              ))}
            </SelectGroup>
          </SelectContent>
        </Select>
      </Field>
      <Field className="min-w-40">
        <FieldLabel htmlFor="responsavel-cargo">Cargo</FieldLabel>
        <Input
          id="responsavel-cargo"
          placeholder="Ex.: Relatora"
          value={cargo}
          onChange={(event) => setCargo(event.target.value)}
        />
      </Field>
      <Button type="submit" disabled={!usuarioId || adicionar.isPending}>
        {adicionar.isPending ? 'Atribuindo...' : 'Atribuir'}
      </Button>
    </form>
  )
}
