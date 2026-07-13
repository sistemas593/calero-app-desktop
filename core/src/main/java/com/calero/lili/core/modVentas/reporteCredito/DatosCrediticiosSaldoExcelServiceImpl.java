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
import java.time.YearMonth;
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


    /**
     * Metodo para cargar el archivo de excel que contiene la información de saldos (archivos de facturas)
     *
     * @param idData
     * @param idEmpresa
     * @param periodo
     * @param file
     * @throws IOException
     */
    public void cargarSaldoDatosCrediticios(Long idData, Long idEmpresa, String periodo, MultipartFile file) throws IOException {

        DateUtils.validarPeriodoAnioMes(periodo);
        DateUtils.validarPeriodo(periodo);

        List<DetalleError> detalleErrores = new ArrayList<>();

        /**
         * Se busca la cabecera por periodo, idData, idEmpresa, si no existe se lanza una excepción en forma de lista
         */
        Optional<DatosCrediticiosEntity> datosCrediticiosExistente = datosCabeceraRepository.findByPeriodo(idData, idEmpresa, periodo);
        if (datosCrediticiosExistente.isEmpty()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle(MessageFormat.format("No existe información previa de detalles en el periodo {0}, para llenar los saldos", periodo));
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);
        }


        /**
         * Paso único: leer todas las filas en memoria una sola vez para poder obtener el número de operación
         * de la totalidad de registros del excel.
         */
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

                    if (c0 != null && !c0.isBlank()) numerosOperacion.add(c0.replaceAll("\\s+", ""));
                }
            }
        }

        /**
         * Con los números de operacion se busca en la base de datos, y se genera un map que contiene el número de operacion
         * y el detalle correspondiente.
         */
        Map<String, DatosCrediticiosDetalleEntity> mapDatos =
                datosCrediticiosRepository.findAllNumeroOperacion(idData, idEmpresa,
                                datosCrediticiosExistente.get().getIdDatosCrediticios(),
                                new ArrayList<>(numerosOperacion))
                        .stream()
                        .collect(Collectors.toMap(DatosCrediticiosDetalleEntity::getNumeroOperacion, Function.identity()));


        List<DatosCrediticiosDetalleEntity> entidadesActualizar = new ArrayList<>();


        for (FilaExcel fila : filas) {
            if (fila.celda0() != null && fila.celda1() != null) {

                /**
                 * Aquí se realiza la busqueda en el map por medio del número de operacion presente en el excel y se lo setea
                 * en este caso, si no lo encuentra lo que se está realizando es continuar con el proceso no se llena
                 * la lista de errores.
                 */
                DatosCrediticiosDetalleEntity entidad = mapDatos.get(fila.celda0());
                if (entidad == null) continue;


                /**
                 * Aquí es donde entra la logica de seteo de los valores
                 * Primero convertimos el valor que viene el excel como BigDecimal
                 * Tomamos el campo de valor de días de morosidad que está en la entidad  y le asignamos a la variable rango,
                 * esto validando que sea un positivo
                 *
                 */

                BigDecimal saldo = convetirValor(fila.celda1());
                int rango = Math.abs(entidad.getDiasMorosidad());

                /**
                 * Aquí se valida si días de morosidad son positivos o no, en este caso, corresponde a como se debe setear los valores.
                 * Si en caso de ser positivos, este valor del saldo debe ir en los valores correspondientes a monto morosidad y
                 * los campos llamados valor vencido, si fueran negativos los valores que se deben llenar son los llamados
                 * valor por vencer (valorXVencer), el valor a setear siempre es el valor del saldo
                 */

                if (esPositivo(entidad.getDiasMorosidad())) {

                    /**
                     * Aquí se setea basandose en el valor del rango que corresponde a los días de mora,
                     * el valor a setear siempre es el valor del saldo independiente del número de días de la deuda
                     */

                    entidad.setMontoMorosidad(saldo);
                    if (rango <= 30) {
                        entidad.setValorVencido1a30Dias(saldo);
                    } else if (rango <= 90) {
                        entidad.setValorVencido31a90Dias(saldo);
                    } else if (rango <= 180) {
                        entidad.setValorVencido91a180Dias(saldo);
                    } else if (rango <= 360) {
                        entidad.setValorVencido181a360Dias(saldo);
                    } else {
                        entidad.setValorVencidoMas360Dias(saldo);
                    }

                    /**
                     * Aquí se valida la información ingresa anteriormente, correspondiente a la periodicidad del pago,
                     * plazo operacional y si se encuentra en legal o no, si en caso de que la periodicidad del pago,
                     * plazo operacional se encuentren como null y estado legal sea verdadero, se guardara el valor en deuda judicial
                     * además se setearan los valores de periodicidad del pago y plazo operacional como 45
                     */

                    // EN EL CASO DE QUE ESTA EN LEGAL SE TRUE SE DEBE COLOCAR LOS DATOS DE PERIODICIDAD Y PLAZO OPERACION EN 45
                    // Y SETER EL VALOR DEL SALDO EN LEGAL
                    if (Objects.isNull(entidad.getPeriodicidadPago()) && Objects.isNull(entidad.getPlazoOperacion())) {
                        if (entidad.getEstaLegal()) {
                            entidad.setPeriodicidadPago(45);
                            entidad.setPlazoOperacion(45);
                            entidad.setValorDemandaJudicial(saldo);

                            entidad.setFechaVencimiento(DateUtils.toPeriodoSaldo(periodo).minusDays(rango));
                            entidad.setFechaExigible(DateUtils.toPeriodoSaldo(periodo).minusDays(rango));
                            entidad.setFechaConcesion(entidad.getFechaVencimiento().minusDays(entidad.getPeriodicidadPago()));
                        }
                    }


                } else {

                    /**
                     * Aquí se setea basandose en el valor del rango que corresponde a los días de mora,
                     * el valor a setear siempre es el valor del saldo independiente del número de días de la deuda
                     */

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

                    /*entidad.setFechaVencimiento(DateUtils.toPeriodoSaldo(periodo).plusDays(rango));
                    entidad.setFechaExigible(DateUtils.toPeriodoSaldo(periodo).plusDays(rango));*/
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
