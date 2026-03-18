package com.calero.lili.core.modRRHH.modRRHHRublos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RubrosRepository extends JpaRepository<RubrosEntity, UUID> {

    @Query("SELECT r FROM RubrosEntity r WHERE r.idRubro = :idRubro AND r.idEmpresa =:idEmpresa AND r.idData =:idData")
    Optional<RubrosEntity> getForFindById(@Param("idRubro") UUID idRubro,
                                          @Param("idEmpresa") Long idEmpresa,
                                          @Param("idData") Long idData);


    @Query("SELECT r FROM RubrosEntity r WHERE r.idEmpresa =:idEmpresa and r.idData =:idData")
    List<RubrosEntity> findAllList(@Param("idEmpresa") Long idEmpresa,
                                   @Param("idData") Long idData);


    @Query(value = "select *from rh_rubros rr where rr.id_data =:idData and rr.id_empresa =:idEmpresa and rr.codigo in (:listaCodigos) ", nativeQuery = true)
    List<RubrosEntity> findAllCodigos(@Param("idData") Long idData,
                                      @Param("idEmpresa") Long idEmpresa,
                                      @Param("listaCodigos") List<String> listaCodigos);


    @Query("SELECT r FROM RubrosEntity r WHERE r.codigo = :codigoRubro AND r.idEmpresa =:idEmpresa AND r.idData =:idData")
    Optional<RubrosEntity> getForFindCodigo(@Param("codigoRubro") String codigoRubro,
                                            @Param("idEmpresa") Long idEmpresa,
                                            @Param("idData") Long idData);


}
