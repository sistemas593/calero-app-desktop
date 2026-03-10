package com.calero.lili.api.modClientesTickets.builder;

import com.calero.lili.api.modClientesTickets.VtClienteTicketsEntity;
import com.calero.lili.api.modClientesTickets.dto.VtClientesTicketsCreationRequestDto;
import com.calero.lili.api.modClientesTickets.dto.VtClientesTicketsGetDtoOne;
import com.calero.lili.api.modClientesTickets.dto.VtClientesTicketsGetListDto;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class ClienteTicketsBuilder {

    public VtClienteTicketsEntity builderEntity(VtClientesTicketsCreationRequestDto model, Long idData) {
        return VtClienteTicketsEntity.builder()
                .idTicket(UUID.randomUUID())
                .idData(idData)
                .asunto(model.getAsunto())
                .contacto(model.getContacto())
                .detalle(model.getDetalle())
                .respaldo(model.getRespaldo())
                .fecha(DateUtils.toLocalDate(model.getFecha()))
                .estado(model.getEstado())
                .username(model.getUsername())
                .cliente(builderCliente(model.getIdTercero()))
                .build();
    }

    public VtClienteTicketsEntity builderUpdateEntity(VtClientesTicketsCreationRequestDto model,
                                                      VtClienteTicketsEntity item) {
        return VtClienteTicketsEntity.builder()
                .idTicket(item.getIdTicket())
                .idData(item.getIdData())
                .asunto(model.getAsunto())
                .contacto(model.getContacto())
                .detalle(model.getDetalle())
                .respaldo(model.getRespaldo())
                .fecha(DateUtils.toLocalDate(model.getFecha()))
                .estado(model.getEstado())
                .username(model.getUsername())
                .cliente(builderCliente(model.getIdTercero()))
                .build();
    }

    public VtClientesTicketsGetDtoOne builderResponse(VtClienteTicketsEntity model) {
        return VtClientesTicketsGetDtoOne.builder()
                .idTicket(model.getIdTicket())
                .asunto(model.getAsunto())
                .contacto(model.getContacto())
                .detalle(model.getDetalle())
                .respaldo(model.getRespaldo())
                .fecha(DateUtils.toString(model.getFecha()))
                .estado(model.getEstado())
                .build();
    }

    public VtClientesTicketsGetListDto builderListResponse(VtClienteTicketsEntity model) {
        return VtClientesTicketsGetListDto.builder()
                .idNovedad(model.getIdTicket())
                .asunto(model.getAsunto())
                .contacto(model.getContacto())
                .detalle(model.getDetalle())
                .respaldo(model.getRespaldo())
                .fecha(DateUtils.toString(model.getFecha()))
                .estado(model.getEstado())
                .build();
    }


    private GeTerceroEntity builderCliente(UUID idTercero) {
        if (Objects.isNull(idTercero)) return null;
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }


}
