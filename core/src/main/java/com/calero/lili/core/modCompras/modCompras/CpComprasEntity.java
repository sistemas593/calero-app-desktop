package com.calero.lili.core.modCompras.modCompras;

import com.calero.lili.core.enums.TipoIdentificacion;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cp_compras")
@Builder
@Where(clause = "deleted = false")
public class CpComprasEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCompra;

    @Column(name = "serie", length = 6)
    private String serie;

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

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;

    private String tipoProveedor;
    private String relacionado;

    private String concepto;

    private String modTipoVenta;
    private String modSerie;
    private String modSecuencial;

    @Builder.Default
    @JoinColumn(name = "id_compra", referencedColumnName = "idCompra")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpComprasValoresEntity> valoresEntity = new ArrayList<>();

    @Builder.Default
    @JoinColumn(name = "id_compra", referencedColumnName = "idCompra")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpComprasDetalleEntity> detalle = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;


}
