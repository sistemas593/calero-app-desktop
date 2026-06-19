package com.calero.lili.core.adInfoAdicional;

import com.calero.lili.core.adInfoAdicional.projection.OneAdInfoProjection;
import com.calero.lili.core.enums.TipoDocumentoSerie;
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


    @Query(value = "SELECT entity.id_info_adicional " +
            "FROM ad_info_adicional entity " +
            "where entity.id_data = :idData and " +
            "entity.id_empresa = :idEmpresa and entity.documento = :documento" , nativeQuery = true)
    Optional<OneAdInfoProjection> findByTipoDocumento(@Param("idData") Long idData,
                                                      @Param("idEmpresa") Long idEmpresa,
                                                      @Param("documento") String documento);


}
