package com.calero.lili.api.modContabilidad.modReportes;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import com.calero.lili.api.modContabilidad.modReportes.builder.BalanceValoresBuilder;
import com.calero.lili.api.modContabilidad.modReportes.builder.CnEstadoFinancieroBuilder;
import com.calero.lili.api.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.api.modContabilidad.modReportes.dto.EstadoFinancieroPdfDto;
import com.calero.lili.api.modContabilidad.modReportes.projection.BalanceValoresProjection;
import com.calero.lili.api.modContabilidad.modReportes.projection.ResultadosProjection;
import com.calero.lili.api.modContabilidad.modReportes.projection.SaldoInicialBCProjection;
import com.calero.lili.api.utils.ConstanteReportes;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CnEstadoSituacionComparativoServiceImpl {


    private final CnReportesRepository cnReportesRepository;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final MayorizacionServiceImpl mayorizacionService;
    private final BalanceValoresBuilder balanceValoresBuilder;
    private final CnEstadoFinancieroBuilder cnEstadoFinancieroBuilder;
    private final AdEmpresasRepository adEmpresasRepository;

    /**
     * Genera y devuelve el PDF del Estado Financiero comparativo para la empresa y rango de fechas indicados.
     * <p>
     * Flujo principal:
     * - Valida las fechas del filtro.
     * - Consulta cuentas mayores, saldos iniciales y movimientos comparativos.
     * - Construye la lista consolidada de valores aplicando la mayorización y ajustes.
     * - Genera el PDF a partir de la lista resultante.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechas, sucursal, opciones de presentación)
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando falla la validación o la generación del reporte
     */
    public byte[] getReporteEstadoFinancieroPdfComparativo(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<SaldoInicialBCProjection> saldosIniciales = cnReportesRepository.getSaldosInicialesEstadoFinancieroComparativo(
                idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta());

        List<BalanceValoresProjection> listaValores = cnReportesRepository.reporteEstadoFinancieroPageableComparativo(idData,
                idEmpresa, filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSucursal());

        List<CnPlanCuentaEntity> listaCuentas = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listMayores = mayorizacionService.obtenerMayorTotalizadores(cuentasOriginalesMayores);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listaCuentas, listMayores);


        List<BalanceValoresDto> resultado = getListaValores(listaValores, saldosIniciales, merged, filters, idData, idEmpresa);

        return getReportPdfComparativo(resultado, filters, idData, idEmpresa);

    }

    /**
     * Compila y llena la plantilla JasperReports para producir el PDF comparativo.
     * <p>
     * - Recupera la información de la empresa para los datos del encabezado.
     * - Compila la plantilla JRXML desde classpath y crea la fuente de datos.
     * - Carga parámetros (empresa, sucursal, fechas, firmas) y exporta a PDF.
     *
     * @param list      lista de {@link BalanceValoresDto} que alimenta el reporte (puede ser vacía)
     * @param filters   filtros usados en la generación (se muestran en el reporte)
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando ocurre cualquier error durante la compilación, llenado o exportación del PDF
     */
    private byte[] getReportPdfComparativo(List<BalanceValoresDto> list, CnPlanCuentaListFilterDto filters,
                                           Long idData, Long idEmpresa) {
        try {

            AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));

            InputStream reporteStream = new ClassPathResource("report-estado-financiero-comparativo.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = null;

            if (!list.isEmpty()) {
                List<EstadoFinancieroPdfDto> lista = validarDatosListaComparativo(list);
                dataSource = new JRBeanCollectionDataSource(lista);
            } else {
                dataSource = new JRBeanCollectionDataSource(cnEstadoFinancieroBuilder.builderDefaultListBalanceComprobacion());
            }


            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", empresa.getRazonSocial());
            parametros.put("sucursal", filters.getSucursal());
            parametros.put("fechaDesde", DateUtils.toString(filters.getFechaEmisionDesde()));
            parametros.put("fechaHasta", DateUtils.toString(filters.getFechaEmisionHasta()));
            parametros.put("fechaActual", DateUtils.toString(LocalDate.now()));
            parametros.put("gerente", "Representante");
            parametros.put("idenGerente", "1212121212001");
            parametros.put("contador", "Contador");
            parametros.put("idenContador", "1212121212001");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);


        } catch (Exception exception) {
            throw new GeneralException("Error al generar el PDF del Estado Financiero");
        }
    }


    /**
     * Construye y retorna la lista consolidada de {@link BalanceValoresDto} para el reporte comparativo.
     * <p>
     * Pasos principales:
     * - Obtiene resultados de utilidad por mes.
     * - Crea el mapa inicial de cuentas y agrega registros especiales (utilidad, totales).
     * - Convierte las proyecciones a DTOs y aplica la mayorización para saldos iniciales y movimientos.
     * - Ajusta totales de pasivo y patrimonio y aplica la validación de valores positivos según el filtro.
     *
     * @param listaValores     lista de proyecciones con movimientos por cuenta/mes
     * @param listSaldoInicial lista de proyecciones con saldos iniciales
     * @param todasLasCuentas  lista completa del plan de cuentas de la empresa
     * @param filters          filtros de entrada que afectan la construcción (fechas, positivo, sucursal)
     * @param idData           identificador lógico del entorno/data
     * @param idEmpresa        identificador de la empresa
     * @return lista mayorizada y ordenada de {@link BalanceValoresDto} lista para el reporte
     */
    private List<BalanceValoresDto> getListaValores(List<BalanceValoresProjection> listaValores,
                                                    List<SaldoInicialBCProjection> listSaldoInicial,
                                                    List<CnPlanCuentaEntity> todasLasCuentas,
                                                    CnPlanCuentaListFilterDto filters, Long idData, Long idEmpresa) {


        List<ResultadosProjection> resultados = cnReportesRepository.obtenerResultadoUtilidadComparativo(idData, idEmpresa,
                DateUtils.getFechaInicio(filters.getFechaEmisionDesde()),
                DateUtils.getFechaFin(filters.getFechaEmisionDesde()), filters.getSucursal());

        Map<String, BalanceValoresDto> mapCuentasOriginales = mayorizacionService.getMapBalanceCuentas(todasLasCuentas);

        obtenerResultadoDelEjercicioComparativo(mapCuentasOriginales);

        List<BalanceValoresDto> listValoresDto = listaValores.stream()
                .map(balanceValoresBuilder::builderEstadoComparativo)
                .toList();

        List<BalanceValoresDto> listSaldoInicialDto = listSaldoInicial.stream()
                .map(balanceValoresBuilder::builderBalanceComprobacionCuentaSaldoInicial)
                .toList();


        for (BalanceValoresDto saldoBalance : listSaldoInicialDto) {


            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(saldoBalance.getCodigoCuentaOriginal()),
                    ConstanteReportes.saldoInicial, saldoBalance.getSaldoInicial());
        }

        for (BalanceValoresDto valorMes : listValoresDto) {

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(valorMes.getCodigoCuentaOriginal()),
                    validarMes(valorMes.getFechaMes()), valorMes.getSaldoFinal());
        }

        for (ResultadosProjection resultado : resultados) {

            mayorizacionService.mayorizacion(mapCuentasOriginales,
                    mayorizacionService.obtenerListaCuentasAMayorizar(ConstanteReportes.utilidadEjerccioCodigoOriginal),
                    validarMes(resultado.getMesFecha()), resultado.getResultado());

        }

        setTotalPasivoyPatrimonio(mapCuentasOriginales);
        validacionValoresPositivos(mapCuentasOriginales, filters.getPositivo());
        return mayorizacionService.getBalanceValoresOrdenado(mapCuentasOriginales);
    }

    /**
     * Marca y calcula los totales de Pasivo y Patrimonio, y deposita la suma en el registro de total general.
     * <p>
     * - Marca los registros de activo/pasivo/patrimonio como totales de mayor.
     * - Suma mes a mes y saldos de pasivo y patrimonio y asigna el resultado al registro
     * identificado por la clave "3.zzzzzzzzz" (total pasivo + patrimonio).
     *
     * @param mapCuentasOriginales mapa de cuentas (clave -> {@link BalanceValoresDto}) sobre el que se realiza la operación
     */
    private void setTotalPasivoyPatrimonio(Map<String, BalanceValoresDto> mapCuentasOriginales) {

        BalanceValoresDto activo = mapCuentasOriginales.get("1.999999999");
        BalanceValoresDto pasivo = mapCuentasOriginales.get("2.999999999");
        BalanceValoresDto patrimonio = mapCuentasOriginales.get("3.999999999");

        activo.setTotalMayor(Boolean.TRUE);
        pasivo.setTotalMayor(Boolean.TRUE);
        patrimonio.setTotalMayor(Boolean.TRUE);

        BalanceValoresDto total = mapCuentasOriginales.get("3.zzzzzzzzz");

        total.setEnero(pasivo.getEnero().add(patrimonio.getEnero()));
        total.setFebrero(pasivo.getFebrero().add(patrimonio.getFebrero()));
        total.setMarzo(pasivo.getMarzo().add(patrimonio.getMarzo()));
        total.setAbril(pasivo.getAbril().add(patrimonio.getAbril()));
        total.setMayo(pasivo.getMayo().add(patrimonio.getMayo()));
        total.setJunio(pasivo.getJunio().add(patrimonio.getJunio()));
        total.setJulio(pasivo.getJulio().add(patrimonio.getJulio()));
        total.setAgosto(pasivo.getAgosto().add(patrimonio.getAgosto()));
        total.setSeptiembre(pasivo.getSeptiembre().add(patrimonio.getSeptiembre()));
        total.setOctubre(pasivo.getOctubre().add(patrimonio.getOctubre()));
        total.setNoviembre(pasivo.getNoviembre().add(patrimonio.getNoviembre()));
        total.setDiciembre(pasivo.getDiciembre().add(patrimonio.getDiciembre()));
        total.setSaldoInicial(pasivo.getSaldoInicial().add(patrimonio.getSaldoInicial()));
        total.setSaldoFinal(pasivo.getSaldoFinal().add(patrimonio.getSaldoFinal()));

    }

    /**
     * Convierte una fecha (LocalDate) al identificador de mes usado en los DTOs de mayorización.
     *
     * @param fechaMes fecha cuyo mes será convertido a la constante correspondiente
     * @return cadena con la constante del mes (ej.: ConstanteReportes.enero, ConstanteReportes.febrero, ...)
     */
    private String validarMes(LocalDate fechaMes) {
        return switch (fechaMes.getMonth()) {
            case JANUARY -> ConstanteReportes.enero;
            case FEBRUARY -> ConstanteReportes.febrero;
            case MARCH -> ConstanteReportes.marzo;
            case APRIL -> ConstanteReportes.abril;
            case MAY -> ConstanteReportes.mayo;
            case JUNE -> ConstanteReportes.junio;
            case JULY -> ConstanteReportes.julio;
            case AUGUST -> ConstanteReportes.agosto;
            case SEPTEMBER -> ConstanteReportes.septiembre;
            case OCTOBER -> ConstanteReportes.octubre;
            case NOVEMBER -> ConstanteReportes.noviembre;
            case DECEMBER -> ConstanteReportes.diciembre;
        };
    }

    /**
     * Transforma y limpia la lista de {@link BalanceValoresDto} a DTOs para el PDF ({@link EstadoFinancieroPdfDto}).
     * <p>
     * - Usa el builder para crear los DTOs de reporte.
     * - Normaliza códigos especiales (ej.: \"999999999\", \"777777777\", \"zzzzzzzzz\") dejando la
     * columna de código vacía cuando corresponda.
     *
     * @param list lista de {@link BalanceValoresDto} a transformar
     * @return lista de {@link EstadoFinancieroPdfDto} lista para ser usada como data source del reporte
     */
    private List<EstadoFinancieroPdfDto> validarDatosListaComparativo(List<BalanceValoresDto> list) {

        List<EstadoFinancieroPdfDto> lista = list.stream()
                .map(cnEstadoFinancieroBuilder::builderComparativoValoresPdf)
                .toList();

        for (EstadoFinancieroPdfDto dto : lista) {
            if (dto.getCodigoCuenta().contains("999999999")) {
                dto.setCodigoCuenta("");
            }
            if (dto.getCodigoCuenta().contains("777777777")) {
                dto.setCodigoCuenta("");
            }
            if (dto.getCodigoCuenta().contains("zzzzzzzzz")) {
                dto.setCodigoCuenta("");
            }

        }
        return lista;
    }


    /**
     * Inserta en el mapa los registros especiales de resultado del ejercicio y del total pasivo+patrimonio.
     * <p>
     * - Construye los DTOs de utilidad/total mediante el builder y los coloca en el modelo con
     * las claves definidas en ConstanteReportes.
     *
     * @param model mapa de cuentas sobre el cual se agregan los registros especiales
     */
    private void obtenerResultadoDelEjercicioComparativo(Map<String, BalanceValoresDto> model) {


        BalanceValoresDto resultadoEjerccio = balanceValoresBuilder
                .builderEstadoFinancieroComparativoUtilidad(ConstanteReportes.utilidadEjerccioCuenta,
                        ConstanteReportes.utilidadEjerccioCodigoCuenta, ConstanteReportes.utilidadEjerccioCodigoOriginal);

        BalanceValoresDto totalPasivoYPatrimonio = balanceValoresBuilder
                .builderEstadoFinancieroComparativoUtilidad(ConstanteReportes.utilidadTotalCuenta,
                        ConstanteReportes.utilidadTotalCodigoCuenta, ConstanteReportes.utilidadTotalCodigoOriginal);


        model.put(ConstanteReportes.utilidadEjerccioCodigoOriginal, resultadoEjerccio);
        model.put(ConstanteReportes.utilidadTotalCodigoOriginal, totalPasivoYPatrimonio);


    }

    /**
     * Ajusta los signos de los valores según la opción de presentación \"positivo\".
     * <p>
     * - Si {@code positivo} es true invierte el signo (multiplica por -1) de los valores mensuales,
     * saldo inicial y saldo final para las cuentas de grupo 2 y 3 (pasivo, patrimonio).
     * - Si {@code positivo} es false no realiza cambios.
     *
     * @param valores  mapa de cuentas (clave -> {@link BalanceValoresDto}) cuyos valores pueden ajustarse
     * @param positivo indicador que determina si se deben presentar valores como positivos
     */
    private void validacionValoresPositivos(Map<String, BalanceValoresDto> valores, Boolean positivo) {
        if (positivo) {

            for (BalanceValoresDto valor : valores.values()) {
                if (valor.getGrupo().equals(2) || valor.getGrupo().equals(3)) {
                    valor.setEnero(valor.getEnero().multiply(new BigDecimal(-1)));
                    valor.setFebrero(valor.getFebrero().multiply(new BigDecimal(-1)));
                    valor.setMarzo(valor.getMarzo().multiply(new BigDecimal(-1)));
                    valor.setAbril(valor.getAbril().multiply(new BigDecimal(-1)));
                    valor.setMayo(valor.getMayo().multiply(new BigDecimal(-1)));
                    valor.setJunio(valor.getJunio().multiply(new BigDecimal(-1)));
                    valor.setJulio(valor.getJulio().multiply(new BigDecimal(-1)));
                    valor.setAgosto(valor.getAgosto().multiply(new BigDecimal(-1)));
                    valor.setSeptiembre(valor.getSeptiembre().multiply(new BigDecimal(-1)));
                    valor.setOctubre(valor.getOctubre().multiply(new BigDecimal(-1)));
                    valor.setNoviembre(valor.getNoviembre().multiply(new BigDecimal(-1)));
                    valor.setDiciembre(valor.getDiciembre().multiply(new BigDecimal(-1)));
                    valor.setSaldoInicial(valor.getSaldoInicial().multiply(new BigDecimal(-1)));
                    valor.setSaldoFinal(valor.getSaldoFinal().multiply(new BigDecimal(-1)));
                }
            }
        }
    }

}
