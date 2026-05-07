package com.calero.lili.api.modAdminUsuarios.adGrupos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdGruposRepository extends JpaRepository<AdGruposEntity, Long> {

    @Query("select u from AdGruposEntity u where u.nombre =:nombre ")
    Optional<AdGruposEntity> getFindNombre(@Param("nombre") String nombre);

    @Query("select u from AdGruposEntity u where u.idGrupo =:idGrupo and u.idData =:idData AND u.idEmpresa =:idEmpresa")
    Optional<AdGruposEntity> getFindId(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idGrupo") Long idGrupo);


    @Query(
            value = "SELECT entity FROM AdGruposEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa AND " +
                    "(:filter IS NULL OR " +
                    "LOWER(entity.nombre) LIKE CONCAT('%', LOWER(:filter), '%'))",
            countQuery = "SELECT COUNT(entity) FROM AdGruposEntity entity " +
                    "WHERE (:filter IS NULL OR " +
                    "LOWER(entity.nombre) LIKE CONCAT('%', LOWER(:filter), '%'))"
    )
    Page<AdGruposEntity> findAllPaginate(@Param("idData") Long idData,
                                         @Param("idData") Long idEmpresa,
                                         @Param("filter") String filter, Pageable pageable);

}
