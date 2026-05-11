package com.calero.lili.core.comprobantes.services;

import com.calero.lili.core.comprobantes.builder.AutorizacionBuilder;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.message.MensajeComprobante;
import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.Impuesto;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modCompras.impuestosXml.CpImpuestosFacturasOneProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.core.modTerceros.builder.GeTercerosTipoBuilder;
import com.calero.lili.core.modVentasRetenciones.VentasRetencionesRepository;
import com.calero.lili.core.modVentasRetenciones.VtRetencionesEntity;
import com.calero.lili.core.modVentasRetenciones.projection.DeRecibidasRetencionesProjection;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@XmlRootElement
@Slf4j
public class DeRecibidasComponentsServiceImpl {

    private final CpImpuestosRepository cpImpuestosRepository;
    private final VentasRetencionesRepository ventasRetencionesRepository;
    private final AutorizacionBuilder autorizacionBuilder;
    private final GeTercerosRepository geTercerosRepository;
    private final GeTercerosTipoRepository geTercerosTipoRepository;
    private final GeTercerosTipoBuilder geTercerosTipoBuilder;
    private final AdEmpresasRepository adEmpresasRepository;


    public Boolean verificarExisteDocumentoElectronicoBdd(Long idData, Long idEmpresa, String claveAcceso) {

        if ((claveAcceso.startsWith("01", 8))
                || (claveAcceso.startsWith("04", 8))
                || (claveAcceso.startsWith("05", 8))) {
            Optional<CpImpuestosFacturasOneProjection> existingFactura = cpImpuestosRepository.findExistByNumeroAutorizacion(idData, idEmpresa, claveAcceso);
            if (existingFactura.isPresent()) {
                log.info("Ya se encuentra registrado la factura, nota de credito o nota de debito");
                return Boolean.TRUE;
            }
        }
        if ((claveAcceso.startsWith("07", 8))) {
            Optional<DeRecibidasRetencionesProjection> existingFactura = ventasRetencionesRepository.findExistByNumeroAutorizacion(idData, idEmpresa, claveAcceso);
            if (existingFactura.isPresent()) {
                log.info("Ya se encuentra registrado la retención");
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public String guardarComprobante(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {

        String numeroAutorizacion = autorizacionDto.getNumeroAutorizacion();
        System.out.println(autorizacionDto.getComprobante());

        String message = validacionNoLiquidacion(autorizacionDto.getNumeroAutorizacion());

        if (message.isEmpty()) {

            /* FACTURAS */

            if (numeroAutorizacion.startsWith("01", 8)) {
                return saveFactura(idData, idEmpresa, autorizacionDto, usuario);
            }

            /* NOTA DE CREDITO */

            if (numeroAutorizacion.startsWith("04", 8)) {
                return saveNotaCredito(idData, idEmpresa, autorizacionDto, usuario);
            }

            /* NOTA DE DEBITO */

            if (numeroAutorizacion.startsWith("05", 8)) {

                return saveNotaDebito(idData, idEmpresa, autorizacionDto, usuario);

            }
            /* RETENCIÓN */
            return validacionRetencion(idData, idEmpresa, autorizacionDto, numeroAutorizacion, usuario);
        }
        return message;
    }

    private String saveNotaDebito(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {
        NotaDebito documento = null;

        if (autorizacionDto.getFormatoDocumento().equals("1")) {
            documento = XmlUtils.getNotaDebito(autorizacionDto);
        } else {
            documento = autorizacionDto.getNotaDebito();
        }

        if (Objects.nonNull(documento)) {

            String message = ""; //validarEmpresaNotaDebito(documento, idEmpresa, idData);

            if (message.isEmpty()) {
                CpImpuestosEntity notaDebito = validarNotaDebito(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(notaDebito)) {
                    notaDebito.setCreatedBy(usuario);
                    notaDebito.setCreatedDate(LocalDateTime.now());
                    cpImpuestosRepository.save(notaDebito);
                    return "";
                }
                return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
            } else {
                return message;
            }

        }

        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
    }

    private String saveNotaCredito(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {
        NotaCredito documento = null;
        if (autorizacionDto.getFormatoDocumento().equals("1")) {
            documento = XmlUtils.getNotaCreditoRecibida(autorizacionDto);
        } else {
            documento = autorizacionDto.getNotaCredito();
        }

        if (Objects.nonNull(documento)) {

            String message = ""; //validarEmpresaNotaCredito(documento, idEmpresa, idData);
            if (message.isEmpty()) {
                CpImpuestosEntity notaCredito = validarNotaCredito(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(notaCredito)) {
                    notaCredito.setCreatedBy(usuario);
                    notaCredito.setCreatedDate(LocalDateTime.now());
                    cpImpuestosRepository.save(notaCredito);
                    return "";
                } else {
                    return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
                }
            } else {
                return message;
            }

        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
    }

    private String saveFactura(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {

        Factura documento = null;
        if (autorizacionDto.getFormatoDocumento().equals("1")) {
            documento = XmlUtils.getFacturaRecibidos(autorizacionDto);
        } else {
            documento = autorizacionDto.getFactura();
        }

        if (Objects.nonNull(documento)) {

            String message = ""; //validacionEmpresaFactura(documento, idEmpresa, idData);
            if (message.isEmpty()) {
                CpImpuestosEntity factura = validarFactura(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(factura)) {
                    factura.setCreatedBy(usuario);
                    factura.setCreatedDate(LocalDateTime.now());
                    cpImpuestosRepository.save(factura);

                    return "";
                } else {
                    return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
                }
            } else {
                return message;
            }

        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
    }


    private String validacionNoLiquidacion(String numeroAutorizacion) {
        if (numeroAutorizacion.startsWith("03", 8)) {
            return MensajeComprobante.ERR_ES_UNA_LIQUIDACION;
        }
        return "";

    }

    private String validacionRetencion(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String numeroAutorizacion, String usuario) {
        if ((numeroAutorizacion.startsWith("07", 8))) {

            if (autorizacionDto.getFormatoDocumento().equals("1")) {
                String version = XmlUtils.obtenerVersionXml(autorizacionDto.getComprobante());

                if (ConstantesDocumento.VERSION_1_0_0.equals(version)) {

                    return saveRetencionVersionUno(idData, idEmpresa, autorizacionDto, usuario);
                } else {
                    return saveRetencionVersionDos(idData, idEmpresa, autorizacionDto, usuario);
                }
            } else {

                if (autorizacionDto.getVersionDocumento().equals(ConstantesDocumento.VERSION_1_0_0)) {
                    VtRetencionesEntity entidad = validarRetencionUno(idData, idEmpresa, autorizacionDto.getComprobanteRetencionV1(), autorizacionDto);
                    if (Objects.nonNull(entidad)) {
                        entidad.setCreatedBy(usuario);
                        entidad.setCreatedDate(LocalDateTime.now());
                        ventasRetencionesRepository.save(entidad);
                        return "";
                    }
                } else {
                    VtRetencionesEntity entidad = validarRetencionDos(idData, idEmpresa, autorizacionDto.getComprobanteRetencionV2(), autorizacionDto);
                    if (Objects.nonNull(entidad)) {
                        entidad.setCreatedBy(usuario);
                        entidad.setCreatedDate(LocalDateTime.now());
                        ventasRetencionesRepository.save(entidad);
                        return "";
                    }
                }
            }

        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
    }

    private String saveRetencionVersionDos(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {
        ComprobanteRetencion documento = getComprobanteRetencionDos(autorizacionDto);

        if (Objects.nonNull(documento)) {
            String message = ""; //validarEmpresaRetencion(documento.getInfoCompRetencion().getIdentificacionSujetoRetenido(), idEmpresa, idData);

            if (message.isEmpty()) {
                VtRetencionesEntity entidad = validarRetencionDos(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(entidad)) {
                    entidad.setCreatedBy(usuario);
                    entidad.setCreatedDate(LocalDateTime.now());
                    ventasRetencionesRepository.save(entidad);
                    return "";
                }
                return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
            } else {
                return message;
            }
        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
    }


    private String saveRetencionVersionUno(Long idData, Long idEmpresa, CampoAutorizacionDto autorizacionDto, String usuario) {
        com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento = getComprobanteRetencionUno(autorizacionDto);

        if (Objects.nonNull(documento)) {
            String message = ""; //validarEmpresaRetencion(documento.getInfoCompRetencion().getIdentificacionSujetoRetenido(), idEmpresa, idData);

            if (message.isEmpty()) {
                VtRetencionesEntity entidad = validarRetencionUno(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(entidad)) {
                    entidad.setCreatedBy(usuario);
                    entidad.setCreatedDate(LocalDateTime.now());
                    ventasRetencionesRepository.save(entidad);
                    return "";
                }
                return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
            } else {
                return message;
            }
        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
    }


    private String validarEmpresaNotaDebito(NotaDebito notaDebito, Long idEmpresa, Long idData) {

        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);
        if (empresa.isPresent()) {

            String rucEmpresa = empresa.get().getRuc().substring(0, 10);
            String identificacionNotaDebito = notaDebito.getInfoNotaDebito().getIdentificacionComprador()
                    .substring(0, 10);

            if (rucEmpresa.equals(identificacionNotaDebito)) {
                return "";
            }
            return MensajeComprobante.ERR_NO_CORRESPONDE_EMPRESA;
        } else {
            return MensajeComprobante.ERR_NO_EXISTE_EMPRESA;
        }

    }


    private String validarEmpresaNotaCredito(NotaCredito documento, Long idEmpresa, Long idData) {
        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);

        if (empresa.isPresent()) {

            String rucEmpresa = empresa.get().getRuc().substring(0, 10);
            String identificacionNotaCredito = documento.getInfoNotaCredito().getIdentificacionComprador()
                    .substring(0, 10);

            if (rucEmpresa.equals(identificacionNotaCredito)) {
                return "";
            }
            return MensajeComprobante.ERR_NO_CORRESPONDE_EMPRESA;
        } else {
            return MensajeComprobante.ERR_NO_EXISTE_EMPRESA;
        }

    }

    private String validacionEmpresaFactura(Factura factura, Long idEmpresa, Long idData) {

        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);
        if (empresa.isPresent()) {

            String rucEmpresa = empresa.get().getRuc().substring(0, 10);
            String identificacionFactura = factura.getInfoFactura().getIdentificacionComprador()
                    .substring(0, 10);

            if (rucEmpresa.equals(identificacionFactura)) {
                return "";
            }
            return MensajeComprobante.ERR_NO_CORRESPONDE_EMPRESA;
        } else {
            return MensajeComprobante.ERR_NO_EXISTE_EMPRESA;
        }

    }


    private String validarEmpresaRetencion(String identificacion, Long idEmpresa, Long idData) {

        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);

        if (empresa.isPresent()) {

            String rucEmpresa = empresa.get().getRuc().substring(0, 10);
            String identificacionRetencion = identificacion.substring(0, 10);

            if (rucEmpresa.equals(identificacionRetencion)) {
                return "";
            }
            return MensajeComprobante.ERR_NO_CORRESPONDE_EMPRESA;
        } else {
            return MensajeComprobante.ERR_NO_EXISTE_EMPRESA;
        }

    }


    private CpImpuestosEntity validarFactura(Long idData, Long idEmpresa, Factura documento, CampoAutorizacionDto autorizacionDto) {

        try {
            CpImpuestosEntity cpImpuestosEntity = autorizacionBuilder.builderFactura(autorizacionDto, documento, idData, idEmpresa,
                    validarProveedor(documento.getInfoTributaria(), idData));
            validarValoresFactura(documento, cpImpuestosEntity, idData, idEmpresa);
            return cpImpuestosEntity;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    private CpImpuestosEntity validarNotaCredito(Long idData, Long idEmpresa, NotaCredito documento, CampoAutorizacionDto autorizacionDto) {
        try {
            CpImpuestosEntity cpImpuestosEntity = autorizacionBuilder.builderNotaCredito(autorizacionDto, documento, idData, idEmpresa,
                    validarProveedor(documento.getInfoTributaria(), idData));
            validarValoresNotaCredito(documento, cpImpuestosEntity, idData, idEmpresa);
            return cpImpuestosEntity;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private CpImpuestosEntity validarNotaDebito(Long idData, Long idEmpresa, NotaDebito documento, CampoAutorizacionDto autorizacionDto) {
        try {
            CpImpuestosEntity cpImpuestosEntity = autorizacionBuilder.builderNotaDebito(autorizacionDto, documento, idData, idEmpresa,
                    validarProveedor(documento.getInfoTributaria(), idData));
            validarValoresNotaDebito(documento, cpImpuestosEntity, idData, idEmpresa);
            return cpImpuestosEntity;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private VtRetencionesEntity validarRetencionDos(Long idData, Long idEmpresa,
                                                    ComprobanteRetencion documento,
                                                    CampoAutorizacionDto autorizacionDto) {
        try {
            VtRetencionesEntity retencion = autorizacionBuilder.builderRetencionRecibidaDos(autorizacionDto, documento, idData, idEmpresa,
                    validarCliente(documento.getInfoTributaria(), idData));
            validarPeriodoFiscal(retencion);
            return retencion;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private VtRetencionesEntity validarRetencionUno(Long idData, Long idEmpresa,
                                                    com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento,
                                                    CampoAutorizacionDto autorizacionDto) {
        try {
            VtRetencionesEntity retencion = autorizacionBuilder.builderRetencionRecibidaUno(autorizacionDto, documento, idData, idEmpresa,
                    validarCliente(documento.getInfoTributaria(), idData));
            validarPeriodoFiscal(retencion);
            return retencion;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private ComprobanteRetencion getComprobanteRetencionDos(CampoAutorizacionDto autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), ComprobanteRetencion.class);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return null;
        }
    }

    private com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion getComprobanteRetencionUno(CampoAutorizacionDto autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion.class);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return null;
        }
    }


    private GeTerceroEntity validarProveedor(InfoTributaria model, Long idData) {
        Optional<GeTerceroEntity> cpProveedorEntity = geTercerosRepository
                .getFindExistByNumeroIdentificacion(idData, model.getRuc());

        if (cpProveedorEntity.isPresent()) {

            boolean isProveedor = cpProveedorEntity.get()
                    .getGeTercerosTipoEntities()
                    .stream()
                    .anyMatch(c -> c.getTipo().equals(TipoTercero.PROVEEDOR.getTipo()));

            if (isProveedor) {
                return cpProveedorEntity.get();
            } else {
                return saveTipoProveedor(cpProveedorEntity.get());
            }
        }

        return saveTipoProveedor(geTercerosRepository.save(autorizacionBuilder.builderProveedor(model, idData)));
    }

    private GeTerceroEntity saveTipoProveedor(GeTerceroEntity geTerceroEntity) {
        geTercerosTipoRepository.save(geTercerosTipoBuilder.builderProveedorEntity(geTerceroEntity));
        return geTerceroEntity;
    }


    private GeTerceroEntity validarCliente(InfoTributaria model, Long idData) {
        Optional<GeTerceroEntity> vtClienteEntity = geTercerosRepository
                .getFindExistByNumeroIdentificacion(idData, model.getRuc());

        if (vtClienteEntity.isPresent()) {
            boolean isCliente = vtClienteEntity.get()
                    .getGeTercerosTipoEntities()
                    .stream()
                    .anyMatch(c -> c.getTipo().equals(TipoTercero.CLIENTE.getTipo()));

            if (isCliente) {
                return vtClienteEntity.get();
            } else {
                return saveTipoCliente(vtClienteEntity.get());
            }
        }

        return saveTipoCliente(geTercerosRepository.save(autorizacionBuilder.builderCliente(model, idData)));
    }

    private GeTerceroEntity saveTipoCliente(GeTerceroEntity geTerceroEntity) {
        geTercerosTipoRepository.save(geTercerosTipoBuilder.builderClienteEntity(geTerceroEntity));
        return geTerceroEntity;
    }

    private void validarValoresFactura(Factura documento, CpImpuestosEntity cpImpuestosEntity, Long idData, Long idEmpresa) {
        List<CpImpuestosValoresEntity> reembolsosEntities = new ArrayList<>();
        BigDecimal tarifa = BigDecimal.ZERO;
        for (TotalImpuesto impuesto : documento.getInfoFactura().getTotalImpuesto()) {
            BigDecimal valor = new BigDecimal(impuesto.getValor().replace(" ", ""));
            BigDecimal baseImponible = new BigDecimal(impuesto.getBaseImponible().replace(" ", ""));
            if (valor.compareTo(BigDecimal.ZERO) > 0 || baseImponible.compareTo(BigDecimal.ZERO) > 0) {
                reembolsosEntities.add(autorizacionBuilder.builderValoresDocumento(impuesto, tarifa, idData, idEmpresa));
            }

        }
        cpImpuestosEntity.setValoresEntity(reembolsosEntities);
    }


    private void validarValoresNotaCredito(NotaCredito documento, CpImpuestosEntity cpImpuestosEntity, Long idData, Long idEmpresa) {
        List<CpImpuestosValoresEntity> reembolsosEntities = new ArrayList<>();
        BigDecimal tarifa = BigDecimal.ZERO;
        for (TotalImpuesto impuesto : documento.getInfoNotaCredito().getTotalImpuesto()) {
            BigDecimal valor = new BigDecimal(impuesto.getValor().replace(" ", ""));
            BigDecimal baseImponible = new BigDecimal(impuesto.getBaseImponible().replace(" ", ""));
            if (valor.compareTo(BigDecimal.ZERO) > 0 || baseImponible.compareTo(BigDecimal.ZERO) > 0) {
                reembolsosEntities.add(autorizacionBuilder.builderValoresDocumento(impuesto, tarifa, idData, idEmpresa));
            }

        }
        cpImpuestosEntity.setValoresEntity(reembolsosEntities);
    }

    private void validarValoresNotaDebito(NotaDebito documento, CpImpuestosEntity cpImpuestosEntity, Long idData, Long idEmpresa) {
        List<CpImpuestosValoresEntity> reembolsosEntities = new ArrayList<>();
        BigDecimal tarifa = BigDecimal.ZERO;
        for (Impuesto impuesto : documento.getInfoNotaDebito().getImpuesto()) {
            BigDecimal valor = new BigDecimal(impuesto.getValor().replace(" ", ""));
            BigDecimal baseImponible = new BigDecimal(impuesto.getBaseImponible().replace(" ", ""));
            if (valor.compareTo(BigDecimal.ZERO) > 0 || baseImponible.compareTo(BigDecimal.ZERO) > 0) {
                reembolsosEntities.add(autorizacionBuilder.builderValoresNotaDebito(impuesto, tarifa, idData, idEmpresa));
            }
        }
        cpImpuestosEntity.setValoresEntity(reembolsosEntities);
    }

    private void validarPeriodoFiscal(VtRetencionesEntity retencion) {
        if (retencion.getFechaEmisionRetencion().getYear() == retencion.getPeriodoFiscal().getYear()
                && retencion.getFechaEmisionRetencion().getMonthValue() == retencion.getPeriodoFiscal().getMonthValue()) {
            retencion.setPeriodoFiscal(retencion.getFechaEmisionRetencion());
        }
    }

}
