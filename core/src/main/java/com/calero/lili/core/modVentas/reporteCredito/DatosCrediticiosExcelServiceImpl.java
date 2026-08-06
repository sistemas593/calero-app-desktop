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
import java.time.temporal.ChronoUnit;
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
     * @param idData    Parametro para busqueda.
     * @param idEmpresa Parametro de la empresa para busqueda.
     * @param file      MultipartFile, archivo de excel para guardar los datos crediticios.
     * @param periodo   Periodo en donde se va guardar los datos crediticios.
     * @throws IOException Propagación de los errores en caso de a verlos.
     */
    public void cargarDatosCrediticios(Long idData, Long idEmpresa, MultipartFile file, String periodo) throws IOException {


        DateUtils.validarPeriodoAnioMes(periodo);
        DateUtils.validarPeriodo(periodo);

        List<DetalleError> detalleErrores = new ArrayList<>();

        /*
          Se busca si el periodo ya existe, en caso de existir, se llena la lista de detalles de errores y se lanza la excepción
          para evitar que continue con el proceso.
         */
        Optional<DatosCrediticiosEntity> datosCrediticiosExistente = datosCrediticiosRepository.findByPeriodo(idData, idEmpresa, periodo);
        if (datosCrediticiosExistente.isPresent()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle(MessageFormat.format("El periodo {0} ya existe", periodo));
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);

        }

        /*
           Paso único: leer todas las filas en memoria una sola vez, de esta manera se obtiene el codigo de tercero
           en el excel y se manda a buscar en la base de datos

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

        /*
          Con los codigos se busca en la base de datos los terceros, y se van almacenando un map, que contiene el codigo
          y la entidad de tercero para posterior asignarse a cada registro en el detalle.

         */
        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllCodigosTercero(idData, new ArrayList<>(codigosUnicos))
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getCodigoTercero, Function.identity()));


        /*
           Se setea los datos en la cabecera, el periodo, idData e idEmpresa
         */
        DatosCrediticiosEntity entidad = new DatosCrediticiosEntity();
        entidad.setIdDatosCrediticios(UUID.randomUUID());
        entidad.setIdData(idData);
        entidad.setIdEmpresa(idEmpresa);
        entidad.setPeriodo(periodo);


        List<DatosCrediticiosDetalleEntity> listaDetalles = new ArrayList<>();

        /*
          Se realiza una iteración para ir leyendo en cada uno de los registros dentro del excel para poder realizar
          asi el seteo de cada detalle
         */
        for (FilaExcel fila : filas) {
            setearDetallesDesdeArreglo(mapTercero, entidad, fila.celdas(), fila.linea(), detalleErrores, listaDetalles);
        }

        entidad.setDatosCrediticiosDetalle(listaDetalles);

        /*
          En caso de que la lista de errores este llena se lanza, la excepción, caso contrario se envia a guardar
          la entidad generada a partir del excel.
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
     * @param mapTercero     Variable en forma de map para poder obtener los terceros mediante el codigo de tercero del archivo excel
     * @param entidad        Entidad de los datos crediticios
     * @param celdas         Valor de las celdas del excel
     * @param linea          Valor de la línea en iteracion en ese momento
     * @param detalleErrores Lista de un DTO que sirve para mostrar a los usuarios errores presentes en el documento del excel
     * @param listaDetalles  Lista de la entidad de detalles, para posterior setearla con la cabecera de DatosCrediticios
     */
    private void setearDetallesDesdeArreglo(Map<String, GeTerceroEntity> mapTercero, DatosCrediticiosEntity entidad,
                                            String[] celdas, int linea,
                                            List<DetalleError> detalleErrores, List<DatosCrediticiosDetalleEntity> listaDetalles) {

        /*
          Se realiza la instancia la entidad de DatosCrediticiosDetalleEntity para generar un nuevo detalle por cada registro del excel.
         */
        DatosCrediticiosDetalleEntity detalle = new DatosCrediticiosDetalleEntity();
        detalle.setIdData(entidad.getIdData());
        detalle.setIdEmpresa(entidad.getIdEmpresa());
        detalle.setIdDatosCrediticiosDetalle(UUID.randomUUID());

        /*
          Mediante el metodo "celda" se obtiene la celda donde se obtiene el valor de codigo de tercero, con el cual,
          con el map anterior, se obtiene el tercero, el cual en caso de encontrarse se setea, si no se lo encuentra
          se llena la lista de errores con el codigo y un mensaje de que no se encuentra, de igual forma si el codigo
          en el excel no se llegara a encontrar tambien se llena la lista de errores con el respectivo mensaje
         */

        /*
          Aquí se llena el campo de periodicidad de pago y plazo de operación, basados en los codigos del Enum
          DinarapPlazoOperacionEnum, basados en el código que se encuentre en el excel se coloca el número de días correspondiente.
          de igual forma si no se lo encuentra se llena la lista de errores.
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


        /*
          Aquí se obtiene el número de operación de y se lo setea en caso de no existir o ser vacio se llena la lista
          de errores con su correspondiente mensaje de error
         */
        String celda1 = celda(celdas, 1);
        if (celda1 != null) {
            detalle.setNumeroOperacion(celda1.replaceAll("\\s+", ""));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El número de la operación no se encuentra");
            detalleErrores.add(detalleError);
        }

        /*
          Aquí se obtiene la fecha de concesión (fecha emision factura), se la setea en caso no existir, se llena la
          lista de errores con su correspondiente mensaje de error.

         */

        obtenerFechaConcesion(celdas, linea, detalleErrores, detalle);


        /*
          Aquí se llena el valor de la operación y cuota de credito
          y el valor de operacion en un BigDecimal, el valor de operacion y cuota de credito se guardan con el mismo valor,
          los otros valores se guardan como cero, ya que estos se setean al momento de subir los saldos, de igual forma si no
          se encuentran las celdas necesarias se llena la lista de errores.

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
            detalleError.setDetalle("El valor de operación no se encuentra");
            detalleErrores.add(detalleError);
        }

        listaDetalles.add(detalle);
    }

    private LocalDate obtenerFechas(String[] celdas, int index) {
        String celda4 = celda(celdas, index);
        if (celda4 != null) {
            return DateUtils.toLocalDate(celda4);
        }
        return null;
    }


    /**
     * /**
     * Aquí se setea la fecha de concesión (fecha emision factura), se la setea en caso no existir, se llena la
     * lista de errores con su correspondiente mensaje de error.
     *
     * @param celdas         Valor de la celda de excel
     * @param linea          Valor del indíce donde se está iterando en este momento
     * @param detalleErrores Lista de errores que se muestra al usuario en caso de existir
     * @param detalle        Entidad de la base de datos donde se setea la fecha de concesión
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
     * Este metodo es usado para setear los datos correspondientes al tipo de documento
     * y término de pago, fechas de vencimiento y exigible, al igual que el valor de esLegal en la entidad, basandose en,
     * la validacion de los campos de la columna F, por el tipo de documento
     *
     * @param celdas         Valor de la celdas de excel
     * @param linea          Valor numerico que representa la iteración del excel
     * @param detalleErrores Lista de detalles de errores que en caso de existir se mostraran al usuario
     * @param detalle        Entidad de la base de datos, la misma es usada para setear los valores correspondientes.
     */
    private void validacionTerminoPago(String[] celdas, int linea, List<DetalleError> detalleErrores, DatosCrediticiosDetalleEntity detalle) {

        Integer diasMorosidad = obtenerDiasMorosidad(celdas);
        LocalDate fechaVencimientoExigible = obtenerFechas(celdas, 7);
        LocalDate fechaConcesion = obtenerFechas(celdas, 6);

        String celda5 = celda(celdas, 5);
        if (celda5 != null) {
            String celda11 = celda(celdas, 11);
            if (celda11 != null) {

                if (celda11.equals(DinarapPlazoOperacionEnum.I0.getCodigo())) {
                    setearInformacionTerminoPagoI0(linea, detalleErrores, detalle, diasMorosidad, fechaVencimientoExigible);
                } else {
                    setearInformacionCuandoExisteTerminoPago(linea, detalleErrores, detalle, celda11,
                            diasMorosidad, fechaVencimientoExigible, fechaConcesion);
                }


            } else {

                /*
                  Si es DZ y no tiene término de pago, los campos de periodicidad de pago y plazo de operacion deben
                  setearse con el valor de 1, y al campo de la entidad llamado esLegal, se lo debe setear como falso.

                  Si es DA, y no tiene término de pago, los campos de periodicidad de pago y plazo de operacion deben
                  setearse como nulos, y al campo de la entidad llamado esLegal, se lo debe setear como verdadero.
                 */


                if (celda5.equals("DA")) {
                    setearInformacionDASinTerminoPago(linea, detalleErrores, detalle, diasMorosidad, fechaVencimientoExigible);
                }

                if (celda5.equals("DZ")) {
                    setearInformacionDZSinTerminoPago(linea, detalleErrores, detalle, diasMorosidad, fechaVencimientoExigible);
                }

            }

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El tipo del documento no se encuentra");
            detalleErrores.add(detalleError);
        }
    }

    private void setearInformacionDASinTerminoPago(int linea, List<DetalleError> detalleErrores,
                                                   DatosCrediticiosDetalleEntity detalle, Integer diasMorosidad,
                                                   LocalDate fechaVencimientoExigible) {
        detalle.setPeriodicidadPago(null);
        detalle.setPlazoOperacion(null);
        detalle.setEstaLegal(Boolean.TRUE);

        if (Objects.nonNull(diasMorosidad)) {
            detalle.setDiasMorosidad(diasMorosidad);
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Los dias de mora no se encuentra");
            detalleErrores.add(detalleError);
        }

        if (Objects.nonNull(fechaVencimientoExigible)) {
            detalle.setFechaExigible(fechaVencimientoExigible);
            detalle.setFechaVencimiento(fechaVencimientoExigible);
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de vencimiento no se encuentra");
            detalleErrores.add(detalleError);
        }
    }

    private void setearInformacionDZSinTerminoPago(int linea, List<DetalleError> detalleErrores,
                                                   DatosCrediticiosDetalleEntity detalle, Integer diasMorosidad,
                                                   LocalDate fechaVencimientoExigible) {

        detalle.setPeriodicidadPago(1);
        detalle.setPlazoOperacion(1);
        detalle.setEstaLegal(Boolean.FALSE);
        if (Objects.nonNull(diasMorosidad)) {
            if (diasMorosidad == 0) {
                detalle.setDiasMorosidad(diasMorosidad);
            } else {
                detalle.setDiasMorosidad(diasMorosidad - 1);
            }
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Los dias de mora no se encuentra");
            detalleErrores.add(detalleError);
        }
        if (Objects.nonNull(fechaVencimientoExigible)) {
            detalle.setFechaExigible(fechaVencimientoExigible.plusDays(1));
            detalle.setFechaVencimiento(fechaVencimientoExigible.plusDays(1));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de vencimiento no se encuentra");
            detalleErrores.add(detalleError);
        }
    }

    /**
     * Si el valor de la celda es igual que I000, eso significa que se debe realizar lo siguiente:
     * 1. Seter el valor de los campos periodicidad de pago y plazo operacion como 1.
     * 2. A los días de morosidad restar un día, y setear ese valor obtenido en días de morosidad.
     * 3. A las fechas de vencimiento y fecha exigible, aumentar un día y la fecha resultante colocarles en dichos campos de la entidad.
     * 4. El campo llamado esLegal debe setearse como falso.
     *
     * @param linea                    Valor de iteración del excel
     * @param detalleErrores           Lista de errores para mostrar al usuario en caso de existan
     * @param detalle                  Entidad de la base donde se setea los valores
     * @param diasMorosidad            Días de morosidad que se guardan
     * @param fechaVencimientoExigible fecha del vencimiento y exigible que guardan
     */
    private void setearInformacionTerminoPagoI0(int linea, List<DetalleError> detalleErrores, DatosCrediticiosDetalleEntity detalle,
                                                Integer diasMorosidad, LocalDate fechaVencimientoExigible) {


        detalle.setPeriodicidadPago(1);
        detalle.setPlazoOperacion(1);
        detalle.setEstaLegal(Boolean.FALSE);
        if (Objects.nonNull(diasMorosidad)) {
            if (diasMorosidad == 0) {
                detalle.setDiasMorosidad(-1);
            } else {
                detalle.setDiasMorosidad(diasMorosidad - 1);
            }

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Los dias de mora no se encuentra");
            detalleErrores.add(detalleError);
        }
        if (Objects.nonNull(fechaVencimientoExigible)) {
            detalle.setFechaExigible(fechaVencimientoExigible.plusDays(1));
            detalle.setFechaVencimiento(fechaVencimientoExigible.plusDays(1));
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de vencimiento no se encuentra");
            detalleErrores.add(detalleError);
        }

    }


    /***
     *  se procede al seteo de la informacion de forma normal, obteniendo el valor de dias para el termino de pago
     *           de la lista que se tiene en el enumerador, la fechas exigibles y de vencimiento se guardan sin sumar dias,
     *           y el valor de días de morosidad se guarda exactamente el mismo.
     * @param linea                    Valor de iteración del excel
     * @param detalleErrores           Lista de errores para mostrar al usuario en caso de existan
     * @param detalle                  Entidad de la base donde se setea los valores
     * @param celda11                   Valor en String de la celda de la columna L del excel
     * @param diasMorosidad            Días de morosidad que se guardan
     * @param fechaVencimientoExigible Fecha del vencimiento y exigible que guardan
     */
    private void setearInformacionCuandoExisteTerminoPago(int linea, List<DetalleError> detalleErrores,
                                                          DatosCrediticiosDetalleEntity detalle, String celda11,
                                                          Integer diasMorosidad, LocalDate fechaVencimientoExigible,
                                                          LocalDate fechaConcesion) {
        try {

            // Se obtiene la diferencia de días entre la fecha de vencimiento exigible y la fecha de concesión
            // Esto se hace para calcular el plazo de operación, que es la cantidad de días entre estas dos fechas.
            // El valor de periodicidad de pago se obtiene del enumerador DinarapPlazoOperacionEnum, que contiene los días correspondientes a cada código.
            // ESTO POR EL MOMENTO HASTA CONFIRMAR LA INFORMACIÓN

            long diferenciaDias = Math.abs(ChronoUnit.DAYS.between(fechaVencimientoExigible, fechaConcesion));
            Integer dias = DinarapPlazoOperacionEnum.getDiasCredito(celda11);

            detalle.setPeriodicidadPago(dias);
            detalle.setPlazoOperacion(Math.toIntExact(diferenciaDias));

            detalle.setEstaLegal(Boolean.FALSE);

            if (Objects.nonNull(diasMorosidad)) {
                detalle.setDiasMorosidad(diasMorosidad);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("Los dias de mora no se encuentra");
                detalleErrores.add(detalleError);
            }
            if (Objects.nonNull(fechaVencimientoExigible)) {
                detalle.setFechaExigible(fechaVencimientoExigible);
                detalle.setFechaVencimiento(fechaVencimientoExigible);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de vencimiento no se encuentra");
                detalleErrores.add(detalleError);
            }
        } catch (Exception exception) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle(exception.getMessage());
            detalleErrores.add(detalleError);
        }

    }


    private Integer obtenerDiasMorosidad(String[] celdas) {
        String diasMorosidad = celda(celdas, 9);
        if (diasMorosidad != null) {
            return convertirEntero(diasMorosidad);

        }
        return null;
    }

    /**
     * Metodo para obtener la información en formato de String de cada celda del excel, mediante el indíce de la celda
     * en caso de ser vacia, devolvera un null.
     *
     * @param celdas Valor de las celdas del excel
     * @param idx    Indíce de las celdas
     * @return Retorno de un String para saber si la celda esta vacia o si tiene contenido
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
