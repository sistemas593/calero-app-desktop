package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirCreationRequestDto;
import com.calero.lili.core.enums.OrigenImpuestos;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestosBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.ImpuestoCodigoBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.AcumulacionProveedorTotalesDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.FilterListDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDtoTotalizado;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.projection.ComprasImpuestoProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.projection.OneProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.projection.TotalesProjection;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CodigoImpuestoResponseDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CompraImpuestoResponseDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisesRepository;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalEntity;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalRepository;
import com.calero.lili.core.utils.ComprobanteSustentoService;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import com.calero.lili.core.utils.validaciones.ValidarValoresComprobantesPdf;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CpImpuestosServiceImpl {

    private final CpImpuestosRepository cpImpuestosRepository;
    private final CpImpuestosBuilder cpImpuestosBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final ImpuestoCodigoBuilder impuestoCodigoBuilder;
    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;
    private final AdEmpresasRepository adEmpresasRepository;
    private final ComprobanteSustentoService comprobanteSustentoService;
    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;
    private final TbPaisesRepository tbPaisesRepository;
    private final TbParaisoFiscalRepository tbParaisoFiscalRepository;
    private final GeTercerosRepository geTercerosRepository;


    public ResponseDto create(Long idData, Long idEmpresa, CreationCompraImpuestoRequestDto request, String usuario) {


        ValidacionDocumentosGeneral.validarSizeSecuencial(request.getSecuencial());
        validarNumeroAutorizacion(request);
        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getFechaEmision()));

        Optional<OneProjection> existingFactura = cpImpuestosRepository
                .findExistBySecuencial(idData, idEmpresa, request.getNumeroIdentificacion(),
                        request.getSerie(), request.getSecuencial(), request.getNumeroAutorizacion(), request.getCodigoSustento());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("El registro ya existe - numeroIdentificacion: {0} Serie: {1} Secuencia: {2} numeroAutorizacion: {3}, codigoSustento: {4}", request.getNumeroIdentificacion(), request.getSerie(), request.getSecuencial(), request.getNumeroAutorizacion(), request.getCodigoSustento()));
        }

        validacionCodigoImpuesto(request);
        validarReembolso(request);
        validarPagoExterior(request);
        validarInfoAddicional(request);
        CpImpuestosEntity impuestosEntity = cpImpuestosBuilder.builderEntity(request, idData, idEmpresa);

        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id de tercero {0} no existe", request.getIdTercero())));

        impuestosEntity.setTercero(proveedor);
        impuestosEntity.setOrigen(setearOrigen(request));
        impuestosEntity.setCreatedBy(usuario);
        impuestosEntity.setCreatedDate(LocalDateTime.now());
        return responseApiBuilder.builderResponse(cpImpuestosRepository
                .save(impuestosEntity).getIdImpuestos().toString());

    }

    private void validarPagoExterior(CreationCompraImpuestoRequestDto request) {

        if (request.getPagoLocExt().equals("01") && Objects.nonNull(request.getPagoExterior())) {
            throw new GeneralException("No es requerido el pago exterior");
        }

        if (request.getPagoLocExt().equals("02") && Objects.isNull(request.getPagoExterior())) {
            throw new GeneralException("No existe pago exterior");
        }

        validacionGeneralPagoExterior(request.getPagoLocExt(), request.getPagoExterior());

    }

    private void validacionGeneralPagoExterior(String pagoLocExt, PagoExterior request) {
        if (pagoLocExt.equals("02")) {

            switch (request.getTipoRegi()) {
                case "01" -> {

                    if (Objects.isNull(request.getPaisEfecPagoGen()) || request.getPaisEfecPagoGen().isEmpty()) {
                        throw new GeneralException("No existe pais al que se realiza el pago en régimen general");
                    }

                    if (Objects.nonNull(request.getPaisEfecPagoParFis()) ||
                            Objects.nonNull(request.getDenopagoRegFis())) {

                        throw new GeneralException("No debe existir pago para paraiso fiscal " +
                                " ni para denominacion del régimen fiscal preferente, si el tipo de registro es 01");
                    }

                    tbPaisesRepository.findById(request.getPaisEfecPago())
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("El pais con codigo {0}, no existe", request.getPaisEfecPago())));

                    if (!request.getPaisEfecPago().equals(request.getPaisEfecPagoGen())) {
                        throw new GeneralException("El pais de pago debe ser igual al pais de pago régimen general");
                    }


                }
                case "02" -> {

                    if (Objects.isNull(request.getPaisEfecPagoParFis()) || request.getPaisEfecPagoParFis().isEmpty()) {
                        throw new GeneralException("No existe pais al que se realiza el pago en paraiso fiscal");
                    }

                    if (Objects.nonNull(request.getPaisEfecPagoGen()) ||
                            Objects.nonNull(request.getDenopagoRegFis())) {

                        throw new GeneralException("No debe existir pago para régimen general " +
                                " ni para denominacion del régimen fiscal preferente, si el tipo de registro es 02");
                    }

                    TbParaisoFiscalEntity entidad = tbParaisoFiscalRepository.findByCodigo(request.getPaisEfecPagoParFis())
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("El paraiso fiscal con codigo {0}, no existe",
                                    request.getPaisEfecPagoParFis())));

                    if (!entidad.getPais().getCodigoPais().equals(request.getPaisEfecPago())) {
                        throw new GeneralException("El pais de pago debe ser igual al pais asignado al paraiso fiscal");
                    }

                }
                case "03" -> {
                    if (Objects.isNull(request.getDenopagoRegFis()) || request.getDenopagoRegFis().isEmpty()) {
                        throw new GeneralException("No existe pago denominacion del régimen fiscal preferente");
                    }

                    if (Objects.nonNull(request.getPaisEfecPagoParFis()) ||
                            Objects.nonNull(request.getPaisEfecPagoGen())) {

                        throw new GeneralException("No debe existir pago para paraiso fiscal " +
                                " ni para régimen general, si el tipo de registro es 03");
                    }

                    tbPaisesRepository.findById(request.getPaisEfecPago())
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("El pais con codigo {0}, no existe", request.getPaisEfecPago())));

                }
                default -> throw new GeneralException("El tipo de registro del pago exterior no es valido");
            }


           /* String codigoPais = request.getPagoExterior().getPaisEfecPago();
            tbPaisesRepository.findById(codigoPais)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("El pais con codigo {0}, no existe", codigoPais)));*/

        }
    }

    private void validarReembolso(CreationCompraImpuestoRequestDto request) {

        if (Objects.nonNull(request.getCodigoDocumento())) {
            if (request.getCodigoDocumento().equals("41")) {
                if (Objects.isNull(request.getReembolsos())) {
                    throw new GeneralException("No existe lista de reembolsos");
                }
            }
        }
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationCompraImpuestoRequestDto request,
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        validarNumeroAutorizacion(request);
        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getFechaEmision()));

        validarPagoExterior(request);
        CpImpuestosEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);


        if (!vtVentaEntity.getTercero().getNumeroIdentificacion().equals(request.getNumeroIdentificacion())
                || !vtVentaEntity.getSerie().equals(request.getSerie())
                || !vtVentaEntity.getSecuencial().equals(request.getSecuencial())
                || !vtVentaEntity.getNumeroAutorizacion().equals(request.getNumeroAutorizacion())
                || !vtVentaEntity.getCodigoSustento().equals(request.getCodigoSustento())) {

            Optional<OneProjection> existingFactura = cpImpuestosRepository.findExistBySecuencial(idData, idEmpresa, request.getNumeroIdentificacion(), request.getSerie(), request.getSecuencial(), request.getNumeroAutorizacion(), request.getCodigoSustento());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("El registro ya existe - numeroIdentificacion{0} Serie: {1} Secuencial: {2} numeroAutorizacion {3} codigoSustento {4}", request.getNumeroIdentificacion(), request.getSerie(), request.getSecuencial(), request.getNumeroAutorizacion(), request.getCodigoSustento()));
            }
        }

        validarInfoAddicional(request);
        validacionCodigoImpuesto(request);

        CpImpuestosEntity impuestosEntity = cpImpuestosBuilder.builderUpdateEntity(request, vtVentaEntity);

        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id de tercero {0} no existe", request.getIdTercero())));

        impuestosEntity.setTercero(proveedor);
        impuestosEntity.setModifiedBy(usuario);
        impuestosEntity.setModifiedDate(LocalDateTime.now());

        impuestosEntity.setOrigen(setearOrigen(request));
        cpImpuestosRepository.save(impuestosEntity);
        return responseApiBuilder.builderResponse(vtVentaEntity.getIdImpuestos().toString());

    }

    private String setearOrigen(CreationCompraImpuestoRequestDto model) {
        if (Objects.nonNull(model.getImpuestoCodigos())) {
            return OrigenImpuestos.ICC.name();
        }
        return OrigenImpuestos.ISC.name();
    }


    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario,
                       FilterListDto filters, TipoPermiso tipoBusqueda) {

        CpImpuestosEntity impuestosEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        impuestosEntity.setDelete(Boolean.TRUE);
        impuestosEntity.setDeletedBy(usuario);
        impuestosEntity.setDeletedDate(LocalDateTime.now());

        cpImpuestosRepository.save(impuestosEntity);


    }


    public GetDto findById(Long idData, Long idEmpresa, UUID id,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpImpuestosEntity entity = validacionTipoBusqueda(idData, idEmpresa, id, filters, tipoBusqueda, usuario);
        GetDto dto = cpImpuestosBuilder.builderDto(entity);

        if (Objects.nonNull(dto.getPagoExterior())) {


            String codigoPais = dto.getPagoExterior().getPaisEfecPago();
            TbPaisEntity pais = tbPaisesRepository.findById(codigoPais)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("El pais con codigo {0}, no existe", codigoPais)));
            dto.getPagoExterior().setNombrePaisEfecPago(pais.getPais());

            switch (dto.getPagoExterior().getTipoRegi()) {

                case "01" -> {
                    dto.getPagoExterior().setNombrePaisEfecPagoGen(pais.getPais());
                }
                case "02" -> {
                    String codigoParaiso = dto.getPagoExterior().getPaisEfecPagoParFis();
                    TbParaisoFiscalEntity paraiso = tbParaisoFiscalRepository.findByCodigo(codigoParaiso)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("El paraiso fiscal con codigo {0}, no existe", codigoParaiso)));
                    dto.getPagoExterior().setNombrePaisEfecPagoParFis(paraiso.getParaisoFiscal());
                }
            }

        }

        return dto;
    }

    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
                                                                      TipoPermiso tipoBusqueda, String usuario) {

        Page<CpImpuestosEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        List<GetListDto> dtoList = page.stream().map(cpImpuestosBuilder::builderGetListDto).toList();

        List<TotalesProjection> totalValoresProjection = cpImpuestosRepository.totalValores(idData, idEmpresa, filters.getTipoDocumento(), filters.getNumeroIdentificacion(), filters.getSerie(), filters.getSecuencial(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getFechaRegistroDesde(), filters.getFechaRegistroHasta(), filters.getNumeroAutorizacion(), filters.getDestino());

        GetListDtoTotalizado totalesDto = new GetListDtoTotalizado<>();
        totalesDto.setContent(dtoList);

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
        totalesDto.setPaginated(paginated);

        GetListDtoTotalizado.Totales tot = new GetListDtoTotalizado.Totales();
        tot.setValoresTotales(totalValoresProjection);

        totalesDto.setTotales(tot);
        return totalesDto;

    }


    public void updateDatos(Long idData, Long idEmpresa, UUID id, CpImpuestosRecibirCreationRequestDto request) {

        CpImpuestosEntity impuesto = cpImpuestosRepository
                .findByIdEntity(idData, idEmpresa, id, null, null)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", id)));

        String codigoDocumento = impuesto.getDocumento().getCodigoDocumento();

        if (Objects.nonNull(request.getDestino())) {
            if (DateUtils.toLocalDate(request.getFechaRegistro()).isAfter(impuesto.getFechaEmision())
                    || DateUtils.toLocalDate(request.getFechaRegistro()).isEqual(impuesto.getFechaEmision())) {

                if ((DateUtils.toLocalDate(request.getFechaRegistro()).isAfter(impuesto.getFechaEmision().plusYears(1)))) {
                    throw new GeneralException("La fecha no puede ser mayor a un año respecto a la fecha de emisión del documento");
                }

                if (!comprobanteSustentoService.validacionCodigos(codigoDocumento, request.getCodigoSustento().name().replace("S", ""))) {
                    throw new GeneralException(MessageFormat.format("La combinación de código de documento: {0} y código de sustento: {1} es inválida.",
                            codigoDocumento, request.getCodigoSustento()));
                }

                impuesto.setDestino(request.getDestino().name());
                impuesto.setFechaRegistro(DateUtils.toLocalDate(request.getFechaRegistro()));
                impuesto.setCodigoSustento(request.getCodigoSustento());
                impuesto.setOrigen(OrigenImpuestos.DSC.name());

                cpImpuestosRepository.save(impuesto);

            } else {
                throw new GeneralException("La fecha de registro no puede ser menor que la fecha de emisiòn ");
            }
        } else {

            impuesto.setDestino(null);
            impuesto.setFechaRegistro(null);
            impuesto.setCodigoSustento(null);

            cpImpuestosRepository.save(impuesto);
        }

    }


    public void exportarExcel(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filter) throws IOException {


        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Recibidos_" + fechaActual + ".xlsx" + "\"");

        List<ComprasImpuestoProjection> recibidos = cpImpuestosRepository.getCompraImpuesto(idData, idEmpresa,
                filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Recibidos");
            XSSFRow headerRow = sheet.createRow(0);

            String[] columnNames = {"Empresa", "Fecha Emisión", "Fecha Registro", "Proveedor", "Identificación Proveedor",
                    "Tipo Documento", "Serie", "Número Autorización", "Secuencial", "Fecha Vencemiento", "Concepto",
                    "Devolucion Iva", "Referencia", "Base Imponible 0", "Tarifa", "Valor Iva 0%",
                    "Base Imponible 15%", "Tarifa", "Valor Iva 15%", "Base Imponible 5%", "Tarifa", "Valor Iva 5%",
                    "Base Imponible 8%", "Tarifa", "Valor Iva 8%", "Base Imp. Objecto", "Base Imp. Base Excenta",
                    "Fecha Autorizacion", "Codigo Doc. Modificado", "Serie Doc. Modificado", "Secuencial Doc. Modificado",
                    "Fecha Aut Doc Modificado", "Fecha Emision Doc Modificado"};
            IntStream.range(0, columnNames.length)
                    .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

            IntStream.range(0, recibidos.size()).forEach(i -> {
                ComprasImpuestoProjection recibido = recibidos.get(i);
                XSSFRow row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(idEmpresa);
                row.createCell(1).setCellValue(recibido.getFecha_emision() != null ? DateUtils.toString(recibido.getFecha_emision()) : "");
                row.createCell(2).setCellValue(recibido.getFecha_registro() != null ? DateUtils.toString(recibido.getFecha_registro()) : "");
                row.createCell(3).setCellValue(recibido.getTercero());
                row.createCell(4).setCellValue(recibido.getNumero_identificacion());
                row.createCell(5).setCellValue(recibido.getCodigo_documento());
                row.createCell(6).setCellValue(recibido.getSerie());
                row.createCell(7).setCellValue(recibido.getNumero_autorizacion());
                row.createCell(8).setCellValue(recibido.getSecuencial());
                row.createCell(9).setCellValue(recibido.getFecha_vencimiento() != null ? DateUtils.toString(recibido.getFecha_vencimiento()) : "");
                row.createCell(10).setCellValue(recibido.getConcepto());
                row.createCell(11).setCellValue(recibido.getDevolucion_iva());
                row.createCell(12).setCellValue(recibido.getReferencia());
                row.createCell(13).setCellValue(recibido.getBase_imponible_cero().doubleValue());
                row.createCell(14).setCellValue(recibido.getTarifa_iva_cero().doubleValue());
                row.createCell(15).setCellValue(recibido.getValor_iva_cero().doubleValue());
                row.createCell(16).setCellValue(recibido.getBase_imponible_quince().doubleValue());
                row.createCell(17).setCellValue(recibido.getTarifa_iva_quince().doubleValue());
                row.createCell(18).setCellValue(recibido.getValor_iva_quince().doubleValue());
                row.createCell(19).setCellValue(recibido.getBase_imponible_cinco().doubleValue());
                row.createCell(20).setCellValue(recibido.getTarifa_iva_cinco().doubleValue());
                row.createCell(21).setCellValue(recibido.getValor_iva_cinco().doubleValue());
                row.createCell(22).setCellValue(recibido.getBase_imponible_ocho().doubleValue());
                row.createCell(23).setCellValue(recibido.getTarifa_iva_ocho().doubleValue());
                row.createCell(24).setCellValue(recibido.getValor_iva_ocho().doubleValue());
                row.createCell(25).setCellValue(recibido.getBase_imponible_no_objecto().doubleValue());
                row.createCell(26).setCellValue(recibido.getBase_imponible_exento().doubleValue());
                row.createCell(27).setCellValue(recibido.getFecha_autorizacion() != null ? DateUtils.toString(recibido.getFecha_autorizacion()) : "");
                row.createCell(28).setCellValue(recibido.getCodigo_documento_reemb());
                row.createCell(29).setCellValue(recibido.getSerie_reemb());
                row.createCell(30).setCellValue(recibido.getSecuencial_reemb());
                row.createCell(31).setCellValue(recibido.getNumero_autorizacion_reemb());
                row.createCell(32).setCellValue(recibido.getFecha_emision_reemb() != null ? DateUtils.toString(recibido.getFecha_emision_reemb()) : "");
            });

            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }

        }
    }

    public void exportarPDF(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filter) throws IOException {


        response.setContentType("application/pdf");
        String filename = "reporte-compra-impuestos" + LocalDate.now().toString().replace("-", "") + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<ComprasImpuestoProjection> facturas = cpImpuestosRepository.getCompraImpuesto(idData, idEmpresa,
                filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta());


        if (facturas.isEmpty()) {
            throw new GeneralException("No existen campos para mostrar");
        }

        Map<String, List<ComprasImpuestoProjection>> facAgrupadas = getMapProveedores(facturas);

        AdEmpresaEntity adEmpresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("La empresa seleccionado no existe"));


        // Iniciar el documento PDF

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();


        Font fontEmpresa = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font fontTitulo = new Font(Font.HELVETICA, 13, Font.BOLD);
        Font fontHeader = new Font(Font.HELVETICA, 9);
        Font fontCell = new Font(Font.HELVETICA, 9, Font.BOLD);


        Paragraph empresa = new Paragraph(adEmpresa.getRazonSocial(), fontEmpresa);
        document.add(empresa);

        Paragraph titulo = new Paragraph("Reporte de Compras", fontTitulo);
        titulo.setSpacingAfter(5);
        document.add(titulo);

        Paragraph fecha = new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontCell);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);

        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);

        PdfPCell line = new PdfPCell();
        line.setFixedHeight(1f);
        line.setBorder(Rectangle.BOTTOM);
        line.setBorderColor(Color.BLACK);

        linea.addCell(line);
        document.add(linea);


        String[] headers = {"Fecha", "TD", "Serie", "Número", "Base Cero", "Base 15", "Valor Iva 15", "Base 5", "Valor Iva 5",
                "Base 8", "Valor Iva 8", "No Objeto", "Excenta"};


        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);

        table.setWidths(new float[]{10f, 6f, 7f, 9f, 10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f});


        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
            cell.setBackgroundColor(new Color(230, 230, 230));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);
        }

        int totalColumnas = headers.length;

        AcumulacionProveedorTotalesDto totalGeneral = new AcumulacionProveedorTotalesDto();

        for (Map.Entry<String, List<ComprasImpuestoProjection>> entry : facAgrupadas.entrySet()) {

            List<ComprasImpuestoProjection> facturaProveedor = entry.getValue();

            ComprasImpuestoProjection proveedorRef = facturaProveedor.getFirst();

            AcumulacionProveedorTotalesDto totales = new AcumulacionProveedorTotalesDto();


            table.addCell(celdaProveedor(proveedorRef, totalColumnas));

            for (ComprasImpuestoProjection factura : facturaProveedor) {

                totales.acumularPorProveedor(factura);
                totalGeneral.acumularPorProveedor(factura);

                table.addCell(validarCeldaTexto(Objects.nonNull(factura.getFecha_emision()) ? DateUtils.toString(factura.getFecha_emision()) : "", fontCell));
                table.addCell(validarCeldaTexto(factura.getCodigo_documento(), fontCell));
                table.addCell(validarCeldaTexto(factura.getSerie(), fontCell));
                table.addCell(validarCeldaTexto(factura.getSecuencial(), fontCell));

                table.addCell(validarCeldaValores(factura.getBase_imponible_cero().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getBase_imponible_cero()), fontCell));

                table.addCell(validarCeldaValores(factura.getBase_imponible_quince().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getBase_imponible_quince()), fontCell));
                table.addCell(validarCeldaValores(factura.getValor_iva_quince().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getValor_iva_quince()), fontCell));

                table.addCell(validarCeldaValores(factura.getBase_imponible_cinco().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getBase_imponible_cinco()), fontCell));
                table.addCell(validarCeldaValores(factura.getValor_iva_cinco().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getValor_iva_cinco()), fontCell));

                table.addCell(validarCeldaValores(factura.getBase_imponible_ocho().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getBase_imponible_ocho()), fontCell));
                table.addCell(validarCeldaValores(factura.getValor_iva_ocho().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getValor_iva_ocho()), fontCell));

                table.addCell(validarCeldaValores(factura.getBase_imponible_no_objecto().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getBase_imponible_no_objecto()), fontCell));
                table.addCell(validarCeldaValores(factura.getBase_imponible_exento().compareTo(BigDecimal.ZERO) == 0 ? "" : validarValoresComprobantesPdf.getValor(factura.getBase_imponible_exento()), fontCell));
            }

            agregarTotalesProveedor(table, totales, fontCell);
        }
        document.add(table);
        agregarLineasSeparadoras(document);

        PdfPTable tablaTotales = new PdfPTable(headers.length);
        tablaTotales.setWidthPercentage(100);
        tablaTotales.setSpacingBefore(5);
        tablaTotales.setWidths(new float[]{10f, 6f, 7f, 9f, 10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f});

        agregarTotalesGenerales(tablaTotales, totalGeneral, fontCell);

        document.add(tablaTotales);

        document.close();
    }


    private void agregarLineasSeparadoras(Document document) throws DocumentException {

        PdfPTable lineas = new PdfPTable(1);
        lineas.setWidthPercentage(100);

        PdfPCell linea = new PdfPCell();
        linea.setFixedHeight(1f);
        linea.setBorder(Rectangle.BOTTOM);
        linea.setBorderColor(Color.BLACK);

        lineas.addCell(linea);
        lineas.addCell(linea); // segunda línea

        document.add(lineas);
    }

    private void agregarTotalesGenerales(PdfPTable table, AcumulacionProveedorTotalesDto totalGeneral, Font fontCell) {
        Font fontTotal = new Font(Font.HELVETICA, 10, Font.BOLD);

        PdfPCell label = new PdfPCell(new Phrase("TOTAL GENERAL", fontTotal));
        label.setColspan(4);
        label.setHorizontalAlignment(Element.ALIGN_RIGHT);
        label.setPadding(6);
        label.setBackgroundColor(new Color(220, 220, 220));
        table.addCell(label);

        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getBaseCero()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getBase15()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getIva15()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getBase5()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getIva5()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getBase8()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getIva8()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getNoObjeto()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totalGeneral.getExenta()), fontCell));
    }

    private PdfPCell validarCeldaTexto(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell validarCeldaValores(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell celdaProveedor(ComprasImpuestoProjection f, int totalColumnas) {

        Font fontProveedor = new Font(Font.HELVETICA, 10, Font.BOLD);

        String texto = "PROVEEDOR: " + f.getTercero() + "  |  IDENTIFICACIÓN: " + f.getNumero_identificacion();
        PdfPCell cell = new PdfPCell(new Phrase(texto, fontProveedor));
        cell.setColspan(totalColumnas);
        cell.setPadding(6);
        cell.setBackgroundColor(new Color(210, 210, 210));
        cell.setBorder(Rectangle.BOX);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

        return cell;
    }

    private void agregarTotalesProveedor(PdfPTable table, AcumulacionProveedorTotalesDto totales, Font fontCell) {
        Font fontTotal = new Font(Font.HELVETICA, 9, Font.BOLD);

        PdfPCell label = new PdfPCell(new Phrase("TOTALES", fontTotal));
        label.setColspan(4); // Fecha, TD, Serie, Número
        label.setHorizontalAlignment(Element.ALIGN_RIGHT);
        label.setPadding(5);
        label.setBackgroundColor(new Color(240, 240, 240));
        table.addCell(label);

        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getBaseCero()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getBase15()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getIva15()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getBase5()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getIva5()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getBase8()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getIva8()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getNoObjeto()), fontCell));
        table.addCell(validarCeldaValores(validarValoresComprobantesPdf.getValor(totales.getExenta()), fontCell));
    }


    private Map<String, List<ComprasImpuestoProjection>> getMapProveedores(List<ComprasImpuestoProjection> facturas) {
        return facturas.stream()
                .collect(Collectors.groupingBy(
                        ComprasImpuestoProjection::getNumero_identificacion,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }


    public void updateImpuestoLiqAndCompra(Long idData, Long idEmpresa, UUID idEntidad, CompraImpuestosDto model) {

        try {
            CpImpuestosEntity impuesto = cpImpuestosRepository.findById(idData, idEmpresa, model.getIdCompraImpuesto())
                    .orElseThrow(() -> new GeneralException("No existe impuesto para asignarse"));

            impuesto.setIdParent(idEntidad);
            impuesto.setOrigen(model.getOrigen());
            List<CpImpuestosCodigosEntity> listCodigos = impuestoCodigoBuilder.builderMultiList(model.getListCodigosImpuesto(), idData, idEmpresa);
            validateCodigosEntity(impuesto, listCodigos);
            cpImpuestosRepository.save(impuesto);
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }

    }


    public void guardarImpuestoRetencion(CpRetencionesEntity entidad, CompraImpuestosDto model) {


        CpImpuestosEntity impuesto = cpImpuestosRepository.findById(entidad.getIdData(), entidad.getIdEmpresa(), model.getIdCompraImpuesto())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El impuesto con id {0} para asignarse", model.getIdCompraImpuesto())));

       /* if (Objects.nonNull(impuesto.getRetencion())) {
            throw new GeneralException(MessageFormat.format("El documento con id {0}  ya posee una retención asignada",
                    model.getIdCompraImpuesto()));
        }*/

        if (permiteRetencion(impuesto.getOrigen())) {
            impuesto.setRetencion(entidad);
            impuesto.setOrigen(model.getOrigen());
            List<CpImpuestosCodigosEntity> listCodigos = impuestoCodigoBuilder.builderMultiList(model.getListCodigosImpuesto(),
                    entidad.getIdData(), entidad.getIdEmpresa());
            validateCodigosEntity(impuesto, listCodigos);
            cpImpuestosRepository.save(impuesto);

        } else {
            throw new GeneralException(MessageFormat.format("El documento con id {0} y su origen: {1} no corresponde para guardar una retención",
                    model.getIdCompraImpuesto(), impuesto.getOrigen()));
        }

    }


    public void actualizarImpuestoRetencion(CpRetencionesEntity entidad, CompraImpuestosDto model) {

        CpImpuestosEntity impuesto = cpImpuestosRepository.findById(entidad.getIdData(), entidad.getIdEmpresa(), model.getIdCompraImpuesto())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El impuesto con id {0} para asignarse", model.getIdCompraImpuesto())));

        impuesto.setRetencion(entidad);
        impuesto.setOrigen(model.getOrigen());
        List<CpImpuestosCodigosEntity> listCodigos = impuestoCodigoBuilder.builderMultiList(model.getListCodigosImpuesto(),
                entidad.getIdData(), entidad.getIdEmpresa());
        validateCodigosEntity(impuesto, listCodigos);
        cpImpuestosRepository.save(impuesto);
    }

    private void validateCodigosEntity(CpImpuestosEntity impuesto, List<CpImpuestosCodigosEntity> listCodigos) {
        if (Objects.isNull(impuesto.getCodigosEntity())) {
            impuesto.setCodigosEntity(new ArrayList<>());
        }

        if (Objects.nonNull(listCodigos)) {
            impuesto.getCodigosEntity().clear();
            impuesto.getCodigosEntity().addAll(listCodigos);
        } else {
            impuesto.getCodigosEntity().clear();
        }


    }


    public List<GetListDto> getListCompraImpuestoForIdParent(UUID idParent, Long idEmpresa, Long idData) {
        List<GetListDto> response = cpImpuestosBuilder.builderListResponse(cpImpuestosRepository
                .findByIdParent(idData, idEmpresa, idParent));
        if (Objects.nonNull(response)) return response;
        return null;
    }

    private void validacionCodigoImpuesto(CreationCompraImpuestoRequestDto model) {

        if (Objects.nonNull(model.getCodigoSustento())) {
            if (!comprobanteSustentoService.validacionCodigos(model.getCodigoDocumento(), model.getCodigoSustento().name().replace("S", ""))) {
                throw new GeneralException(MessageFormat.format("La combinación de código de documento: {0} y código de sustento: {1} es inválida.",
                        model.getCodigoDocumento(), model.getCodigoSustento()));
            }
        }


    }

    private List<Integer> getIntegerTarifaIva(List<ValoresDto> valores) {
        return valores.stream()
                .map(ValoresDto::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }


    private Page<CpImpuestosEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa,
                                                            FilterListDto filters, Pageable pageable,
                                                            TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return cpImpuestosRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getTipoDocumento(), filters.getNumeroIdentificacion(), filters.getSerie(),
                        filters.getSecuencial(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getFechaRegistroDesde(), filters.getFechaRegistroHasta(),
                        filters.getNumeroAutorizacion(), filters.getDestino(), null, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return cpImpuestosRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getTipoDocumento(), filters.getNumeroIdentificacion(), filters.getSerie(),
                            filters.getSecuencial(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            filters.getFechaRegistroDesde(), filters.getFechaRegistroHasta(),
                            filters.getNumeroAutorizacion(), filters.getDestino(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return cpImpuestosRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getTipoDocumento(), filters.getNumeroIdentificacion(), filters.getSerie(),
                        filters.getSecuencial(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getFechaRegistroDesde(), filters.getFechaRegistroHasta(),
                        filters.getNumeroAutorizacion(), filters.getDestino(), usuario, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private CpImpuestosEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return cpImpuestosRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return cpImpuestosRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return cpImpuestosRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private void validarNumeroAutorizacion(CreationCompraImpuestoRequestDto request) {


        if (request.getNumeroAutorizacion().length() == 49 || request.getNumeroAutorizacion().length() == 10) {

            if (!request.getNumeroAutorizacion().matches("\\d+")) {
                throw new GeneralException("El número de autorización no puede contener caracteres que no sean númericos");
            }

        } else {
            throw new GeneralException("El número de autorización no cumple con la cantidad de dígitos 10/49");
        }
    }


    public void validarInfoAddicional(CreationCompraImpuestoRequestDto request) {

        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                request.setInformacionAdicional(null);
            }
        }
    }

    public List<CompraImpuestoResponseDto> builderResponseListCompraImpuesto(List<CpImpuestosEntity> list) {

        List<CompraImpuestoResponseDto> response = new ArrayList<>();

        for (CpImpuestosEntity cpImpuesto : list) {
            CompraImpuestoResponseDto dto = cpImpuestosBuilder.builderCompraImpuestoDto(cpImpuesto);
            dto.setImpuestoCodigos(builderResponseListCodigosImpuesto(cpImpuesto));
            if (Objects.nonNull(cpImpuesto.getValoresEntity())) {

                BigDecimal subtotal = cpImpuesto.getValoresEntity()
                        .stream()
                        .map(CpImpuestosValoresEntity::getBaseImponible)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal impuesto = cpImpuesto.getValoresEntity()
                        .stream()
                        .map(CpImpuestosValoresEntity::getValor)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                dto.setSubtotal(subtotal);
                dto.setImpuestos(impuesto);
            }
            response.add(dto);
        }
        return response;
    }

    private List<CodigoImpuestoResponseDto> builderResponseListCodigosImpuesto(CpImpuestosEntity impuesto) {

        List<CodigoImpuestoResponseDto> codigosImpuesto = new ArrayList<>();

        if (Objects.nonNull(impuesto.getCodigosEntity())) {
            for (CpImpuestosCodigosEntity cpCodigo : impuesto.getCodigosEntity()) {
                codigosImpuesto.add(cpImpuestosBuilder.builderCodigoImpuestoResponse(cpCodigo));
            }
        }
        return codigosImpuesto;
    }

    public List<CpImpuestosEntity> getListCompraImpuestoForIdRetencion(UUID idRetencion, Long idEmpresa, Long idData) {
        return cpImpuestosRepository.idRetencion(idData, idEmpresa, idRetencion);
    }

    private Boolean permiteRetencion(String origen) {
        if (OrigenImpuestos.XDF.name().equals(origen)) return Boolean.TRUE;
        if (OrigenImpuestos.ISC.name().equals(origen)) return Boolean.TRUE;
        if (OrigenImpuestos.DSC.name().equals(origen)) return Boolean.TRUE;
        return Boolean.FALSE;
    }

}

