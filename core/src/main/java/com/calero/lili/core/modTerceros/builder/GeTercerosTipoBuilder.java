package com.calero.lili.core.modTerceros.builder;

import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.modComprasProveedoresGrupos.CpProveedoresGruposEntity;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.ResponseTrabajadorDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosTipoEntity;
import com.calero.lili.core.modTerceros.dto.GeTerceroGetOneDto;
import com.calero.lili.core.modVentasCientesGrupos.VtClienteGrupoEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class GeTercerosTipoBuilder {


    public GeTercerosTipoEntity builderClienteEntity(GeTerceroEntity entidad) {
        return GeTercerosTipoEntity.builder()
                .idTerceroTipo(UUID.randomUUID())
                .tipo(TipoTercero.CLIENTE.getTipo())
                .tercero(entidad)
                .build();
    }

    public GeTercerosTipoEntity builderProveedorEntity(GeTerceroEntity entidad) {
        return GeTercerosTipoEntity.builder()
                .idTerceroTipo(UUID.randomUUID())
                .tipo(TipoTercero.PROVEEDOR.getTipo())
                .tercero(entidad)
                .build();
    }


    public GeTercerosTipoEntity builderTransportistaEntity(GeTerceroEntity entidad) {
        return GeTercerosTipoEntity.builder()
                .idTerceroTipo(UUID.randomUUID())
                .tipo(TipoTercero.TRANSPORTISTA.getTipo())
                .tercero(entidad)
                .build();
    }


    public GeTercerosTipoEntity builderTrabajadorEntity(GeTerceroEntity entidad) {
        return GeTercerosTipoEntity.builder()
                .idTerceroTipo(UUID.randomUUID())
                .tipo(TipoTercero.TRABAJADOR.getTipo())
                .tercero(entidad)
                .build();
    }

    public GeTerceroGetOneDto.TipoTercerosClienteDto builderResponseCliente(VtClienteGrupoEntity model) {
        return GeTerceroGetOneDto.TipoTercerosClienteDto.builder()
                .esCliente(Boolean.TRUE)
                .idGrupoCliente(Objects.nonNull(model) ? model.getIdGrupo() : null)
                .grupo(Objects.nonNull(model) ? model.getGrupo() : null)
                .build();
    }

    public GeTerceroGetOneDto.TipoTercerosTransportistaDto builderResponseTransportista(GeTerceroEntity entity) {
        return GeTerceroGetOneDto.TipoTercerosTransportistaDto.builder()
                .esTransportista(Boolean.TRUE)
                .placa(entity.getPlaca())
                .build();
    }

    public GeTerceroGetOneDto.TipoTercerosProveedorDto builderResponseProveedor(CpProveedoresGruposEntity model) {
        return GeTerceroGetOneDto.TipoTercerosProveedorDto.builder()
                .esProveedor(Boolean.TRUE)
                .idGrupoProveedor(Objects.nonNull(model) ? model.getIdGrupo() : null)
                .grupo(Objects.nonNull(model) ? model.getGrupo() : null)
                .build();
    }

    public GeTerceroGetOneDto.TipoTercerosTrabajadorDto builderResponseTrabajador(ResponseTrabajadorDto response) {
        return GeTerceroGetOneDto.TipoTercerosTrabajadorDto.builder()
                .esTrabajador(Boolean.TRUE)
                .infoTrabajador(response)
                .build();
    }

    public GeTerceroGetOneDto.TipoTercerosClienteDto builderResponseDefaultCliente() {
        return GeTerceroGetOneDto.TipoTercerosClienteDto.builder()
                .esCliente(Boolean.FALSE)
                .grupo("")
                .build();
    }

    public GeTerceroGetOneDto.TipoTercerosProveedorDto builderResponseDefaultProveedor() {
        return GeTerceroGetOneDto.TipoTercerosProveedorDto.builder()
                .esProveedor(Boolean.FALSE)
                .grupo("")
                .build();
    }


    public GeTerceroGetOneDto.TipoTercerosTransportistaDto builderResponseDefaultTransportista() {
        return GeTerceroGetOneDto.TipoTercerosTransportistaDto.builder()
                .esTransportista(Boolean.FALSE)
                .placa("")
                .build();
    }


    public GeTerceroGetOneDto.TipoTercerosTrabajadorDto builderResponseDefaultTrabajador() {
        return GeTerceroGetOneDto.TipoTercerosTrabajadorDto.builder()
                .esTrabajador(Boolean.FALSE)
                .infoTrabajador(null)
                .build();
    }

    public GeTercerosTipoEntity builderTipoTercero(GeTerceroEntity model, TipoTercero tipoTercero) {
        return GeTercerosTipoEntity.builder()
                .idTerceroTipo(UUID.randomUUID())
                .tipo(tipoTercero.getTipo())
                .tercero(model)
                .build();
    }


}
