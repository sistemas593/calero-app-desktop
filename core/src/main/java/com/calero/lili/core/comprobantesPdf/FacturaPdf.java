package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.Destino;
import com.calero.lili.core.comprobantes.objetosXml.factura.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoFactura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoSustitutivaGuiaRemision;
import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.core.enums.FormaPagoSriEnum;
import com.calero.lili.core.enums.TipoPdfFactura;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Rectangle;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Genera el PDF de la factura en hoja completa: A4 ({@link #generarPdf}) y
 * A5 ({@link #generarPdfA5}).
 * <p>
 * Ambos formatos comparten el mismo layout de tablas (encabezado, guía
 * sustitutiva, detalle, información adicional/pagos, totales) porque esas
 * tablas usan anchos porcentuales y se adaptan solas al ancho de hoja. La
 * única sección que NO se comparte es la de datos del comprador (razón
 * social, dirección, fecha, identificación, guía): en A4 usa un truco de
 * paddings negativos para simular columnas fusionadas que, al achicar la
 * hoja a A5, hacía que el texto se montara sobre la celda de al lado. Para
 * A5 esa sección se arma con una tabla simple de 2 columnas (etiqueta /
 * valor), una fila por dato, sin ningún truco de padding.
 */
@Service
public class FacturaPdf {

    private final Float LEFT_PADDING_DOCUMENTO = 20f;
    private final Float LEFT_PADDING_EMPRESA = 10f;
    private final Float PADDING_NONE = 0f;

    // ==================== PUNTOS DE ENTRADA ====================

    public byte[] generarPdf(Factura factura, String autorizacionSri, String fechaAutorizacion, byte[] imageBytes) {
        return construirPdf(factura, autorizacionSri, fechaAutorizacion, imageBytes, TipoPdfFactura.A4);
    }

    public byte[] generarPdfA5(Factura factura, String autorizacionSri, String fechaAutorizacion, byte[] imageBytes) {
        return construirPdf(factura, autorizacionSri, fechaAutorizacion, imageBytes, TipoPdfFactura.A5);
    }

    /**
     * Mantiene compatible el código existente que ya llama a
     * {@code generarPdf(..., tipo)} (por ejemplo cuando el tipo viene de un
     * filtro/DTO); simplemente reparte hacia el método correcto.
     */
    public byte[] generarPdf(Factura factura, String autorizacionSri, String fechaAutorizacion, byte[] imageBytes, TipoPdfFactura tipo) {
        if (tipo == TipoPdfFactura.A5) {
            return generarPdfA5(factura, autorizacionSri, fechaAutorizacion, imageBytes);
        }
        return generarPdf(factura, autorizacionSri, fechaAutorizacion, imageBytes);
    }

    // ==================== ARMADO COMÚN ====================

    private byte[] construirPdf(Factura factura, String autorizacionSri, String fechaAutorizacion, byte[] imageBytes, TipoPdfFactura tipo) {

        boolean esA5 = tipo == TipoPdfFactura.A5;

        try {
            Document document = PdfPageSizeUtil.crearDocumento(tipo);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            Fuentes fuentes = new Fuentes();

            agregarEncabezado(document, pdfWriter, factura, autorizacionSri, fechaAutorizacion, imageBytes, fuentes);

            if (esA5) {
                agregarDatosCompradorA5(document, factura, fuentes);
            } else {
                agregarDatosComprador(document, factura, fuentes);
            }

            agregarGuiaSustitutiva(document, factura, fuentes);
            agregarDetalle(document, factura, fuentes);
            agregarInformacionAdicionalYPagos(document, factura, fuentes, esA5);

            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fuentes reutilizadas por todas las secciones del PDF, armadas una sola
     * vez por documento generado.
     */
    private static final class Fuentes {
        private final Font normal = new Font();
        private final Font titulo = new Font();
        private final Font pagos = new Font();

        private Fuentes() {
            normal.setSize(8);
            titulo.setSize(8);
            titulo.setStyle(Font.BOLD);
            pagos.setSize(8);
        }
    }

    // ==================== ENCABEZADO (logo + info tributaria + datos empresa) ====================
    // Sección compartida entre A4 y A5: usa anchos porcentuales y ya se ve bien en ambos tamaños.

    private void agregarEncabezado(Document document, PdfWriter pdfWriter, Factura factura, String autorizacionSri,
                                    String fechaAutorizacion, byte[] imageBytes, Fuentes fuentes) throws Exception {

        Font fuente = fuentes.normal;
        Font title = fuentes.titulo;

        PdfPCell cell;
        PdfPCell cell_tabla;

        //TABLA1 IMAGEN E INFO TRIBUTARIA

        PdfPTable tabla1 = new PdfPTable(2); // define las columnas
        tabla1.setWidthPercentage(100);

        ////1 IMAGEN

        PdfPCell cell_img;
        if (imageBytes != null) {
            Image image = Image.getInstance(imageBytes);
            image.scaleToFit(100f, 77f);
            cell_img = new PdfPCell(image);
        } else {
            Font fuenteNoLogo = new Font();
            fuenteNoLogo.setSize(12);
            fuenteNoLogo.setStyle(Font.BOLD);
            fuenteNoLogo.setColor(BaseColor.RED);
            cell_img = new PdfPCell(new Phrase("NO TIENE LOGO", fuenteNoLogo));
            cell_img.setVerticalAlignment(Element.ALIGN_MIDDLE);
        }
        cell_img.setRowspan(2);
        cell_img.setBorder(0);
        cell_img.setHorizontalAlignment(Element.ALIGN_CENTER);

        tabla1.addCell(cell_img);

        ///// TABLA1 DATOS DOCUMENTO

        PdfPTable table_datos_documento = new PdfPTable(2);
        table_datos_documento.setTableEvent(new BorderEvent());

        table_datos_documento.setWidthPercentage(100);

        PdfPCell celda;
        celda = generateCell(new Paragraph("R.U.C.:", title), LEFT_PADDING_DOCUMENTO);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph(factura.getInfoTributaria().getRuc(), fuente), PADDING_NONE);
        celda.setPaddingLeft(-30);
        table_datos_documento.addCell(celda);

        Font bigFont = new Font();
        bigFont.setSize(12);
        bigFont.setStyle(Font.BOLD);
        celda = generateCell(new Paragraph("FACTURA", bigFont), LEFT_PADDING_DOCUMENTO);
        celda.setColspan(2);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph("No:", title), LEFT_PADDING_DOCUMENTO);
        table_datos_documento.addCell(celda);


        celda = generateCell(new Paragraph( factura.getInfoTributaria().getEstab() + "-" +
                factura.getInfoTributaria().getPtoEmi() + "-" + factura.getInfoTributaria().getSecuencial(),
                fuente), PADDING_NONE);

        celda.setPaddingLeft(-50);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph("NÚMERO DE AUTORIZACIÓN:", title), LEFT_PADDING_DOCUMENTO);
        celda.setColspan(2);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph(autorizacionSri, fuente), PADDING_NONE);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setColspan(2);
        celda.setPaddingLeft(-25);

        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph("FECHA Y HORA DE AUTORIZACIÓN:", title), LEFT_PADDING_DOCUMENTO);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph(fechaAutorizacion, fuente), PADDING_NONE);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph("AMBIENTE: ", title), LEFT_PADDING_DOCUMENTO);
        table_datos_documento.addCell(celda);

        String ambienteTexto = "";
        if (factura.getInfoTributaria().getAmbiente().equals("1")) {
            ambienteTexto = "PRUEBAS";
        }
        if (factura.getInfoTributaria().getAmbiente().equals("2")) {
            ambienteTexto = "PRODUCCIÓN";
        }

        celda = generateCell(new Paragraph(ambienteTexto, fuente), PADDING_NONE);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph("EMISION:", title), LEFT_PADDING_DOCUMENTO);
        table_datos_documento.addCell(celda);

        String emisionTexto = "";
        if (factura.getInfoTributaria().getTipoEmision().equals("1")) {
            emisionTexto = "NORMAL";
        }

        celda = generateCell(new Paragraph(emisionTexto, fuente), PADDING_NONE);
        table_datos_documento.addCell(celda);

        celda = generateCell(new Paragraph("CLAVE DE ACCESO:", title), LEFT_PADDING_DOCUMENTO);
        celda.setColspan(2);
        celda.setPaddingBottom(15);
        table_datos_documento.addCell(celda);

        Barcode128 codigoBarra = new Barcode128();
        PdfContentByte cimg = pdfWriter.getDirectContent();
        codigoBarra.setCode(factura.getInfoTributaria().getClaveAcceso());
        codigoBarra.setCodeType(Barcode128.CODE128);
        codigoBarra.setTextAlignment(Element.ALIGN_CENTER);
        codigoBarra.setBarHeight(70);
        Image imgCod = codigoBarra.createImageWithBarcode(cimg, null, null);
        imgCod.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cellBar = new PdfPCell();
        cellBar.addElement(imgCod);
        cellBar.setBorder(0);
        cellBar.setColspan(2);
        cellBar.setPaddingRight(15);
        cellBar.setPaddingLeft(15);
        table_datos_documento.addCell(cellBar);

        cell_tabla = new PdfPCell();
        cell_tabla.addElement(table_datos_documento);
        cell_tabla.setRowspan(3);
        cell_tabla.setBorder(0);
        cell_tabla.setPaddingBottom(0);
        cell_tabla.setPaddingLeft(0);
        cell_tabla.setPaddingRight(0);

        tabla1.addCell(cell_tabla);

        // TABLA1 DATOS EMPRESA

        PdfPTable table_datos_empresa = new PdfPTable(5);
        table_datos_empresa.setWidthPercentage(100);
        table_datos_empresa.setTableEvent(new BorderEvent());

        cell = generateCell(new Paragraph(factura.getInfoTributaria().getRazonSocial().toUpperCase(), title), PADDING_NONE);
        cell.setColspan(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoTributaria().getNombreComercial(), fuente), LEFT_PADDING_EMPRESA);
        cell.setColspan(5);
        cell.setPaddingTop(15);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph("Dirección Matriz:", title), LEFT_PADDING_EMPRESA);
        cell.setColspan(1);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoTributaria().getDirMatriz(), fuente), LEFT_PADDING_EMPRESA);
        cell.setColspan(4);
        cell.setPaddingTop(15);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph("Dirección Sucursal: ", title), LEFT_PADDING_EMPRESA);
        cell.setColspan(1);
        cell.setPaddingTop(15);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoFactura().getDirEstablecimiento(), fuente), LEFT_PADDING_EMPRESA);
        cell.setColspan(4);
        cell.setPaddingTop(20);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph("Contribuyente Especial:", title), LEFT_PADDING_EMPRESA);
        cell.setColspan(3);
        cell.setPaddingTop(15);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoFactura().getContribuyenteEspecial(), fuente), LEFT_PADDING_EMPRESA);
        cell.setColspan(2);
        cell.setPaddingTop(20);
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(("Obligado a llevar contabilidad:").toUpperCase(), title), LEFT_PADDING_EMPRESA);
        cell.setColspan(4);
        cell.setPaddingTop(15);
        cell.setPaddingBottom(5); // estaba 15 para no dejar linea
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoFactura().getObligadoContabilidad(), fuente), LEFT_PADDING_EMPRESA);
        cell.setColspan(1);
        cell.setPaddingTop(15);
        cell.setPaddingBottom(5); // estaba 15 para no dejar linea
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(("Agente de Retención Resolución No.:").toUpperCase(), title), LEFT_PADDING_EMPRESA);
        cell.setColspan(4);
        cell.setPaddingTop(15);
        cell.setPaddingBottom(15); // estaba 15 para no dejar linea
        table_datos_empresa.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoTributaria().getAgenteRetencion(), fuente), LEFT_PADDING_EMPRESA);
        cell.setColspan(1);
        cell.setPaddingTop(15);
        cell.setPaddingBottom(15); // estaba 15 para no dejar linea
        table_datos_empresa.addCell(cell);

        cell_tabla = new PdfPCell();
        cell_tabla.addElement(table_datos_empresa);
        cell_tabla.setBorder(0);
        cell_tabla.setPaddingRight(10);
        cell_tabla.setPaddingLeft(0);
        cell_tabla.setVerticalAlignment(Element.ALIGN_BOTTOM);

        tabla1.addCell(cell_tabla);

        document.add(tabla1);
    }

    // ==================== DATOS DEL COMPRADOR ====================

    /**
     * Versión A4: las filas de Razón Social / Dirección usan colspans que no
     * calzan exacto con las 6 columnas de la tabla, más paddings negativos
     * grandes (-120, -80) para simular que la etiqueta y el valor quedan en
     * la misma línea. Funcionan bien en A4 porque esos números están
     * calibrados a mano para su ancho; por eso NO se reutilizan en A5.
     * <p>
     * Fecha / Identificación / Guía / Placa van los 4 en una sola fila, en
     * una tabla anidada de 8 columnas con anchos calibrados según el largo
     * de cada etiqueta/valor (no un reparto parejo): así ninguna etiqueta
     * salta de línea y, al ser ancho real de columna (no padding negativo),
     * etiqueta y valor no se sobreponen. Placa queda al final, después de
     * Guía.
     */
    private void agregarDatosComprador(Document document, Factura factura, Fuentes fuentes) throws DocumentException {

        Font fuente = fuentes.normal;
        Font title = fuentes.titulo;
        PdfPCell cell;

        PdfPTable table_datos = new PdfPTable(6);
        table_datos.setWidthPercentage(100);
        table_datos.setTableEvent(new BorderEventWithoutRadius());

        cell = generateCell(new Paragraph("Razón Social / Nombres y Apellidos:", title), LEFT_PADDING_EMPRESA);
        cell.setPaddingBottom(5);
        cell.setColspan(3);
        table_datos.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoFactura().getRazonSocialComprador(), fuente), PADDING_NONE);
        cell.setColspan(5);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(-120);
        table_datos.addCell(cell);

        cell = generateCell(new Paragraph("Dirección Comprador:", title), LEFT_PADDING_EMPRESA);
        cell.setPaddingBottom(5);
        cell.setColspan(2);
        table_datos.addCell(cell);

        cell = generateCell(new Paragraph(factura.getInfoFactura().getDireccionComprador(), fuente), PADDING_NONE);
        cell.setColspan(5);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(-80);
        table_datos.addCell(cell);

        // Los 4 pares (Fecha, Identificación, Guía, Placa) en una sola fila. Anchos
        // calibrados según el largo de cada etiqueta/valor (Identificación y Guía
        // necesitan más espacio que Placa) para que ningún título salte de línea ni
        // se sobreponga con su valor.
        PdfPTable filaFechaIdGuiaPlaca = new PdfPTable(new float[]{14f, 11f, 15f, 12f, 7f, 16f, 8f, 9f});
        filaFechaIdGuiaPlaca.setWidthPercentage(100);

        agregarFilaEtiquetaValor(filaFechaIdGuiaPlaca, "Fecha emisión:", factura.getInfoFactura().getFechaEmision(), fuentes);
        agregarFilaEtiquetaValor(filaFechaIdGuiaPlaca, "Identificación:", factura.getInfoFactura().getIdentificacionComprador(), fuentes);
        agregarFilaEtiquetaValor(filaFechaIdGuiaPlaca, "Guía:", factura.getInfoFactura().getGuiaRemision(), fuentes);
        agregarFilaEtiquetaValor(filaFechaIdGuiaPlaca, "Placa:", factura.getInfoFactura().getPlaca(), fuentes);

        cell = new PdfPCell();
        cell.addElement(filaFechaIdGuiaPlaca);
        cell.setColspan(6);
        cell.setBorder(0);
        cell.setPadding(0);
        cell.setPaddingBottom(10);
        table_datos.addCell(cell);

        table_datos.setSpacingBefore(5);
        table_datos.setSpacingAfter(5);

        document.add(table_datos);
    }

    /**
     * Versión A5: tabla simple de 2 columnas (etiqueta / valor), una fila
     * por dato, sin colspans que no calzan ni paddings negativos. Al no
     * depender de ningún ajuste fino calibrado para un ancho específico,
     * queda bien alineada sin importar cuán angosta sea la hoja.
     */
    private void agregarDatosCompradorA5(Document document, Factura factura, Fuentes fuentes) throws DocumentException {

        InfoFactura infoFactura = factura.getInfoFactura();

        PdfPTable tabla = new PdfPTable(new float[]{35f, 65f});
        tabla.setWidthPercentage(100);
        tabla.setTableEvent(new BorderEventWithoutRadius());
        tabla.setSpacingBefore(5);
        tabla.setSpacingAfter(5);

        agregarFilaEtiquetaValor(tabla, "Razón Social / Nombres y Apellidos:", infoFactura.getRazonSocialComprador(), fuentes);
        agregarFilaEtiquetaValor(tabla, "Dirección Comprador:", infoFactura.getDireccionComprador(), fuentes);
        agregarFilaEtiquetaValor(tabla, "Fecha emisión:", infoFactura.getFechaEmision(), fuentes);
        agregarFilaEtiquetaValor(tabla, "Identificación:", infoFactura.getIdentificacionComprador(), fuentes);
        agregarFilaEtiquetaValor(tabla, "Guía:", infoFactura.getGuiaRemision(), fuentes);
        agregarFilaEtiquetaValor(tabla, "Placa:", infoFactura.getPlaca(), fuentes);

        document.add(tabla);
    }

    private void agregarFilaEtiquetaValor(PdfPTable tabla, String etiqueta, String valor, Fuentes fuentes) {
        PdfPCell celdaEtiqueta = generateCell(new Paragraph(etiqueta, fuentes.titulo), LEFT_PADDING_EMPRESA);
        celdaEtiqueta.setPaddingBottom(5f);
        tabla.addCell(celdaEtiqueta);

        PdfPCell celdaValor = generateCell(new Paragraph(Objects.toString(valor, ""), fuentes.normal), LEFT_PADDING_EMPRESA);
        celdaValor.setPaddingBottom(5f);
        tabla.addCell(celdaValor);
    }

    // ==================== GUÍA DE REMISIÓN SUSTITUTIVA (opcional) ====================
    // Sección compartida: ya usa filas de 4 columnas iguales y celdas sin trucos de padding.

    private void agregarGuiaSustitutiva(Document document, Factura factura, Fuentes fuentes) throws DocumentException {

        Font fuente = fuentes.normal;
        Font title = fuentes.titulo;

        InfoSustitutivaGuiaRemision guia = factura.getInfoSustitutivaGuiaRemision();

        if (guia == null) {
            return;
        }

        PdfPTable tableGuia = new PdfPTable(1);
        tableGuia.setWidthPercentage(100);
        tableGuia.setTableEvent(new BorderEventWithoutRadius());
        tableGuia.setSpacingAfter(5);

        PdfPCell contenedor = new PdfPCell();
        contenedor.setBorder(Rectangle.NO_BORDER);
        contenedor.setPadding(0);

        // ==========================
        // TÍTULO
        // ==========================
        PdfPTable titulo = new PdfPTable(1);
        titulo.setWidthPercentage(100);

        PdfPCell celdaTitulo = new PdfPCell(new Phrase("Sustitutiva de Guía de Remisión", title));
        celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTitulo.setBorder(Rectangle.NO_BORDER);
        celdaTitulo.setPaddingBottom(4);
        titulo.addCell(celdaTitulo);

        contenedor.addElement(titulo);

        // ==========================
        // FILA 1
        // ==========================
        PdfPTable fila = new PdfPTable(new float[]{25, 25, 25, 25});
        fila.setWidthPercentage(100);

        fila.addCell(getCell("RUC / CI (Transportista):", title));
        fila.addCell(getCell(guia.getRucTransportista(), fuente));

        fila.addCell(getCell("Razón Social / Nombres:", title));
        fila.addCell(getCell(guia.getRazonSocialTransportista(), fuente));

        contenedor.addElement(fila);

        // ==========================
        // FILA 2
        // ==========================
        fila = new PdfPTable(new float[]{25, 25, 25, 25});
        fila.setWidthPercentage(100);

        fila.addCell(getCell("Placa:", title));
        fila.addCell(getCell(guia.getPlaca(), fuente));

        fila.addCell(getCell("Punto de partida:", title));
        fila.addCell(getCell(guia.getDirPartida(), fuente));

        contenedor.addElement(fila);

        // ==========================
        // FILA 3
        // ==========================
        fila = new PdfPTable(new float[]{25, 25, 25, 25});
        fila.setWidthPercentage(100);

        fila.addCell(getCell("Fecha inicio transporte:", title));
        fila.addCell(getCell(guia.getFechaIniTransporte(), fuente));

        fila.addCell(getCell("Fecha fin transporte:", title));
        fila.addCell(getCell(guia.getFechaFinTransporte(), fuente));

        contenedor.addElement(fila);

        // ==========================
        // DESTINOS
        // ==========================
        if (guia.getDestino() != null) {

            for (Destino destino : guia.getDestino()) {

                fila = new PdfPTable(new float[]{25, 25, 25, 25});
                fila.setWidthPercentage(100);

                fila.addCell(getCell("Destino:", title));
                fila.addCell(getCell(guia.getDirDestinatario(), fuente));

                fila.addCell(getCell("Motivo traslado:", title));
                fila.addCell(getCell(destino.getMotivoTraslado(), fuente));

                contenedor.addElement(fila);

                fila = new PdfPTable(new float[]{25, 25, 25, 25});
                fila.setWidthPercentage(100);

                fila.addCell(getCell("Cod. Establecimiento destino:", title));
                fila.addCell(getCell(destino.getCodEstabDestino(), fuente));

                fila.addCell(getCell("Ruta:", title));
                fila.addCell(getCell(destino.getRuta(), fuente));

                contenedor.addElement(fila);
            }
        }

        tableGuia.addCell(contenedor);
        document.add(tableGuia);
    }

    // ==================== DETALLE ====================
    // Sección compartida: tabla de 7 columnas porcentuales, sin trucos de padding.

    private void agregarDetalle(Document document, Factura factura, Fuentes fuentes) throws DocumentException {

        Font fuente = fuentes.normal;
        Font title = fuentes.titulo;
        PdfPCell cell;

        PdfPTable table_detalle = new PdfPTable(7);
        table_detalle.setWidthPercentage(100);
        table_detalle.setTableEvent(new BorderEventWithoutRadius());

        cell = new PdfPCell(new Phrase("Código", title));
        cell.setPaddingBottom(10);
        table_detalle.addCell(cell);

        cell = new PdfPCell(new Phrase("Descripción", title));
        cell.setColspan(2);
        table_detalle.addCell(cell);

        cell = new PdfPCell(new Phrase("Cantidad", title));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_detalle.addCell(cell);

        cell = new PdfPCell(new Phrase("Precio Unitario", title));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_detalle.addCell(cell);

        cell = new PdfPCell(new Phrase("Descuento", title));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_detalle.addCell(cell);

        cell = new PdfPCell(new Phrase("Precio Total", title));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_detalle.addCell(cell);

        List<Detalle> lista1 = factura.getDetalle();
        for (Detalle det : lista1) {

            cell = new PdfPCell(new Phrase(det.getCodigoPrincipal(), fuente));
            cell.setPaddingBottom(5);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase(det.getDescripcion().toUpperCase(), fuente));
            cell.setColspan(2);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase(det.getCantidad(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase(det.getPrecioUnitario(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase(det.getDescuento(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase(det.getPrecioTotalSinImpuesto(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detalle.addCell(cell);
            table_detalle.setSpacingAfter(5);
        }

        document.add(table_detalle);
    }

    // ==================== INFORMACIÓN ADICIONAL + FORMAS DE PAGO ====================
    // Sección compartida. El único punto sensible al ancho es el padding del valor de cada
    // "campo adicional": en A4 se pega el valor a su etiqueta con un padding negativo (-100)
    // calibrado a mano; en A5 esa misma columna es más angosta, así que se usa un padding
    // positivo chico en su lugar (deja un espacio prolijo en vez de encimar el texto).

    private void agregarInformacionAdicionalYPagos(Document document, Factura factura, Fuentes fuentes, boolean esA5) throws DocumentException {

        Font fuente = fuentes.normal;
        Font title = fuentes.titulo;
        Font fuentePagos = fuentes.pagos;
        PdfPCell cell;

        PdfPTable table3 = new PdfPTable(3);
        table3.setWidthPercentage(100);

        PdfPTable table_info_adic = new PdfPTable(2);
        table_info_adic.setWidthPercentage(100);
        table_info_adic.setTableEvent(new BorderEventWithoutRadius());
        table_info_adic.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_info_adic.setSpacingAfter(5);
        cell = new PdfPCell(new Paragraph("Información Adicional", title));
        cell.setPaddingLeft(LEFT_PADDING_DOCUMENTO);

        cell.setColspan(2);
        table_info_adic.addCell(cell);
        List<CampoAdicional> lstCamposAdicionales = factura.getCampoAdicional();
        if (lstCamposAdicionales != null) {
            float paddingValorCampo = esA5 ? 4f : -100f;
            for (int pos = 0; pos < lstCamposAdicionales.size(); pos++) {
                if (lstCamposAdicionales.get(pos).getNombre() != null && lstCamposAdicionales.get(pos).getValor() != null) {
                    cell = new PdfPCell(new Paragraph(lstCamposAdicionales.get(pos).getNombre() + ":", title));
                    cell.setBorder(0);
                    cell.setPaddingLeft(LEFT_PADDING_EMPRESA);

                    if (pos == lstCamposAdicionales.size() - 1)
                        cell.setPaddingBottom(10);
                    table_info_adic.addCell(cell);

                    cell = new PdfPCell(new Paragraph(lstCamposAdicionales.get(pos).getValor(), fuente));
                    cell.setBorder(0);
                    cell.setPaddingLeft(paddingValorCampo);
                    table_info_adic.addCell(cell);
                }
            }
        }
        PdfPTable tableIntermedia = new PdfPTable(1);
        tableIntermedia.setWidthPercentage(100);

        cell = new PdfPCell();
        cell.addElement(table_info_adic);
        cell.setBorder(0);
        cell.setPaddingLeft(0);

        tableIntermedia.addCell(cell);

        // AGREGAMOS TABLA DE PAGOS

        PdfPTable table_pagos = new PdfPTable(3);
        table_pagos.setWidthPercentage(80);
        table_pagos.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_pagos.setTableEvent(new BorderEventWithoutRadius());

        cell = new PdfPCell(new Phrase("Forma de pago", title));
        cell.setColspan(2);
        cell.setPaddingBottom(10);
        table_pagos.addCell(cell);

        cell = new PdfPCell(new Phrase("Valor", title));
        cell.setColspan(1);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_pagos.addCell(cell);

        List<Pago> lstFormasPago = factura.getInfoFactura().getPago();

        if (lstFormasPago != null) {
            for (Pago pag : lstFormasPago) {
                String nombrePago;
                try {
                    nombrePago = pag.getFormaPago() + "-" + FormaPagoSriEnum.getNombrePago(pag.getFormaPago());
                } catch (Exception e) {
                    nombrePago = pag.getFormaPago().toUpperCase();
                }
                cell = new PdfPCell(new Phrase(nombrePago, fuentePagos));
                cell.setColspan(2);
                table_pagos.addCell(cell);

                cell = new PdfPCell(new Phrase(pag.getTotal(), fuentePagos));
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_pagos.addCell(cell);
            }
        }

        cell = new PdfPCell();
        cell.addElement(table_pagos);
        cell.setBorder(0);
        cell.setPaddingLeft(0);

        tableIntermedia.addCell(cell);

        cell = new PdfPCell();
        cell.addElement(tableIntermedia);
        cell.setBorder(0);
        cell.setColspan(2);
        cell.setPaddingLeft(0);
        cell.setPaddingTop(0);
        table3.addCell(cell);

        agregarTablaTotales(document, table3, factura, fuentes);
    }

    // ==================== TOTALES ====================
    // Sección compartida: colspans exactos (2+1 de 3), sin trucos de padding.

    private void agregarTablaTotales(Document document, PdfPTable table3, Factura factura, Fuentes fuentes) throws DocumentException {

        Font fuente = fuentes.normal;
        Font title = fuentes.titulo;
        PdfPCell cell;

        PdfPTable table_totales = new PdfPTable(3);
        table_totales.setWidthPercentage(100);
        table_totales.setHorizontalAlignment(Element.ALIGN_RIGHT);

        InfoFactura infoFactura = factura.getInfoFactura();
        FacturaImpuestosUtil.SubtotalesFactura subtotales = FacturaImpuestosUtil.resolverSubtotales(infoFactura.getTotalImpuesto());

        cell = new PdfPCell(new Phrase("SUBTOTAL SIN IMPUESTOS:", title));
        cell.setPaddingBottom(5);
        cell.setColspan(2);
        table_totales.addCell(cell);
        cell = new PdfPCell(new Phrase(infoFactura.getTotalSinImpuestos(), fuente));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_totales.addCell(cell);

        cell = new PdfPCell(new Phrase("TOTAL DESCUENTO:", title));
        cell.setPaddingBottom(5);
        cell.setColspan(2);
        table_totales.addCell(cell);
        cell = new PdfPCell(new Phrase(infoFactura.getTotalDescuento(), fuente));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_totales.addCell(cell);

        if (Objects.nonNull(subtotales.getSubTotal0())) {
            cell = new PdfPCell(new Phrase("SUBTOTAL 0%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subtotales.getSubTotal0().getBaseImponible(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);
        }

        if (Objects.nonNull(subtotales.getSubTotal15())) {
            cell = new PdfPCell(new Phrase("SUBTOTAL 15%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase(subtotales.getSubTotal15().getBaseImponible(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("IVA 15%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase(subtotales.getSubTotal15().getValor(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);
        }

        if (Objects.nonNull(subtotales.getSubTotal8())) {
            cell = new PdfPCell(new Phrase("SUBTOTAL 8%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase(subtotales.getSubTotal8().getBaseImponible(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("IVA 8%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase(subtotales.getSubTotal8().getValor(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);
        }

        if (Objects.nonNull(subtotales.getSubTotal5())) {
            cell = new PdfPCell(new Phrase("SUBTOTAL 5%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase(subtotales.getSubTotal5().getBaseImponible(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("IVA 5%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase(subtotales.getSubTotal5().getValor(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);
        }

        cell = new PdfPCell(new Phrase("VALOR TOTAL:", title));
        cell.setPaddingBottom(5);
        cell.setColspan(2);
        table_totales.addCell(cell);
        cell = new PdfPCell(new Phrase(infoFactura.getImporteTotal(), fuente));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_totales.addCell(cell);

        cell = new PdfPCell();
        cell.addElement(table_totales);
        cell.setBorder(0);
        cell.setPaddingRight(0);

        table3.addCell(cell);

        document.add(table3);
    }

    public class BorderEvent implements PdfPTableEvent {
        public void tableLayout(PdfPTable table, float[][] widths, float[] height, int headerRows, int rowStart, PdfContentByte[] canvas) {
            float width[] = widths[0];
            float x1 = width[0];
            float x2 = width[width.length - 1];
            float y1 = height[0];
            float y2 = height[height.length - 1];
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.roundRectangle(x1, y1, x2 - x1, y2 - y1, 8);
            cb.stroke();
            cb.resetRGBColorStroke();
        }
    }

    public class BorderEventWithoutRadius implements PdfPTableEvent {
        public void tableLayout(PdfPTable table, float[][] widths, float[] height, int headerRows, int rowStart, PdfContentByte[] canvas) {
            float width[] = widths[0];
            float x1 = width[0];
            float x2 = width[width.length - 1];
            float y1 = height[0];
            float y2 = height[height.length - 1];
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.rectangle(x1, y1, x2 - x1, y2 - y1);
            cb.stroke();
            cb.resetRGBColorStroke();
        }
    }

    private PdfPCell generateCell(Paragraph paragraph, Float LEFT_PADDING) {
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(0);
        cell.setPaddingLeft(LEFT_PADDING);
        cell.setPaddingTop(10);
        return cell;
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    private PdfPCell getCell(String texto, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", font));

        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2);
        cell.setPaddingBottom(2);
        cell.setPaddingLeft(3);
        cell.setPaddingRight(3);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;
    }
}
