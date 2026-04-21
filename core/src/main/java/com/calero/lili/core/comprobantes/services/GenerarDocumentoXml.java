package com.calero.lili.core.comprobantes.services;

import com.calero.lili.core.comprobantes.builder.documentos.comprobanteRetencion.ComprobanteRetencionBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.factura.FacturaBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.factura.FacturaBuilderReembolsos;
import com.calero.lili.core.comprobantes.builder.documentos.factura.FacturaExportacionBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.factura.FacturaGuiaRemisionBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.guiaRemision.GuiaRemisionBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.liquidaciones.LiquidacionBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.liquidaciones.LiquidacionReembolsoBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.notaCredito.NotaCreditoBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.notaDebito.NotaDebitoBuilder;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.utils.generarClaveAcceso.GenerarClaveAcceso;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GenerarDocumentoXml {

    private final GenerarClaveAcceso serviceClaveAcceso;
    private final FacturaBuilder facturaBuilder;
    private final FacturaExportacionBuilder facturaExportacionBuilder;
    private final FacturaGuiaRemisionBuilder facturaGuiaRemisionBuilder;
    private final FacturaBuilderReembolsos facturaBuilderReembolsos;
    private final NotaCreditoBuilder notaCreditoBuilder;
    private final NotaDebitoBuilder notaDebitoBuilder;
    private final LiquidacionBuilder liquidacionBuilder;
    private final LiquidacionReembolsoBuilder liquidacionReembolso;
    private final GuiaRemisionBuilder guiaRemisionBuilder;
    private final ComprobanteRetencionBuilder comprobanteRetencionBuilder;
    private final GeTercerosRepository geTercerosRepository;


    public Factura generarFactura(VtVentaEntity vtVentaEntity, AdEmpresaEntity empresaEntity, AdEmpresasSeriesEntity serieEntity) {

        String claveAcceso = serviceClaveAcceso.generarClaveAcceso(DateUtils.toString(vtVentaEntity.getFechaEmision()),
                "01", empresaEntity.getRuc(), vtVentaEntity.getAmbiente(),
                vtVentaEntity.getSerie(), vtVentaEntity.getSecuencial(), vtVentaEntity.getTipoEmision());

        vtVentaEntity.setClaveAcceso(claveAcceso);

        Factura factura = facturaBuilder.builderFactura(vtVentaEntity, empresaEntity, serieEntity);

        if (Objects.nonNull(vtVentaEntity.getExportacion())) {
            facturaExportacionBuilder.builderFacturaExportacion(factura, vtVentaEntity);
        }

        if (Objects.nonNull(vtVentaEntity.getSustitutivaGuiaRemision())) {
            facturaGuiaRemisionBuilder.builderFacturaGuiaRemision(factura, vtVentaEntity, getTransportista(vtVentaEntity));
        }

        if (Objects.nonNull(vtVentaEntity.getReembolsosEntity())) {
            facturaBuilderReembolsos.builderFacturaReembolso(factura, vtVentaEntity);
        }

        return factura;
    }

    public NotaCredito generarNotaCredito(VtVentaEntity vtVentaEntity, AdEmpresaEntity empresaEntity, AdEmpresasSeriesEntity serieEntity) {

        String claveAcceso = serviceClaveAcceso.generarClaveAcceso(DateUtils.toString(vtVentaEntity.getFechaEmision()),
                "04", empresaEntity.getRuc(), vtVentaEntity.getAmbiente(),
                vtVentaEntity.getSerie(), vtVentaEntity.getSecuencial(), vtVentaEntity.getTipoEmision());

        vtVentaEntity.setClaveAcceso(claveAcceso);

        return notaCreditoBuilder.builderNotaCredito(vtVentaEntity, empresaEntity, serieEntity);
    }

    public NotaDebito generarNotaDebito(VtVentaEntity vtVentaEntity, AdEmpresaEntity empresaEntity, AdEmpresasSeriesEntity serieEntity) {

        String claveAcceso = serviceClaveAcceso.generarClaveAcceso(DateUtils.toString(vtVentaEntity.getFechaEmision()),
                "05", empresaEntity.getRuc(), vtVentaEntity.getAmbiente(),
                vtVentaEntity.getSerie(), vtVentaEntity.getSecuencial(), vtVentaEntity.getTipoEmision());

        vtVentaEntity.setClaveAcceso(claveAcceso);

        return notaDebitoBuilder.builderNotaDebito(vtVentaEntity, empresaEntity, serieEntity);
    }

    public LiquidacionCompra generarLiquidacion(CpLiquidacionesEntity cpLiquidacionesEntity, AdEmpresaEntity empresaEntity, AdEmpresasSeriesEntity serieEntity) {

        String claveAcceso = serviceClaveAcceso.generarClaveAcceso(DateUtils.toString(cpLiquidacionesEntity.getFechaEmision()),
                "03", empresaEntity.getRuc(), cpLiquidacionesEntity.getAmbiente(),
                cpLiquidacionesEntity.getSerie(), cpLiquidacionesEntity.getSecuencial(), cpLiquidacionesEntity.getTipoEmision());

        cpLiquidacionesEntity.setClaveAcceso(claveAcceso);

        LiquidacionCompra liquidacionCompra = liquidacionBuilder.builderLiquidacion(cpLiquidacionesEntity, empresaEntity, serieEntity);

        if (Objects.nonNull(cpLiquidacionesEntity.getCodDocReembolso())) {
            if (cpLiquidacionesEntity.getCodDocReembolso().equals("41")) {
                liquidacionReembolso.builderLiquidacionReembolso(cpLiquidacionesEntity, liquidacionCompra);
            }
        }
        return liquidacionCompra;
    }

    public GuiaRemision generarGuiaRemision(VtGuiaEntity guiaRemision, AdEmpresaEntity empresaEntity, AdEmpresasSeriesEntity serieEntity) {

        String claveAcceso = serviceClaveAcceso.generarClaveAcceso(DateUtils.toString(guiaRemision.getFechaIniTransporte()),
                "06", empresaEntity.getRuc(), guiaRemision.getAmbiente(),
                guiaRemision.getSerie(), guiaRemision.getSecuencial(), guiaRemision.getTipoEmision());

        guiaRemision.setClaveAcceso(claveAcceso);

       return guiaRemisionBuilder.builderGuiaRemision(guiaRemision, empresaEntity, serieEntity);

    }

    public ComprobanteRetencion generarComprobanteRetencion(CpRetencionesEntity retencion,
                                                            AdEmpresaEntity empresaEntity,
                                                            AdEmpresasSeriesEntity serieEntity,
                                                            List<CpImpuestosEntity> listImpuestos,
                                                            GeTerceroEntity proveedor) {


        String claveAcceso = serviceClaveAcceso.generarClaveAcceso(DateUtils.toString(retencion.getFechaEmisionRetencion()),
                "07", empresaEntity.getRuc(), retencion.getAmbiente(),
                retencion.getSerieRetencion(), retencion.getSecuencialRetencion(), retencion.getTipoEmision());


        retencion.setClaveAcceso(claveAcceso);
        return comprobanteRetencionBuilder.builderComprobanteRetencion(retencion, empresaEntity,
                serieEntity, listImpuestos, proveedor);

    }

    private GeTerceroEntity getTransportista(VtVentaEntity venta) {
        GeTerceroEntity entidad = geTercerosRepository.findByIdCliente(venta.getIdData(),
                        venta.getSustitutivaGuiaRemision().getIdTransportista())
                .orElseThrow(() -> new GeneralException("No existe transportista"));

        boolean isTransportista = entidad.getGeTercerosTipoEntities()
                .stream()
                .anyMatch(t -> t.getTipo().equals(TipoTercero.TRANSPORTISTA.getTipo()));

        if (!isTransportista) {
            throw new GeneralException("El tercero no corresponde a un transportista");
        }

        return entidad;

    }


}
