package com.calero.lili.core.comprobantes.builder.documentos.guiaRemision;

import com.calero.lili.core.comprobantes.builder.documentos.CampoAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.Destinatario;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.InfoGuiaRemision;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaDetalleEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GuiaRemisionBuilder {

    private final InfoTributariaGuiaRemisionBuilder infoTributaria;
    private final CampoAdicionalBuilder campoAdicionalBuilder;
    private final FormatoValores formatoValores;

    public GuiaRemision builderGuiaRemision(VtGuiaEntity guiaRemision, AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie) {
        return GuiaRemision.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_1_1_0)
                .infoTributaria(infoTributaria.builderInfoTributaria(guiaRemision, empresa, serie))
                .infoGuiaRemision(builderInfoGuiaRemision(guiaRemision, empresa, serie))
                .destinatario(builderListDestinatario(guiaRemision))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(guiaRemision.getInformacionAdicional()))
                .build();
    }

    private List<Destinatario> builderListDestinatario(VtGuiaEntity guiaRemision) {
        List<Destinatario> destinatarios = new ArrayList<>();
        destinatarios.add(builderDestinatario(guiaRemision));
        return destinatarios;
    }

    private Destinatario builderDestinatario(VtGuiaEntity guiaRemision) {
        Destinatario destinatario = Destinatario.builder()
                .identificacionDestinatario(guiaRemision.getDestinatario().getNumeroIdentificacion())
                .razonSocialDestinatario(guiaRemision.getDestinatario().getTercero())
                .dirDestinatario(guiaRemision.getDestinatario().getDireccion())
                .motivoTraslado(guiaRemision.getMotivoTraslado())
                .docAduaneroUnico(guiaRemision.getDocAduaneroUnico())
                .codEstabDestino(guiaRemision.getCodEstabDestino())
                .detalle(builderListDetalle(guiaRemision.getDetalle()))
                .build();

        builderDocSustentoInfo(guiaRemision, destinatario);
        return destinatario;
    }


    private void builderDocSustentoInfo(VtGuiaEntity guiaRemision, Destinatario destinatario) {

        if (Objects.nonNull(guiaRemision.getCodDocSustento())) {
            destinatario.setCodDocSustento(guiaRemision.getCodDocSustento());
            destinatario.setNumDocSustento(validarNumDocSustento(guiaRemision.getSerieDocSustento(), guiaRemision.getSecuencialDocSustento()));
            destinatario.setNumAutDocSustento(guiaRemision.getNumAutDocSustento());
            destinatario.setFechaEmisionDocSustento(DateUtils.toString(guiaRemision.getFechaEmisionDocSustento()));
        }
    }

    private String validarNumDocSustento(String serieDocSustento, String secuencialDocSustento) {
        String p3 = serieDocSustento.substring(0, 3);
        String s3 = serieDocSustento.substring(3, 6);
        return p3 + "-" + s3 + "-" + secuencialDocSustento;

    }

    private List<Detalle> builderListDetalle(List<VtGuiaDetalleEntity> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private Detalle builderDetalle(VtGuiaDetalleEntity detalle) {
        return Detalle.builder()
                .codigoInterno(detalle.getCodigoPrincipal())
                .codigoAdicional(detalle.getCodigoAuxiliar())
                .descripcion(detalle.getDescripcion())
                .cantidad(formatoValores.convertirBigDecimalToString(detalle.getCantidad()))
                .detAdicional(builderDetalleAdicional(detalle.getDetAdicional()))
                .build();
    }

    private List<DetAdicional> builderDetalleAdicional(List<VtGuiaDetalleEntity.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleAdicional)
                .toList();
    }

    private DetAdicional builderDetalleAdicional(VtGuiaDetalleEntity.DetalleAdicional detalleAdicional) {
        return DetAdicional.builder()
                .nombre(detalleAdicional.getNombre())
                .valor(detalleAdicional.getValor())
                .build();
    }

    private InfoGuiaRemision builderInfoGuiaRemision(VtGuiaEntity guiaRemision, AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie) {
        return InfoGuiaRemision.builder()
                .dirEstablecimiento(serie.getDireccionEstablecimiento())
                .dirPartida(guiaRemision.getDirPartida())
                .contribuyenteEspecial(Objects.isNull(empresa.getContribuyenteEspecial())
                        || empresa.getContribuyenteEspecial().isEmpty() ? null : empresa.getContribuyenteEspecial())
                .razonSocialTransportista(guiaRemision.getTransportista().getTercero())
                .tipoIdentificacionTransportista(TipoIdentificacion.valueOf(guiaRemision.getTransportista().getTipoIdentificacion()).getCodigo())
                .rucTransportista(guiaRemision.getTransportista().getNumeroIdentificacion())
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .fechaIniTransporte(DateUtils.toString(guiaRemision.getFechaIniTransporte()))
                .fechaFinTransporte(DateUtils.toString(guiaRemision.getFechaFinTransporte()))
                .placa(guiaRemision.getTransportista().getPlaca())
                .build();
    }

}
