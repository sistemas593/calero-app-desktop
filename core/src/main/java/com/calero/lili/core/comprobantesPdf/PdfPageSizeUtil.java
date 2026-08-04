package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.enums.TipoPdfFactura;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;

/**
 * Resuelve el {@link Document} (tamaño de página + márgenes) que corresponde
 * a cada {@link TipoPdfFactura}.
 * <p>
 * Se centraliza aquí para que los distintos generadores de comprobantes
 * (FacturaPdf, y a futuro NotaCreditoPdf, NotaDebitoPdf, GuiaRemisionPdf,
 * LiquidacionCompraPdf, ComprobanteRetencionPdf, etc.) reutilicen la misma
 * lógica en vez de duplicar el switch de tamaño en cada clase.
 * <p>
 * Por ahora solo soporta A4 (por defecto) y A5, ya que TICKET_58 / TICKET_80
 * requieren un layout distinto (una sola columna, alto variable) y no
 * simplemente un cambio de tamaño de hoja.
 */
public final class PdfPageSizeUtil {

    private PdfPageSizeUtil() {
    }

    public static Document crearDocumento(TipoPdfFactura tipo) {
        Document document = new Document(resolverPageSize(tipo));
        document.setMargins(15, 15, 10, 10);
        return document;
    }

    private static com.itextpdf.text.Rectangle resolverPageSize(TipoPdfFactura tipo) {
        if (tipo == TipoPdfFactura.A5) {
            return PageSize.A5;
        }
        // A4 es el valor por defecto (y el único otro caso soportado por ahora).
        return PageSize.A4;
    }
}
