package com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.builder;

import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.TsBancosConciliacionesEntity;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.TsBancosConciliacionesProjection;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionCreationRequestDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionCreationResponseDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionReportDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TsBancosConciliacionesBuilder {

    public TsBancosConciliacionesEntity builderEntity(BcBancoConciliacionCreationRequestDto model,
                                                      Long idData, Long idEmpresa) {

        return TsBancosConciliacionesEntity.builder()
                .idConciliacion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .fechaCorte(DateUtils.toLocalDate(model.getFechaCorte()))
                .conciliadoSaldoInicial(model.getConciliadoSaldoInicial())
                .conciliadoDepositos(model.getConciliadoDepositos())
                .conciliadoNotasDebito(model.getConciliadoNotasDebito())
                .conciliadoNotasCredito(model.getConciliadoNotasCredito())
                .conciliadoCheques(model.getConciliadoCheques())
                .conciliadoSaldoFinal(model.getConciliadoSaldoFinal())
                .estadoCuentaDepositos(model.getEstadoCuentaDepositos())
                .estadoCuentaNotasCredito(model.getEstadoCuentaNotasCredito())
                .estadoCuentaNotasDebito(model.getEstadoCuentaNotasDebito())
                .estadoCuentaCheques(model.getEstadoCuentaCheques())
                .tsEntidadEntity(builderEntidad(model.getIdEntidad()))
                .build();
    }

    public TsBancosConciliacionesEntity builderUpdateEntity(BcBancoConciliacionCreationRequestDto model,
                                                            TsBancosConciliacionesEntity item) {

        return TsBancosConciliacionesEntity.builder()
                .idConciliacion(item.getIdConciliacion())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .fechaCorte(DateUtils.toLocalDate(model.getFechaCorte()))
                .conciliadoSaldoInicial(model.getConciliadoSaldoInicial())
                .conciliadoDepositos(model.getConciliadoDepositos())
                .conciliadoNotasDebito(model.getConciliadoNotasDebito())
                .conciliadoNotasCredito(model.getConciliadoNotasCredito())
                .conciliadoCheques(model.getConciliadoCheques())
                .conciliadoSaldoFinal(model.getConciliadoSaldoFinal())
                .estadoCuentaDepositos(model.getEstadoCuentaDepositos())
                .estadoCuentaNotasCredito(model.getEstadoCuentaNotasCredito())
                .estadoCuentaNotasDebito(model.getEstadoCuentaNotasDebito())
                .estadoCuentaCheques(model.getEstadoCuentaCheques())
                .tsEntidadEntity(builderEntidad(model.getIdEntidad()))
                .build();
    }

    public BcBancoConciliacionCreationResponseDto builderResponse(TsBancosConciliacionesEntity model){
        return BcBancoConciliacionCreationResponseDto.builder()
                .idConciliacion(model.getIdConciliacion())
                .fechaCorte(DateUtils.toString(model.getFechaCorte()))
                .conciliadoSaldoInicial(model.getConciliadoSaldoInicial())
                .conciliadoDepositos(model.getConciliadoDepositos())
                .conciliadoNotasDebito(model.getConciliadoNotasDebito())
                .conciliadoNotasCredito(model.getConciliadoNotasCredito())
                .conciliadoCheques(model.getConciliadoCheques())
                .conciliadoSaldoFinal(model.getConciliadoSaldoFinal())
                .estadoCuentaDepositos(model.getEstadoCuentaDepositos())
                .estadoCuentaNotasCredito(model.getEstadoCuentaNotasCredito())
                .estadoCuentaNotasDebito(model.getEstadoCuentaNotasDebito())
                .estadoCuentaCheques(model.getEstadoCuentaCheques())
                .build();
    }

    public BcBancoConciliacionReportDto builderListResponse(TsBancosConciliacionesProjection model){
        return BcBancoConciliacionReportDto.builder()
                .idConciliacion(model.getIdConciliacion())
                .fechaCorte(DateUtils.toString(model.getFechaCorte()))
                .conciliadoSaldoInicial(model.getConciliadoSaldoInicial())
                .conciliadoDepositos(model.getConciliadoDepositos())
                .conciliadoNotasDebito(model.getConciliadoNotasDebito())
                .conciliadoNotasCredito(model.getConciliadoNotasCredito())
                .conciliadoCheques(model.getConciliadoCheques())
                .conciliadoSaldoFinal(model.getConciliadoSaldoFinal())
                .estadoCuentaDepositos(model.getEstadoCuentaDepositos())
                .estadoCuentaNotasCredito(model.getEstadoCuentaNotasCredito())
                .estadoCuentaNotasDebito(model.getEstadoCuentaNotasDebito())
                .estadoCuentaCheques(model.getEstadoCuentaCheques())
                .build();
    }


    private TsEntidadEntity builderEntidad(UUID idEntidad) {
        return TsEntidadEntity.builder()
                .idEntidad(idEntidad)
                .build();
    }
}
