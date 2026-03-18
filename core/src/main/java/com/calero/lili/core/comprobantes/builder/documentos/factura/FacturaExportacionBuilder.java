package com.calero.lili.core.comprobantes.builder.documentos.factura;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.modVentas.VtVentaEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FacturaExportacionBuilder {

    private final FormatoValores formatoValores;

    public void builderFacturaExportacion(Factura model, VtVentaEntity venta) {

        model.getInfoFactura().setComercioExterior(venta.getExportacion().getComercioExterior().getLiquidar());
        model.getInfoFactura().setIncoTermFactura(venta.getExportacion().getIncoTermFactura());
        model.getInfoFactura().setLugarIncoTerm(venta.getExportacion().getLugarIncoTerm());
        model.getInfoFactura().setPaisOrigen(venta.getExportacion().getPaisOrigen());
        model.getInfoFactura().setPuertoEmbarque(venta.getExportacion().getPuertoEmbarque());
        model.getInfoFactura().setPuertoDestino(venta.getExportacion().getPuertoDestino());
        model.getInfoFactura().setPaisDestino(venta.getExportacion().getPaisDestino());

        model.getInfoFactura().setFleteInternacional(formatoValores.convertirBigDecimalToString(venta.getFleteInternacional()));
        model.getInfoFactura().setSeguroInternacional(formatoValores.convertirBigDecimalToString(venta.getSeguroInternacional()));
        model.getInfoFactura().setGastosAduaneros(formatoValores.convertirBigDecimalToString(venta.getGastosAduaneros()));
        model.getInfoFactura().setGastosTransporteOtros(formatoValores.convertirBigDecimalToString(venta.getGastosTransporteOtros()));

    }


}
