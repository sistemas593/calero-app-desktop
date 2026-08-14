package com.calero.lili.core.modCompras.builder;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.enums.PagoLocalExterior;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modCompras.service.CpImpuestoAtsProjection;
import com.calero.lili.core.modCompras.service.CpImpuestosCodigosAtsProjection;
import com.calero.lili.core.modImpuestosAnexos.ats.Air;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleAir;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modImpuestosAnexos.ats.Iva;
import com.calero.lili.core.modImpuestosAnexos.ats.Pago;
import com.calero.lili.core.modImpuestosAnexos.ats.PagoExterior;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AtsBuilder {

    private final FormatoValores formatoValores;
    private final JsonUtils jsonUtils;

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


        BigDecimal total = model.getBaseNoGraIva().add(model.getBaseImponible())
                .add(model.getBaseImpGrav()).add(model.getBaseImpExe()).add(model.getMontoIva());

        DetalleCompras detalleCompras = DetalleCompras.builder()
                .codSustento(model.getCodigoSustento().getCodigo())
                .tpIdProv(validacionTipoId(model.getTipoIdProv()))
                .idProv(model.getIdProv())
                .tipoComprobante(model.getCodigoDocumento().getCodigo())
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
                .build();

        if (total.compareTo(new BigDecimal("500")) > 0) {
            detalleCompras.setFormasDePago(builderFormaPago(model.getFormasPago()));
        }

        return detalleCompras;
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

        if (Objects.nonNull(jsonPagos)) {
            List<FormasPagoSri> formasPagoSri = jsonUtils
                    .convetirStringObjecto(jsonPagos, new TypeReference<List<FormasPagoSri>>() {
                    });
            return Pago.builder()
                    .formaPago(formasPagoSri.stream()
                            .map(fp -> fp.getFormaPago().getCodigo())
                            .toList())
                    .build();
        }
        return null;
    }

    private PagoExterior builderPagoExterior(PagoLocalExterior pagoLocExt, String json) {


        if (pagoLocExt.equals(PagoLocalExterior.L)) {
            PagoExterior pagoExterior01 = new PagoExterior();
            pagoExterior01.setPagoLocExt(pagoLocExt.getCodigo());
            pagoExterior01.setAplicConvDobTrib("NA");
            pagoExterior01.setPaisEfecPago("NA");
            pagoExterior01.setPagExtSujRetNorLeg("NA");
            return pagoExterior01;

        } else {

            if (Objects.nonNull(json)) {


                PagoExterior pagoExteriorOtroCodigo = new PagoExterior();

                com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior model = jsonUtils.convertirListStringObjecto(json,
                        com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior.class);


                pagoExteriorOtroCodigo.setPagoLocExt(pagoLocExt.getCodigo());
                pagoExteriorOtroCodigo.setTipoRegi(model.getTipoRegi());
                pagoExteriorOtroCodigo.setAplicConvDobTrib(model.getAplicConvDobTrib());
                pagoExteriorOtroCodigo.setPagExtSujRetNorLeg(model.getPagExtSujRetNorLeg());
                pagoExteriorOtroCodigo.setPaisEfecPago(model.getPaisEfecPago());

                switch (model.getTipoRegi()) {
                    case "01" -> pagoExteriorOtroCodigo.setPaisEfecPagoGen(model.getPaisEfecPagoGen());

                    case "02" -> pagoExteriorOtroCodigo.setPaisEfecPagoParFis(model.getPaisEfecPagoParFis());

                    case "03" -> pagoExteriorOtroCodigo.setDenopagoRegFis(model.getDenopagoRegFis());
                }

                return pagoExteriorOtroCodigo;
            }

        }


        return null;

    }


    public Air builderAir(List<DetalleAir> detalleAirs) {
        return Air.builder()
                .detalleAirs(detalleAirs)
                .build();
    }

    public DetalleAir builderDetalleAir(CpImpuestosCodigosAtsProjection model) {
        return DetalleAir.builder()
                .codRetAir(model.getCodigoRetencion())
                .baseImpAir(model.getBaseImponible())
                .porcentajeAir(model.getPorcentajeRetener())
                .valRetAir(model.getValorRetenido())
                .build();
    }
}
