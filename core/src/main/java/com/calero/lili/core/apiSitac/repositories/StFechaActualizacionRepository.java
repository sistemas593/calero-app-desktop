package com.calero.lili.core.apiSitac.repositories;

import com.calero.lili.core.apiSitac.repositories.entities.StFechaActualizacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StFechaActualizacionRepository extends JpaRepository<StFechaActualizacionEntity, String> {


}
