package com.calero.lili.api.modCompras.modComprasImpuestos.projection;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface ComprasImpuestoProjection {


    LocalDate getFecha_emision();
    LocalDate getFecha_registro();


    String getTercero();
    String getNumero_identificacion();


    String getCodigo_documento();
    String getSerie();
    String getNumero_autorizacion();
    String getSecuencial();

    LocalDate getFecha_vencimiento();

    String getConcepto();
    String getDevolucion_iva();
    String getReferencia();


    BigDecimal getBase_imponible_cero();
    BigDecimal getTarifa_iva_cero();
    BigDecimal getValor_iva_cero();


    BigDecimal getBase_imponible_quince();
    BigDecimal getTarifa_iva_quince();
    BigDecimal getValor_iva_quince();


    BigDecimal getBase_imponible_cinco();
    BigDecimal getTarifa_iva_cinco();
    BigDecimal getValor_iva_cinco();


    BigDecimal getBase_imponible_ocho();
    BigDecimal getTarifa_iva_ocho();
    BigDecimal getValor_iva_ocho();


    BigDecimal getBase_imponible_no_objecto();
    BigDecimal getBase_imponible_exento();

    LocalDate getFecha_autorizacion();


    String getCodigo_documento_reemb();
    String getSerie_reemb();
    String getSecuencial_reemb();
    String getNumero_autorizacion_reemb();
    LocalDate getFecha_emision_reemb();

}
