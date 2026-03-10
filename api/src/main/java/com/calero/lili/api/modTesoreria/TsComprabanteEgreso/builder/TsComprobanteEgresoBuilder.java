package com.calero.lili.api.modTesoreria.TsComprabanteEgreso.builder;

import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.RequestComprobantesEgresoDto;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.ResponseComprobantesEgresoDto;
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
public class TsComprobanteEgresoBuilder {


    private final TsBancosMovimentosBuilder tsBancosMovimentosBuilder;


    public CnAsientosEntity builderEgresoEntity(RequestComprobantesEgresoDto model,
                                                Long idData, Long idEmpresa, UUID idAsiento) {
        return CnAsientosEntity.builder()
                .idAsiento(idAsiento)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroComprobanteEgreso())
                .fechaAsiento(DateUtils.toLocalDate(model.getFechaComprobante()))
                .concepto(model.getConcepto())
                .valor(model.getValor())
                .codigoSerie(model.getCodigoSerie())
                .observaciones(model.getObservaciones())
                .nombre(model.getNombre())
                .build();
    }


    public ResponseComprobantesEgresoDto builderResponse(CnAsientosEntity model) {
        return ResponseComprobantesEgresoDto.builder()
                .sucursal(model.getSucursal())
                .codigoSerie(model.getCodigoSerie())
                .numeroComprobanteEgreso(model.getNumeroAsiento())
                .numeroIdentificacion(model.getTercero().getNumeroIdentificacion())
                .fechaComprobante(DateUtils.toString(model.getFechaAsiento()))
                .valor(model.getValor())
                .nombre(model.getNombre())
                .observaciones(model.getObservaciones())
                .concepto(model.getConcepto())
                .tercero(builderResponseTercero(model.getTercero()))
                .build();
    }

    private List<TsBancosMovimentosEntity> builderListBancosMovimientos(List<BcBancoMovimientoCreationRequestDto> list,
                                                                        Long idData, Long idEmpresa) {
        return list.stream()
                .map(item -> tsBancosMovimentosBuilder
                        .builderEntity(item, idData, idEmpresa))
                .toList();
    }


    private ResponseComprobantesEgresoDto.TerceroDto builderResponseTercero(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return ResponseComprobantesEgresoDto.TerceroDto.builder()
                .idTercero(cliente.getIdTercero())
                .cliente(cliente.getTercero())
                .build();
    }


}
