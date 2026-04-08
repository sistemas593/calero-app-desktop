package com.calero.lili.core.modAdminEmpresasSucursales;

import com.calero.lili.core.Auditable;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ad_empresas_sucursales")
@Where(clause = "deleted = false")
public class AdEmpresasSucursalesEntity extends Auditable {
         
    private Long idData;
    private Long idEmpresa;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID idSucursal;
    private String sucursal;
    private String nombreSucursal;
    private Boolean bloqueado;
}