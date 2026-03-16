package com.calero.lili.api.modAuditoria;

import com.calero.lili.core.errors.exceptions.GeneralException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }

    // -------------------------------------------------------------------------
    // Método genérico reutilizable
    // -------------------------------------------------------------------------

    /**
     * Resuelve el nivel de permiso más alto que posee el usuario autenticado,
     * comparando sus authorities contra los códigos proporcionados por nivel.
     *
     * <p>Orden de prioridad: {@code TODAS > SUCURSAL > PROPIAS}.</p>
     *
     * <p>Ejemplo de uso con múltiples códigos por nivel:</p>
     * <pre>{@code
     * resolverPermiso(Map.of(
     *     TipoPermiso.TODAS,    List.of("VT_FC_VR_TD"),
     *     TipoPermiso.SUCURSAL, List.of("VT_FC_VR_SC"),
     *     TipoPermiso.PROPIAS,  List.of("VT_FC_VR_PR", "VT_FC_VR_PR_EXTRA")
     * ));
     * }</pre>
     *
     * @param codigosPorNivel mapa donde la clave es el nivel y el valor es la
     *                        lista de códigos de authority que otorgan ese nivel.
     * @return el {@link TipoPermiso} más alto que posee el usuario.
     * @throws GeneralException si el usuario no posee ninguno de los códigos indicados.
     */
    public TipoPermiso resolverPermiso(Map<TipoPermiso, List<String>> codigosPorNivel) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Set<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        for (TipoPermiso nivel : List.of(TipoPermiso.TODAS, TipoPermiso.SUCURSAL, TipoPermiso.PROPIAS)) {
            List<String> codigos = codigosPorNivel.getOrDefault(nivel, List.of());
            if (codigos.stream().anyMatch(authorities::contains)) {
                return nivel;
            }
        }

        throw new GeneralException("No tiene permisos para acceder a esta sección");
    }


    public TipoPermiso getTipoPermisoFacturaVer() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_FC_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_FC_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_FC_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoFacturaModificar() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_FC_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_FC_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_FC_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoFacturaEliminar() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_FC_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_FC_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_FC_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoFacturaAnular() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_FC_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_FC_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_FC_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Notas de Crédito (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerNotaCredito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_FC_VR_TD","VT_NC_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_NC_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_NC_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarNotaCredito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_NC_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_NC_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_NC_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarNotaCredito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_NC_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_NC_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_NC_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoAnularNotaCredito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_NC_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_NC_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_NC_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Notas de Débito (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerNotaDebito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_ND_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_ND_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_ND_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarNotaDebito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_ND_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_ND_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_ND_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarNotaDebito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_ND_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_ND_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_ND_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoAnularNotaDebito() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_ND_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_ND_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_ND_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Guías de Remisión (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerGuia() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_GR_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_GR_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_GR_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarGuia() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_GR_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_GR_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_GR_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarGuia() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_GR_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_GR_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_GR_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoAnularGuia() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_GR_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_GR_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_GR_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Pedidos (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerPedido() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_PD_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_PD_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_PD_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarPedido() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_PD_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_PD_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_PD_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarPedido() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_PD_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_PD_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_PD_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Retenciones de Ventas (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_RT_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_RT_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_RT_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_RT_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_RT_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_RT_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_RT_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_RT_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_RT_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Cotizaciones (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerCotizacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_CO_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_CO_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_CO_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarCotizacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_CO_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_CO_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_CO_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarCotizacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("VT_CO_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("VT_CO_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("VT_CO_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Compras (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_CP_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_CP_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_CP_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_CP_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_CP_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_CP_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_CP_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_CP_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_CP_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Compras Impuestos (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerImpuesto() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_CI_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_CI_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_CI_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarImpuesto() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_CI_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_CI_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_CI_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarImpuesto() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_CI_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_CI_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_CI_EL_PR")
        ));
    }
}
