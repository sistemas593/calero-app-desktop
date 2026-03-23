package com.calero.lili.core.modAdminlistaNegra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdMailsListaNegraRepository extends JpaRepository<AdMailListaNegraEntity, String> {

    @Query("SELECT entity " +
            "FROM AdMailListaNegraEntity entity " +
            "WHERE entity.email IN :emails")
    List<AdMailListaNegraEntity> findByEmails(@Param("emails") List<String> emails);


    @Query(value = """
            SELECT *
            FROM ad_mails_lista_negra amln
            WHERE 
                (CAST(:fechaDesde AS date) IS NULL OR amln.fecha >= CAST(:fechaDesde AS date))
            AND (CAST(:fechaHasta AS date) IS NULL OR amln.fecha <= CAST(:fechaHasta AS date))
            AND (
                :email IS NULL 
                OR amln.email IS NOT NULL 
                AND LOWER(amln.email) LIKE LOWER(CONCAT('%', :email, '%'))
            )
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM ad_mails_lista_negra amln
                    WHERE 
                        (CAST(:fechaDesde AS date) IS NULL OR amln.fecha >= CAST(:fechaDesde AS date))
                    AND (CAST(:fechaHasta AS date) IS NULL OR amln.fecha <= CAST(:fechaHasta AS date))
                    AND (
                        :email IS NULL 
                        OR amln.email IS NOT NULL 
                        AND LOWER(amln.email) LIKE LOWER(CONCAT('%', :email, '%'))
                    )
                    """,
            nativeQuery = true)
    Page<AdMailListaNegraEntity> getFindAll(@Param("fechaDesde") LocalDate fechaDesde,
                                            @Param("fechaHasta") LocalDate fechaHasta,
                                            @Param("email") String email,
                                            Pageable pageable);


    @Query("""
            SELECT entity
            FROM AdMailListaNegraEntity entity
            WHERE entity.email =:email
            """)
    Optional<AdMailListaNegraEntity> findByEmail(@Param("email") String email);

}
