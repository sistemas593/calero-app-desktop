package com.calero.lili.core.modClientesConfiguraciones.builder;

import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesEntity;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesGetListDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesGetOneDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesRequestDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Component
public class VtClienteConfiguracionBuilder {

    public VtClientesConfiguracionesEntity builderEntity(Long idData, VtClientesConfiguracionesRequestDto model) {
        return VtClientesConfiguracionesEntity.builder()
                .idConfiguracion(UUID.randomUUID())
                .idData(idData)
                .clave(model.getClave())
                .fechaVencimiento(DateUtils.toLocalDate(model.getFechaVencimiento()))
                .ruc(model.getRuc())
                .enviarCorreos(model.getEnviarCorreos())
                .valorRenovacion(model.getValorRenovacion())
                .facturaEmite(model.getFacturaEmite())
                .fechaEmitirFactura(Objects.isNull(model.getFechaEmitirFactura()) || model.getFechaEmitirFactura().isEmpty()
                        ? null
                        : DateUtils.toLocalDate(model.getFechaEmitirFactura()))
                .fechaLlamar(Objects.isNull(model.getFechaLlamar()) || model.getFechaLlamar().isEmpty()
                        ? null
                        : DateUtils.toLocalDate(model.getFechaLlamar()))
                .respaldosResponsable(model.getRespaldosResponsable())
                .respaldoUltimoOficina(Objects.isNull(model.getRespaldoUltimoOficina()) || model.getRespaldoUltimoOficina().isEmpty()
                        ? null
                        : DateUtils.toLocalDate(model.getRespaldoUltimoOficina()))
                .conexionBase(model.getConexionBase())
                .configuraciones(model.getConfiguraciones())
                .notas(model.getNotas())
                .usuarios(model.getUsuarios())
                .modulos(model.getModulos())
                .rucsActivados(model.getRucsActivados())
                .clavesPcs(model.getClavesPcs())
                .tipoBlo(model.getTipoBlo())
                .fechaBlo(Objects.isNull(model.getFechaBlo()) || model.getFechaBlo().isEmpty()
                        ? null
                        : DateUtils.toLocalDate(model.getFechaBlo()))
                .tercero(builderCliente(model.getIdTercero()))
                .build();
    }

    public VtClientesConfiguracionesEntity builderUpdateEntity(VtClientesConfiguracionesRequestDto model,
                                                               VtClientesConfiguracionesEntity item) {
        return VtClientesConfiguracionesEntity.builder()
                .idConfiguracion(item.getIdConfiguracion())
                .idData(item.getIdData())
                .clave(model.getClave())
                .fechaVencimiento(DateUtils.toLocalDate(model.getFechaVencimiento()))
                .ruc(model.getRuc())
                .enviarCorreos(model.getEnviarCorreos())
                .valorRenovacion(model.getValorRenovacion())
                .facturaEmite(model.getFacturaEmite())
                .fechaEmitirFactura(Objects.isNull(model.getFechaEmitirFactura()) || model.getFechaEmitirFactura().isEmpty()
                        ? item.getFechaEmitirFactura()
                        : DateUtils.toLocalDate(model.getFechaEmitirFactura()))
                .fechaLlamar(Objects.isNull(model.getFechaLlamar()) || model.getFechaLlamar().isEmpty()
                        ? item.getFechaLlamar()
                        : DateUtils.toLocalDate(model.getFechaLlamar()))
                .respaldosResponsable(model.getRespaldosResponsable())
                .respaldoUltimoOficina(Objects.isNull(model.getRespaldoUltimoOficina()) || model.getRespaldoUltimoOficina().isEmpty()
                        ? item.getRespaldoUltimoOficina()
                        : DateUtils.toLocalDate(model.getRespaldoUltimoOficina()))
                .conexionBase(model.getConexionBase())
                .configuraciones(model.getConfiguraciones())
                .notas(model.getNotas())
                .usuarios(model.getUsuarios())
                .modulos(model.getModulos())
                .rucsActivados(model.getRucsActivados())
                .clavesPcs(model.getClavesPcs())
                .tipoBlo(model.getTipoBlo())
                .fechaBlo(Objects.isNull(model.getFechaBlo()) || model.getFechaBlo().isEmpty()
                        ? item.getFechaBlo()
                        : DateUtils.toLocalDate(model.getFechaBlo()))
                .tercero(builderCliente(model.getIdTercero()))
                .build();
    }


