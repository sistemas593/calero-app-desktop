package com.calero.lili.api.modImpuestosAnexos;

import com.calero.lili.api.builder.retencion.RdepBuilder;
import com.calero.lili.api.modImpuestosAnexos.builder.RetencionFuenteBuilder;
import com.calero.lili.api.modImpuestosAnexos.retencion.Rdep;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF104Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import com.calero.lili.api.utils.validaciones.ValidarValoresComprobantesPdf;
import com.calero.lili.api.dtos.FilterImpuestoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.utils.DateUtils;
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


    public void generarPdfDeclaracionImpuestos(ImpuestosF104Dto model, HttpServletResponse response) {

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


    // Metodos para agregar las distintas secciones de Declaración de IVA

    private void agregarEncabezadoImpuestos(Document document, Font titleFont, Font normalFont, ImpuestosF104Dto model) {

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
        tabla.addCell(crearCeldaConCodigo("445 ", validarValoresComprobantesPdf.getValor(model.getC404()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("425 ", validarValoresComprobantesPdf.getValor(model.getC402()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("435 ", validarValoresComprobantesPdf.getValor(model.getC403()), bold, normal));


        tabla.addCell(crearCelda("IVA generado en la diferencia entre ventas y notas de crédito con distinta tarifa (ajuste a pagar)  ", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("423 ", validarValoresComprobantesPdf.getValor(model.getC405()), bold, normal));


        tabla.addCell(crearCelda("IVA generado en la diferencia entre ventas y notas de crédito con distinta tarifa (ajuste a favor)", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("424 ", validarValoresComprobantesPdf.getValor(model.getC406()), bold, normal));

        tabla.addCell(crearCelda("Ventas locales (excluye activos fijos) gravadas tarifa 0% que no dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("403 ", validarValoresComprobantesPdf.getValor(model.getC403()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("413 ", validarValoresComprobantesPdf.getValor(model.getC413()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Ventas de activos fijos gravadas tarifa 0% que no dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("404 ", validarValoresComprobantesPdf.getValor(model.getC404()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("414 ", validarValoresComprobantesPdf.getValor(model.getC414()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Ventas locales (excluye activos fijos) gravadas tarifa 0% que dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("405 ", validarValoresComprobantesPdf.getValor(model.getC411()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("415 ", validarValoresComprobantesPdf.getValor(model.getC412()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Ventas de activos fijos gravadas tarifa 0% que dan derecho a crédito tributario ", normal));
        tabla.addCell(crearCeldaConCodigo("406 ", validarValoresComprobantesPdf.getValor(model.getC413()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("416 ", validarValoresComprobantesPdf.getValor(model.getC414()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Exportaciones de bienes ", normal));
        tabla.addCell(crearCeldaConCodigo("407 ", validarValoresComprobantesPdf.getValor(model.getC415()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("417 ", validarValoresComprobantesPdf.getValor(model.getC416()), bold, normal));
        tabla.addCell(crearCelda("", normal));

        tabla.addCell(crearCelda("Exportaciones de servicios y/o derechos ", normal));
        tabla.addCell(crearCeldaConCodigo("408 ", validarValoresComprobantesPdf.getValor(model.getC417()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("418 ", validarValoresComprobantesPdf.getValor(model.getC418()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        // CELDAS RESALTADAS

        PdfPCell celdaR1 = crearCelda("TOTAL VENTAS Y OTRAS OPERACIONES", bold);
        PdfPCell celdaR2 = crearCeldaConCodigoResaltar("409 ", validarValoresComprobantesPdf.getValor(model.getC419()), bold, bold);
        PdfPCell celdaR3 = crearCeldaConCodigoResaltar("419 ", validarValoresComprobantesPdf.getValor(model.getC420()), bold, bold);
        PdfPCell celdaR4 = crearCeldaConCodigoResaltar("429 ", validarValoresComprobantesPdf.getValor(model.getC421()), bold, bold);

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
        tabla.addCell(crearCeldaConCodigo("442 ", validarValoresComprobantesPdf.getValor(model.getC424()), bold, normal));
        tabla.addCell(crearCelda("", normal));


        tabla.addCell(crearCelda("Notas de crédito tarifa diferente de cero por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("443 ", validarValoresComprobantesPdf.getValor(model.getC425()), bold, normal));
        tabla.addCell(crearCeldaConCodigo("453 ", validarValoresComprobantesPdf.getValor(model.getC429()), bold, normal));


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
        tabla.addCell(crearCeldaConCodigo("480", validarValoresComprobantesPdf.getValor(model.getC424()), bold, bold));


        tabla.addCell(crearCelda("Total impuesto generado", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese campo 429)", normal));
        tabla.addCell(crearCeldaConCodigo("481", validarValoresComprobantesPdf.getValor(model.getC425()), bold, bold));


        tabla.addCell(crearCelda("Impuesto a liquidar del mes anterior", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(verificar que el valor corresponda al campo 485 por\n" +
                "ventas a crédito de periodos anteriores)", normal));
        tabla.addCell(crearCeldaConCodigo("483", validarValoresComprobantesPdf.getValor(model.getC431()), bold, bold));


        tabla.addCell(crearCelda("Impuesto a liquidar en este mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("484", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Impuesto a liquidar en el próximo mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("482-484", normal));
        tabla.addCell(crearCeldaConCodigo("485", validarValoresComprobantesPdf.getValor(model.getC435()), bold, bold));


        tabla.addCell(crearCelda("Mes a pagar el monto de IVA diferente de cero por ventas a crédito de este mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("486", validarValoresComprobantesPdf.getValor(model.getC441()), bold, bold));


        // CELDAS DEL COLOR RESALTADO

        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A LIQUIDAR EN ESTE MES", normal);
        PdfPCell celda2 = crearCelda("", normal);
        PdfPCell celda3 = crearCelda("483+484", normal);
        PdfPCell celda4 = crearCeldaConCodigoResaltar("486", validarValoresComprobantesPdf.getValor(model.getC442()), bold, bold);

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
        tabla.addCell(crearCeldaConCodigo("500", validarValoresComprobantesPdf.getValor(model.getC443()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("510", validarValoresComprobantesPdf.getValor(model.getC444()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("520", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));

        tabla.addCell(crearCelda("Adquisiciones locales de activos fijos gravados tarifa diferente de cero (con derecho a crédito tributario)", normal));
        tabla.addCell(crearCeldaConCodigo("501", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("511", validarValoresComprobantesPdf.getValor(model.getC453()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("521", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));

        tabla.addCell(crearCelda("Adquisiciones y pagos locales (excluye activos fijos) gravados con tarifa 5% (con derecho a crédito tributario)", normal));
        tabla.addCell(crearCeldaConCodigo("540", validarValoresComprobantesPdf.getValor(model.getC540()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("550", validarValoresComprobantesPdf.getValor(model.getC550()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("560", validarValoresComprobantesPdf.getValor(model.getC560()), bold, bold));


        tabla.addCell(crearCelda("Otras adquisiciones y pagos gravados tarifa diferente de cero (sin derecho a crédito tributario) ", normal));
        tabla.addCell(crearCeldaConCodigo("502", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("512", validarValoresComprobantesPdf.getValor(model.getC453()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("522", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));

        tabla.addCell(crearCelda("Importaciones de servicios y/o derechos gravados tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("503", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("513", validarValoresComprobantesPdf.getValor(model.getC453()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("523", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("Importaciones de bienes (excluye activos fijos) gravados tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("504", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("514", validarValoresComprobantesPdf.getValor(model.getC453()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("524", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("Importaciones de activos fijos gravados tarifa diferente de cero", normal));
        tabla.addCell(crearCeldaConCodigo("505", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("515", validarValoresComprobantesPdf.getValor(model.getC453()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("525", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));

        tabla.addCell(crearCelda("IVA generado en la diferencia entre adquisiciones y notas de crédito con distinta tarifa (ajuste en positivo al\n" +
                "crédito tributario)", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("526", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("IVA generado en la diferencia entre adquisiciones y notas de crédito con distinta tarifa (ajuste en negativo al\n" +
                "crédito tributario)", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("527", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("Importaciones de bienes (incluye activos fijos) gravados tarifa 0%", normal));
        tabla.addCell(crearCeldaConCodigo("506", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("516", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Adquisiciones y pagos (incluye activos fijos) gravados tarifa 0%", normal));
        tabla.addCell(crearCeldaConCodigo("507", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("517", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Adquisiciones realizadas a contribuyentes RISE (hasta diciembre 2021), NEGOCIOS POPULARES (desde\n" +
                "enero 2022)", normal));
        tabla.addCell(crearCeldaConCodigo("508", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("518", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        // RESALTADO

        PdfPCell celda1 = crearCelda("TOTAL ADQUISICIONES Y PAGOS", normal);
        PdfPCell celda2 = crearCeldaConCodigoResaltar("509", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold);
        PdfPCell celda3 = crearCeldaConCodigoResaltar("519", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold);
        PdfPCell celda4 = crearCeldaConCodigoResaltar("529", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold);

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
        tabla.addCell(crearCeldaConCodigo("531", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("541", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Adquisiciones exentas del pago de IVA", normal));
        tabla.addCell(crearCeldaConCodigo("532", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("542", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Notas de crédito tarifa 0% por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("543", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCelda("", bold));


        tabla.addCell(crearCelda("Notas de crédito tarifa diferente de cero por compensar próximo mes", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("544", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("554", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));

        tabla.addCell(crearCelda("Pagos netos por reembolso como intermediario / valores facturados por socios a operadoras de transporte /\n" +
                "pagos realizados por parte de las sociedades de gestión colectiva como intermediarios (informativo)", normal));
        tabla.addCell(crearCeldaConCodigo("535", validarValoresComprobantesPdf.getValor(model.getC445()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("545", validarValoresComprobantesPdf.getValor(model.getC453()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("55", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("Factor de proporcionalidad para crédito tributario", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("(411+412+420+435+415+416+417+418) / 419", bold));
        tabla.addCell(crearCeldaConCodigo("563", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("Crédito tributario aplicable en este período (de acuerdo al factor de proporcionalidad o a su contabilidad)", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("(520+521+534+560+523+524+525+526-527) x 563", bold));
        tabla.addCell(crearCeldaConCodigo("564", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


        tabla.addCell(crearCelda("Valor de IVA no considerado como crédito tributario por factor de proporcionalidad", normal));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCelda("", bold));
        tabla.addCell(crearCeldaConCodigo("565", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold));


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
        tabla.addCell(crearCeldaConCodigo("601", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Crédito tributario aplicable en este período", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(si la diferencia de los campos 499-564 es menor que cero)", normal));
        tabla.addCell(crearCeldaConCodigo("602", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(-) Compensación de IVA por ventas efectuadas con medio electrónico y/o IVA devuelto o descontado por transacciones realizadas con personas adultas mayores\n" +
                "o personas con discapacidad\n", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("603", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(-) Compensación de IVA por ventas efectuadas con medio electrónico y/o IVA devuelto o descontado por transacciones realizadas con personas adultas mayores\n" +
                "o personas con discapacidad\n " +
                "(-) Saldo crédito tributario del mes anterior", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("603", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Por adquisiciones e importaciones", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 615 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("605", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Por retenciones en la fuente de IVA que le han sido efectuadas", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 617 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("606", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas con medio\n" +
                "electrónico", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 618 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("607", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas en zonas\n" +
                "afectadas - Ley de solidaridad, restitución de crédito tributario en\n" +
                "resoluciones administrativas o sentencias judiciales de última\n" +
                "instancia\n", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("(trasládese el campo 619 de la declaración del período\n" +
                "anterior)", normal));
        tabla.addCell(crearCeldaConCodigo("608", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(-) Retenciones en la fuente de IVA que le han sido efectuadas en este período", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("609", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(-) IVA devuelto o descontado por transacciones realizadas con personas adultas mayores o personas con discapacidad", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("622", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto o descontado por adquisiciones efectuadas con medio electrónico", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("610", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto e IVA rechazado (por concepto de devoluciones de IVA), ajuste de IVA por procesos de control y otros (adquisiciones en importaciones),\n" +
                "imputables al crédito tributario", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("612", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto e IVA rechazado, ajuste de IVA por procesos de control y otros (por concepto retenciones en la fuente de IVA), imputables al crédito\n" +
                "tributario", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("613", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("(+) Ajuste por IVA devuelto por otras instituciones del sector público imputable al crédito tributario en el mes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("614", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Por adquisiciones e importaciones", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("615", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Por retenciones en la fuente de IVA que le han sido efectuadas", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("617", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas con medio electrónico", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("618", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Por compensación de IVA por ventas efectuadas en zonas afectadas - Ley de solidaridad, restitución de crédito tributario en\n" +
                "resoluciones administrativas o sentencias judiciales de última instancia", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("619", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("IVA pagado y no compensado, en la adquisición local o importación de bienes o servicios que se carga al gasto de Impuesto a la Renta.", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("624", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Ajuste del crédito tributario de Impuesto al Valor Agregado pagado en adquisiciones locales e importaciones de bienes y servicios superior a cinco (5) años ", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("625", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("SUBTOTAL A PAGAR", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCelda("Si (601-602-603-604-605-606-607-608-609+610+611+612+613+614) > 0", normal));
        tabla.addCell(crearCeldaConCodigo("620", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        // RESALTADO

        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A PAGAR POR PERCEPCIÓN Y RETENCIONES EFECTUADAS EN VENTAS (varios\n" +
                "porcentajes)\n", normal);
        PdfPCell celda2 = crearCelda("", bold);
        PdfPCell celda3 = crearCelda("620+621", bold);
        PdfPCell celda4 = crearCeldaConCodigoResaltar("699", validarValoresComprobantesPdf.getValor(model.getC454()), bold, bold);

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
        tabla.addCell(crearCeldaConCodigo("700", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));
        tabla.addCell(crearCeldaConCodigo("701", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        PdfPCell celda1 = crearCelda("", normal);
        PdfPCell celda2 = crearCelda("", normal);
        PdfPCell celda3 = crearCelda("PORCENTAJE", titleTb);

        celda3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);

        tabla.addCell(crearCelda("Proporción del ingreso neto de divisas desde el exterior al Ecuador, respecto del total de las exportaciones netas de bienes", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("702", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

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
        tabla.addCell(crearCeldaConCodigo("721", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Retención del 20%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("723", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Retención del 30%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("725", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Retención del 50%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("727", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Retención del 70%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("729", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));

        tabla.addCell(crearCelda("Retención del 100%", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("731", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("TOTAL IMPUESTO RETENIDO", normal));
        tabla.addCell(crearCelda("721+723+725+727+729+731", normal));
        tabla.addCell(crearCeldaConCodigo("799", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Devolución provisional de IVA mediante compensación con retenciones efectuadas", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("800", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        // Reslatados

        PdfPCell celda1 = crearCelda("TOTAL IMPUESTO A PAGAR POR RETENCIÓN", bold);
        PdfPCell celda2 = crearCelda("(799-800-802)", bold);
        PdfPCell celda3 = crearCeldaConCodigo("801", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);

        PdfPCell celdaA1 = crearCelda("TOTAL CONSOLIDADO DE IMPUESTO AL VALOR AGREGADO", titleTb);
        PdfPCell celdaA2 = crearCelda("(699+801)", titleTb);
        PdfPCell celdaA3 = crearCeldaConCodigo("859", validarValoresComprobantesPdf.getValor(model.getC434()), titleTb, titleTb);

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
        PdfPCell celda3 = crearCeldaConCodigo("902", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold);

        celda1.setBackgroundColor(colorResaltar);
        celda2.setBackgroundColor(colorResaltar);
        celda3.setBackgroundColor(colorResaltar);

        tabla.addCell(celda1);
        tabla.addCell(celda2);
        tabla.addCell(celda3);


        tabla.addCell(crearCelda("Interés por mora", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("903", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        tabla.addCell(crearCelda("Multa", normal));
        tabla.addCell(crearCelda("", normal));
        tabla.addCell(crearCeldaConCodigo("904", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold));


        PdfPCell celdaT1 = crearCelda("TOTAL PAGADO", bold);
        PdfPCell celdaT2 = crearCelda("", bold);
        PdfPCell celdaT3 = crearCeldaConCodigo("999", validarValoresComprobantesPdf.getValor(model.getC434()), bold, bold);

        celdaT1.setBackgroundColor(colorResaltar);
        celdaT2.setBackgroundColor(colorResaltar);
        celdaT3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaT1);
        tabla.addCell(celdaT2);
        tabla.addCell(celdaT3);


        document.add(tabla);
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
