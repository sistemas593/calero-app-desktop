package com.calero.lili.core.modRRHH.modRRHHCabecera;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoRubro;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modRRHH.RhPeriodosEntity;
import com.calero.lili.core.modRRHH.RhPeriodosRepository;
import com.calero.lili.core.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.core.modRRHH.modRRHHRublos.RubrosRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.utils.DateUtils;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CargarExcelAnualCabeceraServiceImpl {


    private final RhRolCabeceraRepository rhRolCabeceraRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final RubrosRepository rubrosRepository;
    private final RhPeriodosRepository rhPeriodosRepository;

    public void cargarRolCabeceraAnual(Long idData, Long idEmpresa, MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<RhRolCabeceraEntity> rolCabeceraLista = new ArrayList<>();
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


                if (Objects.nonNull(row.getCell(0))) {

                    Optional<GeTerceroEntity> tercero = geTercerosRepository.getFindExistByNumeroIdentificacion(idData,
                            row.getCell(0).getStringCellValue());

                    if (tercero.isPresent()) {

                        RhPeriodosEntity periodo = validarPeriodo(idData, idEmpresa, row, linea, detalleErrores);

                        if (Objects.nonNull(periodo)) {

                            List<RhRolCabeceraEntity> lista = rhRolCabeceraRepository.getAllPeriodo(idData, idEmpresa, periodo.getIdPeriodo());
                            if (lista.isEmpty()) {

                                setearCabecera(idData, idEmpresa, row, linea, detalleErrores, periodo, tercero.get(), mapRubros, rolCabeceraLista);

                            } else {
                                detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_PERIODO_GUARDADO,
                                        "El periodo :  " + row.getCell(25).getStringCellValue() + " ya tiene cabeceras guardadas"));
                            }

                        } else {
                            detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_PERIODO_NOT_FOUND,
                                    "No existe este periodo: " + row.getCell(25).getStringCellValue()));
                        }
                    } else {
                        detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.NUMERO_IDENTIFICACION_NOT_FOUND,
                                "El número de identificación " + row.getCell(0).getStringCellValue()));
                    }

                }

            }

            if (detalleErrores.isEmpty()) {
                rhRolCabeceraRepository.saveAll(rolCabeceraLista);
            } else {
                throwErrors(detalleErrores);
            }
        }
    }

    private void setearCabecera(Long idData, Long idEmpresa, Row row, int linea, List<DetalleError> detalleErrores,
                                RhPeriodosEntity periodo, GeTerceroEntity tercero, Map<String, RubrosEntity> mapRubros,
                                List<RhRolCabeceraEntity> rolCabeceraLista) {

        RhRolCabeceraEntity cabecera = new RhRolCabeceraEntity();
        List<RhRolDetalleEntity> detalles = new ArrayList<>();
        cabecera.setIdRol(UUID.randomUUID());
        cabecera.setIdData(idData);
        cabecera.setIdEmpresa(idEmpresa);

        cabecera.setDiasTrabajados(0);
        cabecera.setFechaGeneracion(LocalDate.now());
        cabecera.setPeriodos(periodo);
        cabecera.setTercero(tercero);

        setearDetalles(idData, idEmpresa, row, linea, detalleErrores, cabecera, mapRubros, detalles);
        validarValoresTotalesCabecera(cabecera, row, linea, detalleErrores, detalles);
        rolCabeceraLista.add(cabecera);
    }

    private void validarValoresTotalesCabecera(RhRolCabeceraEntity cabecera, Row row, int linea, List<DetalleError> detalleErrores, List<RhRolDetalleEntity> detalles) {


        if (!detalles.isEmpty()) {

            if (Objects.nonNull(row.getCell(1))) {
                BigDecimal valor = getValor(row, 1, detalleErrores, linea, EnumError.CAMPO_SUELDOS_Y_SALARIOS);
                cabecera.setSueldoBase(valor);
            }

            BigDecimal totalIngresos = detalles.stream()
                    .filter(item -> TipoRubro.I.equals(item.getRubros().getTipo()))
                    .map(RhRolDetalleEntity::getValor)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalEgresos = detalles.stream()
                    .filter(item -> TipoRubro.E.equals(item.getRubros().getTipo()))
                    .map(RhRolDetalleEntity::getValor)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal neto = totalIngresos.subtract(totalEgresos);
            cabecera.setTotalIngresos(totalIngresos);
            cabecera.setTotalDeducciones(totalEgresos);
            cabecera.setNetoPagar(neto);

        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.DETALLES_CAMPOS_NO_FOUND));
        }
    }


    private void setearDetalles(Long idData, Long idEmpresa, Row row, int linea, List<DetalleError> detalleErrores,
                                RhRolCabeceraEntity cabecera, Map<String, RubrosEntity> mapRubro, List<RhRolDetalleEntity> detalles) {


        for (Map.Entry<String, RubrosEntity> entry : mapRubro.entrySet()) {

            switch (entry.getKey()) {

                case "ING-001" ->
                        procesarDetalle(1, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_SUELDOS_Y_SALARIOS);

                case "ING-002" ->
                        procesarDetalle(2, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_SOBRESUELDOS_COMISIONES_Y_OTRAS_REMUNERACIONES_GRAVADAS_DE_I_RENTA);

                case "ING-015" ->
                        procesarDetalle(3, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_OTROS_INGRESOS_GRAVADOS_DE_I_RENTA);

                case "ING-009" ->
                        procesarDetalle(4, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DECIMO_TERCER_SUELDO);

                case "ING-010" ->
                        procesarDetalle(5, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DECIMO_CUARTO_SUELDO);

                case "ING-013" ->
                        procesarDetalle(6, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_FONDOS_DE_RESERVA);

                case "ING-018" ->
                        procesarDetalle(7, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_COMPENSACION_ECONOMICA_SALARIO_DIGNO);

                case "ING-016" ->
                        procesarDetalle(8, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_PARTICIPACION_UTILIDADES);

                case "ING-017" ->
                        procesarDetalle(9, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_OTROS_INGRESOS_EN_RELACION_DE_DEPENDENCIA_QUE_NO_CONSTITUYEN_RENTA_GRAVADA);

                case "DES-001" ->
                        procesarDetalle(10, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_APORTE_PERSONAL);

                case "GTP-001" ->
                        procesarDetalle(11, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DEDUCIBLE_VIVIENDA);

                case "GTP-002" ->
                        procesarDetalle(12, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DEDUCIBLE_SALUD);

                case "GTP-003" ->
                        procesarDetalle(14, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DEDUCIBLE_EDUCACION);

                case "GTP-004" ->
                        procesarDetalle(13, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DEDUCIBLE_ALIMENTACION);

                case "GTP-005" ->
                        procesarDetalle(15, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DEDUCIBLE_VESTIMENTA);

                case "GTP-006" ->
                        procesarDetalle(16, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_DEDUCIBLE_TURISMO);

                case "EXO-001" ->
                        procesarDetalle(17, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_REBAJAS_ESPECIALES_DISCAPACITADOS);

                case "EXO-002" ->
                        procesarDetalle(18, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_REBAJAS_ESPECIALES_TERCERA_EDAD);

                case "DES-008" ->
                        procesarDetalle(19, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_IMPUESTO_A_LA_RENTA_ASUMIDO_POR_EL_EMPLEADOR);

                case "OTE-001" ->
                        procesarDetalle(20, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_INGRESOS_GRAVADOS_OTROS_EMPLEADORES);

                case "OTE-002" ->
                        procesarDetalle(21, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_REBAJAS_OTROS_EMPLEADORES_IESS);

                case "BAS-001" ->
                        procesarDetalle(22, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_BASE_IMPONIBLE);

                case "RET-002" ->
                        procesarDetalle(23, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_VALOR_RETENIDO);

                case "OTE-003" ->
                        procesarDetalle(24, idData, idEmpresa, row, linea, detalles, entry.getValue(), detalleErrores, EnumError.CAMPO_RETENIDO_Y_ASUMIDO_POR_OTROS_EMPLEADORES);

            }
        }
        cabecera.setDetalles(detalles);

    }

    private void procesarDetalle(int celda, Long idData, Long idEmpresa, Row row, int linea, List<
                                         RhRolDetalleEntity> detalles,
                                 RubrosEntity rubros, List<DetalleError> detalleErrores, EnumError error) {


        if (Objects.nonNull(row.getCell(celda))) {

            RhRolDetalleEntity detalle = new RhRolDetalleEntity();
            detalle.setIdDetalle(UUID.randomUUID());
            detalle.setIdData(idData);
            detalle.setIdEmpresa(idEmpresa);
            detalle.setRubros(rubros);
            detalle.setValor(getValor(row, celda, detalleErrores, linea, error));
            detalles.add(detalle);

        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, error));
        }


    }

    private RhPeriodosEntity validarPeriodo(Long idData, Long idEmpresa, Row row, int linea, List<
            DetalleError> detalleErrores) {

        if (Objects.nonNull(row.getCell(25))) {

            LocalDate fecha = DateUtils.toLocalDate(row.getCell(25).getStringCellValue());
            Optional<RhPeriodosEntity> periodo = rhPeriodosRepository.findByFechaFin(idData, idEmpresa, fecha);

            return periodo.orElse(null);

        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.PARAMETRO_PERIODO_NOT_FOUND));
            return null;
        }

    }


    private Map<String, RubrosEntity> getMapRubros(Long idData, Long idEmpresa, List<DetalleError> errores) {

        List<String> codigosRubros = List.of("ING-001", "ING-002", "ING-015", "ING-009", "ING-010", "ING-013", "ING-018",
                "ING-016", "ING-017", "DES-001", "GTP-001", "GTP-002", "GTP-003", "GTP-004",
                "GTP-005", "GTP-006", "EXO-001", "EXO-002", "DES-008", "OTE-001", "RET-002",
                "OTE-003", "OTE-002", "BAS-001");

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

    private BigDecimal getValor(Row row, Integer celdaValor, List<DetalleError> errors, int linea, EnumError
            error) {
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



