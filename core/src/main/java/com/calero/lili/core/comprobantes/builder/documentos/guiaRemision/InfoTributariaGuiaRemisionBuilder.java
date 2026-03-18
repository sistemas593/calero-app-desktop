package com.calero.lili.core.comprobantes.builder.documentos.guiaRemision;

import com.calero.lili.core.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.enums.TipoContribuyente;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InfoTributariaGuiaRemisionBuilder {

    public InfoTributaria builderInfoTributaria(VtGuiaEntity venta,
                                                AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie) {
        return InfoTributaria.builder()
                .ambiente(venta.getAmbiente().toString())
                .claveAcceso(venta.getClaveAcceso())
                .codDoc(venta.getCodigoDocumento())
                .dirMatriz(empresa.getDireccionMatriz())
                .razonSocial(empresa.getRazonSocial())
                .ruc(empresa.getRuc())
                .nombreComercial(serie.getNombreComercial())
                .tipoEmision(venta.getTipoEmision().toString())
                .estab(venta.getSerie().substring(0, 3))
                .ptoEmi(venta.getSerie().substring(3, 6))
                .secuencial(venta.getSecuencial())
                .agenteRetencion(empresa.getAgenteRetencion())
                .contribuyenteRimpe(validacionTipoContribuyente(empresa.getTipoContribuyente()))
                .build();
    }

    private String validacionTipoContribuyente(TipoContribuyente tipoContribuyente) {
        if (Objects.isNull(tipoContribuyente)) return null;
        return tipoContribuyente.getTipoContribuyente();
    }
}
