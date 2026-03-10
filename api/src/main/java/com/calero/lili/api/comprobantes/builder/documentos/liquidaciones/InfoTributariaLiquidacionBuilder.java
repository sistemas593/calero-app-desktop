package com.calero.lili.api.comprobantes.builder.documentos.liquidaciones;

import com.calero.lili.api.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.enums.TipoContribuyente;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.api.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InfoTributariaLiquidacionBuilder {

    public InfoTributaria builderInfoTributaria(CpLiquidacionesEntity liquidacion,
                                                AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie) {
        return InfoTributaria.builder()
                .ambiente(liquidacion.getAmbiente().toString())
                .claveAcceso(liquidacion.getClaveAcceso())
                .codDoc("03")
                .dirMatriz(empresa.getDireccionMatriz())
                .razonSocial(empresa.getRazonSocial())
                .ruc(empresa.getRuc())
                .nombreComercial(serie.getNombreComercial())
                .tipoEmision(liquidacion.getTipoEmision().toString())
                .estab(liquidacion.getSerie().substring(0, 3))
                .ptoEmi(liquidacion.getSerie().substring(3, 6))
                .secuencial(liquidacion.getSecuencial())
                .agenteRetencion(empresa.getAgenteRetencion())
                .contribuyenteRimpe(validacionTipoContribuyente(empresa.getTipoContribuyente()))
                .build();
    }

    private String validacionTipoContribuyente(TipoContribuyente tipoContribuyente) {
        if (Objects.isNull(tipoContribuyente)) return null;
        return tipoContribuyente.getTipoContribuyente();
    }
}
