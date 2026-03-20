package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.modCompras.impuestosXml.VtVentasFacturaOneProjection;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.comprobantesPdf.FacturaPdf;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlVtVentasFacturasServiceImpl {


    // TODO REVISAR
    private final VtVentasRepository vtVentaRepository;

    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;

    // private final SecurityUtils securityUtils;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final FacturaPdf facturaPdf;

    public VtVentasXMLFacturaGetDto findXMLFacturaById(Long idEmpresa, UUID id) {
        /*UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        VtVentasXMLFacturaGetDto dto = documentosElectronicosComprobanteBuilder.toFacturaVenta(entidad);
        return dto;*/
        return null;
    }

    public byte[] findPDFFacturaById(Long idEmpresa, UUID id) {

     /*   System.out.println("Obtener PDF");

        UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);


        Factura documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(Factura.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            //documento = (Factura) jaxbUnmarshaller1.unmarshal(new StreamSource(new StringReader(comprobante)));
            documento = (Factura) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: "+documento.getInfoFactura().getComercioExterior());
        } catch (JAXBException ex) {
            //Logger.getLogger(Validar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error 1");
        }

        // GENERANDO EL PDF

        return facturaPdf.generarPdf(
                documento,
                entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                entidad.getFechaAutorizacion()== null ? "" : entidad.getFechaAutorizacion(),
                datosEmpresaDto.getImageBytes());

      */
        return null;
    }

}

