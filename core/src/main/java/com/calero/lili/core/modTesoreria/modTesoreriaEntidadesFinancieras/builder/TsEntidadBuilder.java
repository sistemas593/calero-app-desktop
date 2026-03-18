package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.builder;

import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.BcEntidadesProjection;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadCreationResponseDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadReportDto;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TsEntidadBuilder {


    public TsEntidadEntity builderEntity(BcEntidadCreationRequestDto model, Long idData, Long idEmpresa) {
        return TsEntidadEntity.builder()
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idEntidad(UUID.randomUUID())
                .entidad(model.getEntidad())
                .numeroCuenta(model.getNumeroCuenta())
                .agencia(model.getAgencia())
                .contacto(model.getContacto())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .secuencialCheque(model.getSecuencialCheque())
                .archivoCheque(model.getArchivoCheque())
                .saldo(model.getSaldo())
                .tipoEntidad(model.getTipoEntidad().toString())
                .planCuenta(builderPlanCuenta(model.getIdCuenta()))
                .build();
    }


    public TsEntidadEntity builderUpdateEntity(BcEntidadCreationRequestDto model, TsEntidadEntity item) {
        return TsEntidadEntity.builder()
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idEntidad(item.getIdEntidad())
                .entidad(model.getEntidad())
                .numeroCuenta(model.getNumeroCuenta())
                .agencia(model.getAgencia())
                .contacto(model.getContacto())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .secuencialCheque(model.getSecuencialCheque())
                .archivoCheque(model.getArchivoCheque())
                .saldo(model.getSaldo())
                .tipoEntidad(model.getTipoEntidad().toString())
                .planCuenta(builderPlanCuenta(model.getIdCuenta()))
                .build();
    }

    public BcEntidadCreationResponseDto builderResponse(TsEntidadEntity model) {
        return BcEntidadCreationResponseDto.builder()
                .idEntidad(model.getIdEntidad())
                .entidad(model.getEntidad())
                .numeroCuenta(model.getNumeroCuenta())
                .agencia(model.getAgencia())
                .contacto(model.getContacto())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .secuencialCheque(model.getSecuencialCheque())
                .archivoCheque(model.getArchivoCheque())
                .saldo(model.getSaldo())
                .tipoEntidad(model.getTipoEntidad())
                .planCuenta(builderPlanCuentaResponse(model.getPlanCuenta()))
                .build();
    }

    private BcEntidadCreationResponseDto.PlanCuenta builderPlanCuentaResponse(CnPlanCuentaEntity planCuenta) {
        if (Objects.isNull(planCuenta)) return null;
        return BcEntidadCreationResponseDto.PlanCuenta.builder()
                .idCuenta(planCuenta.getIdCuenta())
                .cuenta(planCuenta.getCuenta())
                .build();
    }

    public BcEntidadReportDto builderListResponse(BcEntidadesProjection model) {
        return BcEntidadReportDto.builder()
                .idEntidad(model.getIdEntidad())
                .tipoEntidad(model.getTipoEntidad())
                .entidad(model.getEntidad())
                .numeroCuenta(model.getNumeroCuenta())
                .agencia(model.getAgencia())
                .contacto(model.getContacto())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .secuencialCheque(model.getSecuencialCheque())
                .archivoCheque(model.getArchivoCheque())
                .saldo(model.getSaldo())
                .build();
    }

    private CnPlanCuentaEntity builderPlanCuenta(UUID idCuenta) {
        if (Objects.isNull(idCuenta)) return null;
        return CnPlanCuentaEntity.builder()
                .idCuenta(idCuenta)
                .build();
    }
}
