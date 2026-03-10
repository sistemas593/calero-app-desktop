package com.calero.lili.api.modTercerosClientesParametros;

import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modVentasVendedores.VtVendedorEntity;
import com.calero.lili.api.modVentasZonas.VtZonaEntity;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vt_clientes_parametros")
public class VtClienteParametroEntity {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idParametro;

    @Column(name = "relacionado")
    private Boolean relacionado;
         
    @Column(name = "permitir_credito")
    private Boolean permitirCredito;
         
    @Column(name = "valor_credito")
    private BigDecimal valorCredito;
         
    @Column(name = "cuotas")
    private BigInteger cuotas;
         
    @Column(name = "dias_credito")
    private BigInteger diasCredito;
         
    @Column(name = "forma_pago")
    private String formaPago;
         
    @Column(name = "codigo_precio")
    private String codigoPrecio;
         
    @Column(name = "porcentaje_dscto")
    private BigInteger porcentajeDscto;
         
    @Column(name = "observaciones")
    private String observaciones;


    @ManyToOne()
    @JoinColumn(name = "idVendedor", referencedColumnName = "idVendedor")
    private VtVendedorEntity vtVendedorEntity;

    @ManyToOne()
    @JoinColumn(name = "idZona", referencedColumnName = "idZona")
    private VtZonaEntity vtZonaEntity;

    @ManyToOne()
    @JoinColumn(name = "idTercero", referencedColumnName = "idTercero")
    private GeTerceroEntity vtClienteEntity;

}
