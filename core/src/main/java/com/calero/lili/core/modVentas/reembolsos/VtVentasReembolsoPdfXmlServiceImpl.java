package com.calero.lili.core.modVentas.reembolsos;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantesPdf.FacturaPdf;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modVentas.reembolsos.projection.VtVentasReembolsoProjection;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VtVentasReembolsoPdfXmlServiceImpl {

    private final VtVentasReembolsoRepository vtVentasReembolsoRepository;
    private final FacturaPdf facturaPdf;

    public ArchivoDto findPDFSustentoLiqReembolsoFacturaById(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Obtener PDF");


        VtVentasReembolsoProjection entidad = vtVentasReembolsoRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        validarFactura(entidad);

        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "FAC" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".pdf";

        Factura documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(Factura.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (Factura) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: " + documento.getInfoFactura().getComercioExterior());
        } catch (JAXBException ex) {
            System.out.println("error 1");
            throw new GeneralException("No se pudo convetir el comprobante");
        }


        return ArchivoDto.builder()
                .nombre(nombreArchivo)
                .contenido(facturaPdf.generarPdf(
                        documento,
                        entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                        entidad.getFechaAutorizacion() == null ? "" : entidad.getFechaAutorizacion(),
                        null))
                .build();
    }


    public ArchivoDto findXMLSustentoLiqReembolsoFacturaById(Long idData, Long idEmpresa, UUID id) {

        VtVentasReembolsoProjection entidad = vtVentasReembolsoRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        validarFactura(entidad);


        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "FAC" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".xml";

        Autorizacion aut = new Autorizacion();
        aut.setComprobante(entidad.getComprobante()); //"<![CDATA[" + + "]]>"
        aut.setFechaAutorizacion(entidad.getFechaAutorizacion());
        aut.setNumeroAutorizacion(entidad.getNumeroAutorizacion());
        aut.setEstado("AUTORIZADO");

        try {
            JAXBContext context = JAXBContext.newInstance(new Class[]{Autorizacion.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(aut, stringWriter);

            return ArchivoDto.builder()
                    .nombre(nombreArchivo)
                    .contenido(stringWriter.toString().getBytes())
                    .build();

        } catch (Exception ex) {
            throw new GeneralException("Existe un error: " + ex.getMessage());
        }
    }


    private void validarFactura(VtVentasReembolsoProjection entidad) {

        if (Objects.isNull(entidad.getNumeroAutorizacion()) || entidad.getNumeroAutorizacion().isEmpty()) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no tiene un número de autorización válido", entidad.getIdVentaReembolso()));
        }

        if (!entidad.getNumeroAutorizacion().startsWith("01", 8)) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no corresponde a una factura", entidad.getIdVentaReembolso()));
        }
        validacionComprobante(entidad);
    }

    private void validacionComprobante(VtVentasReembolsoProjection entidad) {
        if (Objects.isNull(entidad.getComprobante()) || entidad.getComprobante().isEmpty()) {
            throw new GeneralException("El documento no contiene un comprobante");
        }
    }

}
