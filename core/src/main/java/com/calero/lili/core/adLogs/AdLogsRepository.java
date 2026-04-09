package com.calero.lili.core.adLogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface AdLogsRepository extends JpaRepository<AdLogsEntity, Long> {

    @Query("SELECT a FROM AdLogsEntity a WHERE a.idDocumento = :id")
    List<AdLogsEntity> getFindById(@Param("id") UUID id);




}
