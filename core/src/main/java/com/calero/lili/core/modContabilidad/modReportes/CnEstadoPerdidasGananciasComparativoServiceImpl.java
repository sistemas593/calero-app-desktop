package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import com.calero.lili.core.modContabilidad.modReportes.builder.BalanceValoresBuilder;
import com.calero.lili.core.modContabilidad.modReportes.builder.CnEstadoFinancieroBuilder;
import com.calero.lili.core.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.core.modContabilidad.modReportes.dto.EstadoFinancieroPdfDto;
import com.calero.lili.core.modContabilidad.modReportes.projection.BalanceValoresProjection;
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
public class CnEstadoPerdidasGananciasComparativoServiceImpl {

    private final CnReportesRepository cnReportesRepository;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final MayorizacionServiceImpl mayorizacionService;
    private final BalanceValoresBuilder balanceValoresBuilder;
    private final CnEstadoFinancieroBuilder cnEstadoFinancieroBuilder;
    private final AdEmpresasRepository adEmpresasRepository;


    public byte[] getReporteEstadoPerdidasGananciasComparativoPdf(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters) {

        DateUtils.validacionFechas(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

        List<CnPlanCuentaEntity> cuentasOriginalesMayores = cnPlanCuentasRepository
                .findAllMayores(idData, idEmpresa, Boolean.TRUE);

        List<BalanceValoresProjection> listaValores = cnReportesRepository.reporteEstadoPerdidasGananciasComparativo(
                idData, idEmpresa, filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                filters.getSucursal(), filters.getCodigoCentroCostos());

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

    private List<BalanceValoresDto> getListaValores(List<BalanceValoresProjection> listaValores,
                                                    List<CnPlanCuentaEntity> todasLasCuentas,
                                                    CnPlanCuentaListFilterDto filters) {


        Map<String, BalanceValoresDto> mapCuentasOriginales = mayorizacionService.getMapBalanceCuentas(todasLasCuentas);

        List<BalanceValoresDto> listValoresDto = listaValores.stream()
                .map(balanceValoresBuilder::builderEstadoComparativo)
                .toList();


        for (BalanceValoresDto debeBalance : listValoresDto) {

            mayorizacionService.mayorizacion(mapCuentasOriginales, mayorizacionService
                            .obtenerListaCuentasAMayorizar(debeBalance.getCodigoCuentaOriginal()),
                    validarMes(debeBalance.getFechaMes()), debeBalance.getSaldoFinal());
        }

        setTotalCostoyGastos(mapCuentasOriginales);
        validacionValoresPositivos(mapCuentasOriginales, filters.getPositivo());

        return mayorizacionService.getBalanceValoresOrdenado(mapCuentasOriginales);
    }


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
        totalCostoyGasto.setSaldoInicial(BigDecimal.ZERO);
        totalCostoyGasto.setEnero(totalCostos.getEnero().add(totalGastos.getEnero()));
        totalCostoyGasto.setFebrero(totalCostos.getFebrero().add(totalGastos.getFebrero()));
        totalCostoyGasto.setMarzo(totalCostos.getMarzo().add(totalGastos.getMarzo()));
        totalCostoyGasto.setAbril(totalCostos.getAbril().add(totalGastos.getAbril()));
        totalCostoyGasto.setMayo(totalCostos.getMayo().add(totalGastos.getMayo()));
        totalCostoyGasto.setJunio(totalCostos.getJunio().add(totalGastos.getJunio()));
        totalCostoyGasto.setJulio(totalCostos.getJulio().add(totalGastos.getJulio()));
        totalCostoyGasto.setAgosto(totalCostos.getAgosto().add(totalGastos.getAgosto()));
        totalCostoyGasto.setSeptiembre(totalCostos.getSeptiembre().add(totalGastos.getSeptiembre()));
        totalCostoyGasto.setOctubre(totalCostos.getOctubre().add(totalGastos.getOctubre()));
        totalCostoyGasto.setNoviembre(totalCostos.getNoviembre().add(totalGastos.getNoviembre()));
        totalCostoyGasto.setDiciembre(totalCostos.getDiciembre().add(totalGastos.getDiciembre()));
        totalCostoyGasto.setSaldoFinal(totalCostosyGastos);

        BalanceValoresDto resultado = new BalanceValoresDto();

        resultado.setCodigoCuenta("6zzzzzzzzz");
        resultado.setCodigoCuentaOriginal("6.zzzzzzzzz");
        resultado.setCuenta("Resultado del Ejercicio");
        resultado.setGrupo(6);
        resultado.setTieneMovimiento(Boolean.TRUE);
        resultado.setTotalMayor(Boolean.FALSE);
        resultado.setSaldoInicial(BigDecimal.ZERO);
        resultado.setSaldoFinal(totalIngresos.getSaldoFinal().add(totalCostosyGastos));
        resultado.setEnero(totalIngresos.getEnero().add(totalCostoyGasto.getEnero()));
        resultado.setFebrero(totalIngresos.getFebrero().add(totalCostoyGasto.getFebrero()));
        resultado.setMarzo(totalIngresos.getMarzo().add(totalCostoyGasto.getMarzo()));
        resultado.setAbril(totalIngresos.getAbril().add(totalCostoyGasto.getAbril()));
        resultado.setMayo(totalIngresos.getMayo().add(totalCostoyGasto.getMayo()));
        resultado.setJunio(totalIngresos.getJunio().add(totalCostoyGasto.getJunio()));
        resultado.setJulio(totalIngresos.getJulio().add(totalCostoyGasto.getJulio()));
        resultado.setAgosto(totalIngresos.getAgosto().add(totalCostoyGasto.getAgosto()));
        resultado.setSeptiembre(totalIngresos.getSeptiembre().add(totalCostoyGasto.getSeptiembre()));
        resultado.setOctubre(totalIngresos.getOctubre().add(totalCostoyGasto.getOctubre()));
        resultado.setNoviembre(totalIngresos.getNoviembre().add(totalCostoyGasto.getNoviembre()));
        resultado.setDiciembre(totalIngresos.getDiciembre().add(totalCostoyGasto.getDiciembre()));

        mapCuentasOriginales.put(totalCostoyGasto.getCodigoCuentaOriginal(), totalCostoyGasto);
        mapCuentasOriginales.put(resultado.getCodigoCuentaOriginal(), resultado);

    }

    private void validacionValoresPositivos(Map<String, BalanceValoresDto> valores, Boolean positivo) {
        if (positivo) {
            for (BalanceValoresDto valor : valores.values()) {
                if (valor.getGrupo().equals(4))
                    valor.setSaldoFinal(valor.getSaldoFinal().multiply(new BigDecimal(-1)));
            }
        }
    }


    private byte[] getReportPdf(List<BalanceValoresDto> list, CnPlanCuentaListFilterDto filters,
                                Long idData, Long idEmpresa) {
        try {

            AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));

            InputStream reporteStream = new ClassPathResource("report-estado-perdidas-ganancias-comparativo.jrxml").getInputStream();
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

    private List<EstadoFinancieroPdfDto> validarDatosLista(List<BalanceValoresDto> list) {
        List<EstadoFinancieroPdfDto> lista = list.stream()
                .map(cnEstadoFinancieroBuilder::builderComparativoValoresPdf)
                .toList();

        for (EstadoFinancieroPdfDto dto : lista) {
            if (dto.getCodigoCuenta().contains("999999999")) {
                dto.setCodigoCuenta("");
            }
        }

        return lista;
    }

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

}
