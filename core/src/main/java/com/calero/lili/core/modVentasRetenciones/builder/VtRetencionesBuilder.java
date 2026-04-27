package com.calero.lili.core.modVentasRetenciones.builder;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentasRetenciones.VtRetencionesEntity;
import com.calero.lili.core.modVentasRetenciones.dto.CreationVentasRetencionesRequestDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetListDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtRetencionesBuilder {


    private final VtRetencionesValoresBuilder vtRetencionesValoresBuilder;


    public VtRetencionesEntity builderEntity(CreationVentasRetencionesRequestDto model, Long idData, Long idEmpresa) {
        return VtRetencionesEntity.builder()
                .idRetencion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valoresEntity(vtRetencionesValoresBuilder.builderList(model.getValores(), idData, idEmpresa))
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .fechaEmisionRetencion(LocalDate.parse(model.getFechaEmisionRetencion()))
                .periodoFiscal(Objects.nonNull(model.getPeriodoFiscal()) ? DateUtils.toPeriodoFiscalDate(model.getPeriodoFiscal()) : null)
                .build();
    }


    public VtRetencionesEntity builderUpdateEntity(CreationVentasRetencionesRequestDto model, VtRetencionesEntity item) {
        return VtRetencionesEntity.builder()
                .idRetencion(item.getIdRetencion())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .valoresEntity(vtRetencionesValoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .fechaEmisionRetencion(LocalDate.parse(model.getFechaEmisionRetencion()))
                .periodoFiscal(Objects.nonNull(model.getPeriodoFiscal()) ? DateUtils.toPeriodoFiscalDate(model.getPeriodoFiscal()) : null)
                .build();
    }


    public GetDto builderResponse(VtRetencionesEntity model) {
        return GetDto.builder()
                .idRetencion(model.getIdRetencion())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacionRetencion())
                .valores(vtRetencionesValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .fechaAutorizacion(Objects.nonNull(model.getFechaAutorizacion())
                        ? DateUtils.toLocalDateTimeString(model.getFechaAutorizacion())
                        : null)
                .fechaEmisionRetencion(DateUtils.toString(model.getFechaEmisionRetencion()))
                .tercero(builderResponseTercero(model.getCliente()))
                .build();
    }

    private GetDto.TerceroDto builderResponseTercero(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return GetDto.TerceroDto.builder()
                .idTercero(cliente.getIdTercero())
                .tercero(cliente.getTercero())
                .tipoIdentificacion(cliente.getTipoIdentificacion())
                .numeroIdentificacion(cliente.getNumeroIdentificacion())
                .build();
    }

    public GetListDto builderListResponse(VtRetencionesEntity model) {
        return GetListDto.builder()
                .idRetencion(model.getIdRetencion())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacionRetencion())
                .valores(vtRetencionesValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .fechaEmisionRetencion(DateUtils.toString(model.getFechaEmisionRetencion()))
                .tercero(builderTerceroResponseList(model.getCliente()))
                .build();

    }

    private GetListDto.TerceroDto builderTerceroResponseList(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return GetListDto.TerceroDto.builder()
                .idTercero(cliente.getIdTercero())
                .tercero(cliente.getTercero())
                .tipoIdentificacion(cliente.getTipoIdentificacion())
                .numeroIdentificacion(cliente.getNumeroIdentificacion())
                .build();
    }
}
