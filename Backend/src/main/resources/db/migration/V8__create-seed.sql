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
    '$2a$10$Ua6bI8M5k6z1.R2f5a8r8uW9L4M3N2O1P0Q9R8S7T6U5V4W3X2Y1Z',
    ARRAY['DOCUMENTO:LER', 'DOCUMENTO:CRIAR', 'DOCUMENTO:ATUALIZAR', 'CONTA:ATIVA']
  )