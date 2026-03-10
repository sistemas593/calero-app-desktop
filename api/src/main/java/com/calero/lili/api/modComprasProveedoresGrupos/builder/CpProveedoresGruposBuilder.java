package com.calero.lili.api.modComprasProveedoresGrupos.builder;

import com.calero.lili.api.modComprasProveedoresGrupos.CpProveedoresGruposEntity;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoCreationRequestDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoCreationResponseDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoReportDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class CpProveedoresGruposBuilder {

    public CpProveedoresGruposEntity builderEntity(CpProveedorGrupoCreationRequestDto model, Long idData, Long idEmpresa) {
        return CpProveedoresGruposEntity.builder()
                .idGrupo(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .grupo(model.getGrupo())
                .predeterminado(Boolean.FALSE)
                .cuentaAnticipo(builderCuentaAnticipo(model.getIdCuentaAnticipo()))
                .cuentaCredito(builderCuentaCredito(model.getIdCuentaCredito()))
                .build();
    }

    public CpProveedoresGruposEntity builderUpdateEntity(CpProveedorGrupoCreationRequestDto model, CpProveedoresGruposEntity item) {
        return CpProveedoresGruposEntity.builder()
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idGrupo(item.getIdGrupo())
                .grupo(model.getGrupo())
                .predeterminado(item.getPredeterminado())
                .cuentaAnticipo(builderCuentaAnticipo(model.getIdCuentaAnticipo()))
                .cuentaCredito(builderCuentaCredito(model.getIdCuentaCredito()))
                .build();
    }


    public CpProveedorGrupoCreationResponseDto builderResponse(CpProveedoresGruposEntity model) {
        return CpProveedorGrupoCreationResponseDto.builder()
                .idGrupo(model.getIdGrupo())
                .grupo(model.getGrupo())
                .cuentaAnticipo(builderResponseCuentaAnticipo(model.getCuentaAnticipo()))
                .cuentaCredito(builderResponseCuentaCredito(model.getCuentaCredito()))
                .build();
    }

    private CpProveedorGrupoCreationResponseDto.CuentaCredito builderResponseCuentaCredito(CnPlanCuentaEntity model) {
        if(Objects.isNull(model)) return null;
        return CpProveedorGrupoCreationResponseDto.CuentaCredito.builder()
                .idCuentaCredito(model.getIdCuenta())
                .cuenta(model.getCuenta())
                .build();
    }

    private CpProveedorGrupoCreationResponseDto.CuentaAnticipo builderResponseCuentaAnticipo(CnPlanCuentaEntity model) {
        if(Objects.isNull(model)) return null;
        return CpProveedorGrupoCreationResponseDto.CuentaAnticipo.builder()
                .idCuentaAnticipo(model.getIdCuenta())
                .cuenta(model.getCuenta())
                .build();
    }

    public CpProveedorGrupoReportDto builderListResponse(CpProveedoresGruposEntity model) {
        return CpProveedorGrupoReportDto.builder()
                .idGrupo(model.getIdGrupo())
                .grupo(model.getGrupo())
                .cuentaAnticipo(builderResponseListCuentaAnticipo(model.getCuentaAnticipo()))
                .cuentaCredito(builderResponseListCuentaCredito(model.getCuentaCredito()))
                .predeterminado(model.getPredeterminado())
                .build();
    }

    private CpProveedorGrupoReportDto.CuentaCredito builderResponseListCuentaCredito(CnPlanCuentaEntity model) {
        if(Objects.isNull(model)) return null;
        return CpProveedorGrupoReportDto.CuentaCredito.builder()
                .idCuentaCredito(model.getIdCuenta())
                .cuenta(model.getCuenta())
                .build();
    }

    private CpProveedorGrupoReportDto.CuentaAnticipo builderResponseListCuentaAnticipo(CnPlanCuentaEntity model) {
        if(Objects.isNull(model)) return null;
        return CpProveedorGrupoReportDto.CuentaAnticipo.builder()
                .idCuentaAnticipo(model.getIdCuenta())
                .cuenta(model.getCuenta())
                .build();
    }

    private CnPlanCuentaEntity builderCuentaCredito(UUID idCuentaCredito) {
        if (Objects.isNull(idCuentaCredito)) return null;
        return CnPlanCuentaEntity.builder()
                .idCuenta(idCuentaCredito)
                .build();
    }

    private CnPlanCuentaEntity builderCuentaAnticipo(UUID idCuentaAnticipos) {
        if (Objects.isNull(idCuentaAnticipos)) return null;
        return CnPlanCuentaEntity.builder()
                .idCuenta(idCuentaAnticipos)
                .build();
    }
}
