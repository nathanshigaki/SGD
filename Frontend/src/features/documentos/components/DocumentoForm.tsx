import { Controller, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'

import { useOrgaosOptions } from '@/features/orgaos'
import { documentoFormSchema } from '@/features/documentos/schema'
import type { DocumentoFormOutput, DocumentoFormValues } from '@/features/documentos/schema'
import type { Documento, DocumentoRequest } from '@/features/documentos/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import { Field, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  SITUACAO_OPTIONS,
  CARACTERIZACAO_TI_OPTIONS,
  TIPO_CONTRATACAO_OPTIONS,
  PARECER_OPTIONS,
} from '../constants'

interface DocumentoFormProps {
  documento?: Documento
  onSubmit: (request: DocumentoRequest) => void
  isSubmitting: boolean
}

function toDateTimeLocal(value: string | null): string {
  if (!value) return ''
  return value.slice(0, 16)
}

function defaultValuesFromDocumento(documento?: Documento): DocumentoFormValues {
  if (!documento) {
    return {
      orgaoId: '',
      sigdoc: '',
      chegouEm: '',
      concluiuEm: '',
      emEspera: 0,
      valor: '',
      situacao: '',
      caracterizacaoTi: '',
      iniciado: false,
      condes: false,
      resumo: '',
      tipoContratacao: '',
      objeto: '',
      recomendacao: '',
      parecerFinal: '',
    }
  }

  return {
    orgaoId: documento.orgao.id,
    sigdoc: documento.sigdoc,
    chegouEm: toDateTimeLocal(documento.chegouEm),
    concluiuEm: toDateTimeLocal(documento.concluiuEm),
    emEspera: documento.emEspera,
    valor: documento.valor ?? '',
    situacao: documento.situacao ?? '',
    caracterizacaoTi: documento.caracterizacaoTi ?? '',
    iniciado: documento.iniciado ?? false,
    condes: documento.condes ?? false,
    resumo: documento.resumo ?? '',
    tipoContratacao: documento.tipoContratacao ?? '',
    objeto: documento.objeto ?? '',
    recomendacao: documento.recomendacao ?? '',
    parecerFinal: documento.parecerFinal ?? '',
  }
}
function sortOptions(options: { label: string; value: string }[]) {
  const placeholder = options.filter((opt) => !opt.value)
  const validOptions = options.filter((opt) => opt.value)

  validOptions.sort((a, b) => a.label.localeCompare(b.label, 'pt-BR'))

  return [...placeholder, ...validOptions]
}

