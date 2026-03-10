package com.calero.lili.api.modVentas.reembolsos.dto;

import com.calero.lili.core.enums.TipoTerceroPerSoc;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreationRequestReembolsoDto {

    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    private String codPaisPagoReemb;
    private TipoTerceroPerSoc tipoProveedorReemb;
    private String codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String numeroAutorizacionReemb;

    @Valid
    @NotEmpty(message = "No existen valores en el reembolso")
    private List<ValoresDto> reembolsosValores;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValoresDto {
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

}
