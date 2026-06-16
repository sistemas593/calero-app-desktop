package com.calero.lili.core.modImpuestosAnexos.formulario103;


import com.calero.lili.core.dtos.FilterImpuestoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modImpuestosAnexos.CeldaPdfServiceImpl;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import com.calero.lili.core.utils.validaciones.ValidarValoresComprobantesPdf;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class GeneralFormulario103PdfServiceImpl {


    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;
    private final CeldaPdfServiceImpl celdaService;


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

        PdfPCell celdaE1 = celdaService.crearCelda("DETALLE DE PAGOS Y RETENCIÓN POR IMPUESTO A LA RENTA", normal);
        PdfPCell celdaE2 = celdaService.crearCelda("POR PAGOS EFECTUADOS A RESIDENTES Y ESTABLECIMIENTOS PERMANENTES", normal);
        PdfPCell celdaE3 = celdaService.crearCelda("DERIVADAS DEL TRABAJO Y SERVICIOS PRESTADOS", normal);

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


        PdfPCell celdaE1 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaE2 = celdaService.crearCelda("BASE\n" +
                "IMPONIBLE", titleTb);
        PdfPCell celdaE3 = celdaService.crearCelda("VALOR\n" +
                "RETENIDO", titleTb);

        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE3.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);


        tabla.addCell(celdaService.crearCelda("En relación de dependencia que supera o no la base desgravada", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("302", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("352", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("Servicios", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));


        tabla.addCell(celdaService.crearCelda("Honorarios profesionales", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("303", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("353", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("Servicios profesionales prestados por sociedades residentes", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3030", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3530", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Predomina el intelecto ", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("304", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("354", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Predomina la mano de obra", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("307", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("357", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Utilización o aprovechamiento de la imagen o renombre (personas naturales, sociedades, influencers)", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("308", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("358", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Publicidad y comunicación", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("309", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("359", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("Transporte privado de pasajeros o servicio público o privado de carga ", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("310", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("360", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("A través de liquidaciones de compra (nivel cultural o rusticidad) ", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("311", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("361", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaPbs1 = celdaService.crearCelda("POR BIENES Y SERVICIOS", normal);
        PdfPCell celdaPbs2 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaPbs3 = celdaService.crearCelda("", titleTb);

        celdaPbs1.setBackgroundColor(colorResaltar);
        celdaPbs2.setBackgroundColor(colorResaltar);
        celdaPbs3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaPbs1);
        tabla.addCell(celdaPbs2);
        tabla.addCell(celdaPbs3);

        tabla.addCell(celdaService.crearCelda("Transferencia de bienes muebles de naturaleza corporal", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("312", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("362", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Seguros y reaseguros (primas y cesiones) ", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("322", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("372", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("COMPRAS AL PRODUCTOR: de bienes de origen agrícola, avícola, pecuario, apícola, cunícola, bioacuático, forestal y carnes en estado natural y\n" +
                "los descritos en el art.27.1 de LRTI.", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3120", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3620", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("COMPRAS AL COMERCIALIZADOR: de bienes de origen agrícola, avícola, pecuario, apícola, cunícola, bioacuático, forestal y carnes en estado\n" +
                "natural y los descritos en el art.27.1 de LRTI.", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3121", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3621", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Actividades de construcción de obra material inmueble, urbanización, lotización o actividades similares", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3430", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3450", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Pagos aplicables el 1% (Energía Eléctrica y régimen RIMPE - Emprendedores, para este caso aplica con cualquier forma de pago inclusive los\n" +
                "pagos que deban realizar las tarjetas de crédito/débito", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("343", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("393", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Pagos aplicables el 2% (incluye Pago local tarjeta de crédito /débito reportada por la Emisora de tarjeta de crédito / entidades del sistema\n" +
                "financiero; adquisición de sustancias minerales dentro del territorio nacional; Recepción de botellas plásticas no retornables de PET)\n", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("344", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("394", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Pagos de bienes y servicios no sujetos a retención o con 0% (distintos de rendimientos financieros)", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("322", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("", "", bold, normal));


        PdfPCell celdaPcao1 = celdaService.crearCelda("POR REGALIAS, COMISIONES, ARRENDAMIENTOS Y OTROS", normal);
        PdfPCell celdaPcao2 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaPcao3 = celdaService.crearCelda("", titleTb);

        celdaPcao1.setBackgroundColor(colorResaltar);
        celdaPcao2.setBackgroundColor(colorResaltar);
        celdaPcao3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaPcao1);
        tabla.addCell(celdaPcao2);
        tabla.addCell(celdaPcao3);


        tabla.addCell(celdaService.crearCelda("Por regalías, derechos de autor, marcas, patentes y similares", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("314", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("364", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Comisiones pagadas a sociedades, nacionales o extranjeras residentes en el Ecuador y establecimientos permanentes domiciliados en el país", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3140", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3640", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Arrendamiento", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));


        tabla.addCell(celdaService.crearCelda("Mercantil", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("319", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("369", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Bienes inmuebles", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("320", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("370", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaRcc1 = celdaService.crearCelda("RELACIONADAS CON EL CAPITAL ( RENDIMIENTOS, GANANCIAS, DIVIDENDOS Y OTROS)", normal);
        PdfPCell celdaRcc2 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaRcc3 = celdaService.crearCelda("", titleTb);

        celdaRcc1.setBackgroundColor(colorResaltar);
        celdaRcc2.setBackgroundColor(colorResaltar);
        celdaRcc3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaRcc1);
        tabla.addCell(celdaRcc2);
        tabla.addCell(celdaRcc3);


        tabla.addCell(celdaService.crearCelda("Rendimientos financieros", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("323", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("373", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Rendimientos financieros entre instituciones del sistema financiero y entidades economía popular y solidaria", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("324", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("374", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Otros Rendimientos financieros 0%", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3230", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("", "", bold, normal));

        tabla.addCell(celdaService.crearCelda("Ganancia en la enajenación de derechos representativos de capital u otros derechos que permitan la exploración, explotación, concesión o\n" +
                "similares de sociedades, que se coticen en las bolsas de valores del Ecuador", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("333", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("383", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Contraprestación en la enajenación de derechos representativos de capital u otros derechos que permitan la exploración, explotación, concesión o\n" +
                "similares de sociedades, no cotizados en las bolsas de valores del Ecuador", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("334", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("384", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaLP1 = celdaService.crearCelda("POR LOTERIAS Y PREMIOS", normal);
        PdfPCell celdaLP2 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaLP3 = celdaService.crearCelda("", titleTb);

        celdaLP1.setBackgroundColor(colorResaltar);
        celdaLP2.setBackgroundColor(colorResaltar);
        celdaLP3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaLP1);
        tabla.addCell(celdaLP2);
        tabla.addCell(celdaLP3);


        tabla.addCell(celdaService.crearCelda("Loterías, rifas, apuestas, pronósticos deportivos y similares", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("335", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("385", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        PdfPCell celdaAOR1 = celdaService.crearCelda("AUTORRETENCIONES Y OTRAS RETENCIONES", normal);
        PdfPCell celdaAOR2 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaAOR3 = celdaService.crearCelda("", titleTb);

        celdaAOR1.setBackgroundColor(colorResaltar);
        celdaAOR2.setBackgroundColor(colorResaltar);
        celdaAOR3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaAOR1);
        tabla.addCell(celdaAOR2);
        tabla.addCell(celdaAOR3);


        tabla.addCell(celdaService.crearCelda("Venta de combustibles\n", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));


        tabla.addCell(celdaService.crearCelda("A comercializadoras", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("336", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("386", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("A distribuidores", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("337", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("387", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("Retención a cargo del propio sujeto pasivo por la comercialización de productos forestales", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3370", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3870", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(celdaService.crearCelda("Otras autorretenciones (inciso 1 y 2 Art.92.1 RLRTI) ", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("350", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("400", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Otras retenciones", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));

        tabla.addCell(celdaService.crearCelda("Aplicables el 2,75%", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3440", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3940", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));

        tabla.addCell(celdaService.crearCelda("Aplicables a otros porcentajes ( Por Donaciones en dinero -Impuesto a las donaciones )", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("346", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("396", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaLiru1 = celdaService.crearCelda("LIQUIDACIÓN DE IMPUESTO A LA RENTA ÚNICO", normal);
        PdfPCell celdaLiru2 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaLiru3 = celdaService.crearCelda("", titleTb);

        celdaLiru1.setBackgroundColor(colorResaltar);
        celdaLiru2.setBackgroundColor(colorResaltar);
        celdaLiru3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaLiru1);
        tabla.addCell(celdaLiru2);
        tabla.addCell(celdaLiru3);


        tabla.addCell(celdaService.crearCelda("IRU Pronósticos deportivos", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));

        tabla.addCell(celdaService.crearCelda("(+) Ingresos generados por la actividad económica de pronósticos deportivos", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3483", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCelda("", normal));

        tabla.addCell(celdaService.crearCelda("(+) Comisiones derivadas de la actividad de pronósticos deportivos", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3484", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCelda("", normal));

        tabla.addCell(celdaService.crearCelda("(-) Premios pagados por pronósticos deportivos", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3485", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCelda("", normal));

        tabla.addCell(celdaService.crearCelda("Impuesto a la renta único sobre los ingresos percibidos por los operadores de pronósticos deportivos", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3480", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("3980", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        PdfPCell celdaSoe1 = celdaService.crearCelda("SUBTOTAL OPERACIONES EFECTUADAS EN EL PAÍS", bold);
        PdfPCell celdaSoe2 = celdaService.crearCeldaConCodigoResaltar("349 ", validarValoresComprobantesPdf.getValor(model.getC419()), bold, bold);
        PdfPCell celdaSoe3 = celdaService.crearCeldaConCodigoResaltar("399 ", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

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


        PdfPCell celdaE1 = celdaService.crearCelda("", titleTb);
        PdfPCell celdaE2 = celdaService.crearCelda("BASE\n" +
                "IMPONIBLE", titleTb);
        PdfPCell celdaE3 = celdaService.crearCelda("VALOR\n" +
                "RETENIDO", titleTb);


        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE3.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);

        PdfPCell celdaTri1 = celdaService.crearCelda("TOTAL DE RETENCIÓN DE IMPUESTO A LA RENTA", normal);
        PdfPCell celdaTri2 = celdaService.crearCelda("399 + 489", normal);
        PdfPCell celdaTri3 = celdaService.crearCeldaConCodigoResaltar("499", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaTri1.setBackgroundColor(colorResaltar);
        celdaTri2.setBackgroundColor(colorResaltar);
        celdaTri3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaTri1);
        tabla.addCell(celdaTri2);
        tabla.addCell(celdaTri3);

        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCelda("", normal));

        PdfPCell celdaVp1 = celdaService.crearCelda("VALORES A PAGAR (luego de imputación al pago)", titleTb);
        PdfPCell celdaVp2 = celdaService.crearCelda("", normal);
        PdfPCell celdaVp3 = celdaService.crearCelda("", normal);

        celdaVp1.setBackgroundColor(colorEncabezadosTablas);
        celdaVp2.setBackgroundColor(colorEncabezadosTablas);
        celdaVp3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaVp1);
        tabla.addCell(celdaVp2);
        tabla.addCell(celdaVp3);


        PdfPCell celdaTir1 = celdaService.crearCelda("TOTAL IMPUESTO A PAGAR", normal);
        PdfPCell celdaTir2 = celdaService.crearCelda("499 - 898", normal);
        PdfPCell celdaTir3 = celdaService.crearCeldaConCodigoResaltar("902", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaTir1.setBackgroundColor(colorResaltar);
        celdaTir2.setBackgroundColor(colorResaltar);
        celdaTir3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaTir1);
        tabla.addCell(celdaTir2);
        tabla.addCell(celdaTir3);


        tabla.addCell(celdaService.crearCelda("Interés por mora ", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("903", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));


        tabla.addCell(celdaService.crearCelda("Multa", normal));
        tabla.addCell(celdaService.crearCelda("", normal));
        tabla.addCell(celdaService.crearCeldaConCodigo("904", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));


        PdfPCell celdaTotal1 = celdaService.crearCelda("TOTAL PAGADO 999 2044.66", normal);
        PdfPCell celdaTotal2 = celdaService.crearCelda("", normal);
        PdfPCell celdaTotal3 = celdaService.crearCeldaConCodigoResaltar("999", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);

        celdaTotal1.setBackgroundColor(colorResaltar);
        celdaTotal2.setBackgroundColor(colorResaltar);
        celdaTotal3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaTotal1);
        tabla.addCell(celdaTotal2);
        tabla.addCell(celdaTotal3);

        document.add(tabla);

    }


}
