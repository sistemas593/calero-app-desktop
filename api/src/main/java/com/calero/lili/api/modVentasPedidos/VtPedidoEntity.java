package com.calero.lili.api.modVentasPedidos;

import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.api.modAuditoria.Auditable;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtPedidos")
@Builder
@Where(clause = "deleted = false")
public class VtPedidoEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idPedido;

    @Column(name = "serie", length = 6)
    private String serie;

    @Column(name = "secuencial", length = 9)
    private String secuencial;
    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_documento")
    private FormatoDocumento formatoDocumento; // EL DETALLE EN EL OBJETO ENUM

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_documento")
    private EstadoDocumento estadoDocumento; // EL DETALLE EN EL OBJETO ENUM

    private String emailEstado;
    private String email;

    private String tipoEmision;
    private String ambiente;
    private String fechaAutorizacion;
    private String claveAcceso;

    @Column(name = "mensaje")
    private String mensaje;
    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

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

    @Column(name = "tercero")
    private String terceroNombre;

    private String tipoCliente;
    private String relacionado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<InformacionAdicional> informacionAdicional;

    private String concepto;

    @JoinColumn(name = "id_pedido", referencedColumnName = "idPedido")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtPedidoValoresEntity> valoresEntity = new ArrayList<>();

    @JoinColumn(name = "id_pedido", referencedColumnName = "idPedido")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtPedidoDetalleEntity> detalle = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private GeTerceroEntity cliente;

}
