package com.calero.lili.api.modAdminUsuarios.adPermisos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdPermisosRepository extends JpaRepository<AdPermisosEntity, Long> {

    @Query("select u from AdPermisosEntity u where u.permiso =:permiso ")
    Optional<AdPermisosEntity> getFindPermiso(@Param("permiso") String nombre);

    @Query("select u from AdPermisosEntity u where u.idPermiso =:idPermiso ")
    Optional<AdPermisosEntity> getFindId(@Param("idPermiso") Long idPermiso);

    @Query(value = "SELECT entity FROM AdPermisosEntity entity " +
            "WHERE (:filter IS NULL OR " +
            "LOWER(entity.nombre) LIKE CONCAT('%', LOWER(:filter), '%'))",
            countQuery = "SELECT COUNT(entity) FROM AdPermisosEntity entity " +
                    "WHERE (:filter IS NULL OR " +
                    "LOWER(entity.nombre) LIKE CONCAT('%', LOWER(:filter), '%'))")
    Page<AdPermisosEntity> findAllPaginate(@Param("filter") String filter, Pageable pageable);

}
