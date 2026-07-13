CREATE TABLE historico (
  id UUID PRIMARY KEY,
  documento_id UUID,
  usuario_id UUID,
  aprovador_id UUID,
  situacao VARCHAR(255),
  acao VARCHAR(255),
  valores JSONB,
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);