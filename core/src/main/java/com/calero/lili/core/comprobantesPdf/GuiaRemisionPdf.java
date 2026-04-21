package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.Destinatario;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class GuiaRemisionPdf {

    private final Float LEFT_PADDING_DOCUMENTO = 20f;
    private final Float LEFT_PADDING_EMPRESA = 10f;
    private final Float PADDING_NONE = 0f;

    public byte[] generarPdf(GuiaRemision factura, String AutorizacionSRI, String fechaAutorizacion, byte[] imageBytes) {

        try {

            Document document = new Document(PageSize.A4);
            document.setMargins(15,15,10,10);
            //PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(carpetaPdf + claveAcceso + ".pdf"));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            Font bf1 = new Font();
            bf1.setSize(10);

            Font fuente = new Font();
            fuente.setSize(8);

            Font title = new Font();
            title.setSize(8);
            title.setStyle(Font.BOLD);

            Font fuentePagos = new Font();
            fuentePagos.setSize(6);

            PdfPCell cell;
            PdfPCell cell_tabla;


            //TABLA1 IMAGEN E INFO TRIBUTARIA

            PdfPTable tabla1 = new PdfPTable(2); // define las columnas
            tabla1.setWidthPercentage(100);

            ////1 IMAGEN

            Image image = Image.getInstance(imageBytes);
            image.scaleToFit(100f, 77f);
            PdfPCell cell_img = new PdfPCell(image);
            cell_img.setRowspan(2); // define cuantas filas utiliza
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
            celda = generateCell(new Paragraph("GUIA DE REMISIÓN", bigFont), LEFT_PADDING_DOCUMENTO);
            celda.setColspan(2);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph("No:", title), LEFT_PADDING_DOCUMENTO);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph(factura.getInfoTributaria().getEstab() + "-" + factura.getInfoTributaria().getPtoEmi() + "-" + factura.getInfoTributaria().getSecuencial(), fuente), PADDING_NONE);
            celda.setPaddingLeft(-50);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph("NÚMERO DE AUTORIZACIÓN:", title), LEFT_PADDING_DOCUMENTO);
            celda.setColspan(2);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph(AutorizacionSRI, fuente), PADDING_NONE);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(2);
            celda.setPaddingLeft(-25);

            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph("FECHA Y HORA DE AUTORIZACIÓN:", title), LEFT_PADDING_DOCUMENTO);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph(fechaAutorizacion, fuente), PADDING_NONE);
            //celda.setPaddingLeft(-10);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph("AMBIENTE: ", title), LEFT_PADDING_DOCUMENTO);
            table_datos_documento.addCell(celda);

            String Ambiente="";
            if (factura.getInfoTributaria().getAmbiente().equals("1")){
                Ambiente="PRUEBAS";
            }
            if (factura.getInfoTributaria().getAmbiente().equals("2")){
                Ambiente="PRODUCCIÓN";
            }

            celda = generateCell(new Paragraph(Ambiente, fuente), PADDING_NONE);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph("EMISION:", title), LEFT_PADDING_DOCUMENTO);
            table_datos_documento.addCell(celda);

            String Emision="";
            if (factura.getInfoTributaria().getTipoEmision().equals("1")){
                Emision="NORMAL";
            }

            celda = generateCell(new Paragraph(Emision, fuente), PADDING_NONE);
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

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getDirEstablecimiento(), fuente), LEFT_PADDING_EMPRESA);
            cell.setColspan(4);
            cell.setPaddingTop(20);
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph("Contribuyente Especial:", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(3);
            cell.setPaddingTop(15);
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getContribuyenteEspecial(), fuente), LEFT_PADDING_EMPRESA);
            cell.setColspan(2);
            cell.setPaddingTop(20);
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph(("Obligado a llevar contabilidad:").toUpperCase(), title), LEFT_PADDING_EMPRESA);
            cell.setColspan(4);
            cell.setPaddingTop(15);
            cell.setPaddingBottom(5); // estaba 15 para no dejar linea
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getObligadoContabilidad(), fuente), LEFT_PADDING_EMPRESA);
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

            // TABLA TRANSPORTISTA

            PdfPTable table_transportista = new PdfPTable(4);
            table_transportista.setWidthPercentage(100);
            table_transportista.setTableEvent(new BorderEventWithoutRadius());
            table_transportista.setSpacingBefore(5);
            table_transportista.setSpacingAfter(5);

            cell = generateCell(new Paragraph("Identificación (Transportista):", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getRucTransportista(), fuente), PADDING_NONE);
            cell.setColspan(3);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph("Razón Social / Nombres y Apellidos:", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getRazonSocialTransportista(), fuente), PADDING_NONE);
            cell.setColspan(3);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph("Placa:", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getPlaca(), fuente), PADDING_NONE);
            cell.setColspan(3);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph("Punto de Partida:", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getDirPartida(), fuente), PADDING_NONE);
            cell.setColspan(3);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph("Fecha inicio", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            cell.setPaddingBottom(10);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getFechaIniTransporte(), fuente), PADDING_NONE);
            cell.setColspan(1);
            cell.setPaddingBottom(10);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph("Fecha fin Transporte:", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            cell.setPaddingBottom(10);
            table_transportista.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoGuiaRemision().getFechaFinTransporte(), fuente), PADDING_NONE);
            cell.setColspan(1);
            cell.setPaddingBottom(10);
            table_transportista.addCell(cell);

            document.add(table_transportista);

            // DESTINATARIOS
            List<Destinatario> destinatarios = factura.getDestinatario();
            if (destinatarios != null) {
                for (Destinatario dest : destinatarios) {

                    PdfPTable table_destinatario = new PdfPTable(4);
                    table_destinatario.setWidthPercentage(100);
                    table_destinatario.setTableEvent(new BorderEventWithoutRadius());
                    table_destinatario.setSpacingBefore(5);
                    table_destinatario.setSpacingAfter(5);

                    cell = generateCell(new Paragraph("Comprobante de Venta:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getNumDocSustento() != null ? dest.getNumDocSustento() : "", fuente), PADDING_NONE);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Fecha de Emisión:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getFechaEmisionDocSustento() != null ? dest.getFechaEmisionDocSustento() : "", fuente), PADDING_NONE);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Número de Autorización:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getNumAutDocSustento() != null ? dest.getNumAutDocSustento() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("", fuente), PADDING_NONE);
                    cell.setColspan(4);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Motivo Traslado:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getMotivoTraslado() != null ? dest.getMotivoTraslado() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Destino(Punto de llegada):", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getDirDestinatario() != null ? dest.getDirDestinatario() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Identificación (Destinatario):", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getIdentificacionDestinatario() != null ? dest.getIdentificacionDestinatario() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Razón Social/Nombres Apellidos:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getRazonSocialDestinatario() != null ? dest.getRazonSocialDestinatario() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Documento Aduanero:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getDocAduaneroUnico() != null ? dest.getDocAduaneroUnico() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Código Establecimiento Destino:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getCodEstabDestino() != null ? dest.getCodEstabDestino() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph("Ruta:", title), LEFT_PADDING_EMPRESA);
                    cell.setColspan(1);
                    cell.setPaddingBottom(10);
                    table_destinatario.addCell(cell);

                    cell = generateCell(new Paragraph(dest.getRuta() != null ? dest.getRuta() : "", fuente), PADDING_NONE);
                    cell.setColspan(3);
                    cell.setPaddingBottom(10);
                    table_destinatario.addCell(cell);

                    document.add(table_destinatario);

                    // DETALLE
                    PdfPTable table_detalle = new PdfPTable(5);
                    table_detalle.setWidthPercentage(100);
                    table_detalle.setTableEvent(new BorderEventWithoutRadius());
                    table_detalle.setSpacingAfter(5);

                    cell = new PdfPCell(new Phrase("Cantidad", title));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPaddingBottom(10);
                    table_detalle.addCell(cell);

                    cell = new PdfPCell(new Phrase("Descripción", title));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_detalle.addCell(cell);

                    cell = new PdfPCell(new Phrase("Detalle Adicional", title));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_detalle.addCell(cell);

                    cell = new PdfPCell(new Phrase("Código Principal", title));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_detalle.addCell(cell);

                    cell = new PdfPCell(new Phrase("Código Auxiliar", title));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_detalle.addCell(cell);

                    List<Detalle> detalles = dest.getDetalle();
                    if (detalles != null) {
                        for (Detalle det : detalles) {
                            cell = new PdfPCell(new Phrase(det.getCantidad() != null ? det.getCantidad() : "", fuente));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(5);
                            table_detalle.addCell(cell);

                            cell = new PdfPCell(new Phrase(det.getDescripcion() != null ? det.getDescripcion() : "", fuente));
                            table_detalle.addCell(cell);

                            String detAdicionalText = "";
                            if (det.getDetAdicional() != null) {
                                StringBuilder sb = new StringBuilder();
                                for (DetAdicional da : det.getDetAdicional()) {
                                    if (da.getValor() != null) {
                                        if (sb.length() > 0) sb.append(", ");
                                        sb.append(da.getValor());
                                    }
                                }
                                detAdicionalText = sb.toString();
                            }
                            cell = new PdfPCell(new Phrase(detAdicionalText, fuente));
                            table_detalle.addCell(cell);

                            cell = new PdfPCell(new Phrase(det.getCodigoInterno() != null ? det.getCodigoInterno() : "", fuente));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table_detalle.addCell(cell);

                            cell = new PdfPCell(new Phrase(det.getCodigoAdicional() != null ? det.getCodigoAdicional() : "", fuente));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table_detalle.addCell(cell);
                        }
                    }
                    document.add(table_detalle);
                }
            }

            // INFORMACIÓN ADICIONAL
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
                for (int pos = 0; pos < lstCamposAdicionales.size(); pos++) {
                    if (lstCamposAdicionales.get(pos).getNombre() != null && lstCamposAdicionales.get(pos).getValor() != null) {
                        cell = generateCell(new Paragraph(lstCamposAdicionales.get(pos).getNombre() + ":", title), LEFT_PADDING_EMPRESA);
                        if (pos == lstCamposAdicionales.size() - 1)
                            cell.setPaddingBottom(20);
                        table_info_adic.addCell(cell);

                        cell = generateCell(new Paragraph(lstCamposAdicionales.get(pos).getValor(), fuente), PADDING_NONE);
                        cell.setPaddingLeft(-70);
                        table_info_adic.addCell(cell);
                    }
                }
            }
            document.add(table_info_adic);

            document.close();
            return byteArrayOutputStream.toByteArray();


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
}
