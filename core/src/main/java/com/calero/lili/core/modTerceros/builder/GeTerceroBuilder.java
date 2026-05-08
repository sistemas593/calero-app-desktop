package com.calero.lili.core.modTerceros.builder;

import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosTipoEntity;
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto;
import com.calero.lili.core.modTerceros.dto.GeTerceroGetOneDto;
import com.calero.lili.core.modTerceros.dto.GeTerceroRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GeTerceroBuilder {


    private final GeTercerosTipoBuilder geTercerosTipoBuilder;

    public GeTerceroEntity builderEntity(GeTerceroRequestDto model, Long idData) {
        return GeTerceroEntity.builder()
                .idTercero(UUID.randomUUID())
                .idData(idData)
                .tercero(model.getTercero())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.C.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .web(model.getWeb())
                .observaciones(model.getObservaciones())
                .tipoClienteProveedor(model.getTipoClienteProveedor())
                .ciudad(model.getCiudad())
                .direccion(model.getDireccion())
                .telefonos(model.getTelefonos())
                .contacto(model.getContacto())
                .email(model.getEmail())
                .placa(model.getTransportista().getPlaca())
                .provincia(Objects.nonNull(model.getCodigoProvincia()) ? builderProvincia(model.getCodigoProvincia()) : null)
                .canton(Objects.nonNull(model.getCodigoCanton()) ? builderCanton(model.getCodigoCanton()) : null)
                .parroquia(Objects.nonNull(model.getCodigoParroquia()) ? builderParroquia(model.getCodigoParroquia()) : null)
                .origenIngresos(model.getOrigenIngresos())
                .sexo(model.getSexo())
                .estadoCivil(model.getEstadoCivil())
                .datosAdicionales(model.getDatosAdicionales())
                .build();
    }



    public GeTerceroEntity builderUpdateEntity(GeTerceroRequestDto model, GeTerceroEntity item) {
        return GeTerceroEntity.builder()
                .idTercero(item.getIdTercero())
                .idData(item.getIdData())
                .tercero(model.getTercero())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.C.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .web(model.getWeb())
                .observaciones(model.getObservaciones())
                .tipoClienteProveedor(model.getTipoClienteProveedor())
                .ciudad(model.getCiudad())
                .direccion(model.getDireccion())
                .telefonos(model.getTelefonos())
                .contacto(model.getContacto())
                .email(model.getEmail())
                .placa(model.getTransportista().getPlaca())
                .provincia(Objects.nonNull(model.getCodigoProvincia()) ? builderProvincia(model.getCodigoProvincia()) : item.getProvincia())
                .canton(Objects.nonNull(model.getCodigoCanton()) ? builderCanton(model.getCodigoCanton()) : item.getCanton())
                .parroquia(Objects.nonNull(model.getCodigoParroquia()) ? builderParroquia(model.getCodigoParroquia()) : item.getParroquia())
                .origenIngresos(model.getOrigenIngresos())
                .sexo(model.getSexo())
                .estadoCivil(model.getEstadoCivil())
                .datosAdicionales(model.getDatosAdicionales())
                .build();
    }

    public GeTerceroGetOneDto builderResponse(GeTerceroEntity model) {
        return GeTerceroGetOneDto.builder()
                .idTercero(model.getIdTercero())
                .tercero(model.getTercero())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .web(model.getWeb())
                .observaciones(model.getObservaciones())
                .tipoClienteProveedor(model.getTipoClienteProveedor())
                .ciudad(model.getCiudad())
                .direccion(model.getDireccion())
                .telefonos(model.getTelefonos())
                .contacto(model.getContacto())
                .email(model.getEmail())
                .origenIngresos(model.getOrigenIngresos())
                .sexo(model.getSexo())
                .estadoCivil(model.getEstadoCivil())
                .build();
    }


    public GeTerceroGetListDto builderListResponse(GeTerceroEntity model) {
        return GeTerceroGetListDto.builder()
                .idTercero(model.getIdTercero())
                .tercero(model.getTercero())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .web(model.getWeb())
                .observaciones(model.getObservaciones())
                .tipoClienteProveedor(model.getTipoClienteProveedor())
                .ciudad(model.getCiudad())
                .direccion(model.getDireccion())
                .telefonos(model.getTelefonos())
                .contacto(model.getContacto())
                .email(model.getEmail())
                .placa(model.getPlaca())
                .tipoTerceros(builderResposeListTipoTercero(model.getGeTercerosTipoEntities()))
                .origenIngresos(model.getOrigenIngresos())
                .sexo(model.getSexo())
                .estadoCivil(model.getEstadoCivil())
                .build();
    }

    private List<GeTerceroGetListDto.TipoTercerosDto> builderResposeListTipoTercero(List<GeTercerosTipoEntity> geTercerosTipoEntities) {
        if (Objects.isNull(geTercerosTipoEntities)) return null;
        return geTercerosTipoEntities.stream()
                .map(this::builderResponseTipoTercero)
                .toList();
    }

    private GeTerceroGetListDto.TipoTercerosDto builderResponseTipoTercero(GeTercerosTipoEntity geTercerosTipoEntity) {
        return GeTerceroGetListDto.TipoTercerosDto.builder()
                .idTipoTercero(geTercerosTipoEntity.getIdTerceroTipo())
                .tipoTercero(TipoTercero.obtenerTipo(geTercerosTipoEntity.getTipo()))
                .build();
    }

    private ParroquiaEntity builderParroquia(String codigoParroquia) {
        return ParroquiaEntity.builder()
                .codigoParroquia(codigoParroquia)
                .build();
    }

    private CantonEntity builderCanton(String codigoCanton) {
        return CantonEntity.builder()
                .codigoCanton(codigoCanton)
                .build();
    }

    private ProvinciaEntity builderProvincia(String codigoProvincia) {
        return ProvinciaEntity.builder()
                .codigoProvincia(codigoProvincia)
                .build();
    }

}
