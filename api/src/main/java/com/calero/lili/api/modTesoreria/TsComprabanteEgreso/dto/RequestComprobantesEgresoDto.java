package com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto;

import com.calero.lili.core.enums.TipoAsiento;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationRequestDto;
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
public class RequestComprobantesEgresoDto {

    private String sucursal;

    private Boolean fisico;

    private String codigoSerie;

    private String numeroComprobanteEgreso;

    private String numeroIdentificacion;

    @NotNull(message = "Es requerido la fecha del comprobante")
    private String fechaComprobante;

    private BigDecimal valor;

    private String concepto;

    private String nombre;

    private String observaciones;

    private UUID idTercero;

    private List<BcBancoMovimientoCreationRequestDto> movimientosBancos;

    private TipoAsiento tipoAsiento;
}
