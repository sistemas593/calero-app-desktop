package com.calero.lili.api.tablas.tbRetencionesCodigos;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_retenciones_codigos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbRetencionesCodigosEntity extends Auditable {
         
    @Id
    private String codigoRetencion;
    private String nombreRetencion;
    private String codigo;

}