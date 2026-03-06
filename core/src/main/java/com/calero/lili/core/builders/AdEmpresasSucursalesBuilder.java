package com.calero.lili.core.builders;

import com.calero.lili.core.entities.AdEmpresaEntity;
import com.calero.lili.core.entities.AdEmpresasSucursalesEntity;
import com.calero.lili.core.dtos.AdEmpresaSucursalCreationRequestDto;
import com.calero.lili.core.dtos.AdEmpresaSucursalGetListDto;
import com.calero.lili.core.dtos.AdEmpresaSucursalGetOneDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdEmpresasSucursalesBuilder {

    public AdEmpresasSucursalesEntity builderEmpresaSucursal(AdEmpresaEntity model) {
        return AdEmpresasSucursalesEntity.builder()
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .idSucursal(UUID.randomUUID())
                .sucursal("001")
                .nombreSucursal(model.getRazonSocial())
                .build();
    }

    public AdEmpresasSucursalesEntity builderCreateEntity(AdEmpresaSucursalCreationRequestDto model, Long idData, Long idEmpresa) {
        return AdEmpresasSucursalesEntity.builder()
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idSucursal(UUID.randomUUID())
                .sucursal(model.getSucursal())
                .nombreSucursal(model.getNombreSucursal())
                .build();
    }

    public AdEmpresasSucursalesEntity builderUpdateEntity(AdEmpresaSucursalCreationRequestDto model,
                                                          AdEmpresasSucursalesEntity item) {
        return AdEmpresasSucursalesEntity.builder()
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idSucursal(item.getIdSucursal())
                .sucursal(model.getSucursal())
                .nombreSucursal(model.getNombreSucursal())
                .build();
    }

    public AdEmpresaSucursalGetOneDto builderDto(AdEmpresasSucursalesEntity model) {
        return AdEmpresaSucursalGetOneDto.builder()
                .idSucursal(model.getIdSucursal())
                .sucursal(model.getSucursal())
                .nombreSucursal(model.getNombreSucursal())
                .build();
    }

    public AdEmpresaSucursalGetListDto builderListDto(AdEmpresasSucursalesEntity model) {
        return AdEmpresaSucursalGetListDto.builder()
                .idSucursal(model.getIdSucursal())
                .sucursal(model.getSucursal())
                .nombreSucursal(model.getNombreSucursal())
                .build();
    }
}
