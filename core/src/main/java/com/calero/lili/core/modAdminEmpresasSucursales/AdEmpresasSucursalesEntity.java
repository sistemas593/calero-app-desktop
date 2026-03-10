package com.calero.lili.core.modAdminEmpresasSucursales;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ad_empresas_sucursales")
@Where(clause = "deleted = false")
public class AdEmpresasSucursalesEntity {
         
    private Long idData;
    private Long idEmpresa;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID idSucursal;
    private String sucursal;
    private String nombreSucursal;
    private Boolean bloqueado;
}