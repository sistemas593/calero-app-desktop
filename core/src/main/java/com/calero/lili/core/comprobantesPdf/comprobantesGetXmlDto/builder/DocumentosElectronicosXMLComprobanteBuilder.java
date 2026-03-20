package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder;

import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
@AllArgsConstructor

public class DocumentosElectronicosXMLComprobanteBuilder {

    public Factura comprobanteToFactura(String comprobante){
        comprobante=comprobante.replace("<![CDATA[", "").replace("]]>", "");
        Factura documento = null;
        jakarta.xml.bind.JAXBContext jaxbContext1 = null;
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller1 = null;
        try {
            jaxbContext1 = jakarta.xml.bind.JAXBContext.newInstance(Factura.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (Factura) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: "+documento.getInfoFactura().getComercioExterior());
        } catch (jakarta.xml.bind.JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
        return documento;
    }

    public NotaCredito comprobanteToNotaCredito(String comprobante){
        comprobante=comprobante.replace("<![CDATA[", "").replace("]]>", "");
        NotaCredito documento = null;
        jakarta.xml.bind.JAXBContext jaxbContext1 = null;
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller1 = null;
        try {
            jaxbContext1 = jakarta.xml.bind.JAXBContext.newInstance(NotaCredito.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (NotaCredito) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (jakarta.xml.bind.JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
        return documento;
    }

    public NotaDebito comprobanteToNotaDebito(String comprobante){
        comprobante=comprobante.replace("<![CDATA[", "").replace("]]>", "");
        NotaDebito documento = null;
        jakarta.xml.bind.JAXBContext jaxbContext1 = null;
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller1 = null;
        try {
            jaxbContext1 = jakarta.xml.bind.JAXBContext.newInstance(NotaDebito.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (NotaDebito) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (jakarta.xml.bind.JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
        return documento;
    }

    public GuiaRemision comprobanteToGuiaRemision(String comprobante){
        comprobante=comprobante.replace("<![CDATA[", "").replace("]]>", "");
        GuiaRemision documento = null;
        jakarta.xml.bind.JAXBContext jaxbContext1 = null;
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller1 = null;
        try {
            jaxbContext1 = jakarta.xml.bind.JAXBContext.newInstance(GuiaRemision.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (GuiaRemision) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
        } catch (jakarta.xml.bind.JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
        return documento;
    }

    public LiquidacionCompra comprobanteToLiquidacion(String comprobante){
        comprobante=comprobante.replace("<![CDATA[", "").replace("]]>", "");
        LiquidacionCompra documento = null;
        jakarta.xml.bind.JAXBContext jaxbContext1 = null;
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller1 = null;
        try {
            jaxbContext1 = jakarta.xml.bind.JAXBContext.newInstance(LiquidacionCompra.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (LiquidacionCompra) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
        } catch (jakarta.xml.bind.JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
        return documento;
    }

    public ComprobanteRetencion comprobanteToRetencion(String comprobante){
        comprobante=comprobante.replace("<![CDATA[", "").replace("]]>", "");
        ComprobanteRetencion documento = null;
        jakarta.xml.bind.JAXBContext jaxbContext1 = null;
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller1 = null;
        try {
            jaxbContext1 = jakarta.xml.bind.JAXBContext.newInstance(ComprobanteRetencion.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (ComprobanteRetencion) jaxbUnmarshaller1.unmarshal(new StringReader(comprobante));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: "+documento.getInfoCompRetencion().getDirEstablecimiento());
        } catch (jakarta.xml.bind.JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }
        return documento;
    }

}
