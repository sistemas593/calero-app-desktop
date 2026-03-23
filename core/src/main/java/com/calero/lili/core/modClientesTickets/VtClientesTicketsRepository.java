package com.calero.lili.core.modClientesTickets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VtClientesTicketsRepository extends JpaRepository<VtClienteTicketsEntity, UUID> {

    @Query(
            value = "SELECT entity " +
                    "FROM VtClienteTicketsEntity entity " +
                    "WHERE (entity.idData = :idData) ",
            countQuery = "SELECT COUNT(1) FROM VtClienteTicketsEntity entity "
    )
    Page<VtClienteTicketsEntity> findAllPaginate(@Param("idData") Long idData, Pageable pageable);

    @Query(
            value = "SELECT entity " +
                    "FROM VtClienteTicketsEntity entity " +
                    "WHERE (entity.idData = :idData) AND " +
                    "entity.idTicket = :idNovedad"
    )
    Optional<VtClienteTicketsEntity> findById(@Param("idData") Long idData, @Param("idNovedad") UUID idNovedad);
}
