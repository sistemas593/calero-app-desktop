package com.calero.lili.api.modCompras.modComprasRetenciones;

import com.calero.lili.api.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.api.tablas.tbDocumentos.TbDocumentoEntity;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cp_retenciones_referencias")
@Builder
public class CpRetencionReferencias {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idReferencia;

    private String serie;
    private String secuencial;
    private String numeroIdentificacion;

    @ManyToOne()
    @JoinColumn(name = "idImpuestos", referencedColumnName = "idImpuestos")
    private CpImpuestosEntity impuestos;

    @ManyToOne()
    @JoinColumn(name = "codigoDocumento", referencedColumnName = "codigoDocumento")
    private TbDocumentoEntity documento;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ImpuestosCodigo> impuestosCodigos ;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImpuestosCodigo {
        private String codigo;
        private String codigoRetencion;
        private BigDecimal baseImponible;
        private BigDecimal porcentajeRetener;
        private BigDecimal valorRetenido;
    }

}
