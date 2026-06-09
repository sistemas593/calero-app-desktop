package com.calero.lili.core.adInfoAdicional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdInfoAdicionalRepository extends JpaRepository<AdInfoAdicionalEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM AdInfoAdicionalEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and entity.idInfoAdicional = :idInfoAdicional")
    Optional<AdInfoAdicionalEntity> findByIdInfoAdicional(@Param("idData") Long idData,
                                                          @Param("idEmpresa") Long idEmpresa,
                                                          @Param("idInfoAdicional") UUID idInfoAdicional);


    @Query(value = "SELECT entity " +
            "FROM AdInfoAdicionalEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa")
    List<AdInfoAdicionalEntity> findAll(@Param("idData") Long idData,
                                        @Param("idEmpresa") Long idEmpresa);

}
