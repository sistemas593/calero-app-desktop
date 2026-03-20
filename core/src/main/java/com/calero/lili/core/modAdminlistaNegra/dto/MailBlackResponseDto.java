package com.calero.lili.core.modAdminlistaNegra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailBlackResponseDto {

    private String email;
    private String motivo;
    private String fecha;

}
