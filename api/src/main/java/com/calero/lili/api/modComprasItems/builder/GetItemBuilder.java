package com.calero.lili.api.modComprasItems.builder;

import com.calero.lili.api.modComprasItems.GeImpuestosEntity;
import com.calero.lili.api.modComprasItems.GeItemEntity;
import com.calero.lili.api.modComprasItems.GeItemsPreciosEntity;
import com.calero.lili.api.modComprasItems.GeMedidasItemsEntity;
import com.calero.lili.api.modComprasItems.dto.GeItemGetListDto;
import com.calero.lili.api.modComprasItems.dto.GeItemGetOneDto;
import com.calero.lili.api.modComprasItems.dto.GeItemRequestDto;
import com.calero.lili.api.modComprasItems.dto.GeMedidasItemsDto;
import com.calero.lili.api.modComprasItems.dto.GeMedidasResponseDto;
import com.calero.lili.api.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.api.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.api.modComprasItemsMarcas.GeItemsMarcasEntity;
import com.calero.lili.api.modComprasItemsMedidas.GeItemsMedidasEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GetItemBuilder {

    private final GeItemsPreciosBuilder geItemsPreciosBuilder;

    public GeItemEntity builderEntity(GeItemRequestDto model, Long idData, Long idEmpresa) {
        return GeItemEntity.builder()
                .idItem(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .detallesAdicionales(builderListDetalles(model.getDetallesAdicionales()))
                .impuestos(builderListImpuestos(model.getImpuestos()))
                .geItemsPreciosEntities(geItemsPreciosBuilder.builderListPrecios(model.getPrecios()))
                .grupos(builderGrupo(model.getIdGrupo()))
                .marcas(builderMarca(model.getIdMarca()))
                .categorias(builderCategoria(model.getIdCategoria()))
                .medidas(builderListMedidas(model.getMedidas()))
                .caracteristicas(model.getCaracteristicas())
                .build();
    }

    public GeItemEntity builderUpdateEntity(GeItemRequestDto model, GeItemEntity item) {
        return GeItemEntity.builder()
                .idItem(item.getIdItem())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .detallesAdicionales(builderListDetalles(model.getDetallesAdicionales()))
                .impuestos(builderListImpuestos(model.getImpuestos()))
                .geItemsPreciosEntities(geItemsPreciosBuilder.builderListPrecios(model.getPrecios()))
                .grupos(builderGrupo(model.getIdGrupo()))
                .marcas(builderMarca(model.getIdMarca()))
                .categorias(builderCategoria(model.getIdCategoria()))
                .medidas(builderListMedidas(model.getMedidas()))
                .caracteristicas(model.getCaracteristicas())
                .build();
    }

    private List<GeItemEntity.DetalleAdicional> builderListDetalles(List<GeItemRequestDto.DetalleAdicional> list) {
       // if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetallesAdicionales)
                .toList();
    }

    private GeItemEntity.DetalleAdicional builderDetallesAdicionales(GeItemRequestDto.DetalleAdicional model) {
        return GeItemEntity.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }


    private List<GeImpuestosEntity> builderListImpuestos(List<GeItemRequestDto.Impuesto> list) {
        return list.stream()
                .map(this::builderImpuesto)
                .toList();
    }

    private GeImpuestosEntity builderImpuesto(GeItemRequestDto.Impuesto model) {
        return GeImpuestosEntity.builder()
                .idImpuesto(model.getIdImpuesto())
                .build();
    }

    private GeItemGrupoEntity builderGrupo(UUID idGrupo) {
        return GeItemGrupoEntity.builder()
                .idGrupo(idGrupo)
                .build();
    }

    private GeItemsMarcasEntity builderMarca(UUID idMarca) {
        return GeItemsMarcasEntity.builder()
                .idMarca(idMarca)
                .build();
    }

    private GeItemsCategoriaEntity builderCategoria(UUID idCategoria) {
        return GeItemsCategoriaEntity.builder()
                .idCategoria(idCategoria)
                .build();
    }

    private List<GeMedidasItemsEntity> builderListMedidas(List<GeMedidasItemsDto> list) {
        return list.
                stream()
                .map(this::builderMedidas)
                .toList();
    }

    private GeMedidasItemsEntity builderMedidas(GeMedidasItemsDto model) {
        return GeMedidasItemsEntity.builder()
                .idItemMedida(UUID.randomUUID())
                .idUnidadMedida(model.getIdMedida())
                .factor(model.getFactor())
                .build();
    }

    public GeItemGetOneDto builderResponse(GeItemEntity model) {
        return GeItemGetOneDto.builder()
                .idItem(model.getIdItem())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .caracteristicas(model.getCaracteristicas())
                .idGrupo(model.getGrupos().getIdGrupo())
                .impuestos(builderListImpuestoResponse(model.getImpuestos()))
                .detallesAdicionales(builderListDetallesResponse(model.getDetallesAdicionales()))
                .precios(builderListResponsePrecios(model.getGeItemsPreciosEntities()))
                .categoria(builderResponseCategoria(model.getCategorias()))
                .marca(builderMarcaResponse(model.getMarcas()))
                .grupo(builderGrupoResponse(model.getGrupos()))
                .build();
    }


    public GeItemGetListDto builderListResponse(GeItemEntity model) {
        return GeItemGetListDto.builder()
                .idItem(model.getIdItem())
                .codigoPrincipal(model.getCodigoPrincipal())
                .codigoAuxiliar(model.getCodigoAuxiliar())
                .codigoBarras(model.getCodigoBarras())
                .descripcion(model.getDescripcion())
                .caracteristicas(model.getCaracteristicas())
                .idGrupo(model.getGrupos().getIdGrupo())
                .impuestos(builderResponseListImpuestos(model.getImpuestos()))
                .detallesAdicionales(builderResponseListDetalles(model.getDetallesAdicionales()))
                .precios(builderResponseListPrecios(model.getGeItemsPreciosEntities()))
                .categoria(builderResponseCategoria(model.getCategorias()))
                .marca(builderMarcaResponse(model.getMarcas()))
                .grupo(builderGrupoResponse(model.getGrupos()))
                .build();
    }

    private List<GeItemGetListDto.Precios> builderResponseListPrecios(List<GeItemsPreciosEntity> geItemsPreciosEntities) {
        return geItemsPreciosEntities.stream()
                .map(this::builderListPrecios)
                .toList();
    }

    private GeItemGetListDto.Precios builderListPrecios(GeItemsPreciosEntity model) {
        return GeItemGetListDto.Precios.builder()
                .precio1(model.getPrecio1())
                .precio2(model.getPrecio2())
                .precio3(model.getPrecio3())
                .precio4(model.getPrecio4())
                .precio5(model.getPrecio5())
                .build();
    }

    private List<GeItemGetListDto.DetalleAdicional> builderResponseListDetalles(List<GeItemEntity.DetalleAdicional> detallesAdicionales) {
        return detallesAdicionales.stream()
                .map(this::builderListDetalle)
                .toList();
    }

    private GeItemGetListDto.DetalleAdicional builderListDetalle(GeItemEntity.DetalleAdicional model) {
        return GeItemGetListDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private List<GeItemGetListDto.Impuesto> builderResponseListImpuestos(List<GeImpuestosEntity> impuestos) {
        return impuestos.stream()
                .map(this::builderListImpuesto)
                .toList();
    }

    private GeItemGetListDto.Impuesto builderListImpuesto(GeImpuestosEntity model) {
        return GeItemGetListDto.Impuesto.builder()
                .idImpuesto(model.getIdImpuesto())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .build();
    }


    private List<GeItemGetOneDto.Precios> builderListResponsePrecios(List<GeItemsPreciosEntity> geItemsPreciosEntities) {
        return geItemsPreciosEntities.stream()
                .map(this::builderPrecioResponse)
                .toList();
    }

    private GeItemGetOneDto.Precios builderPrecioResponse(GeItemsPreciosEntity model) {
        return GeItemGetOneDto.Precios.builder()
                .precio1(model.getPrecio1())
                .precio2(model.getPrecio2())
                .precio3(model.getPrecio3())
                .precio4(model.getPrecio4())
                .precio5(model.getPrecio5())
                .build();
    }

    private List<GeItemGetOneDto.DetalleAdicional> builderListDetallesResponse(List<GeItemEntity.DetalleAdicional> detallesAdicionales) {
        return detallesAdicionales.stream()
                .map(this::builderResponseDetalle)
                .toList();
    }

    private GeItemGetOneDto.DetalleAdicional builderResponseDetalle(GeItemEntity.DetalleAdicional model) {
        return GeItemGetOneDto.DetalleAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    private List<GeItemGetOneDto.Impuesto> builderListImpuestoResponse(List<GeImpuestosEntity> impuestos) {
        return impuestos.stream()
                .map(this::builderImpuestoResponse)
                .toList();
    }

    private GeItemGetOneDto.Impuesto builderImpuestoResponse(GeImpuestosEntity model) {
        return GeItemGetOneDto.Impuesto.builder()
                .idImpuesto(model.getIdImpuesto())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .build();
    }


    private GeItemGetOneDto.GrupoItem builderGrupoResponse(GeItemGrupoEntity grupos) {
        if (Objects.isNull(grupos)) return null;
        return GeItemGetOneDto.GrupoItem.builder()
                .idGrupoItem(grupos.getIdGrupo())
                .grupo(grupos.getGrupo())
                .build();
    }

    private GeItemGetOneDto.MarcaItem builderMarcaResponse(GeItemsMarcasEntity marcas) {
        if (Objects.isNull(marcas)) return null;
        return GeItemGetOneDto.MarcaItem.builder()
                .idMarca(marcas.getIdMarca())
                .marca(marcas.getMarca())
                .build();
    }

    private GeItemGetOneDto.CategoriaItem builderResponseCategoria(GeItemsCategoriaEntity categorias) {
        if (Objects.isNull(categorias)) return null;

        return GeItemGetOneDto.CategoriaItem.builder()
                .idCategoria(categorias.getIdCategoria())
                .categoria(categorias.getCategoria())
                .build();
    }


    public GeMedidasResponseDto builderResponseMedidas(GeItemsMedidasEntity model, Integer factor) {
        return GeMedidasResponseDto.builder()
                .idMedida(model.getIdUnidadMedida())
                .medida(model.getUnidadMedida())
                .factor(factor)
                .build();
    }

}
