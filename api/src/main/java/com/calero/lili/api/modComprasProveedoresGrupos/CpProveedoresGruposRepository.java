package com.calero.lili.api.modComprasProveedoresGrupos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CpProveedoresGruposRepository extends JpaRepository<CpProveedoresGruposEntity, UUID> {


    Optional<CpProveedoresGruposEntity> findByIdDataAndIdEmpresaAndIdGrupo(Long idData, Long idEmpresa, UUID idGrupo);

    @Query(
            value = "SELECT entity " +
                    "FROM CpProveedoresGruposEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa)  AND " +
                    "(:filter IS NULL OR LOWER(entity.grupo) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) ")
    List<CpProveedoresGruposEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent);


    @Query("select  e from CpProveedoresGruposEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.predeterminado = :predeterminado")
    Optional<CpProveedoresGruposEntity> findByIdPredeterminado(Long idData, Long idEmpresa, Boolean predeterminado);

}
