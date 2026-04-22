package com.calero.lili.core.modVentasGuias.builder;

import com.calero.lili.core.builder.InformacionAdicionalBuilder;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.modVentasGuias.dto.CreationRequestGuiaRemisionDto;
import com.calero.lili.core.modVentasGuias.dto.GetDto;
import com.calero.lili.core.modVentasGuias.dto.GetListDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtGuiaBuilder {

    private final InformacionAdicionalBuilder informacionAdicionalBuilder;
    private final VtGuiaDetalleBuilder vtGuiaDetalleBuilder;

    public VtGuiaEntity builderEntity(CreationRequestGuiaRemisionDto model, Long idData, Long idEmpresa) {
        return VtGuiaEntity.builder()
                .idGuia(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .motivoTraslado(model.getMotivoTraslado())
                .ruta(model.getRuta())
                .docAduaneroUnico(model.getDocAduaneroUnico())
                .codEstabDestino(model.getCodEstabDestino())
                .codDocSustento(model.getCodDocSustento())
                .serieDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? model.getDocumentoSustento().getSerieDocSustento() : null)
                .secuencialDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? model.getDocumentoSustento().getSecuencialDocSustento() : null)
                .numAutDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? model.getDocumentoSustento().getNumAutDocSustento() : null)
                .fechaEmisionDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? DateUtils.toLocalDate(model.getDocumentoSustento().getFechaEmisionDocSustento())
                        : null)
                .formatoDocumento(model.getFormatoDocumento())
                .numeroItems(model.getNumeroItems())
                .anulada(Boolean.FALSE)
                .impresa(model.getImpresa())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .detalle(vtGuiaDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaIniTransporte(DateUtils.toLocalDate(model.getFechaIniTransporte()))
                .fechaFinTransporte(DateUtils.toLocalDate(model.getFechaFinTransporte()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento())
                        ? model.getCodigoDocumento()
                        : "06")
                .dirPartida(model.getDirPartida())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .build();
    }


    public VtGuiaEntity builderUpdateEntity(CreationRequestGuiaRemisionDto model, VtGuiaEntity item) {
        return VtGuiaEntity.builder()
                .idGuia(item.getIdGuia())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .motivoTraslado(model.getMotivoTraslado())
                .ruta(model.getRuta())
                .docAduaneroUnico(model.getDocAduaneroUnico())
                .codEstabDestino(model.getCodEstabDestino())
                .serieDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? model.getDocumentoSustento().getSerieDocSustento() : null)
                .secuencialDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? model.getDocumentoSustento().getSecuencialDocSustento() : null)
                .numAutDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? model.getDocumentoSustento().getNumAutDocSustento() : null)
                .fechaEmisionDocSustento(Objects.nonNull(model.getDocumentoSustento())
                        ? DateUtils.toLocalDate(model.getDocumentoSustento().getFechaEmisionDocSustento())
                        : null)
                .fechaAnulacion(item.getFechaAnulacion())
                .formatoDocumento(model.getFormatoDocumento())
                .numeroItems(model.getNumeroItems())
                .anulada(item.getAnulada())
                .impresa(model.getImpresa())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .detalle(vtGuiaDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaIniTransporte(DateUtils.toLocalDate(model.getFechaIniTransporte()))
                .fechaFinTransporte(DateUtils.toLocalDate(model.getFechaFinTransporte()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento())
                        ? model.getCodigoDocumento()
                        : "06")
                .dirPartida(model.getDirPartida())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .build();
    }


    public GetDto.Transportista builderDtoTransportista(GeTerceroEntity model) {
        return GetDto.Transportista.builder()
                .idTransportista(model.getIdTercero())
                .razonSocialTransportista(model.getTercero())
                .tipoIdentificacionTransportista(model.getTipoIdentificacion())
                .numeroIdentificacionTransportista(model.getNumeroIdentificacion())
                .placa(model.getPlaca())
                .telefono(model.getTelefonos())
                .build();
    }


    public GetDto builderResponse(VtGuiaEntity model) {
        return GetDto.builder()
                .idGuia(model.getIdGuia())
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .motivoTraslado(model.getMotivoTraslado())
                .ruta(model.getRuta())
                .docAduaneroUnico(model.getDocAduaneroUnico())
                .codEstabDestino(model.getCodEstabDestino())
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toString(model.getFechaAnulacion()) : null)
                .fechaAutorizacion(Objects.nonNull(model.getFechaAutorizacion())
                        ? model.getFechaAutorizacion() : null)
                .numeroItems(model.getNumeroItems())
                .anulada(model.getAnulada())
                .email(model.getEmail())
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .detalle(vtGuiaDetalleBuilder.builderListDto((model.getDetalle())))
                .transportista(builderDtoTransportista(model.getTransportista()))
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaIniTransporte(Objects.nonNull(model.getFechaIniTransporte())
                        ? DateUtils.toString(model.getFechaIniTransporte()) : null)
                .fechaFinTransporte(Objects.nonNull(model.getFechaFinTransporte())
                        ? DateUtils.toString(model.getFechaFinTransporte()) : null)
                .dirPartida(model.getDirPartida())
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .idDestinatario(model.getDestinatario().getIdTercero())
                .numeroIdentificacionDestinatario(model.getDestinatario().getNumeroIdentificacion())
                .tipoIdentificacionDestinatario(TipoIdentificacion.obtenerTipoIdentificacion(model.getDestinatario().getTipoIdentificacion()))
                .razonSocialDestinatario(model.getDestinatario().getTercero())
                .dirDestinatario(model.getDestinatario().getDireccion())
                .codDocSustento(model.getCodDocSustento())
                .documentoSustento(builderDocumentoSustentoResponse(model))
                .build();
    }

    // Revisar el retorno del dto 
    private GetDto.DocumentoSustentoDto builderDocumentoSustentoResponse(VtGuiaEntity model) {
        if (Objects.isNull(model.getSerieDocSustento())) return null;
        return GetDto.DocumentoSustentoDto.builder()
                .serieDocSustento(model.getSerieDocSustento())
                .secuencialDocSustento(model.getSecuencialDocSustento())
                .numAutDocSustento(model.getNumAutDocSustento())
                .fechaEmisionDocSustento(Objects.nonNull(model.getFechaEmisionDocSustento())
                        ? DateUtils.toString(model.getFechaEmisionDocSustento()) : null)
                .build();
    }

    public GetListDto builderListResponse(VtGuiaEntity model) {
        return GetListDto.builder()
                .idGuia(model.getIdGuia())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroItems(model.getNumeroItems())
                .anulada(model.getAnulada())
                .fechaEmision(model.getFechaEmision().toString())
                .fechaIniTransporte(Objects.nonNull(model.getFechaIniTransporte())
                        ? DateUtils.toString(model.getFechaIniTransporte()) : null)
                .fechaFinTransporte(Objects.nonNull(model.getFechaFinTransporte())
                        ? DateUtils.toString(model.getFechaFinTransporte()) : null)
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .email(model.getDestinatario().getEmail())
                .idDestinatario(model.getDestinatario().getIdTercero())
                .numeroIdentificacionDestinatario(model.getDestinatario().getNumeroIdentificacion())
                .razonSocialDestinatario(model.getDestinatario().getTercero())
                .estadoDocumento(model.getEstadoDocumento())
                .emailEstado(model.getEmailEstado())
                .build();
    }

}
