package com.calero.lili.api.modCxC.XcPagos;

import com.calero.lili.api.modAuditoria.Auditable;
import com.calero.lili.api.modCxC.XcFacturas.XcFacturasEntity;
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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xc_pagos")
@Where(clause = "deleted = false")
public class XcPagosEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idPago;

    @Column(name = "id_pago_grupo")
    private UUID idPagoGrupo;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "sucursal")
    private String sucursal;

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
    private XcFacturasEntity factura;


}
