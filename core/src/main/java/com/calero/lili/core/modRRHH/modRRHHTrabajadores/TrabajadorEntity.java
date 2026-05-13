package com.calero.lili.core.modRRHH.modRRHHTrabajadores;


import com.calero.lili.core.enums.FormaDecimo;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rh_trabajadores")
public class TrabajadorEntity {

    @Id
    @Column(name = "id_trabajador", unique = true, updatable = false, nullable = false)
    private UUID idTrabajador;

    @Column(length = 1)
    private String codigoSalario;

    @Column(nullable = false)
    private String codigoEstablecimiento;

    private String codigoResidencia;

    @Column(length = 1)
    private String aplicaConvenio;

    @Column(length = 2)
    private String tipoDiscapacidad;

    private Integer porcentajeDiscapacidad;

    @Column(length = 1)
    private String tipoIdDiscapacidad;

    private String idDiscapacidad;

    @Column(length = 1)
    private String beneficioProvGalapagos;

    @Column(length = 1)
    private String enfermedadCatastrofica;

    private LocalDate fechaIngreso;

    private Integer estado;

    private LocalDate fechaNacimiento;

    private LocalDate fechaSalida;

    private UUID idCargo;

    private BigDecimal sueldoBase;

    @Enumerated(EnumType.STRING)
    private FormaDecimo decimoTerceroForma;

    @Enumerated(EnumType.STRING)
    private FormaDecimo decimoCuartoForma;

    private String apellidos;

    private String nombres;

    @ManyToOne()
    @JoinColumn(name = "codigoPais", referencedColumnName = "codigoPais")
    private TbPaisEntity pais;

    @ManyToOne()
    @JoinColumn(name = "id_tercero", referencedColumnName = "idTercero")
    private GeTerceroEntity tercero;

}
