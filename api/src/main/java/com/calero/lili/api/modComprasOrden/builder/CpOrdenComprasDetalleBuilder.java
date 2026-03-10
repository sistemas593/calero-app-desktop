package com.calero.lili.api.modComprasOrden.builder;

import com.calero.lili.api.modComprasItems.GeItemEntity;
import com.calero.lili.api.modComprasOrden.CpOrdenComprasDetalleEntity;
import com.calero.lili.api.modComprasOrden.dto.OrdenCompraRequestDto;
import com.calero.lili.api.modComprasOrden.dto.detalles.DetalleGetDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CpOrdenComprasDetalleBuilder {


    public List<CpOrdenComprasDetalleEntity> builderList(List<OrdenCompraRequestDto.DetailDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpOrdenComprasDetalleEntity builderEntity(OrdenCompraRequestDto.DetailDto model, Long idData, Long idEmpresa) {
        return CpOrdenComprasDetalleEntity.builder()
                .idLiquidacionDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .cantidad(model.getCantidad())
                .precioUnitario(model.getPrecioUnitario())
                .descuento(model.getDescuento())
                .dsctoItem(model.getDsctoItem())
                .subtotalItem(model.getSubtotalItem())
                .unidadMedida(model.getUnidadMedida())
                .impuesto(builderListImpuesto(model.getImpuesto()))
                .detAdicional(builderListDetalleAdicional(model.getDetAdicional()))
                .items(builderGetItemBuilder(model.getIdItem()))
                .build();
    }

    private List<CpOrdenComprasDetalleEntity.Impuestos> builderListImpuesto(List<OrdenCompraRequestDto.DetailDto.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuesto)
                .toList();
    }

    private CpOrdenComprasDetalleEntity.Impuestos builderImpuesto(OrdenCompraRequestDto.DetailDto.Impuestos model) {
        return CpOrdenComprasDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.hashCode())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    private List<CpOrdenComprasDetalleEntity.DetalleAdicional> builderListDetalleAdicional(List<OrdenCompraRequestDto.DetailDto.DetalleAdicional> list) {
        return list.stream()
                .map(this::builderDetalleAdicional)
                .toList();
    }

    private CpOrdenComprasDetalleEntity.DetalleAdicional builderDetalleAdicional(OrdenCompraRequestDto.DetailDto.DetalleAdicional model) {
        return CpOrdenComprasDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private GeItemEntity builderGetItemBuilder(UUID idItem) {
        return GeItemEntity.builder()
                .idItem(idItem)
                .build();
    }

    public List<DetalleGetDto> builderResponseList(List<CpOrdenComprasDetalleEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private DetalleGetDto builderResponse(CpOrdenComprasDetalleEntity model) {
        return DetalleGetDto.builder()
                .idItem(model.getItems().getIdItem())
                .itemOrden(model.getItemOrden())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .precioUnitario(model.getPrecioUnitario())
                .cantidad(model.getCantidad())
                .descuento(model.getDescuento())
                .subTotalItem(model.getSubtotalItem())
                .detAdicional(builderListDetailResponse(model.getDetAdicional()))
                .impuesto(builderListImpuestoResponse(model.getImpuesto()))
                .build();
    }

    private List<DetalleGetDto.Impuestos> builderListImpuestoResponse(List<CpOrdenComprasDetalleEntity.Impuestos> impuesto) {
        return impuesto.stream()
                .map(this::builderImpuestoResponse)
                .toList();
    }

    private DetalleGetDto.Impuestos builderImpuestoResponse(CpOrdenComprasDetalleEntity.Impuestos model) {
        return DetalleGetDto.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }

    private List<DetalleGetDto.DetalleAdicional> builderListDetailResponse(List<CpOrdenComprasDetalleEntity.DetalleAdicional> detAdicional) {
        return detAdicional.stream()
                .map(this::builderDetalleAdicionalResponse)
                .toList();

    }

    private DetalleGetDto.DetalleAdicional builderDetalleAdicionalResponse(CpOrdenComprasDetalleEntity.DetalleAdicional model) {
        return DetalleGetDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }
}
