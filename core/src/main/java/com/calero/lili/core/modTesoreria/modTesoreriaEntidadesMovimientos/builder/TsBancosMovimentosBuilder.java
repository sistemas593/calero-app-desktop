package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.builder;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.TsBancosMovimentosEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationResponseDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TsBancosMovimentosBuilder {

    public TsBancosMovimentosEntity builderEntity(BcBancoMovimientoCreationRequestDto model,
                                                  Long idData, Long idEmpresa) {

        return TsBancosMovimentosEntity.builder()
                .idMovimiento(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(DateUtils.toLocalDate(model.getFechaDocumento()))
                .fechaRegistro(DateUtils.toLocalDate(model.getFechaRegistro()))
                .valor(model.getValor())
                .concepto(model.getConcepto())
                .nombre(model.getNombre())
                .observaciones(model.getObservaciones())
                .fisico(model.getFisico())
                .codigoSerie(model.getCodigoSerie())
                .numeroComprobante(model.getNumeroComprobante())
                .idConciliacion(model.getIdConciliacion())
                .tipomovbc(model.getTipomovbc())
                .chejercant(model.getChejercant())
                .tsEntidadEntity(builderEntidad(model.getIdEntidad()))
                .vtClienteEntity(builderCliente(model.getIdTercero()))
                .build();
    }

    public TsBancosMovimentosEntity builderUpdateEntity(BcBancoMovimientoCreationRequestDto model,
                                                        TsBancosMovimentosEntity item) {

        return TsBancosMovimentosEntity.builder()
                .idMovimiento(item.getIdMovimiento())
                .idData(item.getIdData())
                .sucursal(model.getSucursal())
                .idEmpresa(item.getIdEmpresa())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(DateUtils.toLocalDate(model.getFechaDocumento()))
                .fechaRegistro(DateUtils.toLocalDate(model.getFechaRegistro()))
                .valor(model.getValor())
                .concepto(model.getConcepto())
                .nombre(model.getNombre())
                .observaciones(model.getObservaciones())
                .fisico(model.getFisico())
                .codigoSerie(model.getCodigoSerie())
                .numeroComprobante(model.getNumeroComprobante())
                .idConciliacion(model.getIdConciliacion())
                .tipomovbc(model.getTipomovbc())
                .chejercant(model.getChejercant())
                .tsEntidadEntity(builderEntidad(model.getIdEntidad()))
                .vtClienteEntity(builderCliente(model.getIdTercero()))
                .build();
    }

    public BcBancoMovimientoCreationResponseDto builderResponse(TsBancosMovimentosEntity model) {
        return BcBancoMovimientoCreationResponseDto.builder()
                .idMovimiento(model.getIdMovimiento())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .movimiento(model.getMovimiento())
                .fechaDocumento(DateUtils.toString(model.getFechaDocumento()))
                .fechaRegistro(DateUtils.toString(model.getFechaRegistro()))
                .valor(model.getValor())
                .concepto(model.getConcepto())
                .nombre(model.getNombre())
                .observaciones(model.getObservaciones())
                .fisico(model.getFisico())
                .codigoSerie(model.getCodigoSerie())
                .numeroComprobante(model.getNumeroComprobante())
                .idConciliacion(model.getIdConciliacion())
                .tipomovbc(model.getTipomovbc())
                .chejercant(model.getChejercant())
                .sucursal(model.getSucursal())
                .build();
    }


    private GeTerceroEntity builderProveedor(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

    private GeTerceroEntity builderCliente(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

    private TsEntidadEntity builderEntidad(UUID idEntidad) {
        return TsEntidadEntity.builder()
                .idEntidad(idEntidad)
                .build();
    }

}