export function DocumentoForm({ documento, onSubmit, isSubmitting }: DocumentoFormProps) {
  const { orgaos, isLoading: carregandoOrgaos, podeListar: podeListarOrgaos } = useOrgaosOptions()

  const form = useForm<DocumentoFormValues, unknown, DocumentoFormOutput>({
    resolver: zodResolver(documentoFormSchema),
    defaultValues: defaultValuesFromDocumento(documento),
  })

  const orgaoItems = [
    { label: 'Selecione um órgão', value: null },
    ...orgaos
      .map((orgao) => ({ label: `${orgao.acronimo} — ${orgao.nome}`, value: orgao.id }))
      .sort((a, b) => a.label.localeCompare(b.label, 'pt-BR')),
  ]

  const situacaoOptions = sortOptions(SITUACAO_OPTIONS)
  const caracterizacaoOptions = sortOptions(CARACTERIZACAO_TI_OPTIONS)
  const tipoContratacaoOptions = sortOptions(TIPO_CONTRATACAO_OPTIONS)
  const parecerOptions = sortOptions(PARECER_OPTIONS)

  const submit = form.handleSubmit((values) => {
    onSubmit({
      id: documento?.id ?? undefined,
      orgaoId: values.orgaoId,
      sigdoc: values.sigdoc,
      chegouEm: values.chegouEm || undefined,
      concluiuEm: values.concluiuEm || undefined,
      emEspera: values.emEspera,
      valor: values.valor || undefined,
      situacao: values.situacao || undefined,
      caracterizacaoTi: values.caracterizacaoTi || undefined,
      iniciado: values.iniciado,
      condes: values.condes,
      resumo: values.resumo || undefined,
      tipoContratacao: values.tipoContratacao || undefined,
      objeto: values.objeto || undefined,
      recomendacao: values.recomendacao || undefined,
      parecerFinal: values.parecerFinal || undefined,
    })
  })

  return (
    <form onSubmit={submit} noValidate className="flex flex-col gap-6">
      <FieldGroup>
        <div className="grid gap-4 sm:grid-cols-2">
          <Field data-invalid={!!form.formState.errors.orgaoId}>
            <FieldLabel htmlFor="orgaoId">Órgão</FieldLabel>
            {podeListarOrgaos ? (
              <Controller
                control={form.control}
                name="orgaoId"
                render={({ field }) => (
                  <Select
                    items={orgaoItems}
                    value={field.value || null}
                    onValueChange={(value: string | null) => field.onChange(value ?? '')}
                  >
                    <SelectTrigger id="orgaoId" aria-invalid={!!form.formState.errors.orgaoId}>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectGroup>
                        {orgaoItems.map((item) => (
                          <SelectItem key={item.value ?? ''} value={item.value}>
                            {item.label}
                          </SelectItem>
                        ))}
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                )}
              />
            ) : (
              <p className="text-sm text-muted-foreground">
                Você não tem permissão para listar órgãos. Peça a um administrador o
                identificador do órgão.
              </p>
            )}
            <FieldError errors={[form.formState.errors.orgaoId]} />
            {carregandoOrgaos && <p className="text-xs text-muted-foreground">Carregando...</p>}
          </Field>

          <Field data-invalid={!!form.formState.errors.sigdoc}>
            <FieldLabel htmlFor="sigdoc">SIGDOC</FieldLabel>
            <Input
              id="sigdoc"
              placeholder="XXX-XXX-0000/00000"
              aria-invalid={!!form.formState.errors.sigdoc}
              {...form.register('sigdoc')}
            />
            <FieldError errors={[form.formState.errors.sigdoc]} />
          </Field>

          <Field data-invalid={!!form.formState.errors.chegouEm}>
            <FieldLabel htmlFor="chegouEm">Chegou em</FieldLabel>
            <Input id="chegouEm" type="datetime-local" {...form.register('chegouEm')} />
          </Field>

          <Field data-invalid={!!form.formState.errors.concluiuEm}>
            <FieldLabel htmlFor="concluiuEm">Concluiu em</FieldLabel>
            <Input id="concluiuEm" type="datetime-local" {...form.register('concluiuEm')} />
          </Field>

          <Field data-invalid={!!form.formState.errors.emEspera}>
            <FieldLabel htmlFor="emEspera">Dias em espera</FieldLabel>
            <Input
              id="emEspera"
              type="number"
              min={0}
              aria-invalid={!!form.formState.errors.emEspera}
              {...form.register('emEspera')}
            />
            <FieldError errors={[form.formState.errors.emEspera]} />
          </Field>

          <Field data-invalid={!!form.formState.errors.valor}>
            <FieldLabel htmlFor="valor">Valor (R$)</FieldLabel>
            <Input
              id="valor"
              type="text"
              inputMode="decimal"
              placeholder="0.00"
              aria-invalid={!!form.formState.errors.valor}
              {...form.register('valor')}
            />
            <FieldError errors={[form.formState.errors.valor]} />
          </Field>

          <Field data-invalid={!!form.formState.errors.situacao}>
            <FieldLabel htmlFor="situacao">Situação</FieldLabel>
            <Controller
              control={form.control}
              name="situacao"
              render={({ field }) => (
                <Select
                  items={situacaoOptions}
                  value={field.value || null}
                  onValueChange={(value: string | null) => field.onChange(value ?? '')}
                >
                  <SelectTrigger id="situacao" aria-invalid={!!form.formState.errors.situacao}>
                    <SelectValue placeholder="Selecione..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      {situacaoOptions.map((item) => (
                        <SelectItem key={item.value} value={item.value}>
                          {item.label}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              )}
            />
            <FieldError errors={[form.formState.errors.situacao]} />
          </Field>

          <Field data-invalid={!!form.formState.errors.caracterizacaoTi}>
            <FieldLabel htmlFor="caracterizacaoTi">Caracterização TI</FieldLabel>
            <Controller
              control={form.control}
              name="caracterizacaoTi"
              render={({ field }) => (
                <Select
                  items={caracterizacaoOptions}
                  value={field.value || null}
                  onValueChange={(value: string | null) => field.onChange(value ?? '')}
                >
                  <SelectTrigger id="caracterizacaoTi" aria-invalid={!!form.formState.errors.caracterizacaoTi}>
                    <SelectValue placeholder="Selecione..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      {caracterizacaoOptions.map((item) => (
                        <SelectItem key={item.value} value={item.value}>
                          {item.label}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              )}
            />
            <FieldError errors={[form.formState.errors.caracterizacaoTi]} />
          </Field>

          <Field data-invalid={!!form.formState.errors.tipoContratacao}>
            <FieldLabel htmlFor="tipoContratacao">Tipo de contratação</FieldLabel>
            <Controller
              control={form.control}
              name="tipoContratacao"
              render={({ field }) => (
                <Select
                  items={tipoContratacaoOptions}
                  value={field.value || null}
                  onValueChange={(value: string | null) => field.onChange(value ?? '')}
                >
                  <SelectTrigger id="tipoContratacao" aria-invalid={!!form.formState.errors.tipoContratacao}>
                    <SelectValue placeholder="Selecione..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      {tipoContratacaoOptions.map((item) => (
                        <SelectItem key={item.value} value={item.value}>
                          {item.label}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              )}
            />
            <FieldError errors={[form.formState.errors.tipoContratacao]} />
          </Field>

          <Field data-invalid={!!form.formState.errors.parecerFinal}>
            <FieldLabel htmlFor="parecerFinal">Parecer final</FieldLabel>
            <Controller
              control={form.control}
              name="parecerFinal"
              render={({ field }) => (
                <Select
                  items={parecerOptions}
                  value={field.value || null}
                  onValueChange={(value: string | null) => field.onChange(value ?? '')}
                >
                  <SelectTrigger id="parecerFinal" aria-invalid={!!form.formState.errors.parecerFinal}>
                    <SelectValue placeholder="Selecione o parecer..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      {parecerOptions.map((item) => (
                        <SelectItem key={item.value} value={item.value}>
                          {item.label}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              )}
            />
            <FieldError errors={[form.formState.errors.parecerFinal]} />
          </Field>
        </div>

        <div className="flex flex-wrap gap-6">
          <Controller
            control={form.control}
            name="iniciado"
            render={({ field }) => (
              <Field orientation="horizontal" className="w-auto">
                <Checkbox
                  id="iniciado"
                  checked={field.value ?? false}
                  onCheckedChange={(checked) => field.onChange(checked === true)}
                />
                <FieldLabel htmlFor="iniciado" className="font-normal">
                  Iniciado
                </FieldLabel>
              </Field>
            )}
          />
          <Controller
            control={form.control}
            name="condes"
            render={({ field }) => (
              <Field orientation="horizontal" className="w-auto">
                <Checkbox
                  id="condes"
                  checked={field.value ?? false}
                  onCheckedChange={(checked) => field.onChange(checked === true)}
                />
                <FieldLabel htmlFor="condes" className="font-normal">
                  Condes
                </FieldLabel>
              </Field>
            )}
          />
        </div>

        <Field>
          <FieldLabel htmlFor="resumo">Resumo</FieldLabel>
          <Textarea id="resumo" rows={3} {...form.register('resumo')} />
        </Field>

        <Field>
          <FieldLabel htmlFor="objeto">Objeto</FieldLabel>
          <Textarea id="objeto" rows={3} {...form.register('objeto')} />
        </Field>

        <Field>
          <FieldLabel htmlFor="recomendacao">Recomendação</FieldLabel>
          <Textarea id="recomendacao" rows={3} {...form.register('recomendacao')} />
        </Field>

        <Button type="submit" disabled={isSubmitting} className="self-start">
          {isSubmitting ? 'Salvando...' : documento ? 'Salvar alterações' : 'Criar documento'}
        </Button>
      </FieldGroup>
    </form>
  )
}
