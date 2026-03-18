package com.calero.lili.core.modContabilidad.modPlanCuentas.builder;

import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaCreationRequestDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetListDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetOneDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CnPlanCuentaBuilder {

    public CnPlanCuentaEntity builderEntity(CnPlanCuentaCreationRequestDto model, Long idData, Long idEmpresa) {
        return CnPlanCuentaEntity.builder()
                .idCuenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idCuentaPadre(model.getIdCuentaPadre())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .mayor(model.getMayor())
                .tipoAuxiliar(model.getTipoAuxiliar())
                .build();
    }

    public CnPlanCuentaEntity builderUpdateEntity(CnPlanCuentaCreationRequestDto model, CnPlanCuentaEntity item) {
        return CnPlanCuentaEntity.builder()
                .idCuenta(item.getIdCuenta())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idCuentaPadre(model.getIdCuentaPadre())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .mayor(model.getMayor())
                .tipoAuxiliar(model.getTipoAuxiliar())
                .build();
    }

    public CnPlanCuentaGetOneDto builderResponse(CnPlanCuentaEntity model) {
        return CnPlanCuentaGetOneDto.builder()
                .idCuenta(model.getIdCuenta())
                .idCuentaPadre(model.getIdCuentaPadre())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .mayor(model.getMayor())
                .nivel(model.getNivel())
                .tipoAuxiliar(model.getTipoAuxiliar())
                .build();
    }

    public CnPlanCuentaGetListDto builderListResponse(CnPlanCuentaEntity model) {
        return CnPlanCuentaGetListDto.builder()
                .idCuenta(model.getIdCuenta())
                .idCuentaPadre(model.getIdCuentaPadre())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .mayor(model.getMayor())
                .nivel(model.getNivel())
                .tipoAuxiliar(model.getTipoAuxiliar())
                .build();
    }
}
