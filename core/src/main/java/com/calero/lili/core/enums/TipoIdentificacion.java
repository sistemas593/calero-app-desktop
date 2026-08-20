package com.calero.lili.core.enums;

import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoIdentificacion {
    R("RUC", "04", "01"),
    C("CEDULA", "05", "02"),
    P("PASAPORTE", "06", "03");

    private final String identificacion;
    private final String codigo;
    private final String codigoCompra;

    public static TipoIdentificacion obtenerTipoIdentificacion(String codigo) {
        for (TipoIdentificacion tipo : TipoIdentificacion.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        return R;
    }

    public static TipoIdentificacion obtenerTipoIdentifiacionPorCodigoCompra(String codigoCompra) {
        for (TipoIdentificacion tipo : TipoIdentificacion.values()) {
            if (tipo.getCodigoCompra().equals(codigoCompra)) {
                return tipo;
            }
        }
        throw new GeneralException("El codigo de identifiacion:" + codigoCompra + " no existe");
    }


    public static String obtenerNombrePorCodigoCompra(String codigoCompra) {
        for (TipoIdentificacion tipo : TipoIdentificacion.values()) {
            if (tipo.getCodigoCompra().equals(codigoCompra)) {
                return tipo.getIdentificacion();
            }
        }
        return codigoCompra;
    }
}
