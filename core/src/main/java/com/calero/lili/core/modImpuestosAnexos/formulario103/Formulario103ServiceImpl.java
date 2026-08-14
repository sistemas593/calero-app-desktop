package com.calero.lili.core.modImpuestosAnexos.formulario103;


import com.calero.lili.core.dtos.FilterImpuestoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosAnexos.formulario103.projection.Formulario103Projection;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class Formulario103ServiceImpl {

    private final AdEmpresasRepository adEmpresasRepository;
    private final Formulario103Repository formulario103Repository;

    public ImpuestosF103Dto setearFImpuestosF103(Long idData, Long idEmpresa, FilterImpuestoDto request) {



        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("No existe empresa con id: " + idEmpresa));



        int anio = request.getFechaHasta().getYear();
        int mes = request.getFechaDesde().getMonthValue();


        List<Formulario103Projection> lista = formulario103Repository.obtenerRetencionesFormulario103(idData, idEmpresa,
                request.getFechaDesde(), request.getFechaHasta());




        ImpuestosF103Dto f103 = new ImpuestosF103Dto();

        f103.setAno(String.valueOf(anio));
        f103.setMes(String.valueOf(mes));
        f103.setRuc(empresa.getRuc());
        f103.setRazonSocial(empresa.getRazonSocial());

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

        lista.forEach(retencion -> setValores(f103, retencion.getCodigoFormulario(), retencion.getBaseImponible(), retencion.getValorRetenido()));

        return f103;
    }


    // Mapa código base imponible -> código valor retenido, según el layout oficial del Formulario 103
    // (no existe una fórmula fija de offset entre ambos códigos, por eso va explícito).
    // Los códigos que no aparecen aquí (ej. 3230, 3483, 3484, 3485) solo tienen columna de base imponible en el formulario.
    private static final Map<String, String> CODIGOS_RETENIDO_POR_BASE = Map.ofEntries(
            Map.entry("302", "352"),
            Map.entry("303", "353"),
            Map.entry("3030", "3530"),
            Map.entry("304", "354"),
            Map.entry("307", "357"),
            Map.entry("308", "358"),
            Map.entry("309", "359"),
            Map.entry("310", "360"),
            Map.entry("311", "361"),
            Map.entry("312", "362"),
            Map.entry("322", "372"),
            Map.entry("3120", "3620"),
            Map.entry("3121", "3621"),
            Map.entry("3430", "3450"),
            Map.entry("343", "393"),
            Map.entry("344", "394"),
            Map.entry("314", "364"),
            Map.entry("3140", "3640"),
            Map.entry("319", "369"),
            Map.entry("320", "370"),
            Map.entry("323", "373"),
            Map.entry("324", "374"),
            Map.entry("333", "383"),
            Map.entry("334", "384"),
            Map.entry("335", "385"),
            Map.entry("336", "386"),
            Map.entry("337", "387"),
            Map.entry("3370", "3870"),
            Map.entry("350", "400"),
            Map.entry("3440", "3940"),
            Map.entry("346", "396"),
            Map.entry("3480", "3980")
    );


    private void setValores(ImpuestosF103Dto f103, String codigoFormulario, BigDecimal baseImponible, BigDecimal valorRetenido) {

        if (codigoFormulario == null) {
            return;
        }

        for (String codigo : codigoFormulario.split(",")) {

            String codigoBase = codigo.trim();

            if (codigoBase.isEmpty()) {
                continue;
            }

            setValor(f103, codigoBase, baseImponible);

            String codigoRetenido = CODIGOS_RETENIDO_POR_BASE.get(codigoBase);
            if (codigoRetenido != null) {
                setValor(f103, codigoRetenido, valorRetenido);
            } else {
                log.debug("El código {} no tiene código de valor retenido asociado, solo se setea la base imponible", codigoBase);
            }
        }
    }


    private void setValor(ImpuestosF103Dto f103, String codigo, BigDecimal valor) {

        if (valor == null) {
            return;
        }

        try {
            Method setter = ImpuestosF103Dto.class.getMethod("setC" + codigo, BigDecimal.class);
            setter.invoke(f103, valor);
        } catch (NoSuchMethodException e) {
            log.debug("Código de formulario 103 sin campo correspondiente en el DTO, se ignora: {}", codigo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new GeneralException("Error al setear el código " + codigo + " del formulario 103: " + e.getMessage());
        }
    }

}
