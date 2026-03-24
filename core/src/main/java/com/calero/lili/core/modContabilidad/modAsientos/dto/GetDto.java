package com.calero.lili.core.modContabilidad.modAsientos.dto;

import com.calero.lili.core.enums.TipoAsiento;
import com.calero.lili.core.modContabilidad.modAsientos.dto.detalles.DetalleGetDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDto {

    private UUID idAsiento;
    @NotEmpty(message = "No existe la sucursal")

    private String sucursal;

    private String idPeriodo;

    private TipoAsiento tipoAsiento;

    private String numeroAsiento;

    @NotEmpty(message = "No existe la fecha")
    private LocalDate fechaAsiento;

    private String concepto;

    private Boolean mayorizado;

    private Boolean anulada;

    private List<DetalleGetDto> detalle;

}
