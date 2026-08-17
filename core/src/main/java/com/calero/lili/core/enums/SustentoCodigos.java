package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SustentoCodigos {


    S01("Crédito Tributario para declaración de IVA (servicios y bienes distintos de inventarios y activos fijos)", "01"),
    S02("Costo o Gasto para declaración de IR (servicios y bienes distintos de inventarios y activos fijos)", "02"),
    S03("Activo Fijo - Crédito Tributario para declaración de IVA", "03"),
    S04("Activo Fijo - Costo o Gasto para declaración de IR", "04"),
    S05("Liquidación Gastos de Viaje, hospedaje y alimentación Gastos IR (a nombre de empleados y no de la empresa)", "05"),
    S06("Inventario - Crédito Tributario para declaración de IVA", "06"),
    S07("Inventario - Costo o Gasto para declaración de IR", "07"),
    S08("Valor pagado para solicitar Reembolso de Gasto (intermediario)", "08"),
    S09("Reembolso por Siniestros", "09"),
    S10("Distribución de Dividendos, Beneficios o Utilidades", "10"),
    S11("Convenios de débito o recaudación para IFI´s", "11"),
    S12("Impuestos y retenciones presuntivos", "12"),
    S13("Valores reconocidos por entidades del sector público a favor de sujetos pasivos", "13"),
    S14("Valores facturados por socios a operadoras de transporte (que no constituyen gasto de dicha operadora)", "14"),
    S15("Pagos efectuados por consumos propios y de terceros de servicios digitales", "15"),
    S00("Casos especiales cuyo sustento no aplica en las opciones anteriores", "00");


    public static SustentoCodigos getCodigoDocumento(String codigoDocumento) {

        for (SustentoCodigos documento : SustentoCodigos.values()) {
            if (documento.getCodigo().equals(codigoDocumento)) {
                return documento;
            }
        }
        throw new IllegalArgumentException("No existe codigo de sustento: " + codigoDocumento);


    }

    private final String nombreSustento;
    private final String codigo;

}
