package com.calero.lili.api.comprobantes.builder.documentos;

import com.calero.lili.api.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.enums.TipoContribuyente;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.api.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.api.modVentas.VtVentaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InfoTributariaBuilder {

    public InfoTributaria builderInfoTributaria(VtVentaEntity venta,
                                                AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie,
                                                String codigoDocumento) {
        return InfoTributaria.builder()
                .ambiente(venta.getAmbiente().toString())
                .claveAcceso(venta.getClaveAcceso())
                .codDoc(codigoDocumento)
                .dirMatriz(empresa.getDireccionMatriz())
                .razonSocial(empresa.getRazonSocial())
                .ruc(empresa.getRuc())
                .nombreComercial(serie.getNombreComercial())
                .tipoEmision(venta.getTipoEmision().toString())
                .estab(venta.getSerie().substring(0, 3))
                .ptoEmi(venta.getSerie().substring(3, 6))
                .secuencial(venta.getSecuencial())
                .agenteRetencion(empresa.getAgenteRetencion())
                .contribuyenteRimpe(validarContribuyenteRimpe(empresa.getTipoContribuyente()))
                .build();
    }

    private String validarContribuyenteRimpe(TipoContribuyente tipoContribuyente) {
        if (Objects.isNull(tipoContribuyente)) return null;
        return tipoContribuyente.getTipoContribuyente();
    }
}
