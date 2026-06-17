package com.calero.lili.core.modVentasRetenciones.builder;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentasRetenciones.VtRetencionesEntity;
import com.calero.lili.core.modVentasRetenciones.dto.CreationVentasRetencionesRequestDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetVentaRetencionesDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetVentaRetencionesListDto;
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
                .periodoFiscal(Objects.nonNull(model.getPeriodoFiscal()) ? DateUtils.toLocalDate(model.getPeriodoFiscal()) : null)
                .existeComprobante(Boolean.FALSE)
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
                .periodoFiscal(Objects.nonNull(model.getPeriodoFiscal()) ? DateUtils.toLocalDate(model.getPeriodoFiscal()) : item.getPeriodoFiscal())
                .existeComprobante(item.getExisteComprobante())
                .build();
    }


    public GetVentaRetencionesDto builderResponse(VtRetencionesEntity model) {
        return GetVentaRetencionesDto.builder()
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
                .existeComprobante(model.getExisteComprobante())
                .build();
    }

    private GetVentaRetencionesDto.TerceroDto builderResponseTercero(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return GetVentaRetencionesDto.TerceroDto.builder()
                .idTercero(cliente.getIdTercero())
                .tercero(cliente.getTercero())
                .tipoIdentificacion(cliente.getTipoIdentificacion())
                .numeroIdentificacion(cliente.getNumeroIdentificacion())
                .build();
    }

    public GetVentaRetencionesListDto builderListResponse(VtRetencionesEntity model) {
        return GetVentaRetencionesListDto.builder()
                .idRetencion(model.getIdRetencion())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacionRetencion())
                .valores(vtRetencionesValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .fechaEmisionRetencion(DateUtils.toString(model.getFechaEmisionRetencion()))
                .tercero(builderTerceroResponseList(model.getCliente()))
                .existeComprobante(model.getExisteComprobante())
                .build();

    }

    private GetVentaRetencionesListDto.TerceroDto builderTerceroResponseList(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return GetVentaRetencionesListDto.TerceroDto.builder()
                .idTercero(cliente.getIdTercero())
                .tercero(cliente.getTercero())
                .tipoIdentificacion(cliente.getTipoIdentificacion())
                .numeroIdentificacion(cliente.getNumeroIdentificacion())
                .build();
    }
}
