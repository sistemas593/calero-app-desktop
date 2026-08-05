package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.factura.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoFactura;
import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.core.enums.FormaPagoSriEnum;
import com.calero.lili.core.enums.TipoDocumentoPdf;
import com.calero.lili.core.enums.TipoPdfFactura;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

/**
 * Genera el PDF de la factura en formato ticket para impresoras térmicas de
 * 58mm y 80mm ({@link TipoPdfFactura#TICKET_58} / {@link TipoPdfFactura#TICKET_80}).
 * <p>
 * El formato sigue el modelo de un ticket real impreso (texto plano en
 * fuente monoespaciada, columnas alineadas a mano, separadores hechos con
 * "=", sin logo ni código de barras): encabezado de la empresa, datos del
 * documento y del cliente, detalle en una sola línea por ítem
 * (cant/producto/P.U./P.Total), desglose de subtotales e IVA, total y forma
 * de pago. TICKET_58 y TICKET_80 comparten exactamente la misma lógica; solo
 * cambia el ancho de página y, por lo tanto, cuántos caracteres entran por
 * línea.
 * <p>
 * Como iText necesita el alto de página al crear el {@link Document}, se
 * arma primero con un alto "provisional" generoso y, una vez escrito todo el
 * contenido, se recorta el PDF final al alto realmente ocupado (ver
 * {@link #recortarAlto(byte[], float, float)}).
 */
@Service
public class TicktesFacturaServicePdf {

    private static final float MM_A_PT = 2.83465f;
    private static final float MARGEN = 6f;
    private static final float TAMANO_FUENTE = 7f;

