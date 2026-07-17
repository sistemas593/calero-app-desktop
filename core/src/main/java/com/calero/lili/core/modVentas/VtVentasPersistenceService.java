package com.calero.lili.core.modVentas;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.core.modCxC.XcFacturas.builder.XcFacturasBuilder;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VtVentasPersistenceService {

    private final VtVentasRepository vtVentaRepository;
    private final XcFacturasRepository xcFacturasRepository;
    private final XcFacturasBuilder xcFacturasBuilder;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;
    private final ComprobanteServiceImpl vtComprobanteService;


    @Transactional
    public VtVentaEntity guardarFactura(VtVentaEntity vtVentaEntity, AdEmpresaEntity empresa,
                                        AdEmpresasSeriesEntity serie, CreationFacturaRequestDto request,
                                        Long idData, Long idEmpresa, GeTerceroEntity tercero) {

        if (Objects.isNull(vtVentaEntity.getSecuencial())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa, serie.getIdSerie(), TipoDocumentoSerie.FAC);
            vtVentaEntity.setSecuencial(secuencial);
        }

        vtComprobanteService.getComprobanteXmlFactura(idData, vtVentaEntity, empresa, serie);
        VtVentaEntity saved = vtVentaRepository.save(vtVentaEntity);
        if (request.getCuentaPorCobrar()) {
            xcFacturasRepository.save(
                    xcFacturasBuilder.builderEntityFac(request, idData, idEmpresa, vtVentaEntity, tercero)
            );
        }
        return saved;
    }


    @Transactional
    public VtVentaEntity guardarNotaCredito(VtVentaEntity notaCredito) {
        return vtVentaRepository.save(notaCredito);
    }


    @Transactional
    public VtVentaEntity guardarNotaDebito(VtVentaEntity notaCredito) {
        return vtVentaRepository.save(notaCredito);
    }

}
