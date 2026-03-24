package com.calero.lili.core.modContabilidad.modAsientos.dto;

import com.calero.lili.core.enums.TipoAsiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreationAsientosRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    private String idPeriodo;

    private TipoAsiento tipoAsiento;

    private String numeroAsiento;

    @NotEmpty(message = "No existe la fecha de asiento")
    private String fechaAsiento;

    private String concepto;

    private Boolean mayorizado;

    private Boolean anulada;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailDto> detalle;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailDto {

        private UUID idCuenta;

        private UUID idCentroCostos;

        private int itemOrden;

        private String detalle;

        private String tipoDocumento;

        private String numeroDocumento;

        private String fechaDocumento;

        private BigDecimal debe;

        private BigDecimal haber;

        private String tipoAuxiliar;

        private String nombreAuxiliar;

        private String numeroIdentificacionAuxiliar;

        private UUID idConciliacion;

        private UUID idTercero;

        private UUID idItem;

    }

    private UUID idTercero;

}
