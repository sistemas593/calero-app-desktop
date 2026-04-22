package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoFactura;
import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.core.enums.FormaPagoSriEnum;
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
public class FacturaPdf {

    private final Float LEFT_PADDING_DOCUMENTO = 20f;
    private final Float LEFT_PADDING_EMPRESA = 10f;
    private final Float PADDING_NONE = 0f;

//    private static String projectId = "caleroapp";
//    private static String bucketName = "caleroapp-bucket-sgn";

    public byte[] generarPdf(Factura factura, String AutorizacionSRI, String fechaAutorizacion, byte[] imageBytes) {

        try {

            Document document = new Document(PageSize.A4);
            document.setMargins(15,15,10,10);
//            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(carpetaPdf + claveAcceso + ".pdf"));
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
            fuentePagos.setSize(8);

            PdfPCell cell;
            PdfPCell cell_tabla;



            //TABLA1 IMAGEN E INFO TRIBUTARIA

            PdfPTable tabla1 = new PdfPTable(2); // define las columnas
            tabla1.setWidthPercentage(100);

            ////1 IMAGEN

//            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//            BlobId blobId = BlobId.of(bucketName, "data00001/logo-fe-0001.jpg");
//            Blob blob = storage.get(blobId);
//            if (blob == null || !blob.exists()) {
//                throw new GeneralException("El archivo especificado no existe en el bucket.");
//            }
//            ReadChannel reader = blob.reader();
//            InputStream inputStream = Channels.newInputStream(reader);
//            byte[] imageBytes = inputStream.readAllBytes();

            Image image = Image.getInstance(imageBytes);

            //Image image = Image.getInstance("logo.jpg");
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
            celda = generateCell(new Paragraph("FACTURA", bigFont), LEFT_PADDING_DOCUMENTO);
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

            //////////////////////////////
            // TABLA 2 iNFO FACTURA

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

            cell = generateCell(new Paragraph("Fecha emision: ", title), LEFT_PADDING_EMPRESA);
            cell.setPaddingBottom(15);
            cell.setColspan(1);
            table_datos.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoFactura().getFechaEmision(), fuente), PADDING_NONE);
            cell.setColspan(1);
            cell.setPaddingBottom(15);
            cell.setPaddingLeft(-20);
            table_datos.addCell(cell);

