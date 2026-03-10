package com.calero.lili.api.modCxC.XcPagos.builder;

import com.calero.lili.api.modCxC.XcFacturas.XcFacturasEntity;
import com.calero.lili.api.modCxC.XcPagos.XcPagosEntity;
import com.calero.lili.api.modCxC.XcPagos.dto.RequestPagoDto;
import com.calero.lili.api.modCxC.XcPagos.dto.ResponseXcPagosDto;
import com.calero.lili.api.modCxC.XcPagos.dto.XcPagosDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class XcPagosBuilder {


    public List<XcPagosEntity> builderListEntity(RequestPagoDto model, UUID idPagoGrupo,
                                                 Long idData, Long idEmpresa) {
        return model.getPagos()
                .stream()
                .map(item -> builderEntity(item, model.getIdFactura(), idPagoGrupo, idData, idEmpresa))
                .toList();
    }

    private XcPagosEntity builderEntity(XcPagosDto model, UUID idFactura,
                                        UUID idPagoGrupo, Long idData, Long idEmpresa) {
        return XcPagosEntity.builder()
                .idPago(UUID.randomUUID())
                .idPagoGrupo(idPagoGrupo)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .tipoDocumento(model.getTipoDocumento().name())
                .fechaPago(DateUtils.toLocalDate(model.getFechaPago()))
                .concepto(model.getConcepto())
                .valor(model.getValor())
                .anulada(model.getAnulada())
                .factura(builderFactura(idFactura))
                .build();
    }

    public List<ResponseXcPagosDto> builderListResponse(List<XcPagosEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private ResponseXcPagosDto builderResponse(XcPagosEntity model) {
        return ResponseXcPagosDto.builder()
                .idPago(model.getIdPago())
                .idPagoGrupo(model.getIdPagoGrupo())
                .sucursal(model.getSucursal())
                .tipoDocumento(model.getTipoDocumento())
                .fechaPago(DateUtils.toString(model.getFechaPago()))
                .concepto(model.getConcepto())
                .valor(model.getValor())
                .anulada(model.getAnulada())
                .build();
    }


    private XcFacturasEntity builderFactura(UUID idFactura) {
        return XcFacturasEntity.builder()
                .idFactura(idFactura)
                .build();
    }

}
