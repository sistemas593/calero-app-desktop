package com.calero.lili.core.comprobantes.objetosXml.notaDebito;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlType(propOrder = {"razon", "valor"})
public class Motivo {
    private String razon;
    private String valor;


    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
    

