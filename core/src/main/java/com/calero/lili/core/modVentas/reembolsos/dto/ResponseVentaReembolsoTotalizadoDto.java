package com.calero.lili.core.modVentas.reembolsos.dto;

import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.TipoTerceroPerSoc;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ResponseVentaReembolsoTotalizadoDto {

    private UUID idVentaReembolsos;
    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    private TipoTerceroPerSoc tipoProveedorReemb;
    private DocumentoEnum codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String numeroAutorizacionReemb;
    private String fechaAutorizacionReemb;
    private String codigoPais;
    private String pais;
    private UUID idVenta;
    private Boolean existeComprobante;

    private List<ReembolsoValoresDto> reembolsosValores;


}
