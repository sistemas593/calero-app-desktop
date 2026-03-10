package com.calero.lili.api.modVentasVendedores.builder;

import com.calero.lili.api.modVentasVendedores.VtVendedorEntity;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorCreationRequestDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorCreationResponseDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorReportDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VtVendedorBuilder {


    public VtVendedorEntity builderEntity(VtVendedorCreationRequestDto model, Long idData, Long idEmpresa){
        return VtVendedorEntity.builder()
                .idVendedor(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .firma(model.getVendedor())
                .vendedor(model.getVendedor())
                .bloqueado(model.getBloqueado())
                .firma(model.getFirma())
                .build();
    }

    public VtVendedorEntity builderUpdateEntity(VtVendedorCreationRequestDto model, VtVendedorEntity item){
        return VtVendedorEntity.builder()
                .idVendedor(item.getIdVendedor())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .firma(model.getVendedor())
                .vendedor(model.getVendedor())
                .bloqueado(model.getBloqueado())
                .firma(model.getFirma())
                .build();
    }

    public VtVendedorCreationResponseDto builderResponse(VtVendedorEntity model){
        return VtVendedorCreationResponseDto.builder()
                .idVendedor(model.getIdVendedor())
                .firma(model.getVendedor())
                .vendedor(model.getVendedor())
                .bloqueado(model.getBloqueado())
                .firma(model.getFirma())
                .build();
    }

    public VtVendedorReportDto builderListResponse(VtVendedorEntity model){
        return VtVendedorReportDto.builder()
                .idVendedor(model.getIdVendedor())
                .firma(model.getVendedor())
                .vendedor(model.getVendedor())
                .bloqueado(model.getBloqueado())
                .firma(model.getFirma())
                .build();
    }

}
