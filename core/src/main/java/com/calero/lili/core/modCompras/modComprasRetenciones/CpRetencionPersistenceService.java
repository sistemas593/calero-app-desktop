package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.enums.OrigenImpuestos;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.ValidacionGeneralCpImpuestoService;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.ImpuestoCodigoBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.projection.DeEmitidasRetencionesProjection;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CpRetencionPersistenceService {

    private final ComprasRetencionesRepository comprasRetencionesRepository;
    private final ComprobanteServiceImpl comprobanteService;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;
    private final ValidacionGeneralCpImpuestoService validacionGeneralService;
    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleErrorBuilder;
    private final ImpuestoCodigoBuilder impuestoCodigoBuilder;
    private final CpImpuestosRepository cpImpuestosRepository;

    @Transactional
    public CpRetencionesEntity guardarRetencion(CpRetencionesEntity entidad, AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa,
                                                CreationRetencionRequestDto request, Map<UUID, CpImpuestosEntity> mapImpuestos) {


        if (Objects.isNull(entidad.getSecuencialRetencion())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa, serie.getIdSerie(),
                    TipoDocumentoSerie.CRT);
            request.setSecuencialRetencion(secuencial);
            entidad.setSecuencialRetencion(secuencial);
        }

        Optional<DeEmitidasRetencionesProjection> existingRetencion = comprasRetencionesRepository
                .findExistBySecuencial(idData, idEmpresa, request.getSerieRetencion(), request.getSecuencialRetencion());


        if (existingRetencion.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe : " +
                    "Serie: {0} Secuencia: {1}", request.getSerieRetencion(), request.getSecuencialRetencion()));
        }

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


        CpRetencionesEntity saved = comprasRetencionesRepository.save(entidad);

        Map<UUID, CompraImpuestosDto> mapImpuestosDto = builderCompraImpuestoMap(request);
        for (Map.Entry<UUID, CpImpuestosEntity> entry : mapImpuestos.entrySet()) {

            UUID id = entry.getKey();
            CpImpuestosEntity impuesto = entry.getValue();

            CompraImpuestosDto impuestoDto = mapImpuestosDto.get(id);

            if (permiteRetencion(impuesto.getOrigen())) {

                impuesto.setRetencion(saved);
                impuesto.setOrigen(OrigenImpuestos.RCC.name());

                List<CpImpuestosCodigosEntity> listCodigos =
                        impuestoCodigoBuilder.builderMultiList(impuestoDto.getListCodigosImpuesto(),
                                saved.getIdData(),
                                saved.getIdEmpresa());

                validateCodigosEntity(impuesto, listCodigos);
                cpImpuestosRepository.save(impuesto);
            }
        }


        return saved;
    }

    @Transactional
    public CpRetencionesEntity actualizarRetencion(CpRetencionesEntity entidad, AdEmpresaEntity empresa,
                                                   AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa,
                                                   CreationRetencionRequestDto request, Map<UUID, CpImpuestosEntity> mapImpuestos) {

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

        Map<UUID, CompraImpuestosDto> mapImpuestosDto = builderCompraImpuestoMap(request);
        for (Map.Entry<UUID, CpImpuestosEntity> entry : mapImpuestos.entrySet()) {

            UUID id = entry.getKey();
            CpImpuestosEntity impuesto = entry.getValue();

            CompraImpuestosDto impuestoDto = mapImpuestosDto.get(id);

            if (permiteRetencion(impuesto.getOrigen())) {

                impuesto.setRetencion(update);
                impuesto.setOrigen(OrigenImpuestos.RCC.name());

                List<CpImpuestosCodigosEntity> listCodigos =
                        impuestoCodigoBuilder.builderMultiList(impuestoDto.getListCodigosImpuesto(),
                                update.getIdData(),
                                update.getIdEmpresa());

                validateCodigosEntity(impuesto, listCodigos);
                cpImpuestosRepository.save(impuesto);
            }
        }

        return update;
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
