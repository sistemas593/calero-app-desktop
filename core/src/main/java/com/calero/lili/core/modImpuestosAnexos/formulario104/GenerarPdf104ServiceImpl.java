package com.calero.lili.core.modImpuestosAnexos.formulario104;


import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF104Dto;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class GenerarPdf104ServiceImpl {


    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;

    private final Color colorResaltar = new Color(255, 253, 208);
    private final Color colorEncabezadosTablas = new Color(0, 85, 237);


    public void generarPdfFormulario104DeclaracionImpuestos(ImpuestosF104Dto model, HttpServletResponse response) {

        try {

            response.setContentType("application/pdf");
            String filename = "Impuestos_" + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font titleFontTb = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            Font normalFont = new Font(Font.HELVETICA, 10);
            Font smallBold = new Font(Font.HELVETICA, 9, Font.BOLD);
            Font small = new Font(Font.HELVETICA, 9);

            agregarEncabezadoImpuestos(document, titleFont, normalFont, model);
            agregarResumenVentasImpuestos(document, smallBold, small, titleFontTb, model);
            agregarLiquidacionIvaImpuestos(document, smallBold, small, titleFontTb, model);
            agregarAdquisicionesImpuestos(document, smallBold, small, titleFontTb, model);
            agregarResumenImpositivoImpuestos(document, smallBold, small, titleFontTb, model);
            agregarSalidaDivisasImpuestos(document, smallBold, small, titleFontTb, model);
            agregarRetencionIVAImpuestos(document, smallBold, small, titleFontTb, model);
            agregarTotalesImpuestos(document, smallBold, small, titleFontTb, model);

            document.close();
            writer.close();


        } catch (Exception exception) {
            log.info(exception.getMessage());
            throw new GeneralException(exception.getMessage());
        }


    }


    private void agregarEncabezadoImpuestos(Document document, com.lowagie.text.Font titleFont, Font normalFont, ImpuestosF104Dto model) {

        Paragraph title = new Paragraph("Sistema de declaración\nde impuestos\na través de internet", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);

        Paragraph info = new Paragraph(
                "Obligación Tributaria:  2011 DECLARACION DE IVA\n Identificación: " + model.getRuc()
                        + "\nRazón Social: " + model.getRazonSocial()
                        + "\nPeríodo Fiscal: " + model.getMes() + " " + model.getAno() + "\nTipo Declaración: ORIGINAL",
                normalFont);
        info.setSpacingBefore(10f);
        document.add(info);
    }

    private void agregarResumenVentasImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{40, 20, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // ENCABEZADOS DE TABLA

        PdfPCell celdaE1 = crearCelda("\nRESUMEN DE VENTAS Y OTRAS OPERACIONES DEL PERÍODO", titleTb);
        PdfPCell celdaE2 = crearCelda("Valor Bruto", titleTb);
        PdfPCell celdaE3 = crearCelda("Valor Neto", titleTb);
        PdfPCell celdaE4 = crearCelda("Impuesto", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);
        celdaE4.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);
        tabla.addCell(celdaE4);

        //

        tabla.addCell(crearCelda("Ventas locales (excluye activos fijos) gravadas tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("401", validarValoresComprobantesPdf.getValor(model.getC401()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("411", validarValoresComprobantesPdf.getValor(model.getC411()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("421", validarValoresComprobantesPdf.getValor(model.getC421()), bold, normal));

        tabla.addCell(crearCelda("Ventas de activos fijos gravadas tarifa diferente de cero ", normal));
        tabla.addCell(crearCeldaConCodigo("402 ", validarValoresComprobantesPdf.getValor(model.getC402()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("412 ", validarValoresComprobantesPdf.getValor(model.getC412()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("422 ", validarValoresComprobantesPdf.getValor(model.getC422()), bold, normal));


        tabla.addCell(crearCelda("Ventas locales (excluye activos fijos) gravadas tarifa 5% ", normal));
        tabla.addCell(crearCeldaConCodigo("445 ", validarValoresComprobantesPdf.getValor(model.getC445()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("425 ", validarValoresComprobantesPdf.getValor(model.getC425()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("435 ", validarValoresComprobantesPdf.getValor(model.getC435()), bold, normal));


        tabla.addCell(crearCelda("IVA generado en la diferencia entre ventas y notas de crédito con distinta tarifa (ajuste a pagar)  ", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("423 ", validarValoresComprobantesPdf.getValor(model.getC423()), bold, normal));


        tabla.addCell(crearCelda("IVA generado en la diferencia entre ventas y notas de crédito con distinta tarifa (ajuste a favor)", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("424 ", validarValoresComprobantesPdf.getValor(model.getC424()), bold, normal));

        tabla.addCell(crearCelda("Ventas locales (excluye activos fijos) gravadas tarifa 0% que no dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("403 ", validarValoresComprobantesPdf.getValor(model.getC403()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("413 ", validarValoresComprobantesPdf.getValor(model.getC413()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Ventas de activos fijos gravadas tarifa 0% que no dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("404 ", validarValoresComprobantesPdf.getValor(model.getC404()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("414 ", validarValoresComprobantesPdf.getValor(model.getC414()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Ventas locales (excluye activos fijos) gravadas tarifa 0% que dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("405 ", validarValoresComprobantesPdf.getValor(model.getC405()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("415 ", validarValoresComprobantesPdf.getValor(model.getC415()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Ventas de activos fijos gravadas tarifa 0% que dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("406 ", validarValoresComprobantesPdf.getValor(model.getC406()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("416 ", validarValoresComprobantesPdf.getValor(model.getC416()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Exportaciones de bienes ", normal));
        tabla.addCell(crearCeldaConCodigo("407 ", validarValoresComprobantesPdf.getValor(model.getC407()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("417 ", validarValoresComprobantesPdf.getValor(model.getC417()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Exportaciones de servicios y/o derechos ", normal));
        tabla.addCell(crearCeldaConCodigo("408 ", validarValoresComprobantesPdf.getValor(model.getC408()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("418 ", validarValoresComprobantesPdf.getValor(model.getC418()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        // CELDAS RESALTADAS

        PdfPCell celdaR1 = crearCelda("TOTAL VENTAS Y OTRAS OPERACIONES", bold);
        PdfPCell celdaR2 = crearCeldaConCodigoResaltar("409 ", validarValoresComprobantesPdf.getValor(model.getC409()), bold, bold);
        PdfPCell celdaR3 = crearCeldaConCodigoResaltar("419 ", validarValoresComprobantesPdf.getValor(model.getC419()), bold, bold);
        PdfPCell celdaR4 = crearCeldaConCodigoResaltar("429 ", validarValoresComprobantesPdf.getValor(model.getC429()), bold, bold);

        celdaR1.setBackgroundColor(colorResaltar);
        celdaR2.setBackgroundColor(colorResaltar);
        celdaR3.setBackgroundColor(colorResaltar);
        celdaR4.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaR1);
        tabla.addCell(celdaR2);
        tabla.addCell(celdaR3);
        tabla.addCell(celdaR4);

        //

        tabla.addCell(crearCelda("Transferencias de bienes y prestación de servicios no objeto o exentos de IVA", normal));
        tabla.addCell(crearCeldaConCodigo("431 ", validarValoresComprobantesPdf.getValor(model.getC431()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("441 ", validarValoresComprobantesPdf.getValor(model.getC441()), bold, bold));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Notas de crédito tarifa 0% por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("442 ", validarValoresComprobantesPdf.getValor(model.getC442()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Notas de crédito tarifa diferente de cero por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("443 ", validarValoresComprobantesPdf.getValor(model.getC443()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("453 ", validarValoresComprobantesPdf.getValor(model.getC453()), bold, normal));


        tabla.addCell(crearCelda("Ingresos por reembolso como intermediario / valores facturados por operadoras de transporte / ingresos\n" +
                "obtenidos por parte de las sociedades de gestión colectiva como intermediarios (informativo)", normal));
        tabla.addCell(crearCeldaConCodigo("434 ", validarValoresComprobantesPdf.getValor(model.getC434()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("444 ", validarValoresComprobantesPdf.getValor(model.getC444()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        document.add(tabla);
    }

    private void agregarLiquidacionIvaImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{40, 20, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // Encabezados


        PdfPCell celdaE1 = crearCelda("\nLIQUIDACIÓN DEL IVA EN EL MES\n", titleTb);
        PdfPCell celdaE2 = crearCelda("", titleTb);
        PdfPCell celdaE3 = crearCelda("", titleTb);
        PdfPCell celdaE4 = crearCelda("", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);
        celdaE4.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);
        tabla.addCell(celdaE4);

        //


        tabla.addCell(crearCelda("Total transferencias gravadas tarifa diferente de cero a contado este mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("480", validarValoresComprobantesPdf.getValor(model.getC480()), bold, bold));


        tabla.addCell(crearCelda("Total impuesto generado", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese campo 429)", normal));
        tabla.addCell(crearCeldaConCodigo("481", validarValoresComprobantesPdf.getValor(model.getC481()), bold, bold));


        tabla.addCell(crearCelda("Impuesto a liquidar del mes anterior", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(verificar que el valor corresponda al campo 485 por\n" +
                "ventas a crédito de periodos anteriores)", normal));
        tabla.addCell(crearCeldaConCodigo("483", validarValoresComprobantesPdf.getValor(model.getC483()), bold, bold));


        tabla.addCell(crearCelda("Impuesto a liquidar en este mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("484", validarValoresComprobantesPdf.getValor(model.getC484()), bold, bold));


        tabla.addCell(crearCelda("Impuesto a liquidar en el próximo mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("482-484", normal));
        tabla.addCell(crearCeldaConCodigo("485", validarValoresComprobantesPdf.getValor(model.getC485()), bold, bold));


        tabla.addCell(crearCelda("Mes a pagar el monto de IVA diferente de cero por ventas a crédito de este mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("486", validarValoresComprobantesPdf.getValor(model.getC486()), bold, bold));


        // CELDAS DEL COLOR RESALTADO

        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A LIQUIDAR EN ESTE MES", normal);
        PdfPCell celda2 = crearCelda("", normal);
        PdfPCell celda3 = crearCelda("483+484", normal);
        PdfPCell celda4 = crearCeldaConCodigoResaltar("486", validarValoresComprobantesPdf.getValor(model.getC486()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);
        celda4.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);
        tabla.addCell(celda4);


        document.add(tabla);
    }

    private void agregarAdquisicionesImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{40, 20, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // Encabezados

        PdfPCell celdaE1 = crearCelda("\nRESUMEN DE ADQUISICIONES Y PAGOS DEL PERÍODO QUE DECLARA\n", titleTb);
        PdfPCell celdaE2 = crearCelda("VALOR BRUTO", titleTb);
        PdfPCell celdaE3 = crearCelda("VALOR NETO ", titleTb);
        PdfPCell celdaE4 = crearCelda("IMPUESTO GENERADO", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);
        celdaE4.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);
        tabla.addCell(celdaE4);


        tabla.addCell(crearCelda("Adquisiciones y pagos (excluye activos fijos) gravados tarifa diferente de cero (con derecho a crédito\n" +
                "tributario)\n", normal));
        tabla.addCell(crearCeldaConCodigo("500", validarValoresComprobantesPdf.getValor(model.getC500()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("510", validarValoresComprobantesPdf.getValor(model.getC501()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("520", validarValoresComprobantesPdf.getValor(model.getC520()), bold, bold));

        tabla.addCell(crearCelda("Adquisiciones locales de activos fijos gravados tarifa diferente de cero (con derecho a crédito tributario)", normal));
        tabla.addCell(crearCeldaConCodigo("501", validarValoresComprobantesPdf.getValor(model.getC501()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("511", validarValoresComprobantesPdf.getValor(model.getC511()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("521", validarValoresComprobantesPdf.getValor(model.getC521()), bold, bold));

        tabla.addCell(crearCelda("Adquisiciones y pagos locales (excluye activos fijos) gravados con tarifa 5% (con derecho a crédito tributario)", normal));
        tabla.addCell(crearCeldaConCodigo("540", validarValoresComprobantesPdf.getValor(model.getC540()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("550", validarValoresComprobantesPdf.getValor(model.getC550()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("560", validarValoresComprobantesPdf.getValor(model.getC560()), bold, bold));


        tabla.addCell(crearCelda("Otras adquisiciones y pagos gravados tarifa diferente de cero (sin derecho a crédito tributario) ", normal));
        tabla.addCell(crearCeldaConCodigo("502", validarValoresComprobantesPdf.getValor(model.getC502()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("512", validarValoresComprobantesPdf.getValor(model.getC512()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("522", validarValoresComprobantesPdf.getValor(model.getC522()), bold, bold));

        tabla.addCell(crearCelda("Importaciones de servicios y/o derechos gravados tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("503", validarValoresComprobantesPdf.getValor(model.getC503()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("513", validarValoresComprobantesPdf.getValor(model.getC513()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("523", validarValoresComprobantesPdf.getValor(model.getC523()), bold, bold));


        tabla.addCell(crearCelda("Importaciones de bienes (excluye activos fijos) gravados tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("504", validarValoresComprobantesPdf.getValor(model.getC504()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("514", validarValoresComprobantesPdf.getValor(model.getC514()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("524", validarValoresComprobantesPdf.getValor(model.getC524()), bold, bold));


        tabla.addCell(crearCelda("Importaciones de activos fijos gravados tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("505", validarValoresComprobantesPdf.getValor(model.getC505()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("515", validarValoresComprobantesPdf.getValor(model.getC515()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("525", validarValoresComprobantesPdf.getValor(model.getC525()), bold, bold));

        tabla.addCell(crearCelda("IVA generado en la diferencia entre adquisiciones y notas de crédito con distinta tarifa (ajuste en positivo al\n" +
                "crédito tributario)", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("526", validarValoresComprobantesPdf.getValor(model.getC526()), bold, bold));


        tabla.addCell(crearCelda("IVA generado en la diferencia entre adquisiciones y notas de crédito con distinta tarifa (ajuste en negativo al\n" +
                "crédito tributario)", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("527", validarValoresComprobantesPdf.getValor(model.getC527()), bold, bold));


        tabla.addCell(crearCelda("Importaciones de bienes (incluye activos fijos) gravados tarifa 0%", normal));
        tabla.addCell(crearCeldaConCodigo("506", validarValoresComprobantesPdf.getValor(model.getC506()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("516", validarValoresComprobantesPdf.getValor(model.getC516()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Adquisiciones y pagos (incluye activos fijos) gravados tarifa 0%", normal));
        tabla.addCell(crearCeldaConCodigo("507", validarValoresComprobantesPdf.getValor(model.getC507()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("517", validarValoresComprobantesPdf.getValor(model.getC517()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Adquisiciones realizadas a contribuyentes RISE (hasta diciembre 2021), NEGOCIOS POPULARES (desde\n" +
                "enero 2022)", normal));
        tabla.addCell(crearCeldaConCodigo("508", validarValoresComprobantesPdf.getValor(model.getC508()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("518", validarValoresComprobantesPdf.getValor(model.getC518()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        // RESALTADO

        PdfPCell celda1 = crearCelda("TOTAL ADQUISICIONES Y PAGOS", normal);
        PdfPCell celda2 = crearCeldaConCodigoResaltar("509", validarValoresComprobantesPdf.getValor(model.getC509()), bold, bold);
        PdfPCell celda3 = crearCeldaConCodigoResaltar("519", validarValoresComprobantesPdf.getValor(model.getC519()), bold, bold);
        PdfPCell celda4 = crearCeldaConCodigoResaltar("529", validarValoresComprobantesPdf.getValor(model.getC529()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);
        celda4.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);
        tabla.addCell(celda4);

        //

        tabla.addCell(crearCelda("Adquisiciones no objeto de IVA", normal));
        tabla.addCell(crearCeldaConCodigo("531", validarValoresComprobantesPdf.getValor(model.getC531()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("541", validarValoresComprobantesPdf.getValor(model.getC541()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Adquisiciones exentas del pago de IVA", normal));
        tabla.addCell(crearCeldaConCodigo("532", validarValoresComprobantesPdf.getValor(model.getC532()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("542", validarValoresComprobantesPdf.getValor(model.getC542()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Notas de crédito tarifa 0% por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("543", validarValoresComprobantesPdf.getValor(model.getC543()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Notas de crédito tarifa diferente de cero por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("544", validarValoresComprobantesPdf.getValor(model.getC544()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("554", validarValoresComprobantesPdf.getValor(model.getC554()), bold, bold));

        tabla.addCell(crearCelda("Pagos netos por reembolso como intermediario / valores facturados por socios a operadoras de transporte /\n" +
                "pagos realizados por parte de las sociedades de gestión colectiva como intermediarios (informativo)", normal));
        tabla.addCell(crearCeldaConCodigo("535", validarValoresComprobantesPdf.getValor(model.getC535()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("545", validarValoresComprobantesPdf.getValor(model.getC545()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("555", validarValoresComprobantesPdf.getValor(model.getC555()), bold, bold));


        tabla.addCell(crearCelda("Factor de proporcionalidad para crédito tributario", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("(411+412+420+435+415+416+417+418) / 419", bold));
        tabla.addCell(crearCeldaConCodigo("563", validarValoresComprobantesPdf.getValor(model.getC563()), bold, bold));


        tabla.addCell(crearCelda("Crédito tributario aplicable en este período (de acuerdo al factor de proporcionalidad o a su contabilidad)", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("(520+521+534+560+523+524+525+526-527) x 563", bold));
        tabla.addCell(crearCeldaConCodigo("564", validarValoresComprobantesPdf.getValor(model.getC564()), bold, bold));


        tabla.addCell(crearCelda("Valor de IVA no considerado como crédito tributario por factor de proporcionalidad", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("565", validarValoresComprobantesPdf.getValor(model.getC565()), bold, bold));


        document.add(tabla);
    }

    private void agregarResumenImpositivoImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {


        PdfPTable tabla = new PdfPTable(new float[]{40, 20, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // Encabezados

        PdfPCell celdaE1 = crearCelda("\nRESUMEN IMPOSITIVO: AGENTE DE PERCEPCIÓN DEL IMPUESTO AL VALOR AGREGADO\n", titleTb);
        PdfPCell celdaE2 = crearCelda("", titleTb);
        PdfPCell celdaE3 = crearCelda("", titleTb);
        PdfPCell celdaE4 = crearCelda("", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);
        celdaE4.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);
        tabla.addCell(celdaE4);

        //

        tabla.addCell(crearCelda("Impuesto causado", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(si la diferencia de los campos 499-564 es mayor que cero)", normal));
        tabla.addCell(crearCeldaConCodigo("601", validarValoresComprobantesPdf.getValor(model.getC601()), bold, bold));


        tabla.addCell(crearCelda("Crédito tributario aplicable en este período", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(si la diferencia de los campos 499-564 es menor que cero)", normal));
        tabla.addCell(crearCeldaConCodigo("602", validarValoresComprobantesPdf.getValor(model.getC602()), bold, bold));


        tabla.addCell(crearCelda("(-) Compensación de IVA por ventas efectuadas con medio electrónico y/o IVA devuelto o descontado por transacciones realizadas con personas adultas mayores\n" +
                "o personas con discapacidad\n", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("603", validarValoresComprobantesPdf.getValor(model.getC603()), bold, bold));


        tabla.addCell(crearCelda("(-) Compensación de IVA por ventas efectuadas con medio electrónico y/o IVA devuelto o descontado por transacciones realizadas con personas adultas mayores\n" +
                "o personas con discapacidad\n " +
                "(-) Saldo crédito tributario del mes anterior", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("603", validarValoresComprobantesPdf.getValor(model.getC603()), bold, bold));


        tabla.addCell(crearCelda("Por adquisiciones e importaciones", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 615 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("605", validarValoresComprobantesPdf.getValor(model.getC605()), bold, bold));


        tabla.addCell(crearCelda("Por retenciones en la fuente de IVA que le han sido efectuadas", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 617 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("606", validarValoresComprobantesPdf.getValor(model.getC606()), bold, bold));

        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas con medio\n" +
                "electrónico", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 618 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("607", validarValoresComprobantesPdf.getValor(model.getC607()), bold, bold));


        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas en zonas\n" +
                "afectadas - Ley de solidaridad, restitución de crédito tributario en\n" +
                "resoluciones administrativas o sentencias judiciales de última\n" +
                "instancia\n", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 619 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("608", validarValoresComprobantesPdf.getValor(model.getC608()), bold, bold));


        tabla.addCell(crearCelda("(-) Retenciones en la fuente de IVA que le han sido efectuadas en este período", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("609", validarValoresComprobantesPdf.getValor(model.getC609()), bold, bold));


        tabla.addCell(crearCelda("(-) IVA devuelto o descontado por transacciones realizadas con personas adultas mayores o personas con discapacidad", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("622", validarValoresComprobantesPdf.getValor(model.getC622()), bold, bold));


        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto o descontado por adquisiciones efectuadas con medio electrónico", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("610", validarValoresComprobantesPdf.getValor(model.getC610()), bold, bold));


        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto e IVA rechazado (por concepto de devoluciones de IVA), ajuste de IVA por procesos de control y otros (adquisiciones en importaciones),\n" +
                "imputables al crédito tributario", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("612", validarValoresComprobantesPdf.getValor(model.getC612()), bold, bold));


        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto e IVA rechazado, ajuste de IVA por procesos de control y otros (por concepto retenciones en la fuente de IVA), imputables al crédito\n" +
                "tributario", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("613", validarValoresComprobantesPdf.getValor(model.getC613()), bold, bold));

        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto por otras instituciones del sector público imputable al crédito tributario en el mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("614", validarValoresComprobantesPdf.getValor(model.getC614()), bold, bold));


        tabla.addCell(crearCelda("Por adquisiciones e importaciones", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("615", validarValoresComprobantesPdf.getValor(model.getC615()), bold, bold));


        tabla.addCell(crearCelda("Por retenciones en la fuente de IVA que le han sido efectuadas", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("617", validarValoresComprobantesPdf.getValor(model.getC617()), bold, bold));


        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas con medio electrónico", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("618", validarValoresComprobantesPdf.getValor(model.getC618()), bold, bold));

        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas en zonas afectadas - Ley de solidaridad, restitución de crédito tributario en\n" +
                "resoluciones administrativas o sentencias judiciales de última instancia", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("619", validarValoresComprobantesPdf.getValor(model.getC619()), bold, bold));


        tabla.addCell(crearCelda("IVA pagado y no compensado, en la adquisición local o importación de bienes o servicios que se carga al gasto de Impuesto a la Renta.", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("624", validarValoresComprobantesPdf.getValor(model.getC624()), bold, bold));

        tabla.addCell(crearCelda("Ajuste del crédito tributario de Impuesto al Valor Agregado pagado en adquisiciones locales e importaciones de bienes y servicios superior a cinco (5) años ", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("625", validarValoresComprobantesPdf.getValor(model.getC625()), bold, bold));


        tabla.addCell(crearCelda("SUBTOTAL A PAGAR", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("Si (601-602-603-604-605-606-607-608-609+610+611+612+613+614) > 0", normal));
        tabla.addCell(crearCeldaConCodigo("620", validarValoresComprobantesPdf.getValor(model.getC620()), bold, bold));

        // RESALTADO

        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A PAGAR POR PERCEPCIÓN Y RETENCIONES EFECTUADAS EN VENTAS (varios\n" +
                "porcentajes)\n", normal);
        PdfPCell celda2 = crearCelda("", bold);
        PdfPCell celda3 = crearCelda("620+621", bold);
        PdfPCell celda4 = crearCeldaConCodigoResaltar("699", validarValoresComprobantesPdf.getValor(model.getC699()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);
        celda4.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);
        tabla.addCell(celda4);

        //
        document.add(tabla);
    }

    private void agregarSalidaDivisasImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // Encabezados

        PdfPCell celdaE1 = crearCelda("\nIMPUESTO A LA SALIDA DE DIVISAS A EFECTOS DE DEVOLUCIÓN A EXPORTADORES HABITUALES DE BIENES\n", titleTb);
        PdfPCell celdaE2 = crearCelda("VALOR", titleTb);
        PdfPCell celdaE3 = crearCelda("ISD PAGADO", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);

        //

        tabla.addCell(crearCelda("Importaciones de materias primas, insumos y bienes de capital que sean incorporadas en procesos productivos de bienes que se\n" +
                "exporten\n", normal));
        tabla.addCell(crearCeldaConCodigo("700", validarValoresComprobantesPdf.getValor(model.getC700()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("701", validarValoresComprobantesPdf.getValor(model.getC701()), bold, bold));


        PdfPCell celda1 = crearCelda("", normal);
        PdfPCell celda2 = crearCelda("", normal);
        PdfPCell celda3 = crearCelda("PORCENTAJE", titleTb);

        celda3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);

        tabla.addCell(crearCelda("Proporción del ingreso neto de divisas desde el exterior al Ecuador, respecto del total de las exportaciones netas de bienes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("702", validarValoresComprobantesPdf.getValor(model.getC702()), bold, bold));

        document.add(tabla);
    }

    private void agregarRetencionIVAImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // Encabezados

        PdfPCell celdaE1 = crearCelda("\nAGENTE DE RETENCIÓN DEL IMPUESTO AL VALOR AGREGADO", titleTb);
        PdfPCell celdaE2 = crearCelda("", titleTb);
        PdfPCell celdaE3 = crearCelda("", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);

        //

        tabla.addCell(crearCelda("Retención del 10%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("721", validarValoresComprobantesPdf.getValor(model.getC721()), bold, bold));


        tabla.addCell(crearCelda("Retención del 20%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("723", validarValoresComprobantesPdf.getValor(model.getC723()), bold, bold));

        tabla.addCell(crearCelda("Retención del 30%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("725", validarValoresComprobantesPdf.getValor(model.getC725()), bold, bold));

        tabla.addCell(crearCelda("Retención del 50%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("727", validarValoresComprobantesPdf.getValor(model.getC727()), bold, bold));

        tabla.addCell(crearCelda("Retención del 70%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("729", validarValoresComprobantesPdf.getValor(model.getC729()), bold, bold));

        tabla.addCell(crearCelda("Retención del 100%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("731", validarValoresComprobantesPdf.getValor(model.getC731()), bold, bold));


        tabla.addCell(crearCelda("TOTAL IMPUESTO RETENIDO", normal));
        tabla.addCell(crearCelda("721+723+725+727+729+731", normal));
        tabla.addCell(crearCeldaConCodigo("799", validarValoresComprobantesPdf.getValor(model.getC799()), bold, bold));


        tabla.addCell(crearCelda("Devolución provisional de IVA mediante compensación con retenciones efectuadas", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("800", validarValoresComprobantesPdf.getValor(model.getC800()), bold, bold));


        // Reslatados

        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A PAGAR POR RETENCIÓN", bold);
        PdfPCell celda2 = crearCelda("(799-800-802)", bold);
        PdfPCell celda3 = crearCeldaConCodigo("801", validarValoresComprobantesPdf.getValor(model.getC801()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);

        PdfPCell celdaA1 = crearCelda("TOTAL CONSOLIDADO DE IMPUESTO AL VALOR AGREGADO", titleTb);
        PdfPCell celdaA2 = crearCelda("(699+801)", titleTb);
        PdfPCell celdaA3 = crearCeldaConCodigo("859", validarValoresComprobantesPdf.getValor(model.getC859()), titleTb, titleTb);

        celdaA1.setBackgroundColor(colorEncabezadosTablas);
        celdaA2.setBackgroundColor(colorEncabezadosTablas);
        celdaA3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaA1);
        tabla.addCell(celdaA2);
        tabla.addCell(celdaA3);

        //

        document.add(tabla);
    }

    private void agregarTotalesImpuestos(Document document, Font bold, Font normal, Font titleTb, ImpuestosF104Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        // Encabezados

        PdfPCell celdaE1 = crearCelda("\nVALORES A PAGAR (luego de imputación al pago en declaraciones sustitutivas)", titleTb);
        PdfPCell celdaE2 = crearCelda("", titleTb);
        PdfPCell celdaE3 = crearCelda("", titleTb);

        celdaE1.setBackgroundColor(colorEncabezadosTablas);
        celdaE2.setBackgroundColor(colorEncabezadosTablas);
        celdaE3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaE1);
        tabla.addCell(celdaE2);
        tabla.addCell(celdaE3);


        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A PAGAR ", bold);
        PdfPCell celda2 = crearCelda("(859-898)", bold);
        PdfPCell celda3 = crearCeldaConCodigo("902", validarValoresComprobantesPdf.getValor(model.getC902()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);


        tabla.addCell(crearCelda("Interés por mora", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("903", validarValoresComprobantesPdf.getValor(model.getC903()), bold, bold));


        tabla.addCell(crearCelda("Multa", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("904", validarValoresComprobantesPdf.getValor(model.getC904()), bold, bold));


        PdfPCell celdaT1 = crearCelda("TOTAL PAGADO", bold);
        PdfPCell celdaT2 = crearCelda("", bold);
        PdfPCell celdaT3 = crearCeldaConCodigo("999", validarValoresComprobantesPdf.getValor(model.getC999()), bold, bold);

        celdaT1.setBackgroundColor(colorResaltar);
        celdaT2.setBackgroundColor(colorResaltar);
        celdaT3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaT1);
        tabla.addCell(celdaT2);
        tabla.addCell(celdaT3);


        document.add(tabla);
    }


    private PdfPCell crearCelda(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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

}
