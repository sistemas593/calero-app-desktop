package com.calero.lili.api.modAdminUsuarios.adRol;

import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposEntity;
import com.calero.lili.api.modAuditoria.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "adRoles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class AdRolEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Column(unique = true)
    private String nombre;

    @ManyToMany
    @JoinTable(
            name = "ad_rol_grupos",
            joinColumns = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_grupo"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_rol", "id_grupo"})})
    private List<AdGruposEntity> grupos;

}
