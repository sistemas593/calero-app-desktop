package com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.builder;

import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.TsComprobantesSecuenciasEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto.TsComprobanteSecuenciaRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto.TsComprobanteSecuenciaResponseDto;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TsComprobanteSecuenciaBuilder {

    public TsComprobantesSecuenciasEntity builderEntity(Long idData, Long iEmpresa,
                                                        TsComprobanteSecuenciaRequestDto model) {
        return TsComprobantesSecuenciasEntity.builder()
                .idComprobanteSecuencia(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(iEmpresa)
                .anio(model.getAnio())
                .tipo(model.getTipo())
                .ultimoNumero(model.getUltimoNumero())
                .build();
    }

    public TsComprobantesSecuenciasEntity builderUpdateEntity(TsComprobanteSecuenciaRequestDto model,
                                                              TsComprobantesSecuenciasEntity item) {
        return TsComprobantesSecuenciasEntity.builder()
                .idComprobanteSecuencia(item.getIdComprobanteSecuencia())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .anio(model.getAnio())
                .tipo(model.getTipo())
                .ultimoNumero(model.getUltimoNumero())
                .build();
    }

    public TsComprobanteSecuenciaResponseDto builderUpdateEntity(TsComprobantesSecuenciasEntity model) {
        return TsComprobanteSecuenciaResponseDto.builder()
                .idComprobanteSecuencia(model.getIdComprobanteSecuencia())
                .anio(model.getAnio())
                .tipo(model.getTipo())
                .ultimoNumero(model.getUltimoNumero())
                .idCajas(Objects.nonNull(model.getCajas()) ? model.getCajas().getIdCaja() : null)
                .nombreCaja(Objects.nonNull(model.getCajas()) ? model.getCajas().getNombre() : null)
                .codigoCaja(Objects.nonNull(model.getCajas()) ? model.getCajas().getCodigoCaja() : null)
                .build();
    }

}
