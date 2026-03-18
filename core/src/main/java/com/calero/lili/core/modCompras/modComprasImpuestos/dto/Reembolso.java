package com.calero.lili.core.modCompras.modComprasImpuestos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reembolso {

    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    private String codPaisPagoReemb;
    private String tipoProveedorReemb;
    private String codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String numeroAutorizacionReemb;


    @Valid
    @NotEmpty(message = "No existen valores en el reembolso")
    private List<ValoresDto> reembolsosValores;

    private PaisDto pais;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaisDto {
        private String codigoPais;
        private String pais;
    }

}
