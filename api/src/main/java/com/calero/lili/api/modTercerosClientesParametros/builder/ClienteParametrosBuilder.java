package com.calero.lili.api.modTercerosClientesParametros.builder;

import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTercerosClientesParametros.VtClienteParametroEntity;
import com.calero.lili.api.modTercerosClientesParametros.dto.VtClienteParametroCreationRequestDto;
import com.calero.lili.api.modTercerosClientesParametros.dto.VtClienteParametroCreationResponseDto;
import com.calero.lili.api.modTercerosClientesParametros.dto.VtClienteParametroReportDto;
import com.calero.lili.api.modVentasVendedores.VtVendedorEntity;
import com.calero.lili.api.modVentasZonas.VtZonaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClienteParametrosBuilder {

    public VtClienteParametroEntity builderEntity(VtClienteParametroCreationRequestDto model, Long idData, Long idEmpresa) {
        return VtClienteParametroEntity.builder()
                .idParametro(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .relacionado(model.getRelacionado())
                .permitirCredito(model.getPermitirCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .formaPago(model.getFormaPago())
                .codigoPrecio(model.getCodigoPrecio())
                .observaciones(model.getObservaciones())
                .vtClienteEntity(builderCliente(model.getIdTercero()))
                .vtVendedorEntity(builderVendedor(model.getIdVendedor()))
                .vtZonaEntity(builderZona(model.getIdZona()))
                .build();
    }

    public VtClienteParametroEntity builderUpdateEntity(VtClienteParametroCreationRequestDto model, VtClienteParametroEntity item) {
        return VtClienteParametroEntity.builder()
                .idParametro(item.getIdParametro())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .relacionado(model.getRelacionado())
                .permitirCredito(model.getPermitirCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .formaPago(model.getFormaPago())
                .codigoPrecio(model.getCodigoPrecio())
                .observaciones(model.getObservaciones())
                .vtClienteEntity(builderCliente(model.getIdTercero()))
                .vtVendedorEntity(builderVendedor(model.getIdVendedor()))
                .vtZonaEntity(builderZona(model.getIdZona()))
                .build();
    }

    public VtClienteParametroCreationResponseDto builderResponse(VtClienteParametroEntity model) {
        return VtClienteParametroCreationResponseDto.builder()
                .idParametro(model.getIdParametro())
                .relacionado(model.getRelacionado())
                .permitirCredito(model.getPermitirCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .formaPago(model.getFormaPago())
                .codigoPrecio(model.getCodigoPrecio())
                .observaciones(model.getObservaciones())
                .build();
    }

    public VtClienteParametroReportDto builderListResponse(VtClienteParametroEntity model) {
        return VtClienteParametroReportDto.builder()
                .idParametro(model.getIdParametro())
                .relacionado(model.getRelacionado())
                .permitirCredito(model.getPermitirCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .formaPago(model.getFormaPago())
                .codigoPrecio(model.getCodigoPrecio())
                .observaciones(model.getObservaciones())
                .build();
    }


    private VtZonaEntity builderZona(UUID idZona) {
        return VtZonaEntity.builder()
                .idZona(idZona)
                .build();
    }

    private VtVendedorEntity builderVendedor(UUID idVendedor) {
        return VtVendedorEntity.builder()
                .idVendedor(idVendedor)
                .build();
    }

    private GeTerceroEntity builderCliente(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

}
