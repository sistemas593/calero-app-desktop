package com.calero.lili.core.modCxP.XpFacturas;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
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
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xp_facturas")
@Where(clause = "deleted = false")
public class XpFacturasEntity extends Auditable {
    
    @Column(name = "id_data")
    private Long idData;
    
    @Column(name = "id_empresa")
    private Long idEmpresa;
    
    @Column(name = "sucursal")
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idFactura;

    @Column(name = "periodo")
    private String periodo;

    @Column(name = "tipo_documento")
    private String tipoDocumento;
    
    @Column(name = "serie")
    private String serie;
    
    @Column(name = "secuencial")
    private String secuencial;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "valor")
    private BigDecimal valor;
    
    @Column(name = "registros_aplicados")
    private BigInteger registrosAplicados;

    @Column(name = "pagos")
    private BigDecimal pagos;
    
    @Column(name = "retenciones_iva")
    private BigDecimal retencionesIva;
    
    @Column(name = "retenciones_renta")
    private BigDecimal retencionesRenta;
    
    @Column(name = "notas_credito")
    private BigDecimal notasCredito;
    
    @Column(name = "saldo")
    private BigDecimal saldo;
    
    @Column(name = "anulada")
    private Boolean anulada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTercero")
    private GeTerceroEntity proveedor;
}
