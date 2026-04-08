package com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TbParaisoFiscalRequestDto {

    @NotEmpty(message = "No existe el código del paraíso fiscal")
    private String codigo;

    @NotEmpty(message = "No existe el nombre del paraíso fiscal")
    private String paraisoFiscal;

    private String codigoPais;

}
