package com.calero.lili.core.modCompras.modCompras.dto;

import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraImpuestosDto {

    private UUID compraImpuestoId;
    @NotNull(message = "La lista de codigos no se encuetra")
    @NotEmpty(message = "La lista de codigos no se encuetra")
    private List<ImpuestoCodigoDto> impuestoCodigos;

}
