package com.calero.lili.core.modTesoreria.modTesoreriaEstadosCuenta;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelCargarEstadoCuentaServiceImpl {

    private final DetalleErrorBuilder detalleErrorBuilder;
    private final TsEstadosCuentaRepository tsEstadosCuentaRepository;


    public void cargarEstadoCuenta(MultipartFile file, UUID idEntidad, String periodo) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<TsEstadosCuentaEntity> listItems = new ArrayList<>();

        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                TsEstadosCuentaEntity item = new TsEstadosCuentaEntity();
                item.setIdEstadoCuenta(UUID.randomUUID());
               /* item.setTsEntidadEntity(TsEntidadEntity.builder()
                        .idEntidad(idEntidad)
                        .build());*/

                validateRegister(periodo, idEntidad, linea, detalleErrores);

                if (Objects.isNull(row.getCell(0))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FECHA_ESTADO_CUENTA_NOT_FOUND));
                } else {
                    Date date = row.getCell(0).getDateCellValue();
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    item.setFechaDocumento(localDate);
                }

                if (Objects.isNull(row.getCell(1))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.MOVIMIENTO_ESTADO_CUENTA_NOT_FOUND));
                } else {
                    item.setMovimiento(validateMovimiento(row.getCell(1).getStringCellValue()));
                }

                if (Objects.isNull(row.getCell(2))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NUMERO_ESTADO_CUENTA_NOT_FOUND));
                } else {
                    item.setNumeroDocumento(validateMovimiento(row.getCell(2).getStringCellValue()));
                }

                if (Objects.isNull(row.getCell(4))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.VALOR_ESTADO_CUENTA_NOT_FOUND));
                } else {
                    item.setValor(validateValor(row.getCell(4).getStringCellValue()));
                }

                listItems.add(item);

            }

            if (detalleErrores.isEmpty()) {
                tsEstadosCuentaRepository.saveAll(listItems);
            } else {
                throwErrors(detalleErrores);
            }

        }
    }

    private BigDecimal validateValor(String stringCellValue) {
        if (stringCellValue.contains(",")) {
            return new BigDecimal(stringCellValue.replace(",", "."));
        }
        return new BigDecimal(stringCellValue);
    }


    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription())
                .toList();
        throw new ListErrorException(list);
    }

    private void validateRegister(String periodo, UUID idEntidad, int linea, List<DetalleError> detalleErrores) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth yearMonth = YearMonth.parse(periodo, formatter);

        LocalDate fechaInicio = yearMonth.atDay(1);
        LocalDate fechaFin = yearMonth.plusMonths(1).atDay(1);

        List<TsEstadosCuentaEntity> list = tsEstadosCuentaRepository.findByPeriodoAndEntidad(idEntidad, fechaInicio, fechaFin);

        if (!list.isEmpty()) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.ESTADO_CUENTA_IS_PRESENT));
            throwErrors(detalleErrores);
        }
    }

    private String validateMovimiento(String movimiento) {
        if (movimiento.contains("+")) {
            return "C";
        } else if (movimiento.contains("-")) {
            return "D";
        }
        return movimiento.toUpperCase();
    }


}
