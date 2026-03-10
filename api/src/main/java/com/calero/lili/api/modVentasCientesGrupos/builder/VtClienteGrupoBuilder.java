package com.calero.lili.api.modVentasCientesGrupos.builder;

import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modVentasCientesGrupos.VtClienteGrupoEntity;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoCreationRequestDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoCreationResponseDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoReportDto;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class VtClienteGrupoBuilder {

    public VtClienteGrupoEntity builderEntity(VtClienteGrupoCreationRequestDto model, Long idData, Long idEmpresa) {
        return VtClienteGrupoEntity.builder()
                .idGrupo(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .grupo(model.getGrupo())
                .cuentaAnticipo(builderCuentaAnticipo(model.getIdCuentaAnticipos()))
                .cuentaCredito(builderCuentaCredito(model.getIdCuentaCredito()))
                .predeterminado(Boolean.FALSE)
                .build();
    }


    public VtClienteGrupoEntity builderUpdateEntity(VtClienteGrupoCreationRequestDto model, VtClienteGrupoEntity item) {
        return VtClienteGrupoEntity.builder()
                .idGrupo(item.getIdGrupo())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .grupo(model.getGrupo())
                .cuentaAnticipo(builderCuentaAnticipo(model.getIdCuentaAnticipos()))
                .cuentaCredito(builderCuentaCredito(model.getIdCuentaCredito()))
                .predeterminado(item.getPredeterminado())
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

    public VtClienteGrupoCreationResponseDto builderClienteGroupResponse(VtClienteGrupoEntity model) {
        return VtClienteGrupoCreationResponseDto.builder()
                .idGrupo(model.getIdGrupo())
                .grupo(model.getGrupo())
                .cuentaCredito(builderResponseCuentaCredito(model.getCuentaCredito()))
                .cuentaAnticipo(builderResponseCuentaAnticipo(model.getCuentaAnticipo()))
                .build();
    }

    private VtClienteGrupoCreationResponseDto.CuentaCredito builderResponseCuentaCredito(CnPlanCuentaEntity model) {
        if (Objects.isNull(model)) return null;
        return VtClienteGrupoCreationResponseDto.CuentaCredito.builder()
                .idCuentaCredito(model.getIdCuenta())
                .cuenta(model.getCuenta())
                .build();
    }

    private VtClienteGrupoCreationResponseDto.CuentaAnticipo builderResponseCuentaAnticipo(CnPlanCuentaEntity model) {
        if (Objects.isNull(model)) return null;
        return VtClienteGrupoCreationResponseDto.CuentaAnticipo.builder()
                .idCuentaAnticipo(model.getIdCuenta())
                .cuenta(model.getCuenta())
                .build();
    }

    public VtClienteGrupoReportDto builderListClienteGroupResponse(VtClienteGrupoEntity model) {
        return VtClienteGrupoReportDto.builder()
                .grupo(model.getGrupo())
                .idGrupo(model.getIdGrupo())
                .cuentaCredito(builderListResponseCuentaCredito(model.getCuentaCredito()))
                .cuentaAnticipo(builderListResponseCuentaAnticipo(model.getCuentaAnticipo()))
                .predeterminado(model.getPredeterminado())
                .build();
    }

    private VtClienteGrupoReportDto.CuentaAnticipo builderListResponseCuentaAnticipo(CnPlanCuentaEntity cuentaAnticipo) {
        if (Objects.isNull(cuentaAnticipo)) return null;
        return VtClienteGrupoReportDto.CuentaAnticipo.builder()
                .idCuentaAnticipo(cuentaAnticipo.getIdCuenta())
                .cuenta(cuentaAnticipo.getCuenta())
                .build();
    }

    private VtClienteGrupoReportDto.CuentaCredito builderListResponseCuentaCredito(CnPlanCuentaEntity cuentaCredito) {
        if (Objects.isNull(cuentaCredito)) return null;
        return VtClienteGrupoReportDto.CuentaCredito.builder()
                .idCuentaCredito(cuentaCredito.getIdCuenta())
                .cuenta(cuentaCredito.getCuenta())
                .build();
    }

}
