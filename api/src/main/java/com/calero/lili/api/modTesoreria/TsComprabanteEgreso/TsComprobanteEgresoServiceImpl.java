package com.calero.lili.api.modTesoreria.TsComprabanteEgreso;

import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosRepository;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.builder.TsComprobanteEgresoBuilder;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.RequestComprobantesEgresoDto;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.ResponseComprobantesEgresoDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.ComprobanteIngresoFilterDto;
import com.calero.lili.api.utils.validaciones.ValidarValoresComprobantesPdf;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TsComprobanteEgresoServiceImpl {


    private final CnAsientosRepository cnAsientosRepository;
    private final TsComprobanteEgresoBuilder tsComprobanteEgresoBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final AdEmpresasRepository adEmpresasRepository;
    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;
    private final GeTercerosRepository geTercerosRepository;


    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, UUID idComprobanteEgreso, RequestComprobantesEgresoDto request, String usuario) {


        Optional<CnAsientosEntity> existeComprobante = cnAsientosRepository
                .findByNumeroAsiento(idData, idEmpresa, request.getNumeroComprobanteEgreso(), request.getTipoAsiento());

        if (existeComprobante.isPresent()) {
            throw new GeneralException(MessageFormat.format("El comprobante ya existe con sucursal: {0}," +
                            " serie: {1}, numero comprobante: {2}",
                    request.getSucursal(), request.getCodigoSerie(), request.getNumeroComprobanteEgreso()));
        }

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        CnAsientosEntity cnAsientos = tsComprobanteEgresoBuilder
                .builderEgresoEntity(request, idData, idEmpresa, idComprobanteEgreso);
        cnAsientos.setTercero(tercero);
        cnAsientos.setCreatedBy(usuario);
        cnAsientos.setCreatedDate(LocalDateTime.now());
        CnAsientosEntity entidad = cnAsientosRepository.save(cnAsientos);


        return responseApiBuilder.builderResponse(entidad.getIdAsiento().toString());
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idComprobante, RequestComprobantesEgresoDto request, String usuario) {


        CnAsientosEntity entity = cnAsientosRepository.findByIdEntity(idData, idEmpresa, idComprobante)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El comprobante no existe: {0}",
                        idComprobante)));


        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        entity.setSucursal(request.getSucursal());
        entity.setTipoAsiento(request.getTipoAsiento());
        entity.setNumeroAsiento(request.getNumeroComprobanteEgreso());
        entity.setFechaAsiento(DateUtils.toLocalDate(request.getFechaComprobante()));
        entity.setConcepto(request.getConcepto());
        entity.setValor(request.getValor());
        entity.setCodigoSerie(request.getCodigoSerie());
        entity.setObservaciones(request.getObservaciones());
        entity.setNombre(request.getNombre());
        entity.setTercero(tercero);
        entity.setModifiedBy(usuario);
        entity.setModifiedDate(LocalDateTime.now());

        return responseApiBuilder.builderResponse(cnAsientosRepository
                .save(entity).getIdAsiento().toString());

    }

    public void delete(Long idData, Long idEmpresa, UUID idComprobante, String usuario) {
        CnAsientosEntity existeComprobante = cnAsientosRepository.findByIdEntity(idData, idEmpresa, idComprobante)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El comprobante no existe: {0}",
                        idComprobante)));

        existeComprobante.setDeletedBy(usuario);
        existeComprobante.setDeletedDate(LocalDateTime.now());
        existeComprobante.setDelete(Boolean.TRUE);
        cnAsientosRepository.save(existeComprobante);
    }


    public ResponseComprobantesEgresoDto findById(Long idData, Long idEmpresa, UUID idComprobante) {
        return tsComprobanteEgresoBuilder.builderResponse(cnAsientosRepository.findByIdEntity(idData, idEmpresa, idComprobante)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El comprobante no existe: {0}",
                        idComprobante))));
    }

    public PaginatedDto<ResponseComprobantesEgresoDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                       ComprobanteIngresoFilterDto filter, Pageable pageable) {


        Page<CnAsientosEntity> page = cnAsientosRepository
                .findAllPaginate(idData, idEmpresa, filter.getSucursal(),
                        filter.getFechaComprobanteDesde(), filter.getFechaComprobanteHasta(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaGetListDto>();

        List<ResponseComprobantesEgresoDto> dtoList = page.stream().map(tsComprobanteEgresoBuilder::builderResponse).toList();
        paginatedDto.setContent(dtoList);
        Paginator paginated = new Paginator();
        paginated.setTotalElements(page.getTotalElements());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setNumberOfElements(page.getNumberOfElements());
        paginated.setSize(page.getSize());
        paginated.setFirst(page.isFirst());
        paginated.setLast(page.isLast());
        paginated.setPageNumber(page.getPageable().getPageNumber());
        paginated.setPageSize(page.getPageable().getPageSize());
        paginated.setEmpty(page.isEmpty());
        paginated.setNumber(page.getNumber());
        paginated.setNumber(page.getNumber());
        paginatedDto.setPaginator(paginated);
        return paginatedDto;

    }


    public void generateComprobanteEgresoPdf(Long idData, Long idEmpresa,
                                             UUID idComprobanteEgreso, HttpServletResponse response) {


        CnAsientosEntity comprobante = cnAsientosRepository
                .findByIdEntity(idData, idEmpresa, idComprobanteEgreso)
                .orElseThrow(() -> new GeneralException("No existe comprobante de egreso"));


        try {

            response.setContentType("application/pdf");
            String filename = "Comprobante_Egreso_ " + LocalDate.now().toString().replace("-", "") + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


            if (Objects.nonNull(comprobante)) {

                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
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

                PdfPTable datosEncabezado = new PdfPTable(3);
                datosEncabezado.setWidthPercentage(100);
                datosEncabezado.setWidths(new float[]{40, 20, 40}); // proporción izquierda-centro-derecha


                datosEncabezado.addCell(getCell("Comprobante de Egreso", titleFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));


                String anio = String.valueOf(comprobante.getFechaAsiento().getYear());
                datosEncabezado.addCell(getCell(anio, titleFont, Element.ALIGN_CENTER, Rectangle.NO_BORDER));


                datosEncabezado.addCell(getCell(comprobante.getNumeroAsiento(), titleFont, Element.ALIGN_RIGHT, Rectangle.NO_BORDER));


                document.add(datosEncabezado);


                document.add(Chunk.NEWLINE);

                // --- BLOQUE DE INFORMACIÓN  ---

                PdfPTable datosGenerales = new PdfPTable(6);
                datosGenerales.setWidthPercentage(100);
                datosGenerales.setWidths(new float[]{10, 20, 10, 40, 10, 10});

                datosGenerales.addCell(getCell("Ciudad:", boldFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                datosGenerales.addCell(getCell(getCiudad(comprobante), normalFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));

                datosGenerales.addCell(getCell("Fecha:", boldFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                datosGenerales.addCell(getCell(DateUtils.obtenerFormatoFechaLetras(comprobante.getFechaAsiento()), normalFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));

                datosGenerales.addCell(getCell("Valor:", boldFont, Element.ALIGN_RIGHT, Rectangle.NO_BORDER));
                datosGenerales.addCell(getCell(validarValoresComprobantesPdf.getValor(comprobante.getValor()), boldFont, Element.ALIGN_RIGHT, Rectangle.NO_BORDER));

                document.add(datosGenerales);

                PdfPTable filaConcepto = new PdfPTable(2);
                filaConcepto.setWidthPercentage(100);
                filaConcepto.setWidths(new float[]{15, 85});
                filaConcepto.addCell(getCell("Concepto:", boldFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                filaConcepto.addCell(getCell(comprobante.getConcepto(), normalFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                document.add(filaConcepto);

                PdfPTable filaBeneficiario = new PdfPTable(2);
                filaBeneficiario.setWidthPercentage(100);
                filaBeneficiario.setWidths(new float[]{15, 85});
                filaBeneficiario.addCell(getCell("Beneficiario:", boldFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                filaBeneficiario.addCell(getCell(getBeneficiario(comprobante), normalFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                document.add(filaBeneficiario);


                document.add(Chunk.NEWLINE);


                // -- DETALLES --

                // TODO VALIDAR DETALLES Y VALOR DE BANCO

             /*   PdfPTable tablaDetalle = new PdfPTable(3);
                tablaDetalle.setWidthPercentage(100);
                tablaDetalle.setWidths(new float[]{15, 15, 15});

                String[] headers = {"TD", "Número", "Valor"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaDetalle.addCell(cell);
                }

                for (TsBancosMovimentosEntity bancosMovimentos : comprobante.getBancosMovimientosList()) {

                    PdfPCell celdaTd = new PdfPCell(new Phrase(bancosMovimentos.getTipoDocumento(), smallFont));
                    celdaTd.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tablaDetalle.addCell(celdaTd);

                    PdfPCell celdaNumeroDocumento = new PdfPCell(new Phrase(bancosMovimentos.getNumeroDocumento(), smallFont));
                    celdaNumeroDocumento.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tablaDetalle.addCell(celdaNumeroDocumento);

                    PdfPCell celdaValor = new PdfPCell(new Phrase(validarValoresComprobantesPdf.getValor(bancosMovimentos.getValor()), smallFont));
                    celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaDetalle.addCell(celdaValor);
                }
*/

                // -- TOTALES --

                PdfPCell total = new PdfPCell(new Phrase("Total:", smallBoldFont));
                total.setColspan(2);
                total.setHorizontalAlignment(Element.ALIGN_RIGHT);

                // TODO VALIDAR DETALLES Y VALOR DE BANCO

                // tablaDetalle.addCell(total);

                PdfPCell totalCelda = new PdfPCell(new Phrase(comprobante.getValor().toString(), smallFont));
                totalCelda.setHorizontalAlignment(Element.ALIGN_RIGHT);

                // TODO VALIDAR DETALLES Y VALOR DE BANCO

                // tablaDetalle.addCell(totalCelda);
                //document.add(tablaDetalle);

                document.add(Chunk.NEWLINE);


                // -- OBSERVACIONES --

                PdfPTable observacionTable = new PdfPTable(2);
                observacionTable.setWidthPercentage(100);
                observacionTable.setWidths(new float[]{17, 83});

                PdfPCell observacionLabel = new PdfPCell(new Phrase("Observación:", boldFont));
                observacionLabel.setBorder(Rectangle.NO_BORDER);
                observacionLabel.setPaddingBottom(5f);
                observacionTable.addCell(observacionLabel);

                PdfPCell observacionContent = new PdfPCell(new Phrase(
                        Objects.nonNull(comprobante.getObservaciones()) ? comprobante.getObservaciones() : "",
                        normalFont));
                observacionContent.setMinimumHeight(20f);
                observacionContent.setBorder(Rectangle.NO_BORDER);
                observacionTable.addCell(observacionContent);

                document.add(observacionTable);
                document.add(Chunk.NEWLINE);


                PdfPTable letrasTable = new PdfPTable(2);
                letrasTable.setWidthPercentage(100);
                letrasTable.setWidths(new float[]{15, 85});

                letrasTable.addCell(getCell("En Letras:", boldFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                letrasTable.addCell(getCell(validarValoresComprobantesPdf.convertirNumeroALetras(comprobante.getValor()), normalFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));

                document.add(letrasTable);
                document.add(Chunk.NEWLINE);


                PdfPTable firmaTable = new PdfPTable(1);
                firmaTable.setWidthPercentage(40);
                firmaTable.setHorizontalAlignment(Element.ALIGN_RIGHT);


                PdfPCell linea = new PdfPCell(new Phrase(" "));
                linea.setBorder(Rectangle.BOTTOM);
                linea.setFixedHeight(20f); // alto de la línea
                firmaTable.addCell(linea);


                PdfPCell textoFirma = new PdfPCell(new Phrase("Recibí Conforme", boldFont));
                textoFirma.setHorizontalAlignment(Element.ALIGN_CENTER);
                textoFirma.setBorder(Rectangle.NO_BORDER);
                textoFirma.setPaddingTop(5f);
                firmaTable.addCell(textoFirma);


                PdfPCell ciCell = new PdfPCell(new Phrase("C.I.:", boldFont));
                ciCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                ciCell.setBorder(Rectangle.NO_BORDER);
                ciCell.setPaddingTop(5f);
                firmaTable.addCell(ciCell);

                document.add(firmaTable);
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

    private String getBeneficiario(CnAsientosEntity comprobante) {

        if (Objects.nonNull(comprobante.getNombre()) && Objects.nonNull(comprobante.getTercero().getNumeroIdentificacion())) {
            return comprobante.getNombre() + " " + comprobante.getTercero().getNumeroIdentificacion();
        }
        return "";
    }

    private String getCiudad(CnAsientosEntity comprobante) {
        if (Objects.nonNull(comprobante.getTercero())) {
            return comprobante.getTercero().getCiudad();
        }
        return "Quito";
    }

    private PdfPCell getCell(String text, Font font, int alignment, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(border);
        return cell;
    }
}
