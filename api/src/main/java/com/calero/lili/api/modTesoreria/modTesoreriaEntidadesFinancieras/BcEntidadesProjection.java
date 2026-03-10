package com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras;

import java.math.BigDecimal;
import java.util.UUID;


public interface BcEntidadesProjection {

    String getIdData();
    void setIdData(Long idData);

    String getIdEmpresa();
    void setIdEmpresa(Long idEmpresa);

    UUID getIdEntidad();
    void setIdEntidad(UUID idEntidad);

    String getEntidad();
    void setEntidad(String entidad);

    UUID getIdCuenta();
    void setIdCuenta(UUID idCuenta);

    String getNumeroCuenta();
    void setNumeroCuenta(String numeroCuenta);

    String getTipoEntidad();
    void setTipoEntidad(String tipoEntidad);

    String getAgencia();
    void setAgencia(String agencia);

    String getContacto();
    void setContacto(String contacto);

    String getTelefono1();
    void setTelefono1(String telefono1);

    String getTelefono2();
    void setTelefono2(String telefono2);

    String getSecuencialCheque();
    void setSecuencialCheque(String secuencialCheque);

    String getArchivoCheque();
    void setArchivoCheque(String archivoCheque);

    BigDecimal getSaldo();
    void setSaldo(BigDecimal saldo);

}
