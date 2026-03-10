package com.calero.lili.api.comprobantes.builder.documentos.comprobanteRetencion;

import com.calero.lili.api.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.enums.TipoContribuyente;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.api.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionesEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InfoTributariaRetencionBuilder {

    public InfoTributaria builderInfoTributaria(CpRetencionesEntity retencion,
                                                AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie) {
        return InfoTributaria.builder()
                .ambiente(retencion.getAmbiente().toString())
                .claveAcceso(retencion.getClaveAcceso())
                .codDoc(retencion.getCodigoDocumento())
                .dirMatriz(empresa.getDireccionMatriz())
                .razonSocial(empresa.getRazonSocial())
                .ruc(empresa.getRuc())
                .nombreComercial(serie.getNombreComercial())
                .tipoEmision(retencion.getTipoEmision().toString())
                .estab(retencion.getSerieRetencion().substring(0, 3))
                .ptoEmi(retencion.getSerieRetencion().substring(3, 6))
                .secuencial(retencion.getSecuencialRetencion())
                .agenteRetencion(empresa.getAgenteRetencion())
                .contribuyenteRimpe(validarContribuyenteRimpe(empresa.getTipoContribuyente()))
                .build();
    }

    private String validarContribuyenteRimpe(TipoContribuyente tipoContribuyente) {
        if (Objects.isNull(tipoContribuyente)) return null;
        return tipoContribuyente.getTipoContribuyente();
    }
}
