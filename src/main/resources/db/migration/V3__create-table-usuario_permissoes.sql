CREATE TABLE usuario_permissoes (
    usuario_id UUID NOT NULL,
    permissao VARCHAR(255) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);