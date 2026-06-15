package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.builder;

import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.TsComprobanteDetallesEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteCreationRequestDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TsComprobanteDetalleBuilder {

    public List<TsComprobanteDetallesEntity> builderList(List<TsComprobanteCreationRequestDto.Detalle> detalles,
                                                         Long idData, Long idEmpresa) {

        return detalles.stream().map(x -> builderDetalle(x, idData, idEmpresa)).toList();
    }

    private TsComprobanteDetallesEntity builderDetalle(TsComprobanteCreationRequestDto.Detalle model,
                                                       Long idData, Long idEmpresa) {
        return TsComprobanteDetallesEntity.builder()
                .idComprobanteDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .tipoComprobante(model.getTipoComprobante())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(Objects.nonNull(model.getFechaDocumento()) ? DateUtils.toLocalDate(model.getDescripcion()) : null)
                .tipoComprobante(model.getTipoComprobante())
                .descripcion(model.getDescripcion())
                .valor(model.getValor())
                .build();
    }


    public List<TsComprobanteCreationRequestDto.Detalle> builderResponseList(List<TsComprobanteDetallesEntity> list) {
        return list
                .stream()
                .map(this::builderResponseDetalle)
                .toList();
    }

    private TsComprobanteCreationRequestDto.Detalle builderResponseDetalle(TsComprobanteDetallesEntity model) {
        return TsComprobanteCreationRequestDto.Detalle.builder()
            /*    .idComprobanteDetalle()
                .tipoComprobante(model.getT())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(Objects.nonNull(model.getFechaDocumento()) ? DateUtils.toLocalDate(model.getDescripcion()) : null)
                .tipoComprobante(model.getTipoComprobante())
                .descripcion(model.getDescripcion())
                .valor(model.getValor())*/
                .build();

    }

}
