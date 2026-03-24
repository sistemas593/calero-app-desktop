package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.dto;

import com.calero.lili.core.enums.TipoEntidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
