package com.calero.lili.core.modCxP.XpPagos;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modCxP.XpFacturas.XpFacturasEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xp_pagos")
public class XpPagosEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "sucursal")
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idPago;

    private UUID idPagoGrupo;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "anulada")
    private Boolean anulada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFactura")
    private XpFacturasEntity factura;


}
