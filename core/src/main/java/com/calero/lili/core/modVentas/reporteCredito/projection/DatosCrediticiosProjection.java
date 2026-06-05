package com.calero.lili.core.modVentas.reporteCredito.projection;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface DatosCrediticiosProjection {

    String getPeriodo();
    String getTipoIdentificacion();
    String getIdentificacionSujeto();
    String getNombreSujeto();
    String getClaseSujeto();
    String getCodigoProvincia();
    String getCodigoCanton();
    String getCodigoParroquia();
    String getSexo();
    String getEstadoCivil();
    String getOrigenIngresos();

    String getNumeroOperacion();

    BigDecimal getValorOperacion();
    BigDecimal getSaldoOperacion();

    LocalDate getFechaConcesion();
    LocalDate getFechaVencimiento();
    LocalDate getFechaExigible();

    Integer getPlazoOperacion();
    String getPeriosidadPago();

    Integer getDiasMorosidad();

    BigDecimal getMontoMorisidad();
    BigDecimal getMontoInteresMora();

    BigDecimal getValorPorVencer1a30Dias();
    BigDecimal getValorPorVencer31a90Dias();
    BigDecimal getValorPorVencer91a180Dias();
    BigDecimal getValorPorVencer181a360Dias();
    BigDecimal getValorPorVencerMas360Dias();

    BigDecimal getValorVencido1a30Dias();
    BigDecimal getValorVencido31a90Dias();
    BigDecimal getValorVencido91a180Dias();
    BigDecimal getValorVencido181a360Dias();
    BigDecimal getValorVencidoMas360Dias();

    BigDecimal getValorDemandaJudicial();
    BigDecimal getCarteraCastigada();
    BigDecimal getCuotaCredito();

    LocalDate getFechaCancelacion();

    String getFormaCancelacion();

}
