package com.calero.lili.api.modRRHH.modRRHHParametros;

import com.calero.lili.api.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.api.modRRHH.modRRHHParametros.builder.RhRolParametroBuilder;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CargarExcelParametrosServiceImpl {

    private final RolParametrosRepository rolParametrosRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final RubrosRepository rubrosRepository;
    private final RhRolParametroBuilder rhRolParametroBuilder;
    private final RhParametrosCargaRepository rhParametrosCargaRepository;

    // TODO CAMBIAR LA FORMA DE LA LOGICA DEL SWITCH PARA USAR DIRECTAMENTE TODOS LOS CODIGOS RUBROS Y EN SI NO USAR CODIGO RUBROS LA CLASE QUE SE REALIZO PARA ESO

    public void cargarParametrosExcel(Long idData, Long idEmpresa, MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<RhRolParametrosEntity> rhRolParametroLista = new ArrayList<>();
        List<DetalleError> detalleErrores = new ArrayList<>();
        List<RhRolParametrosCargasEntity> rhRolParametrosCargasLista = new ArrayList<>();

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


                if (Objects.nonNull(row.getCell(1))) {

                    Optional<GeTerceroEntity> tercero = geTercerosRepository.getFindExistByNumeroIdentificacion(idData,
                            row.getCell(1).getStringCellValue());
                    if (tercero.isPresent()) {

                        rolParametros(idData, idEmpresa, row, tercero.get(), detalleErrores, linea, rhRolParametroLista);
                        rolParametrosCarga(idData, idEmpresa, row, tercero.get(), detalleErrores, linea, rhRolParametrosCargasLista);

                    } else {
                        detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_TRABAJADOR_NOT_FOUND,
                                " Número de identificación: " + row.getCell(1).getStringCellValue()));
                    }

                } else {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.PARAMETRO_IDENTIFICACION_NOT_FOUND));
                }
            }

            if (detalleErrores.isEmpty()) {
                if (!rhRolParametroLista.isEmpty()) {
                    rolParametrosRepository.saveAll(rhRolParametroLista);
                }
                if (!rhRolParametrosCargasLista.isEmpty()) {
                    rhParametrosCargaRepository.saveAll(rhRolParametrosCargasLista);
                }
            } else {
                throwErrors(detalleErrores);
            }

        }
    }

    private void rolParametrosCarga(Long idData, Long idEmpresa, Row row, GeTerceroEntity tercero,
                                    List<DetalleError> detalleErrores, int linea, List<RhRolParametrosCargasEntity> lista) {

        List<RhRolParametrosCargasEntity> cargas = rhParametrosCargaRepository.findByAllTercero(tercero.getIdTercero());
        if (!cargas.isEmpty()) {

            validacionRolCargasExistentes(cargas, row, detalleErrores, tercero, linea, lista);
        } else {
            validarRolParametrosCargas(row, detalleErrores, tercero, linea, lista, idData, idEmpresa);
        }


    }

    private void validarRolParametrosCargas(Row row, List<DetalleError> detalleErrores, GeTerceroEntity tercero,
                                            int linea, List<RhRolParametrosCargasEntity> lista, Long idData, Long idEmpresa) {

        if (Objects.nonNull(row.getCell(2)) && Objects.nonNull(row.getCell(0))) {

            if (Integer.parseInt(row.getCell(2).getStringCellValue()) != 0) {
                RhRolParametrosCargasEntity newCarga = RhRolParametrosCargasEntity.builder()
                        .idCarga(UUID.randomUUID())
                        .tercero(tercero)
                        .anio(row.getCell(0).getStringCellValue())
                        .idData(idData)
                        .idEmpresa(idEmpresa)
                        .numeroCargas(Integer.parseInt(row.getCell(2).getStringCellValue()))
                        .build();
                lista.add(newCarga);
            }
        } else {
            detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_ANIO_Y_CARGA_NOT_FOUND, "Número de cargas"));
        }
    }

    private void validacionRolCargasExistentes(List<RhRolParametrosCargasEntity> cargas, Row row, List<DetalleError> detalleErrores,
                                               GeTerceroEntity tercero, int linea, List<RhRolParametrosCargasEntity> lista) {

        RhRolParametrosCargasEntity entidad = cargas.get(0);
        if (Objects.nonNull(row.getCell(2)) && Objects.nonNull(row.getCell(0))) {

            if (Integer.parseInt(row.getCell(2).getStringCellValue()) == 0) {
                rhParametrosCargaRepository.deleteAll(cargas);
            } else {
                if (!entidad.getNumeroCargas().equals(Integer.parseInt(row.getCell(2).getStringCellValue()))) {
                    RhRolParametrosCargasEntity newCarga = RhRolParametrosCargasEntity.builder()
                            .idCarga(UUID.randomUUID())
                            .tercero(tercero)
                            .anio(row.getCell(0).getStringCellValue())
                            .idData(entidad.getIdData())
                            .idEmpresa(entidad.getIdEmpresa())
                            .numeroCargas(Integer.parseInt(row.getCell(2).getStringCellValue()))
                            .build();
                    lista.add(newCarga);
                    rhParametrosCargaRepository.delete(entidad);
                }
            }
        } else {
            detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_ANIO_Y_CARGA_NOT_FOUND, "Número de cargas"));
        }


    }

    private void rolParametros(Long idData, Long idEmpresa, Row row, GeTerceroEntity tercero, List<DetalleError> detalleErrores, int linea, List<RhRolParametrosEntity> rhRolParametroLista) {
        List<RhRolParametrosEntity> parametros = rolParametrosRepository.findByAllTercero(tercero.getIdTercero());
        if (!parametros.isEmpty()) {
            validacionRolParametrosExistentes(parametros, row, detalleErrores, tercero, linea, rhRolParametroLista);
        } else {
            validacionRolParametros(row, detalleErrores, tercero, linea, rhRolParametroLista, idData, idEmpresa);
        }
    }

    private void validacionRolParametros(Row row, List<DetalleError> detalleErrores,
                                         GeTerceroEntity tercero, int linea, List<RhRolParametrosEntity> lista,
                                         Long idData, Long idEmpresa) {

        List<RubrosEntity> rubros = rubrosRepository.findAllList(idData, idEmpresa);

        for (RubrosEntity rubro : rubros) {

            switch (rubro.getCodigo()) {
                case "GTP-001" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 3, "Deducible de vivienda", linea, detalleErrores);

                }

                case "GTP-002" -> {

                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 4, "Deducible de salud", linea, detalleErrores);
                }

                case "GTP-003" -> {

                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 6, "Deducible alimentación", linea, detalleErrores);
                }

                case "GTP-004" -> {

                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 5, "Deducible de educación", linea, detalleErrores);
                }

                case "GTP-005" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 7, "Deducible vestimenta", linea, detalleErrores);
                }

                case "GTP-006" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 8, "Deducible turismo", linea, detalleErrores);
                }

                case "EXO-001" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 9, "Rebajas especiales discapacitados", linea, detalleErrores);
                }

                case "EXO-002" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 10, "Rebajas especiales tercera edad", linea, detalleErrores);
                }

                case "OTE-001" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 11, "Ingreso gravados otros empleadores", linea, detalleErrores);
                }


                case "OTE-002" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 12, "Iees otros empleados", linea, detalleErrores);
                }


                case "OTE-003" -> {
                    RhRolParametrosEntity entidad = rhRolParametroBuilder.builderNewParametro(tercero, rubro);
                    setearRolParametro(row, idData, idEmpresa, entidad, lista, 13, "Retenido y asumido otros empleadores", linea, detalleErrores);
                }
            }

        }

    }

    private void validacionRolParametrosExistentes(List<RhRolParametrosEntity> parametros,
                                                   Row row, List<DetalleError> detalleErrores,
                                                   GeTerceroEntity tercero, int linea, List<RhRolParametrosEntity> lista) {


        Map<String, RhRolParametrosEntity> mapCodigoParametro = getMapCodigoRubroParametro(parametros);

        for (String key : mapCodigoParametro.keySet()) {

            switch (key) {
                case "GTP-001" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 3, "Deducible de vivienda", linea, detalleErrores);
                }

                case "GTP-002" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 4, "Deducible de salud", linea, detalleErrores);
                }

                case "GTP-003" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 6, "Deducible alimentación", linea, detalleErrores);
                }

                case "GTP-004" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 5, "Deducible de educación", linea, detalleErrores);
                }

                case "GTP-005" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 7, "Deducible vestimenta", linea, detalleErrores);
                }

                case "GTP-006" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 8, "Deducible turismo", linea, detalleErrores);
                }

                case "EXO-001" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 9, "Rebajas especiales discapacitados", linea, detalleErrores);
                }

                case "EXO-002" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 10, "Rebajas especiales tercera edad", linea, detalleErrores);
                }

                case "OTE-001" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 11, "Ingreso gravados otros empleadores", linea, detalleErrores);
                }


                case "OTE-002" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 12, "Iees otros empleados", linea, detalleErrores);
                }


                case "OTE-003" -> {
                    RhRolParametrosEntity entidad = mapCodigoParametro.get(key);
                    updateRolParametro(row, tercero, entidad, lista, 13, "Retenido y asumido otros empleadores", linea, detalleErrores);
                }

            }
        }


    }

    private void updateRolParametro(Row row, GeTerceroEntity tercero, RhRolParametrosEntity entidad,
                                    List<RhRolParametrosEntity> lista, Integer celdaValor,
                                    String mensaje, int linea, List<DetalleError> detalleErrores) {

        if (Objects.nonNull(row.getCell(celdaValor)) && Objects.nonNull(row.getCell(0))) {

            BigDecimal valor = getValor(row, celdaValor, detalleErrores, linea);

            if (!valor.equals(entidad.getValor())) {
                RhRolParametrosEntity newParametro = rhRolParametroBuilder.builderNewParametro(tercero, entidad.getRubros());
                newParametro.setAnio(row.getCell(0).getStringCellValue());
                newParametro.setIdData(entidad.getIdData());
                newParametro.setIdEmpresa(entidad.getIdEmpresa());
                lista.add(newParametro);
                rolParametrosRepository.delete(entidad);
            }
        } else {
            detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_ANIO_Y_VALOR_NOT_FOUND, mensaje));
        }
    }


    private void setearRolParametro(Row row, Long idData, Long idEmpresa, RhRolParametrosEntity entidad,
                                    List<RhRolParametrosEntity> lista, Integer celdaValor,
                                    String mensaje, int linea, List<DetalleError> detalleErrores) {

        if (Objects.nonNull(row.getCell(celdaValor)) && Objects.nonNull(row.getCell(0))) {

            BigDecimal valor = getValor(row, celdaValor, detalleErrores, linea);

            entidad.setAnio(row.getCell(0).getStringCellValue());
            entidad.setIdData(idData);
            entidad.setIdEmpresa(idEmpresa);
            entidad.setValor(valor);

            lista.add(entidad);

        } else {
            detalleErrores.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_ANIO_Y_VALOR_NOT_FOUND, mensaje));
        }
    }

    private BigDecimal getValor(Row row, Integer celdaValor, List<DetalleError> errors, int linea) {
        if (Objects.isNull(celdaValor)) {
            errors.add(builderErrorMensajeAdicional(linea, EnumError.PARAMETRO_CELDA_NOT_FOUND, "La celda del valor esta vacía"));
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

    private Map<String, RhRolParametrosEntity> getMapCodigoRubroParametro(List<RhRolParametrosEntity> parametros) {
        return parametros.stream()
                .filter(p -> p.getRubros() != null)
                .filter(p -> p.getRubros().getCodigo() != null)
                .collect(Collectors.toMap(
                        p -> p.getRubros().getCodigo(),
                        Function.identity(),
                        (p1, p2) -> p1
                ));
    }


    private DetalleError builderErrorMensajeAdicional(int linea, EnumError enumError, String mensaje) {
        return new DetalleError(1, enumError, mensaje);
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

}



