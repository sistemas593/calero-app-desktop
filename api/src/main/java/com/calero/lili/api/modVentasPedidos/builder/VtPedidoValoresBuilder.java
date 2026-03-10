package com.calero.lili.api.modVentasPedidos.builder;

import com.calero.lili.api.modVentasPedidos.VtPedidoValoresEntity;
import com.calero.lili.api.modVentasPedidos.dto.CreationComprasPedidosRequestDto;
import com.calero.lili.api.modVentasPedidos.dto.ResponseValoresDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtPedidoValoresBuilder {

    public  List<VtPedidoValoresEntity> builderList(List<CreationComprasPedidosRequestDto.ValoresDto> list){
        return list.stream()
                .map(this::builderValores)
                .toList();
    }

    private VtPedidoValoresEntity builderValores(CreationComprasPedidosRequestDto.ValoresDto model) {
        return VtPedidoValoresEntity.builder()
                .idPedidoValores(UUID.randomUUID())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }

    public List<ResponseValoresDto> builderListResponse(List<VtPedidoValoresEntity> list){
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private ResponseValoresDto builderResponse(VtPedidoValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .build();
    }

}
