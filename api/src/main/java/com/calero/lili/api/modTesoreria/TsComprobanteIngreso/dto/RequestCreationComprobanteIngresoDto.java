package com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto;

import com.calero.lili.core.enums.TipoAsiento;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreationComprobanteIngresoDto {

    @NotNull(message = "No existe sucursal")
    @NotEmpty(message = "No existe sucursal")
    private String sucursal;

    @NotNull(message = "No existe codigo serie")
    @NotEmpty(message = "No existe codigo serie")
    private String codigoSerie;

    @NotNull(message = "No existe numero comprobante")
    @NotEmpty(message = "No existe numero comprobante")
    private String numeroComprobante;

    private String numeroIdentificacion;

    @NotNull(message = "No existe fecha de comprobante")
    @NotEmpty(message = "No existe fecha de comprobante")
    private String fechaComprobante;

    private BigDecimal valor;
    private String concepto;
    private String nombre;
    private String observaciones;
    private Boolean fisico;
    private UUID idTercero;

    private TipoAsiento tipoAsiento;

    // relacion con asientos.
    private List<BcBancoMovimientoCreationRequestDto> movimientosBancos;

}
