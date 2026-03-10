package com.calero.lili.api.modCompras.modComprasImpuestos.builder;

import com.calero.lili.api.builder.FormasPagoBuilder;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.GetDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.GetListDto;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.tablas.tbDocumentos.TbDocumentoEntity;
import com.calero.lili.api.tablas.tbSustentos.TbSustentosEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CpImpuestosBuilder {


    private final ValoresBuilder valoresBuilder;
    private final ReembolsoBuilder reembolsoBuilder;
    private final ImpuestoCodigoBuilder impuestoCodigoBuilder;
    private final FormasPagoBuilder formasPagoBuilder;

    public CpImpuestosEntity builderEntity(CreationCompraImpuestoRequestDto model, Long idData, Long idEmpresa) {
        return CpImpuestosEntity.builder()
                .idImpuestos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .documento(builderDocumento(model.getCodigoDocumento()))
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .serie(model.getSerie())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.R.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .sustento(builderSustento(model.getCodigoSustento()))
                .tipoContribuyente(model.getTipoContribuyente())
                .concepto(model.getConcepto())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .referencia(model.getReferencia())
                .liquidar(model.getLiquidar())
                .devolucionIva(model.getDevolucionIva())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaRegistro(DateUtils.toLocalDate(model.getFechaRegistro()))
                .fechaVencimiento(Objects.isNull(model.getFechaVencimiento()) || model.getFechaVencimiento().isEmpty()
                        ? null
                        : DateUtils.toLocalDate(model.getFechaVencimiento()))
                .valoresEntity(Objects.nonNull(model.getValores())
                        ? valoresBuilder.builderList(model.getValores(), idData, idEmpresa)
                        : null)
                .reembolsosEntity(Objects.nonNull(model.getReembolsos())
                        ? reembolsoBuilder.builderListEntity(model.getReembolsos(), idData, idEmpresa)
                        : null)
                .pagoExterior(model.getPagoExterior())
                .tercero(builderProveedor(model.getIdTercero()))
                .codigosEntity(Objects.nonNull(model.getImpuestoCodigos())
                        ? impuestoCodigoBuilder.builderList(model.getImpuestoCodigos(), idData, idEmpresa)
                        : null)
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .pagoLocExt(Objects.nonNull(model.getPagoLocExt())
                        ? model.getPagoLocExt()
                        : "01")
                .destino(model.getDestino())
                .build();
    }

    public CpImpuestosEntity builderUpdateEntity(CreationCompraImpuestoRequestDto model, CpImpuestosEntity item) {
        return CpImpuestosEntity.builder()
                .idImpuestos(item.getIdImpuestos())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .documento(builderDocumento(model.getCodigoDocumento()))
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .serie(model.getSerie())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.R.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .sustento(builderSustento(model.getCodigoSustento()))
                .tipoContribuyente(model.getTipoContribuyente())
                .concepto(model.getConcepto())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .referencia(model.getReferencia())
                .liquidar(model.getLiquidar())
                .devolucionIva(model.getDevolucionIva())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaRegistro(DateUtils.toLocalDate(model.getFechaRegistro()))
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .valoresEntity(Objects.nonNull(model.getValores())
                        ? valoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa())
                        : item.getValoresEntity())
                .reembolsosEntity(Objects.nonNull(model.getReembolsos())
                        ? reembolsoBuilder.builderListEntity(model.getReembolsos(), item.getIdData(), item.getIdEmpresa())
                        : item.getReembolsosEntity())
                .pagoExterior(model.getPagoExterior())
                .tercero(builderProveedor(model.getIdTercero()))
                .codigosEntity(Objects.nonNull(model.getImpuestoCodigos())
                        ? impuestoCodigoBuilder.builderList(model.getImpuestoCodigos(), item.getIdData(), item.getIdEmpresa())
                        : item.getCodigosEntity())
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .pagoLocExt(Objects.nonNull(model.getPagoLocExt())
                        ? model.getPagoLocExt()
                        : item.getPagoLocExt())
                .destino(model.getDestino())
                .build();
    }

    public GetDto builderDto(CpImpuestosEntity model) {
        return GetDto.builder()
                .valores(Objects.nonNull(model.getValoresEntity())
                        ? valoresBuilder.builderListDto(model.getValoresEntity())
                        : new ArrayList<>())
                .reembolsos(Objects.nonNull(model.getReembolsosEntity())
                        ? reembolsoBuilder.builderListDto(model.getReembolsosEntity())
                        : new ArrayList<>())
                .idTercero(Objects.nonNull(model.getTercero())
                        ? model.getTercero().getIdTercero()
                        : null)
                .idImpuestos(model.getIdImpuestos())
                .sucursal(model.getSucursal())
                .codigoDocumento(Objects.nonNull(model.getDocumento())
                        ? model.getDocumento().getCodigoDocumento()
                        : null)
                .documento(Objects.nonNull(model.getDocumento())
                        ? model.getDocumento().getDocumento()
                        : null)
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .concepto(model.getConcepto())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .fechaAutorizacion(Objects.nonNull(model.getFechaAutorizacion())
                        ? DateUtils.toString(model.getFechaAutorizacion())
                        : null)
                .tipoContribuyente(model.getTipoContribuyente())
                .referencia(model.getReferencia())
                .liquidar(model.getLiquidar())
                .devolucionIva(model.getDevolucionIva())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .codigoSustento(Objects.nonNull(model.getSustento())
                        ? model.getSustento().getCodigoSustento()
                        : null)
                .sustento(Objects.nonNull(model.getSustento())
                        ? model.getSustento().getSustento()
                        : null)
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaRegistro(Objects.nonNull(model.getFechaRegistro())
                        ? DateUtils.toString(model.getFechaRegistro())
                        : null)
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaRegistro())
                        : null)
                .impuestoCodigos(Objects.nonNull(model.getCodigosEntity())
                        ? impuestoCodigoBuilder.builderListResponse(model.getCodigosEntity())
                        : null)
                .pagoLocExt(model.getPagoLocExt())
                .paisEfecPago(Objects.nonNull(model.getPagoExterior())
                        ? model.getPagoExterior().getPaisEfecPago()
                        : null)
                .aplicConvDobTrib(Objects.nonNull(model.getPagoExterior())
                        ? model.getPagoExterior().getAplicConvDobTrib()
                        : null)
                .pagExtSujRetNorLeg(Objects.nonNull(model.getPagoExterior())
                        ? model.getPagoExterior().getPagExtSujRetNorLeg()
                        : null)
                .destino(model.getDestino())
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .build();
    }

    public GetListDto builderGetListDto(CpImpuestosEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idImpuestos(model.getIdImpuestos())
                .codigoDocumento(Objects.nonNull(model.getDocumento())
                        ? model.getDocumento().getCodigoDocumento()
                        : null)
                .documento(Objects.nonNull(model.getDocumento())
                        ? model.getDocumento().getDocumento()
                        : null)
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaRegistro(Objects.nonNull(model.getFechaRegistro())
                        ? DateUtils.toString(model.getFechaRegistro())
                        : null)
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idTercero(Objects.nonNull(model.getTercero())
                        ? model.getTercero().getIdTercero()
                        : null)
                .valores(valoresBuilder.builderListDto(model.getValoresEntity()))
                .codigoSustento(Objects.nonNull(model.getSustento())
                        ? model.getSustento().getCodigoSustento()
                        : null)
                .sustento(Objects.nonNull(model.getSustento())
                        ? model.getSustento().getSustento()
                        : null)
                .impuestoCodigos(Objects.nonNull(model.getCodigosEntity())
                        ? impuestoCodigoBuilder.builderListResponse(model.getCodigosEntity())
                        : null)
                .destino(model.getDestino())
                .numeroItems(0)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .build();
    }

    public List<GetListDto> builderListResponse(List<CpImpuestosEntity> list) {
        return list.stream()
                .map(this::builderGetListDto)
                .toList();
    }

    public GeTerceroEntity builderProveedor(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }


    private TbDocumentoEntity builderDocumento(String codigoDocumento) {
        return TbDocumentoEntity.builder()
                .codigoDocumento(codigoDocumento)
                .build();
    }

    private TbSustentosEntity builderSustento(String codigoDocumento) {
        if (Objects.isNull(codigoDocumento)) return null;
        return TbSustentosEntity.builder()
                .codigoSustento(codigoDocumento)
                .build();
    }

}
