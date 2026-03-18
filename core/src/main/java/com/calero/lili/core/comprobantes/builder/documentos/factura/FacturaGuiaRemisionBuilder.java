package com.calero.lili.core.comprobantes.builder.documentos.factura;

import com.calero.lili.core.comprobantes.objetosXml.factura.Destino;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoSustitutivaGuiaRemision;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FacturaGuiaRemisionBuilder {



    public void builderFacturaGuiaRemision(Factura model, VtVentaEntity venta, GeTerceroEntity transportista) {

        model.setInfoSustitutivaGuiaRemision(builderInfoSustitutivaGuiaRemision(venta.getSustitutivaGuiaRemision(), transportista));
    }

    private InfoSustitutivaGuiaRemision builderInfoSustitutivaGuiaRemision(VtVentaEntity.SustitutivaGuiaRemision venta,
                                                                           GeTerceroEntity transportista) {


        return InfoSustitutivaGuiaRemision.builder()
                .dirPartida(venta.getDirPartida())
                .dirDestinatario(venta.getDirDestinatario())
                .fechaIniTransporte(venta.getFechaIniTransporte())
                .fechaFinTransporte(venta.getFechaFinTransporte())
                .razonSocialTransportista(transportista.getTercero())
                .tipoIdentificacionTransportista(TipoIdentificacion.valueOf(transportista.getTipoIdentificacion()).getCodigo())
                .rucTransportista(transportista.getNumeroIdentificacion())
                .placa(transportista.getPlaca())
                .destino(builderListDestino(venta.getDestinos()))
                .build();
    }

    private List<Destino> builderListDestino(List<VtVentaEntity.Destino> list) {
        return list.stream()
                .map(this::builderDestino)
                .toList();
    }

    private Destino builderDestino(VtVentaEntity.Destino destino) {
        return Destino.builder()
                .motivoTraslado(destino.getMotivoTraslado())
                .codEstabDestino(destino.getCodEstabDestino())
                .ruta(destino.getRuta())
                .build();
    }
}
