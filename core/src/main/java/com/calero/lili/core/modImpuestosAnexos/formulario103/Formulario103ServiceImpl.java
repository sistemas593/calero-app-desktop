package com.calero.lili.core.modImpuestosAnexos.formulario103;


import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class Formulario103ServiceImpl {

    private final Formulario103Repository formulario103Repository;

    public ImpuestosF103Dto setearFImpuestosF103() {

        // TODO LLENAR EL FORMULARIO SOLO CON LAS RETENCIONES


       // formulario103Repository.obtenerRetencionesRenta()


        ImpuestosF103Dto f103 = new ImpuestosF103Dto();
        f103.setAno("2025");
        f103.setMes("01");
        f103.setRuc("1717740441001");
        f103.setRazonSocial("CALERO ANDRADE RICARDO JAVIER");

        f103.setC302(BigDecimal.ZERO);
        f103.setC352(BigDecimal.ZERO);
        f103.setC303(BigDecimal.ZERO);
        f103.setC353(BigDecimal.ZERO);
        f103.setC3030(BigDecimal.ZERO);
        f103.setC3530(BigDecimal.ZERO);
        f103.setC304(BigDecimal.ZERO);
        f103.setC354(BigDecimal.ZERO);
        f103.setC307(BigDecimal.ZERO);
        f103.setC357(BigDecimal.ZERO);
        f103.setC308(BigDecimal.ZERO);
        f103.setC358(BigDecimal.ZERO);
        f103.setC309(BigDecimal.ZERO);
        f103.setC359(BigDecimal.ZERO);
        f103.setC310(BigDecimal.ZERO);
        f103.setC360(BigDecimal.ZERO);
        f103.setC311(BigDecimal.ZERO);
        f103.setC361(BigDecimal.ZERO);
        f103.setC312(BigDecimal.ZERO);
        f103.setC362(BigDecimal.ZERO);
        f103.setC322(BigDecimal.ZERO);
        f103.setC372(BigDecimal.ZERO);
        f103.setC3120(BigDecimal.ZERO);
        f103.setC3620(BigDecimal.ZERO);
        f103.setC3121(BigDecimal.ZERO);
        f103.setC3621(BigDecimal.ZERO);
        f103.setC3430(BigDecimal.ZERO);
        f103.setC3450(BigDecimal.ZERO);
        f103.setC343(BigDecimal.ZERO);
        f103.setC393(BigDecimal.ZERO);
        f103.setC344(BigDecimal.ZERO);
        f103.setC394(BigDecimal.ZERO);
        f103.setC332(BigDecimal.ZERO);
        f103.setC314(BigDecimal.ZERO);
        f103.setC364(BigDecimal.ZERO);
        f103.setC3140(BigDecimal.ZERO);
        f103.setC3640(BigDecimal.ZERO);
        f103.setC319(BigDecimal.ZERO);
        f103.setC369(BigDecimal.ZERO);
        f103.setC320(BigDecimal.ZERO);
        f103.setC370(BigDecimal.ZERO);
        f103.setC323(BigDecimal.ZERO);
        f103.setC373(BigDecimal.ZERO);
        f103.setC324(BigDecimal.ZERO);
        f103.setC374(BigDecimal.ZERO);
        f103.setC3230(BigDecimal.ZERO);
        f103.setC325(BigDecimal.ZERO);
        f103.setC375(BigDecimal.ZERO);
        f103.setC326(BigDecimal.ZERO);
        f103.setC376(BigDecimal.ZERO);
        f103.setC327(BigDecimal.ZERO);
        f103.setC377(BigDecimal.ZERO);
        f103.setC328(BigDecimal.ZERO);
        f103.setC378(BigDecimal.ZERO);
        f103.setC329(BigDecimal.ZERO);
        f103.setC379(BigDecimal.ZERO);
        f103.setC330(BigDecimal.ZERO);
        f103.setC380(BigDecimal.ZERO);
        f103.setC331(BigDecimal.ZERO);
        f103.setC333(BigDecimal.ZERO);
        f103.setC383(BigDecimal.ZERO);
        f103.setC334(BigDecimal.ZERO);
        f103.setC384(BigDecimal.ZERO);
        f103.setC335(BigDecimal.ZERO);
        f103.setC385(BigDecimal.ZERO);
        f103.setC336(BigDecimal.ZERO);
        f103.setC386(BigDecimal.ZERO);
        f103.setC337(BigDecimal.ZERO);
        f103.setC387(BigDecimal.ZERO);
        f103.setC3370(BigDecimal.ZERO);
        f103.setC3870(BigDecimal.ZERO);
        f103.setC350(BigDecimal.ZERO);
        f103.setC400(BigDecimal.ZERO);
        f103.setC3440(BigDecimal.ZERO);
        f103.setC3940(BigDecimal.ZERO);
        f103.setC346(BigDecimal.ZERO);
        f103.setC396(BigDecimal.ZERO);
        f103.setC3400(BigDecimal.ZERO);
        f103.setC3900(BigDecimal.ZERO);
        f103.setC3380(BigDecimal.ZERO);
        f103.setC3880(BigDecimal.ZERO);
        f103.setC338(BigDecimal.ZERO);
        f103.setC388(BigDecimal.ZERO);
        f103.setC339(BigDecimal.ZERO);
        f103.setC389(BigDecimal.ZERO);
        f103.setC340(BigDecimal.ZERO);
        f103.setC390(BigDecimal.ZERO);
        f103.setC341(BigDecimal.ZERO);
        f103.setC391(BigDecimal.ZERO);
        f103.setC342(BigDecimal.ZERO);
        f103.setC392(BigDecimal.ZERO);


        f103.setC402(BigDecimal.ZERO);
        f103.setC452(BigDecimal.ZERO);
        f103.setC403(BigDecimal.ZERO);
        f103.setC453(BigDecimal.ZERO);
        f103.setC404(BigDecimal.ZERO);
        f103.setC454(BigDecimal.ZERO);
        f103.setC405(BigDecimal.ZERO);
        f103.setC406(BigDecimal.ZERO);
        f103.setC456(BigDecimal.ZERO);
        f103.setC407(BigDecimal.ZERO);
        f103.setC457(BigDecimal.ZERO);
        f103.setC4050(BigDecimal.ZERO);
        f103.setC4550(BigDecimal.ZERO);
        f103.setC4060(BigDecimal.ZERO);
        f103.setC4560(BigDecimal.ZERO);
        f103.setC4070(BigDecimal.ZERO);
        f103.setC4570(BigDecimal.ZERO);
        f103.setC408(BigDecimal.ZERO);
        f103.setC458(BigDecimal.ZERO);
        f103.setC409(BigDecimal.ZERO);
        f103.setC459(BigDecimal.ZERO);
        f103.setC410(BigDecimal.ZERO);
        f103.setC460(BigDecimal.ZERO);
        f103.setC411(BigDecimal.ZERO);
        f103.setC461(BigDecimal.ZERO);
        f103.setC412(BigDecimal.ZERO);
        f103.setC413(BigDecimal.ZERO);
        f103.setC463(BigDecimal.ZERO);
        f103.setC414(BigDecimal.ZERO);
        f103.setC464(BigDecimal.ZERO);
        f103.setC415(BigDecimal.ZERO);
        f103.setC465(BigDecimal.ZERO);
        f103.setC416(BigDecimal.ZERO);
        f103.setC417(BigDecimal.ZERO);
        f103.setC467(BigDecimal.ZERO);
        f103.setC418(BigDecimal.ZERO);
        f103.setC468(BigDecimal.ZERO);
        f103.setC4160(BigDecimal.ZERO);
        f103.setC4660(BigDecimal.ZERO);
        f103.setC4170(BigDecimal.ZERO);
        f103.setC4670(BigDecimal.ZERO);
        f103.setC4180(BigDecimal.ZERO);
        f103.setC4680(BigDecimal.ZERO);
        f103.setC419(BigDecimal.ZERO);
        f103.setC469(BigDecimal.ZERO);
        f103.setC420(BigDecimal.ZERO);
        f103.setC470(BigDecimal.ZERO);
        f103.setC421(BigDecimal.ZERO);
        f103.setC471(BigDecimal.ZERO);
        f103.setC422(BigDecimal.ZERO);
        f103.setC472(BigDecimal.ZERO);
        f103.setC423(BigDecimal.ZERO);
        f103.setC424(BigDecimal.ZERO);
        f103.setC474(BigDecimal.ZERO);
        f103.setC425(BigDecimal.ZERO);
        f103.setC475(BigDecimal.ZERO);
        f103.setC426(BigDecimal.ZERO);
        f103.setC476(BigDecimal.ZERO);
        f103.setC427(BigDecimal.ZERO);
        f103.setC477(BigDecimal.ZERO);
        f103.setC428(BigDecimal.ZERO);
        f103.setC478(BigDecimal.ZERO);
        f103.setC4260(BigDecimal.ZERO);
        f103.setC4760(BigDecimal.ZERO);
        f103.setC4270(BigDecimal.ZERO);
        f103.setC4770(BigDecimal.ZERO);
        f103.setC4280(BigDecimal.ZERO);
        f103.setC4780(BigDecimal.ZERO);
        f103.setC429(BigDecimal.ZERO);
        f103.setC479(BigDecimal.ZERO);
        f103.setC430(BigDecimal.ZERO);
        f103.setC480(BigDecimal.ZERO);
        f103.setC431(BigDecimal.ZERO);
        f103.setC481(BigDecimal.ZERO);
        f103.setC432(BigDecimal.ZERO);
        f103.setC482(BigDecimal.ZERO);
        f103.setC433(BigDecimal.ZERO);

        f103.setC345(BigDecimal.ZERO);
        f103.setC395(BigDecimal.ZERO);
        f103.setC348(BigDecimal.ZERO);
        f103.setC398(BigDecimal.ZERO);
        f103.setC3481(BigDecimal.ZERO);
        f103.setC3981(BigDecimal.ZERO);

        return f103;
    }

}
