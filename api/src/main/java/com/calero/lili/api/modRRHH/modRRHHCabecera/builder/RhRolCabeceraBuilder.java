package com.calero.lili.api.modRRHH.modRRHHCabecera.builder;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.modRRHH.RhPeriodosEntity;
import com.calero.lili.api.modRRHH.modRRHHCabecera.RhRolCabeceraEntity;
import com.calero.lili.api.modRRHH.modRRHHCabecera.RhRolDetalleEntity;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.RolCabeceraDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.RolCabeceraPDFDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.projection.ReporteRolCabeceraProjection;
import com.calero.lili.api.modRRHH.modRolNovedades.RhRolNovedadesEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RhRolCabeceraBuilder {


    private final FormatoValores formatoValores;

    public RhRolCabeceraEntity builderCabecera(Long idData, Long idEmpresa, RhPeriodosEntity periodos) {
        return RhRolCabeceraEntity.builder()
                .idRol(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .periodos(periodos)
                .diasTrabajados(0)
                .fechaGeneracion(LocalDate.now())
                .build();
    }

    public List<RhRolDetalleEntity> builderListDetalle(List<RhRolNovedadesEntity> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private RhRolDetalleEntity builderDetalle(RhRolNovedadesEntity model) {
        return RhRolDetalleEntity.builder()
                .idDetalle(UUID.randomUUID())
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .valor(model.getValor())
                .rubros(model.getRubros())
                .build();
    }

    public RolCabeceraDto builderResponse(ReporteRolCabeceraProjection model) {
        return RolCabeceraDto.builder()
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tercero(model.getTercero())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .codigoEstablecimiento(model.getCodigoEstablecimiento())
                .codigoResidencia(model.getCodigoResidencia())
                .codigoPais(model.getCodigoPais())
                .aplicaConvenio(model.getAplicaConvenio())
                .porcentajeDiscapacidad(model.getPorcentajeDiscapacidad())
                .idDiscapacidad(model.getIdDiscapacidad())
                .tipoDiscapacidad(model.getTipoDiscapacidad())
                .codigoSalario(model.getCodigoSalario())
                .fechaGeneracion(DateUtils.toString(model.getFechaGeneracion()))
                .ingresos(model.getIngresos())
                .comisiones(model.getComisiones())
                .ingresosNoGravadosIess(model.getOtrosIngresosGravadosImpuestoRenta())
                .decimoTercero(model.getDecimoTercero())
                .decimoCuarto(model.getDecimoCuarto())
                .fondos(model.getFondosDeReserva())
                .utilidades(model.getParticipacionUtilidades())
                .ingresosNoGravadosOtrosEmpleadores(model.getIngresosGravadosOtrosEmpleadores())
                .iess(model.getIessOtrosEmpleadores())
                .gastosVivienda(model.getGastosVivienda())
                .gastosSalud(model.getGastosSalud())
                .gastosAlimentacion(model.getGastosAlimentacion())
                .gastosEducacion(model.getGastosEducacion())
                .gastosVestimenta(model.getGastosVestimenta())
                .gastosTurismo(model.getGastosTurismo())
                .exoneracionDiscapacidad(model.getExoneracionDiscapacidad())
                .exoneracionTerceraEdad(model.getExoneracionTerceraEdad())
                .asumidoOtrosEmpleadores(model.getRetenidoAsumidoOtrosEmpleadores())
                .impuestoCausado(model.getImpuestoCausado())
                .baseImponible(model.getBaseImponible())
                .build();
    }


    public RolCabeceraPDFDto builderPdfCabecera(ReporteRolCabeceraProjection model,
                                                BigDecimal gastosPersonales, BigDecimal tercerEdadDiscapacidad) {
        return RolCabeceraPDFDto.builder()
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .trabajador(model.getTercero())
                .sueldo(model.getIngresos().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getIngresos()))
                .sobreSueldos("")
                .otros(model.getOtrosIngresosRelacionDependenciaNoRentaGravada().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getOtrosIngresosRelacionDependenciaNoRentaGravada()))
                .decimoTercero(model.getDecimoTercero().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getDecimoTercero()))
                .decimoCuarto(model.getDecimoCuarto().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getDecimoCuarto()))
                .fondos(model.getFondosDeReserva().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getFondosDeReserva()))
                .utilidades(model.getParticipacionUtilidades().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getFondosDeReserva()))
                .iessPersonal(model.getAporteIessPersonal().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getAporteIessPersonal()))
                .gastosPersonales(gastosPersonales.compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(gastosPersonales))
                .terceraEdadYDiscapacitados(tercerEdadDiscapacidad.compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getExoneracionDiscapacidad()))
                .ingresosOtrosEmpleadores(model.getIngresosGravadosOtrosEmpleadores().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getIngresosGravadosOtrosEmpleadores()))
                .iessOtrosEmpleadores(model.getIessOtrosEmpleadores().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getIessOtrosEmpleadores()))
                .baseImponible(model.getBaseImponible().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getBaseImponible()))
                .retenidoEsteEmpleador("")
                .retenidoOtrosEmpleador(model.getRetenidoAsumidoOtrosEmpleadores().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getRetenidoAsumidoOtrosEmpleadores()))

                .totalSueldo(model.getIngresos().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getIngresos()))
                .totalSobreSueldos("")
                .totalOtros(model.getOtrosIngresosRelacionDependenciaNoRentaGravada().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getOtrosIngresosRelacionDependenciaNoRentaGravada()))
                .totalDecimoTercero(model.getDecimoTercero().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getDecimoTercero()))
                .totalDecimoCuarto(model.getDecimoCuarto().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getDecimoCuarto()))
                .totalFondos(model.getFondosDeReserva().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getFondosDeReserva()))
                .totalUtilidades(model.getParticipacionUtilidades().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getFondosDeReserva()))
                .totalIessPersonal(model.getAporteIessPersonal().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getAporteIessPersonal()))
                .totalGastosPersonales(gastosPersonales.compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(gastosPersonales))
                .totalTerceraEdadYDiscapacitados(tercerEdadDiscapacidad.compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getExoneracionDiscapacidad()))
                .totalIngresosOtrosEmpleadores(model.getIngresosGravadosOtrosEmpleadores().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getIngresosGravadosOtrosEmpleadores()))
                .totalIessOtrosEmpleadores(model.getIessOtrosEmpleadores().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getIessOtrosEmpleadores()))
                .totalBaseImponible(model.getBaseImponible().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getBaseImponible()))
                .totalRetenidoEsteEmpleador("")
                .totalRetenidoOtrosEmpleador(model.getRetenidoAsumidoOtrosEmpleadores().compareTo(BigDecimal.ZERO) == 0 ? "" :
                        formatoValores.convertirBigDecimalToStringPDF(model.getRetenidoAsumidoOtrosEmpleadores()))
                .build();

    }


}
