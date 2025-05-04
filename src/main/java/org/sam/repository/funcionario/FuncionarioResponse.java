package org.sam.repository.funcionario;

import java.time.LocalDateTime;

public record FuncionarioResponse(
        Long id,
        String nome,
        LocalDateTime dataAdmissao,
        Float salario,
        Boolean status
){}
