package com.calero.lili.core.repositories;

import com.calero.lili.core.entities.AdDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface AdDataRepository extends JpaRepository<AdDataEntity, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM AdDataEntity e WHERE e.idData = ?1 ")
    void deleteByIdData(Long idData);

    Optional<AdDataEntity> findByIdData(Long idData);

    @Query(
            value = "SELECT entity " +
                    "FROM AdDataEntity entity "+
                    "WHERE " +
                    "(:filter IS NULL OR LOWER(entity.data) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) "
           ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM AdDataEntity entity " +
                    "WHERE " +
                    "(:filter IS NULL OR LOWER(entity.data) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) "
    )
    Page<AdDataEntity> findAllPaginate(String filter, String filterContent, Pageable pageable);
}
