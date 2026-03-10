package com.calero.lili.api.modImpuestosProcesos.builder;

import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionReferencias;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.api.modImpuestosProcesos.dto.ImpuestoProcesoResponseDto;
import com.calero.lili.api.modImpuestosProcesos.projection.RetencionReferenciaProjection;
import com.calero.lili.api.tablas.tbDocumentos.TbDocumentoEntity;
import com.calero.lili.api.tablas.tbRetenciones.TbRetencionEntity;
import com.calero.lili.api.tablas.tbRetencionesCodigos.TbRetencionesCodigosEntity;
import com.calero.lili.api.tablas.tbSustentos.TbSustentosEntity;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class ImpuestoProcesoBuilder {


    public ImpuestoProcesoResponseDto builderError(RetencionReferenciaProjection referencia) {
        return ImpuestoProcesoResponseDto.builder()
                .exitoso("N")
                .mensaje(MessageFormat.format("Para los datos de la refencia : Serie {0}, " +
                                "Secuencial {1}, Número Identificación {2} no existe retención",
                        referencia.getSerie(), referencia.getSecuencial(), referencia.getNumeroIdentificacion()))
                .build();
    }

    public ImpuestoProcesoResponseDto builderExistoso(CpImpuestosEntity impuesto) {
        return ImpuestoProcesoResponseDto.builder()
                .exitoso("S")
                .mensaje(MessageFormat.format("Guardado con éxito el documento: Identificación {0}, Serie {1}, Secuencial {2}  ",
                        impuesto.getNumeroIdentificacion(), impuesto.getSerie(), impuesto.getSecuencial()))
                .build();
    }

    public CpRetencionReferencias builderRetencionUpdate(RetencionReferenciaProjection referencia,
                                                         CpImpuestosEntity impuestos,
                                                         List<CpRetencionReferencias.ImpuestosCodigo> impuestosCodigoList) {
        return CpRetencionReferencias.builder()
                .idReferencia(referencia.getIdReferencia())
                .serie(referencia.getSerie())
                .secuencial(referencia.getSecuencial())
                .numeroIdentificacion(referencia.getNumeroIdentificacion())
                .impuestos(impuestos)
                .documento(builderDocumento(referencia.getCodigoDocumento()))
                .impuestosCodigos(impuestosCodigoList)
                .build();
    }

    private TbDocumentoEntity builderDocumento(String codigoDocumento) {
        return TbDocumentoEntity.builder()
                .codigoDocumento(codigoDocumento)
                .build();
    }

    public CpImpuestosEntity builderUpdateImpuesto(CpImpuestosEntity model,
                                                   UUID idRetencion,
                                                   List<CpRetencionReferencias.ImpuestosCodigo> impuestosCodigoList,
                                                   LocalDate fechaRegistro, String codigoSustento) {
        return CpImpuestosEntity.builder()
                .idImpuestos(model.getIdImpuestos())
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .comprobante(model.getComprobante())
                .concepto(model.getConcepto())
                .devolucionIva(model.getDevolucionIva())
                .fechaAutorizacion(model.getFechaAutorizacion())
                .fechaEmision(model.getFechaEmision())
                .fechaRegistro(fechaRegistro)
                .fechaVencimiento(model.getFechaVencimiento())
                .idParent(model.getIdParent())
                .retencion(CpRetencionesEntity.builder()
                        .idRetencion(idRetencion)
                        .build())
                .liquidar(model.getLiquidar())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .origen(model.getOrigen())
                .pagoExterior(model.getPagoExterior())
                .tercero(model.getTercero())
                .secuencial(model.getSecuencial())
                .serie(model.getSerie())
                .sucursal(model.getSucursal())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .tipoProveedor(model.getTipoProveedor())
                .documento(model.getDocumento())
                .tercero(model.getTercero())
                .sustento(TbSustentosEntity.builder().codigoSustento(codigoSustento).build())
                .formasPagoSri(model.getFormasPagoSri())
                .pagoLocExt(model.getPagoLocExt())
                .codigosEntity(builderListCodigos(impuestosCodigoList))
                .reembolsosEntity(model.getReembolsosEntity())
                .valoresEntity(model.getValoresEntity())
                .build();
    }

    private List<CpImpuestosCodigosEntity> builderListCodigos(List<CpRetencionReferencias.ImpuestosCodigo> list) {
        return list.stream()
                .map(this::builderImpuestoCodigos)
                .toList();
    }

    private CpImpuestosCodigosEntity builderImpuestoCodigos(CpRetencionReferencias.ImpuestosCodigo model) {
        return CpImpuestosCodigosEntity.builder()
                .idImpuestosCodigo(UUID.randomUUID())
                .baseImponible(model.getBaseImponible())
                .porcentajeRetener(model.getPorcentajeRetener())
                .valorRetenido(model.getValorRetenido())
                .retencionCodigos(TbRetencionesCodigosEntity.builder()
                        .codigoRetencion(model.getCodigoRetencion())
                        .build())
                .retencion(TbRetencionEntity.builder()
                        .codigo(model.getCodigo())
                        .build())
                .build();
    }
}
