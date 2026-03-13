package com.calero.lili.api.modAuditoria;

import com.calero.lili.core.errors.exceptions.GeneralException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

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


    public String getTipoPermisoVerFacturas() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        boolean verTodas = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_VR_TD"));

        boolean verSucursal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_VR_SC"));

        boolean verPropias = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_VR_PR"));

        if (verTodas) {
            return "TODAS";
        }

        if (verSucursal) {
            return "SUCURSAL";
        }

        if (verPropias) {
            return "PROPIAS";
        }

        throw new GeneralException("No tiene permisos para acceder a esta sección");

    }

    public String getTipoPermisoModificarFacturas() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        boolean verPropias = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_MO_PR"));

        boolean verSucursal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_MO_SC"));

        boolean verTodas = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_MO_TD"));

        if (verTodas) {
            return "TODAS";
        }

        if (verSucursal) {
            return "SUCURSAL";
        }

        if (verPropias) {
            return "PROPIAS";
        }

        throw new GeneralException("No tiene permisos para acceder a esta sección");

    }

    public String getTipoPermisoEliminarFacturas() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        boolean verPropias = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_EL_PR"));

        boolean verSucursal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_EL_SC"));

        boolean verTodas = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_EL_TD"));

        if (verTodas) {
            return "TODAS";
        }

        if (verSucursal) {
            return "SUCURSAL";
        }

        if (verPropias) {
            return "PROPIAS";
        }

        throw new GeneralException("No tiene permisos para acceder a esta sección");

    }

    public String getTipoPermisoAnularFacturas() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        boolean verPropias = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_AN_PR"));

        boolean verSucursal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_AN_SC"));

        boolean verTodas = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VT_FC_AN_TD"));

        if (verTodas) {
            return "TODAS";
        }

        if (verSucursal) {
            return "SUCURSAL";
        }

        if (verPropias) {
            return "PROPIAS";
        }

        throw new GeneralException("No tiene permisos para acceder a esta sección");

    }

}
