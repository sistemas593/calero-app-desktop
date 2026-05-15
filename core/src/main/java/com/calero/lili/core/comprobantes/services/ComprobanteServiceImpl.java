package com.calero.lili.core.comprobantes.services;

import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesRepository;
import com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComprobanteServiceImpl {

    private final AdEmpresasRepository adEmpresasRepository;
    private final AdEmpresasSeriesRepository adEmpresasSeriesRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final GenerarDocumentoXml generarDocumentoXml;
    private final CpImpuestosRepository cpImpuestosRepository;


    // TODO REVISAR EL EL GUARDADO DEL COMPROBANTE
    public void getComprobanteXmlFactura(Long idData, Long idEmpresa, VtVentaEntity vtVentaEntity) {

        if (vtVentaEntity.getFormatoDocumento().equals(FormatoDocumento.E)) {

            validarCliente(idData, vtVentaEntity.getTercero().getIdTercero());

            Factura factura = generarDocumentoXml.generarFactura(vtVentaEntity,
                    obtenerEmpresa(idData, idEmpresa), obtenerEmpresaSerie(idData, idEmpresa, vtVentaEntity.getSerie()));

            vtVentaEntity.setComprobante(XmlUtils.convertToXmlString(Factura.class, factura));
            vtVentaEntity.setEstadoDocumento(EstadoDocumento.ENV);
        }
    }


    public void getComprobanteXmlNotaCredito(Long idData, Long idEmpresa, VtVentaEntity vtVentaEntity) {
        if (vtVentaEntity.getFormatoDocumento().equals(FormatoDocumento.E)) {

            validarCliente(idData, vtVentaEntity.getTercero().getIdTercero());

            NotaCredito notaCredito = generarDocumentoXml.generarNotaCredito(vtVentaEntity,
                    obtenerEmpresa(idData, idEmpresa), obtenerEmpresaSerie(idData, idEmpresa, vtVentaEntity.getSerie()));

            vtVentaEntity.setComprobante(XmlUtils.convertToXmlString(NotaCredito.class, notaCredito));
            vtVentaEntity.setEstadoDocumento(EstadoDocumento.ENV);
        }
    }


    public void getComprobanteXmlNotaDebito(Long idData, Long idEmpresa, VtVentaEntity vtVentaEntity) {
        if (vtVentaEntity.getFormatoDocumento().equals(FormatoDocumento.E)) {

            validarCliente(idData, vtVentaEntity.getTercero().getIdTercero());

            NotaDebito notaDebito = generarDocumentoXml.generarNotaDebito(vtVentaEntity,
                    obtenerEmpresa(idData, idEmpresa), obtenerEmpresaSerie(idData, idEmpresa, vtVentaEntity.getSerie()));

            vtVentaEntity.setComprobante(XmlUtils.convertToXmlString(NotaDebito.class, notaDebito));
            vtVentaEntity.setEstadoDocumento(EstadoDocumento.ENV);
        }
    }

    public void getComprobanteXmlLiquidacion(Long idData, Long idEmpresa, CpLiquidacionesEntity cpLiquidacionesEntity) {
        if (cpLiquidacionesEntity.getFormatoDocumento().equals(FormatoDocumento.E)) {

            LiquidacionCompra liquidacionCompra = generarDocumentoXml.generarLiquidacion(cpLiquidacionesEntity,
                    obtenerEmpresa(idData, idEmpresa), obtenerEmpresaSerie(idData, idEmpresa, cpLiquidacionesEntity.getSerie()));

            cpLiquidacionesEntity.setComprobante(XmlUtils.convertToXmlString(LiquidacionCompra.class, liquidacionCompra));
            cpLiquidacionesEntity.setEstadoDocumento(EstadoDocumento.ENV);
        }
    }

    public void getComprobanteXmlGuiaRemision(Long idData, Long idEmpresa, VtGuiaEntity vtGuiaEntity) {
        if (vtGuiaEntity.getFormatoDocumento().equals(FormatoDocumento.E)) {

            GuiaRemision guiaRemision = generarDocumentoXml.generarGuiaRemision(vtGuiaEntity,
                    obtenerEmpresa(idData, idEmpresa), obtenerEmpresaSerie(idData, idEmpresa, vtGuiaEntity.getSerie()));

            vtGuiaEntity.setComprobante(XmlUtils.convertToXmlString(GuiaRemision.class, guiaRemision));
            vtGuiaEntity.setEstadoDocumento(EstadoDocumento.ENV);
        }
    }

    public void getComprobanteXmlRetencion(Long idData, Long idEmpresa, CpRetencionesEntity retencion,
                                           CreationRetencionRequestDto request) {
        if (retencion.getFormatoDocumento().equals(FormatoDocumento.E)) {

            ComprobanteRetencion comprobanteRetencion = generarDocumentoXml.generarComprobanteRetencion(retencion,
                    obtenerEmpresa(idData, idEmpresa), obtenerEmpresaSerie(idData, idEmpresa, retencion.getSerieRetencion()),
                    obtenerListaImpuestos(idData, idEmpresa, request), obtenerProveedor(idData, retencion.getProveedor().getIdTercero()));

            retencion.setComprobante(XmlUtils.convertToXmlString(ComprobanteRetencion.class, comprobanteRetencion));
            retencion.setEstadoDocumento(EstadoDocumento.ENV);
        }
    }

    private GeTerceroEntity obtenerProveedor(Long idData, UUID idTercero) {
        return geTercerosRepository.findByIdCliente(idData, idTercero)
                .orElseThrow(() -> new GeneralException("Proveedor no existe"));
    }


    private AdEmpresaEntity obtenerEmpresa(Long idData, Long idEmpresa) {
        return adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));
    }

    private AdEmpresasSeriesEntity obtenerEmpresaSerie(Long idData, Long idEmpresa, String serie) {
        return adEmpresasSeriesRepository
                .findBySerie(idData, idEmpresa, serie)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {0}, serie {1} no existe", idEmpresa, serie)));

    }

    private void validarCliente(Long idData, UUID idTercero) {
        Optional<GeTerceroEntity> cliente = geTercerosRepository
                .findByIdCliente(idData, idTercero);
        if (cliente.isEmpty()) {
            throw new GeneralException(MessageFormat.format("Cliente con id {0} no existe", idTercero.toString()));
        }
    }

    private List<CpImpuestosEntity> obtenerListaImpuestos(Long idData, Long idEmpresa, CreationRetencionRequestDto request) {

        List<UUID> list = request.getCompraImpuestos().stream()
                .map(CompraImpuestosDto::getCompraImpuestoId)
                .toList();
        return cpImpuestosRepository.findByListFacturasIdList(idData, idEmpresa, list);
    }

}
