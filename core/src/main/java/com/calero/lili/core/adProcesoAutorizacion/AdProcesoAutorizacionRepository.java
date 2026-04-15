package com.calero.lili.core.adProcesoAutorizacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdProcesoAutorizacionRepository extends JpaRepository<AdProcesoAutorizacionEntity, String> {
}
