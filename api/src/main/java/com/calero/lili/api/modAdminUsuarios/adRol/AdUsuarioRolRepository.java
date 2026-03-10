package com.calero.lili.api.modAdminUsuarios.adRol;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdUsuarioRolRepository extends JpaRepository<AdRolEntity, Long> {

    @Query("select u from AdRolEntity u where u.nombre =:nombre ")
    Optional<AdRolEntity> findForName(String nombre);

    @Query("select u from AdRolEntity u where u.idRol =:id ")
    Optional<AdRolEntity> getFindId(@Param("id") Long id);

    @Query(
            value = "SELECT entity FROM AdRolEntity entity " +
                    "WHERE (:filter IS NULL OR " +
                    "LOWER(entity.nombre) LIKE CONCAT('%', LOWER(:filter), '%'))",
            countQuery = "SELECT COUNT(entity) FROM AdRolEntity entity " +
                    "WHERE (:filter IS NULL OR " +
                    "LOWER(entity.nombre) LIKE CONCAT('%', LOWER(:filter), '%'))"
    )
    Page<AdRolEntity> findAllPaginate(@Param("filter") String filter, Pageable pageable);

}
