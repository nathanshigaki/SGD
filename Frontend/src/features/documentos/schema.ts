import { z } from 'zod'

export const sigdocRegex = /^[a-zA-Z]{3,7}-PRO-\d{4}\/\d{5}(\.[a-zA-Z0-9]+)?$/

const optionalText = z
  .string()
  .optional()
  .transform((value) => (value?.trim() ? value.trim() : undefined))

export const documentoFormSchema = z.object({
  orgaoId: z.string().min(1, 'Selecione um órgão.'),
  sigdoc: z
    .string()
    .min(1, 'Informe o SIGDOC.')
    .regex(sigdocRegex, 'Formato esperado: XXX-XXX-0000/00000'),
  chegouEm: optionalText,
  concluiuEm: optionalText,
  emEspera: z.coerce.number().int().min(0, 'Deve ser zero ou positivo.'),
  valor: z
    .string()
    .optional()
    .transform((value) => (value?.trim() ? value.trim() : undefined))
    .refine((value) => value === undefined || /^\d+(\.\d{1,2})?$/.test(value), {
      message: 'Use o formato 0.00, sem separador de milhar.',
    }),
  situacao: optionalText,
  caracterizacaoTi: optionalText,
  iniciado: z.boolean().optional(),
  condes: z.boolean().optional(),
  resumo: optionalText,
  tipoContratacao: optionalText,
  objeto: optionalText,
  recomendacao: optionalText,
  parecerFinal: optionalText,
})

export type DocumentoFormValues = z.input<typeof documentoFormSchema>
export type DocumentoFormOutput = z.output<typeof documentoFormSchema>
