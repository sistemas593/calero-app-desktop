package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantesPdf.ComprobanteRetencionPdf;
import com.calero.lili.core.comprobantesPdf.FacturaPdf;
import com.calero.lili.core.comprobantesPdf.GuiaRemisionPdf;
import com.calero.lili.core.comprobantesPdf.LiquidacionCompraPdf;
import com.calero.lili.core.comprobantesPdf.NotaCreditoPdf;
import com.calero.lili.core.comprobantesPdf.NotaDebitoPdf;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.EnvioCorreoDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StCorreoRequestDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;

@Service
@RequiredArgsConstructor

public class SetearCorreoServiceImpl {

    private final FacturaPdf facturaPdf;
    private final NotaCreditoPdf notaCreditoPdf;
    private final NotaDebitoPdf notaDebitoPdf;
    private final GuiaRemisionPdf guiaRemisionPdf;
    private final LiquidacionCompraPdf liquidacionCompraPdf;
    private final ComprobanteRetencionPdf comprobanteRetencionPdf;



    @Transactional
    public StCorreoRequestDto seterarRequestCorreo(EnvioCorreoDto envioCorreoDto, byte[] imageBytes) {

        StCorreoRequestDto request = new StCorreoRequestDto();

        if (envioCorreoDto.getCodigoDocumento().equals("01")){
            Factura documento = null;
            JAXBContext jaxbContext1 = null;
            Unmarshaller jaxbUnmarshaller1 = null;

            try {
                jaxbContext1 = JAXBContext.newInstance(Factura.class);
                jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
                //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
                documento = (Factura) jaxbUnmarshaller1.unmarshal(new StringReader(envioCorreoDto.getComprobante()));
                System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: "+documento.getInfoFactura().getComercioExterior());
            } catch (JAXBException ex) {
                //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error 1");
            }

            // GENERANDO EL PDF
            byte[] bytes = facturaPdf.generarPdf(documento, envioCorreoDto.getNumeroAutorizacion(), envioCorreoDto.getFechaAutorizacion(), imageBytes);
            request.setPdf(Base64.getEncoder().encodeToString(bytes));
            request.setNombreEmisor(documento.getInfoTributaria().getRazonSocial());
            request.setRucEmisor(documento.getInfoTributaria().getRuc());

        }

        if (envioCorreoDto.getCodigoDocumento().equals("03")){
            LiquidacionCompra documento = null;
            JAXBContext jaxbContext1 = null;
            Unmarshaller jaxbUnmarshaller1 = null;

            try {
                jaxbContext1 = JAXBContext.newInstance(LiquidacionCompra.class);
                jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
                //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
                documento = (LiquidacionCompra) jaxbUnmarshaller1.unmarshal(new StringReader(envioCorreoDto.getComprobante()));
                System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: "+documento.getInfoTributaria().getClaveAcceso());
            } catch (JAXBException ex) {
                //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error 1");
            }

            // GENERANDO EL PDF
            byte[] bytes = liquidacionCompraPdf.generarPdf(documento, envioCorreoDto.getNumeroAutorizacion(), envioCorreoDto.getFechaAutorizacion(), imageBytes);
            request.setPdf(Base64.getEncoder().encodeToString(bytes));
            request.setNombreEmisor(documento.getInfoTributaria().getRazonSocial());
            request.setRucEmisor(documento.getInfoTributaria().getRuc());

        }

        if (envioCorreoDto.getCodigoDocumento().equals("04")){
            NotaCredito documento = null;
            JAXBContext jaxbContext1 = null;
            Unmarshaller jaxbUnmarshaller1 = null;

            try {
                jaxbContext1 = JAXBContext.newInstance(NotaCredito.class);
                jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
                //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
                documento = (NotaCredito) jaxbUnmarshaller1.unmarshal(new StringReader(envioCorreoDto.getComprobante()));
                System.out.println("Si se pudo leer el String y convertirlo en objeto NC: "+documento.getInfoTributaria().getClaveAcceso());
            } catch (JAXBException ex) {
                //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error 1");
            }

            // GENERANDO EL PDF
            byte[] bytes = notaCreditoPdf.generarPdf(documento, envioCorreoDto.getNumeroAutorizacion(), envioCorreoDto.getFechaAutorizacion(), imageBytes);
            request.setPdf(Base64.getEncoder().encodeToString(bytes));
            request.setNombreEmisor(documento.getInfoTributaria().getRazonSocial());
            request.setRucEmisor(documento.getInfoTributaria().getRuc());

        }

        if (envioCorreoDto.getCodigoDocumento().equals("05")){
            NotaDebito documento = null;
            JAXBContext jaxbContext1 = null;
            Unmarshaller jaxbUnmarshaller1 = null;

            try {
                jaxbContext1 = JAXBContext.newInstance(NotaDebito.class);
                jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
                //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
                documento = (NotaDebito) jaxbUnmarshaller1.unmarshal(new StringReader(envioCorreoDto.getComprobante()));
                System.out.println("Si se pudo leer el String y convertirlo en objeto NC: "+documento.getInfoTributaria().getClaveAcceso());
            } catch (JAXBException ex) {
                //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error 1");
            }

            // GENERANDO EL PDF
            byte[] bytes = notaDebitoPdf.generarPdf(documento, envioCorreoDto.getNumeroAutorizacion(), envioCorreoDto.getFechaAutorizacion(), imageBytes);
            request.setPdf(Base64.getEncoder().encodeToString(bytes));
            request.setNombreEmisor(documento.getInfoTributaria().getRazonSocial());
            request.setRucEmisor(documento.getInfoTributaria().getRuc());

        }

        if (envioCorreoDto.getCodigoDocumento().equals("06")){
            GuiaRemision documento = null;
            JAXBContext jaxbContext1 = null;
            Unmarshaller jaxbUnmarshaller1 = null;

            try {
                jaxbContext1 = JAXBContext.newInstance(GuiaRemision.class);
                jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
                //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
                documento = (GuiaRemision) jaxbUnmarshaller1.unmarshal(new StringReader(envioCorreoDto.getComprobante()));
                System.out.println("Si se pudo leer el String y convertirlo en objeto NC: "+documento.getInfoTributaria().getClaveAcceso());
            } catch (JAXBException ex) {
                //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error 1");
            }

            // GENERANDO EL PDF
            byte[] bytes = guiaRemisionPdf.generarPdf(documento, envioCorreoDto.getNumeroAutorizacion(), envioCorreoDto.getFechaAutorizacion(), imageBytes);
            request.setPdf(Base64.getEncoder().encodeToString(bytes));
            request.setNombreEmisor(documento.getInfoTributaria().getRazonSocial());
            request.setRucEmisor(documento.getInfoTributaria().getRuc());

        }

        if (envioCorreoDto.getCodigoDocumento().equals("07")){
            ComprobanteRetencion documento = null;
            JAXBContext jaxbContext1 = null;
            Unmarshaller jaxbUnmarshaller1 = null;

            try {
                jaxbContext1 = JAXBContext.newInstance(ComprobanteRetencion.class);
                jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
                //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
                documento = (ComprobanteRetencion) jaxbUnmarshaller1.unmarshal(new StringReader(envioCorreoDto.getComprobante()));
                System.out.println("Si se pudo leer el String y convertirlo en objeto NC: "+documento.getInfoTributaria().getClaveAcceso());
            } catch (JAXBException ex) {
                //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error 1");
            }

            // GENERANDO EL PDF
            byte[] bytes = comprobanteRetencionPdf.generarPdf(documento, envioCorreoDto.getNumeroAutorizacion(), envioCorreoDto.getFechaAutorizacion(), imageBytes);
            request.setPdf(Base64.getEncoder().encodeToString(bytes));
            request.setNombreEmisor(documento.getInfoTributaria().getRazonSocial());
            request.setRucEmisor(documento.getInfoTributaria().getRuc());

        }


        Autorizacion aut = new Autorizacion();
        aut.setComprobante(envioCorreoDto.getComprobante()); //"<![CDATA[" + + "]]>"
        aut.setFechaAutorizacion(envioCorreoDto.getFechaAutorizacion());
        aut.setNumeroAutorizacion(envioCorreoDto.getNumeroAutorizacion());
        aut.setEstado("AUTORIZADO");


        try {
            JAXBContext context = JAXBContext.newInstance(
                    new Class[]{Autorizacion.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(aut, stringWriter);
            request.setXml(    Base64.getEncoder().encodeToString( stringWriter.toString().getBytes())) ;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.setMailFrom("mi-empresa@software.com.ec");
        request.setNombreReceptor(envioCorreoDto.getNombreReceptor());
        request.setCodigoDocumento(envioCorreoDto.getCodigoDocumento());
        request.setSerie(envioCorreoDto.getSerie());
        request.setSecuencia(envioCorreoDto.getSecuencial());
        request.setFechaEmision(envioCorreoDto.getFechaEmision());
        request.setClaveAcceso(envioCorreoDto.getClaveAcceso());
        request.setTo(envioCorreoDto.getEmail());

        //venta.setEmailEstado("1");
        //vtVentaRepository.save(venta);
        return request;
    }
}
