package com.calero.lili.api.modCompras.service;

import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.enums.CodigoDocumento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.api.modCompras.builder.AtsBuilder;
import com.calero.lili.api.modCompras.dto.FilterDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.api.modCompras.projection.AtsProjection;
import com.calero.lili.api.modCompras.projection.AtsRetencionResumenProjection;
import com.calero.lili.api.modCompras.projection.AtsRetencionValoresProjection;
import com.calero.lili.api.modImpuestosAnexos.ats.DetalleAir;
import com.calero.lili.api.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.api.modImpuestosAnexos.ats.Iva;
import com.calero.lili.core.utils.DateUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtsService {


    private final CpImpuestosRepository cpImpuestosRepository;
    private final AtsBuilder atsBuilder;
    private final AdEmpresasRepository adEmpresasRepository;


    public void generateDocumentoAtsXml(Long idData, Long idEmpresa, FilterDto model, HttpServletResponse response) {

        try {

            List<DetalleCompras> detalleComprasList = new ArrayList<>();


            List<CpImpuestosEntity> comprasImpuesto = cpImpuestosRepository
                    .findAllByDates(idData, idEmpresa, model.getFechaRegistroDesde(), model.getFechaRegistroHasta());


            if (Objects.nonNull(comprasImpuesto)) {
                comprasImpuesto.forEach(cpImpuestosEntity -> {

                    DetalleCompras detalleCompra = atsBuilder.builderDetalleCompraLiquidacion(cpImpuestosEntity);
                    validateValores(detalleCompra, cpImpuestosEntity);
                    if (Objects.nonNull(cpImpuestosEntity.getCodigosEntity())) {
                        validateRetencionesIva(detalleCompra, cpImpuestosEntity.getCodigosEntity());
                        validateRetencionesRenta(detalleCompra, cpImpuestosEntity.getCodigosEntity(), cpImpuestosEntity.getRetencion());
                    }
                    detalleComprasList.add(detalleCompra);
                });
            }
            AdEmpresaEntity adEmpresaEntity = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException("No existe informacion de la empresa"));

            generarXml(atsBuilder.builderAtsWithRetencion(detalleComprasList, adEmpresaEntity,
                    getPeriodo(DateUtils.toString(model.getFechaRegistroDesde()))), response, model.getFechaRegistroHasta());

        } catch (Exception exception) {
            throw new GeneralException(exception.getMessage());
        }

    }


    private void validateValores(DetalleCompras detalleCompra,
                                 CpImpuestosEntity model) {

        for (CpImpuestosValoresEntity impuestos : model.getValoresEntity()) {

            if (impuestos.getCodigo().equals("2")
                    && impuestos.getCodigoPorcentaje().equals("0")) {

                detalleCompra.setBaseImponible(impuestos.getBaseImponible());

            }

            if (impuestos.getCodigo().equals("2")
                    && impuestos.getCodigoPorcentaje().equals("6")) {
                detalleCompra.setBaseNoGraIva(impuestos.getBaseImponible());
            }


            if (impuestos.getCodigo().equals("2")
                    && impuestos.getCodigoPorcentaje().equals("7")) {

                detalleCompra.setBaseImpExe(impuestos.getBaseImponible());

            }

            if (impuestos.getCodigo().equals("2")
                    && impuestos.getCodigoPorcentaje().equals("2")
                    || impuestos.getCodigoPorcentaje().equals("4")
                    || impuestos.getCodigoPorcentaje().equals("5")
                    || impuestos.getCodigoPorcentaje().equals("8")) {

                detalleCompra.setBaseImpGrav(impuestos.getBaseImponible());
                detalleCompra.setMontoIva(impuestos.getValor());
            }
        }

        validarSeccionPago(detalleCompra, model.getFormasPagoSri());

    }

    private void validarSeccionPago(DetalleCompras detalleCompra, List<FormasPagoSri> list) {

        BigDecimal valorPago = (new BigDecimal("500"));
        BigDecimal total = detalleCompra.getBaseNoGraIva()
                .add(detalleCompra.getBaseImponible())
                .add(detalleCompra.getBaseImpGrav())
                .add(detalleCompra.getBaseImpExe())
                .add(detalleCompra.getMontoIva())
                .add(detalleCompra.getMontoIce());

        if (total.compareTo(valorPago) > 0) {
            detalleCompra.setFormasDePago(atsBuilder.builderFormaDePago(list));
        }
    }


    private void validateRetencionesIva(DetalleCompras detalleCompra,
                                        List<CpImpuestosCodigosEntity> listCodigos) {

        if (!listCodigos.isEmpty()) {
            for (CpImpuestosCodigosEntity impuesto : listCodigos) {

                if (impuesto.getRetencion().getCodigo().equals("2")
                        && impuesto.getRetencionCodigos().getCodigoRetencion().equals("9")) {

                    detalleCompra.setValRetBien10(impuesto.getValorRetenido());

                }

                if (impuesto.getRetencion().getCodigo().equals("2")
                        && impuesto.getRetencionCodigos().getCodigoRetencion().equals("10")) {
                    detalleCompra.setValRetServ20(impuesto.getValorRetenido());
                }


                if (impuesto.getRetencion().getCodigo().equals("2")
                        && impuesto.getRetencionCodigos().getCodigoRetencion().equals("1")) {

                    detalleCompra.setValorRetBienes(impuesto.getValorRetenido());

                }


                if (impuesto.getRetencion().getCodigo().equals("2")
                        && impuesto.getRetencionCodigos().getCodigoRetencion().equals("11")) {

                    detalleCompra.setValRetServ50(impuesto.getValorRetenido());

                }

                if (impuesto.getRetencion().getCodigo().equals("2")
                        && impuesto.getRetencionCodigos().getCodigoRetencion().equals("2")) {

                    detalleCompra.setValorRetServicios(impuesto.getValorRetenido());

                }

                if (impuesto.getRetencion().getCodigo().equals("2")
                        && impuesto.getRetencionCodigos().getCodigoRetencion().equals("3")) {

                    detalleCompra.setValRetServ100(impuesto.getValorRetenido());

                }
            }
        }


    }

    private void validateRetencionesRenta(DetalleCompras detalleCompra,
                                          List<CpImpuestosCodigosEntity> listCodigos,
                                          CpRetencionesEntity retencion) {

        List<DetalleAir> detalleAirs = new ArrayList<>();
        listCodigos.forEach(item -> {
            if (item.getRetencion().getCodigo().equals("1")) {
                detalleAirs.add(atsBuilder.builderDetalleAir(item));
            }
        });

        if (!detalleAirs.isEmpty() && Objects.nonNull(retencion)) {
            detalleCompra.setDetalleAir(detalleAirs);
            detalleCompra.setEstabRetencion1(retencion.getSerieRetencion().substring(0, 3));
            detalleCompra.setPtoEmiRetencion1(retencion.getSerieRetencion().substring(3, 6));
            detalleCompra.setSecRetencion1(new java.math.BigInteger(retencion.getSecuencialRetencion()).toString());
            detalleCompra.setAutRetencion1(retencion.getNumeroAutorizacionRetencion());
            detalleCompra.setFechaEmiRet1(DateUtils.toString(retencion.getFechaEmisionRetencion()));
        }

    }

    private void generarXml(Iva iva, HttpServletResponse response, LocalDate fechaRegistroHasta) {
        try {

            String mes = fechaRegistroHasta.format(DateTimeFormatter.ofPattern("MM"));

            String fileName = "AT-" + mes + fechaRegistroHasta.getYear() + ".xml";
            String zipFileName = "AT-" + mes + fechaRegistroHasta.getYear() + ".zip";

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);

                JAXBContext context = JAXBContext.newInstance(Iva.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.encoding", "UTF-8");
                marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));

                OutputStream out = response.getOutputStream();
                marshaller.marshal(iva, zipOut);
                out.flush();

                zipOut.closeEntry();
            }


            System.out.println("XML Generado con exito a partir de objeto: xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void generarTalonResumen(Long idData, Long idEmpresa, FilterDto model, HttpServletResponse response) {

        try {
            List<AtsProjection> listPfds = null;
            response.setContentType("application/pdf");
            String filename = "talon_resumen_ " + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            listPfds = cpImpuestosRepository.obtenerResumenCompras(idData, idEmpresa,
                    model.getFechaRegistroDesde(),
                    model.getFechaRegistroHasta());

            AtsRetencionValoresProjection retencion = cpImpuestosRepository.obtenerResumenRetenciones(idData, idEmpresa,
                    model.getFechaRegistroDesde(),
                    model.getFechaRegistroHasta());

            AdEmpresaEntity adEmpresaEntity = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException("No existe informacion de la empresa"));


            List<AtsRetencionResumenProjection> listRetencionRenta = cpImpuestosRepository.
                    obtenerRetencionesRenta(idData, idEmpresa, model.getFechaRegistroDesde(),
                            model.getFechaRegistroHasta());


            if (listPfds.isEmpty()) {
                listPfds = validarValoresCero();
            }

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font valoresFont = new Font(Font.FontFamily.HELVETICA, 8);


            // Título
            Paragraph title = new Paragraph("TALÓN RESUMEN\nSERVICIO DE RENTAS INTERNAS\nANEXO TRANSACCIONAL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Datos Generales TODO CONOCER DE DONDE SALDRAN ESTOS DATOS
            document.add(new Paragraph("Nombre: " + adEmpresaEntity.getRazonSocial(), boldFont));
            document.add(new Paragraph("RUC: " + adEmpresaEntity.getRuc(), boldFont));
            document.add(new Paragraph("Periodo: " + getPeriodo(DateUtils.toString(model.getFechaRegistroDesde())), boldFont));
            document.add(new Paragraph("Fecha de Generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), boldFont));
            document.add(Chunk.NEWLINE);

            // Texto de declaración
            document.add(new Paragraph("Certifico que la información contenida en el medio magnético del Anexo Transaccional es fiel reflejo del siguiente reporte:", normalFont));
            document.add(Chunk.NEWLINE);

            // COMPRAS
            document.add(new Paragraph("COMPRAS", boldFont));
            PdfPTable comprasTable = new PdfPTable(7);
            comprasTable.setWidthPercentage(100);
            comprasTable.setSpacingBefore(10f);

            String[] comprasHeader = {"Cod. Transacción", "Tipo Doc.", "No. Registros", "BI tarifa 0%", "BI tarifa diferente 0%", "BI No Objeto IVA", "Valor IVA"};
            for (String header : comprasHeader) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                comprasTable.addCell(cell);
            }

            BigDecimal total0 = BigDecimal.ZERO, totalDif = BigDecimal.ZERO, totalNoObj = BigDecimal.ZERO, totalIVA = BigDecimal.ZERO;

            for (AtsProjection factura : listPfds) {

                PdfPCell cellCodigo = new PdfPCell(new Phrase(factura.getCodigoDocumento(), valoresFont));
                cellCodigo.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellCodigo);

                PdfPCell cellNombreDocumento = new PdfPCell(new Phrase(CodigoDocumento.fromCodigo(factura.getCodigoDocumento()).getNombreDocumento(), valoresFont));
                cellNombreDocumento.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellNombreDocumento);

                PdfPCell cellTotalRegistros = new PdfPCell(new Phrase(factura.getTotalRegistros().toString(), valoresFont));
                cellTotalRegistros.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellTotalRegistros);

                PdfPCell cellBiTarifaCero = new PdfPCell(new Phrase(factura.getBiTarifaCero().toString(), valoresFont));
                cellBiTarifaCero.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellBiTarifaCero);

                PdfPCell cellBiBaseDiferenteCero = new PdfPCell(new Phrase(factura.getBiBaseDiferenteCero().toString(), valoresFont));
                cellBiBaseDiferenteCero.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellBiBaseDiferenteCero);

                PdfPCell cellBaseNoObjectoIva = new PdfPCell(new Phrase(factura.getBiBaseNoObjectoIva().toString(), valoresFont));
                cellBaseNoObjectoIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellBaseNoObjectoIva);

                PdfPCell cellValorIva = new PdfPCell(new Phrase(factura.getValorIva().toString(), valoresFont));
                cellValorIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
                comprasTable.addCell(cellValorIva);

                total0 = total0.add(factura.getBiTarifaCero());
                totalDif = totalDif.add(factura.getBiBaseDiferenteCero());
                totalNoObj = totalNoObj.add(factura.getBiBaseNoObjectoIva());
                totalIVA = totalIVA.add(factura.getValorIva());
            }

            // Totales
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL:", boldFont));
            totalCell.setColspan(3);
            comprasTable.addCell(totalCell);

            PdfPCell cellTotalCero = new PdfPCell(new Phrase(total0.toString(), valoresFont));
            cellTotalCero.setHorizontalAlignment(Element.ALIGN_RIGHT);
            comprasTable.addCell(cellTotalCero);

            PdfPCell cellTotalDif = new PdfPCell(new Phrase(totalDif.toString(), valoresFont));
            cellTotalDif.setHorizontalAlignment(Element.ALIGN_RIGHT);
            comprasTable.addCell(cellTotalDif);

            PdfPCell cellTotalNoObj = new PdfPCell(new Phrase(totalNoObj.toString(), valoresFont));
            cellTotalNoObj.setHorizontalAlignment(Element.ALIGN_RIGHT);
            comprasTable.addCell(cellTotalNoObj);

            PdfPCell cellTotalIVA = new PdfPCell(new Phrase(totalIVA.toString(), valoresFont));
            cellTotalIVA.setHorizontalAlignment(Element.ALIGN_RIGHT);
            comprasTable.addCell(cellTotalIVA);

            document.add(comprasTable);
            document.add(Chunk.NEWLINE);

            // RETENCIONES RENTA

            if (Objects.nonNull(listRetencionRenta)) {

                // Título principal
                document.add(new Paragraph("RESUMEN DE RETENCIONES - AGENTE DE RETENCION", boldFont));
                document.add(Chunk.NEWLINE);

                // Subtítulo
                document.add(new Paragraph("RETENCION EN LA FUENTE DE IMPUESTO A LA RENTA", boldFont));

                // TABLA
                PdfPTable rentaTable = new PdfPTable(5);
                rentaTable.setWidthPercentage(100);
                rentaTable.setSpacingBefore(10f);
                rentaTable.setWidths(new float[]{0.5f, 4f, 1f, 1f, 1f});

                String[] rentaHeader = {"Cod.", "Concepto de Retención", "No. Registros", "Base Imponible", "Valor Retenido"};
                for (String header : rentaHeader) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    rentaTable.addCell(cell);
                }

                BigDecimal totalBase = BigDecimal.ZERO;
                BigDecimal totalRetenido = BigDecimal.ZERO;

                for (AtsRetencionResumenProjection item : listRetencionRenta) {
                    rentaTable.addCell(new Phrase(item.getCodigoRetencion(), valoresFont));
                    rentaTable.addCell(new Phrase(item.getConceptoRetencion(), valoresFont));

                    PdfPCell cellRegistros = new PdfPCell(new Phrase(item.getRegistros().toString(), valoresFont));
                    cellRegistros.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    rentaTable.addCell(cellRegistros);

                    PdfPCell cellBase = new PdfPCell(new Phrase(item.getBaseImponible().toString(), valoresFont));
                    cellBase.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    rentaTable.addCell(cellBase);

                    PdfPCell cellRetenido = new PdfPCell(new Phrase(item.getValorRetenido().toString(), valoresFont));
                    cellRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    rentaTable.addCell(cellRetenido);

                    // Acumular solo base y valor retenido
                    totalBase = totalBase.add(item.getBaseImponible());
                    totalRetenido = totalRetenido.add(item.getValorRetenido());
                }

                PdfPCell emptyTotalCell = new PdfPCell(new Phrase("TOTAL:", boldFont));
                emptyTotalCell.setColspan(3);
                emptyTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rentaTable.addCell(emptyTotalCell);

                PdfPCell totalBaseCell = new PdfPCell(new Phrase(totalBase.toString(), valoresFont));
                totalBaseCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rentaTable.addCell(totalBaseCell);

                PdfPCell totalRetenidoCell = new PdfPCell(new Phrase(totalRetenido.toString(), valoresFont));
                totalRetenidoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rentaTable.addCell(totalRetenidoCell);

                document.add(rentaTable);
                document.add(Chunk.NEWLINE);
            }

            // RETENCIONES
            document.add(new Paragraph("RETENCION EN LA FUENTE DE IVA", boldFont));
            PdfPTable retencionTable = new PdfPTable(3);
            retencionTable.setWidthPercentage(100);
            retencionTable.setSpacingBefore(10f);

            String[] retencionHeader = {"Operación", "Concepto de Retención", "Valor Retenido"};
            for (String header : retencionHeader) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                retencionTable.addCell(cell);
            }


            String[] conceptos = {
                    "Retencion IVA 10%",
                    "Retencion IVA 20%",
                    "Retencion IVA 30%",
                    "Retencion IVA 50%",
                    "Retencion IVA 70%",
                    "Retencion IVA 100%",
                    "Retencion IVA NC"
            };

            BigDecimal[] valoresRetenciones = {
                    retencion.getRetencionValor10(),
                    retencion.getRetencionValor20(),
                    retencion.getRetencionValor30(),
                    retencion.getRetencionValor50(),
                    retencion.getRetencionValor70(),
                    retencion.getRetencionValor100(),
                    BigDecimal.ZERO
            };

            BigDecimal totalRetenido = BigDecimal.ZERO;

            for (int i = 0; i < conceptos.length; i++) {
                // Columna 1: Operación fija "COMPRA"
                PdfPCell operacion = new PdfPCell(new Phrase("COMPRA", valoresFont));
                operacion.setHorizontalAlignment(Element.ALIGN_LEFT);
                retencionTable.addCell(operacion);

                // Columna 2: Concepto
                PdfPCell concepto = new PdfPCell(new Phrase(conceptos[i], valoresFont));
                concepto.setHorizontalAlignment(Element.ALIGN_LEFT);
                retencionTable.addCell(concepto);

                // Columna 3: Valor
                BigDecimal valor = valoresRetenciones[i] != null ? valoresRetenciones[i] : BigDecimal.ZERO;
                PdfPCell valorCell = new PdfPCell(new Phrase(valor.toString(), valoresFont));
                valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                retencionTable.addCell(valorCell);

                totalRetenido = totalRetenido.add(valor);
            }

            PdfPCell totalRetCell = new PdfPCell(new Phrase("TOTAL:", boldFont));
            totalRetCell.setColspan(2);
            retencionTable.addCell(totalRetCell);

            PdfPCell valorTotalCell = new PdfPCell(new Phrase(totalRetenido.toString(), valoresFont));
            valorTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            retencionTable.addCell(valorTotalCell);

            document.add(retencionTable);
            document.add(Chunk.NEWLINE);

            // Declaración
            Paragraph declaracion = new Paragraph("Declaro que los datos contenidos en este anexo son verdaderos, por lo que asumo la responsabilidad correspondiente, de acuerdo a lo establecido en el Art. 101 de la Codificación de la Ley de Régimen Tributario Interno", normalFont);
            declaracion.setSpacingBefore(10f);
            document.add(declaracion);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Firma
            PdfPTable firmaTable = new PdfPTable(2);
            firmaTable.setWidthPercentage(100);
            firmaTable.addCell(new Phrase("__________________________\nFirma del Contador", normalFont));
            firmaTable.addCell(new Phrase("__________________________\nFirma del Representante Legal", normalFont));
            document.add(firmaTable);

            document.close();


        } catch (Exception exception) {
            log.info(exception.getMessage());
            throw new GeneralException(exception.getMessage());
        }

    }

    private List<AtsProjection> validarValoresCero() {

        List<AtsProjection> atsProjections = new ArrayList<>();

        AtsProjection atsProjection = new AtsProjection() {
            @Override
            public String getCodigoDocumento() {
                return "01";
            }

            @Override
            public BigDecimal getBiTarifaCero() {
                return new BigDecimal("0.00");
            }

            @Override
            public BigDecimal getBiBaseDiferenteCero() {
                return new BigDecimal("0.00");
            }

            @Override
            public BigDecimal getBiBaseNoObjectoIva() {
                return new BigDecimal("0.00");
            }

            @Override
            public BigDecimal getValorIva() {
                return new BigDecimal("0.00");
            }

            @Override
            public Integer getTotalRegistros() {
                return 0;
            }
        };

        atsProjections.add(atsProjection);
        return atsProjections;
    }

    private String getPeriodo(String fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(fecha, formatter);
        return String.format("%02d-%d", date.getMonthValue(), date.getYear());
    }

}
