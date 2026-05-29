package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
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
import java.util.ArrayList;
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
public class DatosCrediticiosExcelServiceImpl {

    private final DatosCrediticiosRepository datosCrediticiosRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;

    public void cargarDatosCrediticios(Long idData, Long idEmpresa, MultipartFile file, String fechaDatos) throws IOException {


        // Primer paso: recolectar códigos únicos de los clientes
        Set<String> codigosUnicos = new HashSet<>();
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
                        String codigo = row.getCell(0).getStringCellValue();
                        if (!codigo.isBlank()) codigosUnicos.add(codigo);
                    }
                }
            }
        }


        // Segundo paso: procesar filas con los datos de la consulta
        List<String> codigos = new ArrayList<>(codigosUnicos);
        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllCodigosTercero(idData, codigos)
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getCodigoTercero, Function.identity()));

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<DatosCrediticiosEntity> datosCrediticiosList = new ArrayList<>();
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

                DatosCrediticiosEntity entidad = new DatosCrediticiosEntity();
                entidad.setIdDatosCrediticios(UUID.randomUUID());
                entidad.setIdData(idData);
                entidad.setIdEmpresa(idEmpresa);
                entidad.setCodigoEntidad("");
                entidad.setFechaDatos(DateUtils.toLocalDate(fechaDatos));

                setearDetalles(mapTercero, entidad, row, linea, detalleErrores);
                datosCrediticiosList.add(entidad);

            }

            if (detalleErrores.isEmpty()) {
                datosCrediticiosRepository.saveAll(datosCrediticiosList);
            } else {
                throwErrors(detalleErrores);
            }
        }
    }

    private void setearDetalles(Map<String, GeTerceroEntity> mapTercero, DatosCrediticiosEntity entidad, Row row, int linea, List<DetalleError> detalleErrores) {

        List<DatosCrediticiosDetalleEntity> listaDetalles = new ArrayList<>();
        DatosCrediticiosDetalleEntity detalle = new DatosCrediticiosDetalleEntity();

        detalle.setIdData(entidad.getIdData());
        detalle.setIdEmpresa(entidad.getIdEmpresa());
        detalle.setIdDatosCrediticiosDetalle(UUID.randomUUID());

        if (Objects.nonNull(row.getCell(0))) {

            GeTerceroEntity tercero = mapTercero.get(row.getCell(0).getStringCellValue());
            if (Objects.nonNull(tercero)) {
                detalle.setTercero(tercero);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El codigo " + row.getCell(0).getStringCellValue() + "no se encuentra registrado");
                detalleErrores.add(detalleError);
            }

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El codigo del tercero no se encuentra");
            detalleErrores.add(detalleError);
        }


        if (Objects.nonNull(row.getCell(1))) {
            detalle.setNumeroOperacion(row.getCell(1).getStringCellValue());
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El número de la operación no se encuentra");
            detalleErrores.add(detalleError);
        }


        if (Objects.nonNull(row.getCell(3))) {
            detalle.setFechaConcesion(DateUtils.toLocalDate(row.getCell(3).getStringCellValue()));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de concesión no se encuentra");
            detalleErrores.add(detalleError);
        }

        if (Objects.nonNull(row.getCell(4))) {
            detalle.setFechaVencimiento(DateUtils.toLocalDate(row.getCell(4).getStringCellValue()));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de vencimiento no se encuentra");
            detalleErrores.add(detalleError);
        }


        if (Objects.nonNull(row.getCell(6)) && Objects.nonNull(row.getCell(7))) {

            BigDecimal valor = convetirValor(row.getCell(7).getStringCellValue());
            int diasMora = Integer.parseInt(row.getCell(6).getStringCellValue());
            int rango = Math.abs(diasMora);
            detalle.setValorOperacion(valor);

            if (esPositivo(diasMora)) {

                if (rango <= 30) {

                    detalle.setValorXVencer1a30Dias(valor);

                } else if (rango <= 90) {

                    detalle.setValorXVencer31a90Dias(valor);

                } else if (rango <= 180) {

                    detalle.setValorXVencer91a180Dias(valor);

                } else if (rango <= 360) {

                    detalle.setValorXVencer181a360Dias(valor);

                } else {

                    detalle.setValorXVencerMas360Dias(valor);
                }

            } else {

                if (rango <= 30) {

                    detalle.setValorVencido1a30Dias(valor);

                } else if (rango <= 90) {

                    detalle.setValorVencido31a90Dias(valor);

                } else if (rango <= 180) {

                    detalle.setValorVencido91a180Dias(valor);

                } else if (rango <= 360) {

                    detalle.setValorVencido181a360Dias(valor);

                } else {

                    detalle.setValorVencidoMas360Dias(valor);
                }
            }

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Los dias de mora no se encuentra");
            detalleErrores.add(detalleError);
        }

        listaDetalles.add(detalle);
        entidad.setDatosCrediticiosDetalle(listaDetalles);
    }

    private boolean esPositivo(Integer diasMora) {
        return diasMora >= 0;
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
