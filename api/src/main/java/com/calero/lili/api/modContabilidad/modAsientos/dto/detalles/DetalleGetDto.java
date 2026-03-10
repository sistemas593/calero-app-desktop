package com.calero.lili.api.modContabilidad.modAsientos.dto.detalles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class DetalleGetDto {

    private Cuenta cuentas;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Cuenta {
        private UUID idCuenta;
        private String cuenta;
        private String codigoCuenta;
    }


    private Proveedor proveedor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Proveedor {
        private UUID idTercero;
        private String proveedor;
    }


    private Cliente cliente;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Cliente {
        private UUID idTercero;
        private String cliente;
    }

    private GeItem item;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GeItem {
        private UUID idItem;
        private String item;
    }

    private int itemOrden;

    private String detalle;

    private String tipoDocumento;

    private String numeroDocumento;

    private String fechaDocumento;

    private BigDecimal debe;

    private BigDecimal haber;

    private UUID idConciliacion;

}
