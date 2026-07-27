package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.ValidarTipoArchivo;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    public void cargarReembolsos(Long idData, Long idEmpresa,
                                 MultipartFile file, String usuario) throws IOException {


        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        List<CpImpuestosReembolsosEntity> reembolsos = new ArrayList<>();
        List<DetalleError> detalleErrores = new ArrayList<>();

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
                        String codigoCompuesto = String.join("-", celdas[0], celdas[1], "D" + celdas[2], celdas[3], comprobarSecuencial(celdas[4]));
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


            int linea = fila.linea();
            CpImpuestosReembolsosEntity item = new CpImpuestosReembolsosEntity();


            item.setIdImpuestosReembolsos(UUID.randomUUID());
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
            if (Objects.nonNull(celdaSerie)) {
                item.setSecuencialReemb(celdaSecuencia);
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

            String celdaNumAut = celda(fila.celdas(), 8);
            if (Objects.nonNull(celdaNumAut)) {
                //item.setF(celdaFechaEmision);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("No existe la fecha de emisión del documento de reembolso");
                detalleErrores.add(detalleError);
            }

            reembolsos.add(item);
        }


        if (detalleErrores.isEmpty()) {
            System.out.println("Aqui");
        } else {
            throwErrors(detalleErrores);
        }


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

    private String comprobarSecuencial(String secuencial) {
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

}
