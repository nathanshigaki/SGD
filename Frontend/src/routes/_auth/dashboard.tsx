import { createFileRoute } from '@tanstack/react-router'

import { useAuth } from '@/hooks/use-auth'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'

export const Route = createFileRoute('/_auth/dashboard')({
  component: DashboardPage,
})

function DashboardPage() {
  const { user } = useAuth()

  return (
    <Card>
      <CardHeader>
        <CardTitle>Bem-vindo, {user?.email}</CardTitle>
        <CardDescription>
          Este conteúdo só é exibido para usuários autenticados.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p className="text-sm text-muted-foreground">
          Permissões:{' '}
          {user?.permissoes.length ? user.permissoes.join(', ') : 'Nenhuma'}
        </p>
      </CardContent>
    </Card>
  )
}
