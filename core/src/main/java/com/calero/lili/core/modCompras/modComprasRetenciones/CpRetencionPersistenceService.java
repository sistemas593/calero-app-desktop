package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.enums.CodigoImpuesto;
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

    @Transactional
    public CpRetencionesEntity guardarRetencion(CpRetencionesEntity retencionesEntity, CreationRetencionRequestDto request) {

        CpRetencionesEntity saved = comprasRetencionesRepository.save(retencionesEntity);
        validarImpuesto(request, saved);

        return saved;
    }

    private void validarImpuesto(CreationRetencionRequestDto request,
                                 CpRetencionesEntity entidad) {
        if (Objects.nonNull(request.getCompraImpuestos())) {
            builderListSave(request).forEach(item -> {
                cpImpuestosService.updateImpuestoRetencion(entidad, item);
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
                    .origen(validarCodigoImpuesto(item))
                    .build());
        });
        return listImpuesto;
    }

    private String validarCodigoImpuesto(com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto model) {
        if (Objects.nonNull(model.getImpuestoCodigos())) {
            return CodigoImpuesto.IMP.name();
        }
        return CodigoImpuesto.IMC.name();
    }

}
