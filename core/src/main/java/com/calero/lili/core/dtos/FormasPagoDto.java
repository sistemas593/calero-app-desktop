package com.calero.lili.core.dtos;

import com.calero.lili.core.enums.FormaPagoSriEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormasPagoDto {

    @NotNull(message = "La forma de pago es requerida")
    private FormaPagoSriEnum formaPago;
    private BigDecimal total;
    private String plazo;
    private String unidadTiempo;
}
