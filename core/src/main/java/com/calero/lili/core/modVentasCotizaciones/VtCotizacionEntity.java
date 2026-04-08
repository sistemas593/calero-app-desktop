package com.calero.lili.core.modVentasCotizaciones;

import com.calero.lili.core.dtos.InformacionAdicional;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

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
@Table(name = "vtCotizaciones")
@Builder
@Where(clause = "deleted = false")
public class VtCotizacionEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCotizacion;

    @Column(name = "secuencial", length = 9)
    private String secuencial;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_anulacion")
    private  LocalDate fechaAnulacion;

    @Column(name = "forma_pago", length = 2)
    private String formaPago;
    // (CO) Contado, (CR) Credito

    // info credito
    @Column(name = "dias_credito")
    private Integer diasCredito;
    @Column(name = "fecha_vencimiento")
    private  LocalDate fechaVencimiento;
    @Column(name = "cuotas")
    private Integer cuotas;

    private String emailEstado;
    private String email;

    @Column(name = "numero_items")
    private int numeroItems;

    // VALORES no ponia aqui porque si quiero manejar ice o cualquier otro impuesto toca aumentar campos
    // PARA COMPRAS IGUAL PUEDE EXISTIR ICE U OTROS IMPUESTOS, CAMBIAR EL % DE IVA O APARECER OTRO
    // igual si quiero internacionalizar la aplicacion
    // PARA EXPORTAR A EXCEL IGUAL TOCA RECORRER LA TABLA PARA CAMBIAR LA FECHA ASI ESTE EN UNA SOLA TABLA, SOLO TOCA RECORRER TAMBIEN DE VALORES
    // SI APARECE UN NUEVO % DE IVA TOCA CAMBIAR BESTIALMENTE EN TODE EL SISTEMA, COMPRAS, IMPUESTOUES, REEMBOLSOS PARA CREAR NUEVOS CAMPOS

    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    @Column(name = "id_vendedor")
    private Integer idVendedor;

    @Column(name = "id_zona")
    private UUID idZona;

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "impresa")
    private Boolean impresa;

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;

    @Column(name = "cliente")
    private String terceroNombre;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<InformacionAdicional> informacionAdicional;

    private String concepto;

    @Builder.Default
    @JoinColumn(name = "id_cotizacion", referencedColumnName = "idCotizacion")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtCotizacionValoresEntity> valoresEntity = new ArrayList<>();

    @Builder.Default
    @JoinColumn(name = "id_cotizacion", referencedColumnName = "idCotizacion")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtCotizacionDetalleEntity> detalle = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private GeTerceroEntity cliente;

}
