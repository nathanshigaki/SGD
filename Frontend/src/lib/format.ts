const brlFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

const dateFormatter = new Intl.DateTimeFormat('pt-BR')

const dateTimeFormatter = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
  timeStyle: 'short',
})

export function formatBRL(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') return '-'

  const numeric = typeof value === 'number' ? value : Number(value)

  if (Number.isNaN(numeric)) return '-'

  return brlFormatter.format(numeric)
}

function parseIsoDate(value: string): Date {
  return new Date(value.length === 10 ? `${value}T00:00:00` : value)
}

export function formatDate(value: string | null | undefined): string {
  if (!value) return '-'

  return dateFormatter.format(parseIsoDate(value))
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) return '-'

  return dateTimeFormatter.format(parseIsoDate(value))
}
