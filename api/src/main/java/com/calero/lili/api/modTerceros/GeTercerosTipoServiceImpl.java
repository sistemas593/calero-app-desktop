package com.calero.lili.api.modTerceros;

import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.api.modRRHH.modRRHHTrabajadores.TrabajadorServiceImpl;
import com.calero.lili.api.modTerceros.builder.GeTercerosTipoBuilder;
import com.calero.lili.api.modTerceros.dto.GeTerceroGetOneDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GeTercerosTipoServiceImpl {


    private final GeTercerosTipoRepository geTercerosTipoRepository;
    private final GeTercerosTipoBuilder geTercerosTipoBuilder;
    private final GeTercerosGruposClientesServiceImpl geTercerosGruposClientesService;
    private final GeTercerosGruposProveedoresServiceImpl geTercerosGruposProveedoresService;
    private final TrabajadorServiceImpl trabajadorService;

    public void save(GeTerceroRequestDto model,
                     GeTerceroEntity entidad, Long idData, Long idEmpresa) {

        if (model.getCliente().getEsCliente()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderClienteEntity(entidad));
            geTercerosGruposClientesService.save(model.getCliente().getIdGrupoCliente(), entidad, idData, idEmpresa);
        }

        if (model.getProveedor().getEsProveedor()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderProveedorEntity(entidad));
            geTercerosGruposProveedoresService.save(model.getProveedor().getIdGrupoProveedor(), entidad, idData, idEmpresa);
        }

        if (model.getTransportista().getEsTransportista()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderTransportistaEntity(entidad));
        }

        if (model.getTrabajador().getEsTrabajador()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderTrabajadorEntity(entidad));
            trabajadorService.create(model.getTrabajador().getInfoTrabajador(), entidad);
        }

    }

    public void update(GeTerceroRequestDto model, GeTerceroEntity entidad, Long idEmpresa) {

        deleteUpdate(entidad.getIdTercero(), TipoTercero.CLIENTE.getTipo());
        deleteUpdate(entidad.getIdTercero(), TipoTercero.PROVEEDOR.getTipo());
        deleteUpdate(entidad.getIdTercero(), TipoTercero.TRANSPORTISTA.getTipo());
        deleteUpdate(entidad.getIdTercero(), TipoTercero.TRABAJADOR.getTipo());

        if (model.getCliente().getEsCliente()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderClienteEntity(entidad));
            geTercerosGruposClientesService.update(model.getCliente().getIdGrupoCliente(),
                    entidad, entidad.getIdData(), idEmpresa);
        }

        if (model.getProveedor().getEsProveedor()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderProveedorEntity(entidad));
            geTercerosGruposProveedoresService.update(model.getProveedor().getIdGrupoProveedor(),
                    entidad, entidad.getIdData(), idEmpresa);
        }

        if (model.getTransportista().getEsTransportista()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderTransportistaEntity(entidad));
        }

        if (model.getTrabajador().getEsTrabajador()) {
            geTercerosTipoRepository.save(geTercerosTipoBuilder.builderTrabajadorEntity(entidad));
            trabajadorService.update(model.getTrabajador().getInfoTrabajador(), entidad);
        }
    }

    public void delete(GeTerceroEntity entidad) {
        entidad.getGeTercerosTipoEntities().forEach(item -> {
            if (item.getTipo().equals(TipoTercero.TRABAJADOR.getTipo())) {
                trabajadorService.delete(entidad.getIdTercero());
            }
        });

        geTercerosTipoRepository.deleteByIdTercero(entidad.getIdTercero());
    }

    public void deleteUpdate(UUID idTercero, Integer tipo) {
        geTercerosTipoRepository.deleteByIdTerceroAndTipo(idTercero, tipo);
    }


    public void getResponseTercerosTipos(GeTerceroEntity entity, GeTerceroGetOneDto response, Long idEmpresa) {

        response.setProveedor(geTercerosTipoBuilder.builderResponseDefaultProveedor());
        response.setCliente(geTercerosTipoBuilder.builderResponseDefaultCliente());
        response.setTransportista(geTercerosTipoBuilder.builderResponseDefaultTransportista());
        response.setTrabajador(geTercerosTipoBuilder.builderResponseDefaultTrabajador());

        entity.getGeTercerosTipoEntities().forEach(item -> {

            if (item.getTipo().equals(TipoTercero.CLIENTE.getTipo())) {
                response.setCliente(geTercerosTipoBuilder.builderResponseCliente(geTercerosGruposClientesService.getGrupo(entity, idEmpresa)));
            }

            if (item.getTipo().equals(TipoTercero.PROVEEDOR.getTipo())) {
                response.setProveedor(geTercerosTipoBuilder.builderResponseProveedor(geTercerosGruposProveedoresService.getGrupo(entity, idEmpresa)));
            }

            if (item.getTipo().equals(TipoTercero.TRANSPORTISTA.getTipo())) {
                response.setTransportista(geTercerosTipoBuilder.builderResponseTransportista(entity));
            }

            if (item.getTipo().equals(TipoTercero.TRABAJADOR.getTipo())) {
                response.setTrabajador(geTercerosTipoBuilder.builderResponseTrabajador(trabajadorService
                        .findByIdTercero(entity.getIdTercero())));
            }

        });

    }

}
