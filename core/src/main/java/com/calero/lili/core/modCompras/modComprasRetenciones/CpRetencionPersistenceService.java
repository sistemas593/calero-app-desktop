package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.enums.OrigenImpuestos;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CpRetencionPersistenceService {

    private final ComprasRetencionesRepository comprasRetencionesRepository;
    private final CpImpuestosServiceImpl cpImpuestosService;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;

    @Transactional
    public CpRetencionesEntity guardarRetencion(CpRetencionesEntity entidad, CreationRetencionRequestDto request) {

        CpRetencionesEntity saved = comprasRetencionesRepository.save(entidad);
        guardarCpImpuesto(request, saved);
        return saved;
    }

    @Transactional
    public CpRetencionesEntity actualizarRetencion(CpRetencionesEntity entidad, CreationRetencionRequestDto request) {
        CpRetencionesEntity saved = comprasRetencionesRepository.save(entidad);
        actualizarCpImpuesto(request, saved);
        return saved;
    }

    private void guardarCpImpuesto(CreationRetencionRequestDto request,
                                   CpRetencionesEntity entidad) {
        if (Objects.nonNull(request.getCompraImpuestos())) {
            builderListSave(request).forEach(item -> {
                cpImpuestosService.guardarImpuestoRetencion(entidad, item);
            });
        }
    }

    private void actualizarCpImpuesto(CreationRetencionRequestDto request,
                                      CpRetencionesEntity entidad) {
        if (Objects.nonNull(request.getCompraImpuestos())) {
            builderListSave(request).forEach(item -> {
                cpImpuestosService.actualizarImpuestoRetencion(entidad, item);
            });
        }
    }

    private List<CompraImpuestosDto> builderListSave(CreationRetencionRequestDto request) {
        List<com.calero.lili.core.dtos.CompraImpuestosDto> listImpuesto = new ArrayList<>();

        request.getCompraImpuestos().forEach(item -> {
            listImpuesto.add(com.calero.lili.core.dtos.CompraImpuestosDto.builder()
                    .idCompraImpuesto(item.getCompraImpuestoId())
                    .listCodigosImpuesto(Objects.nonNull(item.getImpuestoCodigos())
                            ? item.getImpuestoCodigos()
                            : null)
                    .origen(OrigenImpuestos.RCC.name())
                    .build());
        });
        return listImpuesto;
    }


}
