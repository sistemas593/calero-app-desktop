package com.calero.lili.api.modCompras.modComprasRetenciones.builder;

import com.calero.lili.api.builder.FormasPagoBuilder;
import com.calero.lili.api.builder.InformacionAdicionalBuilder;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.CodigoDocumento;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.api.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.api.modCompras.modComprasRetenciones.dto.GetDto;
import com.calero.lili.api.modCompras.modComprasRetenciones.dto.GetListDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CpRetencionesBuilder {

    private final InformacionAdicionalBuilder informacionAdicionalBuilder;
    private final FormasPagoBuilder formasPagoBuilder;

    public CpRetencionesEntity builderEntity(CreationRetencionRequestDto model, Long idData, Long idEmpresa) {
        return CpRetencionesEntity.builder()
                .idRetencion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacionRetencion())
                .fechaEmisionRetencion(Objects.nonNull(model.getFechaEmisionRetencion())
                        ? DateUtils.toLocalDate(model.getFechaEmisionRetencion())
                        : null)
                .email(model.getEmail())
                .anulada(Boolean.FALSE)
                .impresa(Objects.nonNull(model.getImpresa())
                        ? model.getImpresa()
                        : Boolean.FALSE)
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento())
                        ? model.getCodigoDocumento()
                        : CodigoDocumento.COMPROBANTE_RETENCION.getCodigoDocumento())
                .formatoDocumento(model.getFormatoDocumento())
                .periodoFiscal(model.getPeriodoFiscal())
                .relacionado(model.getRelacionado())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .build();
    }


    public CpRetencionesEntity builderUpdateEntity(CreationRetencionRequestDto model, CpRetencionesEntity item) {
        return CpRetencionesEntity.builder()
                .idRetencion(item.getIdRetencion())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacionRetencion())
                .fechaEmisionRetencion(DateUtils.toLocalDate(model.getFechaEmisionRetencion()))
                .fechaAnulacion(item.getFechaAnulacion())
                .anulada(item.getAnulada())
                .impresa(Objects.nonNull(model.getImpresa())
                        ? model.getImpresa()
                        : Boolean.FALSE)
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento())
                        ? model.getCodigoDocumento()
                        : CodigoDocumento.COMPROBANTE_RETENCION.getCodigoDocumento())
                .periodoFiscal(model.getPeriodoFiscal())
                .relacionado(model.getRelacionado())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .build();
    }

    public GetDto builderResponse(CpRetencionesEntity model) {
        return GetDto.builder()
                .idRetencion(model.getIdRetencion())
                .sucursal(model.getSucursal())
                .fechaEmision(DateUtils.toString(model.getFechaEmisionRetencion()))
                .serie(model.getSerieRetencion())
                .secuencial(model.getSecuencialRetencion())
                .idTercero(model.getProveedor().getIdTercero())
                .numeroIdentificacion(Objects.nonNull(model.getProveedor()) ? model.getProveedor().getNumeroIdentificacion() : null)
                .terceroNombre(Objects.nonNull(model.getProveedor()) ? model.getProveedor().getTercero() : null)
                .tipoIdentificacion(Objects.nonNull(model.getProveedor()) ? model.getProveedor().getTipoIdentificacion() : null)
                .email(model.getEmail())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .fechaAutorizacion(model.getFechaAutorizacion())
                .claveAcceso(model.getClaveAcceso())
                .formatoDocumento(model.getFormatoDocumento())
                .ambiente(model.getAmbiente())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacionRetencion())
                .build();

    }

    public GetListDto builderListResponse(CpRetencionesEntity model) {
        return GetListDto.builder()
                .idRetencion(model.getIdRetencion())
                .serieRetencion(model.getSerieRetencion())
                .secuencialRetencion(model.getSecuencialRetencion())
                .sucursal(model.getSucursal())
                .fechaEmisionRetencion(Objects.nonNull(model.getFechaEmisionRetencion()) ? DateUtils.toString(model.getFechaEmisionRetencion()) : null)
                .idTercero(model.getProveedor().getIdTercero())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .formatoDocumento(model.getFormatoDocumento())
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .numeroIdentificacion(Objects.nonNull(model.getProveedor()) ? model.getProveedor().getNumeroIdentificacion() : null)
                .terceroNombre(Objects.nonNull(model.getProveedor()) ? model.getProveedor().getTercero() : null)
                .tipoIdentificacion(Objects.nonNull(model.getProveedor()) ? model.getProveedor().getTipoIdentificacion() : null)
                .build();
    }

}
