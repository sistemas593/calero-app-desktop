package com.calero.lili.core.modCxC.XcRetenciones.builder;

import com.calero.lili.core.modCxC.XcFacturas.XcFacturasEntity;
import com.calero.lili.core.modCxC.XcPagos.XcPagosEntity;
import com.calero.lili.core.modCxC.XcRetenciones.dto.RequestRetencionesDto;
import com.calero.lili.core.modCxC.XcRetenciones.dto.ResponseXcRetencionesDto;
import com.calero.lili.core.modCxC.XcRetenciones.dto.XcRetencionesDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class XcRetencionesBuilder {


    public List<XcPagosEntity> builderListEntity(RequestRetencionesDto model, UUID idPagoGrupo,
                                                 Long idData, Long idEmpresa) {
        return model.getPagos()
                .stream()
                .map(item -> builderEntity(item, model.getIdFactura(), idPagoGrupo, idData, idEmpresa))
                .toList();
    }

    private XcPagosEntity builderEntity(XcRetencionesDto model, UUID idFactura,
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

    public List<ResponseXcRetencionesDto> builderListResponse(List<XcPagosEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private ResponseXcRetencionesDto builderResponse(XcPagosEntity model) {
        return ResponseXcRetencionesDto.builder()
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
