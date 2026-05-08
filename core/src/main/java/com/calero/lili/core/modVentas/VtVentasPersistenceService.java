package com.calero.lili.core.modVentas;

import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.core.modCxC.XcFacturas.builder.XcFacturasBuilder;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.core.modVentas.notasDebito.dto.CreationNotaDebitoRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class VtVentasPersistenceService {

    private final VtVentasRepository vtVentaRepository;
    private final XcFacturasRepository xcFacturasRepository;
    private final XcFacturasBuilder xcFacturasBuilder;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;

    @Transactional
    public VtVentaEntity guardarFactura(VtVentaEntity vtVentaEntity, CreationFacturaRequestDto request,
                                        Long idData, Long idEmpresa, GeTerceroEntity tercero) {

        VtVentaEntity saved = vtVentaRepository.save(vtVentaEntity);

        if (request.getCuentaPorCobrar()) {
            xcFacturasRepository.save(
                    xcFacturasBuilder.builderEntityFac(request, idData, idEmpresa, vtVentaEntity.getIdVenta(), tercero)
            );
        }

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), TipoVenta.FAC.name())
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("Serie {0}, Secuencial {1}, documento {2} no existe",
                                request.getSerie(), request.getSecuencial(), TipoVenta.FAC.name())
                ));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        return saved;
    }


    @Transactional
    public VtVentaEntity guardarNotaCredito(VtVentaEntity notaCredito, CreationNotaCreditoRequestDto request,
                                            Long idData, Long idEmpresa) {

        VtVentaEntity saved = vtVentaRepository.save(notaCredito);

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), TipoVenta.FAC.name())
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("Serie {0}, Secuencial {1}, documento {2} no existe",
                                request.getSerie(), request.getSecuencial(), TipoVenta.FAC.name())
                ));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        return saved;
    }


    @Transactional
    public VtVentaEntity guardarNotaDebito(VtVentaEntity notaCredito, CreationNotaDebitoRequestDto request,
                                            Long idData, Long idEmpresa) {

        VtVentaEntity saved = vtVentaRepository.save(notaCredito);

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), TipoVenta.NDB.name())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe", request.getSerie(), TipoVenta.NDB.name())));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        return saved;
    }

}
