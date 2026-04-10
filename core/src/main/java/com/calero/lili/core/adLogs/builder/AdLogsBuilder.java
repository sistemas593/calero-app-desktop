package com.calero.lili.core.adLogs.builder;

import com.calero.lili.core.adLogs.AdLogsEntity;
import com.calero.lili.core.adLogs.dto.AdLogsDtoResponse;
import com.calero.lili.core.adLogs.dto.AdLogsRequestDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
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

    public AdLogsRequestDto builderVentasDocumentos(VtVentaEntity model, Boolean validacionPrevia) {
        return AdLogsRequestDto.builder()
                .idDocumento(model.getIdVenta())
                .tipoDocumento(model.getTipoVenta())
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .secuencial(model.getSecuencial())
                .serie(model.getSerie())
                .validacionPrevia(validacionPrevia)
                .build();
    }

    public AdLogsRequestDto builderGuiaRemision(VtGuiaEntity model, Boolean validacionPrevia) {
        return AdLogsRequestDto.builder()
                .idDocumento(model.getIdGuia())
                .tipoDocumento("GUIA")
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .secuencial(model.getSecuencial())
                .serie(model.getSerie())
                .validacionPrevia(validacionPrevia)
                .build();
    }

    public AdLogsRequestDto builderLiquidacion(CpLiquidacionesEntity model, Boolean validacionPrevia) {
        return AdLogsRequestDto.builder()
                .idDocumento(model.getIdLiquidacion())
                .tipoDocumento("LIQ")
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .secuencial(model.getSecuencial())
                .serie(model.getSerie())
                .validacionPrevia(validacionPrevia)
                .build();
    }

    public AdLogsRequestDto builderComprobanteRetencion(CpRetencionesEntity model, Boolean validacionPrevia) {
        return AdLogsRequestDto.builder()
                .idDocumento(model.getIdRetencion())
                .tipoDocumento("RET")
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .secuencial(model.getSecuencialRetencion())
                .serie(model.getSerieRetencion())
                .validacionPrevia(validacionPrevia)
                .build();
    }


}
