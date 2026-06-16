package com.calero.lili.core.modImpuestosAnexos.formulario107;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modImpuestosAnexos.CeldaPdfServiceImpl;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.validaciones.ValidarValoresComprobantesPdf;
import com.lowagie.text.Document;
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
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class GenerarFormulario107PdfServiceImpl {

    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;
    private final CeldaPdfServiceImpl celdaService;

    private final Color colorResaltar = new Color(255, 253, 208);
    private final Color colorEncabezadosTablas = new Color(0, 85, 237);


    public void generarFormulario107(ImpuestosF107Dto model, HttpServletResponse response) {

        try {

            response.setContentType("application/pdf");
            String filename = model.getIdRet() + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 7, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font titleFontTb = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 5, com.lowagie.text.Font.BOLD, Color.WHITE);
            com.lowagie.text.Font normalFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 5);
            com.lowagie.text.Font smallBold = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 5, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font small = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 5);


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


    private void agregarEncabezadoFormulario107(Document document, com.lowagie.text.Font titleFont, com.lowagie.text.Font normalFont) {
        Paragraph title = new Paragraph("Formulario 107\nComprobante de retenciones en la fuente del impuestos la renta\npor ingresos del trabajo en relacion de dependencia", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);
    }

    private void agregarTablaEncabezadoFormulario107(Document document, com.lowagie.text.Font bold, com.lowagie.text.Font normal, com.lowagie.text.Font titleTb, ImpuestosF107Dto model) {

        PdfPTable tablaEncabezado = new PdfPTable(new float[]{50, 25, 25, 25});
        tablaEncabezado.setWidthPercentage(100);
        tablaEncabezado.setSpacingBefore(5f);

        PdfPCell celdaE1 = celdaService.crearCelda("Período Fiscal", normal);
        PdfPCell celdaE2 = celdaService.crearCelda(model.getAno(), normal);
        PdfPCell celdaE3 = celdaService.crearCelda("Fecha de entrega", normal);
        PdfPCell celdaE4 = celdaService.crearCelda(DateUtils.toString(LocalDate.now()), normal);

        celdaE2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE3.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaE4.setHorizontalAlignment(Element.ALIGN_CENTER);

        tablaEncabezado.addCell(celdaE1);
        tablaEncabezado.addCell(celdaE2);
        tablaEncabezado.addCell(celdaE3);
        tablaEncabezado.addCell(celdaE4);


        PdfPCell celdaIE1 = celdaService.crearCelda("Identificación del Empleador (Agente de Retención)", titleTb);
        PdfPCell celdaIE2 = celdaService.crearCelda("", normal);
        PdfPCell celdaIE3 = celdaService.crearCelda("", normal);
        PdfPCell celdaIE4 = celdaService.crearCelda("", normal);

        celdaIE1.setBackgroundColor(colorEncabezadosTablas);
        celdaIE2.setBackgroundColor(colorEncabezadosTablas);
        celdaIE3.setBackgroundColor(colorEncabezadosTablas);
        celdaIE4.setBackgroundColor(colorEncabezadosTablas);

        tablaEncabezado.addCell(celdaIE1);
        tablaEncabezado.addCell(celdaIE2);
        tablaEncabezado.addCell(celdaIE3);
        tablaEncabezado.addCell(celdaIE4);

        tablaEncabezado.addCell(celdaService.crearCelda("RUC", normal));
        tablaEncabezado.addCell(celdaService.crearCelda(model.getRuc(), normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));


        tablaEncabezado.addCell(celdaService.crearCelda("Razón social o apellidos y nombres completos", normal));
        tablaEncabezado.addCell(celdaService.crearCelda(model.getRazonSocial(), normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));


        PdfPCell celdaIT1 = celdaService.crearCelda("Identificación del Trabajador (Contribuyente)", titleTb);
        PdfPCell celdaIT2 = celdaService.crearCelda("", normal);
        PdfPCell celdaIT3 = celdaService.crearCelda("", normal);
        PdfPCell celdaIT4 = celdaService.crearCelda("", normal);


        celdaIT1.setBackgroundColor(colorEncabezadosTablas);
        celdaIT2.setBackgroundColor(colorEncabezadosTablas);
        celdaIT3.setBackgroundColor(colorEncabezadosTablas);
        celdaIT4.setBackgroundColor(colorEncabezadosTablas);

        tablaEncabezado.addCell(celdaIT1);
        tablaEncabezado.addCell(celdaIT2);
        tablaEncabezado.addCell(celdaIT3);
        tablaEncabezado.addCell(celdaIT4);

        tablaEncabezado.addCell(celdaService.crearCelda("Apellidos y nombres completos ", normal));
        tablaEncabezado.addCell(celdaService.crearCelda(model.getApellidoTrab() + " " + model.getNombreTrab(), normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));


        tablaEncabezado.addCell(celdaService.crearCelda("Cédula o Pasaporte", normal));
        tablaEncabezado.addCell(celdaService.crearCelda(model.getIdRet(), normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));
        tablaEncabezado.addCell(celdaService.crearCelda("", normal));

        document.add(tablaEncabezado);
    }

    private void agregarTablaLiqImpuestoFormulario107(Document document, com.lowagie.text.Font bold, com.lowagie.text.Font normal, com.lowagie.text.Font titleTb, ImpuestosF107Dto model) {

        PdfPTable tabla = new PdfPTable(new float[]{60, 20, 20});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5f);

        PdfPCell celdaLQI1 = celdaService.crearCelda("Liquidación del Impuesto", titleTb);
        PdfPCell celdaLQI2 = celdaService.crearCelda("", normal);
        PdfPCell celdaLQI3 = celdaService.crearCelda("", normal);

        celdaLQI1.setBackgroundColor(colorEncabezadosTablas);
        celdaLQI2.setBackgroundColor(colorEncabezadosTablas);
        celdaLQI3.setBackgroundColor(colorEncabezadosTablas);

        tabla.addCell(celdaLQI1);
        tabla.addCell(celdaLQI2);
        tabla.addCell(celdaLQI3);

        // 301+303+305+381
        BigDecimal valorGravadoEsteEmpleador = model.getSuelSal().add(model.getSobSuelComRemu())
                .add(model.getOtrosIngRenNoGrav()).add(model.getPartUtil()).add(model.getValImpAsuEsteEmpl());

        tabla.addCell(celdaService.crearCelda("SUELDOS, SALARIOS Y OTROS INGRESOS GRAVADOS DE IMPUESTO A LA RENTA (MATERIA GRAVADA DE LA SEGURIDAD SOCIAL)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("301", "+", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getSuelSal().add(model.getSobSuelComRemu())), normal));

        tabla.addCell(celdaService.crearCelda("OTROS INGRESOS GRAVADOS DE IMPUESTO A LA RENTA (MATERIA NO GRAVADA DE LA SEGURIDAD SOCIAL)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("303", "+", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getOtrosIngRenNoGrav()), normal));

        tabla.addCell(celdaService.crearCelda("PARTICIPACIÓN UTILIDADES", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("305", "+", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getPartUtil()), normal));

        tabla.addCell(celdaService.crearCelda("INGRESOS GRAVADOS GENERADOS CON OTROS EMPLEADORES\n", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("307", "+", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getIntGrabGen()), normal));

        tabla.addCell(celdaService.crearCelda("DÉCIMO TERCER SUELDO", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("311", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDecimTer()), normal));

        tabla.addCell(celdaService.crearCelda("DÉCIMO CUARTO SUELDO", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("313", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDecimCuar()), normal));

        tabla.addCell(celdaService.crearCelda("FONDO DE RESERVA", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("315", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getFondoReserva()), normal));

        tabla.addCell(celdaService.crearCelda("OTROS INGRESOS EN RELACIÓN DE DEPENDENCIA QUE NO CONSTITUYEN MATERIA GRAVADA DE IMPUESTO A LA RENTA", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("317", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getOtrosIngRenGrav()), normal));

        tabla.addCell(celdaService.crearCelda("(-) APORTE PERSONAL A LA SEGURIDAD SOCIAL CON ESTE EMPLEADOR (únicamente pagado por el trabajador), APORTES PERSONALES A LAS CAJAS MILITAR\n" +
                "O POLICIAL PARA FINES DE RETIRO O CESANTÍA", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("351", "-", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getApoPerIess()), normal));

        tabla.addCell(celdaService.crearCelda("(-) APORTE PERSONAL A LA SEGURIDAD SOCIAL CON OTROS EMPLEADORES (únicamente pagado por el trabajador)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("353", "-", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getAporPerIessConOtrosEmpls()), normal));

        tabla.addCell(celdaService.crearCelda("GASTOS PERSONALES - VIVIENDA (Informativo)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("361", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducVivienda()), normal));

        tabla.addCell(celdaService.crearCelda("GASTOS PERSONALES - TURISMO (Informativo)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("362", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeduccionTurismo()), normal));

        tabla.addCell(celdaService.crearCelda("GASTOS PERSONALES - SALUD (Informativo)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("363", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducSalud()), normal));

        tabla.addCell(celdaService.crearCelda("GASTOS PERSONALES - EDUCACIÓN - ARTE Y CULTURA (Informativo)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("365", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducEducartcult()), normal));

        tabla.addCell(celdaService.crearCelda("GASTOS PERSONALES - ALIMENTACIÓN (Informativo)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("367", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducAliement()), normal));

        tabla.addCell(celdaService.crearCelda("GASTOS PERSONALES - VESTIMENTA (Informativo)", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("369", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getDeducVestim()), normal));

        tabla.addCell(celdaService.crearCelda("(-) EXONERACIÓN POR DISCAPACIDAD", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("371", "-", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getExoDiscap()), normal));

        tabla.addCell(celdaService.crearCelda("(-) EXONERACIÓN POR TERCERA EDAD", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("373", "-", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getExoTerEd()), normal));

        tabla.addCell(celdaService.crearCelda("IMPUESTO A LA RENTA ASUMIDO POR ESTE EMPLEADOR", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("381", "+", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValImpAsuEsteEmpl()), normal));


        PdfPCell celdaBI1 = celdaService.crearCelda("BASE IMPONIBLE GRAVADA (301+303+305+307-351-353-361-363-365-367-369-371-373+381 mayor o igual a 0)", normal);
        PdfPCell celdaBI2 = celdaService.crearCeldaConSigno("399", "=", bold, bold);
        PdfPCell celdaBI3 = celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getBasImp()), normal);

        celdaBI1.setBackgroundColor(colorResaltar);
        celdaBI2.setBackgroundColor(colorResaltar);
        celdaBI3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaBI1);
        tabla.addCell(celdaBI2);
        tabla.addCell(celdaBI3);

        tabla.addCell(celdaService.crearCelda("IMPUESTO A LA RENTA CAUSADO", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("401", "=", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getImpRentCaus()), normal));

        tabla.addCell(celdaService.crearCelda("REBAJA POR GASTOS PERSONALES", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("402", "=", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getRebajaGastosPersonales()), normal));

        tabla.addCell(celdaService.crearCelda("IMPUESTO A LA RENTA DESPUÉS DE LA REBAJA", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("403", "=", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO), normal));


        tabla.addCell(celdaService.crearCelda("VALOR DEL IMPUESTO RETENIDO Y ASUMIDO POR OTROS EMPLEADORES DURANTE EL PERÍODO DECLARADO", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("404", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValRetAsuOtrosEmpls()), normal));


        tabla.addCell(celdaService.crearCelda("VALOR DEL IMPUESTO ASUMIDO POR ESTE EMPLEADO", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("405", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValImpAsuEsteEmpl()), normal));


        tabla.addCell(celdaService.crearCelda("VALOR DEL IMPUESTO RETENIDO AL TRABAJADOR POR ESTE EMPLEADOR", normal));
        tabla.addCell(celdaService.crearCeldaConSigno("407", "", bold, bold));
        tabla.addCell(celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(model.getValRet()), normal));


        PdfPCell celdaIGE1 = celdaService.crearCelda("INGRESOS GRAVADOS CON ESTE EMPLEADOR (informativo) 301+303+305+381 ", normal);
        PdfPCell celdaIGE2 = celdaService.crearCeldaConSigno("349", "=", bold, bold);
        PdfPCell celdaIGE3 = celdaService.crearCeldaADer(validarValoresComprobantesPdf.getValor(valorGravadoEsteEmpleador), normal);

        celdaIGE1.setBackgroundColor(colorResaltar);
        celdaIGE2.setBackgroundColor(colorResaltar);
        celdaIGE3.setBackgroundColor(colorResaltar);

        tabla.addCell(celdaIGE1);
        tabla.addCell(celdaIGE2);
        tabla.addCell(celdaIGE3);

        document.add(tabla);
    }

    private void agregarInstruccionesFormulario107(Document document, com.lowagie.text.Font bold, Font normal, ImpuestosF107Dto model) {


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

}
