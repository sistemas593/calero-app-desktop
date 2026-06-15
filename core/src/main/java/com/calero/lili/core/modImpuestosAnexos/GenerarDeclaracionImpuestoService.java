package com.calero.lili.core.modImpuestosAnexos;

import com.calero.lili.core.builder.retencion.RdepBuilder;
import com.calero.lili.core.dtos.FilterImpuestoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosAnexos.builder.RetencionFuenteBuilder;
import com.calero.lili.core.modImpuestosAnexos.retencion.Rdep;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.validaciones.ValidarValoresComprobantesPdf;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
@Slf4j
public class GenerarDeclaracionImpuestoService {

    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;
    private final RdepBuilder rdepBuilder;
    private final RetencionFuenteBuilder retencionFuenteBuilder;
    private AdEmpresasRepository adEmpresasRepository;

    private final Color colorResaltar = new Color(255, 253, 208);
    private final Color colorEncabezadosTablas = new Color(0, 85, 237);


    public void generarPdfDeclaracionRetenciones(ImpuestosF103Dto model, HttpServletResponse response, Long idEmpresa, FilterImpuestoDto filter) {

        try {

            response.setContentType("application/pdf");
            String filename = "Declaración_Retenciones_de_la_fuente_" + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font titleFontTb = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            Font normalFont = new Font(Font.HELVETICA, 10);
            Font smallBold = new Font(Font.HELVETICA, 9, Font.BOLD);
            Font small = new Font(Font.HELVETICA, 9);


            agregarEncabezadoRetencion(document, titleFont, normalFont);
            agregarTablaEncabezadoPagosRetencionesDeImpuesto(document, smallBold, small, titleFontTb, model);
            agregarDetallePagosRetencionesDeImpuesto(document, smallBold, small, titleFontTb, model);
            agregarTotalPagosRetencionesDeImpuesto(document, smallBold, small, titleFontTb, model);

            document.close();
            writer.close();

        } catch (Exception exception) {
            log.info(exception.getMessage());
            throw new GeneralException(exception.getMessage());
        }

    }

    public void generarFormulario107(ImpuestosF107Dto model, HttpServletResponse response) {

        try {

            response.setContentType("application/pdf");
            String filename = model.getIdRet() + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            Font titleFont = new Font(Font.HELVETICA, 7, Font.BOLD);
            Font titleFontTb = new Font(Font.HELVETICA, 5, Font.BOLD, Color.WHITE);
            Font normalFont = new Font(Font.HELVETICA, 5);
            Font smallBold = new Font(Font.HELVETICA, 5, Font.BOLD);
            Font small = new Font(Font.HELVETICA, 5);


            agregarEncabezadoFormulario107(document, titleFont, normalFont);
            agregarTablaEncabezadoFormulario107(document, smallBold, small, titleFontTb, model);
            agregarTablaLiqImpuestoFormulario107(document, smallBold, small, titleFontTb, model);
            agregarInstruccionesFormulario107(document, smallBold, small, model);

            document.close();
            writer.close();

        } catch (Exception exception) {
            log.info(exception.getMessage());
            throw new GeneralException(exception.getMessage());
        }

    }



    // Metodos para agregar las distintas secciones de la declaración de retenciones de la fuente

    private void agregarEncabezadoRetencion(Document document, Font titleFont, Font normalFont) {

        Paragraph title = new Paragraph("Sistema de declaración\nde impuestos\na través de internet", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);

        Paragraph info = new Paragraph(
                "Obligación Tributaria:  1031 - DECLARACIÓN DE RETENCIONES EN LA FUENTE\n Identificación: 1793203185001\nRazón Social: EOFERTIL S.A.S.\nPeríodo Fiscal: AGOSTO 2025\nTipo Declaración: ORIGINAL\nFormulario Sustituye: ",
                normalFont);
        info.setSpacingBefore(10f);
        document.add(info);
    }


    private void agregarTablaEncabezadoPagosRetencionesDeImpuesto(Document document, Font bold, Font normal, Font titleTb, ImpuestosF103Dto model) {


        PdfPTable tablaEncabezado = new PdfPTable(new float[]{100});
        tablaEncabezado.setWidthPercentage(100);
        tablaEncabezado.setSpacingBefore(5f);

        PdfPCell celdaE1 = crearCelda("DETALLE DE PAGOS Y RETENCIÓN POR IMPUESTO A LA RENTA", normal);
        PdfPCell celdaE2 = crearCelda("POR PAGOS EFECTUADOS A RESIDENTES Y ESTABLECIMIENTOS PERMANENTES", normal);
        PdfPCell celdaE3 = crearCelda("DERIVADAS DEL TRABAJO Y SERVICIOS PRESTADOS", normal);

        celdaE1.setBackgroundColor(colorResaltar);
        celdaE2.setBackgroundColor(colorResaltar);
        celdaE3.setBackgroundColor(colorResaltar);

        celdaE1.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);

        tablaEncabezado.addCell(celdaE1);
        tablaEncabezado.addCell(celdaE2);
        tablaEncabezado.addCell(celdaE3);

