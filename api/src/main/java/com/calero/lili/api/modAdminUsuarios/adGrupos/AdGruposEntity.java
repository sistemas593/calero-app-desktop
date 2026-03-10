package com.calero.lili.api.modAdminUsuarios.adGrupos;


import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosEntity;
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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_grupos")
@Where(clause = "deleted = false")
public class AdGruposEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToMany
    @JoinTable(
            name = "ad_grupos_permisos",
            joinColumns = @JoinColumn(name = "id_grupo"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_grupo", "id_permiso"})})
    private List<AdPermisosEntity> permisos;


}
