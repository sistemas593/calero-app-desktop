package com.calero.lili.core.dtos;

import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CompraImpuestosDto {

    private UUID idCompraImpuesto;
    private String origen;

    @NotNull(message = "Lista de codigos vacia")
    @NotEmpty(message = "Lista de codigos vacia")
    private List<ImpuestoCodigoDto> listCodigosImpuesto;

}
