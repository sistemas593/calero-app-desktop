package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.tablas.tbRetenciones.TbRetencionEntity;
import com.calero.lili.core.tablas.tbRetenciones.TbRetencionesGetOneDto;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosEntity;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosGetOneDto;
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
                .retencion(builderRetencion(model.getCodigo()))
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
                .retencion(builderRetencionResponse(model.getRetencion()))
//                .retencionCodigo(builderCodigosRetencionResponse(model.getCodigoRetencion()))
                .baseImponible(model.getBaseImponible())
                .porcentajeRetener(model.getPorcentajeRetener())
                .valorRetenido(model.getValorRetenido())
                .build();
    }

    private TbRetencionEntity builderRetencion(String codigo) {
        return TbRetencionEntity.builder()
                .codigo(codigo)
                .build();
    }

    private TbRetencionesGetOneDto builderRetencionResponse(TbRetencionEntity model) {
        return TbRetencionesGetOneDto.builder()
                .codigo(model.getCodigo())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }

    public TbRetencionesCodigosEntity builderCodigosRetencion(String codigoRetencion) {
        return TbRetencionesCodigosEntity.builder()
                .codigoRetencion(codigoRetencion)
                .build();
    }

    // TODO VERIFICAR
    public TbRetencionesCodigosGetOneDto builderCodigosRetencionResponse(TbRetencionesCodigosEntity model) {
        return TbRetencionesCodigosGetOneDto.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }

}
