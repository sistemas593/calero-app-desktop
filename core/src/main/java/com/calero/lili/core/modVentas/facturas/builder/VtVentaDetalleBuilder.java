package com.calero.lili.core.modVentas.facturas.builder;


import com.calero.lili.core.dtos.ImpuestoItemsDto;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modContabilidad.modCentroCostos.CnCentroCostosEntity;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.modVentas.facturas.dto.detalles.DetalleGetDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class VtVentaDetalleBuilder {


    public List<VtVentaDetalleEntity> builderList(List<CreationFacturaRequestDto.DetailDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderDetalle(x, idData, idEmpresa))
                .toList();
    }

    private VtVentaDetalleEntity builderDetalle(CreationFacturaRequestDto.DetailDto model, Long idData, Long idEmpresa) {
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
                .centroCostos(builderCentroCostos(model.getIdCentroCostos()))
                .build();
    }


    // Revisar en todos los detalles.
    private static String getCodigoAuxiliar(CreationFacturaRequestDto.DetailDto model) {
        if (Objects.nonNull(model.getCodigoAuxiliar())) {
            if (model.getCodigoAuxiliar().isEmpty()) {
                return null;
            }
            return model.getCodigoAuxiliar();
        }
        return null;
    }


    private List<VtVentaDetalleEntity.Impuestos> builderListImpuestos(List<ImpuestoItemsDto> list) {
        return list.stream()
                .map(this::builderImpuestos)
                .toList();
    }

    private VtVentaDetalleEntity.Impuestos builderImpuestos(ImpuestoItemsDto model) {
        return VtVentaDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }

    private List<VtVentaDetalleEntity.DetalleAdicional> builderListDetalleAddicional(List<CreationFacturaRequestDto.DetailDto.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private VtVentaDetalleEntity.DetalleAdicional builderDetalle(CreationFacturaRequestDto.DetailDto.DetalleAdicional model) {
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
                .detAdicional(builderListDetalleAddicionalDto(model.getDetAdicional()))
                .subTotalItem(model.getSubtotalItem())
                .unidadMedida(model.getUnidadMedida())
                .centroCostos(builderCentroCostosResponse(model.getCentroCostos()))
                .build();
    }

    private DetalleGetDto.CentroCostosDto builderCentroCostosResponse(CnCentroCostosEntity centroCostos) {
        if (Objects.isNull(centroCostos)) return null;
        return DetalleGetDto.CentroCostosDto.builder()
                .idCentroCostos(centroCostos.getIdCentroCostos())
                .centroCostos(centroCostos.getCentroCostos())
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


    private List<DetalleGetDto.DetalleAdicional> builderListDetalleAddicionalDto(List<VtVentaDetalleEntity.DetalleAdicional> list) {
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


    private CnCentroCostosEntity builderCentroCostos(UUID idCentroCostos) {
        if (Objects.isNull(idCentroCostos)) return null;
        return CnCentroCostosEntity.builder()
                .idCentroCostos(idCentroCostos)
                .build();
    }

}
