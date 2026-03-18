package com.calero.lili.core.enums;

/**
 * Niveles de permiso de acceso a datos.
 * <p>
 * Prioridad (de mayor a menor): TODAS > SUCURSAL > PROPIAS
 * <ul>
 *   <li>TODAS    – el usuario puede ver/operar sobre todos los registros.</li>
 *   <li>SUCURSAL – el usuario solo puede ver/operar los registros de su sucursal.</li>
 *   <li>PROPIAS  – el usuario solo puede ver/operar los registros creados por él mismo.</li>
 * </ul>
 */
public enum TipoPermiso {
    TODAS,
    SUCURSAL,
    PROPIAS
}
