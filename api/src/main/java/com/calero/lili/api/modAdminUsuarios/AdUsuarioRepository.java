package com.calero.lili.api.modAdminUsuarios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdUsuarioRepository extends JpaRepository<AdUsuarioEntity, Long> {

    AdUsuarioEntity findByUsername(String username);

    @Query("select u from AdUsuarioEntity u where u.username=?1")
    Optional<AdUsuarioEntity> getUserByUsername(String username);

    AdUsuarioEntity findByEmail(String mail);


    Page<AdUsuarioEntity> findAll(Pageable pageable);

    @Query(
            value = "SELECT entity " +
                    "FROM AdUsuarioEntity entity " +
                    "WHERE " +
                    ":filter IS NULL OR LOWER(entity.username) LIKE LOWER(CONCAT('%', :filterContent, '%'))"
    )
    Page<AdUsuarioEntity> findAllPaginatxxx(String filter, String filterContent, Pageable pageable);


    @Query(
            value = "SELECT entity " +
                    "FROM AdUsuarioEntity entity " +
                    "WHERE " +
                    ":filter IS NULL OR LOWER(entity.username) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.email) LIKE LOWER(CONCAT('%', :filterContent, '%'))",
            countQuery = "SELECT COUNT(1) FROM AdUsuarioEntity entity " +
                    "WHERE " +
                    ":filter IS NULL OR LOWER(entity.username) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.email) LIKE LOWER(CONCAT('%', :filterContent, '%')) ")
    Page<AdUsuarioEntity> findAllPaginate(String filter, String filterContent, Pageable pageable);


    @Query(
            value = "SELECT entity " +
                    "FROM AdUsuarioEntity entity " +
                    "WHERE " +
                    "entity.idData =:idData and " +
                    ":filter IS NULL OR LOWER(entity.username) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.email) LIKE LOWER(CONCAT('%', :filterContent, '%'))",
            countQuery = "SELECT COUNT(1) FROM AdUsuarioEntity entity " +
                    "WHERE " +
                    "entity.idData =:idData and " +
                    ":filter IS NULL OR LOWER(entity.username) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.email) LIKE LOWER(CONCAT('%', :filterContent, '%')) ")
    Page<AdUsuarioEntity> findAllPaginateIdData(Long idData, String filter, String filterContent, Pageable pageable);


    AdUsuarioEntity findByIdUsuario(Long idUsuario);


    @Query("SELECT entity " +
            "FROM AdUsuarioEntity entity WHERE " +
            "entity.idData = :idData AND entity.idUsuario = :idUsuario")
    Optional<AdUsuarioEntity> findByIdDataAndIdUsuario(Long idData, Long idUsuario);

}
