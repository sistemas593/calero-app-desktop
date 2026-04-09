package com.calero.lili.core.adLogs.builder;

import com.calero.lili.core.adLogs.AdLogsEntity;
import com.calero.lili.core.adLogs.dto.AdLogsDtoResponse;
import com.calero.lili.core.adLogs.dto.AdLogsRequestDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AdLogsBuilder {


    public AdLogsEntity builderLog(AdLogsRequestDto model, String mensajes) {
        return AdLogsEntity.builder()
                .idLog(UUID.randomUUID())
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .idDocumento(model.getIdDocumento())
                .mensajes(mensajes)
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .tipoDocumento(model.getTipoDocumento())
                .fechaHora(LocalDateTime.now())
                .build();
    }

    public AdLogsDtoResponse builderResponse(AdLogsEntity model) {
        return AdLogsDtoResponse.builder()
                .id(model.getIdDocumento())
                .mensajes(model.getMensajes())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .tipoDocumento(model.getTipoDocumento())
                .fechaHora(DateUtils.obtenerFechaHora(model.getFechaHora()))
                .build();
    }



}
