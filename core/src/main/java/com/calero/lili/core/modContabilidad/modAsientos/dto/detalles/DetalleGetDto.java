package com.calero.lili.core.modContabilidad.modAsientos.dto.detalles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleGetDto {

    private UUID idCuenta;
    private String cuenta;
    private String codigoCuenta;

    private UUID idTercero;
    private String numeroIdentificacion;
    private String tercero;

    private UUID idItem;
    private String item;

    private int itemOrden;

    private String detalle;

    private String tipoDocumento;

    private String numeroDocumento;

    private String fechaDocumento;

    private BigDecimal debe;

    private BigDecimal haber;

    private UUID idCentroCosto;
    private String centroCosto;


}
