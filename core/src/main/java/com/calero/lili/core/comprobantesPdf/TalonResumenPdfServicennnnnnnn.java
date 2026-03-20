package com.calero.lili.core.comprobantesPdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service

public class TalonResumenPdfServicennnnnnnn {

        public void generarPDF(String rutaArchivo) throws Exception {
            Document document = new Document(PageSize.A4, 36, 36, 60, 36);
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();

            // Fuentes
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font subTituloFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

            // Título
            Paragraph titulo = new Paragraph("TALÓN RESUMEN", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("SERVICIO DE RENTAS INTERNAS", subTituloFont));
            document.add(new Paragraph("ANEXO TRANSACCIONAL", subTituloFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("RUC: 1212121212001", normalFont));
            document.add(new Paragraph("Periodo: 01-2025", normalFont));
            document.add(new Paragraph("Fecha de Generación: 26/06/2025 15:07:33", normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Certifico que la información contenida en el medio magnético del Anexo Transaccional para el período 01-2025, es fiel reflejo del siguiente reporte:", normalFont));
            document.add(Chunk.NEWLINE);

            // Tabla COMPRAS
            document.add(new Paragraph("COMPRAS", subTituloFont));
            PdfPTable tablaCompras = new PdfPTable(6);
            tablaCompras.setWidthPercentage(100);
            tablaCompras.setSpacingBefore(5f);
            tablaCompras.setSpacingAfter(5f);

            agregarCeldaHeader(tablaCompras, "Cod. Transacción");
            agregarCeldaHeader(tablaCompras, "No. Registros");
            agregarCeldaHeader(tablaCompras, "BI tarifa 0%");
            agregarCeldaHeader(tablaCompras, "BI tarifa diferente 0%");
            agregarCeldaHeader(tablaCompras, "BI No Objeto IVA");
            agregarCeldaHeader(tablaCompras, "Valor IVA");

            tablaCompras.addCell("01 FACTURA");
            tablaCompras.addCell("1");
            tablaCompras.addCell("100.00");
            tablaCompras.addCell("0.00");
            tablaCompras.addCell("0.00");
            tablaCompras.addCell("0.00");

            document.add(tablaCompras);
            document.add(new Paragraph("TOTAL: 100.00 0.00 0.00 0.00", normalFont));
            document.add(Chunk.NEWLINE);

            // Tabla RETENCION
            document.add(new Paragraph("RETENCION EN LA FUENTE DE IVA", subTituloFont));
            PdfPTable tablaRetencion = new PdfPTable(3);
            tablaRetencion.setWidthPercentage(100);
            tablaRetencion.setSpacingBefore(5f);
            tablaRetencion.setSpacingAfter(5f);

            agregarCeldaHeader(tablaRetencion, "Operación");
            agregarCeldaHeader(tablaRetencion, "Concepto de Retención");
            agregarCeldaHeader(tablaRetencion, "Valor Retenido");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA 10%");
            tablaRetencion.addCell("0.00");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA 20%");
            tablaRetencion.addCell("0.00");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA 30%");
            tablaRetencion.addCell("0.00");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA 50%");
            tablaRetencion.addCell("0.00");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA 70%");
            tablaRetencion.addCell("0.00");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA 100%");
            tablaRetencion.addCell("0.00");

            tablaRetencion.addCell("COMPRA");
            tablaRetencion.addCell("Retencion IVA NC");
            tablaRetencion.addCell("0.00");

            document.add(tablaRetencion);
            document.add(new Paragraph("TOTAL: 0.00", normalFont));
            document.add(Chunk.NEWLINE);

            // Declaración final
            document.add(new Paragraph("Declaro que los datos contenidos en este anexo son verdaderos, por lo que asumo la responsabilidad correspondiente, de acuerdo a lo establecido en el Art. 101 de la Codificación de la Ley de Régimen Tributario Interno", normalFont));
            document.add(Chunk.NEWLINE);

            // Firmas
            PdfPTable tablaFirmas = new PdfPTable(2);
            tablaFirmas.setWidthPercentage(100);
            tablaFirmas.setSpacingBefore(20f);
            tablaFirmas.addCell(crearCeldaFirma("Firma del Contador"));
            tablaFirmas.addCell(crearCeldaFirma("Firma del Representante Legal"));
            document.add(tablaFirmas);

            document.close();
        }

        private void agregarCeldaHeader(PdfPTable table, String texto) {
            PdfPCell cell = new PdfPCell(new Phrase(texto, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        private PdfPCell crearCeldaFirma(String texto) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setFixedHeight(50);
            Paragraph p = new Paragraph("_____________________________________\n" + texto, new Font(Font.FontFamily.HELVETICA, 10));
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            return cell;
        }

}
