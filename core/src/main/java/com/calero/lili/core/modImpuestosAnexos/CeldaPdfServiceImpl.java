package com.calero.lili.core.modImpuestosAnexos;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class CeldaPdfServiceImpl {

    public PdfPCell crearCelda(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        return cell;
    }

    public PdfPCell crearCeldaADer(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        return cell;
    }

    public PdfPCell crearCeldaConCodigo(String codigo, String valor, Font fontCodigo, Font fontValor) {
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

    public PdfPCell crearCeldaConCodigoResaltar(String codigo, String valor, Font fontCodigo, Font fontValor) {
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

    public PdfPCell crearCeldaConSigno(String codigo, String signo, Font fontCodigo, Font fontValor) {
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

}
