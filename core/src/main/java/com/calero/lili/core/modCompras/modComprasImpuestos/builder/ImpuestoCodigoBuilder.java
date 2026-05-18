package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class ImpuestoCodigoBuilder {

    public List<CpImpuestosCodigosEntity> builderList(List<ImpuestoCodigoDto> list, Long idData, Long idEmpresa) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(x -> builderImpuestoCodigo(x, idData, idEmpresa))
                .toList();
    }

    public List<CpImpuestosCodigosEntity> builderMultiList(List<ImpuestoCodigoDto> list, Long idData, Long idEmpresa) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(x -> builderImpuestoCodigo(x, idData, idEmpresa))
                .toList();
    }

    private CpImpuestosCodigosEntity builderImpuestoCodigo(ImpuestoCodigoDto model, Long idData, Long idEmpresa) {
        return CpImpuestosCodigosEntity.builder()
                .idImpuestosCodigo(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigo(model.getCodigo())
                .codigoRetencion(model.getCodigoRetencion())
                .baseImponible(model.getBaseImponible())
                .porcentajeRetener(model.getPorcentajeRetener())
                .valorRetenido(model.getValorRetenido())
                .build();
    }

    public List<ImpuestoCodigoDto> builderListResponse(List<CpImpuestosCodigosEntity> list) {
        return list.stream()
                .map(this::builderResponseImpuestoCodigo)
                .toList();
    }

    private ImpuestoCodigoDto builderResponseImpuestoCodigo(CpImpuestosCodigosEntity model) {
        return ImpuestoCodigoDto.builder()
                .idImpuestos(model.getIdImpuestosCodigo())
                .codigo(model.getCodigo())
                .codigoRetencion(model.getCodigoRetencion())
                .baseImponible(model.getBaseImponible())
                .porcentajeRetener(model.getPorcentajeRetener())
                .valorRetenido(model.getValorRetenido())
                .build();
    }
}
