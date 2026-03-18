package com.calero.lili.core.modImpuestosAnexos;

import com.calero.lili.core.dtos.FilterImpuestoDto;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosAnexos.builder.RetencionFuenteBuilder;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF104Dto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolCabeceraEntity;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolCabeceraRepository;
import com.calero.lili.core.modRRHH.modRRHHCabecera.RhRolDetalleEntity;
import com.calero.lili.core.modRRHH.modRRHHCabecera.projection.ReporteRolCabeceraProjection;
import com.calero.lili.core.modRRHH.modRRHHCabecera.projection.TalonResumenTrabajadoresProjection;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.TrabajadorEntity;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.TrabajadorRepository;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentas.projection.ImpuestosF104Projection;
import com.calero.lili.core.utils.validaciones.ValidarValoresComprobantesPdf;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImpuestosServicesImpl {


    private final ValidarValoresComprobantesPdf validarValoresComprobantesPdf;
    private final AdEmpresasRepository adEmpresasRepository;
    private final VtVentasRepository vtVentasRepository;
    private final RhRolCabeceraRepository rhRolCabeceraRepository;
    private final RetencionFuenteBuilder retencionFuenteBuilder;
    private final TrabajadorRepository trabajadorRepository;


    public ImpuestosF103Dto setearFImpuestosF103() {

        ImpuestosF103Dto f103 = new ImpuestosF103Dto();
        f103.setAno("2025");
        f103.setMes("01");
        f103.setRuc("1717740441001");
        f103.setRazonSocial("CALERO ANDRADE RICARDO JAVIER");

        f103.setC302(BigDecimal.valueOf(302));
        f103.setC352(BigDecimal.valueOf(352));
        f103.setC303(BigDecimal.valueOf(303));
        f103.setC353(BigDecimal.valueOf(353));
        f103.setC3030(BigDecimal.valueOf(3030));
        f103.setC3530(BigDecimal.valueOf(3530));
        f103.setC304(BigDecimal.valueOf(304));
        f103.setC354(BigDecimal.valueOf(354));
        f103.setC307(BigDecimal.valueOf(307));
        f103.setC357(BigDecimal.valueOf(357));
        f103.setC308(BigDecimal.valueOf(308));
        f103.setC358(BigDecimal.valueOf(358));
        f103.setC309(BigDecimal.valueOf(309));
        f103.setC359(BigDecimal.valueOf(359));
        f103.setC310(BigDecimal.valueOf(310));
        f103.setC360(BigDecimal.valueOf(360));
        f103.setC311(BigDecimal.valueOf(311));
        f103.setC361(BigDecimal.valueOf(361));
        f103.setC312(BigDecimal.valueOf(312));
        f103.setC362(BigDecimal.valueOf(362));
        f103.setC322(BigDecimal.valueOf(322));
        f103.setC372(BigDecimal.valueOf(372));
        f103.setC3120(BigDecimal.valueOf(3120));
        f103.setC3620(BigDecimal.valueOf(3620));
        f103.setC3121(BigDecimal.valueOf(3121));
        f103.setC3621(BigDecimal.valueOf(3621));
        f103.setC3430(BigDecimal.valueOf(3430));
        f103.setC3450(BigDecimal.valueOf(3450));
        f103.setC343(BigDecimal.valueOf(343));
        f103.setC393(BigDecimal.valueOf(393));
        f103.setC344(BigDecimal.valueOf(344));
        f103.setC394(BigDecimal.valueOf(394));
        f103.setC332(BigDecimal.valueOf(332));
        f103.setC314(BigDecimal.valueOf(314));
        f103.setC364(BigDecimal.valueOf(364));
        f103.setC3140(BigDecimal.valueOf(3140));
        f103.setC3640(BigDecimal.valueOf(3640));
        f103.setC319(BigDecimal.valueOf(319));
        f103.setC369(BigDecimal.valueOf(369));
        f103.setC320(BigDecimal.valueOf(320));
        f103.setC370(BigDecimal.valueOf(370));
        f103.setC323(BigDecimal.valueOf(323));
        f103.setC373(BigDecimal.valueOf(373));
        f103.setC324(BigDecimal.valueOf(324));
        f103.setC374(BigDecimal.valueOf(374));
        f103.setC3230(BigDecimal.valueOf(3230));
        f103.setC325(BigDecimal.valueOf(325));
        f103.setC375(BigDecimal.valueOf(375));
        f103.setC326(BigDecimal.valueOf(326));
        f103.setC376(BigDecimal.valueOf(376));
        f103.setC327(BigDecimal.valueOf(327));
        f103.setC377(BigDecimal.valueOf(377));
        f103.setC328(BigDecimal.valueOf(328));
        f103.setC378(BigDecimal.valueOf(378));
        f103.setC329(BigDecimal.valueOf(329));
        f103.setC379(BigDecimal.valueOf(379));
        f103.setC330(BigDecimal.valueOf(330));
        f103.setC380(BigDecimal.valueOf(380));
        f103.setC331(BigDecimal.valueOf(331));
        f103.setC333(BigDecimal.valueOf(333));
        f103.setC383(BigDecimal.valueOf(383));
        f103.setC334(BigDecimal.valueOf(334));
        f103.setC384(BigDecimal.valueOf(384));
        f103.setC335(BigDecimal.valueOf(335));
        f103.setC385(BigDecimal.valueOf(385));
        f103.setC336(BigDecimal.valueOf(336));
        f103.setC386(BigDecimal.valueOf(386));
        f103.setC337(BigDecimal.valueOf(337));
        f103.setC387(BigDecimal.valueOf(387));
        f103.setC3370(BigDecimal.valueOf(3370));
        f103.setC3870(BigDecimal.valueOf(3870));
        f103.setC350(BigDecimal.valueOf(350));
        f103.setC400(BigDecimal.valueOf(400));
        f103.setC3440(BigDecimal.valueOf(3440));
        f103.setC3940(BigDecimal.valueOf(3940));
        f103.setC346(BigDecimal.valueOf(346));
        f103.setC396(BigDecimal.valueOf(396));
        f103.setC3400(BigDecimal.valueOf(3400));
        f103.setC3900(BigDecimal.valueOf(3900));
        f103.setC3380(BigDecimal.valueOf(3380));
        f103.setC3880(BigDecimal.valueOf(3880));
        f103.setC338(BigDecimal.valueOf(338));
        f103.setC388(BigDecimal.valueOf(388));
        f103.setC339(BigDecimal.valueOf(339));
        f103.setC389(BigDecimal.valueOf(389));
        f103.setC340(BigDecimal.valueOf(340));
        f103.setC390(BigDecimal.valueOf(390));
        f103.setC341(BigDecimal.valueOf(341));
        f103.setC391(BigDecimal.valueOf(391));
        f103.setC342(BigDecimal.valueOf(342));
        f103.setC392(BigDecimal.valueOf(392));


        f103.setC402(BigDecimal.valueOf(402));
        f103.setC452(BigDecimal.valueOf(452));
        f103.setC403(BigDecimal.valueOf(403));
        f103.setC453(BigDecimal.valueOf(453));
        f103.setC404(BigDecimal.valueOf(404));
        f103.setC454(BigDecimal.valueOf(454));
        f103.setC405(BigDecimal.valueOf(405));
        f103.setC406(BigDecimal.valueOf(406));
        f103.setC456(BigDecimal.valueOf(456));
        f103.setC407(BigDecimal.valueOf(407));
        f103.setC457(BigDecimal.valueOf(457));
        f103.setC4050(BigDecimal.valueOf(4050));
        f103.setC4550(BigDecimal.valueOf(4550));
        f103.setC4060(BigDecimal.valueOf(4060));
        f103.setC4560(BigDecimal.valueOf(4560));
        f103.setC4070(BigDecimal.valueOf(4070));
        f103.setC4570(BigDecimal.valueOf(4570));
        f103.setC408(BigDecimal.valueOf(408));
        f103.setC458(BigDecimal.valueOf(458));
        f103.setC409(BigDecimal.valueOf(409));
        f103.setC459(BigDecimal.valueOf(459));
        f103.setC410(BigDecimal.valueOf(410));
        f103.setC460(BigDecimal.valueOf(460));
        f103.setC411(BigDecimal.valueOf(411));
        f103.setC461(BigDecimal.valueOf(461));
        f103.setC412(BigDecimal.valueOf(412));
        f103.setC413(BigDecimal.valueOf(413));
        f103.setC463(BigDecimal.valueOf(463));
        f103.setC414(BigDecimal.valueOf(414));
        f103.setC464(BigDecimal.valueOf(464));
        f103.setC415(BigDecimal.valueOf(415));
        f103.setC465(BigDecimal.valueOf(465));
        f103.setC416(BigDecimal.valueOf(416));
        f103.setC417(BigDecimal.valueOf(417));
        f103.setC467(BigDecimal.valueOf(467));
        f103.setC418(BigDecimal.valueOf(418));
        f103.setC468(BigDecimal.valueOf(468));
        f103.setC4160(BigDecimal.valueOf(4160));
        f103.setC4660(BigDecimal.valueOf(4660));
        f103.setC4170(BigDecimal.valueOf(4170));
        f103.setC4670(BigDecimal.valueOf(4670));
        f103.setC4180(BigDecimal.valueOf(4180));
        f103.setC4680(BigDecimal.valueOf(4680));
        f103.setC419(BigDecimal.valueOf(419));
        f103.setC469(BigDecimal.valueOf(469));
        f103.setC420(BigDecimal.valueOf(420));
        f103.setC470(BigDecimal.valueOf(470));
        f103.setC421(BigDecimal.valueOf(421));
        f103.setC471(BigDecimal.valueOf(471));
        f103.setC422(BigDecimal.valueOf(422));
        f103.setC472(BigDecimal.valueOf(472));
        f103.setC423(BigDecimal.valueOf(423));
        f103.setC424(BigDecimal.valueOf(424));
        f103.setC474(BigDecimal.valueOf(474));
        f103.setC425(BigDecimal.valueOf(425));
        f103.setC475(BigDecimal.valueOf(475));
        f103.setC426(BigDecimal.valueOf(426));
        f103.setC476(BigDecimal.valueOf(476));
        f103.setC427(BigDecimal.valueOf(427));
        f103.setC477(BigDecimal.valueOf(477));
        f103.setC428(BigDecimal.valueOf(428));
        f103.setC478(BigDecimal.valueOf(478));
        f103.setC4260(BigDecimal.valueOf(4260));
        f103.setC4760(BigDecimal.valueOf(4760));
        f103.setC4270(BigDecimal.valueOf(4270));
        f103.setC4770(BigDecimal.valueOf(4770));
        f103.setC4280(BigDecimal.valueOf(4280));
        f103.setC4780(BigDecimal.valueOf(4780));
        f103.setC429(BigDecimal.valueOf(429));
        f103.setC479(BigDecimal.valueOf(479));
        f103.setC430(BigDecimal.valueOf(430));
        f103.setC480(BigDecimal.valueOf(480));
        f103.setC431(BigDecimal.valueOf(431));
        f103.setC481(BigDecimal.valueOf(481));
        f103.setC432(BigDecimal.valueOf(432));
        f103.setC482(BigDecimal.valueOf(482));
        f103.setC433(BigDecimal.valueOf(433));

        f103.setC345(BigDecimal.valueOf(345));
        f103.setC395(BigDecimal.valueOf(395));
        f103.setC348(BigDecimal.valueOf(348));
        f103.setC398(BigDecimal.valueOf(398));
        f103.setC3481(BigDecimal.valueOf(3481));
        f103.setC3981(BigDecimal.valueOf(3981));

        return f103;
    }

    public ImpuestosF104Dto setearImpuestosF104(FilterImpuestoDto filter, Long idData, Long idEmpresa) {

        AdEmpresaEntity adEmpresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("La empresa no existe"));

        if (Objects.nonNull(filter.getFechaDesde()) && Objects.nonNull(filter.getFechaHasta())) {

            ImpuestosF104Dto f104 = new ImpuestosF104Dto();

            f104.setAno(String.valueOf(filter.getFechaDesde().getYear()));
            f104.setMes(DateUtils.obtenerMesLetras(filter.getFechaHasta()));
            f104.setRuc(adEmpresa.getRuc());
            f104.setRazonSocial(adEmpresa.getRazonSocial());


            Optional<ImpuestosF104Projection> facturasYNotasDebito = vtVentasRepository.findByImpuestoFacturasNotasDebito(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasCredito = vtVentasRepository.findByImpuestoNotasCredito(idData, idEmpresa,
                    TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());

            if (facturasYNotasDebito.isPresent() && notasCredito.isPresent()) {
                BigDecimal c411 = facturasYNotasDebito.get().getBaseImponible().add(notasCredito.get().getBaseImponible());
                BigDecimal c421 = facturasYNotasDebito.get().getValor().add(notasCredito.get().getValor());
                f104.setC401(facturasYNotasDebito.get().getBaseImponible());
                f104.setC411(c411);
                f104.setC421(c421);
            } else {
                f104.setC401(new BigDecimal("0.00"));
                f104.setC411(new BigDecimal("0.00"));
                f104.setC421(new BigDecimal("0.00"));
            }


            Optional<ImpuestosF104Projection> notasCreditoBaseCero = vtVentasRepository.findByImpuestoNotasCreditoBaseCero(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> facturasYNotasDebitoBaseCero = vtVentasRepository.findByImpuestoFacturasNotasDebitoBaseCero
                    (idData, idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            if (notasCreditoBaseCero.isPresent() && facturasYNotasDebitoBaseCero.isPresent()) {

                BigDecimal c413 = facturasYNotasDebitoBaseCero.get().getBaseImponible().add(notasCreditoBaseCero
                        .get().getBaseImponible());
                f104.setC403(facturasYNotasDebitoBaseCero.get().getBaseImponible());
                f104.setC413(c413);

            } else {
                f104.setC403(new BigDecimal("0.00"));
                f104.setC413(new BigDecimal("0.00"));
            }


            Optional<ImpuestosF104Projection> notasCreditoExento = vtVentasRepository.findByImpuestoNotasCreditoExentoIva(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());

            Optional<ImpuestosF104Projection> facturasYNotasDebitoExento = vtVentasRepository.findByImpuestoFacturasNotasDebitoExentoIva
                    (idData, idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            if (notasCreditoExento.isPresent() && facturasYNotasDebitoExento.isPresent()) {

                BigDecimal c441 = facturasYNotasDebitoExento.get().getBaseImponible().add(notasCreditoExento.get()
                        .getBaseImponible());

                f104.setC431(facturasYNotasDebitoExento.get().getBaseImponible());
                f104.setC441(c441);

            } else {

                f104.setC431(new BigDecimal("0.00"));
                f104.setC441(new BigDecimal("0.00"));

            }


            Optional<ImpuestosF104Projection> facturasYNotasDebitoIva5 = vtVentasRepository.findByImpuestoFacturasNotasDebitoTarifaCinco
                    (idData, idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasCreditoIva5 = vtVentasRepository.findByImpuestoNotasCreditoTarifaCinco(idData,
                    idEmpresa, TipoIngreso.VL.name(), filter.getFechaDesde(), filter.getFechaHasta());


            if (facturasYNotasDebitoExento.isPresent() && notasCreditoIva5.isPresent()) {
                BigDecimal c550 = facturasYNotasDebitoIva5.get().getBaseImponible().add(notasCreditoIva5.get().getBaseImponible());
                BigDecimal c560 = facturasYNotasDebitoIva5.get().getValor().add(notasCreditoIva5.get().getValor());

                f104.setC540(facturasYNotasDebitoIva5.get().getBaseImponible());
                f104.setC550(c550);
                f104.setC560(c560);
            } else {

                f104.setC540(new BigDecimal("0.00"));
                f104.setC550(new BigDecimal("0.00"));
                f104.setC560(new BigDecimal("0.00"));
            }


            Optional<ImpuestosF104Projection> facturasYNotasDebitoAF = vtVentasRepository.findByImpuestoFacturasNotasDebito(idData,
                    idEmpresa, TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> notasCreditoAF = vtVentasRepository.findByImpuestoNotasCredito(idData, idEmpresa,
                    TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            if (facturasYNotasDebitoAF.isPresent() && notasCreditoAF.isPresent()) {

                BigDecimal c412 = facturasYNotasDebitoAF.get().getBaseImponible().add(notasCreditoAF.get().getBaseImponible());
                BigDecimal c422 = facturasYNotasDebitoAF.get().getValor().add(notasCreditoAF.get().getValor());

                f104.setC402(facturasYNotasDebitoAF.get().getBaseImponible());
                f104.setC412(c412);
                f104.setC422(c422);

            } else {
                f104.setC402(new BigDecimal("0.00"));
                f104.setC412(new BigDecimal("0.00"));
                f104.setC422(new BigDecimal("0.00"));
            }


            Optional<ImpuestosF104Projection> notasCreditoBaseCeroAf = vtVentasRepository.findByImpuestoNotasCreditoBaseCero(idData,
                    idEmpresa, TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            Optional<ImpuestosF104Projection> facturasYNotasDebitoBaseCeroAf = vtVentasRepository.findByImpuestoFacturasNotasDebitoBaseCero
                    (idData, idEmpresa, TipoIngreso.AF.name(), filter.getFechaDesde(), filter.getFechaHasta());


            if (notasCreditoBaseCeroAf.isPresent() && facturasYNotasDebitoBaseCeroAf.isPresent()) {
                BigDecimal c414 = facturasYNotasDebitoBaseCeroAf.get().getBaseImponible()
                        .add(notasCreditoBaseCeroAf.get().getBaseImponible());

                f104.setC404(facturasYNotasDebitoBaseCeroAf.get().getBaseImponible());
                f104.setC414(c414);

            } else {
                f104.setC404(new BigDecimal("0.00"));
                f104.setC414(new BigDecimal("0.00"));
            }


            Optional<ImpuestosF104Projection> notasCreditoReembolso = vtVentasRepository.findByImpuestoNotasCreditoReembolso
                    (idData, idEmpresa, TipoIngreso.RG.name(), filter.getFechaDesde(), filter.getFechaHasta());

            Optional<ImpuestosF104Projection> facturaNotasDebitoReembolso = vtVentasRepository.findByImpuestoFacturasNotasDebitoReembolso
                    (idData, idEmpresa, TipoIngreso.RG.name(), filter.getFechaDesde(), filter.getFechaHasta());


            if (notasCreditoReembolso.isPresent() && facturaNotasDebitoReembolso.isPresent()) {

                BigDecimal c444 = facturaNotasDebitoReembolso.get().getBaseImponible()
                        .add(notasCreditoReembolso.get().getBaseImponible());
                f104.setC434(facturaNotasDebitoReembolso.get().getBaseImponible());
                f104.setC444(c444);

            } else {

                f104.setC434(new BigDecimal("0.00"));
                f104.setC444(new BigDecimal("0.00"));
            }


            f104.setC111(BigDecimal.valueOf(111));
            f104.setC113(BigDecimal.valueOf(113));
            f104.setC115(BigDecimal.valueOf(115));
            f104.setC117(BigDecimal.valueOf(117));
            f104.setC119(BigDecimal.valueOf(119));


            f104.setC425(BigDecimal.valueOf(425));

            f104.setC405(BigDecimal.valueOf(405));
            f104.setC406(BigDecimal.valueOf(406));
            f104.setC407(BigDecimal.valueOf(407));
            f104.setC408(BigDecimal.valueOf(408));
            f104.setC409(BigDecimal.valueOf(409));
            f104.setC410(BigDecimal.valueOf(410));

            f104.setC435(BigDecimal.valueOf(435));

            f104.setC415(BigDecimal.valueOf(415));
            f104.setC416(BigDecimal.valueOf(416));
            f104.setC417(BigDecimal.valueOf(417));
            f104.setC418(BigDecimal.valueOf(418));
            f104.setC419(BigDecimal.valueOf(419));
            f104.setC420(BigDecimal.valueOf(420));

            f104.setC445(BigDecimal.valueOf(445));
            f104.setC423(BigDecimal.valueOf(423));
            f104.setC424(BigDecimal.valueOf(424));
            f104.setC429(BigDecimal.valueOf(429));


            f104.setC442(BigDecimal.valueOf(442));
            f104.setC443(BigDecimal.valueOf(443));

            f104.setC453(BigDecimal.valueOf(453));
            f104.setC454(BigDecimal.valueOf(454));
            f104.setC480(BigDecimal.valueOf(480));
            f104.setC481(BigDecimal.valueOf(481));
            f104.setC482(BigDecimal.valueOf(482));
            f104.setC483(BigDecimal.valueOf(483));
            f104.setC484(BigDecimal.valueOf(484));
            f104.setC485(BigDecimal.valueOf(485));
            f104.setC499(BigDecimal.valueOf(499));
            f104.setC500(BigDecimal.valueOf(500));
            f104.setC501(BigDecimal.valueOf(501));

            f104.setC502(BigDecimal.valueOf(502));
            f104.setC503(BigDecimal.valueOf(503));
            f104.setC504(BigDecimal.valueOf(504));
            f104.setC505(BigDecimal.valueOf(505));
            f104.setC506(BigDecimal.valueOf(506));
            f104.setC507(BigDecimal.valueOf(507));
            f104.setC508(BigDecimal.valueOf(508));
            f104.setC509(BigDecimal.valueOf(509));
            f104.setC510(BigDecimal.valueOf(510));
            f104.setC511(BigDecimal.valueOf(511));


            f104.setC512(BigDecimal.valueOf(512));
            f104.setC513(BigDecimal.valueOf(513));
            f104.setC514(BigDecimal.valueOf(514));
            f104.setC515(BigDecimal.valueOf(515));
            f104.setC516(BigDecimal.valueOf(516));
            f104.setC517(BigDecimal.valueOf(517));
            f104.setC518(BigDecimal.valueOf(518));
            f104.setC519(BigDecimal.valueOf(519));
            f104.setC520(BigDecimal.valueOf(520));
            f104.setC521(BigDecimal.valueOf(521));

            f104.setC522(BigDecimal.valueOf(522));
            f104.setC523(BigDecimal.valueOf(523));
            f104.setC524(BigDecimal.valueOf(524));
            f104.setC525(BigDecimal.valueOf(525));
            f104.setC526(BigDecimal.valueOf(526));
            f104.setC527(BigDecimal.valueOf(527));
            f104.setC529(BigDecimal.valueOf(529));
            f104.setC531(BigDecimal.valueOf(531));
            f104.setC532(BigDecimal.valueOf(532));
            f104.setC535(BigDecimal.valueOf(535));
            f104.setC541(BigDecimal.valueOf(541));
            f104.setC542(BigDecimal.valueOf(542));
            f104.setC543(BigDecimal.valueOf(543));
            f104.setC544(BigDecimal.valueOf(544));
            f104.setC545(BigDecimal.valueOf(545));

            f104.setC554(BigDecimal.valueOf(554));
            f104.setC555(BigDecimal.valueOf(555));
            f104.setC563(BigDecimal.valueOf(563));
            f104.setC564(BigDecimal.valueOf(564));
            f104.setC601(BigDecimal.valueOf(601));
            f104.setC602(BigDecimal.valueOf(602));
            f104.setC603(BigDecimal.valueOf(603));
            f104.setC604(BigDecimal.valueOf(604));
            f104.setC605(BigDecimal.valueOf(605));
            f104.setC606(BigDecimal.valueOf(606));
            f104.setC607(BigDecimal.valueOf(607));
            f104.setC608(BigDecimal.valueOf(608));
            f104.setC609(BigDecimal.valueOf(609));
            f104.setC610(BigDecimal.valueOf(610));
            f104.setC611(BigDecimal.valueOf(611));
            f104.setC612(BigDecimal.valueOf(612));
            f104.setC613(BigDecimal.valueOf(613));
            f104.setC614(BigDecimal.valueOf(614));
            f104.setC615(BigDecimal.valueOf(615));
            f104.setC617(BigDecimal.valueOf(617));
            f104.setC618(BigDecimal.valueOf(618));
            f104.setC619(BigDecimal.valueOf(619));
            f104.setC620(BigDecimal.valueOf(620));
            f104.setC621(BigDecimal.valueOf(621));
            f104.setC699(BigDecimal.valueOf(699));
            f104.setC721(BigDecimal.valueOf(721));
            f104.setC723(BigDecimal.valueOf(723));
            f104.setC725(BigDecimal.valueOf(725));
            f104.setC727(BigDecimal.valueOf(727));
            f104.setC729(BigDecimal.valueOf(729));
            f104.setC731(BigDecimal.valueOf(731));
            f104.setC799(BigDecimal.valueOf(799));
            f104.setC800(BigDecimal.valueOf(800));
            f104.setC801(BigDecimal.valueOf(801));
            f104.setC802(BigDecimal.valueOf(802));
            f104.setC859(BigDecimal.valueOf(859));
            f104.setC890(BigDecimal.valueOf(890));
            f104.setC897(BigDecimal.valueOf(897));
            f104.setC898(BigDecimal.valueOf(898));
            f104.setC899(BigDecimal.valueOf(899));
            f104.setC880(BigDecimal.valueOf(880));
            f104.setC902(BigDecimal.valueOf(902));
            f104.setC903(BigDecimal.valueOf(903));
            f104.setC904(BigDecimal.valueOf(904));
            f104.setC999(BigDecimal.valueOf(999));

            return f104;


        } else {
            throw new GeneralException("Las fechas de busqueda no pueden ser nulas");
        }

    }


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

    public ValoresTalonResumenDto setearRetencionAlaFuente(FilterImpuestoDto filter, Long idData, Long idEmpresa) {

        List<TalonResumenTrabajadoresProjection> lista = rhRolCabeceraRepository
                .getTalResumenRetencionTrabajadores(idData, idEmpresa, filter.getFechaDesde(), filter.getFechaHasta());

        ValoresTalonResumenDto retencionFuente = retencionFuenteBuilder.builderRetencionFuentePdf();
        retencionFuente.setNumeroRegistros(lista.getFirst().getTotalRegistros());

        for (TalonResumenTrabajadoresProjection item : lista) {


            switch (item.getCodigo()) {

                case "ING-001" -> retencionFuente.setSuelSal(item.getTotalRubro());

                case "ING-002" -> retencionFuente.setSobSuelComRemu(item.getTotalRubro());

                case "ING-015" -> retencionFuente.setOtrosIngRenNoGrav(item.getTotalRubro());

                case "ING-017" -> retencionFuente.setOtrosIngRenGrav(item.getTotalRubro());

                case "ING-009" -> retencionFuente.setDecimTer(item.getTotalRubro());

                case "ING-010" -> retencionFuente.setDecimCuar(item.getTotalRubro());

                case "ING-013" -> retencionFuente.setFondoReserva(item.getTotalRubro());

                case "ING-018" -> retencionFuente.setSalarioDigno(item.getTotalRubro());

                case "ING-016" -> retencionFuente.setPartUtil(item.getTotalRubro());

                case "DES-001" -> retencionFuente.setApoPerIess(item.getTotalRubro());

                case "GTP-001" -> retencionFuente.setDeducVivienda(item.getTotalRubro());

                case "GTP-002" -> retencionFuente.setDeducSalud(item.getTotalRubro());

                case "GTP-003" -> retencionFuente.setDeducAliement(item.getTotalRubro());

                case "GTP-004" -> retencionFuente.setDeducEducartcult(item.getTotalRubro());

                case "GTP-005" -> retencionFuente.setDeducVestim(item.getTotalRubro());

                case "GTP-006" -> retencionFuente.setDeduccionTurismo(item.getTotalRubro());

                case "EXO-001" -> retencionFuente.setExoDiscap(item.getTotalRubro());

                case "EXO-002" -> retencionFuente.setExoTerEd(item.getTotalRubro());

                case "DES-008" -> retencionFuente.setValImpAsuEsteEmpl(item.getTotalRubro());

                case "RET-002" -> retencionFuente.setValRet(item.getTotalRubro());

                case "BAS-001" -> retencionFuente.setBasImp(item.getTotalRubro());

                case "RET-001" -> retencionFuente.setImpRentCaus(item.getTotalRubro());

                case "OTE-003" -> retencionFuente.setValRetAsuOtrosEmpls(item.getTotalRubro());

                case "OTE-002" -> retencionFuente.setAporPerIessConOtrosEmpls(item.getTotalRubro());

                case "OTE-001" -> retencionFuente.setIntGrabGen(item.getTotalRubro());


            }

        }

        retencionFuente.setIngGravConEsteEmpl(BigDecimal.ZERO);
        retencionFuente.setRebajaGastosPersonales(BigDecimal.ZERO);
        retencionFuente.setImpuestoRentaRebajaGastosPersonales(BigDecimal.ZERO);


        return retencionFuente;
    }


    public RetencionFuenteXmlDto setearRetencionFuenteXml(Long idData, Long idEmpresa, FilterImpuestoDto filter) {

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La empresa con id {0} no existe", idEmpresa)));

        List<ReporteRolCabeceraProjection> rolCabeceraProjections = rhRolCabeceraRepository
                .getRolCabeceraForPeriodo(idData, idEmpresa, filter.getFechaDesde(), filter.getFechaHasta());


        if (rolCabeceraProjections.isEmpty()) {
            return null;
        }

        Integer numeroRegistros = rolCabeceraProjections.size();
        return retencionFuenteBuilder.builderRetencionFuenteXmlDto(empresa, String.valueOf(numeroRegistros), filter.getFechaDesde(), rolCabeceraProjections);

    }


}
