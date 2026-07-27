INSERT INTO usuarios (id, nome, email, senha, permissoes)
VALUES 
  (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'Admin',
    'admin@seplag.mt.gov.br',
    '$2a$10$E217a3fG/5oT08I582R4ne/9pY5M9pLz4UqE6M3a3X.9K1.8k1/2S',
    ARRAY['*:*', 'CONTA:ATIVA']
  ),
  (
    'b1fec200-0d1c-4fg9-cc7e-7cc0ce491b22',
    'Usuário',
    'usuario@seplag.mt.gov.br',
    '$2a$10$Ua6bI8M5k6z1.R2f5a8r8uW9L4M3N2O1P0Q9R8S7T6U5V4W3X2Y1Z',
    ARRAY['DOCUMENTO:LER', 'DOCUMENTO:CRIAR', 'DOCUMENTO:ATUALIZAR', 'CONTA:ATIVA']
  )