package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.enums.OrigenImpuestos;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.ValidacionGeneralCpImpuestosService;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.ImpuestoCodigoBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CpRetencionPersistenceService {

    private final ComprasRetencionesRepository comprasRetencionesRepository;
    private final ComprobanteServiceImpl comprobanteService;
    private final ValidacionGeneralCpImpuestosService validacionGeneralService;
    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleErrorBuilder;
    private final ImpuestoCodigoBuilder impuestoCodigoBuilder;
    private final CpImpuestosRepository cpImpuestosRepository;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;

    @Transactional
    public CpRetencionesEntity guardarRetencion(CpRetencionesEntity entidad, AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa,
                                                CreationRetencionRequestDto request, Map<UUID, CpImpuestosEntity> mapImpuestos) {


        comprobanteService.getComprobanteXmlRetencion(idData, empresa, serie, entidad, request);


        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, entidad.getIdEmpresa(), entidad.getSerieRetencion(), TipoDocumentoSerie.CRT.name())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe",
                        entidad.getSerieRetencion(), entidad.getSecuencialRetencion())));


        int nuevo = Integer.parseInt(entidad.getSecuencialRetencion()) + 1;
        documentosEntity.setSecuencial(nuevo);


        request.getCompraImpuestos().forEach(dto -> {

            CpImpuestosEntity impuesto = mapImpuestos.get(dto.getCompraImpuestoId());

            List<CpImpuestoDetalleError> detalleErrors = validacionGeneralService.
                    validacionGeneral(cpImpuestoDetalleErrorBuilder.builderValidacionImpuestoRetencion(impuesto,
                            entidad, request.getCompraImpuestos()));

            if (!detalleErrors.isEmpty()) {
                List<String> list = detalleErrors.stream()
                        .map(CpImpuestoDetalleError::getDetalle)
                        .toList();
                throw new ListErrorException(list);
            }
        });


        CpRetencionesEntity saved = comprasRetencionesRepository.save(entidad);

        Map<UUID, CompraImpuestosDto> mapImpuestosDto = builderCompraImpuestoMap(request);
        for (Map.Entry<UUID, CpImpuestosEntity> entry : mapImpuestos.entrySet()) {

            UUID idImpuesto = entry.getKey();
            CpImpuestosEntity impuesto = entry.getValue();

            CompraImpuestosDto impuestoDto = mapImpuestosDto.get(idImpuesto);

            if (permiteRetencion(impuesto.getOrigen())) {

                impuesto.setRetencion(saved);
                impuesto.setOrigen(OrigenImpuestos.RCC.name());

                List<CpImpuestosCodigosEntity> listCodigos =
                        impuestoCodigoBuilder.builderMultiList(impuestoDto.getListCodigosImpuesto(),
                                saved.getIdData(),
                                saved.getIdEmpresa());

                validateCodigosEntity(impuesto, listCodigos);
                cpImpuestosRepository.save(impuesto);
            } else {
                throw new GeneralException(MessageFormat.format("El documento con id {0} y su origen: {1} no corresponde para guardar una retención",
                        idImpuesto, impuesto.getOrigen()));
            }

        }


        return saved;
    }

    @Transactional
    public CpRetencionesEntity actualizarRetencion(CpRetencionesEntity entidad, AdEmpresaEntity empresa,
                                                   AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa,
                                                   CreationRetencionRequestDto request, Map<UUID, CpImpuestosEntity> mapImpuestos,
                                                   List<CpImpuestosEntity> impuestosADesligar) {

        comprobanteService.getComprobanteXmlRetencion(idData, empresa, serie, entidad, request);

        request.getCompraImpuestos().forEach(dto -> {

            CpImpuestosEntity impuesto = mapImpuestos.get(dto.getCompraImpuestoId());

            List<CpImpuestoDetalleError> detalleErrors = validacionGeneralService.
                    validacionGeneral(cpImpuestoDetalleErrorBuilder.builderValidacionImpuestoRetencion(impuesto,
                            entidad, request.getCompraImpuestos()));

            if (!detalleErrors.isEmpty()) {
                List<String> list = detalleErrors.stream()
                        .map(CpImpuestoDetalleError::getDetalle)
                        .toList();
                throw new ListErrorException(list);
            }
        });


        CpRetencionesEntity update = comprasRetencionesRepository.save(entidad);

        impuestosADesligar.forEach(impuesto -> {
            impuesto.setRetencion(null);
            impuesto.setOrigen(OrigenImpuestos.ISC.name());
            impuesto.getCodigosEntity().clear();
            cpImpuestosRepository.save(impuesto);
        });

        Map<UUID, CompraImpuestosDto> mapImpuestosDto = builderCompraImpuestoMap(request);
        for (Map.Entry<UUID, CpImpuestosEntity> entry : mapImpuestos.entrySet()) {

            UUID id = entry.getKey();
            CpImpuestosEntity impuesto = entry.getValue();

            CompraImpuestosDto impuestoDto = mapImpuestosDto.get(id);

            if (permiteRetencion(impuesto.getOrigen()) || perteneceARetencion(impuesto, update)) {

                impuesto.setRetencion(update);
                impuesto.setOrigen(OrigenImpuestos.RCC.name());

                List<CpImpuestosCodigosEntity> listCodigos =
                        impuestoCodigoBuilder.builderMultiList(impuestoDto.getListCodigosImpuesto(),
                                update.getIdData(),
                                update.getIdEmpresa());

                validateCodigosEntity(impuesto, listCodigos);
                cpImpuestosRepository.save(impuesto);
            } else {
                throw new GeneralException(MessageFormat.format("El documento con id {0} y su origen: {1} no corresponde para actualizar una retención",
                        id, impuesto.getOrigen()));
            }
        }

        return update;
    }

    private boolean perteneceARetencion(CpImpuestosEntity impuesto, CpRetencionesEntity retencion) {
        return Objects.nonNull(impuesto.getRetencion())
                && impuesto.getRetencion().getIdRetencion().equals(retencion.getIdRetencion());
    }

    private Map<UUID, CompraImpuestosDto> builderCompraImpuestoMap(CreationRetencionRequestDto request) {

        Map<UUID, CompraImpuestosDto> mapImpuestos = new HashMap<>();

        request.getCompraImpuestos().forEach(item -> {
            CompraImpuestosDto impuestoDto = CompraImpuestosDto.builder()
                    .idCompraImpuesto(item.getCompraImpuestoId())
                    .listCodigosImpuesto(Objects.nonNull(item.getImpuestoCodigos())
                            ? item.getImpuestoCodigos()
                            : null)
                    .origen(OrigenImpuestos.RCC.name())
                    .build();

            mapImpuestos.put(item.getCompraImpuestoId(), impuestoDto);
        });

        return mapImpuestos;
    }


    private Boolean permiteRetencion(String origen) {
        if (OrigenImpuestos.XDF.name().equals(origen)) return Boolean.TRUE;
        if (OrigenImpuestos.ISC.name().equals(origen)) return Boolean.TRUE;
        if (OrigenImpuestos.DSC.name().equals(origen)) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private void validateCodigosEntity(CpImpuestosEntity impuesto, List<CpImpuestosCodigosEntity> listCodigos) {
        if (Objects.isNull(impuesto.getCodigosEntity())) {
            impuesto.setCodigosEntity(new ArrayList<>());
        }

        if (Objects.nonNull(listCodigos)) {
            impuesto.getCodigosEntity().clear();
            impuesto.getCodigosEntity().addAll(listCodigos);
        } else {
            impuesto.getCodigosEntity().clear();
        }


    }

}
