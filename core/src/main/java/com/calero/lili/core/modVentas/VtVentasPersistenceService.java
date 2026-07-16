package com.calero.lili.core.modVentas;

import com.calero.lili.core.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.core.modCxC.XcFacturas.builder.XcFacturasBuilder;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VtVentasPersistenceService {

    private final VtVentasRepository vtVentaRepository;
    private final XcFacturasRepository xcFacturasRepository;
    private final XcFacturasBuilder xcFacturasBuilder;


    @Transactional
    public VtVentaEntity guardarFactura(VtVentaEntity vtVentaEntity, CreationFacturaRequestDto request,
                                        Long idData, Long idEmpresa, GeTerceroEntity tercero) {


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
