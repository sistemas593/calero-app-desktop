package com.calero.lili.core.modVentasCotizaciones.builder;


import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modVentasCotizaciones.VtCotizacionDetalleEntity;
import com.calero.lili.core.modVentasCotizaciones.dto.CreationVentasCotizacionesRequestDto;
import com.calero.lili.core.modVentasCotizaciones.dto.detalles.DetalleGetDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtCotizacionDetalleBuilder {


    public List<VtCotizacionDetalleEntity> builderList(List<CreationVentasCotizacionesRequestDto.DetailDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderDetalle(x, idData, idEmpresa))
                .toList();
    }

    private VtCotizacionDetalleEntity builderDetalle(CreationVentasCotizacionesRequestDto.DetailDto model, Long idData, Long idEmpresa) {
        return VtCotizacionDetalleEntity.builder()
                .idCotizacionDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
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
                .detallesAdicionales(builderListDetalleAddicional(model.getDetAdicional()))
                .items(builderGetItem(model.getIdItem()))
                .build();
    }


    private List<VtCotizacionDetalleEntity.Impuestos> builderListImpuestos(List<CreationVentasCotizacionesRequestDto.DetailDto.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuestos)
                .toList();
    }

    private VtCotizacionDetalleEntity.Impuestos builderImpuestos(CreationVentasCotizacionesRequestDto.DetailDto.Impuestos model) {
        return VtCotizacionDetalleEntity.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }

    private List<VtCotizacionDetalleEntity.DetalleAdicional> builderListDetalleAddicional(List<CreationVentasCotizacionesRequestDto.DetailDto.DetalleAdicional> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private VtCotizacionDetalleEntity.DetalleAdicional builderDetalle(CreationVentasCotizacionesRequestDto.DetailDto.DetalleAdicional model) {
        return VtCotizacionDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    public List<DetalleGetDto> builderListDto(List<VtCotizacionDetalleEntity> list) {
        return list.stream()
                .map(this::builderDetalleDto)
                .toList();
    }

    private DetalleGetDto builderDetalleDto(VtCotizacionDetalleEntity model) {
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
                .detAdicional(builderListDetalleAddicionalDto(model.getDetallesAdicionales()))
                .build();
    }


    private List<DetalleGetDto.Impuestos> builderListImpuestosDto(List<VtCotizacionDetalleEntity.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuestosDto)
                .toList();
    }

    private DetalleGetDto.Impuestos builderImpuestosDto(VtCotizacionDetalleEntity.Impuestos model) {
        return DetalleGetDto.Impuestos.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    private List<DetalleGetDto.DetalleAdicional> builderListDetalleAddicionalDto(List<VtCotizacionDetalleEntity.DetalleAdicional> list) {
        return list.stream()
                .map(this::builderDetalleDto)
                .toList();
    }

    private DetalleGetDto.DetalleAdicional builderDetalleDto(VtCotizacionDetalleEntity.DetalleAdicional model) {
        return DetalleGetDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private GeItemEntity builderGetItem(UUID idItem) {
        return GeItemEntity.builder()
                .idItem(idItem)
                .build();
    }

}
