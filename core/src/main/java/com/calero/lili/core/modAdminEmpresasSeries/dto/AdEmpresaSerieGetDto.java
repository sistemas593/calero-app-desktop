package com.calero.lili.core.modAdminEmpresasSeries.dto;

import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEmpresaSerieGetDto {

    private UUID idSerie;

    private String serie;

    private String nombreComercial;

    private String direccionEstablecimiento;

    private String ciudad;

    private String telefono1;

    private String telefono2;

    private List<Documentos> documentos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Documentos {
        private UUID idDocumento;
        private TipoDocumentoSerie documento;
        private String numeroAutorizacion;
        private Integer secuencial;
        private FormatoDocumento formatoDocumento;
        private String desde;
        private String hasta;
        private String fechaVencimiento;

    }

}
