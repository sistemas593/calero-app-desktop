package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SustentoCodigos {


    S01("Crédito Tributario para declaración de IVA (servicios y bienes distintos de inventarios y activos fijos)"),
    S02("Costo o Gasto para declaración de IR (servicios y bienes distintos de inventarios y activos fijos)"),
    S03("Activo Fijo - Crédito Tributario para declaración de IVA"),
    S04("Activo Fijo - Costo o Gasto para declaración de IR"),
    S05("Liquidación Gastos de Viaje, hospedaje y alimentación Gastos IR (a nombre de empleados y no de la empresa)"),
    S06("Inventario - Crédito Tributario para declaración de IVA"),
    S07("Inventario - Costo o Gasto para declaración de IR"),
    S08("Valor pagado para solicitar Reembolso de Gasto (intermediario)"),
    S09("Reembolso por Siniestros"),
    S10("Distribución de Dividendos, Beneficios o Utilidades"),
    S11("Convenios de débito o recaudación para IFI´s"),
    S12("Impuestos y retenciones presuntivos"),
    S13("Valores reconocidos por entidades del sector público a favor de sujetos pasivos"),
    S14("Valores facturados por socios a operadoras de transporte (que no constituyen gasto de dicha operadora)"),
    S15("Pagos efectuados por consumos propios y de terceros de servicios digitales"),
    S00("Casos especiales cuyo sustento no aplica en las opciones anteriores");


    private final String nombreSustento;

}
