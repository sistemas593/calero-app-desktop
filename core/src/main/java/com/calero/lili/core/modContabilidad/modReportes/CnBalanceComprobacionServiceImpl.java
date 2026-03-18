package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import com.calero.lili.core.modContabilidad.modReportes.builder.BalanceValoresBuilder;
import com.calero.lili.core.modContabilidad.modReportes.dto.BalanceComprobacionPdfDto;
import com.calero.lili.core.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.core.modContabilidad.modReportes.projection.BalanceValoresProjection;
import com.calero.lili.core.modContabilidad.modReportes.projection.SaldoInicialBCProjection;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.utils.ConstanteReportes;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor

public class CnBalanceComprobacionServiceImpl {


    private final AdEmpresasRepository adEmpresasRepository;
    private final CnReportesRepository cnReportesRepository;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final BalanceValoresBuilder balanceValoresBuilder;
    private final MayorizacionServiceImpl mayorizacionService;


    /**
     * Genera y devuelve una respuesta paginada con el Balance de Comprobación para la empresa y rango de fechas indicados.
     * <p>
     * Realiza las siguientes operaciones:
     * - Valida las fechas de búsqueda.
     * - Obtiene saldos iniciales y movimientos desde los repositorios según la lógica del mes inicial.
     * - Integra información de cuentas y aplica la mayorización.
     * - Construye y devuelve una página con los resultados filtrados según el parámetro {@code pageable}.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechas, cuenta inicial/final, sucursal, mayor)
     * @param pageable  parámetros de paginación y ordenamiento
     * @return PaginatedDto\<BalanceValoresDto> con la página de resultados del balance
     * @throws GeneralException cuando la validación de fechas falla u ocurre un error en la obtención de datos
     */
    public PaginatedDto<BalanceValoresDto> getBalanceComprobacion(Long idData, Long idEmpresa,
                                                                  CnPlanCuentaListFilterDto filters, Pageable pageable) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());
        List<SaldoInicialBCProjection> listSaldoInicial = null;

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<BalanceValoresProjection> listValores = cnReportesRepository.getValoresBalanceComprobacion(idData, idEmpresa, filters.getSucursal(),
                filters.getCuentaInicial(), validacionCuentaFinal(filters), filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta());


        List<CnPlanCuentaEntity> listCuentas = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listMayores = mayorizacionService.obtenerMayorTotalizadores(cuentasOriginalesMayores);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listCuentas, listMayores);


        /**
         * El cálculo del saldo inicial varía según el mes de inicio del período:
         * Si inicia en enero, se consideran los saldos iniciales por tipo de asiento.
         * Si inicia en otro mes, el saldo inicial se calcula acumulando movimientos desde el primero de enero hasta un dia antes de la fecha inicial de busqueda.
         */
        if (DateUtils.validacionFechaEnero(filters.getFechaEmisionDesde())) {

            listSaldoInicial = cnReportesRepository.getSaldosInicialesBalanceConTipoAsiento(idData, idEmpresa, filters.getSucursal(),
                    filters.getCuentaInicial(), validacionCuentaFinal(filters), filters.getFechaEmisionDesde(),
                    filters.getFechaEmisionHasta());

        } else {

            listSaldoInicial = cnReportesRepository.getSaldosInicialesBalanceComprobacionSinTipoAsiento(idData, idEmpresa,
                    filters.getSucursal(), filters.getCuentaInicial(), validacionCuentaFinal(filters),
                    DateUtils.getFechaInicio(filters.getFechaEmisionDesde()), getFechaFinalSaldo(filters.getFechaEmisionDesde()));
        }


        List<BalanceValoresDto> resultado = getListaValores(listValores, listSaldoInicial, merged, filters.getMayor());

        return mayorizacionService.getPageResponse(validarListaResultados(resultado, filters), pageable);

    }

    /**
     * Genera y devuelve el Balance de Comprobación en formato PDF para la empresa y rango de fechas indicados.
     * <p>
     * Realiza la misma lógica de obtención y consolidación de saldos y movimientos que
     * el método paginado, luego crea el PDF mediante JasperReports.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechas, cuenta inicial/final, sucursal, mayor)
     * @return arreglo de bytes con el contenido PDF generado
     * @throws GeneralException cuando sucede un error al generar el PDF o al obtener datos requeridos
     */
    public byte[] getBalanceComprobacionPdf(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {


        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());
        List<SaldoInicialBCProjection> listSaldoInicial = null;


        List<BalanceValoresProjection> listValores = cnReportesRepository.getValoresBalanceComprobacion(idData, idEmpresa, filters.getSucursal(),
                filters.getCuentaInicial(), validacionCuentaFinal(filters), filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta());


        List<CnPlanCuentaEntity> listCuentas = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listMayores = mayorizacionService.obtenerMayorTotalizadores(listCuentas);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listCuentas, listMayores);

        if (DateUtils.validacionFechaEnero(filters.getFechaEmisionDesde())) {

            listSaldoInicial = cnReportesRepository.getSaldosInicialesBalanceConTipoAsiento(idData, idEmpresa, filters.getSucursal(),
                    filters.getCuentaInicial(), validacionCuentaFinal(filters), filters.getFechaEmisionDesde(),
                    filters.getFechaEmisionHasta());

        } else {

            listSaldoInicial = cnReportesRepository.getSaldosInicialesBalanceComprobacionSinTipoAsiento(idData, idEmpresa,
                    filters.getSucursal(), filters.getCuentaInicial(), validacionCuentaFinal(filters),
                    DateUtils.getFechaInicio(filters.getFechaEmisionDesde()), getFechaFinalSaldo(filters.getFechaEmisionDesde()));

        }

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));


        List<BalanceValoresDto> resultado = getListaValores(listValores, listSaldoInicial,
                merged, filters.getMayor());


        return generatePDFBalanceComprobacion(validarListaResultados(resultado, filters), empresa, filters);
    }

    /**
     * Valida y retorna la cuenta final a usar en las consultas a partir de los filtros.
     * <p>
     * - Si {@code filters.getCuentaInicial()} es nulo retorna {@code null}.
     * - Si {@code filters.getCuentaFinal()} es nulo o vacío, retorna {@code filters.getCuentaInicial()}.
     * - En caso contrario retorna {@code filters.getCuentaFinal()}.
     *
     * @param filters filtros que contienen cuenta inicial y cuenta final
     * @return cadena con la cuenta final válida o {@code null} si no aplica
     */
    private String validacionCuentaFinal(CnPlanCuentaListFilterDto filters) {

        if (Objects.isNull(filters.getCuentaInicial())) {
            return null;
        }

        if (Objects.isNull(filters.getCuentaFinal()) || filters.getCuentaFinal().isBlank()) {
            return filters.getCuentaInicial();
        } else {
            return filters.getCuentaFinal();
        }

    }

    /**
     * Obtiene la fecha final para el cálculo del saldo inicial cuando no se tiene en cuenta
     * el tipo de asiento. Devuelve un día antes de {@code fechaEmisionDesde}.
     *
     * @param fechaEmisionDesde fecha desde la cual inicia la búsqueda
     * @return fecha final para cálculo de saldo inicial ({@code fechaEmisionDesde.minusDays(1)})
     */
    private LocalDate getFechaFinalSaldo(LocalDate fechaEmisionDesde) {
        return fechaEmisionDesde.minusDays(1);
    }

    /**
     * Construye la lista de {@link BalanceValoresDto} a partir de las proyecciones consultadas y los saldos iniciales,
     * aplicando la lógica de mayorización y agregación sobre el mapa de cuentas.
     * <p>
     * Pasos principales:
     * - Convierte proyecciones a DTOs mediante el builder.
     * - Aplica saldos iniciales y movimientos (debe/haber) sobre las cuentas a mayorizar.
     * - Crea el total general y devuelve la lista ordenada resultante.
     *
     * @param listaValores     lista de proyecciones con valores de movimientos (debe/haber)
     * @param listSaldoInicial lista de proyecciones con saldos iniciales
     * @param todasLasCuentas  lista de entidades de plan de cuentas de la empresa
     * @param mayor            indicador para incluir o no cuentas mayores en el resultado
     * @return lista de {@link BalanceValoresDto} ya mayorizada y lista para presentación
     */
    private List<BalanceValoresDto> getListaValores(List<BalanceValoresProjection> listaValores,
                                                    List<SaldoInicialBCProjection> listSaldoInicial,
                                                    List<CnPlanCuentaEntity> todasLasCuentas,
                                                    Boolean mayor) {


        Map<String, BalanceValoresDto> mapCuentasOriginales = mayorizacionService.getMapBalanceCuentas(todasLasCuentas);

        List<BalanceValoresDto> listValoresDto = listaValores.stream()
                .map(balanceValoresBuilder::builderBalanceComprobacionCuenta)
                .toList();

        List<BalanceValoresDto> listSaldoInicialDto = listSaldoInicial.stream()
                .map(balanceValoresBuilder::builderBalanceComprobacionCuentaSaldoInicial)
                .toList();


        for (BalanceValoresDto saldoBalance : listSaldoInicialDto) {

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(saldoBalance.getCodigoCuentaOriginal()),
                    ConstanteReportes.saldoInicial, saldoBalance.getSaldoInicial());
        }

        for (BalanceValoresDto debeBalance : listValoresDto) {

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(debeBalance.getCodigoCuentaOriginal()),
                    ConstanteReportes.debe, debeBalance.getDebe());

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(debeBalance.getCodigoCuentaOriginal()),
                    ConstanteReportes.haber, debeBalance.getHaber());

        }

        BalanceValoresDto x = mapCuentasOriginales.get("1.1.1.01.008");

        System.out.println(x);

        crearTotalGeneral(mapCuentasOriginales);

        return mayorizacionService.getBalanceValoresOrdenado(mapCuentasOriginales);
    }


    /**
     * Calcula y agrega al mapa proporcionado el registro "Total general" con la suma de
     * saldo inicial, debe, haber y saldo final de todas las cuentas (omitiendo códigos marcados).
     * <p>
     * El resultado se inserta en el mapa con clave vacía ("") para ser mostrado en el conjunto.
     *
     * @param lista mapa de cuentas (clave -> BalanceValoresDto) sobre el que se agregará el total general
     */
    private void crearTotalGeneral(Map<String, BalanceValoresDto> lista) {


        BigDecimal totalSaldoInicial = BigDecimal.ZERO;
        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;
        BigDecimal totalSaldoFinal = BigDecimal.ZERO;

        for (BalanceValoresDto dto : lista.values()) {

            if (dto.getCodigoCuenta().contains("999999999")) {
                continue;
            }
            totalSaldoInicial = totalSaldoInicial.add(
                    dto.getSaldoInicial() != null ? dto.getSaldoInicial() : BigDecimal.ZERO
            );

            totalDebe = totalDebe.add(
                    dto.getDebe() != null ? dto.getDebe() : BigDecimal.ZERO
            );

            totalHaber = totalHaber.add(
                    dto.getHaber() != null ? dto.getHaber() : BigDecimal.ZERO
            );

            totalSaldoFinal = totalSaldoFinal.add(
                    dto.getSaldoFinal() != null ? dto.getSaldoFinal() : BigDecimal.ZERO
            );
        }

        BalanceValoresDto total = new BalanceValoresDto();
        total.setCodigoCuenta("zzzzzzzzz");
        total.setCuenta("Total general:");
        total.setCuentaIndentada("Total general:");
        total.setSaldoInicial(totalSaldoInicial);
        total.setDebe(totalDebe);
        total.setHaber(totalHaber);
        total.setSaldoFinal(totalSaldoFinal);
        total.setTieneMovimiento(Boolean.TRUE);
        total.setEsMayor(Boolean.FALSE);

        lista.put("", total);

    }


    /**
     * Genera el PDF del Balance de Comprobación usando JasperReports a partir de la lista de valores y los parámetros
     * de empresa/filtros.
     * <p>
     * - Compila la plantilla JRXML ubicada en classpath.
     * - Llena el reporte con los parámetros y la colección de datos.
     * - Exporta y devuelve el PDF en bytes.
     *
     * @param list    lista de valores para poblar el reporte (ya validados)
     * @param empresa entidad de la empresa que aporta datos del encabezado
     * @param filters filtros usados que se muestran en el reporte (fechas, cuentas, sucursal)
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando ocurre cualquier error durante la compilación/llenado/exportación del PDF
     */
    private byte[] generatePDFBalanceComprobacion(List<BalanceValoresDto> list,
                                                  AdEmpresaEntity empresa,
                                                  CnPlanCuentaListFilterDto filters) {
        try {


            InputStream reporteStream = new ClassPathResource("report-balance-comprobacion.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = null;

            if (!list.isEmpty()) {
                dataSource = new JRBeanCollectionDataSource(validarDatosLista(list));
            } else {
                dataSource = new JRBeanCollectionDataSource(balanceValoresBuilder.builderDefaultListBalanceComprobacion());
            }


            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", empresa.getRazonSocial());
            parametros.put("sucursal", filters.getSucursal());
            parametros.put("fechaDesde", DateUtils.toString(filters.getFechaEmisionDesde()));
            parametros.put("fechaHasta", DateUtils.toString(filters.getFechaEmisionHasta()));
            parametros.put("fechaActual", DateUtils.toString(LocalDate.now()));
            parametros.put("cuentaDesde", filters.getCuentaInicial());
            parametros.put("cuentaHasta", filters.getCuentaFinal());
            parametros.put("gerente", "Representante");
            parametros.put("idenGerente", "1212121212001");
            parametros.put("contador", "Contador");
            parametros.put("idenContador", "1212121212001");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);


        } catch (Exception exception) {
            throw new GeneralException("Error al generar el PDF del Balance de Comprobacion");
        }
    }

    /**
     * Convierte la lista de {@link BalanceValoresDto} a DTOs preparados para el PDF ({@link BalanceComprobacionPdfDto})
     * mediante el builder y normaliza valores vacíos para el registro de "Total general".
     *
     * @param list lista de {@link BalanceValoresDto} a transformar
     * @return lista de {@link BalanceComprobacionPdfDto} con valores formateados para el reporte
     */
    private List<BalanceComprobacionPdfDto> validarDatosLista(List<BalanceValoresDto> list) {

        List<BalanceComprobacionPdfDto> lista = list.stream()
                .map(balanceValoresBuilder::builderPdfValores)
                .toList();

        for (BalanceComprobacionPdfDto dto : lista) {
            if (dto.getCuenta().equals("Total general:")) {
                if (dto.getSaldoInicial().isEmpty()) {
                    dto.setSaldoInicial("0.00");
                }
                if (dto.getDebe().isEmpty()) {
                    dto.setDebe("0.00");
                }
                if (dto.getHaber().isEmpty()) {
                    dto.setHaber("0.00");
                }
                if (dto.getSaldoFinal().isEmpty()) {
                    dto.setSaldoFinal("0.00");
                }
            }
        }

        return lista;

    }

    /**
     * Valida y ajusta la lista de resultados antes de su retorno o transformación a PDF:
     * - Si {@code filter.getMayor()} es {@code false} filtra las cuentas que no son mayores.
     * - Si {@code filter.getMayor()} es {@code true} reemplaza los códigos marcados (ej.: "999999999") por cadena vacía.
     *
     * @param resultado lista original de {@link BalanceValoresDto}
     * @param filter    filtros que contienen el indicador {@code mayor}
     * @return lista ajustada según el filtro de mayoridad
     */
    private List<BalanceValoresDto> validarListaResultados(List<BalanceValoresDto> resultado, CnPlanCuentaListFilterDto filter) {

        if (!filter.getMayor()) {
            List<BalanceValoresDto> listaNoMayores = resultado.stream()
                    .filter(m -> !m.getEsMayor())
                    .toList();

            return listaNoMayores;
        }

        resultado.forEach(item -> {
            if (item.getCodigoCuenta().contains("999999999")) {
                item.setCodigoCuenta("");
            }
        });

        return resultado;
    }


}
