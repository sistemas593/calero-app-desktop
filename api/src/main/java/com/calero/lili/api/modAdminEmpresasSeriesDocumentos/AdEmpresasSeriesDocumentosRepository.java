package com.calero.lili.api.modAdminEmpresasSeriesDocumentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdEmpresasSeriesDocumentosRepository extends JpaRepository<AdEmpresasSeriesDocumentosEntity, UUID> {


    @Query(value = """
            select aesd.*
            from ad_empresas_series_documentos aesd
            join ad_empresas_series aes on aes.id_serie = aesd.id_serie
            where aesd.id_data = :idData and aesd.id_empresa = :idEmpresa
            and aes.serie = :serie and aesd.documento = :documento
            """, nativeQuery = true)
    Optional<AdEmpresasSeriesDocumentosEntity> findBySerieAndDocumento(@Param("idData") Long idData,
                                                                       @Param("idEmpresa") Long idEmpresa,
                                                                       @Param("serie") String serie,
                                                                       @Param("documento") String documento);

}
