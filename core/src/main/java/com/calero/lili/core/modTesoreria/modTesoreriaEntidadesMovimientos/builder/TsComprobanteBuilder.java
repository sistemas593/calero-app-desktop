package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.builder;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.TsComprobantesEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteResponseDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TsComprobanteBuilder {


    private final TsComprobanteDetalleBuilder tsComprobanteDetalleBuilder;

    public TsComprobantesEntity builderEntity(TsComprobanteCreationRequestDto model,
                                              Long idData, Long idEmpresa) {

        return TsComprobantesEntity.builder()
                .idComprobante(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .tieneComprobante(model.getTieneComprobante())
                .numeroComprobante(model.getNumeroComprobante())
                .fecha(Objects.nonNull(model.getFecha()) ? DateUtils.toLocalDate(model.getFecha()) : null)
                .total(model.getTotal())
                .concepto(model.getConcepto())
                .observaciones(model.getObservaciones())
                .tipoComprobante(model.getTipoComprobante())
                .detalle(tsComprobanteDetalleBuilder.builderList(model.getDetalles(), idData, idEmpresa))
                .build();
    }

    public TsComprobantesEntity builderUpdateEntity(TsComprobanteCreationRequestDto model,
                                                    TsComprobantesEntity item) {

        return TsComprobantesEntity.builder()
                .idComprobante(item.getIdComprobante())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .tieneComprobante(model.getTieneComprobante())
                .numeroComprobante(model.getNumeroComprobante())
                .fecha(Objects.nonNull(model.getFecha()) ? DateUtils.toLocalDate(model.getFecha()) : null)
                .total(model.getTotal())
                .concepto(model.getConcepto())
                .observaciones(model.getObservaciones())
                .tipoComprobante(model.getTipoComprobante())
                .detalle(tsComprobanteDetalleBuilder.builderList(model.getDetalles(), item.getIdData(), item.getIdEmpresa()))
                .build();
    }

    public TsComprobanteResponseDto builderResponse(TsComprobantesEntity model) {
        return TsComprobanteResponseDto.builder()
                .idComprobante(model.getIdComprobante())
                .sucursal(model.getSucursal())
                .tieneComprobante(model.getTieneComprobante())
                .numeroComprobante(model.getNumeroComprobante())
                .fecha(Objects.nonNull(model.getFecha()) ? DateUtils.toString(model.getFecha()) : null)
                .total(model.getTotal())
                .concepto(model.getConcepto())
                .observaciones(model.getObservaciones())
                .tipoComprobante(model.getTipoComprobante())
                .build();
    }


    private GeTerceroEntity builderProveedor(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

    private GeTerceroEntity builderCliente(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

    private TsEntidadEntity builderEntidad(UUID idEntidad) {
        return TsEntidadEntity.builder()
                .idEntidad(idEntidad)
                .build();
    }

}
