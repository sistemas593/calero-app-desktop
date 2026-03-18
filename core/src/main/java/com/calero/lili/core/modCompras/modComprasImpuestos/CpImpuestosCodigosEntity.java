package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.tablas.tbRetenciones.TbRetencionEntity;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpImpuestosCodigos")
public class CpImpuestosCodigosEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idImpuestosCodigo;

    private BigDecimal baseImponible;

    private BigDecimal porcentajeRetener;

    private BigDecimal valorRetenido;

    @ManyToOne()
    @JoinColumn(name = "codigo", referencedColumnName = "codigo")
    private TbRetencionEntity retencion;

    @ManyToOne()
    @JoinColumn(name = "codigoRetencion", referencedColumnName = "codigoRetencion")
    private TbRetencionesCodigosEntity retencionCodigos;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

}
