package org.sam.repository.funcionario;

import java.time.LocalDateTime;

public record FuncionarioRequest(
        String nome,
        LocalDateTime dataAdmissao,
        Float salario,
        Boolean status
) {}
