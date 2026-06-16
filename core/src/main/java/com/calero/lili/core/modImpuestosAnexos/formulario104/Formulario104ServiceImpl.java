package com.calero.lili.core.modImpuestosAnexos.formulario104;

import com.calero.lili.core.dtos.FilterImpuestoDto;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF104Dto;
import com.calero.lili.core.modVentas.projection.ImpuestosF104Projection;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class Formulario104ServiceImpl {


    private final AdEmpresasRepository adEmpresasRepository;
    private final Formulario104Repository formulario104Repository;

    public ImpuestosF104Dto setearImpuestosF104(FilterImpuestoDto filter, Long idData, Long idEmpresa) {

        AdEmpresaEntity adEmpresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("La empresa no existe"));

        if (Objects.nonNull(filter.getFechaDesde()) && Objects.nonNull(filter.getFechaHasta())) {

            ImpuestosF104Dto f104 = new ImpuestosF104Dto();

            f104.setAno(String.valueOf(filter.getFechaDesde().getYear()));
            f104.setMes(DateUtils.obtenerMesLetras(filter.getFechaHasta()));
            f104.setRuc(adEmpresa.getRuc());
            f104.setRazonSocial(adEmpresa.getRazonSocial());


            Optional<ImpuestosF104Projection> valorBrutoVentasLocales15 = formulario104Repository.valorBruto15(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());

            Optional<ImpuestosF104Projection> notasCreditoVentasLocales15 = formulario104Repository.notasCredito15(idData, idEmpresa,
                    TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());

            BigDecimal valorBrutoVL15 = BigDecimal.ZERO;
            BigDecimal notasCreditoVL15 = BigDecimal.ZERO;

            BigDecimal impuestoVL15 = BigDecimal.ZERO;
            BigDecimal notaCreditoImpuestoVL15 = BigDecimal.ZERO;


            if (valorBrutoVentasLocales15.isPresent()) {
                valorBrutoVL15 = valorBrutoVL15.add(valorBrutoVentasLocales15.get().getBaseImponible());
                impuestoVL15 = impuestoVL15.add(valorBrutoVentasLocales15.get().getValor());
            }

            if (notasCreditoVentasLocales15.isPresent()) {
                notasCreditoVL15 = notasCreditoVL15.add(notasCreditoVentasLocales15.get().getBaseImponible());
                notaCreditoImpuestoVL15 = notaCreditoImpuestoVL15.add(notasCreditoVentasLocales15.get().getValor());
            }


            f104.setC401(valorBrutoVL15);
            f104.setC411(valorBrutoVL15.subtract(notasCreditoVL15));
            f104.setC421(impuestoVL15.subtract(notaCreditoImpuestoVL15));


            Optional<ImpuestosF104Projection> valorBrutoVentasLocalesBaseCero = formulario104Repository.valorBrutoBaseCero
                    (idData, idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasCreditoVentasLocalesBaseCero = formulario104Repository.notasCreditoBaseCero(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            BigDecimal valorBrutoVLBaseCero = BigDecimal.ZERO;
            BigDecimal notaCreditoVLBaseCero = BigDecimal.ZERO;

            if (valorBrutoVentasLocalesBaseCero.isPresent()) {
                valorBrutoVLBaseCero = valorBrutoVLBaseCero.add(valorBrutoVentasLocalesBaseCero.get().getBaseImponible());
            }

            if (notasCreditoVentasLocalesBaseCero.isPresent()) {
                notaCreditoVLBaseCero = notaCreditoVLBaseCero.add(notasCreditoVentasLocalesBaseCero.get().getBaseImponible());
            }

            f104.setC403(valorBrutoVLBaseCero);
            f104.setC413(valorBrutoVLBaseCero.subtract(notaCreditoVLBaseCero));


            Optional<ImpuestosF104Projection> valorBrutoVentasLocalesExcentaYNoObjecto = formulario104Repository
                    .valorBrutoVentasLocalesExcentaYNoObjecto(idData, idEmpresa, TipoIngreso.VL.name(),
                            filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasedCreditoVentasLocalesExentaYNoObjecto = formulario104Repository
                    .notasCreditoVentasLocalesExentaYNoObjecto(idData, idEmpresa, TipoIngreso.VL.name(),
                            filter.getFechaDesde(), filter.getFechaHasta());


            BigDecimal valorBrutoVLExcentaNoObjecto = BigDecimal.ZERO;
            BigDecimal notaCreditoVLExcentaNoObjecto = BigDecimal.ZERO;


            if (valorBrutoVentasLocalesExcentaYNoObjecto.isPresent()) {
                valorBrutoVLExcentaNoObjecto = valorBrutoVLExcentaNoObjecto.add(valorBrutoVentasLocalesExcentaYNoObjecto.get().getBaseImponible());
            }

            if (notasedCreditoVentasLocalesExentaYNoObjecto.isPresent()) {
                notaCreditoVLExcentaNoObjecto = notaCreditoVLExcentaNoObjecto.add(notasedCreditoVentasLocalesExentaYNoObjecto.get().getBaseImponible());
            }

            f104.setC431(valorBrutoVLExcentaNoObjecto);
            f104.setC441(valorBrutoVLExcentaNoObjecto.subtract(notaCreditoVLExcentaNoObjecto));


            Optional<ImpuestosF104Projection> valorBrutoVentasLocales5 = formulario104Repository.valorBrutoVentasLocales5
                    (idData, idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());

            Optional<ImpuestosF104Projection> notasCreditoVentaLocales5 = formulario104Repository.notasCreditoVentasLocalesTarifa5(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            BigDecimal valorBrutoVL5 = BigDecimal.ZERO;
            BigDecimal notaCreditoVL5 = BigDecimal.ZERO;
            BigDecimal impuestoVL5 = BigDecimal.ZERO;
            BigDecimal notaCreditoImpuestoVL5 = BigDecimal.ZERO;

            if (valorBrutoVentasLocales5.isPresent()) {
                valorBrutoVL5 = valorBrutoVL5.add(valorBrutoVentasLocales5.get().getBaseImponible());
                impuestoVL5 = impuestoVL5.add(valorBrutoVentasLocales5.get().getValor());
            }

            if (notasCreditoVentaLocales5.isPresent()) {
                notaCreditoVL5 = notaCreditoVL5.add(notasCreditoVentaLocales5.get().getBaseImponible());
                notaCreditoImpuestoVL5 = notaCreditoImpuestoVL5.add(notasCreditoVentaLocales5.get().getValor());
            }

            f104.setC445(valorBrutoVL5);
            f104.setC425(valorBrutoVL5.subtract(notaCreditoVL5));
            f104.setC435(impuestoVL5.subtract(notaCreditoImpuestoVL5));


            Optional<ImpuestosF104Projection> valorBrutoActivoFijo15 = formulario104Repository.valorBruto15(idData,
                    idEmpresa, TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasCreditoActivoFijo15 = formulario104Repository.notasCredito15(idData, idEmpresa,
                    TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            BigDecimal valorBrutoAF15 = BigDecimal.ZERO;
            BigDecimal notaCreditoAF15 = BigDecimal.ZERO;
            BigDecimal impuestoAF15 = BigDecimal.ZERO;
            BigDecimal notaCreditoImpuestoAF15 = BigDecimal.ZERO;


            if (valorBrutoActivoFijo15.isPresent()) {
                valorBrutoAF15 = valorBrutoAF15.add(valorBrutoActivoFijo15.get().getBaseImponible());
                notaCreditoAF15 = notaCreditoAF15.add(valorBrutoActivoFijo15.get().getValor());
            }

            if (notasCreditoActivoFijo15.isPresent()) {
                impuestoAF15 = impuestoAF15.add(notasCreditoActivoFijo15.get().getBaseImponible());
                notaCreditoImpuestoAF15 = notaCreditoImpuestoAF15.add(notasCreditoActivoFijo15.get().getValor());
            }

            f104.setC402(valorBrutoAF15);
            f104.setC412(valorBrutoAF15.subtract(impuestoAF15));
            f104.setC422(notaCreditoAF15.subtract(notaCreditoImpuestoAF15));


            Optional<ImpuestosF104Projection> valorBrutoActivoFijoBaseCero = formulario104Repository.valorBrutoBaseCero
                    (idData, idEmpresa, TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasCreditoActivoFijoBaseCero = formulario104Repository.notasCreditoBaseCero(idData,
                    idEmpresa, TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            BigDecimal valorBrutoAFBaseCero = BigDecimal.ZERO;
            BigDecimal notaCreditoAFBaseCero = BigDecimal.ZERO;


            if (valorBrutoActivoFijoBaseCero.isPresent()) {
                valorBrutoAFBaseCero = valorBrutoAFBaseCero.add(valorBrutoActivoFijoBaseCero.get().getBaseImponible());
            }

            if (notasCreditoActivoFijoBaseCero.isPresent()) {
                notaCreditoAFBaseCero = notaCreditoAFBaseCero.add(notasCreditoActivoFijoBaseCero.get().getBaseImponible());
            }

            f104.setC404(valorBrutoAFBaseCero);
            f104.setC414(valorBrutoAFBaseCero.subtract(notaCreditoAFBaseCero));


            Optional<ImpuestosF104Projection> valorBrutoReembolso = formulario104Repository.valorBrutoReembolso
                    (idData, idEmpresa, TipoIngreso.RG.name(), filter.getFechaDesde(), filter.getFechaHasta());

            Optional<ImpuestosF104Projection> notasCreditoReembolso = formulario104Repository.notasCreditoReembolso
                    (idData, idEmpresa, TipoIngreso.RG.name(), filter.getFechaDesde(), filter.getFechaHasta());


            BigDecimal valorBrutoRG = BigDecimal.ZERO;
            BigDecimal notaCreditoRG = BigDecimal.ZERO;


            if (valorBrutoReembolso.isPresent()) {
                valorBrutoRG = valorBrutoRG.add(valorBrutoReembolso.get().getBaseImponible());
            }

            if (notasCreditoReembolso.isPresent()) {
                notaCreditoRG = notaCreditoRG.add(notasCreditoReembolso.get().getBaseImponible());

            }

            f104.setC434(valorBrutoRG);
            f104.setC444(valorBrutoRG.subtract(notaCreditoRG));

            // totalizar todas las que sean facturas, notas de credito, notas de debito
            // agrupar para obtener el total por el tipo de documento (agrupar) por codigo de iva 2 y codigo 0,
            // las que no esten anuladas


            f104.setC540(BigDecimal.valueOf(0));
            f104.setC550(BigDecimal.valueOf(0));
            f104.setC560(BigDecimal.valueOf(0));

            f104.setC111(BigDecimal.valueOf(0));
            f104.setC113(BigDecimal.valueOf(0));
            f104.setC115(BigDecimal.valueOf(0));
            f104.setC117(BigDecimal.valueOf(0));
            f104.setC119(BigDecimal.valueOf(0));


            f104.setC405(BigDecimal.valueOf(0));
            f104.setC406(BigDecimal.valueOf(0));
            f104.setC407(BigDecimal.valueOf(0));
            f104.setC408(BigDecimal.valueOf(0));
            f104.setC409(BigDecimal.valueOf(0));
            f104.setC410(BigDecimal.valueOf(0));
            f104.setC415(BigDecimal.valueOf(0));
            f104.setC416(BigDecimal.valueOf(0));
            f104.setC417(BigDecimal.valueOf(0));
            f104.setC418(BigDecimal.valueOf(0));
            f104.setC419(BigDecimal.valueOf(0));
            f104.setC420(BigDecimal.valueOf(0));
            f104.setC423(BigDecimal.valueOf(0));
            f104.setC424(BigDecimal.valueOf(0));
            f104.setC429(BigDecimal.valueOf(0));
            f104.setC442(BigDecimal.valueOf(0));
            f104.setC443(BigDecimal.valueOf(0));
            f104.setC453(BigDecimal.valueOf(0));
            f104.setC454(BigDecimal.valueOf(0));
            f104.setC480(BigDecimal.valueOf(0));
            f104.setC481(BigDecimal.valueOf(0));
            f104.setC482(BigDecimal.valueOf(0));
            f104.setC483(BigDecimal.valueOf(0));
            f104.setC484(BigDecimal.valueOf(0));
            f104.setC485(BigDecimal.valueOf(0));
            f104.setC486(BigDecimal.valueOf(0));
            f104.setC499(BigDecimal.valueOf(0));

            f104.setC500(BigDecimal.valueOf(0));
            f104.setC501(BigDecimal.valueOf(0));
            f104.setC502(BigDecimal.valueOf(0));
            f104.setC503(BigDecimal.valueOf(0));
            f104.setC504(BigDecimal.valueOf(0));
            f104.setC505(BigDecimal.valueOf(0));
            f104.setC506(BigDecimal.valueOf(0));
            f104.setC507(BigDecimal.valueOf(0));
            f104.setC508(BigDecimal.valueOf(0));
            f104.setC509(BigDecimal.valueOf(0));
            f104.setC510(BigDecimal.valueOf(0));
            f104.setC511(BigDecimal.valueOf(0));


            f104.setC512(BigDecimal.valueOf(0));
            f104.setC513(BigDecimal.valueOf(0));
            f104.setC514(BigDecimal.valueOf(0));
            f104.setC515(BigDecimal.valueOf(0));
            f104.setC516(BigDecimal.valueOf(0));
            f104.setC517(BigDecimal.valueOf(0));
            f104.setC518(BigDecimal.valueOf(0));
            f104.setC519(BigDecimal.valueOf(0));
            f104.setC520(BigDecimal.valueOf(0));
            f104.setC521(BigDecimal.valueOf(0));

            f104.setC522(BigDecimal.valueOf(0));
            f104.setC523(BigDecimal.valueOf(0));
            f104.setC524(BigDecimal.valueOf(0));
            f104.setC525(BigDecimal.valueOf(0));
            f104.setC526(BigDecimal.valueOf(0));
            f104.setC527(BigDecimal.valueOf(0));
            f104.setC529(BigDecimal.valueOf(0));
            f104.setC531(BigDecimal.valueOf(0));
            f104.setC532(BigDecimal.valueOf(0));
            f104.setC535(BigDecimal.valueOf(0));
            f104.setC541(BigDecimal.valueOf(0));
            f104.setC542(BigDecimal.valueOf(0));
            f104.setC543(BigDecimal.valueOf(0));
            f104.setC544(BigDecimal.valueOf(0));
            f104.setC545(BigDecimal.valueOf(0));

            f104.setC550(BigDecimal.valueOf(0));

            f104.setC554(BigDecimal.valueOf(0));
            f104.setC555(BigDecimal.valueOf(0));
            f104.setC563(BigDecimal.valueOf(0));
            f104.setC564(BigDecimal.valueOf(0));
            f104.setC565(BigDecimal.valueOf(0));
            f104.setC601(BigDecimal.valueOf(0));
            f104.setC602(BigDecimal.valueOf(0));
            f104.setC603(BigDecimal.valueOf(0));
            f104.setC604(BigDecimal.valueOf(0));
            f104.setC605(BigDecimal.valueOf(0));
            f104.setC606(BigDecimal.valueOf(0));
            f104.setC607(BigDecimal.valueOf(0));
            f104.setC608(BigDecimal.valueOf(0));
            f104.setC609(BigDecimal.valueOf(0));
            f104.setC610(BigDecimal.valueOf(0));
            f104.setC611(BigDecimal.valueOf(0));
            f104.setC612(BigDecimal.valueOf(0));
            f104.setC613(BigDecimal.valueOf(0));
            f104.setC614(BigDecimal.valueOf(0));
            f104.setC615(BigDecimal.valueOf(0));
            f104.setC617(BigDecimal.valueOf(0));
            f104.setC618(BigDecimal.valueOf(0));
            f104.setC619(BigDecimal.valueOf(0));
            f104.setC620(BigDecimal.valueOf(0));
            f104.setC621(BigDecimal.valueOf(0));
            f104.setC622(BigDecimal.valueOf(0));
            f104.setC624(BigDecimal.valueOf(0));
            f104.setC625(BigDecimal.valueOf(0));
            f104.setC699(BigDecimal.valueOf(0));
            f104.setC700(BigDecimal.valueOf(0));
            f104.setC701(BigDecimal.valueOf(0));
            f104.setC702(BigDecimal.valueOf(0));
            f104.setC721(BigDecimal.valueOf(0));
            f104.setC723(BigDecimal.valueOf(0));
            f104.setC725(BigDecimal.valueOf(0));
            f104.setC727(BigDecimal.valueOf(0));
            f104.setC729(BigDecimal.valueOf(0));
            f104.setC731(BigDecimal.valueOf(0));
            f104.setC799(BigDecimal.valueOf(0));
            f104.setC800(BigDecimal.valueOf(0));
            f104.setC801(BigDecimal.valueOf(0));
            f104.setC802(BigDecimal.valueOf(0));
            f104.setC859(BigDecimal.valueOf(0));
            f104.setC890(BigDecimal.valueOf(0));
            f104.setC897(BigDecimal.valueOf(0));
            f104.setC898(BigDecimal.valueOf(0));
            f104.setC899(BigDecimal.valueOf(0));
            f104.setC880(BigDecimal.valueOf(0));
            f104.setC902(BigDecimal.valueOf(0));
            f104.setC903(BigDecimal.valueOf(0));
            f104.setC904(BigDecimal.valueOf(0));
            f104.setC999(BigDecimal.valueOf(0));

            return f104;


        } else {
            throw new GeneralException("Las fechas de busqueda no pueden ser nulas");
        }

    }

}
