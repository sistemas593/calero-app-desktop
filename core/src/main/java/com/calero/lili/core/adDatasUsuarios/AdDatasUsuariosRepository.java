package com.calero.lili.core.adDatasUsuarios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AdDatasUsuariosRepository extends JpaRepository<AdDataUsuarioEntity, UUID> {

    @Query(value = "SELECT id_registro as id_registro, id_data as id_data, id_usuario as id_usuario " +
            "FROM ad_datas_usuarios " +
            "where id_data = :idData AND " +
            "id_usuario= :idUsuario " +
            "LIMIT 1", nativeQuery = true)
    Optional<AdDataUsuarioEntity> findFirstByIdDataAndIdUsuario(Long idData, Long idUsuario);

    @Query(
            value = "SELECT entity.idRegistro as idRegistro," +
                    "entity.idData as idData, " +
                    "entity.idUsuario as idUsuario " +
                    "FROM AdDataUsuarioEntity entity " +
                    "WHERE (:idData IS NULL OR entity.idData = :idData) AND " +
                    "(:idUsuario IS NULL OR entity.idUsuario = :idUsuario)",
            countQuery = "SELECT COUNT(1) " +
                    "FROM AdDataUsuarioEntity entity " +
                    "WHERE (:idData IS NULL OR entity.idData = :idData) AND " +
                    "(:idUsuario IS NULL OR entity.idUsuario = :idUsuario)")
    Page<AdDataUsuarioEntity> findAllPaginate(Long idData, Long idUsuario, Pageable pageable);


    @Query("SELECT c FROM AdDataUsuarioEntity c WHERE c.idRegistro = :idRegistro AND c.idData = :idData")
    Optional<AdDataUsuarioEntity> getForIdDataAndIdRegistro(@Param("idRegistro") UUID idRegistro,
                                                            @Param("idData") Long idData);


    @Query("SELECT c FROM AdDataUsuarioEntity c WHERE c.idRegistro = :idRegistro")
    Optional<AdDataUsuarioEntity> getForIdRegistro(@Param("idRegistro") UUID idRegistro);

}
