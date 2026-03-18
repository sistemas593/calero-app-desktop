package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosDetalleEntity;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosRepository;
import com.calero.lili.core.utils.DateUtils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CnAsientosComprobanteDiario {


    private final CnAsientosRepository cnAsientosRepository;
    private final AdEmpresasRepository adEmpresasRepository;


    /**
     * Genera y envía por la respuesta HTTP un PDF con el comprobante diario del asiento especificado.
     * <p>
     * El método realiza los siguientes pasos:
     * - Recupera el asiento por `idData`, `idEmpresa` y `idAsiento`.
     * - Valida que exista el asiento y la empresa asociada.
     * - Construye el PDF (encabezado, información, detalle, totales y firmas).
     * - Escribe el PDF en el `HttpServletResponse` con `Content-Type: application/pdf`
     * y la cabecera `Content-Disposition` para forzar la descarga.
     *
     * @param idData    identificador lógico del entorno/de la data
     * @param idEmpresa identificador de la empresa
     * @param idAsiento UUID del asiento contable a exportar
     * @param response  HttpServletResponse donde se escribe el PDF resultante
     * @throws GeneralException si no existe el asiento o la empresa, o si ocurre un error durante la generación/escritura del PDF
     */
    public void generateComprobanteDiario(Long idData, Long idEmpresa, UUID idAsiento,
                                          HttpServletResponse response) {

        CnAsientosEntity asiento = cnAsientosRepository.findByIdEntity(idData, idEmpresa, idAsiento, null, null)
                .orElseThrow(() -> new GeneralException("El asiento al que se hace referencia no existe"));

        try {


            response.setContentType("application/pdf");
            String filename = "Comprobante_Diario_ " + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


            if (!asiento.getDetalleEntity().isEmpty()) {

                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Font responseTitleFont = new Font(Font.FontFamily.HELVETICA, 16);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
                Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Font smallFont = new Font(Font.FontFamily.HELVETICA, 7);
                Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);


                AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                        .orElseThrow(() -> new GeneralException("Empresa no existe"));


                // -- ENCABEZADO --
                Paragraph nombreEmpresa = new Paragraph(empresa.getRazonSocial().toUpperCase(), titleFont);
                nombreEmpresa.setAlignment(Element.ALIGN_CENTER);
                document.add(nombreEmpresa);
                document.add(Chunk.NEWLINE);


                // --- ENCABEZADO DE TÍTULO ---
                PdfPTable encabezadoTitulo = new PdfPTable(2);
                encabezadoTitulo.setWidthPercentage(100);
                encabezadoTitulo.setWidths(new float[]{50, 50});


                PdfPCell datosIzq = new PdfPCell(new Phrase("Comprobante de diario", titleFont));
                datosIzq.setBorder(Rectangle.NO_BORDER);
                datosIzq.setHorizontalAlignment(Element.ALIGN_LEFT);
                encabezadoTitulo.addCell(datosIzq);

                Paragraph datosDerecha = new Paragraph();
                datosDerecha.add(new Chunk("Tipo: ", titleFont));
                datosDerecha.add(new Chunk(asiento.getTipoAsiento().name(), responseTitleFont));
                datosDerecha.add(new Chunk("   ", boldFont));
                datosDerecha.add(new Chunk("No: ", titleFont));
                datosDerecha.add(new Chunk(asiento.getNumeroAsiento(), responseTitleFont));


                PdfPCell datosDerechaTitulo = new PdfPCell(datosDerecha);
                datosDerechaTitulo.setBorder(Rectangle.NO_BORDER);
                datosDerechaTitulo.setHorizontalAlignment(Element.ALIGN_LEFT);
                encabezadoTitulo.addCell(datosDerechaTitulo);

                document.add(encabezadoTitulo);
                document.add(Chunk.NEWLINE);


                // --- BLOQUE DE INFORMACIÓN  ---

                PdfPTable infoTabla = new PdfPTable(1);
                infoTabla.setWidthPercentage(100);

                PdfPCell infoCelda = new PdfPCell();
                infoCelda.setBorder(Rectangle.NO_BORDER);
                infoCelda.setHorizontalAlignment(Element.ALIGN_LEFT);


                Paragraph pFecha = new Paragraph();
                pFecha.add(new Chunk("Fecha: ", boldFont));
                pFecha.add(new Chunk(DateUtils.obtenerFormatoFechaLetras(asiento.getFechaAsiento()), normalFont));
                infoCelda.addElement(pFecha);

                Paragraph pConcepto = new Paragraph();
                pConcepto.add(new Chunk("Concepto: ", boldFont));
                pConcepto.add(new Chunk(asiento.getConcepto(), normalFont));
                infoCelda.addElement(pConcepto);

                Paragraph pNombre = new Paragraph();
                pNombre.add(new Chunk("Nombre: ", boldFont));
                pNombre.add(new Chunk(getTercero(asiento), normalFont));
                infoCelda.addElement(pNombre);

                infoTabla.addCell(infoCelda);
                document.add(infoTabla);
                document.add(Chunk.NEWLINE);

                // -- DETALLES --

                PdfPTable tablaDetalle = new PdfPTable(6);
                tablaDetalle.setWidthPercentage(100);
                tablaDetalle.setWidths(new float[]{10, 35, 5, 13, 10, 10});

                String[] headers = {"Código", "Cuenta", "TD", "Número", "Debe", "Haber"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaDetalle.addCell(cell);
                }

                for (CnAsientosDetalleEntity detalle : asiento.getDetalleEntity()) {

                    PdfPCell celdaCodigo = new PdfPCell(new Phrase(detalle.getCuenta().getCodigoCuenta(), smallFont));
                    celdaCodigo.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tablaDetalle.addCell(celdaCodigo);

                    PdfPCell celdaCuenta = new PdfPCell(new Phrase(detalle.getCuenta().getCuenta(), smallFont));
                    celdaCuenta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tablaDetalle.addCell(celdaCuenta);

                    PdfPCell celdaTD = new PdfPCell(new Phrase(detalle.getTipoDocumento(), smallFont));
                    celdaTD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaDetalle.addCell(celdaTD);


                    PdfPCell celdaNumero = new PdfPCell(new Phrase(detalle.getNumeroDocumento(), smallFont));
                    celdaNumero.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaDetalle.addCell(celdaNumero);

                    PdfPCell celdaDebe = new PdfPCell(new Phrase(detalle.getDebe().toString(), smallFont));
                    celdaDebe.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaDetalle.addCell(celdaDebe);


                    PdfPCell celdaHaber = new PdfPCell(new Phrase(detalle.getHaber().toString(), smallFont));
                    celdaHaber.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaDetalle.addCell(celdaHaber);

                }

                // -- TOTALES --

                PdfPCell total = new PdfPCell(new Phrase("Totales:", smallBoldFont));
                total.setColspan(4);
                total.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaDetalle.addCell(total);


                PdfPCell debeTotalCell = new PdfPCell(new Phrase(getDebeTotal(asiento.getDetalleEntity()), smallFont));
                debeTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);


                PdfPCell haberTotalCell = new PdfPCell(new Phrase(getHaberTotal(asiento.getDetalleEntity()), smallFont));
                haberTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);


                tablaDetalle.addCell(debeTotalCell);
                tablaDetalle.addCell(haberTotalCell);

                document.add(tablaDetalle);
                document.add(Chunk.NEWLINE);


                // -- FIRMAS --

                PdfPTable firmas = new PdfPTable(4);
                firmas.setWidthPercentage(100);
                firmas.setWidths(new float[]{25, 25, 25, 25});

                String[] roles = {"Elaborado", "Revisado", "Aprobado", "Contabilizado"};
                for (String rol : roles) {
                    PdfPCell cell = new PdfPCell(new Phrase(rol, smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(40);
                    firmas.addCell(cell);
                }

                document.add(firmas);
                document.close();

            }


        } catch (Exception exception) {
            log.info(exception.getMessage());
            throw new GeneralException(exception.getMessage());
        }

    }


    /**
     * Obtiene el nombre del tercero asociado al asiento.
     * <p>
     * Devuelve el valor del tercero si está presente en la entidad del asiento;
     * en caso contrario retorna cadena vacía.
     *
     * @param asiento entidad del asiento que puede contener la referencia al tercero
     * @return nombre del tercero o cadena vacía cuando no existe
     */
    private String getTercero(CnAsientosEntity asiento) {
        if (Objects.nonNull(asiento.getTercero())) return asiento.getTercero().getTercero();
        return "";
    }

    /**
     * Calcula el total de la columna `haber` en la lista de detalles del asiento.
     * <p>
     * Suma únicamente las entradas no nulas y devuelve el resultado como `String`
     * usando `BigDecimal.toString()` (sin formateo adicional).
     *
     * @param detalleEntity lista de líneas/detalles del asiento
     * @return suma total del `haber` como String
     */
    private String getHaberTotal(List<CnAsientosDetalleEntity> detalleEntity) {
        BigDecimal totalHaber = detalleEntity.stream()
                .map(CnAsientosDetalleEntity::getHaber)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalHaber.toString();
    }

    /**
     * Calcula el total de la columna `debe` en la lista de detalles del asiento.
     * <p>
     * Suma únicamente las entradas no nulas y devuelve el resultado como `String`
     * usando `BigDecimal.toString()` (sin formateo adicional).
     *
     * @param detalleEntity lista de líneas/detalles del asiento
     * @return suma total del `debe` como String
     */
    private String getDebeTotal(List<CnAsientosDetalleEntity> detalleEntity) {
        BigDecimal totalDebe = detalleEntity.stream()
                .map(CnAsientosDetalleEntity::getDebe)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalDebe.toString();
    }
}
