package com.calero.lili.core.tablas.tbFormasPagoSri;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tb_formas_pago_sri")
public class TbFormaPagoSriEntity extends Auditable {
         
    @Id
    private String codigoFormaPagoSri;
    private String formaPagoSri;

}