    public byte[] generarPdf(Factura factura, String autorizacionSri, String fechaAutorizacion, byte[] imageBytes, TipoPdfFactura tipo) {

        try {
            float ancho = resolverAncho(tipo);
            float altoIntento = calcularAltoProvisional(factura);

            // El alto "provisional" es solo una estimación de partida: si algún texto
            // envuelve más líneas de las previstas (nombres largos, direcciones, etc.)
            // el contenido no entra en una sola página y iText agrega una segunda
            // página, lo que rompe el recorte final (que solo ajusta la página 1).
            // Por eso se valida cuántas páginas resultaron y, si fue más de una, se
            // duplica el alto y se vuelve a generar — como de todas formas se recorta
            // al final, no cuesta nada partir de un alto generoso.
            ResultadoGeneracion resultado = construirDocumento(factura, ancho, altoIntento);
            int intentos = 0;
            while (resultado.numeroDePaginas() > 1 && intentos < 4) {
                altoIntento *= 2f;
                resultado = construirDocumento(factura, ancho, altoIntento);
                intentos++;
            }

            return recortarAlto(resultado.bytes(), ancho, resultado.posicionFinalContenido());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultadoGeneracion construirDocumento(Factura factura, float ancho, float altoIntento) throws Exception {

        float anchoUtil = ancho - (2 * MARGEN);

        Rectangle pageSize = new Rectangle(ancho, altoIntento);
        Document document = new Document(pageSize, MARGEN, MARGEN, MARGEN, MARGEN);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        // Fuente monoespaciada: permite alinear columnas (CANT/PRODUCTO/P.U./P.TOT)
        // a mano con espacios, igual que en un ticket impreso por una impresora térmica.
        Font fuenteEmpresa = new Font(Font.FontFamily.COURIER, TAMANO_FUENTE + 1, Font.BOLD);
        Font fuenteNormal = new Font(Font.FontFamily.COURIER, TAMANO_FUENTE, Font.NORMAL);
        Font fuenteTotal = new Font(Font.FontFamily.COURIER, TAMANO_FUENTE, Font.BOLD);

        int caracteresPorLinea = caracteresPorLinea(anchoUtil, TAMANO_FUENTE);

        agregarEncabezado(document, factura, fuenteEmpresa, fuenteNormal);
        agregarDivisor(document, fuenteNormal, caracteresPorLinea);
        agregarDetalle(document, factura, fuenteNormal, caracteresPorLinea);
        agregarDivisor(document, fuenteNormal, caracteresPorLinea);
        agregarTotales(document, factura, fuenteNormal, fuenteTotal, caracteresPorLinea);
        agregarFormasPago(document, factura, fuenteNormal, caracteresPorLinea);
        agregarPie(document, fuenteNormal);

        float posicionFinalContenido = pdfWriter.getVerticalPosition(false);
        int numeroDePaginas = pdfWriter.getPageNumber();
        document.close();

        return new ResultadoGeneracion(byteArrayOutputStream.toByteArray(), numeroDePaginas, posicionFinalContenido);
    }

    private record ResultadoGeneracion(byte[] bytes, int numeroDePaginas, float posicionFinalContenido) {
    }

    private float resolverAncho(TipoPdfFactura tipo) {
        if (tipo == TipoPdfFactura.TICKET_80) {
            return 80f * MM_A_PT;
        }
        // TICKET_58 es el valor por defecto de este servicio.
        return 58f * MM_A_PT;
    }

    private float calcularAltoProvisional(Factura factura) {
        int cantidadItems = factura.getDetalle() != null ? factura.getDetalle().size() : 0;
        int cantidadPagos = (factura.getInfoFactura() != null && factura.getInfoFactura().getPago() != null)
                ? factura.getInfoFactura().getPago().size() : 0;

        // Alto "de sobra": encabezado + detalle + totales + pagos + pie. Es solo un
        // punto de partida (el que realmente evita una segunda página es el
        // reintento en generarPdf); al final se recorta al alto real ocupado
        // (ver recortarAlto).
        return 450f + (cantidadItems * 20f) + (cantidadPagos * 15f);
    }

    /**
     * Cuántos caracteres de la fuente monoespaciada entran en el ancho útil de
     * la página. En Courier cada carácter mide aprox. 0.6 * tamaño de fuente,
     * sin importar cuál sea (a diferencia de Helvetica, donde el ancho varía
     * según la letra) — por eso se puede alinear "a mano" con espacios.
     */
    private int caracteresPorLinea(float anchoUtil, float tamanoFuente) {
        float anchoCaracter = tamanoFuente * 0.6f;
        return Math.max(20, (int) Math.floor(anchoUtil / anchoCaracter));
    }

    private void agregarEncabezado(Document document, Factura factura, Font fuenteEmpresa, Font fuenteNormal) throws DocumentException {

        Paragraph empresa = new Paragraph(nombreEmpresa(factura).toUpperCase(), fuenteEmpresa);
        empresa.setAlignment(Element.ALIGN_CENTER);
        document.add(empresa);

        Paragraph direccion = new Paragraph(Objects.toString(factura.getInfoTributaria().getDirMatriz(), ""), fuenteNormal);
        direccion.setAlignment(Element.ALIGN_CENTER);
        document.add(direccion);

        document.add(new Paragraph("RUC NRO : " + Objects.toString(factura.getInfoTributaria().getRuc(), ""), fuenteNormal));
        document.add(new Paragraph("OBLIGADO A LLEVAR CONTABILIDAD: " +
                Objects.toString(factura.getInfoFactura().getObligadoContabilidad(), ""), fuenteNormal));
        document.add(new Paragraph("AGENTE DE RETENCION   RESOLUCION No.: " +
                Objects.toString(factura.getInfoTributaria().getAgenteRetencion(), ""), fuenteNormal));

        document.add(new Paragraph(" ", fuenteNormal));

        TipoDocumentoPdf tipoDocumento = TipoDocumentoPdf.getTipoDocumento(factura.getInfoTributaria().getCodDoc());
        document.add(new Paragraph("NO. " + tipoDocumento.getNombre() + "   " +
                Objects.toString(factura.getInfoTributaria().getEstab(), "") + "   " +
                Objects.toString(factura.getInfoTributaria().getPtoEmi(), "") + "   " +
                Objects.toString(factura.getInfoTributaria().getSecuencial(), ""), fuenteNormal));

        // La "clave de acceso" es, una vez autorizado el comprobante, el mismo
        // número de autorización del SRI; por eso el ticket no repite un campo
        // aparte de autorización (a diferencia del PDF A4/A5).
        document.add(new Paragraph("CLAVE DE ACCESO :   " +
                Objects.toString(factura.getInfoTributaria().getClaveAcceso(), ""), fuenteNormal));

        String ambienteTexto = "1".equals(factura.getInfoTributaria().getAmbiente()) ? "PRUEBAS" : "PRODUCCION";
        String emisionTexto = "1".equals(factura.getInfoTributaria().getTipoEmision()) ? "NORMAL" : "";
        document.add(new Paragraph("AMBIENTE: " + ambienteTexto + "   EMISION: " + emisionTexto, fuenteNormal));

        document.add(new Paragraph("FECHA :   " + Objects.toString(factura.getInfoFactura().getFechaEmision(), ""), fuenteNormal));
        document.add(new Paragraph("CLIENTE :   " + Objects.toString(factura.getInfoFactura().getRazonSocialComprador(), ""), fuenteNormal));
        document.add(new Paragraph("RUC / CI :   " + Objects.toString(factura.getInfoFactura().getIdentificacionComprador(), ""), fuenteNormal));
        document.add(new Paragraph("DIRECCION : " + Objects.toString(factura.getInfoFactura().getDireccionComprador(), ""), fuenteNormal));

        document.add(new Paragraph(" ", fuenteNormal));
    }

    private String nombreEmpresa(Factura factura) {
        String nombreComercial = factura.getInfoTributaria().getNombreComercial();
        if (nombreComercial != null && !nombreComercial.isBlank()) {
            return nombreComercial;
        }
        return Objects.toString(factura.getInfoTributaria().getRazonSocial(), "");
    }

    private void agregarDetalle(Document document, Factura factura, Font fuenteNormal, int caracteresPorLinea) throws DocumentException {

        int colCantidad = 6;
        int colPrecioUnitario = 8;
        int colPrecioTotal = 9;
        int colDescripcion = Math.max(8, caracteresPorLinea - colCantidad - colPrecioUnitario - colPrecioTotal);

        document.add(new Paragraph(
                pad("CANT", colCantidad) + pad("PRODUCTO", colDescripcion) +
                        padIzquierda("P.U", colPrecioUnitario) + padIzquierda("P.TOT", colPrecioTotal),
                fuenteNormal));

        List<Detalle> detalles = factura.getDetalle();
        if (detalles == null) {
            return;
        }

        for (Detalle detalle : detalles) {
            String linea = pad(formatoMonto(detalle.getCantidad()), colCantidad) +
                    pad(Objects.toString(detalle.getDescripcion(), "").toUpperCase(), colDescripcion) +
                    padIzquierda(formatoMonto(detalle.getPrecioUnitario()), colPrecioUnitario) +
                    padIzquierda(formatoMonto(detalle.getPrecioTotalSinImpuesto()), colPrecioTotal);
            document.add(new Paragraph(linea, fuenteNormal));
        }
    }

    private void agregarTotales(Document document, Factura factura, Font fuenteNormal, Font fuenteTotal, int caracteresPorLinea) throws DocumentException {

        InfoFactura infoFactura = factura.getInfoFactura();
        FacturaImpuestosUtil.SubtotalesFactura subtotales = FacturaImpuestosUtil.resolverSubtotales(infoFactura.getTotalImpuesto());

        if (subtotales.getSubTotal15() != null) {
            agregarLineaTotal(document, "SUBTOTAL 15%:", subtotales.getSubTotal15().getBaseImponible(), fuenteNormal, caracteresPorLinea);
        }
        if (subtotales.getSubTotal0() != null) {
            agregarLineaTotal(document, "SUBTOTAL 0%:", subtotales.getSubTotal0().getBaseImponible(), fuenteNormal, caracteresPorLinea);
        }
        if (subtotales.getSubTotal8() != null) {
            agregarLineaTotal(document, "SUBTOTAL 8%:", subtotales.getSubTotal8().getBaseImponible(), fuenteNormal, caracteresPorLinea);
        }
        if (subtotales.getSubTotal5() != null) {
            agregarLineaTotal(document, "SUBTOTAL 5%:", subtotales.getSubTotal5().getBaseImponible(), fuenteNormal, caracteresPorLinea);
        }

        agregarLineaTotal(document, "DESCUENTO:", infoFactura.getTotalDescuento(), fuenteNormal, caracteresPorLinea);
        agregarLineaTotal(document, "SUBTOTAL:", infoFactura.getTotalSinImpuestos(), fuenteNormal, caracteresPorLinea);

        if (subtotales.getSubTotal15() != null) {
            agregarLineaTotal(document, "IVA 15%:", subtotales.getSubTotal15().getValor(), fuenteNormal, caracteresPorLinea);
        }
        if (subtotales.getSubTotal8() != null) {
            agregarLineaTotal(document, "IVA 8%:", subtotales.getSubTotal8().getValor(), fuenteNormal, caracteresPorLinea);
        }
        if (subtotales.getSubTotal5() != null) {
            agregarLineaTotal(document, "IVA 5%:", subtotales.getSubTotal5().getValor(), fuenteNormal, caracteresPorLinea);
        }

        agregarDivisor(document, fuenteNormal, caracteresPorLinea);
        agregarLineaTotal(document, "TOTAL:", infoFactura.getImporteTotal(), fuenteTotal, caracteresPorLinea);
    }

    private void agregarLineaTotal(Document document, String etiqueta, String valor, Font font, int caracteresPorLinea) throws DocumentException {
        int anchoValor = 9;
        int anchoEtiqueta = Math.max(8, caracteresPorLinea - anchoValor);
        String linea = pad(etiqueta, anchoEtiqueta) + padIzquierda(formatoMonto(valor), anchoValor);
        document.add(new Paragraph(linea, font));
    }

    private void agregarFormasPago(Document document, Factura factura, Font fuenteNormal, int caracteresPorLinea) throws DocumentException {
        List<Pago> pagos = factura.getInfoFactura().getPago();
        if (pagos == null || pagos.isEmpty()) {
            return;
        }

        for (Pago pago : pagos) {
            String nombrePago;
            try {
                nombrePago = FormaPagoSriEnum.getNombrePago(pago.getFormaPago());
            } catch (Exception e) {
                nombrePago = pago.getFormaPago() == null ? "" : pago.getFormaPago().toUpperCase();
            }
            agregarLineaTotal(document, nombrePago + ":", pago.getTotal(), fuenteNormal, caracteresPorLinea);
        }
    }

    private void agregarPie(Document document, Font font) throws DocumentException {
        document.add(new Paragraph(" ", font));

        Paragraph aviso = new Paragraph("PARA CONSULTAR SU FACTURA INGRESE A WWW.SRI.GOB.EC", font);
        aviso.setAlignment(Element.ALIGN_CENTER);
        document.add(aviso);

        Paragraph gracias = new Paragraph("GRACIAS POR SU COMPRA!", font);
        gracias.setAlignment(Element.ALIGN_CENTER);
        gracias.setSpacingBefore(4f);
        document.add(gracias);
    }

    private void agregarDivisor(Document document, Font font, int caracteresPorLinea) throws DocumentException {
        document.add(new Paragraph("=".repeat(caracteresPorLinea), font));
    }

    /**
     * Corta o rellena {@code texto} a la derecha hasta {@code ancho}
     * caracteres, para que quede a la izquierda de la columna (etiquetas,
     * descripciones).
     */
    private String pad(String texto, int ancho) {
        String valor = Objects.toString(texto, "");
        if (valor.length() >= ancho) {
            return valor.substring(0, ancho);
        }
        return valor + " ".repeat(ancho - valor.length());
    }

    /**
     * Rellena {@code texto} a la izquierda hasta {@code ancho} caracteres,
     * para que quede alineado a la derecha de la columna (montos).
     */
    private String padIzquierda(String texto, int ancho) {
        String valor = Objects.toString(texto, "");
        if (valor.length() >= ancho) {
            return valor.substring(valor.length() - ancho);
        }
        return " ".repeat(ancho - valor.length()) + valor;
    }

    /**
     * Los montos vienen del XML con punto decimal ("200.00"); para que el
     * ticket se vea igual que uno impreso localmente se muestran con coma
     * ("200,00").
     */
    private String formatoMonto(String valor) {
        if (valor == null || valor.isBlank()) {
            return "0,00";
        }
        return valor.replace('.', ',');
    }

    /**
     * El {@link Document} se crea con un alto provisional generoso (ver
     * {@link #calcularAltoProvisional(Factura)}) porque iText necesita saber
     * el tamaño de página al momento de abrirlo. Una vez escrito todo el
     * contenido, se recorta el PDF final al alto realmente usado: se toma la
     * posición vertical donde terminó de escribirse el contenido
     * ({@code posicionFinalContenido}, ya en coordenadas del PDF) y se
     * reduce el MediaBox de la página a esa altura + el margen inferior.
     */
    private byte[] recortarAlto(byte[] pdfBytes, float ancho, float posicionFinalContenido) {
        try {
            PdfReader reader = new PdfReader(pdfBytes);

            float nuevoLimiteInferior = Math.max(posicionFinalContenido - MARGEN, 0f);
            Rectangle tamanoOriginal = reader.getPageSize(1);

            Rectangle nuevoTamano = new Rectangle(0, nuevoLimiteInferior, ancho, tamanoOriginal.getHeight());

            PdfDictionary pagina = reader.getPageN(1);
            pagina.put(PdfName.MEDIABOX, new PdfArray(new float[]{
                    nuevoTamano.getLeft(), nuevoTamano.getBottom(), nuevoTamano.getRight(), nuevoTamano.getTop()
            }));

            ByteArrayOutputStream salidaRecortada = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, salidaRecortada);
            stamper.close();
            reader.close();

            return salidaRecortada.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            // Si algo falla al recortar, se devuelve el PDF sin recortar
            // (con la altura provisional) en vez de perder el documento.
            return pdfBytes;
        }
    }
}
