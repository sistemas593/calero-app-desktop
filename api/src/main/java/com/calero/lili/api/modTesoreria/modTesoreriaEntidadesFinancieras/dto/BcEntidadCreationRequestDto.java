package com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.dto;

import com.calero.lili.core.enums.TipoEntidad;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
public class BcEntidadCreationRequestDto {

    private UUID idEntidad;
    private TipoEntidad tipoEntidad;
    private String entidad;
    private UUID idCuenta;
    private String numeroCuenta;
    private String agencia;
    private String contacto;
    private String telefono1;
    private String telefono2;
    private String secuencialCheque;
    private String archivoCheque;
    private BigDecimal saldo;

}
