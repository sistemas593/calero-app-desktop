package com.calero.lili.core.modImpuestosAnexos.retencionFuente;


import com.calero.lili.core.dtos.FilterImpuestoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosAnexos.builder.RetencionFuenteBuilder;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolCabeceraRepository;
import com.calero.lili.core.modRRHH.modRRHHCabecera.projection.ReporteRolCabeceraProjection;
import com.calero.lili.core.modRRHH.modRRHHCabecera.projection.TalonResumenTrabajadoresProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

@Service
@AllArgsConstructor
public class TalonResumenRetencionFuenteServiceImpl {

    private final AdEmpresasRepository adEmpresasRepository;
    private final RhRolCabeceraRepository rhRolCabeceraRepository;
    private final RetencionFuenteBuilder retencionFuenteBuilder;


    public ValoresTalonResumenDto setearRetencionAlaFuente(FilterImpuestoDto filter, Long idData, Long idEmpresa) {

        List<TalonResumenTrabajadoresProjection> lista = rhRolCabeceraRepository
                .getTalResumenRetencionTrabajadores(idData, idEmpresa, filter.getFechaDesde(), filter.getFechaHasta());

        ValoresTalonResumenDto retencionFuente = retencionFuenteBuilder.builderRetencionFuentePdf();
        retencionFuente.setNumeroRegistros(lista.getFirst().getTotalRegistros());

        for (TalonResumenTrabajadoresProjection item : lista) {


            switch (item.getCodigo()) {

                case "ING-001" -> retencionFuente.setSuelSal(item.getTotalRubro());

                case "ING-002" -> retencionFuente.setSobSuelComRemu(item.getTotalRubro());

                case "ING-015" -> retencionFuente.setOtrosIngRenNoGrav(item.getTotalRubro());

                case "ING-017" -> retencionFuente.setOtrosIngRenGrav(item.getTotalRubro());

                case "ING-009" -> retencionFuente.setDecimTer(item.getTotalRubro());

                case "ING-010" -> retencionFuente.setDecimCuar(item.getTotalRubro());

                case "ING-013" -> retencionFuente.setFondoReserva(item.getTotalRubro());

                case "ING-018" -> retencionFuente.setSalarioDigno(item.getTotalRubro());

                case "ING-016" -> retencionFuente.setPartUtil(item.getTotalRubro());

                case "DES-001" -> retencionFuente.setApoPerIess(item.getTotalRubro());

                case "GTP-001" -> retencionFuente.setDeducVivienda(item.getTotalRubro());

                case "GTP-002" -> retencionFuente.setDeducSalud(item.getTotalRubro());

                case "GTP-003" -> retencionFuente.setDeducAliement(item.getTotalRubro());

                case "GTP-004" -> retencionFuente.setDeducEducartcult(item.getTotalRubro());

                case "GTP-005" -> retencionFuente.setDeducVestim(item.getTotalRubro());

                case "GTP-006" -> retencionFuente.setDeduccionTurismo(item.getTotalRubro());

                case "EXO-001" -> retencionFuente.setExoDiscap(item.getTotalRubro());

                case "EXO-002" -> retencionFuente.setExoTerEd(item.getTotalRubro());

                case "DES-008" -> retencionFuente.setValImpAsuEsteEmpl(item.getTotalRubro());

                case "RET-002" -> retencionFuente.setValRet(item.getTotalRubro());

                case "BAS-001" -> retencionFuente.setBasImp(item.getTotalRubro());

                case "RET-001" -> retencionFuente.setImpRentCaus(item.getTotalRubro());

                case "OTE-003" -> retencionFuente.setValRetAsuOtrosEmpls(item.getTotalRubro());

                case "OTE-002" -> retencionFuente.setAporPerIessConOtrosEmpls(item.getTotalRubro());

                case "OTE-001" -> retencionFuente.setIntGrabGen(item.getTotalRubro());


            }

        }

        retencionFuente.setIngGravConEsteEmpl(BigDecimal.ZERO);
        retencionFuente.setRebajaGastosPersonales(BigDecimal.ZERO);
        retencionFuente.setImpuestoRentaRebajaGastosPersonales(BigDecimal.ZERO);


        return retencionFuente;
    }

    public RetencionFuenteXmlDto setearRetencionFuenteXml(Long idData, Long idEmpresa, FilterImpuestoDto filter) {

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La empresa con id {0} no existe", idEmpresa)));

        List<ReporteRolCabeceraProjection> rolCabeceraProjections = rhRolCabeceraRepository
                .getRolCabeceraForPeriodo(idData, idEmpresa, filter.getFechaDesde(), filter.getFechaHasta());


        if (rolCabeceraProjections.isEmpty()) {
            return null;
        }

        Integer numeroRegistros = rolCabeceraProjections.size();
        return retencionFuenteBuilder.builderRetencionFuenteXmlDto(empresa, String.valueOf(numeroRegistros), filter.getFechaDesde(), rolCabeceraProjections);

    }
}
