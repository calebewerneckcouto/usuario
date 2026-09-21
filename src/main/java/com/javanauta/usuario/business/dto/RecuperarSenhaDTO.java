package com.javanauta.usuario.business.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecuperarSenhaDTO {

    private String email;
    private String senha;
}
