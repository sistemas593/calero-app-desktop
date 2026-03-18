package com.calero.lili.core.modCxP.XpFacturas.builder;

import com.calero.lili.core.modCxP.XpFacturas.XpFacturasEntity;
import com.calero.lili.core.modCxP.XpFacturas.dto.XpFacturasRequestDto;
import com.calero.lili.core.modCxP.XpFacturas.dto.XpFacturasResponseDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Component
public class XpFacturasBuilder {

    public XpFacturasEntity builderEntity(XpFacturasRequestDto model, Long idData, Long idEmpresa) {
        return XpFacturasEntity.builder()
                .idFactura(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .periodo(model.getPeriodo())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaRegistro(DateUtils.toLocalDate(model.getFechaRegistro()))
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
                .anulada(model.getAnulada())
                .proveedor(builderProveedor(model.getIdTercero()))
                .tipoDocumento(model.getTipoDocumento().name())
                .build();
    }

    public XpFacturasEntity builderUpdateEntity(XpFacturasRequestDto model, XpFacturasEntity item) {
        return XpFacturasEntity.builder()
                .idFactura(item.getIdFactura())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .periodo(model.getPeriodo())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaRegistro(DateUtils.toLocalDate(model.getFechaRegistro()))
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
                .anulada(model.getAnulada())
                .proveedor(builderProveedor(model.getIdTercero()))
                .tipoDocumento(model.getTipoDocumento().name())
                .build();
    }


    public XpFacturasResponseDto builderResponse(XpFacturasEntity model) {
        return XpFacturasResponseDto.builder()
                .idFactura(model.getIdFactura())
                .sucursal(model.getSucursal())
                .periodo(model.getPeriodo())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaRegistro(Objects.nonNull(model.getFechaRegistro())
                        ? DateUtils.toString(model.getFechaRegistro())
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
                .proveedor(builderProveedorResponse(model.getProveedor()))
                .build();
    }

    private XpFacturasResponseDto.Proveedor builderProveedorResponse(GeTerceroEntity proveedor) {
        return XpFacturasResponseDto.Proveedor.builder()
                .idTercero(proveedor.getIdTercero())
                .proveedor(proveedor.getTercero())
                .build();
    }

    private GeTerceroEntity builderProveedor(UUID idTercero) {
        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }

}
