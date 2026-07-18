package com.calero.lili.core.modCompras.modComprasLiquidaciones;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.enums.OrigenImpuestos;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.CreationRequestLiquidacionCompraDto;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LiquidacionPersistenceService {

    private final LiquidacionesRepository liquidacionesRepository;
    private final CpImpuestosServiceImpl cpImpuestosService;
    private final ComprobanteServiceImpl comprobanteService;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;

    @Transactional
    public CpLiquidacionesEntity guardarLiquidacion(CpLiquidacionesEntity cpLiquidacionesEntity,
                                                    AdEmpresaEntity empresa,
                                                    AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa,
                                                    CreationRequestLiquidacionCompraDto request) {


        if (Objects.isNull(cpLiquidacionesEntity.getSecuencial())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa,
                    serie.getIdSerie(), TipoDocumentoSerie.LIQ);
            cpLiquidacionesEntity.setSecuencial(secuencial);
        }

        comprobanteService.getComprobanteXmlLiquidacion(idData, cpLiquidacionesEntity, empresa, serie);
        CpLiquidacionesEntity saved = liquidacionesRepository.save(cpLiquidacionesEntity);
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
                    .origen(setearOrigen(item))
                    .build());
        });
        return listImpuesto;
    }

    private String setearOrigen(CreationCompraImpuestoRequestDto model) {
        if (Objects.nonNull(model.getImpuestoCodigos())) {
            return OrigenImpuestos.LCC.name();
        }
        return OrigenImpuestos.LSC.name();
    }

}
