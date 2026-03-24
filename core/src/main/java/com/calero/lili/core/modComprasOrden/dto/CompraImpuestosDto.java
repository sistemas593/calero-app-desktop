package com.calero.lili.core.modComprasOrden.dto;

import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CompraImpuestosDto {

    private UUID compraImpuestoId;
    @NotNull(message = "La lista de codigos no se encuetra")
    @NotEmpty(message = "La lista de codigos no se encuetra")
    private List<ImpuestoCodigoDto> impuestoCodigos;

}
