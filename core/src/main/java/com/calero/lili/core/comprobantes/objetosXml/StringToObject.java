package com.calero.lili.core.comprobantes.objetosXml;

import jakarta.xml.bind.JAXBException;

public class StringToObject {
    public static void main(String[] args) throws JAXBException {

      /*  String comprobante = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<factura id=\"comprobante\" version=\"1.0\">\n" +
                "    <infoTributaria>\n" +
                "        <ambiente>1</ambiente>\n" +
                "        <tipoEmision>1</tipoEmision>\n" +
                "        <razonSocial>Javier Cálero</razonSocial>\n" +
                "        <nombreComercial>comerc</nombreComercial>\n" +
                "        <ruc>1714406236001</ruc>\n" +
                "        <claveAcceso>2110201101179214673900110020010000000011234567813</claveAcceso>\n" +
                "        <codDoc>01</codDoc>\n" +
                "        <estab>001</estab>\n" +
                "        <ptoEmi>001</ptoEmi>\n" +
                "        <secuencial>000000124</secuencial>\n" +
                "        <dirMatriz>Pinta y Amazonas</dirMatriz>\n" +
                "    </infoTributaria>\n" +
                "    <infoFactura>\n" +
                "        <fechaEmision>01/12/2013</fechaEmision>\n" +
                "        <dirEstablecimiento>Pamela Egas</dirEstablecimiento>\n" +
                "        <contribuyenteEspecial>4521</contribuyenteEspecial>\n" +
                "        <obligadoContabilidad>NO</obligadoContabilidad>\n" +
                "        <comercioExterior>EXPORTADORString</comercioExterior>\n" +
                "        <incoTermFactura>FOB</incoTermFactura>\n" +
                "        <lugarIncoTerm>ECUADOR</lugarIncoTerm>\n" +
                "        <paisOrigen>593</paisOrigen>\n" +
                "        <puertoEmbarque>QUITO</puertoEmbarque>\n" +
                "        <puertoDestino>USA</puertoDestino>\n" +
                "        <paisDestino>110</paisDestino>\n" +
                "        <paisAdquisicion>593</paisAdquisicion>\n" +
                "        <tipoIdentificacionComprador>04</tipoIdentificacionComprador>\n" +
                "        <guiaRemision>001-001-000000001</guiaRemision>\n" +
                "        <razonSocialComprador>PRUEBAS SERVICIO DERENTAS INTERNAS</razonSocialComprador>\n" +
                "        <identificacionComprador>1717740441001</identificacionComprador>\n" +
                "        <totalSinImpuestos>295000.00</totalSinImpuestos>\n" +
                "        <totalDescuento>5000.00</totalDescuento>\n" +
                "        <totalConImpuestos>\n" +
                "            <totalImpuesto>\n" +
                "                <codigo>1</codigo>\n" +
                "                <codigoPorcentaje>3072</codigoPorcentaje>\n" +
                "                <baseImponible>1000.00</baseImponible>\n" +
                "                <tarifa>5.00</tarifa>\n" +
                "                <valor>10</valor>\n" +
                "            </totalImpuesto>\n" +
                "            <totalImpuesto>\n" +
                "                <codigo>2</codigo>\n" +
                "                <codigoPorcentaje>2</codigoPorcentaje>\n" +
                "                <baseImponible>2000.00</baseImponible>\n" +
                "                <tarifa>12.00</tarifa>\n" +
                "                <valor>20</valor>\n" +
                "            </totalImpuesto>\n" +
                "        </totalConImpuestos>\n" +
                "        <propina>100.98</propina>\n" +
                "        <fleteInternacional>0.00</fleteInternacional>\n" +
                "        <seguroInternacional>0.00</seguroInternacional>\n" +
                "        <gastosAduaneros>0.00</gastosAduaneros>\n" +
                "        <gastosTransporteOtros>0.00</gastosTransporteOtros>\n" +
                "        <importeTotal>341020.98</importeTotal>\n" +
                "        <moneda>DOLAR</moneda>\n" +
                "        <pagos>\n" +
                "            <pago>\n" +
                "                <formaPago>20</formaPago>\n" +
                "                <total>20</total>\n" +
                "            </pago>\n" +
                "            <pago>\n" +
                "                <formaPago>20</formaPago>\n" +
                "                <total>20</total>\n" +
                "            </pago>\n" +
                "        </pagos>\n" +
                "    </infoFactura>\n" +
                "    <detalles>\n" +
                "        <detalle>\n" +
                "            <codigoPrincipal>125BJC-01</codigoPrincipal>\n" +
                "            <codigoAuxiliar>1234D56789</codigoAuxiliar>\n" +
                "            <descripcion>CAMIONETA 4X4 DIESEL 3.7</descripcion>\n" +
                "            <cantidad>10.00</cantidad>\n" +
                "            <precioUnitario>30000.00</precioUnitario>\n" +
                "            <descuento>5000.00</descuento>\n" +
                "            <precioTotalSinImpuesto>295000.00</precioTotalSinImpuesto>\n" +
                "            <detallesAdicionales>\n" +
                "                <detAdicional nombre=\"Marca Chevrolet\" valor=\"Chevrolet\"/>\n" +
                "                <detAdicional nombre=\"Modelo\" valor=\"2012\"/>\n" +
                "                <detAdicional nombre=\"Chasis\" valor=\"8LDETA03V20003289\"/>\n" +
                "            </detallesAdicionales>\n" +
                "            <impuestos>\n" +
                "                <impuesto>\n" +
                "                    <codigo>3</codigo>\n" +
                "                    <codigoPorcentaje>3072</codigoPorcentaje>\n" +
                "                    <tarifa>5</tarifa>\n" +
                "                    <baseImponible>295000.00</baseImponible>\n" +
                "                    <valor>14750.00</valor>\n" +
                "                </impuesto>\n" +
                "            </impuestos>\n" +
                "        </detalle>\n" +
                "    </detalles>\n" +
                "    <infoSustitutivaGuiaRemision>\n" +
                "        <dirPartida>La Pinta E4-432 y Av. Amazonas</dirPartida>\n" +
                "        <dirDestinatario>La Pinta E4 - 432 y Av. Amazonas</dirDestinatario>\n" +
                "        <fechaIniTransporte>07/02/2020</fechaIniTransporte>\n" +
                "        <fechaFinTransporte>07/02/2020</fechaFinTransporte>\n" +
                "        <razonSocialTransportista>JAVIER CALERO</razonSocialTransportista>\n" +
                "        <tipoIdentificacionTransportista>04</tipoIdentificacionTransportista>\n" +
                "        <rucTransportista>1212121212001</rucTransportista>\n" +
                "        <placa>NA</placa>\n" +
                "        <destinos>\n" +
                "            <destino>\n" +
                "                <motivoTraslado>Venta</motivoTraslado>\n" +
                "                <ruta>NORTE</ruta>\n" +
                "            </destino>\n" +
                "        </destinos>\n" +
                "    </infoSustitutivaGuiaRemision>\n" +
                "    <reembolsos>\n" +
                "        <reembolsoDetalle>\n" +
                "            <tipoIdentificacionProveedorReembolso>04</tipoIdentificacionProveedorReembolso>\n" +
                "            <identificacionProveedorReembolso>1721964953001</identificacionProveedorReembolso>\n" +
                "            <codPaisPagoProveedorReembolso>593</codPaisPagoProveedorReembolso>\n" +
                "            <tipoProveedorReembolso>01</tipoProveedorReembolso>\n" +
                "            <codDocReembolso>01</codDocReembolso>\n" +
                "            <estabDocReembolso>001</estabDocReembolso>\n" +
                "            <ptoEmiDocReembolso>001</ptoEmiDocReembolso>\n" +
                "            <secuencialDocReembolso>000000123</secuencialDocReembolso>\n" +
                "            <fechaEmisionDocReembolso>01/04/2022</fechaEmisionDocReembolso>\n" +
                "            <numeroautorizacionDocReemb>1234564567</numeroautorizacionDocReemb>\n" +
                "            <detalleImpuestos>\n" +
                "                <detalleImpuesto>\n" +
                "                    <codigo>3</codigo>\n" +
                "                    <codigoPorcentaje>3072</codigoPorcentaje>\n" +
                "                    <tarifa>5</tarifa>\n" +
                "                    <baseImponibleReembolso>10.00</baseImponibleReembolso>\n" +
                "                    <impuestoReembolso>1.20</impuestoReembolso>\n" +
                "                </detalleImpuesto>\n" +
                "            </detalleImpuestos>\n" +
                "        </reembolsoDetalle>\n" +
                "    </reembolsos>\n" +
                "    <infoAdicional>\n" +
                "        <campoAdicional nombre=\"nombre\">xxxxx</campoAdicional>\n" +
                "        <campoAdicional nombre=\"nombre\">nombre impuesto</campoAdicional>\n" +
                "    </infoAdicional>\n" +
                "</factura>\n";

        Factura documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(Factura.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
            documento = (Factura) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: "+documento.getInfoFactura().getComercioExterior());
        } catch (JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
*/
    }
}
