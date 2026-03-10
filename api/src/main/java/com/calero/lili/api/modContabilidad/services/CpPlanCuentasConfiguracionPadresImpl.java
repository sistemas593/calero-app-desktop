package com.calero.lili.api.modContabilidad.services;

import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CpPlanCuentasConfiguracionPadresImpl {


    public void asignarPadres(List<CnPlanCuentaEntity> cuentas) {
        Map<String, UUID> mapaCodigoUuid = new HashMap<>();

        for (CnPlanCuentaEntity cuenta : cuentas) {
            mapaCodigoUuid.put(cuenta.getCodigoCuentaOriginal(), cuenta.getIdCuenta());
        }

        for (CnPlanCuentaEntity cuenta : cuentas) {
            String codigo = cuenta.getCodigoCuentaOriginal();

            String codigoPadre = obtenerPadre(codigo);
            if (codigoPadre != null && mapaCodigoUuid.containsKey(codigoPadre)) {
                cuenta.setIdCuentaPadre(mapaCodigoUuid.get(codigoPadre));
            }
        }
    }

    private String obtenerPadre(String codigo) {
        if (codigo == null || !codigo.contains(".")) return null;

        String trimmed = codigo.endsWith(".") ? codigo.substring(0, codigo.length() - 1) : codigo;
        int lastDot = trimmed.lastIndexOf('.');
        if (lastDot == -1) return null;

        return trimmed.substring(0, lastDot + 1);
    }





}
