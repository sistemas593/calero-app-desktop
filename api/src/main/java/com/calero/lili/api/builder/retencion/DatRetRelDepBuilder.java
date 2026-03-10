package com.calero.lili.api.builder.retencion;

import com.calero.lili.api.modImpuestosAnexos.retencion.DatRetRelDep;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DatRetRelDepBuilder {

    private final EmpleadoBuilder empleadoBuilder;

    public List<DatRetRelDep> builderListRetRelDel(List<RetencionFuenteXmlDto.DetalleValores> detalleValores) {
        return detalleValores.stream()
                .map(this::builderRetDel)
                .toList();
    }

    private DatRetRelDep builderRetDel(RetencionFuenteXmlDto.DetalleValores model) {
        return DatRetRelDep.builder()
                .empleado(empleadoBuilder.builderEmpleado(model.getInfoEmpleado()))
                .suelSal(model.getSuelSal())
                .sobSuelComRemu(model.getSobSuelComRemu())
                .partUtil(model.getPartUtil())
                .intGrabGen(model.getIntGrabGen())
                .impRentEmpl(model.getImpRentEmpl())
                .decimTer(model.getDecimTer())
                .decimCuar(model.getDecimCuar())
                .fondoReserva(model.getFondoReserva())
                .salarioDigno(model.getSalarioDigno())
                .otrosIngRenGrav(model.getOtrosIngRenGrav())
                .ingGravConEsteEmpl(model.getIngGravConEsteEmpl())
                .apoPerIess(model.getApoPerIess())
                .aporPerIessConOtrosEmpls(model.getAporPerIessConOtrosEmpls())
                .deducVivienda(model.getDeducVivienda())
                .deducSalud(model.getDeducSalud())
                .deducEducartcult(model.getDeducEducartcult())
                .deducAliement(model.getDeducAliement())
                .deducVestim(model.getDeducVestim())
                .deduccionTurismo(model.getDeduccionTurismo())
                .exoDiscap(model.getExoDiscap())
                .exoTerEd(model.getExoTerEd())
                .basImp(model.getBasImp())
                .impRentCaus(model.getImpRentCaus())
                .rebajaGastosPersonales(model.getRebajaGastosPersonales())
                .impuestoRentaRebajaGastosPersonales(model.getImpuestoRentaRebajaGastosPersonales())
                .valRetAsuOtrosEmpls(model.getValRetAsuOtrosEmpls())
                .valImpAsuEsteEmpl(model.getValImpAsuEsteEmpl())
                .valRet(model.getValRet())
                .build();
    }
}
