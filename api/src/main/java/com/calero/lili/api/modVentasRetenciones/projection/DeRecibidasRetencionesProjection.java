package com.calero.lili.api.modVentasRetenciones.projection;

import java.time.LocalDate;


public interface DeRecibidasRetencionesProjection {
    String getIdData();
    void setIdData(Long idData);

    String getIdEmpresa();
    void setIdEmpresa(Long idEmpresa);

    Long getIdRetencion();
    void setIdRetencion(Long idRetencion);

    String getClaveAcceso();
    void setClaveAcceso(String claveAcceso);

    String getAmbiente();
    void setAmbiente(String ambiente);

    String getEstadoDocumento();
    void setEstadoDocumento(String estadoDocumento);

    String getTipoEmision();
    void setTipoEmision(String tipoEmision);

    String getTipoDocumento();
    void setTipoDocumento(String tipoDocumento);

    String getSerie();
    void setSerie(String serie);

    String getSecuencia();
    void setSecuencia(String secuencia);

    LocalDate getFechaEmision();
    void setFechaEmision(LocalDate fechaEmision);

    LocalDate getFechaAutorizacion();
    void setFechaAutorizacion(LocalDate fechaAutorizacion);

    String getAutorizacionSri();
    void setAutorizacionSri(String autorizacionSri);

    String getNumeroIdentificacion();
    void setNumeroIdentificacion(String numeroIdentificacion);

    float getRetencionRenta();
    void setRetencionRenta(float retencionRenta);

    float getRetencionIva();
    void setRetencionIva(float retencionIva);

    String getMensaje();
    void setMensaje(float mensaje);

    String getXml();
    void setXml(String xml);

    Boolean getImpresa();
    void setImpresa(Boolean impresa);


}
