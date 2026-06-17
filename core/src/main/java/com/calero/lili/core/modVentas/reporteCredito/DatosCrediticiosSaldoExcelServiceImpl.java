package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modTerceros.tercerosLegal.TerceroLegaRepository;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DatosCrediticiosSaldoExcelServiceImpl {


    private final DatosCrediticiosDetalleRepository datosCrediticiosRepository;
    private final DatosCrediticiosRepository datosCabeceraRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final TerceroLegaRepository terceroLegaRepository;

    public void cargarSaldoDatosCrediticios(Long idData, Long idEmpresa, String periodo, MultipartFile file) throws IOException {


        List<DetalleError> detalleErrores = new ArrayList<>();

        Optional<DatosCrediticiosEntity> datosCrediticiosExistente = datosCabeceraRepository.findByPeriodo(idData, idEmpresa, DateUtils.getPeriodo(periodo));
        if (datosCrediticiosExistente.isEmpty()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle(MessageFormat.format("No existe información previa de detalles en el periodo {0}, para llenar los saldos", periodo));
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);
        }


        // Paso único: leer todas las filas en memoria una sola vez
        record FilaExcel(int linea, String celda0, String celda1) {
        }
        List<FilaExcel> filas = new ArrayList<>();
        Set<String> numerosOperacion = new HashSet<>();

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

                    String c0 = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
                    String c1 = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                    filas.add(new FilaExcel(row.getRowNum() + 1, c0, c1));

                    if (c0 != null && !c0.isBlank()) numerosOperacion.add(c0);
                }
            }
        }

        Map<String, DatosCrediticiosDetalleEntity> mapDatos =
                datosCrediticiosRepository.findAllNumeroOperacion(idData, idEmpresa,
                                datosCrediticiosExistente.get().getIdDatosCrediticios(),
                                new ArrayList<>(numerosOperacion))
                        .stream()
                        .collect(Collectors.toMap(DatosCrediticiosDetalleEntity::getNumeroOperacion, Function.identity()));


        List<DatosCrediticiosDetalleEntity> entidadesActualizar = new ArrayList<>();

        for (FilaExcel fila : filas) {
            if (fila.celda0() != null && fila.celda1() != null) {
                DatosCrediticiosDetalleEntity entidad = mapDatos.get(fila.celda0());
                if (entidad == null) continue;

                BigDecimal saldo = convetirValor(fila.celda1());
                int rango = Math.abs(entidad.getDiasMorosidad());

                if (esPositivo(entidad.getDiasMorosidad())) {


                    entidad.setMontoMorosidad(saldo);
                    if (rango <= 30) {
                        entidad.setValorVencido1a30Dias(saldo);
                    } else if (rango <= 90) {
                        entidad.setValorVencido31a90Dias(saldo);
                    } else if (rango <= 180) {
                        entidad.setValorVencido91a180Dias(saldo);
                    } else if (rango <= 360) {
                        // EN EL CASO DE QUE EL NUMERO DE DIAS MOROSIDAD SUPERE LOS de 181  SE DEBE SETEAR EL MISMO VALOR
                        // EN VALOR DE DEMANDA JUDICIAL.

                        entidad.setValorVencido181a360Dias(saldo);
                        if (Objects.isNull(entidad.getPeriodicidadPago()) && Objects.isNull(entidad.getPlazoOperacion())) {
                            entidad.setPeriodicidadPago(45);
                            entidad.setPlazoOperacion(45);
                            entidad.setValorDemandaJudicial(saldo);
                        }


                    } else {
                        // EN EL CASO DE QUE EL NUMERO DE DIAS MOROSIDAD SUPERE LOS de 180 a 360 DIAS, SE DEBE SETEAR EL MISMO VALOR
                        // EN VALOR DE DEMANDA JUDICIAL.
                        /*if (listaIdTerceroLegal.contains(entidad.getTercero().getIdTercero())) {
                            entidad.setValorDemandaJudicial(saldo);
                        }*/
                        entidad.setValorVencidoMas360Dias(saldo);
                        if (Objects.isNull(entidad.getPeriodicidadPago()) && Objects.isNull(entidad.getPlazoOperacion())) {
                            entidad.setPeriodicidadPago(45);
                            entidad.setPlazoOperacion(45);
                            entidad.setValorDemandaJudicial(saldo);
                        }
                    }
                } else {
                    if (rango <= 30) {
                        entidad.setValorXVencer1a30Dias(saldo);
                    } else if (rango <= 90) {
                        entidad.setValorXVencer31a90Dias(saldo);
                    } else if (rango <= 180) {
                        entidad.setValorXVencer91a180Dias(saldo);
                    } else if (rango <= 360) {
                        entidad.setValorXVencer181a360Dias(saldo);
                    } else {
                        entidad.setValorXVencerMas360Dias(saldo);
                    }
                }

                entidad.setSaldoOperacion(saldo);
                entidadesActualizar.add(entidad);

            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
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


    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            if (row.getCell(c) != null) {
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


    private boolean esPositivo(Integer diasMora) {
        return diasMora > 0;
    }
}
