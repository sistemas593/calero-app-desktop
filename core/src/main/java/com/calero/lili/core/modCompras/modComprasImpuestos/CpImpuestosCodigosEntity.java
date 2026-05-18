package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.enums.CodigoRetencion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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

    @Enumerated(EnumType.STRING)
    private CodigoRetencion codigo;

    private String codigoRetencion;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

}
