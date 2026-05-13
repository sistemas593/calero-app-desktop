package com.calero.lili.core.modTerceros.builder;

import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoTercero;
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
                .origenIngresos(model.getOrigenIngresos())
                .sexo(model.getSexo())
                .estadoCivil(model.getEstadoCivil())
                //.datosAdicionales(model.getDatosAdicionales())
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
                .origenIngresos(model.getOrigenIngresos())
                .sexo(model.getSexo())
                .estadoCivil(model.getEstadoCivil())
                //.datosAdicionales(model.getDatosAdicionales())
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
                .codigoParroquia(Objects.nonNull(model.getParroquia()) ? model.getParroquia().getCodigoParroquia() : null)
                .parroquia(Objects.nonNull(model.getParroquia()) ? model.getParroquia().getParroquia() : null)
                .codigoCanton(Objects.nonNull(model.getCanton()) ? model.getCanton().getCodigoCanton() : null)
                .canton(Objects.nonNull(model.getCanton()) ? model.getCanton().getCanton() : null)
                .codigoProvincia(Objects.nonNull(model.getProvincia()) ? model.getProvincia().getCodigoProvincia() : null)
                .provincia(Objects.nonNull(model.getProvincia()) ? model.getProvincia().getProvincia() : null)
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
                .codigoParroquia(Objects.nonNull(model.getParroquia()) ? model.getParroquia().getCodigoParroquia() : null)
                .parroquia(Objects.nonNull(model.getParroquia()) ? model.getParroquia().getParroquia() : null)
                .codigoCanton(Objects.nonNull(model.getCanton()) ? model.getCanton().getCodigoCanton() : null)
                .canton(Objects.nonNull(model.getCanton()) ? model.getCanton().getCanton() : null)
                .codigoProvincia(Objects.nonNull(model.getProvincia()) ? model.getProvincia().getCodigoProvincia() : null)
                .provincia(Objects.nonNull(model.getProvincia()) ? model.getProvincia().getProvincia() : null)
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
}
