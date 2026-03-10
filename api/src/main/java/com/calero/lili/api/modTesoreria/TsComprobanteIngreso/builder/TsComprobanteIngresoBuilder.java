package com.calero.lili.api.modTesoreria.TsComprobanteIngreso.builder;

import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.RequestCreationComprobanteIngresoDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.ResponseComprobanteIngresoDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.TsBancosMovimentosEntity;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.builder.TsBancosMovimentosBuilder;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationRequestDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TsComprobanteIngresoBuilder {


    private final TsBancosMovimentosBuilder tsBancosMovimentosBuilder;


    public CnAsientosEntity builderIngresoEntity(RequestCreationComprobanteIngresoDto model,
                                                 Long idData, Long idEmpresa, UUID idAsiento) {
        return CnAsientosEntity.builder()
                .idAsiento(idAsiento)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroComprobante())
                .fechaAsiento(DateUtils.toLocalDate(model.getFechaComprobante()))
                .concepto(model.getConcepto())
                .valor(model.getValor())
                .codigoSerie(model.getCodigoSerie())
                .observaciones(model.getObservaciones())
                .nombre(model.getNombre())
                .build();
    }


    public ResponseComprobanteIngresoDto builderResponse(CnAsientosEntity model) {
        return ResponseComprobanteIngresoDto.builder()
                .sucursal(model.getSucursal())
                .codigoSerie(model.getCodigoSerie())
                .numeroComprobante(model.getNumeroAsiento())
                .numeroIdentificacion(model.getTercero().getNumeroIdentificacion())
                .fechaComprobante(DateUtils.toString(model.getFechaAsiento()))
                .valor(model.getValor())
                .concepto(model.getConcepto())
                .nombre(model.getNombre())
                .observaciones(model.getObservaciones())
                .tercero(builderResponseTercero(model.getTercero()))
                .build();
    }

    private ResponseComprobanteIngresoDto.TerceroDto builderResponseTercero(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return ResponseComprobanteIngresoDto.TerceroDto.builder()
                .idTercero(cliente.getIdTercero())
                .cliente(cliente.getTercero())
                .build();
    }

    private List<TsBancosMovimentosEntity> builderListBancosMovimientos(List<BcBancoMovimientoCreationRequestDto> list,
                                                                        Long idData, Long idEmpresa) {
        return list.stream()
                .map(item -> tsBancosMovimentosBuilder
                        .builderEntity(item, idData, idEmpresa))
                .toList();
    }

}
