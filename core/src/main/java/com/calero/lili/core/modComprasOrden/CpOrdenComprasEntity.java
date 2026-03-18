package com.calero.lili.core.modComprasOrden;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cp_orden_compras")
@Builder
@Where(clause = "deleted = false")
public class CpOrdenComprasEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCompra;

    @Column(name = "secuencial", length = 9)
    private String secuencial;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_anulacion")
    private  LocalDate fechaAnulacion;

    @Column(name = "forma_pago", length = 2)
    private String formaPago;

    @Column(name = "dias_credito")
    private Integer diasCredito;

    @Column(name = "fecha_vencimiento")
    private  LocalDate fechaVencimiento;

    @Column(name = "cuotas")
    private Integer cuotas;

    @Column(name = "numero_items")
    private int numeroItems;

    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "impresa")
    private Boolean impresa;

    private String concepto;

    @JoinColumn(name = "id_compra", referencedColumnName = "idCompra")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpOrdenComprasValoresEntity> valoresEntity = new ArrayList<>();

    @JoinColumn(name = "id_compra", referencedColumnName = "idCompra")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpOrdenComprasDetalleEntity> detalle = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;


}
