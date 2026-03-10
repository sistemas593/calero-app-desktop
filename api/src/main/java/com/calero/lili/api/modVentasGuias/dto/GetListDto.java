package com.calero.lili.api.modVentasGuias.dto;

import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class GetListDto {

    private String sucursal;
    private UUID idGuia;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String fechaEmision;
    private String fechaIniTransporte;
    private String fechaFinTransporte;
    private int numeroItems;
    private EstadoDocumento estadoDocumento;
    private Integer emailEstado;
    private Boolean anulada;
    private Boolean impresa;
    private UUID idDestinatario;
    private String numeroIdentificacionDestinatario;
    private String razonSocialDestinatario;
    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String email;

}
