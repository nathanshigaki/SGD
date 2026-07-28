CREATE TABLE historico (
  id UUID PRIMARY KEY DEFAULT uuidv7(),
  documento_id UUID,
  usuario_id UUID,
  aprovador_id UUID,
  situacao VARCHAR(255),
  acao VARCHAR(255),
  valores JSONB,
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (documento_id) REFERENCES documentos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (aprovador_id) REFERENCES usuarios(id)
);