package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoReembolsoValidacionBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modImpuestosAnexos.ats.Reembolso;
import com.calero.lili.core.utils.ValidarTipoArchivo;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CpImpuestoReembolsoExcel {


    private final CpImpuestosRepository cpImpuestosRepository;
    private final CpImpuestoReembolsoRepository cpImpuestoReembolsoRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final CpImpuestoReembolsoValidacionBuilder cpImpuestoReembolsoValidacionBuilder;
    private final ValidacionReembolsoService validacionReembolsoService;

    public void cargarReembolsos(Long idData, Long idEmpresa,
                                 MultipartFile file, String usuario) throws IOException {


        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        List<CpImpuestosReembolsosEntity> reembolsos = new ArrayList<>();
        List<DetalleError> detalleErrores = new ArrayList<>();
        List<CpImpuestosEntity> impuestoActualizar = new ArrayList<>();

        record FilaExcel(int linea, String[] celdas) {
        }
        List<FilaExcel> filas = new ArrayList<>();
        Set<String> campoValidacion = new HashSet<>();

        try (InputStream is = file.getInputStream();
             Workbook wb = StreamingReader.builder()
                     .rowCacheSize(500)
                     .bufferSize(65536)
                     .open(is)) {
            for (Sheet sheet : wb) {
                boolean isHeader = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    int lastCell = row.getLastCellNum();
                    String[] celdas = new String[lastCell];
                    for (int i = 0; i < lastCell; i++) {
                        celdas[i] = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : null;
                    }
                    filas.add(new FilaExcel(row.getRowNum() + 1, celdas));

                    if (Arrays.stream(celdas, 0, 5).allMatch(s -> s != null && !s.isBlank())) {
                        String codigoCompuesto = String.join("-", celdas[0], celdas[1], "D" + celdas[2], celdas[3], validacionSecuencial(celdas[4]));
                        campoValidacion.add(codigoCompuesto);
                    }
                }

            }
        }

        List<CpImpuestosEntity> impuestos = cpImpuestosRepository.findByCodigosCompuestos(idData, idEmpresa, campoValidacion);

        if (Objects.isNull(impuestos) || impuestos.isEmpty()) {
            throw new GeneralException("No existen cabeceras");
        }

        Map<String, CpImpuestosEntity> mapaImpuestos = impuestos
                .stream()
                .collect(Collectors.toMap(i -> i.getSerie() + i.getSecuencial(),
                        Function.identity()));


        for (FilaExcel fila : filas) {

            String serieCompraImpuesto = celda(fila.celdas(), 3);
            String secuencialCompraImpuesto = validacionSecuencial(celda(fila.celdas(), 4));

            String key = serieCompraImpuesto + secuencialCompraImpuesto;

            int linea = fila.linea();
            CpImpuestosReembolsosEntity item = new CpImpuestosReembolsosEntity();
            List<CpImpuestosReembolsosValoresEntity> valoresReembolso = new ArrayList<>();


            item.setIdImpuestosReembolsos(UUID.randomUUID());
            item.setIdData(idData);
            item.setIdEmpresa(idEmpresa);
            item.setCreatedBy(usuario);
            item.setCreatedDate(LocalDateTime.now());

            String celdaIdentificacion = celda(fila.celdas(), 6);

            if (Objects.nonNull(celdaIdentificacion)) {
                item.setTipoIdentificacionReemb(tipoIdentificacion(celdaIdentificacion, detalleErrores, linea));
                item.setNumeroIdentificacionReemb(celdaIdentificacion);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe el número de identifiación del proveedor");
                detalleErrores.add(detalleError);
            }


            String celdaTipoProv = celda(fila.celdas(), 5);
            if (Objects.nonNull(celdaTipoProv)) {
                item.setTipoProveedorReemb(celdaTipoProv);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe el tipo de proveedor");
                detalleErrores.add(detalleError);
            }

            String celdaTipoDoc = celda(fila.celdas(), 7);
            if (Objects.nonNull(celdaIdentificacion)) {
                item.setCodigoDocumentoReemb(celdaTipoDoc);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe el tipo de documento de reembolso");
                detalleErrores.add(detalleError);
            }


            String celdaSerie = celda(fila.celdas(), 9);
            if (Objects.nonNull(celdaSerie)) {
                item.setSerieReemb(celdaSerie);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe la serie del documento de reembolso");
                detalleErrores.add(detalleError);
            }

            String celdaSecuencia = celda(fila.celdas(), 10);
            if (Objects.nonNull(celdaSecuencia)) {
                item.setSecuencialReemb(validacionSecuencial(celdaSecuencia));
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe la secuencia del documento de reembolso");
                detalleErrores.add(detalleError);
            }


            String celdaFechaEmision = celda(fila.celdas(), 8);
            if (Objects.nonNull(celdaFechaEmision)) {
                item.setFechaEmisionReemb(celdaFechaEmision);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe la fecha de emisión del documento de reembolso");
                detalleErrores.add(detalleError);
            }

            String celdaNumAut = celda(fila.celdas(), 11);
            if (Objects.nonNull(celdaNumAut)) {
                item.setNumeroAutorizacionReemb(celdaNumAut);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe el número de autorización del documento de reembolso");
                detalleErrores.add(detalleError);
            }


            CpImpuestosReembolsosValoresEntity valorImpuesto1 = new CpImpuestosReembolsosValoresEntity();


            valorImpuesto1.setIdImpuestosValores(UUID.randomUUID());
            valorImpuesto1.setIdData(idData);
            valorImpuesto1.setIdEmpresa(idEmpresa);


            String celdaBaseCero = celda(fila.celdas(), 12);

            if (Objects.nonNull(celdaBaseCero)) {
                BigDecimal baseCero = convetirValor(celdaBaseCero);
                if (baseCero.compareTo(BigDecimal.ZERO) != 0) {
                    valorImpuesto1.setCodigo("2");
                    valorImpuesto1.setTarifa(0);
                    valorImpuesto1.setCodigoPorcentaje("0");
                    valorImpuesto1.setValor(BigDecimal.ZERO);
                    valorImpuesto1.setBaseImponible(baseCero);
                }
            }


            String celdaBaseGrav1 = celda(fila.celdas(), 13);
            String celdaTarifa1 = celda(fila.celdas(), 14);
            String celdaValorIva1 = celda(fila.celdas(), 15);


            if (Objects.nonNull(celdaBaseGrav1)) {
                if (Objects.nonNull(celdaTarifa1) && Objects.nonNull(celdaValorIva1)) {

                    BigDecimal baseGrav = convetirValor(celdaBaseGrav1);
                    BigDecimal valorImp = convetirValor(celdaValorIva1);

                    if (baseGrav.compareTo(BigDecimal.ZERO) != 0) {
                        int tarifa = validarTarifa(celdaTarifa1);
                        valorImpuesto1.setCodigo("2");
                        valorImpuesto1.setTarifa(tarifa);
                        valorImpuesto1.setCodigoPorcentaje(celdaTarifa1);
                        valorImpuesto1.setValor(valorImp);
                        valorImpuesto1.setBaseImponible(baseGrav);
                    }
                } else {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("La tarifa o el valor del impuesto no existe");
                    detalleErrores.add(detalleError);
                }
            }


            String celdaBaseGrav2 = celda(fila.celdas(), 16);
            String celdaTarifa2 = celda(fila.celdas(), 17);
            String celdaValorIva2 = celda(fila.celdas(), 18);

            if (Objects.nonNull(celdaBaseGrav2)) {

                CpImpuestosReembolsosValoresEntity valorImpuesto2 = new CpImpuestosReembolsosValoresEntity();

                if (Objects.nonNull(celdaTarifa2) && Objects.nonNull(celdaValorIva2)) {

                    BigDecimal baseGrav = convetirValor(celdaBaseGrav2);
                    BigDecimal valorImp = convetirValor(celdaValorIva2);

                    if (baseGrav.compareTo(BigDecimal.ZERO) != 0) {
                        int tarifa = validarTarifa(celdaTarifa2);
                        valorImpuesto2.setCodigo("2");
                        valorImpuesto2.setTarifa(tarifa);
                        valorImpuesto2.setCodigoPorcentaje(celdaTarifa2);
                        valorImpuesto2.setValor(valorImp);
                        valorImpuesto2.setBaseImponible(baseGrav);
                    }
                } else {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("La tarifa o el valor del impuesto no existe");
                    detalleErrores.add(detalleError);
                }

                valoresReembolso.add(valorImpuesto2);
            }


            String celdaBaseNoObj = celda(fila.celdas(), 19);
            if (Objects.nonNull(celdaBaseNoObj)) {
                BigDecimal baseNoObj = convetirValor(celdaBaseNoObj);
                if (baseNoObj.compareTo(BigDecimal.ZERO) != 0) {
                    valorImpuesto1.setCodigo("2");
                    valorImpuesto1.setTarifa(0);
                    valorImpuesto1.setCodigoPorcentaje("6");
                    valorImpuesto1.setValor(BigDecimal.ZERO);
                    valorImpuesto1.setBaseImponible(baseNoObj);
                }
            }


            String celdaBaseExe = celda(fila.celdas(), 20);
            if (Objects.nonNull(celdaBaseExe)) {
                BigDecimal baseExcenta = convetirValor(celdaBaseNoObj);
                if (baseExcenta.compareTo(BigDecimal.ZERO) != 0) {
                    valorImpuesto1.setCodigo("2");
                    valorImpuesto1.setTarifa(0);
                    valorImpuesto1.setCodigoPorcentaje("7");
                    valorImpuesto1.setValor(BigDecimal.ZERO);
                    valorImpuesto1.setBaseImponible(baseExcenta);
                }
            }

            valoresReembolso.add(valorImpuesto1);
            item.setReembolsosValores(valoresReembolso);
            reembolsos.add(item);

            CpImpuestosEntity entidad = mapaImpuestos.get(key);

            if (Objects.nonNull(entidad)) {
                entidad.getReembolsosEntity().clear();
                entidad.getReembolsosEntity().addAll(reembolsos);
                impuestoActualizar.add(entidad);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La cabecera en impuestos a la que se hace referencia no existe," +
                        " serie: " + serieCompraImpuesto + " secuencial: " + secuencialCompraImpuesto);
                detalleErrores.add(detalleError);
            }


        }


        if (detalleErrores.isEmpty()) {
            List<CpImpuestoDetalleError> erroresValidacion = new ArrayList<>();
            for (CpImpuestosEntity impuesto : impuestoActualizar) {
                impuesto.getReembolsosEntity().forEach(item -> {
                    Reembolso reembolso = cpImpuestoReembolsoValidacionBuilder.builderValidacionExcel(item);
                    validacionReembolsoService.validarReembolso(reembolso, erroresValidacion, impuesto.getFechaEmision());
                });

            }

            if (erroresValidacion.isEmpty()) {
                cpImpuestosRepository.saveAll(impuestoActualizar);
            } else {
                List<String> list = erroresValidacion.stream()
                        .map(CpImpuestoDetalleError::getDetalle)
                        .toList();
                throw new ListErrorException(list);
            }

        } else {
            throwErrors(detalleErrores);
        }
    }

    private int validarTarifa(String celdaTarifa1) {
        return Integer.parseInt(celdaTarifa1);
    }


    private String celda(String[] celdas, int idx) {
        if (idx >= celdas.length) return null;
        String v = celdas[idx];
        return (v != null && !v.isBlank()) ? v : null;
    }


    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            if (row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                return false;
            }
        }
        return true;
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                .toList();
        throw new ListErrorException(list);
    }

    private String validacionSecuencial(String secuencial) {
        if (Objects.isNull(secuencial)) {
            return "";
        }

        if (secuencial.matches("\\d{9}")) {
            return secuencial;
        } else {
            return String.format("%09d", Integer.parseInt(secuencial));
        }
    }

    private String tipoIdentificacion(String celdaIdentificacion, List<DetalleError> detalleErrores, int linea) {


        return switch (celdaIdentificacion.length()) {
            case 10 -> "C";
            case 13 -> "R";
            default -> {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El número de identificación:" + celdaIdentificacion + " debe tener 10 o 13 caracteres");
                detalleErrores.add(detalleError);
                yield null;
            }
        };

    }

    private BigDecimal convetirValor(String valor) {
        valor = valor.trim();
        if (valor.contains(",") && valor.contains(".")) {
            if (valor.lastIndexOf(",") > valor.lastIndexOf(".")) {
                valor = valor.replace(".", "").replace(",", ".");
            } else {
                valor = valor.replace(",", "");
            }
        } else if (valor.contains(",")) {
            valor = valor.replace(",", ".");
        }
        return new BigDecimal(valor);
    }

}
