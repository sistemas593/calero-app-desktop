package com.calero.lili.core.modVentas.reporteCredito;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatosCrediticiosRepository extends JpaRepository<DatosCrediticiosEntity, UUID> {
}
