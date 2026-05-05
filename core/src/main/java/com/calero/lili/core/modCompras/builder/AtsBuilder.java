package com.calero.lili.core.modCompras.builder;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modImpuestosAnexos.ats.Air;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleAir;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modImpuestosAnexos.ats.Iva;
import com.calero.lili.core.modImpuestosAnexos.ats.Pago;
import com.calero.lili.core.modImpuestosAnexos.ats.PagoExterior;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class AtsBuilder {

    private final FormatoValores formatoValores;

    public Iva builderAtsWithRetencion(List<DetalleCompras> detalleCompras, AdEmpresaEntity model, String periodo) {

        String[] partes = periodo.split("-");
        return Iva.builder()
                .TipoIDInformante("R")
                .IdInformante(model.getRuc())
                .razonSocial(model.getRazonSocial())
                .Anio(partes[1])
                .Mes(partes[0])
                .totalVentas(new BigDecimal("0.00"))
                .codigoOperativo("IVA")
                .detalleCompras(detalleCompras)
                .build();
    }

    public DetalleCompras builderDetalleRetencion(CpImpuestosEntity model, CpRetencionesEntity item) {
        return DetalleCompras.builder()
                .codSustento(model.getSustento().getSustento())
                .tpIdProv(model.getTipoProveedor())
                .idProv(model.getTercero().getNumeroIdentificacion())
                .tipoComprobante(model.getDocumento().getCodigoDocumento())
                .parteRel("NO")
                .fechaRegistro(DateUtils.toString(model.getFechaRegistro()))
                .establecimiento(model.getSucursal())
                .puntoEmision(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .autorizacion(model.getNumeroAutorizacion())
                .baseNoGraIva(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .baseImponible(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .baseImpGrav(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .baseImpExe(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .montoIva(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .pagoExterior(builderPagoExterior(model.getPagoLocExt(), model.getPagoExterior()))
                .fechaEmiRet1(DateUtils.toString(item.getFechaEmisionRetencion()))
                .montoIce(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .valRetBien10(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .valRetServ20(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .valorRetBienes(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .valorRetServicios(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .valRetServ100(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .totbasesImpReemb(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO))
                .build();
    }

    public DetalleCompras builderDetalleCompra(CpImpuestosEntity model) {
        List<CpImpuestosValoresEntity> valores = model.getValoresEntity();

        // Base no objeto de IVA — codigoPorcentaje "6"
        BigDecimal baseNoGraIva = valores.stream()
                .filter(v -> "2".equals(v.getCodigo()) && "6".equals(v.getCodigoPorcentaje()))
                .map(CpImpuestosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Base imponible tarifa 0% — codigoPorcentaje "0"
        BigDecimal baseImponible = valores.stream()
                .filter(v -> "2".equals(v.getCodigo()) && "0".equals(v.getCodigoPorcentaje()))
                .map(CpImpuestosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Base gravada acumulada (tarifas 12%, 5%, 8%, 15%) — codigoPorcentaje "2", "4", "5", "8"
        BigDecimal baseImpGrav = valores.stream()
                .filter(v -> "2".equals(v.getCodigo()) && List.of("2", "4", "5", "8").contains(v.getCodigoPorcentaje()))
                .map(CpImpuestosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Base exenta de IVA — codigoPorcentaje "7"
        BigDecimal baseImpExe = valores.stream()
                .filter(v -> "2".equals(v.getCodigo()) && "7".equals(v.getCodigoPorcentaje()))
                .map(CpImpuestosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Monto IVA: suma del valor para las tarifas gravadas — codigoPorcentaje "2", "4", "5", "8"
        BigDecimal montoIva = valores.stream()
                .filter(v -> "2".equals(v.getCodigo()) && List.of("4", "5", "8").contains(v.getCodigoPorcentaje()))
                .map(CpImpuestosValoresEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DetalleCompras.builder()
                .codSustento(model.getSustento().getCodigoSustento())
                .tpIdProv(validacionTipoId(model.getTercero().getTipoIdentificacion()))
                .idProv(model.getTercero().getNumeroIdentificacion())
                .tipoComprobante(model.getDocumento().getCodigoDocumento())
                .parteRel("NO")
                .fechaRegistro(DateUtils.toString(model.getFechaRegistro()))
                .establecimiento(model.getSerie().substring(0, 3))
                .puntoEmision(model.getSerie().substring(3, 6))
                .secuencial(new java.math.BigInteger(model.getSecuencial()).toString())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .autorizacion(model.getNumeroAutorizacion())
                .baseNoGraIva(formatoValores.convertirBigDecimalToString(baseNoGraIva))
                .baseImponible(formatoValores.convertirBigDecimalToString(baseImponible))
                .baseImpGrav(formatoValores.convertirBigDecimalToString(baseImpGrav))
                .baseImpExe(formatoValores.convertirBigDecimalToString(baseImpExe))
                .montoIva(formatoValores.convertirBigDecimalToString(montoIva))
                .pagoExterior(builderPagoExterior(model.getPagoLocExt(), model.getPagoExterior()))
                .montoIce(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetBien10(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetServ20(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valorRetBienes(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetServ50(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valorRetServicios(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetServ100(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totbasesImpReemb(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .formasDePago(builderFormaDePago(model.getFormasPagoSri()))
                .build();
    }

    private String validacionTipoId(String tipoIdentificacion) {
        switch (tipoIdentificacion) {
            case "R" -> {
                return "01";
            }
            case "C" -> {
                return "02";
            }
            case "P" -> {
                return "03";
            }
            case null, default -> throw new GeneralException("No existe tipo de identificación para el proveedor");
        }
    }

    public Pago builderFormaDePago(List<FormasPagoSri> formasPagoSri) {
        return Pago.builder()
                .formaPago(formasPagoSri.stream()
                        .map(FormasPagoSri::getFormaPago)
                        .toList())
                .build();
    }

    private PagoExterior builderPagoExterior(String pagoCode, com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior model) {

        if (pagoCode.equals("01")) {
            return PagoExterior.builder()
                    .pagoLocExt(pagoCode)
                    .aplicConvDobTrib("NA")
                    .paisEfecPago("NA")
                    .pagExtSujRetNorLeg("NA")
                    .build();
        } else {

            PagoExterior pagoExterior = new PagoExterior();

            pagoExterior.setPagoLocExt(pagoCode);
            pagoExterior.setTipoRegi(model.getTipoRegi());
            pagoExterior.setAplicConvDobTrib(model.getAplicConvDobTrib() ? "SI" : "NO");
            pagoExterior.setPagExtSujRetNorLeg(model.getPagExtSujRetNorLeg() ? "SI" : "NO");
            // Este dato no va en el ATS, pero se usa en las retenciones electronicas.
            //pagoExterior.setPagoRegFis(model.getPagoRegFis() ? "SI" : "NO");
            pagoExterior.setPaisEfecPago(model.getPaisEfecPago());

            switch (model.getTipoRegi()) {
                case "01" -> pagoExterior.setPaisEfecPagoGen(model.getPaisEfecPagoGen());

                case "02" -> pagoExterior.setPaisEfecPagoParFis(model.getPaisEfecPagoParFis());

                case "03" -> pagoExterior.setDenopagoRegFis(model.getDenopagoRegFis());
            }

            return pagoExterior;
        }

    }


    public Air builderAir(List<DetalleAir> detalleAirs) {
        return Air.builder()
                .detalleAirs(detalleAirs)
                .build();
    }

    public DetalleAir builderDetalleAir(CpImpuestosCodigosEntity model) {
        return DetalleAir.builder()
                .codRetAir(model.getCodigoRetencion())
                .baseImpAir(model.getBaseImponible())
                .porcentajeAir(model.getPorcentajeRetener())
                .valRetAir(model.getValorRetenido())
                .build();
    }

}