        document.add(tablaEncabezado);
    }


    private void agregarDetallePagosRetencionesDeImpuesto(Document document, Font bold, Font normal, Font titleTb, ImpuestosF103Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);


        PdfPCell celdaE1 = crearCelda("", titleTb);
        PdfPCell celdaE2 = crearCelda("BASE\n" +
                "IMPONIBLE", titleTb);
        PdfPCell celdaE3 = crearCelda("VALOR\n" +
                "RETENIDO", titleTb);

        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE3.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);


        tabla.addCell(crearCelda("En relación de dependencia que supera o no la base desgravada", normal));
        tabla.addCell(crearCeldaConCodigo("302", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("352", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("Servicios", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Honorarios profesionales", normal));
        tabla.addCell(crearCeldaConCodigo("303", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("353", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("Servicios profesionales prestados por sociedades residentes", normal));
        tabla.addCell(crearCeldaConCodigo("3030", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3530", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Predomina el intelecto ", normal));
        tabla.addCell(crearCeldaConCodigo("304", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("354", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Predomina la mano de obra", normal));
        tabla.addCell(crearCeldaConCodigo("307", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("357", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Utilización o aprovechamiento de la imagen o renombre (personas naturales, sociedades, influencers)", normal));
        tabla.addCell(crearCeldaConCodigo("308", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("358", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Publicidad y comunicación", normal));
        tabla.addCell(crearCeldaConCodigo("309", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("359", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("Transporte privado de pasajeros o servicio público o privado de carga ", normal));
        tabla.addCell(crearCeldaConCodigo("310", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("360", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("A través de liquidaciones de compra (nivel cultural o rusticidad) ", normal));
        tabla.addCell(crearCeldaConCodigo("311", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("361", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaPbs1 = crearCelda("POR BIENES Y SERVICIOS", normal);
        PdfPCell celdaPbs2 = crearCelda("", titleTb);
        PdfPCell celdaPbs3 = crearCelda("", titleTb);

        celdaPbs1.setBackgroundColor(colorResaltar);
        celdaPbs2.setBackgroundColor(colorResaltar);
        celdaPbs3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaPbs1);
        tabla.addCell(celdaPbs2);
        tabla.addCell(celdaPbs3);

        tabla.addCell(crearCelda("Transferencia de bienes muebles de naturaleza corporal", normal));
        tabla.addCell(crearCeldaConCodigo("312", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("362", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Seguros y reaseguros (primas y cesiones) ", normal));
        tabla.addCell(crearCeldaConCodigo("322", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("372", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("COMPRAS AL PRODUCTOR: de bienes de origen agrícola, avícola, pecuario, apícola, cunícola, bioacuático, forestal y carnes en estado natural y\n" +
                "los descritos en el art.27.1 de LRTI.", normal));
        tabla.addCell(crearCeldaConCodigo("3120", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3620", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("COMPRAS AL COMERCIALIZADOR: de bienes de origen agrícola, avícola, pecuario, apícola, cunícola, bioacuático, forestal y carnes en estado\n" +
                "natural y los descritos en el art.27.1 de LRTI.", normal));
        tabla.addCell(crearCeldaConCodigo("3121", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3621", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Actividades de construcción de obra material inmueble, urbanización, lotización o actividades similares", normal));
        tabla.addCell(crearCeldaConCodigo("3430", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3450", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Pagos aplicables el 1% (Energía Eléctrica y régimen RIMPE - Emprendedores, para este caso aplica con cualquier forma de pago inclusive los\n" +
                "pagos que deban realizar las tarjetas de crédito/débito", normal));
        tabla.addCell(crearCeldaConCodigo("343", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("393", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Pagos aplicables el 2% (incluye Pago local tarjeta de crédito /débito reportada por la Emisora de tarjeta de crédito / entidades del sistema\n" +
                "financiero; adquisición de sustancias minerales dentro del territorio nacional; Recepción de botellas plásticas no retornables de PET)\n", normal));
        tabla.addCell(crearCeldaConCodigo("344", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("394", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Pagos de bienes y servicios no sujetos a retención o con 0% (distintos de rendimientos financieros)", normal));
        tabla.addCell(crearCeldaConCodigo("322", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("", "", bold, normal));


        PdfPCell celdaPcao1 = crearCelda("POR REGALIAS, COMISIONES, ARRENDAMIENTOS Y OTROS", normal);
        PdfPCell celdaPcao2 = crearCelda("", titleTb);
        PdfPCell celdaPcao3 = crearCelda("", titleTb);

        celdaPcao1.setBackgroundColor(colorResaltar);
        celdaPcao2.setBackgroundColor(colorResaltar);
        celdaPcao3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaPcao1);
        tabla.addCell(celdaPcao2);
        tabla.addCell(celdaPcao3);


        tabla.addCell(crearCelda("Por regalías, derechos de autor, marcas, patentes y similares", normal));
        tabla.addCell(crearCeldaConCodigo("314", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("364", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Comisiones pagadas a sociedades, nacionales o extranjeras residentes en el Ecuador y establecimientos permanentes domiciliados en el país", normal));
        tabla.addCell(crearCeldaConCodigo("3140", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3640", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Arrendamiento", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Mercantil", normal));
        tabla.addCell(crearCeldaConCodigo("319", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("369", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Bienes inmuebles", normal));
        tabla.addCell(crearCeldaConCodigo("320", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("370", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaRcc1 = crearCelda("RELACIONADAS CON EL CAPITAL ( RENDIMIENTOS, GANANCIAS, DIVIDENDOS Y OTROS)", normal);
        PdfPCell celdaRcc2 = crearCelda("", titleTb);
        PdfPCell celdaRcc3 = crearCelda("", titleTb);

        celdaRcc1.setBackgroundColor(colorResaltar);
        celdaRcc2.setBackgroundColor(colorResaltar);
        celdaRcc3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaRcc1);
        tabla.addCell(celdaRcc2);
        tabla.addCell(celdaRcc3);


        tabla.addCell(crearCelda("Rendimientos financieros", normal));
        tabla.addCell(crearCeldaConCodigo("323", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("373", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Rendimientos financieros entre instituciones del sistema financiero y entidades economía popular y solidaria", normal));
        tabla.addCell(crearCeldaConCodigo("324", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("374", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Otros Rendimientos financieros 0%", normal));
        tabla.addCell(crearCeldaConCodigo("3230", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("", "", bold, normal));

        tabla.addCell(crearCelda("Ganancia en la enajenación de derechos representativos de capital u otros derechos que permitan la exploración, explotación, concesión o\n" +
                "similares de sociedades, que se coticen en las bolsas de valores del Ecuador", normal));
        tabla.addCell(crearCeldaConCodigo("333", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("383", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Contraprestación en la enajenación de derechos representativos de capital u otros derechos que permitan la exploración, explotación, concesión o\n" +
                "similares de sociedades, no cotizados en las bolsas de valores del Ecuador", normal));
        tabla.addCell(crearCeldaConCodigo("334", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("384", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaLP1 = crearCelda("POR LOTERIAS Y PREMIOS", normal);
        PdfPCell celdaLP2 = crearCelda("", titleTb);
        PdfPCell celdaLP3 = crearCelda("", titleTb);

        celdaLP1.setBackgroundColor(colorResaltar);
        celdaLP2.setBackgroundColor(colorResaltar);
        celdaLP3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaLP1);
        tabla.addCell(celdaLP2);
        tabla.addCell(celdaLP3);


        tabla.addCell(crearCelda("Loterías, rifas, apuestas, pronósticos deportivos y similares", normal));
        tabla.addCell(crearCeldaConCodigo("335", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("385", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        PdfPCell celdaAOR1 = crearCelda("AUTORRETENCIONES Y OTRAS RETENCIONES", normal);
        PdfPCell celdaAOR2 = crearCelda("", titleTb);
        PdfPCell celdaAOR3 = crearCelda("", titleTb);

        celdaAOR1.setBackgroundColor(colorResaltar);
        celdaAOR2.setBackgroundColor(colorResaltar);
        celdaAOR3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaAOR1);
        tabla.addCell(celdaAOR2);
        tabla.addCell(celdaAOR3);


        tabla.addCell(crearCelda("Venta de combustibles\n", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("A comercializadoras", normal));
        tabla.addCell(crearCeldaConCodigo("336", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("386", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("A distribuidores", normal));
        tabla.addCell(crearCeldaConCodigo("337", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("387", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("Retención a cargo del propio sujeto pasivo por la comercialización de productos forestales", normal));
        tabla.addCell(crearCeldaConCodigo("3370", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3870", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("Otras autorretenciones (inciso 1 y 2 Art.92.1 RLRTI) ", normal));
        tabla.addCell(crearCeldaConCodigo("350", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("400", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Otras retenciones", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Aplicables el 2,75%", normal));
        tabla.addCell(crearCeldaConCodigo("3440", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3940", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(crearCelda("Aplicables a otros porcentajes ( Por Donaciones en dinero -Impuesto a las donaciones )", normal));
        tabla.addCell(crearCeldaConCodigo("346", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("396", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaLiru1 = crearCelda("LIQUIDACIÓN DE IMPUESTO A LA RENTA ÚNICO", normal);
        PdfPCell celdaLiru2 = crearCelda("", titleTb);
        PdfPCell celdaLiru3 = crearCelda("", titleTb);

        celdaLiru1.setBackgroundColor(colorResaltar);
        celdaLiru2.setBackgroundColor(colorResaltar);
        celdaLiru3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaLiru1);
        tabla.addCell(celdaLiru2);
        tabla.addCell(celdaLiru3);


        tabla.addCell(crearCelda("IRU Pronósticos deportivos", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("(+) Ingresos generados por la actividad económica de pronósticos deportivos", normal));
        tabla.addCell(crearCeldaConCodigo("3483", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("(+) Comisiones derivadas de la actividad de pronósticos deportivos", normal));
        tabla.addCell(crearCeldaConCodigo("3484", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("(-) Premios pagados por pronósticos deportivos", normal));
        tabla.addCell(crearCeldaConCodigo("3485", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Impuesto a la renta único sobre los ingresos percibidos por los operadores de pronósticos deportivos", normal));
        tabla.addCell(crearCeldaConCodigo("3480", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("3980", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaSoe1 = crearCelda("SUBTOTAL OPERACIONES EFECTUADAS EN EL PAÍS", bold);
        PdfPCell celdaSoe2 = crearCeldaConCodigoResaltar("349 ", validarValoresComprobantesPdf.getValor(model.getC419()), bold, bold);
        PdfPCell celdaSoe3 = crearCeldaConCodigoResaltar("399 ", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaSoe1.setBackgroundColor(colorResaltar);
        celdaSoe2.setBackgroundColor(colorResaltar);
        celdaSoe3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaSoe1);
        tabla.addCell(celdaSoe2);
        tabla.addCell(celdaSoe3);


        document.add(tabla);

    }


    private void agregarTotalPagosRetencionesDeImpuesto(Document document, Font bold, Font normal, Font titleTb, ImpuestosF103Dto model) {


        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);


        PdfPCell celdaE1 = crearCelda("", titleTb);
        PdfPCell celdaE2 = crearCelda("BASE\n" +
                "IMPONIBLE", titleTb);
        PdfPCell celdaE3 = crearCelda("VALOR\n" +
                "RETENIDO", titleTb);


        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE3.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);

        PdfPCell celdaTri1 = crearCelda("TOTAL DE RETENCIÓN DE IMPUESTO A LA RENTA", normal);
        PdfPCell celdaTri2 = crearCelda("399 + 489", normal);
        PdfPCell celdaTri3 = crearCeldaConCodigoResaltar("499", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaTri1.setBackgroundColor(colorResaltar);
        celdaTri2.setBackgroundColor(colorResaltar);
        celdaTri3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaTri1);
        tabla.addCell(celdaTri2);
        tabla.addCell(celdaTri3);

        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));

        PdfPCell celdaVp1 = crearCelda("VALORES A PAGAR (luego de imputación al pago)", titleTb);
        PdfPCell celdaVp2 = crearCelda("", normal);
        PdfPCell celdaVp3 = crearCelda("", normal);

        celdaVp1.setBackgroundColor(colorEncabezadosTablas);
        celdaVp2.setBackgroundColor(colorEncabezadosTablas);
        celdaVp3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaVp1);
        tabla.addCell(celdaVp2);
        tabla.addCell(celdaVp3);


        PdfPCell celdaTir1 = crearCelda("TOTAL IMPUESTO A PAGAR", normal);
        PdfPCell celdaTir2 = crearCelda("499 - 898", normal);
        PdfPCell celdaTir3 = crearCeldaConCodigoResaltar("902", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaTir1.setBackgroundColor(colorResaltar);
        celdaTir2.setBackgroundColor(colorResaltar);
        celdaTir3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaTir1);
        tabla.addCell(celdaTir2);
        tabla.addCell(celdaTir3);


        tabla.addCell(crearCelda("Interés por mora ", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("903", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));


        tabla.addCell(crearCelda("Multa", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("904", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));


        PdfPCell celdaTotal1 = crearCelda("TOTAL PAGADO 999 2044.66", normal);
        PdfPCell celdaTotal2 = crearCelda("", normal);
        PdfPCell celdaTotal3 = crearCeldaConCodigoResaltar("999", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaTotal1.setBackgroundColor(colorResaltar);
        celdaTotal2.setBackgroundColor(colorResaltar);
        celdaTotal3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaTotal1);
        tabla.addCell(celdaTotal2);
        tabla.addCell(celdaTotal3);

        document.add(tabla);

    }


    // Metodos para agregar las distintas secciones del formulario 107

    private void agregarEncabezadoFormulario107(Document document, Font titleFont, Font normalFont) {
        Paragraph title = new Paragraph("Formulario 107\nComprobante de retenciones en la fuente del impuestos la renta\npor ingresos del trabajo en relacion de dependencia", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);
    }

    private void agregarTablaEncabezadoFormulario107(Document document, Font bold, Font normal, Font titleTb, ImpuestosF107Dto model) {

        PdfPTable tablaEncabezado = new PdfPTable(new float[]{50, 25, 25, 25});
        tablaEncabezado.setWidthPercentage(100);
        tablaEncabezado.setSpacingBefore(5f);

        PdfPCell celdaE1 = crearCelda("Período Fiscal", normal);
        PdfPCell celdaE2 = crearCelda(model.getAno(), normal);
        PdfPCell celdaE3 = crearCelda("Fecha de entrega", normal);
        PdfPCell celdaE4 = crearCelda(DateUtils.toString(LocalDate.now()), normal);

        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE3.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE4.setHorizontalAlignment(Element.ALIGN_CENTER);

        tablaEncabezado.addCell(celdaE1);
        tablaEncabezado.addCell(celdaE2);
        tablaEncabezado.addCell(celdaE3);
        tablaEncabezado.addCell(celdaE4);


        PdfPCell celdaIE1 = crearCelda("Identificación del Empleador (Agente de Retención)", titleTb);
        PdfPCell celdaIE2 = crearCelda("", normal);
        PdfPCell celdaIE3 = crearCelda("", normal);
        PdfPCell celdaIE4 = crearCelda("", normal);

        celdaIE1.setBackgroundColor(colorEncabezadosTablas);
        celdaIE2.setBackgroundColor(colorEncabezadosTablas);
        celdaIE3.setBackgroundColor(colorEncabezadosTablas);
        celdaIE4.setBackgroundColor(colorEncabezadosTablas);

        tablaEncabezado.addCell(celdaIE1);
        tablaEncabezado.addCell(celdaIE2);
        tablaEncabezado.addCell(celdaIE3);
        tablaEncabezado.addCell(celdaIE4);

        tablaEncabezado.addCell(crearCelda("RUC", normal));
        tablaEncabezado.addCell(crearCelda(model.getRuc(), normal));
        tablaEncabezado.addCell(crearCelda("", normal));
        tablaEncabezado.addCell(crearCelda("", normal));


        tablaEncabezado.addCell(crearCelda("Razón social o apellidos y nombres completos", normal));
        tablaEncabezado.addCell(crearCelda(model.getRazonSocial(), normal));
        tablaEncabezado.addCell(crearCelda("", normal));
        tablaEncabezado.addCell(crearCelda("", normal));


        PdfPCell celdaIT1 = crearCelda("Identificación del Trabajador (Contribuyente)", titleTb);
        PdfPCell celdaIT2 = crearCelda("", normal);
        PdfPCell celdaIT3 = crearCelda("", normal);
        PdfPCell celdaIT4 = crearCelda("", normal);


        celdaIT1.setBackgroundColor(colorEncabezadosTablas);
        celdaIT2.setBackgroundColor(colorEncabezadosTablas);
        celdaIT3.setBackgroundColor(colorEncabezadosTablas);
        celdaIT4.setBackgroundColor(colorEncabezadosTablas);

        tablaEncabezado.addCell(celdaIT1);
        tablaEncabezado.addCell(celdaIT2);
        tablaEncabezado.addCell(celdaIT3);
        tablaEncabezado.addCell(celdaIT4);

        tablaEncabezado.addCell(crearCelda("Apellidos y nombres completos ", normal));
        tablaEncabezado.addCell(crearCelda(model.getApellidoTrab() + " " + model.getNombreTrab(), normal));
        tablaEncabezado.addCell(crearCelda("", normal));
        tablaEncabezado.addCell(crearCelda("", normal));


        tablaEncabezado.addCell(crearCelda("Cédula o Pasaporte", normal));
        tablaEncabezado.addCell(crearCelda(model.getIdRet(), normal));
        tablaEncabezado.addCell(crearCelda("", normal));
        tablaEncabezado.addCell(crearCelda("", normal));

        document.add(tablaEncabezado);
    }

    private void agregarTablaLiqImpuestoFormulario107(Document document, Font bold, Font normal, Font titleTb, ImpuestosF107Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        PdfPCell celdaLQI1 = crearCelda("Liquidación del Impuesto", titleTb);
        PdfPCell celdaLQI2 = crearCelda("", normal);
        PdfPCell celdaLQI3 = crearCelda("", normal);

        celdaLQI1.setBackgroundColor(colorEncabezadosTablas);
        celdaLQI2.setBackgroundColor(colorEncabezadosTablas);
        celdaLQI3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaLQI1);
        tabla.addCell(celdaLQI2);
        tabla.addCell(celdaLQI3);

        // 301+303+305+381
        BigDecimal valorGravadoEsteEmpleador = model.getSuelSal().add(model.getSobSuelComRemu())
                .add(model.getOtrosIngRenNoGrav()).add(model.getPartUtil()).add(model.getValImpAsuEsteEmpl());

        tabla.addCell(crearCelda("SUELDOS, SALARIOS Y OTROS INGRESOS GRAVADOS DE IMPUESTO A LA RENTA (MATERIA GRAVADA DE LA SEGURIDAD SOCIAL)", normal));
        tabla.addCell(crearCeldaConSigno("301", "+", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getSuelSal().add(model.getSobSuelComRemu())), normal));

        tabla.addCell(crearCelda("OTROS INGRESOS GRAVADOS DE IMPUESTO A LA RENTA (MATERIA NO GRAVADA DE LA SEGURIDAD SOCIAL)", normal));
        tabla.addCell(crearCeldaConSigno("303", "+", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getOtrosIngRenNoGrav()), normal));

        tabla.addCell(crearCelda("PARTICIPACIÓN UTILIDADES", normal));
        tabla.addCell(crearCeldaConSigno("305", "+", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getPartUtil()), normal));

        tabla.addCell(crearCelda("INGRESOS GRAVADOS GENERADOS CON OTROS EMPLEADORES\n", normal));
        tabla.addCell(crearCeldaConSigno("307", "+", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getIntGrabGen()), normal));

        tabla.addCell(crearCelda("DÉCIMO TERCER SUELDO", normal));
        tabla.addCell(crearCeldaConSigno("311", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDecimTer()), normal));

        tabla.addCell(crearCelda("DÉCIMO CUARTO SUELDO", normal));
        tabla.addCell(crearCeldaConSigno("313", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDecimCuar()), normal));

        tabla.addCell(crearCelda("FONDO DE RESERVA", normal));
        tabla.addCell(crearCeldaConSigno("315", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getFondoReserva()), normal));

        tabla.addCell(crearCelda("OTROS INGRESOS EN RELACIÓN DE DEPENDENCIA QUE NO CONSTITUYEN MATERIA GRAVADA DE IMPUESTO A LA RENTA", normal));
        tabla.addCell(crearCeldaConSigno("317", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getOtrosIngRenGrav()), normal));

        tabla.addCell(crearCelda("(-) APORTE PERSONAL A LA SEGURIDAD SOCIAL CON ESTE EMPLEADOR (únicamente pagado por el trabajador), APORTES PERSONALES A LAS CAJAS MILITAR\n" +
                "O POLICIAL PARA FINES DE RETIRO O CESANTÍA", normal));
        tabla.addCell(crearCeldaConSigno("351", "-", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getApoPerIess()), normal));

        tabla.addCell(crearCelda("(-) APORTE PERSONAL A LA SEGURIDAD SOCIAL CON OTROS EMPLEADORES (únicamente pagado por el trabajador)", normal));
        tabla.addCell(crearCeldaConSigno("353", "-", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getAporPerIessConOtrosEmpls()), normal));

        tabla.addCell(crearCelda("GASTOS PERSONALES - VIVIENDA (Informativo)", normal));
        tabla.addCell(crearCeldaConSigno("361", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducVivienda()), normal));

        tabla.addCell(crearCelda("GASTOS PERSONALES - TURISMO (Informativo)", normal));
        tabla.addCell(crearCeldaConSigno("362", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeduccionTurismo()), normal));

        tabla.addCell(crearCelda("GASTOS PERSONALES - SALUD (Informativo)", normal));
        tabla.addCell(crearCeldaConSigno("363", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducSalud()), normal));

        tabla.addCell(crearCelda("GASTOS PERSONALES - EDUCACIÓN - ARTE Y CULTURA (Informativo)", normal));
        tabla.addCell(crearCeldaConSigno("365", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducEducartcult()), normal));

        tabla.addCell(crearCelda("GASTOS PERSONALES - ALIMENTACIÓN (Informativo)", normal));
        tabla.addCell(crearCeldaConSigno("367", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducAliement()), normal));

        tabla.addCell(crearCelda("GASTOS PERSONALES - VESTIMENTA (Informativo)", normal));
        tabla.addCell(crearCeldaConSigno("369", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducVestim()), normal));

        tabla.addCell(crearCelda("(-) EXONERACIÓN POR DISCAPACIDAD", normal));
        tabla.addCell(crearCeldaConSigno("371", "-", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getExoDiscap()), normal));

        tabla.addCell(crearCelda("(-) EXONERACIÓN POR TERCERA EDAD", normal));
        tabla.addCell(crearCeldaConSigno("373", "-", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getExoTerEd()), normal));

        tabla.addCell(crearCelda("IMPUESTO A LA RENTA ASUMIDO POR ESTE EMPLEADOR", normal));
        tabla.addCell(crearCeldaConSigno("381", "+", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValImpAsuEsteEmpl()), normal));


        PdfPCell celdaBI1 = crearCelda("BASE IMPONIBLE GRAVADA (301+303+305+307-351-353-361-363-365-367-369-371-373+381 mayor o igual a 0)", normal);
        PdfPCell celdaBI2 = crearCeldaConSigno("399", "=", bold, bold);
        PdfPCell celdaBI3 = crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getBasImp()), normal);

        celdaBI1.setBackgroundColor(colorResaltar);
        celdaBI2.setBackgroundColor(colorResaltar);
        celdaBI3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaBI1);
        tabla.addCell(celdaBI2);
        tabla.addCell(celdaBI3);

        tabla.addCell(crearCelda("IMPUESTO A LA RENTA CAUSADO", normal));
        tabla.addCell(crearCeldaConSigno("401", "=", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getImpRentCaus()), normal));

        tabla.addCell(crearCelda("REBAJA POR GASTOS PERSONALES", normal));
        tabla.addCell(crearCeldaConSigno("402", "=", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getRebajaGastosPersonales()), normal));

        tabla.addCell(crearCelda("IMPUESTO A LA RENTA DESPUÉS DE LA REBAJA", normal));
        tabla.addCell(crearCeldaConSigno("403", "=", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO), normal));


        tabla.addCell(crearCelda("VALOR DEL IMPUESTO RETENIDO Y ASUMIDO POR OTROS EMPLEADORES DURANTE EL PERÍODO DECLARADO", normal));
        tabla.addCell(crearCeldaConSigno("404", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValRetAsuOtrosEmpls()), normal));


        tabla.addCell(crearCelda("VALOR DEL IMPUESTO ASUMIDO POR ESTE EMPLEADO", normal));
        tabla.addCell(crearCeldaConSigno("405", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValImpAsuEsteEmpl()), normal));


        tabla.addCell(crearCelda("VALOR DEL IMPUESTO RETENIDO AL TRABAJADOR POR ESTE EMPLEADOR", normal));
        tabla.addCell(crearCeldaConSigno("407", "", bold, bold));
        tabla.addCell(crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValRet()), normal));


        PdfPCell celdaIGE1 = crearCelda("INGRESOS GRAVADOS CON ESTE EMPLEADOR (informativo) 301+303+305+381 ", normal);
        PdfPCell celdaIGE2 = crearCeldaConSigno("349", "=", bold, bold);
        PdfPCell celdaIGE3 = crearCeldaADer(validarValoresComprobantesPdf.getValor(valorGravadoEsteEmpleador), normal);

        celdaIGE1.setBackgroundColor(colorResaltar);
        celdaIGE2.setBackgroundColor(colorResaltar);
        celdaIGE3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaIGE1);
        tabla.addCell(celdaIGE2);
        tabla.addCell(celdaIGE3);

        document.add(tabla);
    }


    private void agregarInstruccionesFormulario107(Document document, Font bold, Font normal, ImpuestosF107Dto model) {


        PdfPCell titulo = new PdfPCell(new Phrase("IMPORTANTE: Sírvase leer cada una de las siguientes instrucciones.", bold));
        titulo.setBackgroundColor(new Color(230, 230, 230));
        titulo.setPadding(5);

        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);
        tabla.addCell(titulo);

        // ======= INSTRUCCIONES =======
        String[] instrucciones = {
                "1.- El trabajador que, en el mismo periodo fiscal haya reiniciado su actividad con otro empleador, estará en la obligación de entregar el formulario 107 entregado por su anterior empleador, para que aquel, efectúe el cálculo de las retenciones o realicen lo que este al final.",
                "2.- El campo 307 deberá ser llenado con la información registrada en el campo 349 del Formulario 107 entregado por el anterior empleador, y/o con la proyección de ingresos de otros empleadores actuales, en caso de que el empleador que registra y entrega el presente formulario haya efectuado la retención por los ingresos percibidos en éstos últimos.",
                "3.- Deben presentar la información relativa a los gastos personales, las personas naturales que hagan uso de la rebaja por sus gastos personales de su Impuesto a la Renta causado, inclusive aquellos que se encuentren en relación de dependencia.",
                "4.- El beneficio de la exoneración por tercera edad se configura a partir del ejercicio en el cual el beneficiario cumpla los 65 años de edad. A partir del ejercicio fiscal 2017, el monto de la exoneración será el equivalente al monto de la fracción básica exenta de Impuesto a la Renta.",
                "5.- Conforme lo dispuesto en la Ley Orgánica de Discapacidades del monto de la exoneración por discapacidad será el equivalente al doble de la fracción básica exenta de Impuesto a la Renta y según el porcentaje de discapacidad.",
                "6.- Las exoneraciones por discapacidad y por tercera edad no podrán aplicarse simultáneamente para un mismo trabajador; en esos casos se podrá aplicar la exención más beneficiosa para el trabajador.",
                "7.- El presente formulario constituye la declaración de Impuesto a la Renta del trabajador, siempre que durante el período declarado la persona únicamente haya prestado sus servicios en relación de dependencia con el empleador que entregue este formulario, y no existan valores de gastos personales que deban ser reliquidados. En caso de pérdida de documento el trabajador deberá solicitar una copia a su empleador, sin embargo también podrá obtener la información de este formulario a través de la página web del SRI.",
                "8.- Para establecer el monto máximo de la rebaja de gastos personales o la exoneración por discapacidad, se deberá observar las reglas establecidas en el segundo artículo innumerado incorporado a continuación del artículo 10 de la Ley de Régimen Tributario Interno."
        };

        for (String texto : instrucciones) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, normal));
            celda.setPadding(4);
            celda.setBorderWidthBottom(0.5f);
            tabla.addCell(celda);
        }

        document.add(tabla);

        // ======= PIE CON RUC =======

        PdfPTable pie = new PdfPTable(new float[]{90, 10});
        pie.setWidthPercentage(100);
        pie.setSpacingBefore(5f);

        PdfPCell textoPie = new PdfPCell(new Phrase("DECLARO QUE LOS DATOS PROPORCIONADOS EN ESTE DOCUMENTO SON EXACTOS Y VERDADEROS, POR LO QUE ASUMO LA RESPONSABILIDAD LEGAL QUE DE ELLA SE DERIVEN (Art. 101 de la L.R.T.I.)", normal));
        textoPie.setColspan(2);
        textoPie.setPadding(5);
        textoPie.setHorizontalAlignment(Element.ALIGN_CENTER);
        pie.addCell(textoPie);

        document.add(pie);

        PdfPTable firmas = new PdfPTable(new float[]{30f, 30f, 40f});
        firmas.setWidthPercentage(100);
        firmas.setSpacingBefore(10f);

        PdfPCell cellAgente = new PdfPCell(new Phrase("FIRMA DEL AGENTE DE RETENCIÓN", normal));
        cellAgente.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellAgente.setVerticalAlignment(Element.ALIGN_TOP);
        cellAgente.setFixedHeight(15f);
        firmas.addCell(cellAgente);


        PdfPCell cellTrabajador = new PdfPCell(new Phrase("FIRMA DEL TRABAJADOR CONTRIBUYENTE", normal));
        cellTrabajador.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellTrabajador.setVerticalAlignment(Element.ALIGN_TOP);
        cellTrabajador.setFixedHeight(15f);
        firmas.addCell(cellTrabajador);


        PdfPTable tablaContadorAnidada = new PdfPTable(2);
        tablaContadorAnidada.setWidthPercentage(100);
        tablaContadorAnidada.setWidths(new float[]{15f, 85f});


        PdfPCell cellFirmaContador = new PdfPCell(new Phrase("FIRMA DEL CONTADOR", normal));
        cellFirmaContador.setColspan(2); // Ocupa ambas columnas
        cellFirmaContador.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellFirmaContador.setVerticalAlignment(Element.ALIGN_TOP);
        cellFirmaContador.setFixedHeight(15f);
        tablaContadorAnidada.addCell(cellFirmaContador);

        PdfPCell cell199 = new PdfPCell(new Phrase("199", normal));
        cell199.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell199.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell199.setPaddingBottom(3f);
        cell199.setFixedHeight(12f);
        tablaContadorAnidada.addCell(cell199);

        PdfPCell cellRucContador = new PdfPCell(new Phrase("RUC CONTADOR", normal));
        cellRucContador.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellRucContador.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cellRucContador.setPaddingBottom(0.5f);
        tablaContadorAnidada.addCell(cellRucContador);


        PdfPCell cellVacia = new PdfPCell(new Phrase(""));
        cellVacia.setFixedHeight(12f);
        tablaContadorAnidada.addCell(cellVacia);


        PdfPCell cellNumeroRuc = new PdfPCell(new Phrase(model.getRucContador(), normal));
        cellNumeroRuc.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellNumeroRuc.setVerticalAlignment(Element.ALIGN_TOP);
        cellNumeroRuc.setPaddingBottom(0.5f);
        tablaContadorAnidada.addCell(cellNumeroRuc);

        PdfPCell cellContadorPrincipal = new PdfPCell(tablaContadorAnidada);
        cellContadorPrincipal.setPadding(0);
        firmas.addCell(cellContadorPrincipal);


        document.add(firmas);

    }


    // Metodos para crear los tipos de columnas de las tablas.

    private PdfPCell crearCelda(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        return cell;
    }

    private PdfPCell crearCeldaADer(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        return cell;
    }

    private PdfPCell crearCeldaConCodigo(String codigo, String valor, Font fontCodigo, Font fontValor) {
        PdfPTable inner = new PdfPTable(2);
        inner.setWidthPercentage(100);
        try {
            inner.setWidths(new float[]{30, 70}); // proporción: 30% código, 70% valor
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // --- Celda izquierda (código con fondo celeste)


        PdfPCell codigoCell = new PdfPCell(new Phrase(codigo, fontCodigo));
        codigoCell.setBackgroundColor(new Color(189, 231, 255)); // Celeste
        codigoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codigoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        codigoCell.setPadding(4f);

        // --- Celda derecha (valor normal)
        PdfPCell valorCell = new PdfPCell(new Phrase(valor, fontValor));
        valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valorCell.setPadding(4f);

        // Línea divisoria entre ambas celdas
        valorCell.setBorderWidthLeft(0.5f);
        valorCell.setBorderColorLeft(Color.GRAY);

        inner.addCell(codigoCell);
        inner.addCell(valorCell);

        // Ahora la celda final contendrá esta tabla interna
        PdfPCell cell = new PdfPCell(inner);
        cell.setPadding(0f);
        return cell;
    }

    private PdfPCell crearCeldaConCodigoResaltar(String codigo, String valor, Font fontCodigo, Font fontValor) {
        PdfPTable inner = new PdfPTable(2);
        inner.setWidthPercentage(100);
        try {
            inner.setWidths(new float[]{30, 70}); // proporción: 30% código, 70% valor
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        PdfPCell codigoCell = new PdfPCell(new Phrase(codigo, fontCodigo));
        codigoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codigoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        codigoCell.setPadding(4f);


        PdfPCell valorCell = new PdfPCell(new Phrase(valor, fontValor));
        valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valorCell.setPadding(4f);


        valorCell.setBorderWidthLeft(0.5f);
        valorCell.setBorderColorLeft(Color.GRAY);

        inner.addCell(codigoCell);
        inner.addCell(valorCell);


        PdfPCell cell = new PdfPCell(inner);
        cell.setPadding(0f);
        return cell;
    }

    private PdfPCell crearCeldaConSigno(String codigo, String signo, Font fontCodigo, Font fontValor) {
        PdfPTable inner = new PdfPTable(2);
        inner.setWidthPercentage(100);
        try {
            inner.setWidths(new float[]{50, 50}); // proporción: 30% código, 70% valor
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // --- Celda izquierda (código con fondo celeste)


        PdfPCell codigoCell = new PdfPCell(new Phrase(codigo, fontCodigo));
        codigoCell.setBackgroundColor(new Color(189, 231, 255)); // Celeste
        codigoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codigoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        codigoCell.setPadding(4f);

        // --- Celda derecha (valor normal)
        PdfPCell valorCell = new PdfPCell(new Phrase(signo, fontValor));
        valorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valorCell.setPadding(4f);

        // Línea divisoria entre ambas celdas
        valorCell.setBorderWidthLeft(0.5f);
        valorCell.setBorderColorLeft(Color.GRAY);

        inner.addCell(codigoCell);
        inner.addCell(valorCell);

        // Ahora la celda final contendrá esta tabla interna
        PdfPCell cell = new PdfPCell(inner);
        cell.setPadding(0f);
        return cell;
    }


    public byte[] generarReporteTalonResumenRetencionFuentePDF(Long idData, Long idEmpresa, ValoresTalonResumenDto dto) {

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La empresa con id {0} no existe", idEmpresa)));

        try {
            InputStream reporteStream = new ClassPathResource("reporte_talon_rdep.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections
                    .singletonList(retencionFuenteBuilder.builderTalonResumen(empresa, dto)));
            Map<String, Object> parametros = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception exception) {
            throw new GeneralException(exception.getMessage());
        }
    }


    public void generarReporteTalonResumenRetencionFuenteXml(RetencionFuenteXmlDto model,
                                                             HttpServletResponse response) {

        try {
            if (Objects.isNull(model)) {
                throw new GeneralException("No se ha encontrado información para generar el XML");
            }
            generarXml(rdepBuilder.builderRdep(model), response);
        } catch (Exception exception) {
            throw new GeneralException(exception.getMessage());
        }

    }


    private void generarXml(Rdep rdep, HttpServletResponse response) {
        try {

            String fileName = "RDEP" + LocalDate.now().getYear() + ".xml";
            String zipFileName = "RDEP" + LocalDate.now().getYear() + ".zip";

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);

                JAXBContext context = JAXBContext.newInstance(Rdep.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.encoding", "UTF-8");
                marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));

                OutputStream out = response.getOutputStream();
                marshaller.marshal(rdep, zipOut);
                out.flush();

                zipOut.closeEntry();
            }


            System.out.println("XML Generado con exito a partir de objeto: xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
