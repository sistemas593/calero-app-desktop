package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder;

import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLLiquidacionesGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLRetencionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLGuiaRemisionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLRetencionGetDto;
import com.calero.lili.core.modCompras.impuestosXml.CpImpuestosFacturasOneProjection;
import com.calero.lili.core.modCompras.impuestosXml.CpLiquidacionOneProjection;
import com.calero.lili.core.modCompras.impuestosXml.CpRetencionesOneProjection;
import com.calero.lili.core.modCompras.impuestosXml.VtGuiaRemisionOneProjection;
import com.calero.lili.core.modCompras.impuestosXml.VtRetencionesOneProjection;
import com.calero.lili.core.modCompras.impuestosXml.VtVentasFacturaOneProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentosElectronicosComprobanteBuilder {
    @Autowired
    private final DocumentosElectronicosXMLComprobanteBuilder documentosElectronicosXMLComprobanteBuilder;

    public VtVentasXMLFacturaGetDto toFacturaVenta(VtVentasFacturaOneProjection projection) {
        if (projection == null) {
            return null;
        }

        VtVentasXMLFacturaGetDto vtVentasXMLFacturaGetDto = new VtVentasXMLFacturaGetDto();

        vtVentasXMLFacturaGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToFactura(projection.getComprobante()));
        vtVentasXMLFacturaGetDto.setIdVenta(projection.getIdVenta());
        vtVentasXMLFacturaGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        vtVentasXMLFacturaGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        vtVentasXMLFacturaGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return vtVentasXMLFacturaGetDto;
    }

    public VtVentasXMLNotaCreditoGetDto toNotaCreditoVenta(VtVentasFacturaOneProjection projection) {
        if (projection == null) {
            return null;
        }

        VtVentasXMLNotaCreditoGetDto vtVentasXMLNotaCreditoGetDto = new VtVentasXMLNotaCreditoGetDto();

        vtVentasXMLNotaCreditoGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToNotaCredito(projection.getComprobante()));
        vtVentasXMLNotaCreditoGetDto.setIdVenta(projection.getIdVenta());
        vtVentasXMLNotaCreditoGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        vtVentasXMLNotaCreditoGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        vtVentasXMLNotaCreditoGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return vtVentasXMLNotaCreditoGetDto;
    }

    public VtVentasXMLNotaDebitoGetDto toNotaDebitoVenta(VtVentasFacturaOneProjection projection) {
        if (projection == null) {
            return null;
        }

        VtVentasXMLNotaDebitoGetDto vtVentasXMLNotaDebitoGetDto = new VtVentasXMLNotaDebitoGetDto();

        vtVentasXMLNotaDebitoGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToNotaDebito(projection.getComprobante()));
        vtVentasXMLNotaDebitoGetDto.setIdVenta(projection.getIdVenta());
        vtVentasXMLNotaDebitoGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        vtVentasXMLNotaDebitoGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        vtVentasXMLNotaDebitoGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return vtVentasXMLNotaDebitoGetDto;
    }

    public VtVentasXMLGuiaRemisionGetDto toGuiaRemision(VtGuiaRemisionOneProjection projection) {
        if (projection == null) {
            return null;
        }

        VtVentasXMLGuiaRemisionGetDto vtVentasXMLGuiaRemisionGetDto = new VtVentasXMLGuiaRemisionGetDto();

        vtVentasXMLGuiaRemisionGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToGuiaRemision(projection.getComprobante()));
        vtVentasXMLGuiaRemisionGetDto.setIdGuia(projection.getIdGuia());
        vtVentasXMLGuiaRemisionGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        vtVentasXMLGuiaRemisionGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        vtVentasXMLGuiaRemisionGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return vtVentasXMLGuiaRemisionGetDto;
    }

    public CpComprasXMLLiquidacionesGetDto toLiquidacion(CpLiquidacionOneProjection projection) {
        if (projection == null) {
            return null;
        }

        CpComprasXMLLiquidacionesGetDto cpComprasXMLLiquidacionesGetDto = new CpComprasXMLLiquidacionesGetDto();

        cpComprasXMLLiquidacionesGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToLiquidacion(projection.getComprobante()));
        cpComprasXMLLiquidacionesGetDto.setIdLiquidacion(projection.getIdLiquidacion());
        cpComprasXMLLiquidacionesGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        cpComprasXMLLiquidacionesGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        cpComprasXMLLiquidacionesGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return cpComprasXMLLiquidacionesGetDto;
    }

    public CpImpuestosXMLFacturaGetDto toFacturaCompra(CpImpuestosFacturasOneProjection projection) {
        if (projection == null) {
            return null;
        }

        CpImpuestosXMLFacturaGetDto cpImpuestosXMLFacturaGetDto = new CpImpuestosXMLFacturaGetDto();

        cpImpuestosXMLFacturaGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToFactura(projection.getComprobante()));
        cpImpuestosXMLFacturaGetDto.setIdImpuestos(projection.getIdImpuestos());
        cpImpuestosXMLFacturaGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        cpImpuestosXMLFacturaGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());
        cpImpuestosXMLFacturaGetDto.setTipoDocumento(projection.getTipoDocumento());
        cpImpuestosXMLFacturaGetDto.setDestino(projection.getDestino());

        return cpImpuestosXMLFacturaGetDto;
    }

    public CpImpuestosXMLNotaCreditoGetDto toNotaCreditoCompra(CpImpuestosFacturasOneProjection projection) {
        if (projection == null) {
            return null;
        }

        CpImpuestosXMLNotaCreditoGetDto cpImpuestosXMLNotaCreditoGetDto = new CpImpuestosXMLNotaCreditoGetDto();

        cpImpuestosXMLNotaCreditoGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToNotaCredito(projection.getComprobante()));
        cpImpuestosXMLNotaCreditoGetDto.setIdImpuestos(projection.getIdImpuestos());
        cpImpuestosXMLNotaCreditoGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        cpImpuestosXMLNotaCreditoGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());
        cpImpuestosXMLNotaCreditoGetDto.setTipoDocumento(projection.getTipoDocumento());
        cpImpuestosXMLNotaCreditoGetDto.setDestino(projection.getDestino());

        return cpImpuestosXMLNotaCreditoGetDto;
    }

    public CpImpuestosXMLNotaDebitoGetDto toNotaDebitoCompra(CpImpuestosFacturasOneProjection projection) {
        if (projection == null) {
            return null;
        }

        CpImpuestosXMLNotaDebitoGetDto cpImpuestosXMLNotaDebitoGetDto = new CpImpuestosXMLNotaDebitoGetDto();

        cpImpuestosXMLNotaDebitoGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToNotaDebito(projection.getComprobante()));
        cpImpuestosXMLNotaDebitoGetDto.setIdImpuestos(projection.getIdImpuestos());
        cpImpuestosXMLNotaDebitoGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        cpImpuestosXMLNotaDebitoGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());
        cpImpuestosXMLNotaDebitoGetDto.setTipoDocumento(projection.getTipoDocumento());
        cpImpuestosXMLNotaDebitoGetDto.setDestino(projection.getDestino());

        return cpImpuestosXMLNotaDebitoGetDto;
    }

    public VtVentasXMLRetencionGetDto toRetencionVenta(VtRetencionesOneProjection projection) {
        if (projection == null) {
            return null;
        }

        VtVentasXMLRetencionGetDto vtVentasXMLRetencionGetDto = new VtVentasXMLRetencionGetDto();

        vtVentasXMLRetencionGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToRetencion(projection.getComprobante()));
        vtVentasXMLRetencionGetDto.setIdRetencion(projection.getIdRetencion());
        vtVentasXMLRetencionGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        vtVentasXMLRetencionGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        vtVentasXMLRetencionGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return vtVentasXMLRetencionGetDto;
    }

    public CpComprasXMLRetencionGetDto toRetencionCompra(CpRetencionesOneProjection projection) {
        if (projection == null) {
            return null;
        }

        CpComprasXMLRetencionGetDto cpComprasXMLRetencionGetDto = new CpComprasXMLRetencionGetDto();

        cpComprasXMLRetencionGetDto.setComprobante(documentosElectronicosXMLComprobanteBuilder.comprobanteToRetencion(projection.getComprobante()));
        cpComprasXMLRetencionGetDto.setIdRetencion(projection.getIdRetencion());
        cpComprasXMLRetencionGetDto.setEstadoDocumento(projection.getEstadoDocumento());
        cpComprasXMLRetencionGetDto.setNumeroAutorizacion(projection.getNumeroAutorizacion());
        cpComprasXMLRetencionGetDto.setFechaAutorizacion(projection.getFechaAutorizacion());

        return cpComprasXMLRetencionGetDto;
    }


}
