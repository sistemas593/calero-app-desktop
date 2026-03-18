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
public class CnEstadoSituacionFinancieraServiceImpl {


    private final CnReportesRepository cnReportesRepository;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final MayorizacionServiceImpl mayorizacionService;
    private final BalanceValoresBuilder balanceValoresBuilder;
    private final CnEstadoFinancieroBuilder cnEstadoFinancieroBuilder;

    private final AdEmpresasRepository adEmpresasRepository;

    /**
     * Genera y devuelve una página con el Estado de Situación Financiera para la empresa y rango de fechas indicados.
     * <p>
     * Flujo:
     * - Valida las fechas del filtro.
     * - Recupera cuentas mayores y valores desde repositorios.
     * - Ejecuta la lógica de mayorización y obtiene la lista resultante.
     * - Normaliza códigos especiales y retorna la respuesta paginada.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechas, sucursal, opciones de presentación)
     * @param pageable  parámetros de paginación y ordenamiento
     * @return PaginatedDto\<BalanceValoresDto> con la página de resultados
     * @throws GeneralException cuando falla la validación de fechas u ocurre un error al obtener/procesar datos
     */
    public PaginatedDto<BalanceValoresDto> getReporteEstadoFinancieroPaginado(Long idData, Long idEmpresa,
                                                                              CnPlanCuentaListFilterDto filters, Pageable pageable) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<BalanceValoresProjection> listaValores = cnReportesRepository.reporteEstadoFinancieroPageable(idData, idEmpresa, filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSucursal());

