package com.calero.lili.core.modCompras.modComprasLiquidaciones.builder;

import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesDetalleEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.CreationRequestLiquidacionCompraDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CpLiquidacionesDetalleBuilder {


    public List<CpLiquidacionesDetalleEntity> builderList(List<CreationRequestLiquidacionCompraDto.DetailDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpLiquidacionesDetalleEntity builderEntity(CreationRequestLiquidacionCompraDto.DetailDto model, Long idData, Long idEmpresa) {
        return CpLiquidacionesDetalleEntity.builder()
                .idLiquidacionDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(getCodigoAuxiliar(model))
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


    private static String getCodigoAuxiliar(CreationRequestLiquidacionCompraDto.DetailDto model) {
        if (Objects.nonNull(model.getCodigoAuxiliar())) {
            if (model.getCodigoAuxiliar().isEmpty()) {
                return null;
            }
            return model.getCodigoAuxiliar();
        }
        return null;
    }

    private List<CpLiquidacionesDetalleEntity.Impuestos> builderListImpuesto(List<CreationRequestLiquidacionCompraDto.DetailDto.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuesto)
                .toList();
    }

    private CpLiquidacionesDetalleEntity.Impuestos builderImpuesto(CreationRequestLiquidacionCompraDto.DetailDto.Impuestos model) {
        return CpLiquidacionesDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    private List<CpLiquidacionesDetalleEntity.DetalleAdicional> builderListDetalleAdicional(List<CreationRequestLiquidacionCompraDto.DetailDto.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleAdicional)
                .toList();
    }

    private CpLiquidacionesDetalleEntity.DetalleAdicional builderDetalleAdicional(CreationRequestLiquidacionCompraDto.DetailDto.DetalleAdicional model) {
        return CpLiquidacionesDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private GeItemEntity builderGetItemBuilder(UUID idItem) {
        return GeItemEntity.builder()
                .idItem(idItem)
                .build();
    }

    public List<DetalleGetDto> builderListResponse(List<CpLiquidacionesDetalleEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private DetalleGetDto builderResponse(CpLiquidacionesDetalleEntity model) {
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

    private List<DetalleGetDto.Impuestos> builderListImpuestoResponse(List<CpLiquidacionesDetalleEntity.Impuestos> impuesto) {
        return impuesto.stream()
                .map(this::builderImpuestoResponse)
                .toList();
    }

    private DetalleGetDto.Impuestos builderImpuestoResponse(CpLiquidacionesDetalleEntity.Impuestos model) {
        return DetalleGetDto.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }

    private List<DetalleGetDto.DetalleAdicional> builderListDetailResponse(List<CpLiquidacionesDetalleEntity.DetalleAdicional> detAdicional) {
        if (Objects.isNull(detAdicional)) return null;
        return detAdicional.stream()
                .map(this::builderDetalleAdicionalResponse)
                .toList();

    }

    private DetalleGetDto.DetalleAdicional builderDetalleAdicionalResponse(CpLiquidacionesDetalleEntity.DetalleAdicional model) {
        return DetalleGetDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }
}
