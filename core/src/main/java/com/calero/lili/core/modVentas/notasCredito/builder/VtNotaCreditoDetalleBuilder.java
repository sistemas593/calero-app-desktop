package com.calero.lili.core.modVentas.notasCredito.builder;


import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.core.modVentas.notasCredito.dto.detalles.DetalleGetDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class VtNotaCreditoDetalleBuilder {


    public List<VtVentaDetalleEntity> builderList(List<CreationNotaCreditoRequestDto.DetailDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderDetalle(x, idData, idEmpresa))
                .toList();
    }

    private VtVentaDetalleEntity builderDetalle(CreationNotaCreditoRequestDto.DetailDto model, Long idData, Long idEmpresa) {
        return VtVentaDetalleEntity.builder()
                .idVentaDetalle(UUID.randomUUID())
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
                .itemOrden(model.getItemOrden())
                .unidadMedida(model.getUnidadMedida())
                .impuesto(builderListImpuestos(model.getImpuesto()))
                .detAdicional(builderListDetalleAddicional(model.getDetAdicional()))
                .items(builderGetItemEntity(model.getIdItem()))
                .build();
    }

    private static String getCodigoAuxiliar(CreationNotaCreditoRequestDto.DetailDto model) {
        if (Objects.nonNull(model.getCodigoAuxiliar())) {
            if (model.getCodigoAuxiliar().isEmpty()) {
                return null;
            }
            return model.getCodigoAuxiliar();
        }
        return null;
    }


    private List<VtVentaDetalleEntity.Impuestos> builderListImpuestos(List<CreationNotaCreditoRequestDto.DetailDto.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuestos)
                .toList();
    }

    private VtVentaDetalleEntity.Impuestos builderImpuestos(CreationNotaCreditoRequestDto.DetailDto.Impuestos model) {
        return VtVentaDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }

    private List<VtVentaDetalleEntity.DetalleAdicional> builderListDetalleAddicional(List<CreationNotaCreditoRequestDto.DetailDto.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private VtVentaDetalleEntity.DetalleAdicional builderDetalle(CreationNotaCreditoRequestDto.DetailDto.DetalleAdicional model) {
        return VtVentaDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    public List<DetalleGetDto> builderListDto(List<VtVentaDetalleEntity> list) {
        return list.stream()
                .map(this::builderDetalleDto)
                .toList();
    }

    private DetalleGetDto builderDetalleDto(VtVentaDetalleEntity model) {
        return DetalleGetDto.builder()
                .idItem(model.getItems().getIdItem())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .cantidad(model.getCantidad())
                .precioUnitario(model.getPrecioUnitario())
                .descuento(model.getDescuento())
                .dsctoItem(model.getDsctoItem())
                .itemOrden(model.getItemOrden())
                .impuesto(builderListImpuestosDto(model.getImpuesto()))
                .detAdicional(builderListDetalleAdicionalDto(model.getDetAdicional()))
                .unidadMedida(model.getUnidadMedida())
                .build();
    }


    private List<DetalleGetDto.Impuestos> builderListImpuestosDto(List<VtVentaDetalleEntity.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuestosDto)
                .toList();
    }

    private DetalleGetDto.Impuestos builderImpuestosDto(VtVentaDetalleEntity.Impuestos model) {
        return DetalleGetDto.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }


    private List<DetalleGetDto.DetalleAdicional> builderListDetalleAdicionalDto(List<VtVentaDetalleEntity.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleDto)
                .toList();
    }

    private DetalleGetDto.DetalleAdicional builderDetalleDto(VtVentaDetalleEntity.DetalleAdicional model) {
        return DetalleGetDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private GeItemEntity builderGetItemEntity(UUID idItem) {
        return GeItemEntity.builder()
                .idItem(idItem)
                .build();

    }

}
