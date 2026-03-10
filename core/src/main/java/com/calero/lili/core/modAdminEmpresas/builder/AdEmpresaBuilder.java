package com.calero.lili.core.modAdminEmpresas.builder;

import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaCreationResponseDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetOneDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaRequestDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaRucResponseDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Component
public class AdEmpresaBuilder {


    public AdEmpresaEntity builderEntity(AdEmpresaRequestDto model, Long idData, Long nextIdEmpresa) {
        return AdEmpresaEntity.builder()
                .id(UUID.randomUUID())
                .idEmpresa(nextIdEmpresa)
                .idData(idData)
                .razonSocial(model.getRazonSocial())
                .ruc(model.getRuc())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .ciudad(model.getCiudad())
                .direccionMatriz(model.getDireccionMatriz())
                .numero(model.getNumero())
                .contadorNombre(model.getContadorNombre())
                .contadorRuc(model.getContadorRuc())
                .representanteNombre(model.getRepresentanteNombre())
                .representanteTipoIdentificacion(model.getRepresentanteTipoIdentificacion())
                .representanteIdentificacion(model.getRepresentanteIdentificacion())
                .email(model.getEmail())
                .tipoContribuyente(Objects.nonNull(model.getTipoContribuyente())
                        ? model.getTipoContribuyente()
                        : null)
                .obligadoContabilidad(model.getObligadoContabilidad())
                .devolucionIva(model.getDevolucionIva())
                .agenteRetencion(Objects.isNull(model.getAgenteRetencion()) || model.getAgenteRetencion().isEmpty()
                        ? null
                        : model.getAgenteRetencion())
                .contribuyenteEspecial(Objects.isNull(model.getContribuyenteEspecial()) || model.getContribuyenteEspecial().isEmpty()
                        ? null
                        : model.getContribuyenteEspecial())
                .codigoSustento(model.getCodigoSustento())
                .formaPagoSri(model.getFormaPagoSri())
                .estado(1)
                .ambienteFactura(2)
                .ambienteNotaCredito(2)
                .ambienteNotaDebito(2)
                .ambienteLiquidacion(2)
                .ambienteComprobanteRetencion(2)
                .ambienteGuiaRemision(2)
                .momentoEnvio(2)
                .fechaCreacion(LocalDate.now())
                .fechaCaducidadCertificado(Objects.nonNull(model.getFechaCaducidadCertificado())
                        ? DateUtils.toLocalDate(model.getFechaCaducidadCertificado())
                        : null)
                .momentoEnvioFactura(model.getMomentoEnvioFactura())
                .momentoEnvioNotaDebito(model.getMomentoEnvioNotaDebito())
                .momentoEnvioNotaCredito(model.getMomentoEnvioNotaCredito())
                .momentoEnvioLiquidacion(model.getMomentoEnvioLiquidacion())
                .momentoEnvioGuiaRemision(model.getMomentoEnvioGuiaRemision())
                .momentoEnvioComprobanteRetencion(model.getMomentoEnvioComprobanteRetencion())
                .build();
    }

    public AdEmpresaEntity builderUpdateEntity(AdEmpresaRequestDto model, AdEmpresaEntity item) {
        return AdEmpresaEntity.builder()
                .id(item.getId())
                .idEmpresa(item.getIdEmpresa())
                .idData(item.getIdData())
                .fechaCreacion(item.getFechaCreacion())
                .razonSocial(model.getRazonSocial())
                .ruc(model.getRuc())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .ciudad(model.getCiudad())
                .direccionMatriz(model.getDireccionMatriz())
                .numero(model.getNumero())
                .contadorNombre(model.getContadorNombre())
                .contadorRuc(model.getContadorRuc())
                .representanteNombre(model.getRepresentanteNombre())
                .representanteTipoIdentificacion(model.getRepresentanteTipoIdentificacion())
                .representanteIdentificacion(model.getRepresentanteIdentificacion())
                .email(model.getEmail())
                .tipoContribuyente(Objects.nonNull(model.getTipoContribuyente())
                        ? model.getTipoContribuyente()
                        : null)
                .obligadoContabilidad(model.getObligadoContabilidad())
                .devolucionIva(model.getDevolucionIva())
                .agenteRetencion(model.getAgenteRetencion())
                .contribuyenteEspecial(model.getContribuyenteEspecial())
                .codigoSustento(model.getCodigoSustento())
                .formaPagoSri(model.getFormaPagoSri())
                .estado(item.getEstado())
                .ambienteFactura(item.getAmbienteFactura())
                .ambienteNotaCredito(item.getAmbienteNotaCredito())
                .ambienteNotaDebito(item.getAmbienteNotaDebito())
                .ambienteLiquidacion(item.getAmbienteLiquidacion())
                .ambienteComprobanteRetencion(item.getAmbienteComprobanteRetencion())
                .ambienteGuiaRemision(item.getAmbienteGuiaRemision())
                .fechaCaducidadCertificado(Objects.nonNull(model.getFechaCaducidadCertificado())
                        ? DateUtils.toLocalDate(model.getFechaCaducidadCertificado())
                        : item.getFechaCaducidadCertificado())
                .momentoEnvioFactura(model.getMomentoEnvioFactura())
                .momentoEnvioNotaDebito(model.getMomentoEnvioNotaDebito())
                .momentoEnvioNotaCredito(model.getMomentoEnvioNotaCredito())
                .momentoEnvioLiquidacion(model.getMomentoEnvioLiquidacion())
                .momentoEnvioGuiaRemision(model.getMomentoEnvioGuiaRemision())
                .momentoEnvioComprobanteRetencion(model.getMomentoEnvioComprobanteRetencion())
                .build();
    }


