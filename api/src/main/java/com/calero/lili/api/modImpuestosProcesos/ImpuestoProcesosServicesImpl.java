package com.calero.lili.api.modImpuestosProcesos;


import com.calero.lili.api.dtos.FilterFechasDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionReferencias;
import com.calero.lili.api.modCompras.modComprasRetenciones.CpRetencionReferenciasRepository;
import com.calero.lili.api.modImpuestosProcesos.builder.ImpuestoProcesoBuilder;
import com.calero.lili.api.modImpuestosProcesos.dto.ImpuestoProcesoResponseDto;
import com.calero.lili.api.modImpuestosProcesos.projection.RetencionReferenciaProjection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImpuestoProcesosServicesImpl {

    private final CpImpuestosRepository cpImpuestosRepository;
    private final CpRetencionReferenciasRepository cpRetencionReferenciasRepository;
    private final ImpuestoProcesoBuilder impuestoProcesoBuilder;


    public List<ImpuestoProcesoResponseDto> validarReferencia(FilterFechasDto filtro) {

        List<ImpuestoProcesoResponseDto> response = new ArrayList<>();

        List<RetencionReferenciaProjection> retencionReferencias = cpRetencionReferenciasRepository
                .findAllProjection(filtro.getFechaEmisionDesde(), filtro.getFechaEmisionHasta());


        if (!retencionReferencias.isEmpty()) {
            for (RetencionReferenciaProjection referencia : retencionReferencias) {

                CpImpuestosEntity impuestos = cpImpuestosRepository
                        .findBySerieAndSecuencialAndNumeroIdentificacion(referencia.getSerie(),
                                referencia.getSecuencial(), referencia.getNumeroIdentificacion());

                if (Objects.nonNull(impuestos)) {
                    List<CpRetencionReferencias.ImpuestosCodigo> impuestosCodigos = convertJsonToList(referencia.getImpuestosCodigos());
                    cpRetencionReferenciasRepository.save(impuestoProcesoBuilder.builderRetencionUpdate(referencia,
                            impuestos, impuestosCodigos));
                    cpImpuestosRepository.save(impuestoProcesoBuilder.builderUpdateImpuesto(impuestos,
                            referencia.getIdRetencion(), impuestosCodigos, referencia.getFechaRegistro(), "02"));
                    response.add(impuestoProcesoBuilder.builderExistoso(impuestos));
                } else {
                    response.add(impuestoProcesoBuilder.builderError(referencia));
                }
            }
            return response;
        } else {
            throw new GeneralException("No existe impuestos para asignarse");
        }

    }

    public List<CpRetencionReferencias.ImpuestosCodigo> convertJsonToList(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    json,
                    new TypeReference<List<CpRetencionReferencias.ImpuestosCodigo>>() {
                    });
        } catch (Exception ex) {
            throw new GeneralException("No se pudo procesar los impuestos");
        }
    }


}
