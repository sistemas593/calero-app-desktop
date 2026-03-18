package com.calero.lili.core.modTercerosProvedoresParametros;

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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cp_proveedores_parametros")
public class CpProveedoresParametrosEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idParametro;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;


    @Column(name = "forma_pago")
    private String formaPago;

    @Column(name = "permite_credito")
    private Boolean permiteCredito;

    @Column(name = "valor_credito")
    private BigDecimal valorCredito;

    @Column(name = "cuotas")
    private BigInteger cuotas;

    @Column(name = "dias_credito")
    private BigInteger diasCredito;

    @Column(name = "codret")
    private String codret;

    @Column(name = "ideiva")
    private String ideiva;

    @Column(name = "forma_pago_sri")
    private String formaPagoSri;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "pnatural")
    private String pnatural;

    @Column(name = "retiva")
    private String retiva;

    @Column(name = "retir")
    private String retir;

    @Column(name = "conconta")
    private String conconta;

    @Column(name = "titulosup")
    private String titulosup;

    @Column(name = "id_grupo")
    private Integer idGrupo;

    @Column(name = "contesp")
    private String contesp;

    @Column(name = "excretiva")
    private String excretiva;

    @Column(name = "tipoprov")
    private String tipoprov;

    @Column(name = "relacionado")
    private Boolean relacionado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTercero")
    private GeTerceroEntity proveedor;

}