    public AdEmpresaCreationResponseDto builderResponseDto(AdEmpresaEntity model) {
        return AdEmpresaCreationResponseDto.builder()
                .idEmpresa(model.getIdEmpresa())
                .build();
    }

    public AdEmpresaGetOneDto builderResponse(AdEmpresaEntity model) {
        return AdEmpresaGetOneDto.builder()
                .idEmpresa(model.getIdEmpresa())
                .razonSocial(model.getRazonSocial())
                .ruc(model.getRuc())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .ciudad(model.getCiudad())
                .direccionMatriz(model.getDireccionMatriz())
                .numero(model.getNumero())
                .contadorNombre(model.getContadorNombre())
                .contadorRuc(model.getContadorRuc())
                .representanteNombre(model.getRepresentanteNombre())
                .representanteTipoIdenficacion(model.getRepresentanteTipoIdentificacion())
                .representanteIdentificacion(model.getRepresentanteIdentificacion())
                .email(model.getEmail())
                .tipoContribuyente(model.getTipoContribuyente())
                .obligadoContabilidad(model.getObligadoContabilidad())
                .devolucionIva(model.getDevolucionIva())
                .agenteRetencion(model.getAgenteRetencion())
                .contribuyenteEspecial(model.getContribuyenteEspecial())
                .codigoSustento(model.getCodigoSustento())
                .formaPagoSri(model.getFormaPagoSri())
                .fechaCreacion(model.getFechaCreacion())
                .estado(model.getEstado())
                .ambienteFactura(model.getAmbienteFactura())
                .ambienteNotaCredito(model.getAmbienteNotaCredito())
                .ambienteNotaDebito(model.getAmbienteNotaDebito())
                .ambienteLiquidacion(model.getAmbienteLiquidacion())
                .ambienteComprobanteRetencion(model.getAmbienteComprobanteRetencion())
                .momentoEnvio(model.getMomentoEnvio())
                .ambienteGuiaRemision(model.getAmbienteGuiaRemision())
                .fechaCaducidadCertificado(Objects.nonNull(model.getFechaCaducidadCertificado())
                        ? DateUtils.toString(model.getFechaCaducidadCertificado())
                        : null)
                .momentoEnvioFactura(model.getMomentoEnvioFactura())
                .momentoEnvioNotaDebito(model.getMomentoEnvioNotaDebito())
                .momentoEnvioNotaCredito(model.getMomentoEnvioNotaCredito())
                .momentoEnvioLiquidacion(model.getMomentoEnvioLiquidacion())
                .momentoEnvioGuiaRemision(model.getMomentoEnvioGuiaRemision())
                .momentoEnvioComprobanteRetencion(model.getMomentoEnvioComprobanteRetencion())
                .build();
    }

    public AdEmpresaGetListDto builderListResponse(AdEmpresaEntity model) {
        return AdEmpresaGetListDto.builder()
                .idEmpresa(model.getIdEmpresa())
                .razonSocial(model.getRazonSocial())
                .ruc(model.getRuc())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .ciudad(model.getCiudad())
                .direccionMatriz(model.getDireccionMatriz())
                .numero(model.getNumero())
                .contadorNombre(model.getContadorNombre())
                .contadorRuc(model.getContadorRuc())
                .representanteNombre(model.getRepresentanteNombre())
                .representanteIdentificacion(model.getRepresentanteIdentificacion())
                .email(model.getEmail())
                .tipoContribuyente(model.getTipoContribuyente())
                .obligadoContabilidad(model.getObligadoContabilidad())
                .devolucionIva(model.getDevolucionIva())
                .agenteRetencion(model.getAgenteRetencion())
                .contribuyenteEspecial(model.getContribuyenteEspecial())
                .fechaCreacion(model.getFechaCreacion())
                .ambienteFactura(model.getAmbienteFactura())
                .ambienteNotaCredito(model.getAmbienteNotaCredito())
                .ambienteNotaDebito(model.getAmbienteNotaDebito())
                .ambienteLiquidacion(model.getAmbienteLiquidacion())
                .ambienteComprobanteRetencion(model.getAmbienteComprobanteRetencion())
                .ambienteGuiaRemision(model.getAmbienteGuiaRemision())
                .momentoEnvioFactura(model.getMomentoEnvioFactura())
                .momentoEnvioNotaDebito(model.getMomentoEnvioNotaDebito())
                .momentoEnvioNotaCredito(model.getMomentoEnvioNotaCredito())
                .momentoEnvioLiquidacion(model.getMomentoEnvioLiquidacion())
                .momentoEnvioGuiaRemision(model.getMomentoEnvioGuiaRemision())
                .momentoEnvioComprobanteRetencion(model.getMomentoEnvioComprobanteRetencion())
                .build();
    }

    public AdEmpresaRucResponseDto builderRucResponse(AdEmpresaEntity model) {
        return AdEmpresaRucResponseDto.builder()
                .idEmpresa(model.getIdEmpresa())
                .razonSocial(model.getRazonSocial())
                .build();
    }

}
