package com.calero.lili.core.comprobantes.services;

import com.calero.lili.core.comprobantes.builder.AutorizacionBuilder;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.message.MensajeComprobante;
import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.projection.DeEmitidasRetencionesProjection;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTerceros.GeTercerosTipoEntity;
import com.calero.lili.core.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.core.modTerceros.builder.GeTercerosTipoBuilder;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentas.projection.OneProjection;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@XmlRootElement
@Slf4j
public class DeEmitidasComponentsServiceImpl {

    private final ComprasRetencionesRepository retencionesRepository;
    private final AutorizacionBuilder autorizacionBuilder;
    private final GeTercerosRepository cpProveedoresRepository;
    private final GeTercerosTipoRepository geTercerosTipoRepository;
    private final GeTercerosTipoBuilder geTercerosTipoBuilder;
    private final AdEmpresasRepository adEmpresasRepository;
    private final VtVentasRepository vtVentaRepository;
    private final GeItemsRepository geItemsRepository;


    public Boolean verificarExisteDocumentoElectronicoBdd(Long idData, Long idEmpresa, String numeroAutorizacion) {

        if (esRetencion(numeroAutorizacion)) {
            Optional<DeEmitidasRetencionesProjection> existRetencion = retencionesRepository.findExistByNumeroAutorizacion(idData,
                    idEmpresa, numeroAutorizacion);
            if (existRetencion.isPresent()) {
                log.info("El documento de retención consultado ya existe");
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }

        if (esDocumentoVenta(numeroAutorizacion)) {

            String serie = numeroAutorizacion.substring(24, 30);
            String secuencial = numeroAutorizacion.substring(30, 39);

            Optional<OneProjection> existingFactura = vtVentaRepository
                    .findExistBySecuencial(idData, idEmpresa, TipoVenta.FAC.name(), serie, secuencial);

            if (existingFactura.isPresent()) {
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }


        throw new GeneralException(MessageFormat.format("El documento con número: {0}  no corresponde " +
                        "a los documentos permitidos en esta sección "
                , numeroAutorizacion));
    }


    public String guardarComprobante(Long idData, Long idEmpresa, Autorizacion autorizacionDto, String sucursal, String usuario) {

        if (esRetencion(autorizacionDto.getNumeroAutorizacion())) {
            return guardarRetencion(idData, idEmpresa, autorizacionDto, usuario);
        }

        if (autorizacionDto.getNumeroAutorizacion().startsWith("01", 8)) {
            return guardarFactura(idData, idEmpresa, autorizacionDto, sucursal, usuario);
        }

        if (autorizacionDto.getNumeroAutorizacion().startsWith("04", 8)) {
            return guardarNotaCredito(idData, idEmpresa, autorizacionDto, sucursal, usuario);
        }

        return MensajeComprobante.ERR_EL_DOCUMENTO_NO_CORRESPONDE;
    }

    private String guardarNotaCredito(Long idData, Long idEmpresa, Autorizacion autorizacionDto, String sucursal, String usuario) {
        NotaCredito documento = XmlUtils.getNotaCredito(autorizacionDto);
        if (Objects.nonNull(documento)) {
            String message = ""; //validarEmpresa(documento.getInfoTributaria().getRuc(), idEmpresa, idData);

            if (message.isEmpty()) {
                VtVentaEntity notaCredito = validarNotaCredito(idData, idEmpresa, documento, sucursal, autorizacionDto.getComprobante(), usuario);
                if (Objects.nonNull(notaCredito)) {
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


    private String guardarFactura(Long idData, Long idEmpresa, Autorizacion autorizacionDto, String sucursal, String usuario) {

        Factura documento = XmlUtils.getFactura(autorizacionDto);
        if (Objects.nonNull(documento)) {

            String message = "";//validarEmpresa(documento.getInfoTributaria().getRuc(), idEmpresa, idData);
            if (message.isEmpty()) {
                VtVentaEntity factura = validarFactura(idData, idEmpresa, documento, sucursal, autorizacionDto.getComprobante(), usuario);
                if (Objects.nonNull(factura)) {

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

    private String guardarRetencion(Long idData, Long idEmpresa, Autorizacion autorizacionDto, String usuario) {

        String validacion = validarRetencion(autorizacionDto.getNumeroAutorizacion());

        if (validacion.isEmpty()) {
            String numeroAutorizacion = autorizacionDto.getNumeroAutorizacion();

            if ((numeroAutorizacion.startsWith("07", 8))) {

                Optional<DeEmitidasRetencionesProjection> existingFactura = retencionesRepository
                        .findExistByNumeroAutorizacion(idData, idEmpresa, numeroAutorizacion);

                if (existingFactura.isEmpty()) {

                    String version = XmlUtils.obtenerVersionXml(autorizacionDto.getComprobante());

                    if (ConstantesDocumento.VERSION_1_0_0.equals(version)) {
                        return saveRetencionVersionUno(autorizacionDto, idEmpresa, idData, usuario);
                    } else {
                        return saveRetencionVersionDos(autorizacionDto, idEmpresa, idData, usuario);
                    }
                }
            }
        }
        return validacion;
    }

    private String saveRetencionVersionDos(Autorizacion autorizacionDto, Long idEmpresa, Long idData, String usuario) {

        ComprobanteRetencion documento = getComprobanteRetencion(autorizacionDto);
        if (Objects.nonNull(documento)) {

            String message = "";//validarEmpresa(documento.getInfoTributaria().getRuc(), idEmpresa, idData);
            if (message.isEmpty()) {
                CpRetencionesEntity entidad = validarRetencionDos(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(entidad)) {
                    entidad.setCreatedBy(usuario);
                    entidad.setCreatedDate(LocalDateTime.now());
                    retencionesRepository.save(entidad);
                    return "";
                }
                return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
            }
            return message;
        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;

    }

    private String saveRetencionVersionUno(Autorizacion autorizacionDto, Long idEmpresa, Long idData, String usuario) {

        com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento = getComprobanteRetencionUno(autorizacionDto);
        if (Objects.nonNull(documento)) {

            String message = "";//validarEmpresa(documento.getInfoTributaria().getRuc(), idEmpresa, idData);
            if (message.isEmpty()) {
                CpRetencionesEntity entidad = validarRetencionUno(idData, idEmpresa, documento, autorizacionDto);
                if (Objects.nonNull(entidad)) {
                    entidad.setCreatedBy(usuario);
                    entidad.setCreatedDate(LocalDateTime.now());
                    retencionesRepository.save(entidad);
                    return "";
                }
                return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;
            }
            return message;
        }
        return MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO;

    }


    private String validarRetencion(String claveAcceso) {
        if (!claveAcceso.startsWith("07", 8)) {
            return MensajeComprobante.ERR_NO_ES_RETENCION;
        }
        return "";
    }


    private String validarEmpresa(String ruc, Long idEmpresa, Long idData) {

        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);

        if (empresa.isPresent()) {

            String rucEmpresa = empresa.get().getRuc().substring(0, 10);
            String identificacionComprobante = ruc.substring(0, 10);

            if (rucEmpresa.equals(identificacionComprobante)) {
                return "";
            }
            return MensajeComprobante.ERR_NO_CORRESPONDE_EMPRESA;
        } else {
            return MensajeComprobante.ERR_NO_EXISTE_EMPRESA;
        }

    }

    private CpRetencionesEntity validarRetencionDos(Long idData, Long idEmpresa,
                                                    ComprobanteRetencion documento,
                                                    Autorizacion autorizacionDto) {
        try {

            BigDecimal total = obtenerTotalRetenidoDos(documento);
            CpRetencionesEntity retencion = autorizacionBuilder.builderRetencionEmitidaDos(autorizacionDto, documento, idData, idEmpresa,
                    validarProveedor(documento.getInfoTributaria(), idData), total);
            validarPeriodoFiscal(retencion);
            return retencion;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    private CpRetencionesEntity validarRetencionUno(Long idData, Long idEmpresa,
                                                    com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento,
                                                    Autorizacion autorizacionDto) {
        try {

            BigDecimal total = obtenerTotalRetenidoUno(documento);
            CpRetencionesEntity retencion = autorizacionBuilder.builderRetencionEmitidaUno(autorizacionDto, documento, idData, idEmpresa,
                    validarProveedor(documento.getInfoTributaria(), idData), total);
            validarPeriodoFiscal(retencion);
            return retencion;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    private ComprobanteRetencion getComprobanteRetencion(Autorizacion autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), ComprobanteRetencion.class);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return null;
        }
    }


    private com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion getComprobanteRetencionUno(Autorizacion autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion.class);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return null;
        }
    }

    private BigDecimal obtenerTotalRetenidoDos(ComprobanteRetencion documento) {
        return documento.getDocSustento()
                .stream().flatMap(docSustento -> docSustento.getRetencion().stream())
                .map(retencion -> new BigDecimal(retencion.getValorRetenido()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal obtenerTotalRetenidoUno(com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento) {
        return documento.getImpuesto()
                .stream()
                .map(retencion -> new BigDecimal(retencion.getValorRetenido()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private GeTerceroEntity validarProveedor(InfoTributaria model, Long idData) {
        Optional<GeTerceroEntity> cpProveedorEntity = cpProveedoresRepository
                .getFindExistByNumeroIdentificacion(idData, model.getRuc());

        if (cpProveedorEntity.isPresent()) {

            boolean isProveedor = cpProveedorEntity.get()
                    .getGeTercerosTipoEntities()
                    .stream()
                    .anyMatch(c -> c.getTipo().equals(TipoTercero.PROVEEDOR.getTipo()));

            if (isProveedor) {
                return cpProveedorEntity.get();
            } else {
                saveGeTerceroTipoProveedor(cpProveedorEntity.get());
                return cpProveedorEntity.get();
            }
        }

        return saveGeTercero(cpProveedoresRepository
                .save(autorizacionBuilder.builderProveedor(model, idData)), TipoTercero.PROVEEDOR);
    }

    private void saveGeTerceroTipoProveedor(GeTerceroEntity geTerceroEntity) {
        GeTercerosTipoEntity entity = geTercerosTipoBuilder.builderProveedorEntity(geTerceroEntity);
        geTercerosTipoRepository.save(entity);
    }

    private GeTerceroEntity saveGeTercero(GeTerceroEntity geTerceroEntity, TipoTercero tipoTercero) {
        geTercerosTipoRepository.save(geTercerosTipoBuilder.builderTipoTercero(geTerceroEntity, tipoTercero));
        return geTerceroEntity;
    }


    private Boolean esDocumentoVenta(String numeroAutorizacion) {

        if (numeroAutorizacion.startsWith("01", 8) ||
                numeroAutorizacion.startsWith("04", 8)) {

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean esRetencion(String numeroAutorizacion) {
        return numeroAutorizacion.startsWith("07", 8);
    }


    private VtVentaEntity validarFactura(Long idData, Long idEmpresa, Factura documento,
                                         String sucursal, String comprobante, String usuario) {

        try {

            GeTerceroEntity tercero = validarCliente(idData,
                    documento.getInfoFactura().getIdentificacionComprador(),
                    documento.getInfoFactura().getRazonSocialComprador(),
                    documento.getInfoFactura().getTipoIdentificacionComprador(),
                    documento.getInfoFactura().getDireccionComprador());

            Map<String, GeItemEntity> listaItems = validarListDetalleFactura(idData, idEmpresa, documento.getDetalle());
            VtVentaEntity factura = autorizacionBuilder.builderFacturaVenta(idData, idEmpresa, documento, tercero, sucursal, comprobante);

            BigDecimal totalImpuesto = documento.getInfoFactura()
                    .getTotalImpuesto()
                    .stream()
                    .map(TotalImpuesto::getValor)
                    .filter(Objects::nonNull)
                    .map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            factura.getDetalle().forEach(detalle -> {
                GeItemEntity item = listaItems.get(detalle.getCodigoPrincipal());
                if (item != null) {
                    detalle.setItems(item);
                }
            });

            int totalItems = factura.getDetalle() != null ? factura.getDetalle().size() : 0;
            factura.setNumeroItems(totalItems);
            factura.setTotalImpuesto(totalImpuesto);

            factura.setCreatedBy(usuario);
            factura.setCreatedDate(LocalDateTime.now());

            return vtVentaRepository.save(factura);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    private VtVentaEntity validarNotaCredito(Long idData, Long idEmpresa, NotaCredito documento,
                                             String sucursal, String comprobante, String usuario) {
        try {

            GeTerceroEntity tercero = validarCliente(idData, documento.getInfoNotaCredito().getIdentificacionComprador(),
                    documento.getInfoNotaCredito().getRazonSocialComprador(),
                    documento.getInfoNotaCredito().getTipoIdentificacionComprador(),
                    documento.getInfoNotaCredito().getDirEstablecimiento());

            Map<String, GeItemEntity> listaItems = validarListDetalleNotaCredito(idData, idEmpresa, documento.getDetalle());

            BigDecimal totalImpuesto = documento.getInfoNotaCredito().getTotalImpuesto()
                    .stream()
                    .map(item -> new BigDecimal(item.getValor()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            VtVentaEntity notaCredito = autorizacionBuilder.builderEmitidaNotaCredito(idData, idEmpresa,
                    documento, tercero, sucursal, totalImpuesto, comprobante);

            notaCredito.getDetalle().forEach(detalle -> {
                GeItemEntity item = listaItems.get(detalle.getCodigoPrincipal());
                if (item != null) {
                    detalle.setItems(item);
                }
            });

            int totalItems = notaCredito.getDetalle() != null ? notaCredito.getDetalle().size() : 0;
            notaCredito.setNumeroItems(totalItems);

            notaCredito.setCreatedBy(usuario);
            notaCredito.setCreatedDate(LocalDateTime.now());
            return vtVentaRepository.save(notaCredito);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;

        }
    }

    private Map<String, GeItemEntity> validarListDetalleNotaCredito(Long idData, Long idEmpresa, List<com.calero.lili.core.comprobantes.objetosXml.notaCredito.Detalle> detalles) {
        Map<String, GeItemEntity> lista = new HashMap<>();
        for (com.calero.lili.core.comprobantes.objetosXml.notaCredito.Detalle detalle : detalles) {

            Optional<GeItemEntity> item = geItemsRepository
                    .findByCodigoItem(idData, idEmpresa, detalle.getCodigoInterno());

            if (item.isPresent()) {
                lista.put(item.get().getCodigoPrincipal(), item.get());
            } else {
                GeItemEntity geItemEntity = geItemsRepository.save(autorizacionBuilder.builderNotaCreditoItem(detalle,
                        idData, idEmpresa));
                lista.put(geItemEntity.getCodigoPrincipal(), geItemEntity);
            }

        }

        return lista;
    }


    private Map<String, GeItemEntity> validarListDetalleFactura(Long idData, Long idEmpresa, List<Detalle> detalles) {

        Map<String, GeItemEntity> lista = new HashMap<>();
        for (Detalle detalle : detalles) {

            Optional<GeItemEntity> item = geItemsRepository
                    .findByCodigoItem(idData, idEmpresa, detalle.getCodigoPrincipal());

            if (item.isPresent()) {
                lista.put(item.get().getCodigoPrincipal(), item.get());
            } else {
                GeItemEntity geItemEntity = geItemsRepository.save(autorizacionBuilder.builderFacturaItem(detalle,
                        idData, idEmpresa));
                lista.put(geItemEntity.getCodigoPrincipal(), geItemEntity);
            }

        }

        return lista;
    }

    private GeTerceroEntity validarCliente(Long idData, String numeroIdentificacion, String razonSocial,
                                           String tipoIdentificacion, String direccion) {

        Optional<GeTerceroEntity> cpCliente = cpProveedoresRepository
                .getFindExistByNumeroIdentificacion(idData, numeroIdentificacion);

        String tipIdent = TipoIdentificacion.obtenerTipoIdentificacion(tipoIdentificacion).name();

        if (cpCliente.isPresent()) {

            boolean isCliente = cpCliente.get()
                    .getGeTercerosTipoEntities()
                    .stream()
                    .anyMatch(c -> c.getTipo().equals(TipoTercero.CLIENTE.getTipo()));

            if (isCliente) {
                return cpCliente.get();
            } else {
                saveGeTerceroTipoCliente(cpCliente.get());
                return cpCliente.get();
            }
        }

        return saveGeTercero(cpProveedoresRepository
                .save(autorizacionBuilder.builderClienteEmitidas(idData, numeroIdentificacion,
                        razonSocial, tipIdent, direccion)), TipoTercero.CLIENTE);
    }

    private void saveGeTerceroTipoCliente(GeTerceroEntity geTerceroEntity) {
        GeTercerosTipoEntity entity = geTercerosTipoBuilder.builderClienteEntity(geTerceroEntity);
        geTercerosTipoRepository.save(entity);
    }


    private void validarPeriodoFiscal(CpRetencionesEntity retencion) {
        if (retencion.getFechaEmisionRetencion().getYear() == retencion.getPeriodoFiscal().getYear()
                && retencion.getFechaEmisionRetencion().getMonthValue() == retencion.getPeriodoFiscal().getMonthValue()) {
            retencion.setPeriodoFiscal(retencion.getFechaEmisionRetencion());
        }

    }
}
