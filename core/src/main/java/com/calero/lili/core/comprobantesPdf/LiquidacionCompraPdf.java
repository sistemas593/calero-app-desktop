package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.core.enums.FormaPagoSriEnum;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.DetalleImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.InfoLiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.ReembolsoDetalle;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Chunk;
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
import java.util.Objects;

@Service

public class LiquidacionCompraPdf {

    private final Float LEFT_PADDING_DOCUMENTO = 20f;
    private final Float LEFT_PADDING_EMPRESA = 10f;
    private final Float PADDING_NONE = 0f;

    public byte[] generarPdf(LiquidacionCompra factura, String AutorizacionSRI, String fechaAutorizacion, byte[] imageBytes) {

        try {

            Document document = new Document(PageSize.A4);
            document.setMargins(15, 15, 10, 10);
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
            celda = generateCell(new Paragraph("LIQUIDACIÓN DE COMPRA DE BIENES Y PRESTACIÓN DE SERVICIOS", bigFont), LEFT_PADDING_DOCUMENTO);
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

            String Ambiente = "";
            if (factura.getInfoTributaria().getAmbiente().equals("1")) {
                Ambiente = "PRUEBAS";
            }
            if (factura.getInfoTributaria().getAmbiente().equals("2")) {
                Ambiente = "PRODUCCIÓN";
            }

            celda = generateCell(new Paragraph(Ambiente, fuente), PADDING_NONE);
            table_datos_documento.addCell(celda);

            celda = generateCell(new Paragraph("EMISION:", title), LEFT_PADDING_DOCUMENTO);
            table_datos_documento.addCell(celda);

            String Emision = "";
            if (factura.getInfoTributaria().getTipoEmision().equals("1")) {
                Emision = "NORMAL";
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

            cell = generateCell(new Paragraph(factura.getInfoLiquidacionCompra().getDirEstablecimiento(), fuente), LEFT_PADDING_EMPRESA);
            cell.setColspan(4);
            cell.setPaddingTop(20);
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph("Contribuyente Especial:", title), LEFT_PADDING_EMPRESA);
            cell.setColspan(3);
            cell.setPaddingTop(15);
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoLiquidacionCompra().getContribuyenteEspecial(), fuente), LEFT_PADDING_EMPRESA);
            cell.setColspan(2);
            cell.setPaddingTop(20);
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph(("Obligado a llevar contabilidad:").toUpperCase(), title), LEFT_PADDING_EMPRESA);
            cell.setColspan(4);
            cell.setPaddingTop(15);
            cell.setPaddingBottom(5); // estaba 15 para no dejar linea
            table_datos_empresa.addCell(cell);

            cell = generateCell(new Paragraph(factura.getInfoLiquidacionCompra().getObligadoContabilidad(), fuente), LEFT_PADDING_EMPRESA);
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

            PdfPTable table_datos = new PdfPTable(1);
            table_datos.setWidthPercentage(100);
            table_datos.setTableEvent(new BorderEventWithoutRadius());

            Phrase pNombres = new Phrase();
            pNombres.add(new Chunk("Nombres y Apellidos: ", title));
            pNombres.add(new Chunk(factura.getInfoLiquidacionCompra().getRazonSocialProveedor(), fuente));
            cell = new PdfPCell(pNombres);
            cell.setBorder(0);
            cell.setPaddingLeft(LEFT_PADDING_EMPRESA);
            cell.setPaddingTop(10);
            table_datos.addCell(cell);

            Phrase pIdentificacion = new Phrase();
            pIdentificacion.add(new Chunk("Identificación: ", title));
            pIdentificacion.add(new Chunk(factura.getInfoLiquidacionCompra().getIdentificacionProveedor(), fuente));
            cell = new PdfPCell(pIdentificacion);
            cell.setBorder(0);
            cell.setPaddingLeft(LEFT_PADDING_EMPRESA);
            cell.setPaddingTop(10);
            table_datos.addCell(cell);

            Phrase pFecha = new Phrase();
            pFecha.add(new Chunk("Fecha: ", title));
            pFecha.add(new Chunk(factura.getInfoLiquidacionCompra().getFechaEmision(), fuente));
            cell = new PdfPCell(pFecha);
            cell.setBorder(0);
            cell.setPaddingLeft(LEFT_PADDING_EMPRESA);
            cell.setPaddingTop(10);
            table_datos.addCell(cell);

            Phrase pDireccion = new Phrase();
            pDireccion.add(new Chunk("Dirección: ", title));
            pDireccion.add(new Chunk(factura.getInfoLiquidacionCompra().getDireccionProveedor() != null ? factura.getInfoLiquidacionCompra().getDireccionProveedor() : "", fuente));
            cell = new PdfPCell(pDireccion);
            cell.setBorder(0);
            cell.setPaddingLeft(LEFT_PADDING_EMPRESA);
            cell.setPaddingTop(10);
            cell.setPaddingBottom(10);
            table_datos.addCell(cell);

            table_datos.setSpacingBefore(5);
            table_datos.setSpacingAfter(5);

            document.add(table_datos);


            /////////////////DETALLE TABLE 3

            PdfPTable table_detalle = new PdfPTable(10);
            table_detalle.setWidthPercentage(100);
            table_detalle.setTableEvent(new BorderEventWithoutRadius());

            cell = new PdfPCell(new Phrase("Cod.\nPrincipal", title));
            cell.setPaddingBottom(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Cod.\nAuxiliar", title));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Cantidad", title));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Descripción", title));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Detalle Adicional", title));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio Unitario", title));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Subsidio", title));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detalle.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio sin\nSubsidio", title));
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
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_detalle.addCell(cell);

                cell = new PdfPCell(new Phrase("", fuente));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_detalle.addCell(cell);

                cell = new PdfPCell(new Phrase(det.getCantidad(), fuente));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detalle.addCell(cell);

                cell = new PdfPCell(new Phrase(det.getDescripcion().toUpperCase(), fuente));
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

                cell = new PdfPCell(new Phrase(det.getPrecioUnitario(), fuente));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detalle.addCell(cell);

                cell = new PdfPCell(new Phrase("0.00", fuente));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detalle.addCell(cell);

                cell = new PdfPCell(new Phrase("0.00", fuente));
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
                        cell = new PdfPCell(new Paragraph(lstCamposAdicionales.get(pos).getNombre() + ":", title));
                        cell.setBorder(0);
                        cell.setPaddingLeft(LEFT_PADDING_EMPRESA);

                        if (pos == lstCamposAdicionales.size() - 1)
                            cell.setPaddingBottom(10);
                        table_info_adic.addCell(cell);

                        //cell = generateCell(new Paragraph(lstCamposAdicionales.get(pos).getValor(), fuente), PADDING_NONE);
                        cell = new PdfPCell(new Paragraph(lstCamposAdicionales.get(pos).getValor(), fuente));
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

            List<Pago> lstFormasPago = factura.getInfoLiquidacionCompra().getPago();

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

            InfoLiquidacionCompra infoFactura = factura.getInfoLiquidacionCompra();
            List<TotalImpuesto> totalImpuestos = infoFactura.getTotalImpuesto();

            // IVA CODIGO 2
            // 0%  :     0
            // 12% :     2
            // 14% :     3
            // 15% :     4
            // NOOBJETO: 6
            // EXCENTO:  7
            // IVA DIF:  8
            // ICE: codigo 3
            // IRBPNR: codigo 5

            TotalImpuesto subTotal0 = null;
            TotalImpuesto subTotal12 = null;
            TotalImpuesto subTotal15 = null;
            TotalImpuesto subTotalNoObjeto = null;
            TotalImpuesto subTotalExento = null;
            TotalImpuesto subTotalIce = null;
            TotalImpuesto subTotalIrbpnr = null;

            if (totalImpuestos != null && !totalImpuestos.isEmpty()) {
                subTotal0 = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("0"))
                        .findAny().orElse(null);
                subTotal12 = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("2"))
                        .findAny().orElse(null);
                subTotal15 = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("4"))
                        .findAny().orElse(null);
                subTotalNoObjeto = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("6"))
                        .findAny().orElse(null);
                subTotalExento = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("2") && imp.getCodigoPorcentaje().equals("7"))
                        .findAny().orElse(null);
                subTotalIce = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("3"))
                        .findAny().orElse(null);
                subTotalIrbpnr = totalImpuestos.stream()
                        .filter(imp -> imp.getCodigo().equals("5"))
                        .findAny().orElse(null);
            }

            cell = new PdfPCell(new Phrase("SUBTOTAL 15%", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subTotal15 != null ? subTotal15.getBaseImponible() : "0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("SUBTOTAL NO OBJETO DE IVA", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subTotalNoObjeto != null ? subTotalNoObjeto.getBaseImponible() : "0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("SUBTOTAL EXENTO DE IVA", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subTotalExento != null ? subTotalExento.getBaseImponible() : "0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("SUBTOTAL SIN IMPUESTOS", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(infoFactura.getTotalSinImpuestos(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("TOTAL DESCUENTO", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(infoFactura.getTotalDescuento(), fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("ICE", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subTotalIce != null ? subTotalIce.getValor() : "0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("IVA 15%", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subTotal15 != null ? subTotal15.getValor() : "0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("IRBPNR", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(subTotalIrbpnr != null ? subTotalIrbpnr.getValor() : "0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("PROPINA", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase("0.00", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell(new Phrase("VALOR TOTAL", title));
            cell.setPaddingBottom(5);
            cell.setColspan(2);
            table_totales.addCell(cell);
            cell = new PdfPCell(new Phrase(infoFactura.getImporteTotal() != null ? infoFactura.getImporteTotal() : "", fuente));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_totales.addCell(cell);

            cell = new PdfPCell();
            cell.addElement(table_totales);
            cell.setBorder(0);
            cell.setPaddingRight(0);

            table3.addCell(cell);


            document.add(table3);

            // SECCIÓN REEMBOLSO: solo cuando codDocReembolso = "41"
            String codDocReembolso = Objects.nonNull(infoFactura.getCodDocReembolso()) ? infoFactura.getCodDocReembolso() : "";
            if (codDocReembolso != null && codDocReembolso.equals("41")) {

                PdfPTable table_reembolso_header = new PdfPTable(2);
                table_reembolso_header.setWidthPercentage(100);
                table_reembolso_header.setSpacingBefore(5);
                table_reembolso_header.setTableEvent(new BorderEventWithoutRadius());

                cell = new PdfPCell(new Phrase("Documentos Sustentos", title));
                cell.setColspan(2);
                cell.setPaddingLeft(LEFT_PADDING_DOCUMENTO);
                table_reembolso_header.addCell(cell);

                cell = new PdfPCell(new Phrase("Código documento reembolso:", title));
                cell.setBorder(0);
                cell.setPaddingLeft(LEFT_PADDING_EMPRESA);
                table_reembolso_header.addCell(cell);

                cell = new PdfPCell(new Phrase(codDocReembolso, fuente));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_reembolso_header.addCell(cell);

                String totalCompReembolso = infoFactura.getTotalComprobantesReembolso();
                cell = new PdfPCell(new Phrase("Total Comprobantes Reembolso:", title));
                cell.setBorder(0);
                cell.setPaddingLeft(LEFT_PADDING_EMPRESA);
                cell.setPaddingBottom(5);
                table_reembolso_header.addCell(cell);

                cell = new PdfPCell(new Phrase(totalCompReembolso != null ? totalCompReembolso : "0.00", fuente));
                cell.setBorder(0);
                cell.setPaddingBottom(5);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_reembolso_header.addCell(cell);

                document.add(table_reembolso_header);

                List<ReembolsoDetalle> lstReembolso = factura.getReembolsoDetalle();
                if (lstReembolso != null && !lstReembolso.isEmpty()) {

                    Font fuenteReem = new Font();
                    fuenteReem.setSize(6);

                    PdfPTable table_reembolso = new PdfPTable(11);
                    table_reembolso.setWidthPercentage(100);
                    table_reembolso.setTableEvent(new BorderEventWithoutRadius());

                    // Cabecera
                    cell = new PdfPCell(new Phrase("RUC/Cédula", title));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("DocSerie", title));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("SecuenciaEmisión", title));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("Base 0%", title));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("Base Grav", title));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("IVA", title));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("No objeto", title));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("Exento", title));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_reembolso.addCell(cell);

                    cell = new PdfPCell(new Phrase("Total", title));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_reembolso.addCell(cell);

                    // Filas de detalle
                    for (ReembolsoDetalle rem : lstReembolso) {
                        String base0 = "0.00", noObjeto = "0.00", exento = "0.00";
                        double baseGravSum = 0, ivaSum = 0;

                        List<DetalleImpuesto> impuestos = rem.getDetalleImpuesto();
                        if (impuestos != null) {
                            for (DetalleImpuesto imp : impuestos) {
                                String codPct = imp.getCodigoPorcentaje();
                                if ("0".equals(codPct)) {
                                    base0 = imp.getBaseImponibleReembolso() != null ? imp.getBaseImponibleReembolso() : "0.00";
                                } else if ("4".equals(codPct) || "5".equals(codPct) || "8".equals(codPct)) {
                                    try { baseGravSum += Double.parseDouble(imp.getBaseImponibleReembolso()); } catch (Exception ignored) {}
                                    try { ivaSum += Double.parseDouble(imp.getImpuestoReembolso()); } catch (Exception ignored) {}
                                } else if ("6".equals(codPct)) {
                                    noObjeto = imp.getBaseImponibleReembolso() != null ? imp.getBaseImponibleReembolso() : "0.00";
                                } else if ("7".equals(codPct)) {
                                    exento = imp.getBaseImponibleReembolso() != null ? imp.getBaseImponibleReembolso() : "0.00";
                                }
                            }
                        }
                        String baseGrav = String.format("%.2f", baseGravSum);
                        String iva = String.format("%.2f", ivaSum);

                        double base0d = 0, noObjetod = 0, exentod = 0;
                        try { base0d = Double.parseDouble(base0); } catch (Exception ignored) {}
                        try { noObjetod = Double.parseDouble(noObjeto); } catch (Exception ignored) {}
                        try { exentod = Double.parseDouble(exento); } catch (Exception ignored) {}
                        double total = base0d + baseGravSum + ivaSum + noObjetod + exentod;
                        String totalStr = String.format("%.2f", total);

                        cell = new PdfPCell(new Phrase(rem.getIdentificacionProveedorReembolso() != null ? rem.getIdentificacionProveedorReembolso() : "", fuenteReem));
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(rem.getCodDocReembolso() != null ? rem.getCodDocReembolso() : "", fuenteReem));
                        table_reembolso.addCell(cell);

                        String estabPto = (rem.getEstabDocReembolso() != null ? rem.getEstabDocReembolso() : "")
                                + (rem.getPtoEmiDocReembolso() != null ? rem.getPtoEmiDocReembolso() : "");
                        cell = new PdfPCell(new Phrase(estabPto, fuenteReem));
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(rem.getSecuencialDocReembolso() != null ? rem.getSecuencialDocReembolso() : "", fuenteReem));
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(rem.getFechaEmisionDocReembolso() != null ? rem.getFechaEmisionDocReembolso() : "", fuenteReem));
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(base0, fuenteReem));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(baseGrav, fuenteReem));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(iva, fuenteReem));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(noObjeto, fuenteReem));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(exento, fuenteReem));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_reembolso.addCell(cell);

                        cell = new PdfPCell(new Phrase(totalStr, fuenteReem));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_reembolso.addCell(cell);
                    }

                    document.add(table_reembolso);
                }
            }

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
