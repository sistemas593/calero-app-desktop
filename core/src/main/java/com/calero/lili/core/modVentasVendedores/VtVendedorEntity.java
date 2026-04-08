package com.calero.lili.core.modVentasVendedores;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "vt_vendedores")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted = false")
public class VtVendedorEntity extends Auditable {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idVendedor;
         
    @Column(name = "vendedor")
    private String vendedor;
         
    @Column(name = "bloqueado")
    private Boolean bloqueado;
         
    @Column(name = "firma")
    private String firma;
}