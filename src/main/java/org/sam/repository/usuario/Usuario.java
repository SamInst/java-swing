package org.sam.repository.usuario;

public record Usuario(
        String usuario,
        String email,
        String senha
) {}
