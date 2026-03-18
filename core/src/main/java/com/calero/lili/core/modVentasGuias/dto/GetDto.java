package com.calero.lili.core.modVentasGuias.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modVentasGuias.dto.detalles.DetalleGetDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetDto {

    private UUID idGuia;
    private String sucursal;
    private String fechaEmision;

    private String fechaIniTransporte;
    private String fechaFinTransporte;

    private String serie;
    private String secuencial;

    private UUID idDestinatario;
    private TipoIdentificacion tipoIdentificacionDestinatario;
    private String numeroIdentificacionDestinatario;
    private String razonSocialDestinatario;
    private String dirDestinatario;
    private String email;

    private String docAduaneroUnico;
    private String codEstabDestino;

    private Boolean anulada;

    private Integer numeroItems;

    private String fechaAnulacion;

    private List<DetalleGetDto> detalle;

    private List<InformacionAdicionalDto> informacionAdicional;

    private String fechaAutorizacion;
    private String claveAcceso;

    private String dirPartida;

    private String motivoTraslado;
    private String ruta;

    private Transportista transportista;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Transportista {
        private UUID idTransportista;
        private String razonSocialTransportista;
        private String tipoIdentificacionTransportista;
        private String numeroIdentificacionTransportista;
        private String emailTransportista;
        private String placa;
        private String telefono;

    }

    private Integer ambiente;
    private FormatoDocumento formatoDocumento;

    private String codDocSustento;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DocumentoSustentoDto documentoSustento;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DocumentoSustentoDto {

        private String serieDocSustento;
        private String secuencialDocSustento;
        private String numAutDocSustento;
        private String fechaEmisionDocSustento;
    }


}
