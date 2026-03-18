package com.calero.lili.core.modCompras.modCompras.builder;

import com.calero.lili.core.modCompras.modCompras.CpComprasDetalleEntity;
import com.calero.lili.core.modCompras.modCompras.dto.CompraRequestDto;
import com.calero.lili.core.modCompras.modCompras.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CpComprasDetalleBuilder {


    public List<CpComprasDetalleEntity> builderList(List<CompraRequestDto.DetailDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpComprasDetalleEntity builderEntity(CompraRequestDto.DetailDto model, Long idData, Long idEmpresa) {
        return CpComprasDetalleEntity.builder()
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

    private List<CpComprasDetalleEntity.Impuestos> builderListImpuesto(List<CompraRequestDto.DetailDto.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuesto)
                .toList();
    }

    private CpComprasDetalleEntity.Impuestos builderImpuesto(CompraRequestDto.DetailDto.Impuestos model) {
        return CpComprasDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.hashCode())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    private List<CpComprasDetalleEntity.DetalleAdicional> builderListDetalleAdicional(List<CompraRequestDto.DetailDto.DetalleAdicional> list) {
        return list.stream()
                .map(this::builderDetalleAdicional)
                .toList();
    }

    private CpComprasDetalleEntity.DetalleAdicional builderDetalleAdicional(CompraRequestDto.DetailDto.DetalleAdicional model) {
        return CpComprasDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private GeItemEntity builderGetItemBuilder(UUID idItem) {
        return GeItemEntity.builder()
                .idItem(idItem)
                .build();
    }

    public List<DetalleGetDto> builderResponseList(List<CpComprasDetalleEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private DetalleGetDto builderResponse(CpComprasDetalleEntity model) {
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
                .unidadMedida(model.getUnidadMedida())
                .build();
    }

    private List<DetalleGetDto.Impuestos> builderListImpuestoResponse(List<CpComprasDetalleEntity.Impuestos> impuesto) {
        return impuesto.stream()
                .map(this::builderImpuestoResponse)
                .toList();
    }

    private DetalleGetDto.Impuestos builderImpuestoResponse(CpComprasDetalleEntity.Impuestos model) {
        return DetalleGetDto.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }

    private List<DetalleGetDto.DetalleAdicional> builderListDetailResponse(List<CpComprasDetalleEntity.DetalleAdicional> detAdicional) {
        return detAdicional.stream()
                .map(this::builderDetalleAdicionalResponse)
                .toList();

    }

    private DetalleGetDto.DetalleAdicional builderDetalleAdicionalResponse(CpComprasDetalleEntity.DetalleAdicional model) {
        return DetalleGetDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }
}
