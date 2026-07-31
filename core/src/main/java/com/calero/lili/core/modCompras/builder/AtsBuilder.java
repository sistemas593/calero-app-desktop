package com.calero.lili.core.modCompras.builder;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.service.CpImpuestoAtsProjection;
import com.calero.lili.core.modImpuestosAnexos.ats.Air;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleAir;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modImpuestosAnexos.ats.Iva;
import com.calero.lili.core.modImpuestosAnexos.ats.Pago;
import com.calero.lili.core.modImpuestosAnexos.ats.PagoExterior;
import com.calero.lili.core.utils.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class AtsBuilder {

    private final FormatoValores formatoValores;
    private final ObjectMapper objectMapper;

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

    public DetalleCompras builderDetalleCompra(CpImpuestoAtsProjection model) {
        return DetalleCompras.builder()
                .codSustento(model.getCodigoSustento().getCodigo())
                .tpIdProv(validacionTipoId(model.getTipoIdProv()))
                .idProv(model.getIdProv())
                .tipoComprobante(model.getTipoComprobante().getCodigo())
                .parteRel("NO")
                .fechaRegistro(DateUtils.toString(model.getFechaRegistro()))
                .establecimiento(model.getSerie().substring(0, 3))
                .puntoEmision(model.getSerie().substring(3, 6))
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .autorizacion(model.getAutorizacion())
                .baseNoGraIva(formatoValores.convertirBigDecimalToString(model.getBaseNoGraIva()))
                .baseImponible(formatoValores.convertirBigDecimalToString(model.getBaseImponible()))
                .baseImpGrav(formatoValores.convertirBigDecimalToString(model.getBaseImpGrav()))
                .baseImpExe(formatoValores.convertirBigDecimalToString(model.getBaseImpExe()))
                .montoIva(formatoValores.convertirBigDecimalToString(model.getMontoIva()))
                .pagoExterior(builderPagoExterior(model.getPagoLocExt(), model.getPagoExterior()))
                .montoIce(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetBien10(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetServ20(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valorRetBienes(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetServ50(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valorRetServicios(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .valRetServ100(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totbasesImpReemb(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .formasDePago(builderFormaPago(model.getFormasPago()))
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


    public Pago builderFormaPago(String jsonPagos) {
        List<FormasPagoSri> formasPagoSri = getFormaPagoSri(jsonPagos, new TypeReference<List<FormasPagoSri>>() {
        });
        return Pago.builder()
                .formaPago(formasPagoSri.stream()
                        .map(fp -> fp.getFormaPago().getCodigo())
                        .toList())
                .build();

    }

    public Pago builderFormaDePago(List<FormasPagoSri> formasPagoSri) {
        return Pago.builder()
                .formaPago(formasPagoSri.stream()
                        .map(fp -> fp.getFormaPago().getCodigo())
                        .toList())
                .build();
    }

    private PagoExterior builderPagoExterior(String pagoCode, String json) {

        com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior model = getFormaPagoExterior(json,
                com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior.class);

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
            pagoExterior.setAplicConvDobTrib(model.getAplicConvDobTrib());
            pagoExterior.setPagExtSujRetNorLeg(model.getPagExtSujRetNorLeg());
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

    private <T> T getFormaPagoExterior(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Error al convertir el JSON a " + clazz.getSimpleName(), e);
        }
    }

    private <T> T getFormaPagoSri(String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error al convertir el JSON.", e);
        }
    }

}
