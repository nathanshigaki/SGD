CREATE TABLE documento_usuarios (
  id UUID PRIMARY KEY,
  documento_id UUID,
  usuario_id UUID,
  cargo VARCHAR(255),
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (documento_id) REFERENCES documentos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);