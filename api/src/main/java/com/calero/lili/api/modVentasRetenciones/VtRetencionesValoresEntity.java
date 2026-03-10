package com.calero.lili.api.modVentasRetenciones;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtRetencionesValores")
@Builder
public class VtRetencionesValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idRetencionValores;

    private String codigo;

    private String codigoRetencion;

    private BigDecimal porcentajeRetener;

    private BigDecimal baseImponible;

    private BigDecimal valorRetenido;

    private String codigoDocumento;

    private String serie;

    private String secuencial;

    private LocalDate fechaEmision;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFactura")
    private XcFacturasEntity factura;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

}
