package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DatosCrediticiosExcelServiceImpl {

    private final DatosCrediticiosRepository datosCrediticiosRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;

    public void cargarDatosCrediticios(Long idData, Long idEmpresa, MultipartFile file, String periodo) throws IOException {

        Optional<DatosCrediticiosEntity> datosCrediticiosExistente = datosCrediticiosRepository.findByPeriodo(idData, idEmpresa, periodo);
        if (datosCrediticiosExistente.isPresent()) {
            throw new GeneralException(MessageFormat.format("El periodo {0} ya existe ", periodo));
        }

        // Paso único: leer todas las filas en memoria una sola vez
        record FilaExcel(int linea, String[] celdas) {}
        List<FilaExcel> filas = new ArrayList<>();
        Set<String> codigosUnicos = new HashSet<>();

        try (InputStream is = file.getInputStream();
             Workbook wb = StreamingReader.builder()
                     .rowCacheSize(500)
                     .bufferSize(65536)
                     .open(is)) {
            for (Sheet sheet : wb) {
                boolean isHeader = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;
                    if (isHeader) { isHeader = false; continue; }

                    int lastCell = row.getLastCellNum();
                    String[] celdas = new String[lastCell];
                    for (int i = 0; i < lastCell; i++) {
                        celdas[i] = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : null;
                    }
                    filas.add(new FilaExcel(row.getRowNum() + 1, celdas));

                    if (celdas[0] != null && !celdas[0].isBlank()) {
                        codigosUnicos.add(celdas[0]);
                    }
                }
            }
        }

        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllCodigosTercero(idData, new ArrayList<>(codigosUnicos))
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getCodigoTercero, Function.identity()));

        DatosCrediticiosEntity entidad = new DatosCrediticiosEntity();
        entidad.setIdDatosCrediticios(UUID.randomUUID());
        entidad.setIdData(idData);
        entidad.setIdEmpresa(idEmpresa);
        entidad.setCodigoEntidad("");
        entidad.setPeriodo(periodo);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<DatosCrediticiosDetalleEntity> listaDetalles = new ArrayList<>();

        for (FilaExcel fila : filas) {
            setearDetallesDesdeArreglo(mapTercero, entidad, fila.celdas(), fila.linea(), detalleErrores, listaDetalles);
        }

        entidad.setDatosCrediticiosDetalle(listaDetalles);

        if (detalleErrores.isEmpty()) {
            datosCrediticiosRepository.save(entidad);
        } else {
            throwErrors(detalleErrores);
        }
    }

    private void setearDetallesDesdeArreglo(Map<String, GeTerceroEntity> mapTercero, DatosCrediticiosEntity entidad,
                                            String[] celdas, int linea,
                                            List<DetalleError> detalleErrores, List<DatosCrediticiosDetalleEntity> listaDetalles) {

        DatosCrediticiosDetalleEntity detalle = new DatosCrediticiosDetalleEntity();
        detalle.setIdData(entidad.getIdData());
        detalle.setIdEmpresa(entidad.getIdEmpresa());
        detalle.setIdDatosCrediticiosDetalle(UUID.randomUUID());

        String celda0 = celda(celdas, 0);
        if (celda0 != null) {
            GeTerceroEntity tercero = mapTercero.get(celda0);
            if (Objects.nonNull(tercero)) {
                detalle.setTercero(tercero);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El codigo " + celda0 + " no se encuentra registrado");
                detalleErrores.add(detalleError);
            }
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El codigo del tercero no se encuentra");
            detalleErrores.add(detalleError);
        }

        String celda1 = celda(celdas, 1);
        if (celda1 != null) {
            detalle.setNumeroOperacion(celda1);
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El número de la operación no se encuentra");
            detalleErrores.add(detalleError);
        }

        String celda3 = celda(celdas, 3);
        if (celda3 != null) {
            detalle.setFechaConcesion(DateUtils.toLocalDate(celda3));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de concesión no se encuentra");
            detalleErrores.add(detalleError);
        }

        String celda4 = celda(celdas, 4);
        if (celda4 != null) {
            detalle.setFechaVencimiento(DateUtils.toLocalDate(celda4));
            detalle.setFechaExigible(DateUtils.toLocalDate(celda4));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de vencimiento no se encuentra");
            detalleErrores.add(detalleError);
        }

        // LOS VALORES VAN RELACIONADO CON LOS DIAS DE MORA
        // SI EL DIA DE MORA ES NEGATIVO EL VALOR DEBE IR EN LOS DIAS POR VENCER, Y SI ES POSITIVO DEBE IR EN LOS DIAS VENCIDOS
        // EN CASO DE SER NEGATIVOS LOS DIAS DE MORA LOS DIAS DE MOROSIDAD SON CERO Y SI SON POSITIVOS LOS DIAS SE SETEA LOS DIAS QUE ESTE EN EL EXCEL.
        String celda6 = celda(celdas, 6);
        String celda7 = celda(celdas, 7);
        if (celda6 != null && celda7 != null) {
            BigDecimal valor = convetirValor(celda7);
            int diasMora = convertirEntero(celda6);

            detalle.setDiasMorosidad(diasMora);
            detalle.setValorOperacion(valor);
            detalle.setCoutaCredito(valor);

            detalle.setValorVencido1a30Dias(BigDecimal.ZERO);
            detalle.setValorVencido31a90Dias(BigDecimal.ZERO);
            detalle.setValorVencido91a180Dias(BigDecimal.ZERO);
            detalle.setValorVencido181a360Dias(BigDecimal.ZERO);
            detalle.setValorVencidoMas360Dias(BigDecimal.ZERO);
            detalle.setValorXVencer1a30Dias(BigDecimal.ZERO);
            detalle.setValorXVencer31a90Dias(BigDecimal.ZERO);
            detalle.setValorXVencer91a180Dias(BigDecimal.ZERO);
            detalle.setValorXVencer181a360Dias(BigDecimal.ZERO);
            detalle.setValorXVencerMas360Dias(BigDecimal.ZERO);
            detalle.setMontoMorosidad(BigDecimal.ZERO);
            detalle.setMontoInteresMora(BigDecimal.ZERO);
            detalle.setCarteraCastigada(BigDecimal.ZERO);
            detalle.setValorDemandaJudicial(BigDecimal.ZERO);
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Los dias de mora no se encuentra");
            detalleErrores.add(detalleError);
        }

        listaDetalles.add(detalle);
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

    private Integer convertirEntero(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        valor = valor.trim();

        // Elimina puntos y comas usados como separadores de miles
        valor = valor.replace(".", "")
                .replace(",", "");

        return Integer.parseInt(valor);
    }
}