        List<CnPlanCuentaEntity> listCuentas = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listMayores = mayorizacionService.obtenerMayorTotalizadores(cuentasOriginalesMayores);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listCuentas, listMayores);

        List<BalanceValoresDto> resultado = getListaValores(listaValores, merged, idData, idEmpresa, filters);

        resultado.forEach(item -> {
            if (item.getCodigoCuenta().contains("999999999")) {
                item.setCodigoCuenta("");
            }

            if (item.getCodigoCuenta().contains("777777777")) {
                item.setCodigoCuenta("");
            }

            if (item.getCodigoCuenta().contains("zzzzzzzzz")) {
                item.setCodigoCuenta("");
            }

        });

        return mayorizacionService.getPageResponse(resultado, pageable);

    }

    /**
     * Genera y devuelve el Estado de Situación Financiera en formato PDF para la empresa y rango de fechas indicados.
     * <p>
     * Flujo:
     * - Valida las fechas del filtro.
     * - Recupera datos y aplica mayorización igual que el método paginado.
     * - Normaliza códigos especiales y delega en la generación del PDF.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (fechas, sucursal, opciones de presentación)
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando falla la validación o la generación del PDF
     */
    public byte[] getReporteEstadoFinancieroPdf(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<BalanceValoresProjection> listaValores = cnReportesRepository.reporteEstadoFinancieroPageable(idData, idEmpresa, filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSucursal());

        List<CnPlanCuentaEntity> listCuentas = cnPlanCuentasRepository.findAll(idData, idEmpresa);

        List<CnPlanCuentaEntity> listMayores = mayorizacionService.obtenerMayorTotalizadores(cuentasOriginalesMayores);

        List<CnPlanCuentaEntity> merged = mayorizacionService.getCnPlanCuentaMerge(listCuentas, listMayores);

        List<BalanceValoresDto> resultado = getListaValores(listaValores, merged, idData, idEmpresa, filters);

        resultado.forEach(item -> {
            if (item.getCodigoCuenta().contains("999999999")) {
                item.setCodigoCuenta("");
            }

            if (item.getCodigoCuenta().contains("777777777")) {
                item.setCodigoCuenta("");
            }

            if (item.getCodigoCuenta().contains("zzzzzzzzz")) {
                item.setCodigoCuenta("");
            }

        });

        return getReportPdf(resultado, filters, idData, idEmpresa);

    }

    /**
     * Compila y llena la plantilla JasperReports para producir el PDF del Estado Financiero.
     * <p>
     * Pasos:
     * - Recupera la entidad de la empresa para el encabezado.
     * - Compila la plantilla JRXML desde classpath.
     * - Construye la fuente de datos usando la lista provista o una lista por defecto.
     * - Asigna parámetros (empresa, sucursal, fechas, firmas) y exporta a PDF.
     *
     * @param list      lista de {@link BalanceValoresDto} que alimenta el reporte
     * @param filters   filtros usados en la generación (se muestran en el reporte)
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

            InputStream reporteStream = new ClassPathResource("report-estado-financiero.jrxml").getInputStream();
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
            throw new GeneralException("Error al generar el PDF del Estado Financiero");
        }
    }

    /**
     * Inserta en el mapa los registros especiales de cuentas mayores relacionados con la utilidad del ejercicio.
     * <p>
     * - Construye los DTOs de resultado del ejercicio y del total pasivo+patrimonio mediante el builder.
     * - Los coloca en el mapa con las claves definidas en ConstanteReportes para ser procesados posteriormente.
     *
     * @param model    mapa de cuentas (clave -> BalanceValoresDto) donde se insertan los registros especiales
     * @param utilidad valor de utilidad del ejercicio que puede ser utilizado por la mayorización
     */
    private void getCuentasMayoresUtilidad(Map<String, BalanceValoresDto> model, BigDecimal utilidad) {


        BalanceValoresDto resultadoEjerccio = balanceValoresBuilder
                .builderEstadoFinancieroUtilidad(ConstanteReportes.utilidadEjerccioCuenta,
                        ConstanteReportes.utilidadEjerccioCodigoCuenta, ConstanteReportes.utilidadEjerccioCodigoOriginal, BigDecimal.ZERO);

        BalanceValoresDto totalPasivoYPatrimonio = balanceValoresBuilder
                .builderEstadoFinancieroUtilidad(ConstanteReportes.utilidadTotalCuenta,
                        ConstanteReportes.utilidadTotalCodigoCuenta, ConstanteReportes.utilidadTotalCodigoOriginal, BigDecimal.ZERO);


        model.put(ConstanteReportes.utilidadEjerccioCodigoOriginal, resultadoEjerccio);
        model.put(ConstanteReportes.utilidadTotalCodigoOriginal, totalPasivoYPatrimonio);


    }

    /**
     * Ajusta los signos de los saldos finales cuando la opción \"positivo\" está activada.
     * <p>
     * - Si {@code positivo} es true invierte el signo (multiplica por -1) de {@code saldoFinal}
     * para las cuentas de grupo 2 y 3 (pasivo y patrimonio).
     * - Si {@code positivo} es false no realiza cambios.
     *
     * @param valores  mapa de cuentas (clave -> BalanceValoresDto) cuyos valores pueden ajustarse
     * @param positivo indicador que determina si se deben presentar valores como positivos
     */
    private void validacionValoresPositivos(Map<String, BalanceValoresDto> valores, Boolean positivo) {
        if (positivo) {
            for (BalanceValoresDto valor : valores.values()) {
                if (valor.getGrupo().equals(2) || valor.getGrupo().equals(3))
                    valor.setSaldoFinal(valor.getSaldoFinal().multiply(new BigDecimal(-1)));
            }
        }
    }

    /**
     * Convierte y valida la lista de {@link BalanceValoresDto} a DTOs para el PDF ({@link EstadoFinancieroPdfDto}).
     * <p>
     * - Usa el builder para transformar cada elemento.
     * - Retorna la lista resultante lista para usar como JRBeanCollectionDataSource.
     *
     * @param list lista de {@link BalanceValoresDto} a transformar
     * @return lista de {@link EstadoFinancieroPdfDto} preparada para el reporte
     */
    private List<EstadoFinancieroPdfDto> validarDatosLista(List<BalanceValoresDto> list) {
        return list.stream()
                .map(cnEstadoFinancieroBuilder::builderValoresPdf)
                .toList();
    }

    /**
     * Construye la lista consolidada de {@link BalanceValoresDto} que representa el Estado Financiero.
     * <p>
     * Pasos principales:
     * - Obtiene el resultado del ejercicio.
     * - Construye el mapa inicial de cuentas mediante mayorización.
     * - Inserta registros especiales (utilidad / total pasivo+patrimonio).
     * - Convierte las proyecciones a DTOs y aplica la mayorización sobre saldos finales.
     * - Calcula totales y aplica la validación de valores positivos según filtros.
     *
     * @param listaSaldo      lista de proyecciones con saldos finales por cuenta
     * @param todasLasCuentas lista completa del plan de cuentas de la empresa (incluye mayores)
     * @param idData          identificador lógico del entorno/data
     * @param idEmpresa       identificador de la empresa
     * @param filters         filtros de entrada que afectan la construcción (fechas, positivo, sucursal)
     * @return lista mayorizada y ordenada de {@link BalanceValoresDto} lista para presentación
     */
    private List<BalanceValoresDto> getListaValores(List<BalanceValoresProjection> listaSaldo,
                                                    List<CnPlanCuentaEntity> todasLasCuentas,
                                                    Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {


        BigDecimal utilidad = obtenerResultadoDelEjercicio(idData, idEmpresa, filters);
        Map<String, BalanceValoresDto> mapCuentasOriginales = mayorizacionService.getMapBalanceCuentas(todasLasCuentas);
        getCuentasMayoresUtilidad(mapCuentasOriginales, utilidad);


        List<BalanceValoresDto> listSaldoFinalDto = listaSaldo.stream()
                .map(balanceValoresBuilder::builderEstadoFinancieroCuentaSaldoFinal)
                .toList();


        for (BalanceValoresDto saldoBalance : listSaldoFinalDto) {

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(saldoBalance.getCodigoCuentaOriginal()),
                    ConstanteReportes.saldoFinal, saldoBalance.getSaldoFinal());
        }

        mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService.obtenerListaCuentasAMayorizar(ConstanteReportes.utilidadEjerccioCodigoOriginal),
                ConstanteReportes.saldoFinal, utilidad);

        setTotalPasivoyPatrimonio(mapCuentasOriginales);
        validacionValoresPositivos(mapCuentasOriginales, filters.getPositivo());
        return mayorizacionService.getBalanceValoresOrdenado(mapCuentasOriginales);

    }

    /**
     * Calcula y ajusta el total de Pasivo y Patrimonio dentro del mapa de cuentas.
     * <p>
     * - Obtiene los totales de activo, pasivo y patrimonio del mapa.
     * - Marca los totales de mayor correspondientes y suma pasivo + patrimonio en el registro
     * identificado por la clave utilizada para el total pasivo+patrimonio.
     *
     * @param mapCuentasOriginales mapa de cuentas (clave -> BalanceValoresDto) sobre el que se realiza la operación
     */
    private void setTotalPasivoyPatrimonio(Map<String, BalanceValoresDto> mapCuentasOriginales) {

        BalanceValoresDto totalActivo = mapCuentasOriginales.get("1.999999999");
        BalanceValoresDto totalPasivo = mapCuentasOriginales.get("2.999999999");
        BalanceValoresDto totalPatrimonio = mapCuentasOriginales.get("3.999999999");

        totalPatrimonio.setTotalMayor(Boolean.TRUE);
        totalActivo.setTotalMayor(Boolean.TRUE);
        totalPasivo.setTotalMayor(Boolean.TRUE);

        BalanceValoresDto totalPasivoYPatrimonio = mapCuentasOriginales.get("3.zzzzzzzzz");
        totalPasivoYPatrimonio.setTotalMayor(Boolean.FALSE);
        totalPasivoYPatrimonio.setSaldoFinal(totalPasivo.getSaldoFinal().add(totalPatrimonio.getSaldoFinal()));

    }

    /**
     * Obtiene el resultado/resultado del ejercicio (utilidad o pérdida) consultando el repositorio.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtro que contiene las fechas y la sucursal para calcular el resultado
     * @return {@link BigDecimal} con el resultado del ejercicio (puede ser positivo, negativo o cero)
     */
    private BigDecimal obtenerResultadoDelEjercicio(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {
        return cnReportesRepository.obtenerResultadoUtilidad(idData, idEmpresa,
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSucursal());
    }


}
