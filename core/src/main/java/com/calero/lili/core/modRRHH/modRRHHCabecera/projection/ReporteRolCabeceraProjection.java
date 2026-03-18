package com.calero.lili.core.modRRHH.modRRHHCabecera.projection;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReporteRolCabeceraProjection {


    String getNumeroIdentificacion();
    String getTercero();
    String getTipoIdentificacion();
    String getCodigoEstablecimiento();
    String getCodigoResidencia();
    String getCodigoPais();
    String getAplicaConvenio();
    String getTipoDiscapacidad();
    String getPorcentajeDiscapacidad();
    String getEnfermedadCatastrofica();
    String getBeneficioProvGalapagos();
    String getCodigoSalario();
    Integer getNumeroCargas();
    String getIdDiscapacidad();
    LocalDate getFechaGeneracion();
    String getTipoIdDiscapacidad();
    String getApellidos();
    String getNombres();
    BigDecimal getIngresos();
    BigDecimal getComisiones();
    BigDecimal getOtrosIngresosGravadosImpuestoRenta();
    BigDecimal getDecimoTercero();
    BigDecimal getDecimoCuarto();
    BigDecimal getFondosDeReserva();
    BigDecimal getParticipacionUtilidades();
    BigDecimal getIngresosGravadosOtrosEmpleadores();
    BigDecimal getIessOtrosEmpleadores();
    BigDecimal getGastosVivienda();
    BigDecimal getGastosSalud();
    BigDecimal getGastosAlimentacion();
    BigDecimal getGastosEducacion();
    BigDecimal getGastosVestimenta();
    BigDecimal getGastosTurismo();
    BigDecimal getExoneracionDiscapacidad();
    BigDecimal getExoneracionTerceraEdad();
    BigDecimal getRetenidoAsumidoOtrosEmpleadores();
    BigDecimal getBaseImponible();
    BigDecimal getImpuestoCausado();

    BigDecimal getImpuestoRentaAsumidoEmpleador();
    BigDecimal getSalarioDigno();
    BigDecimal getOtrosIngresosRelacionDependenciaNoRentaGravada();
    BigDecimal getAporteIessPersonal();
    BigDecimal getImpuestoRentaRetenido();


}
