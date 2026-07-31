import { ClipboardCheck, FileText, History, LayoutDashboard } from 'lucide-react'

export interface NavItem {
  title: string
  to: string
  icon: typeof LayoutDashboard
  /** Authority required to see this item. Omit for "any authenticated user". */
  permission?: string
}

export const navItems: NavItem[] = [
  {
    title: 'Dashboard',
    to: '/dashboard',
    icon: LayoutDashboard,
  },
  {
    title: 'Documentos',
    to: '/documentos',
    icon: FileText,
    permission: 'DOCUMENTO:LER',
  },
  {
    title: 'Solicitações',
    to: '/documentos/solicitacoes',
    icon: ClipboardCheck,
    permission: '*:*',
  },
  {
    title: 'Histórico',
    to: '/historico',
    icon: History,
    permission: 'HISTORICO:LER',
  },
]
