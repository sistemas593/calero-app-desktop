package com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.builder;

import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.TsCajasTpvEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvCreationResponseDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TsCajasTpvBuilder {

    public TsCajasTpvEntity builderEntity(BcCajaTpvCreationRequestDto model, Long idData, Long idEmpresa) {
        return TsCajasTpvEntity.builder()
                .idCajaTpv(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .fechaApertura(DateUtils.toLocalDate(model.getFechaApertura()))
                .fechaCierre(DateUtils.toLocalDate(model.getFechaCierre()))
                .sucursal(model.getSucursal())
                .efectivo(model.getEfectivo())
                .cheque(model.getCheque())
                .tarjeta(model.getTarjeta())
                .transferencia(model.getTransferencia())
                .retencion(model.getRetencion())
                .otras(model.getOtras())
                .credito(model.getCredito())
                .saldoUtilizado(model.getSaldoUtilizado())
                .base(model.getBase())
                .iva(model.getIva())
                .ice(model.getIce())
                .propina(model.getPropina())
                .build();
    }

    public TsCajasTpvEntity builderUpdateEntity(BcCajaTpvCreationRequestDto model, TsCajasTpvEntity item) {
        return TsCajasTpvEntity.builder()
                .idCajaTpv(item.getIdCajaTpv())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .fechaApertura(DateUtils.toLocalDate(model.getFechaApertura()))
                .fechaCierre(DateUtils.toLocalDate(model.getFechaCierre()))
                .sucursal(model.getSucursal())
                .efectivo(model.getEfectivo())
                .cheque(model.getCheque())
                .tarjeta(model.getTarjeta())
                .transferencia(model.getTransferencia())
                .retencion(model.getRetencion())
                .otras(model.getOtras())
                .credito(model.getCredito())
                .saldoUtilizado(model.getSaldoUtilizado())
                .base(model.getBase())
                .iva(model.getIva())
                .ice(model.getIce())
                .propina(model.getPropina())
                .build();
    }

    public BcCajaTpvCreationResponseDto builderResponse(TsCajasTpvEntity model) {
        return BcCajaTpvCreationResponseDto.builder()
                .idCajaTpv(model.getIdCajaTpv())
                .fechaApertura(DateUtils.toString(model.getFechaApertura()))
                .fechaCierre(DateUtils.toString(model.getFechaCierre()))
                .sucursal(model.getSucursal())
                .efectivo(model.getEfectivo())
                .cheque(model.getCheque())
                .tarjeta(model.getTarjeta())
                .transferencia(model.getTransferencia())
                .retencion(model.getRetencion())
                .otras(model.getOtras())
                .credito(model.getCredito())
                .saldoUtilizado(model.getSaldoUtilizado())
                .base(model.getBase())
                .iva(model.getIva())
                .ice(model.getIce())
                .propina(model.getPropina())
                .build();
    }
}
