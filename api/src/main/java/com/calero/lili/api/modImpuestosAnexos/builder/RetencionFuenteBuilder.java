package com.calero.lili.api.modImpuestosAnexos.builder;

import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.TalonResumenPdfDto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.projection.ReporteRolCabeceraProjection;
import com.calero.lili.api.modRRHH.modRRHHTrabajadores.TrabajadorEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.utils.validaciones.ValidarValoresComprobantesPdf;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class RetencionFuenteBuilder {


    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;

    public RetencionFuenteXmlDto builderRetencionFuenteXmlDto(AdEmpresaEntity empresa, String size,
                                                              LocalDate fecha, List<ReporteRolCabeceraProjection> list) {

        return RetencionFuenteXmlDto.builder()
                .razonSocial(empresa.getRazonSocial())
                .numRuc(empresa.getRuc())
                .anio("2026")
                .numeroRegistros(size)
                .tipoEmpleador("PRIVADO-MIXTO")
                .enteSegSocial("IESS")
                .detalleValores(builderListDetalleValores(list))
                .build();
    }

    private List<RetencionFuenteXmlDto.DetalleValores> builderListDetalleValores(List<ReporteRolCabeceraProjection> list) {
        return list
                .stream()
                .map(this::builderDetalleValores)
                .toList();
    }

    private RetencionFuenteXmlDto.DetalleValores builderDetalleValores(ReporteRolCabeceraProjection model) {

        return RetencionFuenteXmlDto.DetalleValores.builder()
                .infoEmpleado(builderInfoEmpleado(model))
                .suelSal(validarValoresComprobantesPdf.getValor(model.getIngresos()))
                .sobSuelComRemu(validarValoresComprobantesPdf.getValor(model.getOtrosIngresosGravadosImpuestoRenta()))
                .partUtil(validarValoresComprobantesPdf.getValor(model.getParticipacionUtilidades()))
                .intGrabGen(validarValoresComprobantesPdf.getValor(model.getIngresosGravadosOtrosEmpleadores()))
                .impRentEmpl(validarValoresComprobantesPdf.getValor(model.getImpuestoRentaAsumidoEmpleador()))
                .decimTer(validarValoresComprobantesPdf.getValor(model.getDecimoTercero()))
                .decimCuar(validarValoresComprobantesPdf.getValor(model.getDecimoCuarto()))
                .fondoReserva(validarValoresComprobantesPdf.getValor(model.getFondosDeReserva()))
                .salarioDigno(validarValoresComprobantesPdf.getValor(model.getSalarioDigno()))
                .otrosIngRenGrav(validarValoresComprobantesPdf.getValor(model.getOtrosIngresosRelacionDependenciaNoRentaGravada()))
                .ingGravConEsteEmpl(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO)) // TODO TOTALIZAR
                .sisSalNet(model.getCodigoSalario())
                .apoPerIess(validarValoresComprobantesPdf.getValor(model.getAporteIessPersonal()))
                .aporPerIessConOtrosEmpls(validarValoresComprobantesPdf.getValor(model.getIessOtrosEmpleadores()))
                .deducVivienda(validarValoresComprobantesPdf.getValor(model.getGastosVivienda()))
                .deducSalud(validarValoresComprobantesPdf.getValor(model.getGastosSalud()))
                .deducAliement(validarValoresComprobantesPdf.getValor(model.getGastosAlimentacion()))
                .deducEducartcult(validarValoresComprobantesPdf.getValor(model.getGastosEducacion()))
                .deducVestim(validarValoresComprobantesPdf.getValor(model.getGastosVestimenta()))
                .deduccionTurismo(validarValoresComprobantesPdf.getValor(model.getGastosTurismo()))
                .exoDiscap(validarValoresComprobantesPdf.getValor(model.getExoneracionDiscapacidad()))
                .exoTerEd(validarValoresComprobantesPdf.getValor(model.getExoneracionTerceraEdad()))
                .basImp(validarValoresComprobantesPdf.getValor(model.getBaseImponible()))
                .impRentCaus(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO)) // TODO TOTALIZAR
                .rebajaGastosPersonales(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO)) // TODO TOTALIZAR
                .impuestoRentaRebajaGastosPersonales(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO)) // TODO TOTALIZAR
                .valRetAsuOtrosEmpls(validarValoresComprobantesPdf.getValor(model.getRetenidoAsumidoOtrosEmpleadores()))
                .valImpAsuEsteEmpl(validarValoresComprobantesPdf.getValor(BigDecimal.ZERO)) // TODO PREGUNTAR POR QUE NO ES EL DES-008 YA QUE ESTE ES SOLO DE IMPUESTO EL OTRO ES DE IMPUESTO RENTA
                .valRet(validarValoresComprobantesPdf.getValor(model.getImpuestoRentaRetenido()))
                .build();
    }

    private RetencionFuenteXmlDto.InfoEmpleadoDto builderInfoEmpleado(ReporteRolCabeceraProjection item) {
        return RetencionFuenteXmlDto.InfoEmpleadoDto.builder()
                .benGalpg(item.getBeneficioProvGalapagos())
                .enfcatastro(item.getEnfermedadCatastrofica())
                .numCargRebGastPers(Objects.nonNull(item.getNumeroCargas()) ? item.getNumeroCargas().toString() : "0")
                .tipIdRet(item.getTipoIdentificacion())
                .idRet(item.getNumeroIdentificacion())
                .apellidoTrab(item.getApellidos())
                .nombreTrab(item.getNombres())
                .estab(item.getCodigoEstablecimiento())
                .residenciaTrab(item.getCodigoResidencia())
                .paisResidencia(item.getCodigoPais())
                .aplicaConvenio(item.getAplicaConvenio())
                .tipoTrabajDiscap(item.getTipoDiscapacidad())
                .porcentajeDiscap(item.getPorcentajeDiscapacidad())
                .tipIdDiscap(item.getTipoIdDiscapacidad())
                .idDiscap(item.getIdDiscapacidad())
                .build();
    }

    public ValoresTalonResumenDto builderRetencionFuentePdf() {
        return ValoresTalonResumenDto.builder()
                .suelSal(BigDecimal.ZERO)
                .sobSuelComRemu(BigDecimal.ZERO)
                .partUtil(BigDecimal.ZERO)
                .intGrabGen(BigDecimal.ZERO)
                .impRentEmpl(BigDecimal.ZERO)
                .decimTer(BigDecimal.ZERO)
                .decimCuar(BigDecimal.ZERO)
                .fondoReserva(BigDecimal.ZERO)
                .salarioDigno(BigDecimal.ZERO)
                .otrosIngRenGrav(BigDecimal.ZERO)
                .ingGravConEsteEmpl(BigDecimal.ZERO)
                .apoPerIess(BigDecimal.ZERO)
                .aporPerIessConOtrosEmpls(BigDecimal.ZERO)
                .deducVivienda(BigDecimal.ZERO)
                .deducSalud(BigDecimal.ZERO)
                .deducEducartcult(BigDecimal.ZERO)
                .deducAliement(BigDecimal.ZERO)
                .deducVestim(BigDecimal.ZERO)
                .deduccionTurismo(BigDecimal.ZERO)
                .exoDiscap(BigDecimal.ZERO)
                .exoTerEd(BigDecimal.ZERO)
                .basImp(BigDecimal.ZERO)
                .impRentCaus(BigDecimal.ZERO)
                .rebajaGastosPersonales(BigDecimal.ZERO)
                .impuestoRentaRebajaGastosPersonales(BigDecimal.ZERO)
                .valRetAsuOtrosEmpls(BigDecimal.ZERO)
                .valImpAsuEsteEmpl(BigDecimal.ZERO)
                .valRet(BigDecimal.ZERO)
                .otrosIngRenNoGrav(BigDecimal.ZERO)
                .build();
    }

    public TalonResumenPdfDto builderTalonResumen(AdEmpresaEntity empresa, ValoresTalonResumenDto model) {
        return TalonResumenPdfDto.builder()
                .razonSocial(empresa.getRazonSocial())
                .numRuc(empresa.getRuc())
                .numeroRegistros(String.valueOf(model.getNumeroRegistros()))
                .numIdentificacionContador(empresa.getContadorRuc())
                .numIdentificacionRepLegal(empresa.getRepresentanteIdentificacion())
                .fechaCarga(DateUtils.obtenerFechaHora(LocalDateTime.now()))
                .anio(String.valueOf(LocalDate.now().getYear()))
                .suelSal(validarValoresComprobantesPdf.getValor(model.getSuelSal().add(model.getSobSuelComRemu())))
                .sobSuelComRemu(validarValoresComprobantesPdf.getValor(model.getSobSuelComRemu()))
                .partUtil(validarValoresComprobantesPdf.getValor(model.getPartUtil()))
                .intGrabGen(validarValoresComprobantesPdf.getValor(model.getIntGrabGen()))
                .impRentEmpl(validarValoresComprobantesPdf.getValor(model.getImpRentEmpl()))
                .decimTer(validarValoresComprobantesPdf.getValor(model.getDecimTer()))
                .decimCuar(validarValoresComprobantesPdf.getValor(model.getDecimCuar()))
                .fondoReserva(validarValoresComprobantesPdf.getValor(model.getFondoReserva()))
                .salarioDigno(validarValoresComprobantesPdf.getValor(model.getSalarioDigno()))
                .otrosIngRenGrav(validarValoresComprobantesPdf.getValor(model.getOtrosIngRenGrav()))
                .ingGravConEsteEmpl(validarValoresComprobantesPdf.getValor(model.getIngGravConEsteEmpl()))
                .apoPerIess(validarValoresComprobantesPdf.getValor(model.getApoPerIess()))
                .aporPerIessConOtrosEmpls(validarValoresComprobantesPdf.getValor(model.getAporPerIessConOtrosEmpls()))
                .deducVivienda(validarValoresComprobantesPdf.getValor(model.getDeducVivienda()))
                .deducSalud(validarValoresComprobantesPdf.getValor(model.getDeducSalud()))
                .deducEducartcult(validarValoresComprobantesPdf.getValor(model.getDeducEducartcult()))
                .deducAliement(validarValoresComprobantesPdf.getValor(model.getDeducAliement()))
                .deducVestim(validarValoresComprobantesPdf.getValor(model.getDeducVestim()))
                .deduccionTurismo(validarValoresComprobantesPdf.getValor(model.getDeduccionTurismo()))
                .exoDiscap(validarValoresComprobantesPdf.getValor(model.getExoDiscap()))
                .exoTerEd(validarValoresComprobantesPdf.getValor(model.getExoTerEd()))
                .basImp(validarValoresComprobantesPdf.getValor(model.getBasImp()))
                .impRentCaus(validarValoresComprobantesPdf.getValor(model.getImpRentCaus()))
                .rebajaGastosPersonales(validarValoresComprobantesPdf.getValor(model.getRebajaGastosPersonales()))
                .impuestoRentaRebajaGastosPersonales(validarValoresComprobantesPdf.getValor(model.getImpuestoRentaRebajaGastosPersonales()))
                .valRetAsuOtrosEmpls(validarValoresComprobantesPdf.getValor(model.getValRetAsuOtrosEmpls()))
                .valImpAsuEsteEmpl(validarValoresComprobantesPdf.getValor(model.getValImpAsuEsteEmpl()))
                .valRet(validarValoresComprobantesPdf.getValor(model.getValRet()))
                .otrosIngRenNoGrav(validarValoresComprobantesPdf.getValor(model.getOtrosIngRenNoGrav()))
                .build();
    }

    public ImpuestosF107Dto builderImpuestoFormulario107(AdEmpresaEntity empresa,
                                                         TrabajadorEntity trabajador, GeTerceroEntity tercero) {
        return ImpuestosF107Dto.builder()
                .ano(String.valueOf(LocalDate.now().getYear()))
                .ruc(empresa.getRuc())
                .razonSocial(empresa.getRazonSocial())
                .rucContador(empresa.getContadorRuc())
                .benGalpg(trabajador.getBeneficioProvGalapagos())
                .enfcatastro(trabajador.getEnfermedadCatastrofica())
                .numCargRebGastPers(1)
                .tipIdRet(tercero.getTipoIdentificacion())
                .idRet(tercero.getNumeroIdentificacion())
                .apellidoTrab(trabajador.getApellidos())
                .nombreTrab(trabajador.getNombres())
                .estab(trabajador.getCodigoEstablecimiento())
                .residenciaTrab(trabajador.getCodigoResidencia())
                .paisResidencia(String.valueOf(trabajador.getPais()))
                .aplicaConvenio(trabajador.getAplicaConvenio())
                .tipoTrabajDiscap(trabajador.getTipoDiscapacidad())
                .porcentajeDiscap(trabajador.getPorcentajeDiscapacidad())
                .tipIdDiscap(trabajador.getTipoIdDiscapacidad())
                .idDiscap(trabajador.getIdDiscapacidad())
                .suelSal(BigDecimal.ZERO)
                .sobSuelComRemu(BigDecimal.ZERO)
                .partUtil(BigDecimal.ZERO)
                .intGrabGen(BigDecimal.ZERO)
                .impRentEmpl(BigDecimal.ZERO)
                .decimTer(BigDecimal.ZERO)
                .decimCuar(BigDecimal.ZERO)
                .fondoReserva(BigDecimal.ZERO)
                .salarioDigno(BigDecimal.ZERO)
                .otrosIngRenGrav(BigDecimal.ZERO)
                .ingGravConEsteEmpl(BigDecimal.ZERO)
                .apoPerIess(BigDecimal.ZERO)
                .aporPerIessConOtrosEmpls(BigDecimal.ZERO)
                .deducVivienda(BigDecimal.ZERO)
                .deducSalud(BigDecimal.ZERO)
                .deducEducartcult(BigDecimal.ZERO)
                .deducAliement(BigDecimal.ZERO)
                .deducVestim(BigDecimal.ZERO)
                .deduccionTurismo(BigDecimal.ZERO)
                .exoDiscap(BigDecimal.ZERO)
                .exoTerEd(BigDecimal.ZERO)
                .basImp(BigDecimal.ZERO)
                .impRentCaus(BigDecimal.ZERO)
                .rebajaGastosPersonales(BigDecimal.ZERO)
                .impuestoRentaRebajaGastosPersonales(BigDecimal.ZERO)
                .valRetAsuOtrosEmpls(BigDecimal.ZERO)
                .valImpAsuEsteEmpl(BigDecimal.ZERO)
                .valRet(BigDecimal.ZERO)
                .otrosIngRenNoGrav(BigDecimal.ZERO)
                .build();
    }


}
