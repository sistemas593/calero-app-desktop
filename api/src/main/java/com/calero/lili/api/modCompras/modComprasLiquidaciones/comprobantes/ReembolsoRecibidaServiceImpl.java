package com.calero.lili.api.modCompras.modComprasLiquidaciones.comprobantes;

import com.calero.lili.api.comprobantes.builder.AutorizacionBuilder;
import com.calero.lili.api.comprobantes.message.MensajeComprobante;
import com.calero.lili.api.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.api.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.api.comprobantes.utils.XmlUtils;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.LiquidacionReembolsosRepository;
import com.calero.lili.api.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.api.modVentas.reembolsos.VtVentasReembolsoRepository;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReembolsoRecibidaServiceImpl {

    private final LiquidacionReembolsosRepository liquidacionReembolsosRepository;
    private final VtVentasReembolsoRepository vtVentasReembolsoRepository;
    private final AutorizacionBuilder autorizacionBuilder;


    public Boolean verificarExisteDocumentoElectronicoLiqReembolsoBdd(String claveAcceso) {

        validarDocumento(claveAcceso);
        Optional<CpLiquidacionesReembolsosEntity> existingLiquidacion =
                liquidacionReembolsosRepository.findByAutorizacion(claveAcceso);
        if (existingLiquidacion.isPresent()) {
            log.info("Ya se encuentra registrado el reembolso de liquidación");
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean verificarExisteDocumentoElectronicoVentaReembolsoBdd(String claveAcceso) {

        validarDocumento(claveAcceso);
        Optional<VtVentaReembolsosEntity> vtVentaReembolsos = vtVentasReembolsoRepository.findByAutorizacion(claveAcceso);
        if (vtVentaReembolsos.isPresent()) {
            log.info("Ya se encuentra registrado el reembolso de ventas");
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void validarDocumento(String claveAcceso) {

        if (!(claveAcceso.startsWith("01", 8))) {
            throw new GeneralException(MensajeComprobante.ERR_NOT_FACTURA);
        }
    }

    public Boolean guardarComprobanteLiqReembolso(Autorizacion autorizacionDto) {

        System.out.println(autorizacionDto.getComprobante());

        CpLiquidacionesReembolsosEntity factura = validarLiquidacionReembolso(autorizacionDto);
        if (Objects.nonNull(factura)) {
            liquidacionReembolsosRepository.save(factura);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }


    private CpLiquidacionesReembolsosEntity validarLiquidacionReembolso(Autorizacion autorizacionDto) {

        try {
            Factura documento = XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), Factura.class);
            return autorizacionBuilder.builderLiquidacionReembolso(autorizacionDto, documento);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    public Boolean guardarComprobanteVentaReembolso(Autorizacion autorizacionDto) {

        System.out.println(autorizacionDto.getComprobante());

        VtVentaReembolsosEntity reembolso = validarVentaReembolso(autorizacionDto);
        if (Objects.nonNull(reembolso)) {
            vtVentasReembolsoRepository.save(reembolso);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

    private VtVentaReembolsosEntity validarVentaReembolso(Autorizacion autorizacionDto) {

        try {
            Factura documento = XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), Factura.class);
            return autorizacionBuilder.builderVentaReembolso(autorizacionDto, documento);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

}
