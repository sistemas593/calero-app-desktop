package com.calero.lili.core.modContabilidad.modSecuenciales.builder;

import com.calero.lili.core.modContabilidad.modSecuenciales.CnSecuenciasEntity;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasRequestDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CnSecuenciasBuilder {


    public CnSecuenciasEntity builderEntity(Long idData, Long idEmpresa, CnSecuenciasRequestDto model) {
        return CnSecuenciasEntity.builder()
                .idSecuencia(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .anio(model.getAnio())
                .mes(model.getMes())
                .ultimoNumero(model.getUltimoNumero())
                .build();
    }


    public CnSecuenciasEntity builderUpdateEntity(CnSecuenciasEntity item, CnSecuenciasRequestDto model) {
        return CnSecuenciasEntity.builder()
                .idSecuencia(item.getIdSecuencia())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .anio(model.getAnio())
                .mes(model.getMes())
                .ultimoNumero(model.getUltimoNumero())
                .build();
    }


    public CnSecuenciasResponseDto builderResponse(CnSecuenciasEntity model) {
        return CnSecuenciasResponseDto.builder()
                .idSecuencia(model.getIdSecuencia())
                .sucursal(model.getSucursal())
                .anio(model.getAnio())
                .mes(model.getMes())
                .ultimoNumero(model.getUltimoNumero())
                .build();
    }

}
