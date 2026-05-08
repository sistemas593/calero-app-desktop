package com.calero.lili.core.modCxC.XcFacturas.builder;

import com.calero.lili.core.modCxC.XcFacturas.XcFacturasEntity;
import com.calero.lili.core.modCxC.XcFacturas.dto.RequestXcFacturasDto;
import com.calero.lili.core.modCxC.XcFacturas.dto.ResponseXcFacturasDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

@Component
public class XcFacturasBuilder {

    public XcFacturasEntity builderEntity(RequestXcFacturasDto model,
                                          Long idData, Long idEmpresa, UUID idFactura) {
        return XcFacturasEntity.builder()
                .idFactura(idFactura)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .periodo(model.getPeriodo())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .valor(model.getValor())
                .registrosAplicados(model.getRegistrosAplicados())
                .pagos(Objects.nonNull(model.getPagos())
                        ? model.getPagos()
                        : BigDecimal.ZERO)
                .retencionesIva(model.getRetencionesIva())
                .retencionesRenta(model.getRetencionesRenta())
                .notasCredito(model.getNotasCredito())
                .saldo(model.getSaldo())
                .anulada(Boolean.FALSE)
                .cliente(builderCliente(model.getIdTercero()))
                .tipoDocumento(model.getTipoDocumento().name())
                .build();
    }

    public XcFacturasEntity builderEntityFac(CreationFacturaRequestDto model,
                                             Long idData, Long idEmpresa, UUID idFactura, GeTerceroEntity tercero) {
        return XcFacturasEntity.builder()
                .idFactura(idFactura)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .periodo("")
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .valor(model.getTotal())
                .registrosAplicados(BigInteger.valueOf(0))
                .pagos(BigDecimal.ZERO)
                .retencionesIva(BigDecimal.ZERO)
                .retencionesRenta(BigDecimal.ZERO)
                .notasCredito(BigDecimal.ZERO)
                .saldo(model.getTotal())
                .anulada(Boolean.FALSE)
                .cliente(tercero)
                .tipoDocumento("FAC")
                .build();
    }


    public XcFacturasEntity builderUpdateEntity(RequestXcFacturasDto model, XcFacturasEntity item) {
        return XcFacturasEntity.builder()
                .idFactura(item.getIdFactura())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .periodo(model.getPeriodo())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .valor(model.getValor())
                .registrosAplicados(model.getRegistrosAplicados())
                .pagos(Objects.nonNull(model.getPagos())
                        ? model.getPagos()
                        : item.getPagos())
                .retencionesIva(model.getRetencionesIva())
                .retencionesRenta(model.getRetencionesRenta())
                .notasCredito(model.getNotasCredito())
                .saldo(Objects.nonNull(model.getSaldo())
                        ? model.getSaldo()
                        : item.getSaldo())
                .anulada(item.getAnulada())
                .cliente(builderCliente(model.getIdTercero()))
                .tipoDocumento(model.getTipoDocumento().name())
                .build();
    }


    public ResponseXcFacturasDto builderResponse(XcFacturasEntity model) {
        return ResponseXcFacturasDto.builder()
                .idFactura(model.getIdFactura())
                .sucursal(model.getSucursal())
                .periodo(model.getPeriodo())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(Objects.nonNull(model.getFechaEmision())
                        ? DateUtils.toString(model.getFechaEmision())
                        : null)
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .valor(model.getValor())
                .registrosAplicados(model.getRegistrosAplicados())
                .pagos(model.getPagos())
                .retencionesIva(model.getRetencionesIva())
                .retencionesRenta(model.getRetencionesRenta())
                .notasCredito(model.getNotasCredito())
                .saldo(model.getSaldo())
                .anulada(model.getAnulada())
                .build();
    }

    private GeTerceroEntity builderCliente(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

}
