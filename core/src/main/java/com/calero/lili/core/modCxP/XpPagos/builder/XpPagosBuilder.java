package com.calero.lili.core.modCxP.XpPagos.builder;

import com.calero.lili.core.enums.TipoDocumentoPago;
import com.calero.lili.core.modCxP.XpFacturas.XpFacturasEntity;
import com.calero.lili.core.modCxP.XpPagos.XpPagosEntity;
import com.calero.lili.core.modCxP.XpPagos.dto.RequestPagoXpDto;
import com.calero.lili.core.modCxP.XpPagos.dto.ResponsePagoXpDto;
import com.calero.lili.core.modCxP.XpPagos.dto.XpPagosRequestDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class XpPagosBuilder {


    public List<XpPagosEntity> builderListEntity(RequestPagoXpDto model, UUID idPagoGrupo,
                                                 Long idData, Long idEmpresa) {
        return model.getPagos()
                .stream()
                .map(item -> builderEntity(item, model.getIdFactura(), idPagoGrupo, idData, idEmpresa))
                .toList();
    }

    private XpPagosEntity builderEntity(XpPagosRequestDto model, UUID idFactura,
                                        UUID idPagoGrupo, Long idData, Long idEmpresa) {
        return XpPagosEntity.builder()
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

    public List<ResponsePagoXpDto> builderListResponse(List<XpPagosEntity> list) {
        return list.stream()
                .map(this::builderResponse)
                .toList();
    }

    private ResponsePagoXpDto builderResponse(XpPagosEntity model) {
        return ResponsePagoXpDto.builder()
                .idPago(model.getIdPago())
                .idPagoGrupo(model.getIdPagoGrupo())
                .sucursal(model.getSucursal())
                .tipoDocumento(TipoDocumentoPago.valueOf(model.getTipoDocumento()))
                .fechaPago(DateUtils.toString(model.getFechaPago()))
                .concepto(model.getConcepto())
                .valor(model.getValor())
                .anulada(model.getAnulada())
                .build();
    }


    private XpFacturasEntity builderFactura(UUID idFactura) {
        return XpFacturasEntity.builder()
                .idFactura(idFactura)
                .build();
    }

}
