package com.calero.lili.api.modAdminUsuarios;

import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposEntity;
import com.calero.lili.api.modAdminUsuarios.adRol.AdRolEntity;
import com.calero.lili.api.modAdminUsuarios.enums.TipoUsuario;
import com.calero.lili.core.Auditable;
import com.calero.lili.core.dtos.models.IUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "adUsuarios")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdUsuarioEntity extends Auditable implements IUser {

    private Long idData;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Size(min = 4, max = 30)
    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private int random;

    // CONTROLLER USUARIOS, DATAS
    // SUPER,ADMIN y USUARIO

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @Transient
    private boolean admin;


//    private int siguienteIdEmpresa;

    // ad_usuarios_roles NO SE VE AQUI EN LAS ENTITIES SOLO SE VE ESTA RELACION

    @ManyToMany
    @JoinTable(
            name = "ad_usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_usuario", "id_rol"})})

    private List<AdRolEntity> roles;

    @ManyToMany
    @JoinTable(
            name = "ad_usuarios_grupos",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_grupo"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_usuario", "id_grupo"})})
    private List<AdGruposEntity> grupos;

}
