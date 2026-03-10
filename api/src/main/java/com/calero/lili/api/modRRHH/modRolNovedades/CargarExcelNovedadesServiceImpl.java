package com.calero.lili.api.modRRHH.modRolNovedades;

import com.calero.lili.api.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.api.modRRHH.RhPeriodosEntity;
import com.calero.lili.api.modRRHH.RhPeriodosRepository;
import com.calero.lili.api.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.api.modRRHH.modRRHHRublos.RubrosRepository;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CargarExcelNovedadesServiceImpl {

    private final RhRolNovedadesRepository rhRolNovedadesRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final RubrosRepository rubrosRepository;
    private final RhRolNovedadesBuilder rhRolNovedadesBuilder;
    private final RhPeriodosRepository rhPeriodosRepository;

    public void cargarRolParametrosExcel(Long idData, Long idEmpresa, MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<RhRolNovedadesEntity> rhRolNovedadesLista = new ArrayList<>();
        List<DetalleError> detalleErrores = new ArrayList<>();


        Map<String, RubrosEntity> mapRubros = getMapRubros(idData, idEmpresa, detalleErrores);


        for (Sheet sheet : workbook) {

            boolean isHeader = true;
            for (Row row : sheet) {

                if (isRowEmpty(row)) {
                    continue;
                }

                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                if (Objects.nonNull(row.getCell(1)) && Objects.nonNull(row.getCell(0))) {

                    Optional<GeTerceroEntity> tercero = geTercerosRepository.getFindExistByNumeroIdentificacion(idData,
                            row.getCell(1).getStringCellValue());

                    if (tercero.isPresent()) {
                        List<RhRolNovedadesEntity> lista = rhRolNovedadesRepository.findByAllForPeriodo(idData, idEmpresa,
                                row.getCell(0).getStringCellValue());
                        if (!lista.isEmpty()) {
                            detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_PERIODO_GUARDADO,
                                    "El periodo :  " + row.getCell(0).getStringCellValue() + " ya tiene novedades guardadas"));
                        } else {
                            validarRubros(idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero.get(), mapRubros);
                        }
                    } else {
                        detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.NUMERO_IDENTIFICACION_NOT_FOUND,
                                "El número de identificación " + row.getCell(1).getStringCellValue()));
                    }

                }

            }

            if (detalleErrores.isEmpty()) {
                rhRolNovedadesRepository.saveAll(rhRolNovedadesLista);
            } else {
                throwErrors(detalleErrores);
            }
        }
    }


    private void validarRubros(Long idData, Long idEmpresa, Row row, int linea, List<DetalleError> detalleErrores,
                               List<RhRolNovedadesEntity> rhRolNovedadesLista, GeTerceroEntity tercero,
                               Map<String, RubrosEntity> mapRubro) {


        for (Map.Entry<String, RubrosEntity> entry : mapRubro.entrySet()) {

            switch (entry.getKey()) {

                case "ING-001" ->
                        procesarNovedad(2, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_SALARIO_NOT_FOUND);
                case "ING-002" ->
                        procesarNovedad(3, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_SOBRE_SUELDOS_NOT_FOUND);
                case "ING-015" ->
                        procesarNovedad(4, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_OTROS_INGRESOS_GRAVADOS_NOT_FOUND);
                case "ING-009" ->
                        procesarNovedad(5, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_DECIMO_TERCER_NOT_FOUND);
                case "ING-010" ->
                        procesarNovedad(6, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_DECIMO_CUARTO_NOT_FOUND);
                case "ING-013" ->
                        procesarNovedad(7, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_FONDOS_RESERVA_NOT_FOUND);
                case "ING-016" ->
                        procesarNovedad(8, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_PARTICIPACION_UTILIDADES_NOT_FOUND);
                case "ING-017" ->
                        procesarNovedad(9, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_OTROS_INGRESOS_NOT_FOUND);
                case "DES-001" ->
                        procesarNovedad(10, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_APORTE_IESS_NOT_FOUND);
                case "DES-008" ->
                        procesarNovedad(11, idData, idEmpresa, row, linea, detalleErrores, rhRolNovedadesLista, tercero, entry, EnumError.PARAMETRO_IMPUESTO_EMPLEADOR_NOT_FOUND);
            }
        }


    }

    private void procesarNovedad(int celda, Long idData, Long idEmpresa, Row row, int linea,
                                 List<DetalleError> detalleErrores, List<RhRolNovedadesEntity> rhRolNovedadesLista,
                                 GeTerceroEntity tercero, Map.Entry<String, RubrosEntity> entry, EnumError error) {

        if (Objects.nonNull(row.getCell(celda))) {

            RhRolNovedadesEntity rhRolNovedad = rhRolNovedadesBuilder
                    .builderRolNovedades(idData, idEmpresa, entry.getValue(), tercero);
            rhRolNovedad.setValor(getValor(row, celda, detalleErrores, linea, error));
            validarPeriodo(idData, idEmpresa, row, linea, detalleErrores, rhRolNovedad);
            rhRolNovedadesLista.add(rhRolNovedad);

        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, error));
        }

    }

    private void validarPeriodo(Long idData, Long idEmpresa, Row row,
                                int linea, List<DetalleError> detalleErrores,
                                RhRolNovedadesEntity rhRolNovedadesSueldo) {


        if (Objects.nonNull(row.getCell(0))) {

            Optional<RhPeriodosEntity> periodo = rhPeriodosRepository.findByPeriodo(idData, idEmpresa,
                    row.getCell(0).getStringCellValue());

            if (periodo.isPresent()) {
                rhRolNovedadesSueldo.setPeriodos(periodo.get());

            } else {
                detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_PERIODO_NOT_FOUND,
                        "No existe este periodo: " + row.getCell(0).getStringCellValue()));
            }

        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.PARAMETRO_PERIODO_NOT_FOUND));
        }

    }


    private Map<String, RubrosEntity> getMapRubros(Long idData, Long idEmpresa, List<DetalleError> errores) {

        List<String> codigosRubros = List.of("ING-001", "ING-002", "ING-015", "ING-009", "ING-010", "ING-013", "ING-016", "ING-017", "DES-001", "DES-008");

        Map<String, RubrosEntity> rubrosMap = codigosRubros.stream()
                .map(codigo -> Map.entry(
                        codigo,
                        rubrosRepository.getForFindCodigo(codigo, idEmpresa, idData)
                ))
                .peek(entry -> {
                    if (entry.getValue().isEmpty()) {
                        errores.add(builderErrorMensajeAdicional(0, EnumError.PARAMETRO_RUBRO_NOT_FOUND,
                                "No existe el rubro con código: " + entry.getKey()));
                    }
                })
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));

        if (!errores.isEmpty()) {
            throwErrors(errores);
        }

        return rubrosMap;

    }

    private DetalleError builderErrorMensajeAdicional(int linea, EnumError enumError, String mensaje) {
        return new DetalleError(linea, enumError, mensaje);
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

    private BigDecimal getValor(Row row, Integer celdaValor, List<DetalleError> errors, int linea, EnumError error) {
        if (Objects.isNull(celdaValor)) {
            errors.add(builderErrorMensajeAdicional(linea, error, "La celda del valor esta vacía"));
        }

        String valor = row.getCell(celdaValor).getStringCellValue();
        if (valor.isBlank()) {
            return BigDecimal.ZERO;
        }

        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');

            DecimalFormat df = new DecimalFormat("#,##0.##", symbols);
            df.setParseBigDecimal(true);

            return (BigDecimal) df.parse(valor);

        } catch (ParseException e) {
            return BigDecimal.ZERO;
        }
    }

}



