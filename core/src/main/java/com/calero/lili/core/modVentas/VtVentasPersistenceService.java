package com.calero.lili.core.modVentas;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.core.modCxC.XcFacturas.builder.XcFacturasBuilder;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.modVentas.projection.OneProjection;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

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

        try {
            if (Objects.isNull(vtVentaEntity.getSecuencial())) {
                String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa, serie.getIdSerie(), TipoDocumentoSerie.FAC);
                request.setSecuencial(secuencial);
                vtVentaEntity.setSecuencial(secuencial);
            }

            Optional<OneProjection> existingFactura = vtVentaRepository
                    .findExistBySecuencial(idData, idEmpresa, TipoVenta.FAC.name(), request.getSerie(), request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("El documento ya existe TipoIngreso:" +
                        " {0} Serie: {1} Secuencia: {2}", TipoVenta.FAC.name(), request.getSerie(), request.getSecuencial()));
            }


            vtComprobanteService.getComprobanteXmlFactura(idData, vtVentaEntity, empresa, serie);
            VtVentaEntity saved = vtVentaRepository.save(vtVentaEntity);
            if (request.getCuentaPorCobrar()) {
                xcFacturasRepository.save(
                        xcFacturasBuilder.builderEntityFac(request, idData, idEmpresa, vtVentaEntity, tercero)
                );
            }

            return saved;
        } catch (DataIntegrityViolationException exception) {
            throw new GeneralException(exception.getMessage());
        }

    }


    @Transactional
    public VtVentaEntity guardarNotaCredito(VtVentaEntity notaCredito, AdEmpresaEntity empresa,
                                            AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa) {


        if (Objects.isNull(notaCredito.getSecuencial())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa,
                    serie.getIdSerie(), TipoDocumentoSerie.NCR);
            notaCredito.setSecuencial(secuencial);
        }

        Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData,
                idEmpresa, TipoVenta.NCR.name(), serie.getSerie(), notaCredito.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe Tipo: {0} Serie: {1} Secuencia: {2}",
                    TipoVenta.NCR.name(), serie.getSerie(), notaCredito.getSecuencial()));
        }

        vtComprobanteService.getComprobanteXmlNotaCredito(idData, notaCredito, empresa, serie);

        return vtVentaRepository.save(notaCredito);
    }


    @Transactional
    public VtVentaEntity guardarNotaDebito(VtVentaEntity notaDebito,
                                           AdEmpresaEntity empresa,
                                           AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa) {

        if (Objects.isNull(notaDebito.getSecuencial())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa, serie.getIdSerie(),
                    TipoDocumentoSerie.NDB);
            notaDebito.setSecuencial(secuencial);
        }


        Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa,
                TipoVenta.NDB.name(), serie.getSerie(), notaDebito.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe Tipo: {0} Serie: {1} Secuencial: {2}",
                    TipoVenta.NDB.name(), serie.getSerie(), notaDebito.getSecuencial()));
        }

        vtComprobanteService.getComprobanteXmlNotaDebito(idData, notaDebito, empresa, serie);
        return vtVentaRepository.save(notaDebito);
    }

}
