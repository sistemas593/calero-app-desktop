package com.calero.lili.core.modVentas.reembolsos.dto;

import com.calero.lili.core.enums.TipoTerceroPerSoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ResponseReembolsoDto {

    private UUID idVentaReembolsos;
    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    private TipoTerceroPerSoc tipoProveedorReemb;
    private String codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String numeroAutorizacionReemb;
    private String codigoPais;
    private String pais;
    private UUID idVenta;

    private List<ValoresDto> reembolsosValores;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ValoresDto {
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }
}
