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
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
 * A diferencia de {@link FacturaPdf} (A4/A5, donde solo cambia el tamaño de
 * hoja pero el layout de tablas es el mismo), el ticket necesita un layout
 * propio: una sola columna angosta y alto variable según la cantidad de
 * ítems/pagos que tenga la factura. Por eso es un servicio aparte y no un
 * "case" más dentro de {@code FacturaPdf}.
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

    public byte[] generarPdf(Factura factura, String autorizacionSri, String fechaAutorizacion, byte[] imageBytes, TipoPdfFactura tipo) {

        try {
            float ancho = resolverAncho(tipo);
            float anchoUtil = ancho - (2 * MARGEN);
            float altoProvisional = calcularAltoProvisional(factura);

            Rectangle pageSize = new Rectangle(ancho, altoProvisional);
            Document document = new Document(pageSize, MARGEN, MARGEN, MARGEN, MARGEN);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Font fuenteEmpresa = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
            Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
            Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
            Font fuenteChica = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
            Font fuenteTotal = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

            agregarEncabezadoEmpresa(document, factura, imageBytes, fuenteEmpresa, fuenteChica);
            agregarLineaSeparadora(document, pdfWriter);

            agregarDatosDocumento(document, factura, autorizacionSri, fechaAutorizacion, fuenteTitulo, fuenteChica);
            agregarLineaSeparadora(document, pdfWriter);

            agregarDatosCliente(document, factura, fuenteChica);
            agregarLineaSeparadora(document, pdfWriter);

            agregarDetalle(document, factura, fuenteNormal, fuenteChica);
            agregarLineaSeparadora(document, pdfWriter);

            agregarTotales(document, factura, fuenteNormal, fuenteTotal);
            agregarFormasPago(document, factura, fuenteChica);
            agregarLineaSeparadora(document, pdfWriter);

            agregarClaveAcceso(document, pdfWriter, factura, anchoUtil, fuenteChica);
            agregarPie(document, fuenteChica);

            float posicionFinalContenido = pdfWriter.getVerticalPosition(false);
            document.close();

            return recortarAlto(byteArrayOutputStream.toByteArray(), ancho, posicionFinalContenido);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

        // Alto "de sobra": encabezado + datos doc + cliente + detalle + totales +
        // pagos + barcode + pie. Es solo un punto de partida, al final se recorta
        // al alto real ocupado (ver recortarAlto).
        return 320f + (cantidadItems * 26f) + (cantidadPagos * 14f);
    }

    private void agregarEncabezadoEmpresa(Document document, Factura factura, byte[] imageBytes,
                                           Font fuenteEmpresa, Font fuenteChica) throws DocumentException {

        if (imageBytes != null) {
            try {
                Image logo = Image.getInstance(imageBytes);
                logo.scaleToFit(document.getPageSize().getWidth() * 0.5f, 35f);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                // Si el logo no se puede leer, se continúa el ticket sin imagen.
            }
        }

        Paragraph razonSocial = new Paragraph(
                Objects.toString(factura.getInfoTributaria().getRazonSocial(), "").toUpperCase(), fuenteEmpresa);
        razonSocial.setAlignment(Element.ALIGN_CENTER);
        document.add(razonSocial);

        if (factura.getInfoTributaria().getNombreComercial() != null) {
            Paragraph nombreComercial = new Paragraph(factura.getInfoTributaria().getNombreComercial(), fuenteChica);
            nombreComercial.setAlignment(Element.ALIGN_CENTER);
            document.add(nombreComercial);
        }

        Paragraph direccion = new Paragraph(Objects.toString(factura.getInfoTributaria().getDirMatriz(), ""), fuenteChica);
        direccion.setAlignment(Element.ALIGN_CENTER);
        document.add(direccion);

        Paragraph ruc = new Paragraph("RUC: " + Objects.toString(factura.getInfoTributaria().getRuc(), ""), fuenteChica);
        ruc.setAlignment(Element.ALIGN_CENTER);
        document.add(ruc);
    }

    private void agregarDatosDocumento(Document document, Factura factura, String autorizacionSri,
                                        String fechaAutorizacion, Font fuenteTitulo, Font fuenteNormal) throws DocumentException {

        TipoDocumentoPdf tipoDocumento = TipoDocumentoPdf.getTipoDocumento(factura.getInfoTributaria().getCodDoc());

        Paragraph tipo = new Paragraph(tipoDocumento.getNombre(), fuenteTitulo);
        tipo.setAlignment(Element.ALIGN_CENTER);
        document.add(tipo);

        String numero = factura.getInfoTributaria().getEstab() + "-" + factura.getInfoTributaria().getPtoEmi()
                + "-" + factura.getInfoTributaria().getSecuencial();
        Paragraph numeroParrafo = new Paragraph("No: " + numero, fuenteNormal);
        numeroParrafo.setAlignment(Element.ALIGN_CENTER);
        document.add(numeroParrafo);

        Paragraph autorizacion = new Paragraph("Aut: " + Objects.toString(autorizacionSri, ""), fuenteNormal);
        autorizacion.setAlignment(Element.ALIGN_CENTER);
        document.add(autorizacion);

        Paragraph fecha = new Paragraph("Fecha aut: " + Objects.toString(fechaAutorizacion, ""), fuenteNormal);
        fecha.setAlignment(Element.ALIGN_CENTER);
        document.add(fecha);

        String ambiente = "1".equals(factura.getInfoTributaria().getAmbiente()) ? "PRUEBAS" : "PRODUCCIÓN";
        Paragraph ambienteParrafo = new Paragraph("Ambiente: " + ambiente, fuenteNormal);
        ambienteParrafo.setAlignment(Element.ALIGN_CENTER);
        document.add(ambienteParrafo);
    }

    private void agregarDatosCliente(Document document, Factura factura, Font font) throws DocumentException {
        InfoFactura infoFactura = factura.getInfoFactura();

        document.add(new Paragraph("Cliente: " + Objects.toString(infoFactura.getRazonSocialComprador(), ""), font));
        document.add(new Paragraph("Identificación: " + Objects.toString(infoFactura.getIdentificacionComprador(), ""), font));
        document.add(new Paragraph("Fecha: " + Objects.toString(infoFactura.getFechaEmision(), ""), font));
    }

    private void agregarDetalle(Document document, Factura factura, Font fuenteDescripcion, Font fuenteDato) throws DocumentException {

        Paragraph titulo = new Paragraph("DETALLE", fuenteDato);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        List<Detalle> detalles = factura.getDetalle();
        if (detalles == null) {
            return;
        }

        for (Detalle detalle : detalles) {

            Paragraph descripcion = new Paragraph(
                    Objects.toString(detalle.getDescripcion(), "").toUpperCase(), fuenteDescripcion);
            document.add(descripcion);

            PdfPTable filaCantidadPrecio = new PdfPTable(2);
            filaCantidadPrecio.setWidthPercentage(100);
            filaCantidadPrecio.setWidths(new float[]{6, 4});

            String cantidadPrecio = Objects.toString(detalle.getCantidad(), "0") + " x " +
                    Objects.toString(detalle.getPrecioUnitario(), "0.00");
            PdfPCell celdaCantidad = new PdfPCell(new Phrase(cantidadPrecio, fuenteDato));
            celdaCantidad.setBorder(Rectangle.NO_BORDER);
            celdaCantidad.setPaddingBottom(3f);
            filaCantidadPrecio.addCell(celdaCantidad);

            PdfPCell celdaTotal = new PdfPCell(new Phrase(
                    Objects.toString(detalle.getPrecioTotalSinImpuesto(), "0.00"), fuenteDato));
            celdaTotal.setBorder(Rectangle.NO_BORDER);
            celdaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaTotal.setPaddingBottom(3f);
            filaCantidadPrecio.addCell(celdaTotal);

            document.add(filaCantidadPrecio);
        }
    }

    private void agregarTotales(Document document, Factura factura, Font fuenteEtiqueta, Font fuenteTotal) throws DocumentException {

        InfoFactura infoFactura = factura.getInfoFactura();
        FacturaImpuestosUtil.SubtotalesFactura subtotales =
                FacturaImpuestosUtil.resolverSubtotales(infoFactura.getTotalImpuesto());

        agregarFilaTotal(document, "SUBTOTAL SIN IMPUESTOS:", infoFactura.getTotalSinImpuestos(), fuenteEtiqueta);
        agregarFilaTotal(document, "TOTAL DESCUENTO:", infoFactura.getTotalDescuento(), fuenteEtiqueta);

        if (subtotales.getSubTotal0() != null) {
            agregarFilaTotal(document, "SUBTOTAL 0%:", subtotales.getSubTotal0().getBaseImponible(), fuenteEtiqueta);
        }

        if (subtotales.getSubTotal15() != null) {
            agregarFilaTotal(document, "SUBTOTAL 15%:", subtotales.getSubTotal15().getBaseImponible(), fuenteEtiqueta);
            agregarFilaTotal(document, "IVA 15%:", subtotales.getSubTotal15().getValor(), fuenteEtiqueta);
        }

        if (subtotales.getSubTotal8() != null) {
            agregarFilaTotal(document, "SUBTOTAL 8%:", subtotales.getSubTotal8().getBaseImponible(), fuenteEtiqueta);
            agregarFilaTotal(document, "IVA 8%:", subtotales.getSubTotal8().getValor(), fuenteEtiqueta);
        }

        if (subtotales.getSubTotal5() != null) {
            agregarFilaTotal(document, "SUBTOTAL 5%:", subtotales.getSubTotal5().getBaseImponible(), fuenteEtiqueta);
            agregarFilaTotal(document, "IVA 5%:", subtotales.getSubTotal5().getValor(), fuenteEtiqueta);
        }

        if (subtotales.getSubTotalNoObjeto() != null) {
            agregarFilaTotal(document, "NO OBJETO DE IVA:", subtotales.getSubTotalNoObjeto().getBaseImponible(), fuenteEtiqueta);
        }

        if (subtotales.getSubTotalExenta() != null) {
            agregarFilaTotal(document, "EXENTO DE IVA:", subtotales.getSubTotalExenta().getBaseImponible(), fuenteEtiqueta);
        }

        agregarFilaTotal(document, "VALOR TOTAL:", infoFactura.getImporteTotal(), fuenteTotal);
    }

    private void agregarFilaTotal(Document document, String etiqueta, String valor, Font font) throws DocumentException {
        PdfPTable fila = new PdfPTable(2);
        fila.setWidthPercentage(100);
        fila.setWidths(new float[]{6, 4});

        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, font));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        fila.addCell(celdaEtiqueta);

        PdfPCell celdaValor = new PdfPCell(new Phrase(Objects.toString(valor, "0.00"), font));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        fila.addCell(celdaValor);

        document.add(fila);
    }

    private void agregarFormasPago(Document document, Factura factura, Font font) throws DocumentException {
        List<Pago> pagos = factura.getInfoFactura().getPago();
        if (pagos == null || pagos.isEmpty()) {
            return;
        }

        Paragraph titulo = new Paragraph("FORMA DE PAGO", font);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        for (Pago pago : pagos) {
            String nombrePago;
            try {
                nombrePago = pago.getFormaPago() + "-" + FormaPagoSriEnum.getNombrePago(pago.getFormaPago());
            } catch (Exception e) {
                nombrePago = pago.getFormaPago() == null ? "" : pago.getFormaPago().toUpperCase();
            }

            PdfPTable fila = new PdfPTable(2);
            fila.setWidthPercentage(100);
            fila.setWidths(new float[]{7, 3});

            PdfPCell celdaNombre = new PdfPCell(new Phrase(nombrePago, font));
            celdaNombre.setBorder(Rectangle.NO_BORDER);
            fila.addCell(celdaNombre);

            PdfPCell celdaValor = new PdfPCell(new Phrase(Objects.toString(pago.getTotal(), "0.00"), font));
            celdaValor.setBorder(Rectangle.NO_BORDER);
            celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            fila.addCell(celdaValor);

            document.add(fila);
        }
    }

    private void agregarClaveAcceso(Document document, PdfWriter writer, Factura factura,
                                     float anchoUtil, Font font) throws DocumentException {

        String claveAcceso = factura.getInfoTributaria().getClaveAcceso();
        if (claveAcceso == null || claveAcceso.isBlank()) {
            return;
        }

        Paragraph etiqueta = new Paragraph("CLAVE DE ACCESO", font);
        etiqueta.setAlignment(Element.ALIGN_CENTER);
        document.add(etiqueta);

        Barcode128 codigoBarra = new Barcode128();
        codigoBarra.setCode(claveAcceso);
        codigoBarra.setCodeType(Barcode128.CODE128);
        codigoBarra.setBarHeight(28f);
        // El texto legible ya se imprime aparte (más abajo); se omite el de la
        // barra para no duplicarlo y para dejarle más espacio a las barras.
        codigoBarra.setFont(null);

        PdfContentByte cb = writer.getDirectContent();
        Image imagenBarcode = codigoBarra.createImageWithBarcode(cb, null, null);
        imagenBarcode.scaleToFit(anchoUtil, 30f);
        imagenBarcode.setAlignment(Element.ALIGN_CENTER);
        document.add(imagenBarcode);

        Paragraph textoClave = new Paragraph(claveAcceso, font);
        textoClave.setAlignment(Element.ALIGN_CENTER);
        document.add(textoClave);
    }

    private void agregarPie(Document document, Font font) throws DocumentException {
        Paragraph pie = new Paragraph("¡GRACIAS POR SU COMPRA!", font);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(6f);
        document.add(pie);
    }

    private void agregarLineaSeparadora(Document document, PdfWriter writer) throws DocumentException {
        Paragraph espacioAntes = new Paragraph();
        espacioAntes.setSpacingAfter(2f);
        document.add(espacioAntes);

        PdfContentByte cb = writer.getDirectContent();
        float y = writer.getVerticalPosition(true);
        cb.setLineWidth(0.5f);
        cb.moveTo(document.leftMargin(), y);
        cb.lineTo(document.getPageSize().getWidth() - document.rightMargin(), y);
        cb.stroke();

        Paragraph espacioDespues = new Paragraph();
        espacioDespues.setSpacingAfter(2f);
        document.add(espacioDespues);
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
