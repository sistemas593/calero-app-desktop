package com.calero.lili.api.modAuditoria;

import com.calero.lili.core.enums.TipoPermiso;
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

    // -------------------------------------------------------------------------
    // Métodos específicos de Compras Retenciones (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerCompraRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_RT_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_RT_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_RT_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarCompraRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_RT_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_RT_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_RT_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarCompraRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_RT_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_RT_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_RT_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoAnularCompraRetencion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_RT_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_RT_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_RT_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Orden de Compras (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerOrdenCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_OC_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_OC_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_OC_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarOrdenCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_OC_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_OC_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_OC_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarOrdenCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_OC_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_OC_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_OC_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoAnularOrdenCompra() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_OC_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_OC_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_OC_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Asientos Contables (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerAsiento() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CN_AS_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CN_AS_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CN_AS_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarAsiento() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CN_AS_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CN_AS_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CN_AS_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarAsiento() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CN_AS_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CN_AS_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CN_AS_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Liquidaciones de Compra (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerLiquidacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQ_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQ_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQ_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarLiquidacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQ_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQ_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQ_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarLiquidacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQ_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQ_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQ_EL_PR")
        ));
    }

    public TipoPermiso getTipoPermisoAnularLiquidacion() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQ_AN_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQ_AN_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQ_AN_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de Reembolsos de Liquidaciones (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerReembolso() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQR_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQR_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQR_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarReembolso() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQR_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQR_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQR_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarReembolso() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("LQ_LQR_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("LQ_LQR_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("LQ_LQR_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de CxC Facturas (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerXcFactura() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_XC_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_XC_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_XC_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarXcFactura() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_XC_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_XC_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_XC_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarXcFactura() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_XC_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_XC_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_XC_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de CxC Pagos (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerXcPago() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_XP_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_XP_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_XP_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarXcPago() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_XP_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_XP_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_XP_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarXcPago() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_XP_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_XP_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_XP_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de CxP Facturas (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerXpFactura() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_PF_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_PF_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_PF_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarXpFactura() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_PF_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_PF_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_PF_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarXpFactura() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CX_PF_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CX_PF_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CX_PF_EL_PR")
        ));
    }

    // -------------------------------------------------------------------------
    // Métodos específicos de CxP Pagos (usan resolverPermiso internamente)
    // -------------------------------------------------------------------------

    public TipoPermiso getTipoPermisoVerXpPago() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_PG_VR_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_PG_VR_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_PG_VR_PR")
        ));
    }

    public TipoPermiso getTipoPermisoModificarXpPago() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_PG_MO_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_PG_MO_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_PG_MO_PR")
        ));
    }

    public TipoPermiso getTipoPermisoEliminarXpPago() {
        return resolverPermiso(Map.of(
                TipoPermiso.TODAS,    List.of("CP_PG_EL_TD"),
                TipoPermiso.SUCURSAL, List.of("CP_PG_EL_SC"),
                TipoPermiso.PROPIAS,  List.of("CP_PG_EL_PR")
        ));
    }
}
