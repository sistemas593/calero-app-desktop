package com.calero.lili.api.modVentasPedidos.builder;

import com.calero.lili.api.modComprasItems.GeItemEntity;
import com.calero.lili.api.modVentasPedidos.VtPedidoDetalleEntity;
import com.calero.lili.api.modVentasPedidos.dto.CreationComprasPedidosRequestDto;
import com.calero.lili.api.modVentasPedidos.dto.detalles.DetalleGetDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtPedidoDetalleBuilder {

    public List<VtPedidoDetalleEntity> builderList(List<CreationComprasPedidosRequestDto.DetailDto> list) {
        return list.stream()
                .map(this::builderPedidoDetalle)
                .toList();
    }

    private VtPedidoDetalleEntity builderPedidoDetalle(CreationComprasPedidosRequestDto.DetailDto model) {
        return VtPedidoDetalleEntity.builder()
                .idPedidoDetalle(UUID.randomUUID())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .cantidad(model.getCantidad())
                .precioUnitario(model.getPrecioUnitario())
                .descuento(model.getDescuento())
                .dsctoItem(model.getDsctoItem())
                .subtotalItem(model.getSubtotalItem())
                .itemOrden(model.getItemOrden())
                .unidadMedida(model.getUnidadMedida())
                .impuesto(builderListImpuestos(model.getImpuesto()))
                .detAdicional(builderListDetalleAddicional(model.getDetAdicional()))
                .items(builderItem(model.getIdItem()))
                .build();
    }

    private GeItemEntity builderItem(UUID idItem) {
        return GeItemEntity.builder()
                .idItem(idItem)
                .build();
    }

    private List<VtPedidoDetalleEntity.Impuestos> builderListImpuestos(List<CreationComprasPedidosRequestDto.DetailDto.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuestos)
                .toList();
    }

    private VtPedidoDetalleEntity.Impuestos builderImpuestos(CreationComprasPedidosRequestDto.DetailDto.Impuestos model) {
        return VtPedidoDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    private List<VtPedidoDetalleEntity.DetalleAdicional> builderListDetalleAddicional(List<CreationComprasPedidosRequestDto.DetailDto.DetalleAdicional> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private VtPedidoDetalleEntity.DetalleAdicional builderDetalle(CreationComprasPedidosRequestDto.DetailDto.DetalleAdicional model) {
        return VtPedidoDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }


    public List<DetalleGetDto> builderListResponse(List<VtPedidoDetalleEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private DetalleGetDto builderResponse(VtPedidoDetalleEntity model) {
        return DetalleGetDto.builder()
                .idItem(model.getItems().getIdItem())
                .itemOrden(model.getItemOrden())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .precioUnitario(model.getPrecioUnitario())
                .cantidad(model.getCantidad())
                .dsctoItem(model.getDsctoItem())
                .descuento(model.getDescuento())
                .subTotalItem(model.getSubtotalItem())
                .detAdicional(builderListDetalleAddicionalResponse(model.getDetAdicional()))
                .impuesto(builderListImpuestosResponse(model.getImpuesto()))
                .build();
    }

    private List<DetalleGetDto.DetalleAdicional> builderListDetalleAddicionalResponse(List<VtPedidoDetalleEntity.DetalleAdicional> list) {
        return list.stream()
                .map(this::builderDetalleResponse)
                .toList();
    }

    private DetalleGetDto.DetalleAdicional builderDetalleResponse(VtPedidoDetalleEntity.DetalleAdicional model) {
        return DetalleGetDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }


    private List<DetalleGetDto.Impuestos> builderListImpuestosResponse(List<VtPedidoDetalleEntity.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuestosResponse)
                .toList();
    }

    private DetalleGetDto.Impuestos builderImpuestosResponse(VtPedidoDetalleEntity.Impuestos model) {
        return DetalleGetDto.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


}
