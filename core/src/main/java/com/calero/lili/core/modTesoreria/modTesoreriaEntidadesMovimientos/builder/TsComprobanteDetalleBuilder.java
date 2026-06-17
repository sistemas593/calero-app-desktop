package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.builder;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.BcEntidadesRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.TsComprobanteDetallesEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteResponseDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TsComprobanteDetalleBuilder {


    private final BcEntidadesRepository bcEntidadesRepository;

    public List<TsComprobanteDetallesEntity> builderList(List<TsComprobanteCreationRequestDto.DetalleComprobanteDto> detalles,
                                                         Long idData, Long idEmpresa) {

        List<UUID> listUUIDsEntidades = detalles.stream()
                .map(TsComprobanteCreationRequestDto.DetalleComprobanteDto::getIdEntidad)
                .distinct()
                .toList();

        Map<UUID, TsEntidadEntity> mapEntidades = bcEntidadesRepository
                .findAllIdsEntidad(idData, idEmpresa, listUUIDsEntidades)
                .stream()
                .collect(Collectors.toMap(TsEntidadEntity::getIdEntidad, Function.identity()));


        return detalles.stream().map(x -> builderDetalle(x, idData, idEmpresa, mapEntidades)).toList();
    }

    private TsComprobanteDetallesEntity builderDetalle(TsComprobanteCreationRequestDto.DetalleComprobanteDto model,
                                                       Long idData, Long idEmpresa, Map<UUID, TsEntidadEntity> mapEntidades) {

        TsEntidadEntity entidad = mapEntidades.get(model.getIdEntidad());
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("La entidad con id {0} no existe", model.getIdEntidad()));
        }

        return TsComprobanteDetallesEntity.builder()
                .idComprobanteDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .tipoComprobante(model.getTipoComprobante())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(Objects.nonNull(model.getFechaDocumento()) ? DateUtils.toLocalDate(model.getFechaDocumento()) : null)
                .tipoComprobante(model.getTipoComprobante())
                .descripcion(model.getDescripcion())
                .valor(model.getValor())
                .entidad(entidad)
                .build();
    }


    public List<TsComprobanteResponseDto.DetalleComprobanteResponseDto> builderResponseList(List<TsComprobanteDetallesEntity> list) {
        return list
                .stream()
                .map(this::builderResponseDetalle)
                .toList();
    }

    private TsComprobanteResponseDto.DetalleComprobanteResponseDto builderResponseDetalle(TsComprobanteDetallesEntity model) {
        return TsComprobanteResponseDto.DetalleComprobanteResponseDto.builder()
                .idComprobanteDetalle(model.getIdComprobanteDetalle())
                .tipoComprobante(model.getTipoComprobante())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(Objects.nonNull(model.getFechaDocumento()) ? DateUtils.toString(model.getFechaDocumento()) : null)
                .tipoComprobante(model.getTipoComprobante())
                .descripcion(model.getDescripcion())
                .valor(model.getValor())
                .idEntidad(Objects.nonNull(model.getEntidad()) ? model.getEntidad().getIdEntidad() : null)
                .entidad(Objects.nonNull(model.getEntidad()) ? model.getEntidad().getEntidad() : null)
                .build();

    }

}
