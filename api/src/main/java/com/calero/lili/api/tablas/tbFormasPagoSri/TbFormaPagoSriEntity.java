package com.calero.lili.api.tablas.tbFormasPagoSri;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_formas_pago_sri")
public class TbFormaPagoSriEntity extends Auditable {
         
    @Id
    private String codigoFormaPagoSri;
    private String formaPagoSri;

}
