package com.calero.lili.api.modClientesTickets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VtClientesTicketsRepository extends JpaRepository<VtClienteTicketsEntity, UUID> {

        @Query(
            value = "SELECT entity "+
                    "FROM VtClienteTicketsEntity entity " +
                    "WHERE (entity.idData = ?1) ",
            countQuery = "SELECT COUNT(1) FROM VtClienteTicketsEntity entity "
    )
    Page<VtClienteTicketsEntity> findAllPaginate(Long idData, Pageable pageable);

        @Query(
            value = "SELECT entity "+
                    "FROM VtClienteTicketsEntity entity " +
                    "WHERE (entity.idData = ?1) AND " +
                    "entity.idTicket = ?2 "
    )
        Optional<VtClienteTicketsEntity> findById(Long idData, UUID idNovedad);
}