            cell = generateCell(new Paragraph("Identificación:", title), LEFT_PADDING_EMPRESA);
            cell.setPaddingBottom(5);
            cell.setColspan(1);
            table_datos.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoFactura().getIdentificacionComprador(), fuente), PADDING_NONE);
            cell.setColspan(1);
            cell.setPaddingBottom(5);
            cell.setPaddingLeft(-20);
            table_datos.addCell(cell);

            cell = generateCell(new Paragraph("Guia Remision: ", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(1);
            cell.setPaddingBottom(15);
            table_datos.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoFactura().getGuiaRemision(), fuente), PADDING_NONE);
            cell.setColspan(1);
            cell.setPaddingBottom(15);
            cell.setPaddingLeft(-20);
            table_datos.addCell(cell);

            table_datos.setSpacingBefore(5);
            table_datos.setSpacingAfter(5);

            document.add(table_datos);

            /////////////////DETALLE TABLE 3

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

            //////////////////

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
                for (int pos = 0; pos < lstCamposAdicionales.size(); pos++) {
                    if (lstCamposAdicionales.get(pos).getNombre() != null && lstCamposAdicionales.get(pos).getValor() != null) {
                        //cell = generateCell(new Paragraph(lstCamposAdicionales.get(pos).getNombre() + ":", title), LEFT_PADDING_EMPRESA);
                        cell = new PdfPCell(new Paragraph(lstCamposAdicionales.get(pos).getNombre()+":", title));
                        cell.setBorder(0);
                        cell.setPaddingLeft(LEFT_PADDING_EMPRESA);

                        if (pos == lstCamposAdicionales.size() - 1)
                            cell.setPaddingBottom(10);
                        table_info_adic.addCell(cell);

                        //cell = generateCell(new Paragraph(lstCamposAdicionales.get(pos).getValor(), fuente), PADDING_NONE);
                        cell = new PdfPCell(new Paragraph(lstCamposAdicionales.get(pos).getValor(),fuente));
                        cell.setBorder(0);
                        cell.setPaddingLeft(-100);
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
                        nombrePago = FormaPagoSriEnum.getNombrePago(pag.getFormaPago());
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

            //

            cell = new PdfPCell();
            cell.addElement(tableIntermedia);
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setPaddingLeft(0);
            cell.setPaddingTop(0);
            table3.addCell(cell);

            /////

            PdfPTable table_totales = new PdfPTable(3);
            table_totales.setWidthPercentage(100);
            table_totales.setHorizontalAlignment(Element.ALIGN_RIGHT);

            InfoFactura infoFactura = factura.getInfoFactura();
            List<TotalImpuesto> totalImpuestos = infoFactura.getTotalImpuesto();
            TotalImpuesto subTotal0 = null;
            //TotalImpuesto subTotal12 = null;
            TotalImpuesto subTotal15 = null;
            TotalImpuesto subTotalNoObjeto = null;
            TotalImpuesto subTotalExenta = null;

            // IVA CODIGO 2
                // 0%  :     0
                // 12% :     2
                // 14% :     3
                // NOOBJETO: 6
                // EXCENTO:  7
                // IVA DIF:  8

            if (totalImpuestos != null && !totalImpuestos.isEmpty()) {
                subTotal0 = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("0") )
                        .findAny()
                        .orElse(null);

//                subTotal12 = totalImpuestos.stream()
//                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("2") )
//                        .findAny()
//                        .orElse(null);

                subTotal15 = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("4") )
                        .findAny()
                        .orElse(null);

                subTotalNoObjeto = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("6") )
                        .findAny()
                        .orElse(null);

                subTotalExenta = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("7") )
                        .findAny()
                        .orElse(null);
            }

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

            cell = new PdfPCell(new Phrase("SUBTOTAL 0%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            if (subTotal0 != null)
                cell = new PdfPCell(new Phrase(subTotal0.getBaseImponible(), fuente));
            else
                cell = new PdfPCell(new Phrase("0.00", fuente));

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);



//            cell = new PdfPCell(new Phrase("SUBTOTAL 12%:", title));
//            cell.setPaddingBottom(5);
//            cell.setColspan(2);
//            table_totales.addCell(cell);
//
//            if (subTotal12 != null)
//                cell = new PdfPCell(new Phrase(subTotal12.getBaseImponible(), fuente));
//            else
//                cell = new PdfPCell(new Phrase("0.00", fuente));
//
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table_totales.addCell(cell);
//
//            cell = new PdfPCell(new Phrase("IVA 12%:", title));
//            cell.setPaddingBottom(5);
//            cell.setColspan(2);
//            table_totales.addCell(cell);
//
//            if (subTotal12 != null)
//                cell = new PdfPCell(new Phrase(subTotal12.getValor(), fuente));
//            else
//                cell = new PdfPCell(new Phrase("0.00", fuente));
//
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table_totales.addCell(cell);

///////////////
            cell = new PdfPCell(new Phrase("SUBTOTAL 15%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            if (subTotal15 != null)
                cell = new PdfPCell(new Phrase(subTotal15.getBaseImponible(), fuente));
            else
                cell = new PdfPCell(new Phrase("0.00", fuente));

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("IVA 15%:", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);

            if (subTotal15 != null)
                cell = new PdfPCell(new Phrase(subTotal15.getValor(), fuente));
            else
                cell = new PdfPCell(new Phrase("0.00", fuente));

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);


            ////////////////////////////////


//            cell = new PdfPCell(new Phrase("ICE:", title));
//            cell.setPaddingBottom(5);
//            cell.setColspan(2);
//            table_totales.addCell(cell);
//            cell = new PdfPCell(new Phrase("0.00", fuente));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table_totales.addCell(cell);
//
//            cell = new PdfPCell(new Phrase("IRBPNR:", title));
//            cell.setPaddingBottom(5);
//            cell.setColspan(2);
//            table_totales.addCell(cell);
//            cell = new PdfPCell(new Phrase("0.00", fuente));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table_totales.addCell(cell);
//
//            cell = new PdfPCell(new Phrase("PROPINA:", title));
//            cell.setPaddingBottom(5);
//            cell.setColspan(2);
//            table_totales.addCell(cell);
//            cell = new PdfPCell(new Phrase(infoFactura.getPropina(), fuente));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table_totales.addCell(cell);

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