    public VtClientesConfiguracionesGetOneDto builderResponse(VtClientesConfiguracionesEntity model) {
        return VtClientesConfiguracionesGetOneDto.builder()
                .idConfiguracion(model.getIdConfiguracion())
                .clave(model.getClave())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento()) ? DateUtils.toString(model.getFechaVencimiento()) : null)
                .ruc(model.getRuc())
                .enviarCorreos(model.getEnviarCorreos())
                .valorRenovacion(model.getValorRenovacion())
                .facturaEmite(model.getFacturaEmite())
                .fechaEmitirFactura(Objects.nonNull(model.getFechaEmitirFactura()) ? DateUtils.toString(model.getFechaEmitirFactura()) : null)
                .fechaLlamar(Objects.nonNull(model.getFechaLlamar()) ? DateUtils.toString(model.getFechaLlamar()) : null)
                .respaldosResponsable(model.getRespaldosResponsable())
                .respaldoUltimoOficina(Objects.nonNull(model.getRespaldoUltimoOficina()) ? DateUtils.toString(model.getRespaldoUltimoOficina()) : null)
                .conexionBase(model.getConexionBase())
                .configuraciones(model.getConfiguraciones())
                .notas(model.getNotas())
                .usuarios(model.getUsuarios())
                .modulos(model.getModulos())
                .rucsActivados(model.getRucsActivados())
                .clavesPcs(model.getClavesPcs())
                .tipoBlo(model.getTipoBlo())
                .fechaBlo(Objects.nonNull(model.getFechaBlo()) ? DateUtils.toString(model.getFechaBlo()) : null)
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .nombreTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .build();
    }


    public VtClientesConfiguracionesGetListDto builderListResponse(VtClientesConfiguracionesEntity model) {
        return VtClientesConfiguracionesGetListDto.builder()
                .idConfiguracion(model.getIdConfiguracion())
                .clave(model.getClave())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .ruc(model.getRuc())
                .enviarCorreos(model.getEnviarCorreos())
                .valorRenovacion(model.getValorRenovacion())
                .facturaEmite(model.getFacturaEmite())
                .fechaEmitirFactura(Objects.nonNull(model.getFechaEmitirFactura())
                        ? DateUtils.toString(model.getFechaEmitirFactura())
                        : null)
                .fechaLlamar(Objects.nonNull(model.getFechaLlamar())
                        ? DateUtils.toString(model.getFechaLlamar())
                        : null)
                .respaldosResponsable(model.getRespaldosResponsable())
                .respaldoUltimoOficina(Objects.nonNull(model.getRespaldoUltimoOficina())
                        ? DateUtils.toString(model.getRespaldoUltimoOficina())
                        : null)
                .conexionBase(model.getConexionBase())
                .configuraciones(model.getConfiguraciones())
                .notas(model.getNotas())
                .usuarios(model.getUsuarios())
                .modulos(model.getModulos())
                .rucsActivados(model.getRucsActivados())
                .clavesPcs(model.getClavesPcs())
                .tipoBlo(model.getTipoBlo())
                .fechaBlo(Objects.nonNull(model.getFechaBlo())
                        ? DateUtils.toString(model.getFechaBlo())
                        : null)

                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .nombreTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .build();
    }


    private GeTerceroEntity builderCliente(UUID idTercero) {
        if (Objects.isNull(idTercero)) return null;
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

    public VtClientesConfiguracionesEntity builderListEntity(Long idData, VtClientesConfiguracionesRequestDto model) {
        return VtClientesConfiguracionesEntity.builder()
                .idConfiguracion(UUID.randomUUID())
                .idData(idData)
                .clave(model.getClave())
                .fechaVencimiento(DateUtils.toLocalDate(model.getFechaVencimiento()))
                .ruc(model.getRuc())
                .enviarCorreos("N")
                .valorRenovacion(BigDecimal.ZERO)
                .facturaEmite("")
                .fechaEmitirFactura(DateUtils.toLocalDate("01/01/2000"))
                .fechaLlamar(DateUtils.toLocalDate("01/01/2000"))
                .respaldosResponsable("")
                .respaldoUltimoOficina(DateUtils.toLocalDate("01/01/2000"))
                .conexionBase("")
                .configuraciones("")
                .notas("")
                .usuarios("")
                .modulos("")
                .rucsActivados("")
                .clavesPcs("")
                .tipoBlo(0L)
                .fechaBlo(DateUtils.toLocalDate("01/01/2000"))
                .tercero(builderCliente(model.getIdTercero()))
                .build();
    }

    public VtClientesConfiguracionesEntity builderListUpdate(VtClientesConfiguracionesRequestDto model,
                                                             VtClientesConfiguracionesEntity item) {
        return VtClientesConfiguracionesEntity.builder()
                .idConfiguracion(item.getIdConfiguracion())
                .idData(item.getIdData())
                .clave(item.getClave())
                .fechaVencimiento(DateUtils.toLocalDate(model.getFechaVencimiento()))
                .ruc(model.getRuc())
                .enviarCorreos(item.getEnviarCorreos())
                .valorRenovacion(item.getValorRenovacion())
                .facturaEmite(item.getFacturaEmite())
                .fechaEmitirFactura(item.getFechaEmitirFactura())
                .fechaLlamar(item.getFechaLlamar())
                .respaldosResponsable(item.getRespaldosResponsable())
                .respaldoUltimoOficina(item.getRespaldoUltimoOficina())
                .conexionBase(item.getConexionBase())
                .configuraciones(item.getConfiguraciones())
                .notas(item.getNotas())
                .usuarios(item.getUsuarios())
                .modulos(item.getModulos())
                .rucsActivados(item.getRucsActivados())
                .clavesPcs(item.getClavesPcs())
                .tipoBlo(item.getTipoBlo())
                .fechaBlo(item.getFechaBlo())
                .tercero(item.getTercero())
                .build();
    }
}
