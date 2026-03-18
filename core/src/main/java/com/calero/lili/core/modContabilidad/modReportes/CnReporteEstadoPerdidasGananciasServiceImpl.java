package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import com.calero.lili.core.modContabilidad.modReportes.builder.BalanceValoresBuilder;
import com.calero.lili.core.modContabilidad.modReportes.builder.CnEstadoFinancieroBuilder;
import com.calero.lili.core.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.core.modContabilidad.modReportes.dto.EstadoFinancieroPdfDto;
import com.calero.lili.core.modContabilidad.modReportes.projection.BalanceValoresProjection;
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

@Service
@AllArgsConstructor
public class CnReporteEstadoPerdidasGananciasServiceImpl {

    // CENTRO DE COSTOS SOLO EN REPORTE RESULTADOS.

    private final CnReportesRepository cnReportesRepository;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final MayorizacionServiceImpl mayorizacionService;
    private final BalanceValoresBuilder balanceValoresBuilder;
    private final CnEstadoFinancieroBuilder cnEstadoFinancieroBuilder;
    private final AdEmpresasRepository adEmpresasRepository;


    /**
     * Genera y devuelve una página paginada con el Estado de Pérdidas y Ganancias para la empresa y rango de fechas indicados.
     * <p>
     * Flujo principal:
     * - Valida las fechas del filtro.
     * - Recupera cuentas mayores y movimientos desde los repositorios.
     * - Integra cuentas y aplica la lógica de mayorización.
     * - Construye la lista resultante, normaliza códigos especiales y devuelve la página según {@code pageable}.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechaEmisionDesde, fechaEmisionHasta, sucursal, opciones)
     * @param pageable  parámetros de paginación y ordenamiento
     * @return PaginatedDto\<BalanceValoresDto> con la página de resultados del Estado de Pérdidas y Ganancias
     * @throws GeneralException cuando la validación de fechas falla o ocurre un error al obtener/procesar los datos
     */
    public PaginatedDto<BalanceValoresDto> getReporteEstadoPerdidasGanancias(Long idData, Long idEmpresa,
                                                                             CnPlanCuentaListFilterDto filters, Pageable pageable) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<BalanceValoresProjection> listaValores = cnReportesRepository.reporteEstadoPerdidasGanancias(idData, idEmpresa, filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSucursal(), filters.getCodigoCentroCostos());

