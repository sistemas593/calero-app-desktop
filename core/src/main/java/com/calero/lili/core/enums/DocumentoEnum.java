package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum DocumentoEnum {


    D01("Factura", "01"),
    D02("Nota o boleta de venta", "02"),
    D03("Liquidación de compra de Bienes o Prestación de servicios", "03"),
    D04("Nota de crédito", "04"),
    D05("Nota de débito", "05"),
    D08("Boletos o entradas a espectáculos públicos", "08"),
    D09("Tiquetes o vales emitidos por máquinas registradoras", "09"),
    D11("Pasajes expedidos por empresas de aviación", "11"),
    D12("Documentos emitidos por instituciones financieras", "12"),
    D15("Comprobante de venta emitido en el Exterior", "15"),
    D18("Documentos autorizados utilizados en ventas excepto N/C N/D", "18"),
    D19("Comprobantes de Pago de Cuotas o Aportes", "19"),
    D20("Documentos por Servicios Administrativos emitidos por Inst. del Estado", "20"),
    D21("Carta de Porte Aéreo", "21"),
    D41("Comprobante de venta emitido por reembolso", "41"),
    D42("Documento retención presuntiva y retención emitida por propio vendedor o por intermediario", "42"),
    D43("Liquidación para Explotación y Exploración de Hidrocarburos", "43"),
    D45("Liquidación por reclamos de aseguradoras", "45"),
   // D47("Nota de Crédito por Reembolso Emitida por Intermediario", "47"),
    D48("Nota de Débito por Reembolso Emitida por Intermediario", "48"),
    D294("Liquidación de compra de Bienes Muebles Usados", "294"),
    D344("Liquidación de compra de vehículos usados", "344"),
    D364("Acta Entrega-Recepción PET", "364"),
    D375("Liquidación de compra RISE de bienes o prestación de servicios", "375");


    public static DocumentoEnum getCodigoDocumento(String codigoDocumento) {
        if (Objects.isNull(codigoDocumento) || codigoDocumento.isEmpty()) {
            return D01;
        }


        for (DocumentoEnum documento : DocumentoEnum.values()) {
            if (documento.getCodigo().equals(codigoDocumento)) {
                return documento;
            }
        }
        throw new IllegalArgumentException("Código de documento no válido: " + codigoDocumento);


    }


    private final String nombre;
    private final String codigo;
}
