package com.calero.lili.core.modCompras.modComprasLiquidaciones;

import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.enums.CodigoImpuesto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.CreationRequestLiquidacionCompraDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LiquidacionPersistenceService {

    private final LiquidacionesRepository liquidacionesRepository;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;
    private final CpImpuestosServiceImpl cpImpuestosService;

    @Transactional
    public CpLiquidacionesEntity guardarLiquidacion(CpLiquidacionesEntity cpLiquidacionesEntity, CreationRequestLiquidacionCompraDto request, Long idData, Long idEmpresa) {

        CpLiquidacionesEntity saved = liquidacionesRepository.save(cpLiquidacionesEntity);


        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), "LIQ")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0} no existe", request.getSerie())));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        validarImpuesto(request, saved);

        return saved;
    }


    private void validarImpuesto(CreationRequestLiquidacionCompraDto request, CpLiquidacionesEntity entidad) {
        if (Objects.nonNull(request.getCompraImpuestos())) {
            builderListSave(request).forEach(item -> {
                cpImpuestosService.updateImpuestoLiqAndCompra(entidad.getIdData(),
                        entidad.getIdEmpresa(), entidad.getIdLiquidacion(), item);
            });
        }
    }


    private List<CompraImpuestosDto> builderListSave(CreationRequestLiquidacionCompraDto request) {
        List<CompraImpuestosDto> listImpuesto = new ArrayList<>();
        request.getCompraImpuestos().forEach(item -> {
            listImpuesto.add(CompraImpuestosDto.builder()
                    .idCompraImpuesto(item.getIdImpuestos())
                    .listCodigosImpuesto(Objects.nonNull(item.getImpuestoCodigos())
                            ? item.getImpuestoCodigos()
                            : null)
                    .origen(validarCodigoImpuesto(item))
                    .build());
        });
        return listImpuesto;
    }

    private String validarCodigoImpuesto(CreationCompraImpuestoRequestDto model) {
        if (Objects.nonNull(model.getImpuestoCodigos())) {
            return CodigoImpuesto.LIC.name();
        }
        return CodigoImpuesto.LIQ.name();
    }

}
