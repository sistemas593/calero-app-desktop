package com.calero.lili.api.modAdminUsuarios.adPermisos;

import com.calero.lili.api.modAuditoria.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "ad_permisos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class AdPermisosEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPermiso;

    private String descripcion;

    private String permiso;

}
