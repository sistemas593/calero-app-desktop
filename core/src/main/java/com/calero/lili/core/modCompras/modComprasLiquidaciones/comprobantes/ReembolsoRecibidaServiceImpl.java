package com.calero.lili.core.modCompras.modComprasLiquidaciones.comprobantes;

import com.calero.lili.core.comprobantes.builder.AutorizacionBuilder;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosValoresEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.LiquidacionReembolsosRepository;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosItemsRepository;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosValoresEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentasReembolsoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReembolsoRecibidaServiceImpl {

    private final LiquidacionReembolsosRepository liquidacionReembolsosRepository;
    private final VtVentasReembolsoRepository vtVentasReembolsoRepository;
    private final AutorizacionBuilder autorizacionBuilder;
    private final GeImpuestosItemsRepository geImpuestosItemsRepository;


    public Boolean verificarExisteDocumentoElectronicoLiqReembolsoBdd(Long idData, Long idEmpresa, String claveAcceso) {

        if (validarDocumento(claveAcceso)) {

            Optional<CpLiquidacionesReembolsosEntity> existingLiquidacion =
                    liquidacionReembolsosRepository.findByAutorizacion(idData, idEmpresa, claveAcceso);
            if (existingLiquidacion.isPresent()) {
                log.info("Ya se encuentra registrado el reembolso de liquidación");
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean verificarExisteDocumentoElectronicoVentaReembolsoBdd(Long idData, Long idEmpresa, String claveAcceso) {

        if (validarDocumento(claveAcceso)) {

            Optional<VtVentaReembolsosEntity> vtVentaReembolsos = vtVentasReembolsoRepository.findByAutorizacion(idData, idEmpresa, claveAcceso);
            if (vtVentaReembolsos.isPresent()) {
                log.info("Ya se encuentra registrado el reembolso de ventas");
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean validarDocumento(String claveAcceso) {

        if (claveAcceso.startsWith("01", 8) ||
                claveAcceso.startsWith("05", 8)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;


    }

    public Boolean guardarComprobanteLiqReembolso(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {


        String tipoDocumento = tipoDocumento(autorizacionDto.getNumeroAutorizacion());

        switch (tipoDocumento) {

            case "FAC" -> {
                CpLiquidacionesReembolsosEntity factura = validarLiquidacionReembolsoFactura(idData, idEmpresa, autorizacionDto);
                if (Objects.nonNull(factura)) {
                    factura.setCreatedBy(usuario);
                    factura.setCreatedDate(LocalDateTime.now());
                    liquidacionReembolsosRepository.save(factura);
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }

            case "NDB" -> {
                CpLiquidacionesReembolsosEntity notaDebito = validarLiquidacionReembolsoNotaDebito(idData, idEmpresa, autorizacionDto);
                if (Objects.nonNull(notaDebito)) {
                    notaDebito.setCreatedBy(usuario);
                    notaDebito.setCreatedDate(LocalDateTime.now());
                    liquidacionReembolsosRepository.save(notaDebito);
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        }

        return Boolean.FALSE;

    }


    private CpLiquidacionesReembolsosEntity validarLiquidacionReembolsoFactura(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto) {

        try {
            Factura documento = null;
            if (autorizacionDto.getFormatoDocumento().equals("1")) {
                documento = XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), Factura.class);
            } else {
                documento = autorizacionDto.getFactura();
            }
            CpLiquidacionesReembolsosEntity reembolso = autorizacionBuilder.builderLiquidacionReembolsoFactura(idData, idEmpresa, autorizacionDto, documento);
            setearTarifaValoresLiqReembolso(reembolso);
            return reembolso;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private CpLiquidacionesReembolsosEntity validarLiquidacionReembolsoNotaDebito(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto) {

        try {
            NotaDebito documento = null;
            if (autorizacionDto.getFormatoDocumento().equals("1")) {
                documento = XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), NotaDebito.class);
            } else {
                documento = autorizacionDto.getNotaDebito();
            }

            CpLiquidacionesReembolsosEntity reembolso = autorizacionBuilder.builderLiquidacionReembolsoNotaDebito(idData, idEmpresa, autorizacionDto, documento);
            setearTarifaValoresLiqReembolso(reembolso);
            return reembolso;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    public Boolean guardarComprobanteVentaReembolso(Autorizacion autorizacionDto, Long idData, Long idEmpresa, String usuario) {

        VtVentaReembolsosEntity reembolso = validarVentaReembolso(autorizacionDto, idData, idEmpresa);
        if (Objects.nonNull(reembolso)) {
            reembolso.setCreatedBy(usuario);
            reembolso.setCreatedDate(LocalDateTime.now());
            vtVentasReembolsoRepository.save(reembolso);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

    private VtVentaReembolsosEntity validarVentaReembolso(Autorizacion autorizacionDto, Long idData, Long idEmpresa) {

        try {
            Factura documento = XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), Factura.class);
            VtVentaReembolsosEntity reembolso = autorizacionBuilder.builderVentaReembolso(autorizacionDto, documento, idData, idEmpresa);
            setearTarifaValoresVentaReembolso(reembolso);
            return reembolso;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private String tipoDocumento(String claveAcceso) {

        String tipo = claveAcceso.substring(8, 10);
        if (tipo.equals("01")) {
            return "FAC";
        } else {
            return "NDB";
        }
    }

    private void setearTarifaValoresVentaReembolso(VtVentaReembolsosEntity reembolso) {


        Map<String, VtVentaReembolsosValoresEntity> mapa = reembolso.getReembolsosValores()
                .stream()
                .collect(Collectors.toMap(
                        imp -> imp.getCodigo() + "-" + imp.getCodigoPorcentaje(),
                        imp -> imp,
                        (existente, nuevo) -> existente
                ));

        for (Map.Entry<String, VtVentaReembolsosValoresEntity> entry : mapa.entrySet()) {

            String clave = entry.getKey();
            VtVentaReembolsosValoresEntity valorReembolso = entry.getValue();
            Optional<GeImpuestosEntity> impuesto = geImpuestosItemsRepository.findCodigoAndCodigoPorcentaje(clave);
            impuesto.ifPresent(geImpuestosEntity -> valorReembolso.setTarifa(geImpuestosEntity.getTarifa()));
        }
    }


    private void setearTarifaValoresLiqReembolso(CpLiquidacionesReembolsosEntity reembolso) {
        Map<String, CpLiquidacionesReembolsosValoresEntity> mapa = reembolso.getReembolsosValores()
                .stream()
                .collect(Collectors.toMap(
                        imp -> imp.getCodigo() + "-" + imp.getCodigoPorcentaje(),
                        imp -> imp,
                        (existente, nuevo) -> existente
                ));


        for (Map.Entry<String, CpLiquidacionesReembolsosValoresEntity> entry : mapa.entrySet()) {

            String clave = entry.getKey();
            CpLiquidacionesReembolsosValoresEntity valorReembolso = entry.getValue();
            Optional<GeImpuestosEntity> impuesto = geImpuestosItemsRepository.findCodigoAndCodigoPorcentaje(clave);
            impuesto.ifPresent(geImpuestosEntity -> valorReembolso.setTarifa(geImpuestosEntity.getTarifa()));
        }
    }

}
