package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantesPdf.NotaDebitoPdf;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.VtVentasFacturaOneProjection;
import com.calero.lili.core.modVentas.VtVentasRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlVtVentasNotasDebitoServiceImpl {

    private final VtVentasRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final NotaDebitoPdf notaDebitoPdf;

    public VtVentasXMLNotaDebitoGetDto findXMLNotaDebitoById(Long idData, Long idEmpresa, UUID id) {

        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toNotaDebitoVenta(entidad);

    }

    public ArchivoDto findPDFNotaDebitoById(Long idData, Long idEmpresa, UUID id, String origenCertificado) {

        System.out.println("Obtener PDF");

        DatosEmpresaDto datosEmpresaDto = null;


        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarNotaDebito(entidad);

        String nombreArchivo = "E-NDB-" + entidad.getSerie() + "-" + entidad.getSecuencial() + "-" + entidad.getNumeroIdentificacion() + ".pdf";

        switch (origenCertificado) {

            case "WEB" -> datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

            case "LOC" -> datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(idData, idEmpresa);
        }

        NotaDebito documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(NotaDebito.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (NotaDebito) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (JAXBException ex) {
            System.out.println("error 1");
            throw new GeneralException("No se pudo convetir el comprobante");
        }


        return ArchivoDto.builder()
                .nombre(nombreArchivo)
                .contenido(notaDebitoPdf.generarPdf(
                        documento,
                        entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                        entidad.getFechaAutorizacion() == null ? "" : entidad.getFechaAutorizacion(),
                        datosEmpresaDto.getImageBytes()))
                .build();
    }


    public ArchivoDto findFileXMLNotaDebito(Long idData, Long idEmpresa, UUID id) {


        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarNotaDebito(entidad);


        String nombreArchivo = "E-NDB-" + entidad.getSerie() + "-" + entidad.getSecuencial() + "-" + entidad.getNumeroIdentificacion() + ".xml";

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


    private void validarNotaDebito(VtVentasFacturaOneProjection entidad) {
        if (!entidad.getTipoVenta().equals(TipoVenta.NDB.name())) {
            throw new GeneralException("El documento con id " + entidad.getIdVenta() + " no es una factura");
        }

        if (Objects.isNull(entidad.getComprobante()) || entidad.getComprobante().isEmpty()) {
            throw new GeneralException("El documento no contiene un comprobante");
        }

        if (!entidad.getEstadoDocumento().equals(EstadoDocumento.AUT.name())) {
            throw new GeneralException("El documento con id {0} no esta autorizado " + entidad.getIdVenta());
        }
    }


}

