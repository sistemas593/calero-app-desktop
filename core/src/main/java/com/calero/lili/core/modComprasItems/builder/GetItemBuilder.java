package com.calero.lili.core.modComprasItems.builder;

import com.calero.lili.core.modComprasItems.GeImpuestosEntity;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsPreciosEntity;
import com.calero.lili.core.modComprasItems.GeMedidasItemsEntity;
import com.calero.lili.core.modComprasItems.dto.GeItemGetListDto;
import com.calero.lili.core.modComprasItems.dto.GeItemGetOneDto;
import com.calero.lili.core.modComprasItems.dto.GeItemRequestDto;
import com.calero.lili.core.modComprasItems.dto.GeMedidasItemsDto;
import com.calero.lili.core.modComprasItems.dto.GeMedidasResponseDto;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.core.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.core.modComprasItemsMarcas.GeItemsMarcasEntity;
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasEntity;
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
                .tipoItem(model.getTipoItem())
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
        item.setCodigoPrincipal(model.getCodigoPrincipal());
        item.setCodigoAuxiliar(model.getCodigoAuxiliar());
        item.setCodigoBarras(model.getCodigoBarras());
        item.setDescripcion(model.getDescripcion());
        item.setTipoItem(model.getTipoItem());
        item.setCaracteristicas(model.getCaracteristicas());
        item.setGrupos(builderGrupo(model.getIdGrupo()));
        item.setMarcas(builderMarca(model.getIdMarca()));
        item.setCategorias(builderCategoria(model.getIdCategoria()));

        List<GeItemEntity.DetalleAdicional> nuevosDetalles = builderListDetalles(model.getDetallesAdicionales());
        if (item.getDetallesAdicionales() != null) {
            item.getDetallesAdicionales().clear();
            if (nuevosDetalles != null) item.getDetallesAdicionales().addAll(nuevosDetalles);
        } else {
            item.setDetallesAdicionales(nuevosDetalles);
        }

        List<GeImpuestosEntity> nuevosImpuestos = builderListImpuestos(model.getImpuestos());
        if (item.getImpuestos() != null) {
            item.getImpuestos().clear();
            if (nuevosImpuestos != null) item.getImpuestos().addAll(nuevosImpuestos);
        } else {
            item.setImpuestos(nuevosImpuestos);
        }

        List<GeItemsPreciosEntity> nuevosPrecios = geItemsPreciosBuilder.builderListPrecios(model.getPrecios());
        if (item.getGeItemsPreciosEntities() != null) {
            item.getGeItemsPreciosEntities().clear();
            if (nuevosPrecios != null) item.getGeItemsPreciosEntities().addAll(nuevosPrecios);
        } else {
            item.setGeItemsPreciosEntities(nuevosPrecios);
        }

        List<GeMedidasItemsEntity> nuevasMedidas = builderListMedidas(model.getMedidas());
        if (item.getMedidas() != null) {
            item.getMedidas().clear();
            if (nuevasMedidas != null) item.getMedidas().addAll(nuevasMedidas);
        } else {
            item.setMedidas(nuevasMedidas);
        }

        return item;
    }

    private List<GeItemEntity.DetalleAdicional> builderListDetalles(List<GeItemRequestDto.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
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
        if (Objects.isNull(list)) return null;
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
        if (Objects.isNull(idGrupo)) return null;
        return GeItemGrupoEntity.builder()
                .idGrupo(idGrupo)
                .build();
    }

    private GeItemsMarcasEntity builderMarca(UUID idMarca) {
        if (Objects.isNull(idMarca)) return null;
        return GeItemsMarcasEntity.builder()
                .idMarca(idMarca)
                .build();
    }

    private GeItemsCategoriaEntity builderCategoria(UUID idCategoria) {
        if (Objects.isNull(idCategoria)) return null;
        return GeItemsCategoriaEntity.builder()
                .idCategoria(idCategoria)
                .build();
    }

    private List<GeMedidasItemsEntity> builderListMedidas(List<GeMedidasItemsDto> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
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
                .tipoItem(model.getTipoItem())
                .caracteristicas(model.getCaracteristicas())
                .idGrupo(model.getGrupos() != null ? model.getGrupos().getIdGrupo() : null)
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
                .tipoItem(model.getTipoItem())
                .caracteristicas(model.getCaracteristicas())
                .ultimaCompra(model.getUltimaCompra())
                .ultimaVenta(model.getUltimaVenta())
                .idGrupo(model.getGrupos() != null ? model.getGrupos().getIdGrupo() : null)
                .impuestos(builderResponseListImpuestos(model.getImpuestos()))
                .detallesAdicionales(builderResponseListDetalles(model.getDetallesAdicionales()))
                .precios(builderResponseListPrecios(model.getGeItemsPreciosEntities()))
                .categoria(builderResponseCategoria(model.getCategorias()))
                .marca(builderMarcaResponse(model.getMarcas()))
                .grupo(builderGrupoResponse(model.getGrupos()))
                .build();
    }

    private List<GeItemGetListDto.Precios> builderResponseListPrecios(List<GeItemsPreciosEntity> geItemsPreciosEntities) {
        if (Objects.isNull(geItemsPreciosEntities)) return null;
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
        if (Objects.isNull(detallesAdicionales) || detallesAdicionales.isEmpty()) return null;
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
        if (Objects.isNull(impuestos)) return null;
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
        if (Objects.nonNull(geItemsPreciosEntities) || geItemsPreciosEntities.isEmpty()) return null;
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
        if (Objects.isNull(detallesAdicionales) || detallesAdicionales.isEmpty()) return null;
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
        if (Objects.isNull(impuestos) || impuestos.isEmpty()) return null;
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
