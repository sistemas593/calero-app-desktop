package com.calero.lili.api.modCompras.modComprasImpuestos.builder;

import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosReembolsosEntity;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.Reembolso;
import com.calero.lili.api.tablas.tbPaises.TbPaisEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReembolsoBuilder {

    public List<CpImpuestosReembolsosEntity> builderListEntity(List<Reembolso> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builder(x, idData, idEmpresa))
                .toList();
    }

    private CpImpuestosReembolsosEntity builder(Reembolso model, Long idData, Long idEmpresa) {
        return CpImpuestosReembolsosEntity.builder()
                .idImpuestosReembolsos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .numeroIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .serieReemb(model.getSerieReemb())
                .fechaEmisionReemb(model.getFechaEmisionReemb())
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .build();
    }


    public List<Reembolso> builderListDto(List<CpImpuestosReembolsosEntity> list) {
        return list.stream()
                .map(this::builderDto)
                .toList();
    }

    private Reembolso builderDto(CpImpuestosReembolsosEntity model) {
        return Reembolso.builder()
                .numeroIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .pais(builderResponsePais(model.getPais()))
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .serieReemb(model.getSerieReemb())
                .fechaEmisionReemb(model.getFechaEmisionReemb())
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .build();
    }

    private Reembolso.PaisDto builderResponsePais(TbPaisEntity pais) {
        return Reembolso.PaisDto.builder()
                .pais(pais.getPais())
                .codigoPais(pais.getCodigoPais())
                .build();
    }


    private TbPaisEntity builderPais(String codPaisPagoReemb) {
        return TbPaisEntity.builder()
                .codigoPais(codPaisPagoReemb)
                .build();
    }
}
