package com.calero.lili.api.tablas.tbFormasPagoSri;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_formas_pago_sri")
public class TbFormaPagoSriEntity {
         
    @Id
    private String codigoFormaPagoSri;
    private String formaPagoSri;

}
