package com.calero.lili.api.modAdminEmpresasSeries.dto;

import com.calero.lili.core.enums.FormatoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
public class AdEmpresaSerieGetListDto {

    private UUID idSerie;

    private String serie;

    private String nombreComercial;

    private String direccionEstablecimiento;

    private String ciudad;

    private String telefono1;

    private String telefono2;

    // para crear de uno en uno los clientes
    private List<Documentos> documentos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Documentos {
        private String documento;
        private String numeroAutorizacion;
        private String secuencial;
        private FormatoDocumento formatoDocumento;
        private String desde;
        private String hasta;
        private String fechaVencimiento;
    }

}
