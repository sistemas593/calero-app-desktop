package com.calero.lili.api.modTercerosProvedoresParametros.builder;

import com.calero.lili.api.modTercerosProvedoresParametros.CpProveedoresParametrosEntity;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroCreationRequestDto;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroCreationResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CpProveedoresParametroBuilder {

    public CpProveedoresParametrosEntity builderEntity(CpProveedorParametroCreationRequestDto model,
                                                       Long idData, Long idEmpresa) {
        return CpProveedoresParametrosEntity.builder()
                .idParametro(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .formaPago(model.getFormaPago())
                .permiteCredito(model.getPermiteCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .diasCredito(model.getDiasCredito())
                .codret(model.getCodret())
                .ideiva(model.getIdeiva())
                .formaPagoSri(model.getFormaPagoSri())
                .observaciones(model.getObservaciones())
                .pnatural(model.getPnatural())
                .retiva(model.getRetiva())
                .retir(model.getRetir())
                .conconta(model.getConconta())
                .contesp(model.getContesp())
                .excretiva(model.getExcretiva())
                .tipoprov(model.getTipoprov())
                .relacionado(model.getRelacionado())
                .build();
    }


    public CpProveedoresParametrosEntity builderUpdateEntity(CpProveedorParametroCreationRequestDto model,
                                                             CpProveedoresParametrosEntity item) {
        return CpProveedoresParametrosEntity.builder()
                .idParametro(item.getIdParametro())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .formaPago(model.getFormaPago())
                .permiteCredito(model.getPermiteCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .diasCredito(model.getDiasCredito())
                .codret(model.getCodret())
                .ideiva(model.getIdeiva())
                .formaPagoSri(model.getFormaPagoSri())
                .observaciones(model.getObservaciones())
                .pnatural(model.getPnatural())
                .retiva(model.getRetiva())
                .retir(model.getRetir())
                .conconta(model.getConconta())
                .contesp(model.getContesp())
                .excretiva(model.getExcretiva())
                .tipoprov(model.getTipoprov())
                .relacionado(model.getRelacionado())
                .build();
    }

    public CpProveedorParametroCreationResponseDto builderResponse(CpProveedoresParametrosEntity model) {
        return CpProveedorParametroCreationResponseDto.builder()
                .idParametro(model.getIdParametro())
                .permiteCredito(model.getPermiteCredito())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .valorCredito(model.getValorCredito())
                .cuotas(model.getCuotas())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .diasCredito(model.getDiasCredito())
                .codret(model.getCodret())
                .ideiva(model.getIdeiva())
                .formaPagoSri(model.getFormaPagoSri())
                .observaciones(model.getObservaciones())
                .pnatural(model.getPnatural())
                .retiva(model.getRetiva())
                .retir(model.getRetir())
                .conconta(model.getConconta())
                .contesp(model.getContesp())
                .excretiva(model.getExcretiva())
                .tipoprov(model.getTipoprov())
                .relacionado(model.getRelacionado())
                .build();
    }
}
