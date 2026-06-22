package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentas.reporteCredito.enums.DinarapPlazoOperacionEnum;
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
import java.time.LocalDate;
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


    /**
     * Metodo para cargar el archivo de excel que contiene la información principal (archivos de facturas) para el reporte
     *
     * @param idData
     * @param idEmpresa
     * @param file
     * @param periodo
     * @throws IOException
     */
    public void cargarDatosCrediticios(Long idData, Long idEmpresa, MultipartFile file, String periodo) throws IOException {

        List<DetalleError> detalleErrores = new ArrayList<>();

        /**
         * Se busca si el periodo ya existe, en caso de existir, se llena la lista de detalles de errores y se lanza la excepción
         * para evitar que continue con el proceso.
         */
        Optional<DatosCrediticiosEntity> datosCrediticiosExistente = datosCrediticiosRepository.findByPeriodo(idData, idEmpresa, DateUtils.getPeriodo(periodo));
        if (datosCrediticiosExistente.isPresent()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle(MessageFormat.format("El periodo {0} ya existe", periodo));
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);

        }

        /**
         *  Paso único: leer todas las filas en memoria una sola vez, de esta manera se obtiene el codigo de tercero
         *  en el excel y se manda a buscar en la base de datos
         *
         */

        record FilaExcel(int linea, String[] celdas) {
        }
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

                    if (celdas[0] != null && !celdas[0].isBlank()) {
                        codigosUnicos.add(celdas[0]);
                    }
                }
            }
        }

        /**
         * Con los codigos se busca en la base de datos los terceros, y se van almacenando un map, que contiene el codigo
         * y la entidad de tercero para posterior asignarse a cada registro en el detalle.
         *
         */
        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllCodigosTercero(idData, new ArrayList<>(codigosUnicos))
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getCodigoTercero, Function.identity()));


        /**
         *  Se setea los datos en la cabecera, el periodo, idData y idEmpresa
         */
        DatosCrediticiosEntity entidad = new DatosCrediticiosEntity();
        entidad.setIdDatosCrediticios(UUID.randomUUID());
        entidad.setIdData(idData);
        entidad.setIdEmpresa(idEmpresa);
        entidad.setPeriodo(DateUtils.getPeriodo(periodo));


        List<DatosCrediticiosDetalleEntity> listaDetalles = new ArrayList<>();

        /**
         * Se realiza una iteracion para ir leyendo en cada uno de los registros dentro del excel para poder realizar
         * asi el seteo de cada detalle
         */
        for (FilaExcel fila : filas) {
            setearDetallesDesdeArreglo(mapTercero, entidad, fila.celdas(), fila.linea(), detalleErrores, listaDetalles);
        }

        entidad.setDatosCrediticiosDetalle(listaDetalles);

        /**
         * En caso de que la lista de errores este llena se lanza, la excepción, caso contrario se envia a guardar
         * la entidad generada a partir del excel.
         */

        if (detalleErrores.isEmpty()) {
            datosCrediticiosRepository.save(entidad);
        } else {
            throwErrors(detalleErrores);
        }
    }

    /**
     * Metodo para setear los detalles por cada registro del Excel, y en caso de encontrar un error guardarlo en la lista de errores
     *
     * @param mapTercero
     * @param entidad
     * @param celdas
     * @param linea
     * @param detalleErrores
     * @param listaDetalles
     */
    private void setearDetallesDesdeArreglo(Map<String, GeTerceroEntity> mapTercero, DatosCrediticiosEntity entidad,
                                            String[] celdas, int linea,
                                            List<DetalleError> detalleErrores, List<DatosCrediticiosDetalleEntity> listaDetalles) {

        /**
         * Se realiza la instancia la entidad de DatosCrediticiosDetalleEntity para generar un nuevo detalle por cada registro del excel.
         */
        DatosCrediticiosDetalleEntity detalle = new DatosCrediticiosDetalleEntity();
        detalle.setIdData(entidad.getIdData());
        detalle.setIdEmpresa(entidad.getIdEmpresa());
        detalle.setIdDatosCrediticiosDetalle(UUID.randomUUID());

        /**
         * Mediante el metodo "celda" se obtiene la celda donde se obtiene el valor de codigo de tercero, con el cual,
         * con el map anterior, se obtiene el tercero, el cual en caso de encontrarse se setea, si no se lo encuentra
         * se llena la lista de errores con el codigo y un mensaje de que no se lo encontro, de igual forma si el codigo
         * en el excel no se llegara a encontrar tambien se llena la lista de errores con el respectivo mensaje
         */

        /**
         * Aquí se llena el campo de periodicidad de pago y plazo de operación, basandose en los codigos del Enum
         * DinarapPlazoOperacionEnum, basandose en el código que se encuentre en el excel se coloca el número de días correspondiente.
         * de igual forma si no se lo encuentra se llena la lista de errores.
         */
        validacionTerminoPago(celdas, linea, detalleErrores, detalle);


        String celda0 = celda(celdas, 0);
        if (celda0 != null) {
            GeTerceroEntity tercero = mapTercero.get(celda0);
            if (Objects.nonNull(tercero)) {
                detalle.setTercero(tercero);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El codigo del cliente " + celda0 + " no se encuentra registrado");
                detalleErrores.add(detalleError);
            }
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El codigo del tercero no se encuentra");
            detalleErrores.add(detalleError);
        }


        /**
         * Aquí se obtiene el número de operación de y se lo setea en caso de no existir o ser vacio se llena la lista
         * de errores con su correspondiente mensaje de error
         */
        String celda1 = celda(celdas, 1);
        if (celda1 != null) {
            detalle.setNumeroOperacion(celda1);
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El número de la operación no se encuentra");
            detalleErrores.add(detalleError);
        }

        /**
         * Aquí se obtiene la fecha de concesión (fecha emision factura), se la setea en caso no existir, se llena la
         * lista de errores con su correspondiente mensaje de error.
         *
         */

        obtenerFechaConcesion(celdas, linea, detalleErrores, detalle);

        /**
         * Aquí se llena el campo del estado legal, este estado corresponde a un boleano que en referencia a 3 códigos
         * (RV, DZ, DA) se coloca como verdadero o falso, si el codigo es RV o DZ el boleano se llena como falso, si
         * el codigo es DA se coloca como verdadero, esto es para que posteriormente cuando se suba el archivo de saldos
         * se coloquen los valores de deuda judicial correctamente, asi mismo de no encontrarse este valor se llenará
         * la lista de errores con el mensaje correspondiente.
         *
         * NOTA: RV corresponde a facturas, mientras que DA y DZ corresponden a provisiones.
         */

        // EN EL ARCHIVO SI EL TIPO DE RV O DZ, NO SE ENCUENTRA EN LEGAL, PERO SI ES DA SI SE ENCUENTRA Y SE DEBE SETEAR COMO TRUE PARA
        // POSTERIORMENTE VALIDAR ESTE DATO AL SUBIR LOS SALDOS.
        String celda5 = celda(celdas, 5);
        if (celda5 != null) {

            switch (celda5) {
                case "RV":
                case "DZ":
                    detalle.setEstaLegal(Boolean.FALSE);
                    break;
                case "DA":
                    detalle.setEstaLegal(Boolean.TRUE);
                    break;
            }
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El tipo del documento no se encuentra");
            detalleErrores.add(detalleError);
        }


        /**
         * Aquí se llena el valor de la operación y cuota de credito
         * y el valor de operacion en un BigDecimal, el valor de operacion y cuota de credito se guardan con el mismo valor,
         * los otros valores se guardan como cero, ya que estos se setean al momento de subir los saldos, de igual forma si no
         * se encuentran las celdas necesarias se llena la lista de errores.
         *
         */

        String celda7 = celda(celdas, 10);
        if (celda7 != null) {
            BigDecimal valor = convetirValor(celda7);

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

    private LocalDate obtenerFechaVencimientoYExigible(String[] celdas, int linea, List<DetalleError> detalleErrores) {
        String celda4 = celda(celdas, 7);
        if (celda4 != null) {
            return DateUtils.toLocalDate(celda4);

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de vencimiento no se encuentra");
            detalleErrores.add(detalleError);
        }
        return null;
    }


    /**
     * /**
     * Aquí se setea la fecha de concesión (fecha emision factura), se la setea en caso no existir, se llena la
     * lista de errores con su correspondiente mensaje de error.
     *
     * @param celdas
     * @param linea
     * @param detalleErrores
     * @param detalle
     */
    private void obtenerFechaConcesion(String[] celdas, int linea, List<DetalleError> detalleErrores, DatosCrediticiosDetalleEntity detalle) {
        String celda3 = celda(celdas, 6);
        if (celda3 != null) {
            detalle.setFechaConcesion(DateUtils.toLocalDate(celda3));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de concesión no se encuentra");
            detalleErrores.add(detalleError);
        }
    }


    /**
     * Validacion del termino de pago
     *
     * @param celdas
     * @param linea
     * @param detalleErrores
     * @param detalle
     */
    private void validacionTerminoPago(String[] celdas, int linea, List<DetalleError> detalleErrores, DatosCrediticiosDetalleEntity detalle) {
        String celda8 = celda(celdas, 11);
        if (celda8 != null) {
            try {

                /**
                 * Si el valor de la celda es igual que I000, eso significa que se debe realizar lo siguiente:
                 * 1. Seter el valor de los campos periodicidad de pago y plazo operacion como 1.
                 * 2. A los días de morosidad restar un día, y setear ese valor obtenido en días de morosidad.
                 * 3. A las fechas de vencimiento y fecha exigible, aumentar un día y la fecha resultante colocarles en dichos campos de la entidad.
                 * 4. El campo llamado esLegal debe setearse como falso.
                 */

                Integer diasMorosidad = obtenerDiasMorosidad(celdas, linea, detalleErrores);
                LocalDate fechaVencimientoExigible = obtenerFechaVencimientoYExigible(celdas, linea, detalleErrores);

// TODO REVISAR CORRESPONDIENTE EL TEMA DE LA FECHA POR QUE ESTOY DEVOLVIENDO UN NULL Y PUEDE DAR UN NULL POINTER
                if (celda8.equals(DinarapPlazoOperacionEnum.I0.getCodigo())) {

                    detalle.setPeriodicidadPago(1);
                    detalle.setPlazoOperacion(1);
                    detalle.setDiasMorosidad(diasMorosidad - 1);
                    detalle.setFechaExigible(fechaVencimientoExigible.plusDays(1));
                    detalle.setFechaVencimiento(fechaVencimientoExigible.plusDays(1));

                }


                Integer dias = DinarapPlazoOperacionEnum.getDiasCredito(celda8);
                detalle.setPeriodicidadPago(dias);
                detalle.setPlazoOperacion(dias);
            } catch (Exception exception) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle(exception.getMessage());
                detalleErrores.add(detalleError);
            }
        } else {
            detalle.setPeriodicidadPago(null);
            detalle.setPlazoOperacion(null);
        }
    }

    private Integer obtenerDiasMorosidad(String[] celdas, int linea, List<DetalleError> detalleErrores) {
        String diasMorosidad = celda(celdas, 9);
        if (diasMorosidad != null) {
            return convertirEntero(diasMorosidad);

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Los dias de mora no se encuentra");
            detalleErrores.add(detalleError);
        }
        return 0;
    }

    /**
     * Metodo para obtener la información en formato de String de cada celda del excel, mediante el indíce de la celda
     * en caso de ser vacia, devolvera un null.
     *
     * @param celdas
     * @param idx
     * @return
     */
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
