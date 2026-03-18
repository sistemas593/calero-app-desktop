package com.calero.lili.core.modRRHH.modRRHHRublos.builder;

import com.calero.lili.core.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.core.modRRHH.modRRHHRublos.dto.RubroRequestDto;
import com.calero.lili.core.modRRHH.modRRHHRublos.dto.RubroResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RubroBuilder {

    public RubrosEntity builderEntity(RubroRequestDto model, Long idEmpresa, Long idData) {
        return RubrosEntity.builder()
                .idRubro(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigo(model.getCodigo())
                .rubro(model.getRubro())
                .afectaIees(model.getAfectaIees())
                .tipo(model.getTipo())
                .afectaImpuestoRenta(model.getAfectaImpuestoRenta())
                .esObligatorio(model.getEsObligatorio())
                .build();
    }

    public RubrosEntity builderUpdateEntity(RubroRequestDto model, RubrosEntity item) {
        return RubrosEntity.builder()
                .idRubro(item.getIdRubro())
                .idEmpresa(item.getIdEmpresa())
                .idData(item.getIdData())
                .codigo(model.getCodigo())
                .rubro(model.getRubro())
                .afectaIees(model.getAfectaIees())
                .tipo(model.getTipo())
                .afectaImpuestoRenta(model.getAfectaImpuestoRenta())
                .esObligatorio(model.getEsObligatorio())
                .build();
    }


    public RubroResponseDto builderResponse(RubrosEntity model) {
        return RubroResponseDto.builder()
                .idRubro(model.getIdRubro())
                .codigo(model.getCodigo())
                .idData(model.getIdData())
                .idEmpresa(model.getIdEmpresa())
                .rubro(model.getRubro())
                .afectaIees(model.getAfectaIees())
                .tipo(model.getTipo())
                .afectaImpuestoRenta(model.getAfectaImpuestoRenta())
                .esObligatorio(model.getEsObligatorio())
                .build();
    }

}