        List<CnPlanCuentaEntity> listaCuentas = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listaMayores = mayorizacionService.obtenerMayorTotalizadores(cuentasOriginalesMayores);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listaCuentas, listaMayores);


        List<BalanceValoresDto> resultado = getListaValores(listaValores, merged, filters);

        resultado.forEach(item -> {
            if (item.getCodigoCuenta().contains("999999999")) {
                item.setCodigoCuenta("");
            }
        });

        resultado.forEach(BalanceValoresDto::limpiarMeses);

        return mayorizacionService.getPageResponse(resultado, pageable);

    }


    /**
     * Construye y devuelve la lista consolidada de {@link BalanceValoresDto} a partir de las proyecciones consultadas.
     * <p>
     * Pasos principales:
     * - Crea el mapa inicial de cuentas mediante mayorización.
     * - Convierte las proyecciones a DTOs y aplica mayorización sobre el saldo final.
     * - Inserta totales específicos (costos, gastos, resultado) y aplica la presentación de valores positivos si aplica.
     *
     * @param listaValores    lista de proyecciones {@link BalanceValoresProjection} con saldos finales por cuenta
     * @param todasLasCuentas lista completa del plan de cuentas de la empresa (mayores incluidos)
     * @param filters         filtros que afectan la construcción (fechas, positivo, sucursal)
     * @return lista mayorizada y ordenada de {@link BalanceValoresDto} lista para presentación o uso en el PDF
     */
    private List<BalanceValoresDto> getListaValores(List<BalanceValoresProjection> listaValores,
                                                    List<CnPlanCuentaEntity> todasLasCuentas,
                                                    CnPlanCuentaListFilterDto filters) {


        Map<String, BalanceValoresDto> mapCuentasOriginales = mayorizacionService.getMapBalanceCuentas(todasLasCuentas);

        List<BalanceValoresDto> listValoresDto = listaValores.stream()
                .map(balanceValoresBuilder::builderEstadoFinancieroCuentaSaldoFinal)
                .toList();


        for (BalanceValoresDto debeBalance : listValoresDto) {

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(debeBalance.getCodigoCuentaOriginal()),
                    ConstanteReportes.saldoFinal, debeBalance.getSaldoFinal());
        }

        setTotalCostoyGastos(mapCuentasOriginales);
        validacionValoresPositivos(mapCuentasOriginales, filters.getPositivo());

        return mayorizacionService.getBalanceValoresOrdenado(mapCuentasOriginales);
    }

    /**
     * Calcula e inserta en el mapa los totales de ingresos, costos y gastos, además del registro
     * "Total de COSTOS y GASTOS" y el "Resultado del Ejercicio".
     * <p>
     * - Marca los totales de mayor en los registros involucrados.
     * - Calcula la suma de costos y gastos y construye los DTOs especiales que se añaden al mapa.
     *
     * @param mapCuentasOriginales mapa de cuentas (clave -> {@link BalanceValoresDto}) sobre el que se agregarán los registros especiales
     */
    private void setTotalCostoyGastos(Map<String, BalanceValoresDto> mapCuentasOriginales) {

        BalanceValoresDto totalIngresos = mapCuentasOriginales.get("4.999999999");
        BalanceValoresDto totalCostos = mapCuentasOriginales.get("5.999999999");
        BalanceValoresDto totalGastos = mapCuentasOriginales.get("6.999999999");


        totalIngresos.setTotalMayor(Boolean.TRUE);
        totalGastos.setTotalMayor(Boolean.TRUE);
        totalCostos.setTotalMayor(Boolean.TRUE);

        BigDecimal totalCostosyGastos = totalCostos.getSaldoFinal().add(totalGastos.getSaldoFinal());


        BalanceValoresDto totalCostoyGasto = new BalanceValoresDto();
        totalCostoyGasto.setCodigoCuenta("6xxxxxxxxx");
        totalCostoyGasto.setCodigoCuentaOriginal("6.xxxxxxxxx");
        totalCostoyGasto.setCuenta("Total de COSTOS y GASTOS");
        totalCostoyGasto.setGrupo(6);
        totalCostoyGasto.setTieneMovimiento(Boolean.TRUE);
        totalCostoyGasto.setTotalMayor(Boolean.TRUE);
        totalCostoyGasto.setSaldoFinal(totalCostosyGastos);

        BalanceValoresDto resultado = new BalanceValoresDto();
        resultado.setCodigoCuenta("6zzzzzzzzz");
        resultado.setCodigoCuentaOriginal("6.zzzzzzzzz");
        resultado.setCuenta("Resultado del Ejercicio");
        resultado.setGrupo(6);
        resultado.setTieneMovimiento(Boolean.TRUE);
        resultado.setTotalMayor(Boolean.FALSE);
        resultado.setSaldoFinal(totalIngresos.getSaldoFinal().add(totalCostosyGastos));


        mapCuentasOriginales.put(totalCostoyGasto.getCodigoCuentaOriginal(), totalCostoyGasto);
        mapCuentasOriginales.put(resultado.getCodigoCuentaOriginal(), resultado);

    }


    /**
     * Genera y devuelve el PDF del Estado de Pérdidas y Ganancias para la empresa y rango de fechas indicados.
     * <p>
     * Flujo:
     * - Valida las fechas del filtro.
     * - Obtiene y procesa los datos (mayorización y totales).
     * - Normaliza códigos especiales y delega en {@code getReportPdf} para la generación del PDF.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechaEmisionDesde, fechaEmisionHasta, sucursal)
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando falla la validación de fechas o ocurre un error durante la generación del PDF
     */
    public byte[] getReporteEstadoPerdidasGananciasPdf(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<BalanceValoresProjection> listaValores = cnReportesRepository.reporteEstadoPerdidasGanancias(idData, idEmpresa, filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSucursal(), filters.getCodigoCentroCostos());

        List<CnPlanCuentaEntity> listaOriginales = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listaMayores = mayorizacionService.obtenerMayorTotalizadores(cuentasOriginalesMayores);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listaOriginales, listaMayores);

        List<BalanceValoresDto> resultado = getListaValores(listaValores, merged, filters);


        resultado.forEach(item -> {
            if (item.getCodigoCuenta().contains("999999999")) {
                item.setCodigoCuenta("");
            }
            if (item.getCodigoCuenta().contains("xxxxxxxxx")) {
                item.setCodigoCuenta("");
            }
            if (item.getCodigoCuenta().contains("zzzzzzzzz")) {
                item.setCodigoCuenta("");
            }

        });

        return getReportPdf(resultado, filters, idData, idEmpresa);

    }

    /**
     * Compila la plantilla JasperReports, llena el reporte con los parámetros y la colección de datos,
     * y exporta el resultado a PDF en bytes.
     * <p>
     * Pasos:
     * - Recupera la entidad de la empresa para el encabezado.
     * - Compila la plantilla JRXML desde classpath.
     * - Crea la fuente de datos (lista validada o lista por defecto).
     * - Asigna parámetros (empresa, sucursal, fechas, firmas) y exporta a PDF.
     *
     * @param list      lista de {@link BalanceValoresDto} que alimenta el reporte
     * @param filters   filtros usados en el reporte (se muestran en el encabezado)
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando ocurre cualquier error durante la compilación, llenado o exportación del PDF
     */
    private byte[] getReportPdf(List<BalanceValoresDto> list, CnPlanCuentaListFilterDto filters,
                                Long idData, Long idEmpresa) {
        try {

            AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));

            InputStream reporteStream = new ClassPathResource("report-estado-perdidas-ganancias.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = null;

            if (!list.isEmpty()) {
                dataSource = new JRBeanCollectionDataSource(validarDatosLista(list));
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
            throw new GeneralException("Error al generar el PDF del Estado Ganancias y Perdidas: " + exception.getMessage());
        }
    }

    /**
     * Convierte y valida la lista de {@link BalanceValoresDto} a una lista de {@link EstadoFinancieroPdfDto}
     * preparada para usarse como fuente de datos del reporte.
     * <p>
     * - Realiza la transformación mediante el builder.
     * - Normaliza códigos especiales (por ejemplo, reemplaza "999999999" por cadena vacía en la salida).
     *
     * @param list lista de {@link BalanceValoresDto} a transformar
     * @return lista de {@link EstadoFinancieroPdfDto} lista para el JRBeanCollectionDataSource
     */
    private List<EstadoFinancieroPdfDto> validarDatosLista(List<BalanceValoresDto> list) {
        List<EstadoFinancieroPdfDto> lista = list.stream()
                .map(cnEstadoFinancieroBuilder::builderValoresPdf)
                .toList();

        for (EstadoFinancieroPdfDto dto : lista) {
            if (dto.getCodigoCuenta().contains("999999999")) {
                dto.setCodigoCuenta("");
            }
        }

        return lista;
    }

    /**
     * Ajusta los signos de los valores según la opción de presentación "positivo".
     * <p>
     * - Si {@code positivo} es true invierte el signo (multiplica por -1) de los saldos finales
     * para las cuentas del grupo afectado (en este caso, grupo 4: ingresos).
     * - Si {@code positivo} es false no realiza cambios.
     *
     * @param valores  mapa de cuentas (clave -> {@link BalanceValoresDto}) cuyos valores pueden ajustarse
     * @param positivo indicador que determina si se deben presentar valores como positivos
     */
    private void validacionValoresPositivos(Map<String, BalanceValoresDto> valores, Boolean positivo) {
        if (positivo) {
            for (BalanceValoresDto valor : valores.values()) {
                if (valor.getGrupo().equals(4))
                    valor.setSaldoFinal(valor.getSaldoFinal().multiply(new BigDecimal(-1)));
            }
        }
    }


}
