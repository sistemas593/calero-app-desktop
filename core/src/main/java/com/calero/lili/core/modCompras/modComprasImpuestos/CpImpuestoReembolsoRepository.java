package com.calero.lili.core.modCompras.modComprasImpuestos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CpImpuestoReembolsoRepository extends JpaRepository<CpImpuestosReembolsosEntity, UUID> {
}
