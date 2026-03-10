package com.calero.lili.api.modVentasRetenciones.builder;


import com.calero.lili.api.modVentasRetenciones.VtRetencionesValoresEntity;
import com.calero.lili.api.modVentasRetenciones.dto.CreationVentasRetencionesRequestDto;
import com.calero.lili.api.modVentasRetenciones.dto.ResponseValoresDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtRetencionesValoresBuilder {

    public List<VtRetencionesValoresEntity> builderList(List<CreationVentasRetencionesRequestDto.ValoresDto> list, Long idData, Long idEmpresa) {
        return list
                .stream()
                .map(x -> builderValores(x, idData, idEmpresa))
                .toList();
    }

    private VtRetencionesValoresEntity builderValores(CreationVentasRetencionesRequestDto.ValoresDto model, Long idData, Long idEmpresa) {
        return VtRetencionesValoresEntity.builder()
                .idRetencionValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valorRetenido(model.getValor())
                .codigoRetencion(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .codigoDocumento(model.getCodigoDocumento())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .build();
    }

    public List<ResponseValoresDto> builderListValoresDto(List<VtRetencionesValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresDto)
                .toList();
    }

    private ResponseValoresDto builderValoresDto(VtRetencionesValoresEntity model) {
        return ResponseValoresDto.builder()
                .valor(model.getValorRetenido())
                .codigoPorcentaje(model.getCodigoRetencion())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .codigoDocumento(model.getCodigoDocumento())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .build();
    }
}
