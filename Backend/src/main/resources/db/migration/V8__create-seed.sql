INSERT INTO usuarios (nome, email, senha, permissoes)
VALUES 
  (
    'Admin',
    'admin@seplag.mt.gov.br',
    '$2y$10$w71amguGH76LxPPC66OBOeTBtkAneg51aKka5vb3sPDTqtAM450SS',
    ARRAY['*:*', 'CONTA:ATIVA']
  ),
  (
    'Usuário',
    'usuario@seplag.mt.gov.br',
    '$2y$10$kBnt5lDqO8TwKlRjmKxyA..rEwmoE6zN37Q8EI12pMuhCut94R1E.',
    ARRAY['DOCUMENTO:LER', 'DOCUMENTO:CRIAR', 'DOCUMENTO:ATUALIZAR','ORGAO:LER','CONTA:ATIVA']
  )