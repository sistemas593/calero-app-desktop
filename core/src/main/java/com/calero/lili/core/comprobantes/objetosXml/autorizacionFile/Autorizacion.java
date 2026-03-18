package com.calero.lili.core.comprobantes.objetosXml.autorizacionFile;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement
@XmlType(propOrder = {"estado", "numeroAutorizacion","fechaAutorizacion","ambiente","comprobante","mensaje"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Autorizacion {

    private String estado;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private String ambiente;
    private String comprobante;
    private List<Mensaje> mensaje;


    @XmlElementWrapper(name = "mensajes")
    public List<Mensaje> getMensaje() {
        return mensaje;
    }



 /*   public static void main(String args[]){

        //////////////////////////////////////////
        Autorizacion f1= new Autorizacion();
        //////////////////////////////////////////

        //////////////////////////////////////////
        //f1.setId("comprobante");
        //f1.setVersion("1.0");
        f1.setEstado("AUTORIZADO");
        f1.setNumeroAutorizacion("2503201412550217144062360010054583105");
        f1.setFechaAutorizacion("25/03/2014");
        f1.setAmbiente("PRUEBAS");
        f1.setComprobante("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<factura id=\"comprobante\" version=\"2.1.0\">\n" +
                "<infoTributaria>\n" +
                "<ambiente>1</ambiente>\n" +
                "<tipoEmision>1</tipoEmision>\n" +
                "<razonSocial>SOFTWARE ECUADOR</razonSocial>\n" +
                "<nombreComercial>SOFTWARE ECUADOR</nombreComercial>\n" +
                "<ruc>1714406236001</ruc>\n" +
                "<claveAcceso>1707202301171440623600110010010000001711717740410</claveAcceso>\n" +
                "<codDoc>01</codDoc>\n" +
                "<estab>001</estab>\n" +
                "<ptoEmi>001</ptoEmi>\n" +
                "<secuencial>000000171</secuencial>\n" +
                "<dirMatriz>Pinta y Amazonas</dirMatriz>\n" +
                "</infoTributaria>\n" +
                "<infoFactura>\n" +
                "<fechaEmision>17/07/2023</fechaEmision>\n" +
                "<dirEstablecimiento>Pinta y Amazonas</dirEstablecimiento>\n" +
                "<obligadoContabilidad>SI</obligadoContabilidad>\n" +
                "<tipoIdentificacionComprador>04</tipoIdentificacionComprador>\n" +
                "<razonSocialComprador>Liliana Pico</razonSocialComprador>\n" +
                "<identificacionComprador>1717740441001</identificacionComprador>\n" +
                "<direccionComprador>INMACONSA MZ33 S5</direccionComprador>\n" +
                "<totalSinImpuestos>1.00</totalSinImpuestos>\n" +
                "<totalDescuento>0.00</totalDescuento>\n" +
                "<totalConImpuestos>\n" +
                "<totalImpuesto>\n" +
                "<codigo>2</codigo>\n" +
                "<codigoPorcentaje>2</codigoPorcentaje>\n" +
                "<baseImponible>1.00</baseImponible>\n" +
                "<valor>0.12</valor>\n" +
                "</totalImpuesto>\n" +
                "</totalConImpuestos>\n" +
                "<propina>0.00</propina>\n" +
                "<importeTotal>1.12</importeTotal>\n" +
                "<moneda>DOLAR</moneda>\n" +
                "<pagos>\n" +
                "<pago>\n" +
                "<formaPago>01</formaPago>\n" +
                "<total>1.12</total>\n" +
                "</pago>\n" +
                "</pagos>\n" +
                "</infoFactura>\n" +
                "<detalles>\n" +
                "<detalle>\n" +
                "<codigoPrincipal>0001</codigoPrincipal>\n" +
                "<descripcion>Servicios prestados</descripcion>\n" +
                "<cantidad>1.0000</cantidad>\n" +
                "<precioUnitario>1.0000</precioUnitario>\n" +
                "<descuento>0.00</descuento>\n" +
                "<precioTotalSinImpuesto>1.00</precioTotalSinImpuesto>\n" +
                "<impuestos>\n" +
                "<impuesto>\n" +
                "<codigo>2</codigo>\n" +
                "<codigoPorcentaje>2</codigoPorcentaje>\n" +
                "<tarifa>12.00</tarifa>\n" +
                "<baseImponible>1.00</baseImponible>\n" +
                "<valor>0.12</valor>\n" +
                "</impuesto>\n" +
                "</impuestos>\n" +
                "</detalle>\n" +
                "\n" +
                "<detalle>\n" +
                "<codigoPrincipal>0001</codigoPrincipal>\n" +
                "<descripcion>Soooooo</descripcion>\n" +
                "<cantidad>1.0000</cantidad>\n" +
                "<precioUnitario>1.0000</precioUnitario>\n" +
                "<descuento>0.00</descuento>\n" +
                "<precioTotalSinImpuesto>1.00</precioTotalSinImpuesto>\n" +
                "<impuestos>\n" +
                "<impuesto>\n" +
                "<codigo>2</codigo>\n" +
                "<codigoPorcentaje>2</codigoPorcentaje>\n" +
                "<tarifa>12.00</tarifa>\n" +
                "<baseImponible>1.00</baseImponible>\n" +
                "<valor>0.12</valor>\n" +
                "</impuesto>\n" +
                "</impuestos>\n" +
                "</detalle>\n" +
                "\n" +
                "\n" +
                "</detalles>\n" +
                "<infoAdicional>\n" +
                "<campoAdicional nombre=\"email\">consultingglobal23@gmail.com</campoAdicional>\n" +
                "<campoAdicional nombre=\"telf\">3083808</campoAdicional>\n" +
                "<campoAdicional nombre=\"ciudad\">ZARUMA</campoAdicional>\n" +
                "</infoAdicional>\n" +
                "</factura>");

        Mensaje Det1=new Mensaje();
        Det1.setIdentificador("60");
        Det1.setMensaje("ESTE PROCESO FUE REALIZADO EN EL AMBIENTE DE PRUEBAS");
        Det1.setTipo("INFORMATIVO");

        List<Mensaje> listaMensaje=new ArrayList<Mensaje>();
        listaMensaje.add(Det1);
        f1.setMensaje(listaMensaje);

//        OutputStreamWriter out = null;
//        try {
//            JAXBContext context = JAXBContext.newInstance(
//                    new Class[]{Autorizacion.class});
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty("jaxb.encoding", "UTF-8");
//            marshaller.setProperty("jaxb.formatted.output",
//                    Boolean.valueOf(true));
//            String file="C:\\java\\workspace\\CaleroNew\\FeAutorizados\\test01.xml";
//
//            out = new OutputStreamWriter(new FileOutputStream(
//                    file), "UTF-8");
//            marshaller.marshal(f1, out);
//            out.close();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }*/
}
