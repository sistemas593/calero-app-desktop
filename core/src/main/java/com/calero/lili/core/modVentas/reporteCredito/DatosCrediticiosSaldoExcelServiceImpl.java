package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DatosCrediticiosSaldoExcelServiceImpl {

    private final DatosCrediticiosDetalleRepository datosCrediticiosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;

    public void cargarSaldoDatosCrediticios(Long idData, Long idEmpresa, MultipartFile file) throws IOException {

        Set<String> numerosOperacion = new HashSet<>();
        try (InputStream is1 = file.getInputStream();
             Workbook wb1 = StreamingReader.builder()
                     .rowCacheSize(100)
                     .bufferSize(4096)
                     .open(is1)) {
            for (Sheet sheet : wb1) {
                boolean isHeader = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    if (Objects.nonNull(row.getCell(0))) {
                        String numeroOperacion = row.getCell(0).getStringCellValue();
                        if (!numeroOperacion.isBlank()) numerosOperacion.add(numeroOperacion);
                    }
                }
            }
        }


        // Segundo paso: procesar filas con los datos de la consulta
        List<String> codigos = new ArrayList<>(numerosOperacion);

        Map<String, DatosCrediticiosDetalleEntity> mapDatos =
                datosCrediticiosRepository.findAllNumeroOperacion(idData, idEmpresa, codigos)
                        .stream()
                        .collect(Collectors.toMap(DatosCrediticiosDetalleEntity::getNumeroOperacion, Function.identity()));

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<DatosCrediticiosDetalleEntity> entidadesActualizar = new ArrayList<>();
        Workbook workbook2 = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(file.getInputStream());

        boolean isHeader = true;
        for (Sheet sheet : workbook2) {
            for (Row row : sheet) {
                if (isRowEmpty(row)) continue;

                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                if (Objects.nonNull(row.getCell(0)) && Objects.nonNull(row.getCell(1))) {

                    String numeroOperacion = row.getCell(0).getStringCellValue();
                    DatosCrediticiosDetalleEntity entidad = mapDatos.get(numeroOperacion);
                    if (Objects.nonNull(entidad)) {
                        BigDecimal saldo = convetirValor(row.getCell(1).getStringCellValue());
                        entidad.setSaldoOperacion(saldo);
                        entidadesActualizar.add(entidad);

                    } else {
                        DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                        detalleError.setDetalle("El detalle con número de operación " + numeroOperacion + " no existe");
                        detalleErrores.add(detalleError);
                    }

                } else {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("El número operación o el valor del saldo de la operación no se encuentran");
                    detalleErrores.add(detalleError);
                }
            }

            if (detalleErrores.isEmpty()) {
                datosCrediticiosRepository.saveAll(entidadesActualizar);
            } else {
                throwErrors(detalleErrores);
            }
        }
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
