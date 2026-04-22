package com.calero.lili.core.modVentasGuias.builder;


import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaDetalleEntity;
import com.calero.lili.core.modVentasGuias.dto.CreationRequestGuiaRemisionDto;
import com.calero.lili.core.modVentasGuias.dto.detalles.DetalleGetDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class VtGuiaDetalleBuilder {


    public List<VtGuiaDetalleEntity> builderList(List<CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderDetalle(x, idData, idEmpresa))
                .toList();
    }

    private VtGuiaDetalleEntity builderDetalle(CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto model, Long idData, Long idEmpresa) {
        return VtGuiaDetalleEntity.builder()
                .idGuiaDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(getCodigoAuxiliar(model))
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .cantidad(model.getCantidad())
                .itemOrden(model.getItemOrden())
                .unidadMedida(model.getUnidadMedida())
                .detAdicional(builderListDetalleAddicional(model.getDetAdicional()))
                .items(builderGetItem(model.getIdItem()))
                .build();
    }


    private static String getCodigoAuxiliar(CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto model) {
        if (Objects.nonNull(model.getCodigoAuxiliar())) {
            if (model.getCodigoAuxiliar().isEmpty()) {
                return null;
            }
            return model.getCodigoAuxiliar();
        }
        return null;
    }

    private List<VtGuiaDetalleEntity.DetalleAdicional> builderListDetalleAddicional(List<CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private VtGuiaDetalleEntity.DetalleAdicional builderDetalle(CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto.DetalleAdicional model) {
        return VtGuiaDetalleEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    public List<DetalleGetDto> builderListDto(List<VtGuiaDetalleEntity> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleDto)
                .toList();
    }

    private DetalleGetDto builderDetalleDto(VtGuiaDetalleEntity model) {
        return DetalleGetDto.builder()
                .idItem(model.getItems().getIdItem())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .cantidad(model.getCantidad())
                .itemOrden(model.getItemOrden())
                .unidadMedida(model.getUnidadMedida())
                .detAdicional(builderListDetalleAddicionalDto(model.getDetAdicional()))
                .build();
    }


    private List<DetalleGetDto.DetalleAdicional> builderListDetalleAddicionalDto(List<VtGuiaDetalleEntity.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleDto)
                .toList();
    }

    private DetalleGetDto.DetalleAdicional builderDetalleDto(VtGuiaDetalleEntity.DetalleAdicional model) {
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
