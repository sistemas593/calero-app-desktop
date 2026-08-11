package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.dto.EmailDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.LiquidacionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiasRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentosService {


    @Value("${app.origen}")
    private String origenCertificado;


    private final VtVentasRepository vtVentasRepository;
    private final ProcesarDocumentosServiceImpl procesarDocumentosService;
    private final AdLogsBuilder adLogsBuilder;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final VtGuiasRepository vtGuiasRepository;
    private final LiquidacionesRepository liquidacionesRepository;
    private final ComprasRetencionesRepository comprasRetencionesRepository;


    public RespuestaProcesoGetDto procesarDocumentoVenta(Long idData,
                                                         Long idEmpresa, UUID idVenta, EmailDto emailDto) {

        VtVentaEntity documento = vtVentasRepository.findByIdEntity(idData, idEmpresa, idVenta, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento de venta {0} no exists", idVenta)));


        DatosEmpresaDto datosEmpresaDto = null;

        switch (origenCertificado) {

            case "WEB" -> {

                datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(documento.getIdData(), documento.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }

            case "LOC" -> {
                datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(documento.getIdData(), documento.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }
        }

        return procesarDocumentosService.procesarFacNcNd(documento,
                adLogsBuilder.builderVentasDocumentos(documento, Boolean.TRUE), datosEmpresaDto, emailDto.getCorreos());

    }


    public RespuestaProcesoGetDto procesarGuiaRemision(Long idData, Long idEmpresa, UUID idGuiaRemision) {


        VtGuiaEntity guiaRemision = vtGuiasRepository.findByIdEntity(idData, idEmpresa, idGuiaRemision, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Guia Remision {0} no exists", idGuiaRemision)));

        DatosEmpresaDto datosEmpresaDto = null;

        switch (origenCertificado) {

            case "WEB" -> {

                datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(guiaRemision.getIdData(), guiaRemision.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }

            case "LOC" -> {
                datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(guiaRemision.getIdData(), guiaRemision.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }
        }

        return procesarDocumentosService.procesarGuiaRemision(guiaRemision,
                adLogsBuilder.builderGuiaRemision(guiaRemision, Boolean.TRUE), datosEmpresaDto);

    }


    public RespuestaProcesoGetDto procesarLiquidacion(Long idData, Long idEmpresa, UUID idLiquidacion) {


        CpLiquidacionesEntity liquidacion = liquidacionesRepository.findByIdEntity(idData, idEmpresa, idLiquidacion, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Liquidacion {0} no exists", idLiquidacion)));

        DatosEmpresaDto datosEmpresaDto = null;

        switch (origenCertificado) {

            case "WEB" -> {

                datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(liquidacion.getIdData(), liquidacion.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }

            case "LOC" -> {
                datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(liquidacion.getIdData(), liquidacion.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }
        }

        return procesarDocumentosService.procesarLiquidacion(liquidacion,
                adLogsBuilder.builderLiquidacion(liquidacion, Boolean.TRUE), datosEmpresaDto);

    }

    public RespuestaProcesoGetDto procesarComprobanteRetencion(Long idData, Long idEmpresa, UUID idComprobante) {


        CpRetencionesEntity comprobante = comprasRetencionesRepository.findByIdEntity(idData, idEmpresa, idComprobante, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Comprobante retencion {0} no exists", idComprobante)));

        DatosEmpresaDto datosEmpresaDto = null;

        switch (origenCertificado) {

            case "WEB" -> {

                datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(comprobante.getIdData(), comprobante.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }

            case "LOC" -> {
                datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(comprobante.getIdData(), comprobante.getIdEmpresa());
                datosEmpresaDto.setOrigenDatos(origenCertificado);
            }
        }

        return procesarDocumentosService.procesarComprobanteRetencion(comprobante,
                adLogsBuilder.builderComprobanteRetencion(comprobante, Boolean.TRUE), datosEmpresaDto);

    }


}
