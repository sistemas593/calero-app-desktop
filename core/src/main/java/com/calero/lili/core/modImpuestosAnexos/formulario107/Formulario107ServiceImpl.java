package com.calero.lili.core.modImpuestosAnexos.formulario107;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosAnexos.builder.RetencionFuenteBuilder;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolCabeceraEntity;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolCabeceraRepository;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolDetalleEntity;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.TrabajadorEntity;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.TrabajadorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.UUID;

@Service
@AllArgsConstructor
public class Formulario107ServiceImpl {

    private final AdEmpresasRepository adEmpresasRepository;
    private final RhRolCabeceraRepository rhRolCabeceraRepository;
    private final RetencionFuenteBuilder retencionFuenteBuilder;
    private final TrabajadorRepository trabajadorRepository;


    public ImpuestosF107Dto setearImpuestosF107(Long idData, Long idEmpresa, UUID idTrabajador) {

        TrabajadorEntity trabajador = trabajadorRepository.getForFindById(idTrabajador)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El trabajador con id {0} no existe", idTrabajador)));

        RhRolCabeceraEntity cabecera = rhRolCabeceraRepository.getCabeceraForTercero(idData, idEmpresa, trabajador.getTercero().getIdTercero())
                .orElseThrow(() -> new GeneralException("La información del rol no existe"));

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La empresa con id {0} no existe", idEmpresa)));


        ImpuestosF107Dto f107 = retencionFuenteBuilder.builderImpuestoFormulario107(empresa, trabajador, cabecera.getTercero());

        for (RhRolDetalleEntity item : cabecera.getDetalles()) {

            switch (item.getRubros().getCodigo()) {

                case "ING-001" -> f107.setSuelSal(item.getValor());

                case "ING-002" -> f107.setSobSuelComRemu(item.getValor());

                case "ING-015" -> f107.setOtrosIngRenNoGrav(item.getValor());

                case "ING-017" -> f107.setOtrosIngRenGrav(item.getValor());

                case "ING-009" -> f107.setDecimTer(item.getValor());

                case "ING-010" -> f107.setDecimCuar(item.getValor());

                case "ING-013" -> f107.setFondoReserva(item.getValor());

                case "ING-018" -> f107.setSalarioDigno(item.getValor());

                case "ING-016" -> f107.setPartUtil(item.getValor());

                case "DES-001" -> f107.setApoPerIess(item.getValor());

                case "GTP-001" -> f107.setDeducVivienda(item.getValor());

                case "GTP-002" -> f107.setDeducSalud(item.getValor());

                case "GTP-003" -> f107.setDeducAliement(item.getValor());

                case "GTP-004" -> f107.setDeducEducartcult(item.getValor());

                case "GTP-005" -> f107.setDeducVestim(item.getValor());

                case "GTP-006" -> f107.setDeduccionTurismo(item.getValor());

                case "EXO-001" -> f107.setExoDiscap(item.getValor());

                case "EXO-002" -> f107.setExoTerEd(item.getValor());

                case "DES-008" -> f107.setValImpAsuEsteEmpl(item.getValor());

                case "RET-002" -> f107.setValRet(item.getValor());

                case "BAS-001" -> f107.setBasImp(item.getValor());

                case "RET-001" -> f107.setImpRentCaus(item.getValor());

                case "OTE-003" -> f107.setValRetAsuOtrosEmpls(item.getValor());

                case "OTE-002" -> f107.setAporPerIessConOtrosEmpls(item.getValor());

                case "OTE-001" -> f107.setIntGrabGen(item.getValor());
            }
        }

        f107.setImpRentEmpl(BigDecimal.ZERO);
        f107.setIngGravConEsteEmpl(BigDecimal.ZERO);
        f107.setRebajaGastosPersonales(BigDecimal.ZERO);
        f107.setImpuestoRentaRebajaGastosPersonales(BigDecimal.ZERO);

        return f107;
    }

}
