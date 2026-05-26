package com.calero.lili.core.modAdminEmpresasSeries.dto;

import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdEmpresaSerieCreationRequestDto {

    private String serie;
    private String nombreComercial;

    private String direccionEstablecimiento;

    private String ciudad;

    private String telefono1;

    private String telefono2;

    private List<Documentos> documentos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Documentos {
        private TipoDocumentoSerie documento;
        private String numeroAutorizacion;
        private String secuencial;
        private FormatoDocumento formatoDocumento;
        private String desde;
        private String hasta;
        private String fechaVencimiento;

    }

}
