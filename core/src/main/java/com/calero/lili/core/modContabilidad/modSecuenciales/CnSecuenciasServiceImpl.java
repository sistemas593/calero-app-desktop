package com.calero.lili.core.modContabilidad.modSecuenciales;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modContabilidad.modSecuenciales.builder.CnSecuenciasBuilder;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasRequestDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasResponseDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.projection.CnSecuenciasProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CnSecuenciasServiceImpl {


    private final CnSecuenciasRepository cnSecuenciasRepository;
    private final CnSecuenciasBuilder cnSecuenciasBuilder;

    public CnSecuenciasResponseDto create(Long idData, Long idEmpresa,
                                          CnSecuenciasRequestDto request, String usuario) {

       /* Optional<CnSecuenciasProjection> exits = cnSecuenciasRepository.findByAnioMesSucursal(idData, idEmpresa,
                request.getSucursal(), request.getAnio(), request.getMes());

        if (exits.isPresent()) {
            throw new GeneralException(MessageFormat.format("El secuencial con sucursal: {0}, año: {1}, mes: {2} ya existe", request.getSucursal(), request.getAnio(), request.getMes()));
        }*/

        CnSecuenciasEntity secuencias = cnSecuenciasBuilder.builderEntity(idData, idEmpresa, request);
        secuencias.setCreatedBy(usuario);
        secuencias.setCreatedDate(LocalDateTime.now());
        return cnSecuenciasBuilder.builderResponse(cnSecuenciasRepository.save(secuencias));
    }


    public CnSecuenciasResponseDto update(Long idData, Long idEmpresa, UUID idSecuencia,
                                          CnSecuenciasRequestDto request, String usuario) {

        CnSecuenciasEntity secuencias = cnSecuenciasRepository.findById(idData, idEmpresa, idSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe secuencia con id {0}, con idData {1}, con idEmpresa {2} "
                        , idSecuencia, idData, idEmpresa)));

        /*Optional<CnSecuenciasProjection> exits = cnSecuenciasRepository.findByAnioMesSucursal(idData, idEmpresa,
                request.getSucursal(), request.getAnio(), request.getMes());

        if (exits.isPresent()) {

            if (!exits.get().getIdSecuencia().equals(idSecuencia)) {
                throw new GeneralException(MessageFormat.format("El secuencial con sucursal: {0}, año: {1}," +
                        " mes: {2} ya existe", request.getSucursal(), request.getAnio(), request.getMes()));
            }

        }*/

        CnSecuenciasEntity update = cnSecuenciasBuilder.builderUpdateEntity(secuencias, request);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        return cnSecuenciasBuilder.builderResponse(cnSecuenciasRepository.save(update));
    }

    public void delete(Long idData, Long idEmpresa, UUID idSecuencia, String usuario) {

        CnSecuenciasEntity secuencias = cnSecuenciasRepository.findById(idData, idEmpresa, idSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe secuencia con id {0}, con idData {1}, con idEmpresa {2} "
                        , idSecuencia, idData, idEmpresa)));

        secuencias.setDelete(Boolean.TRUE);
        secuencias.setDeletedBy(usuario);
        secuencias.setDeletedDate(LocalDateTime.now());
        cnSecuenciasRepository.save(secuencias);
    }


    public CnSecuenciasResponseDto findById(Long idData, Long idEmpresa, UUID idSecuencia) {
        return cnSecuenciasBuilder.builderResponse(cnSecuenciasRepository.findById(idData, idEmpresa, idSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe secuencia con id {0}, con idData {1}, con idEmpresa {2} "
                        , idSecuencia, idData, idEmpresa))));
    }


    public List<CnSecuenciasResponseDto> findAll(Long idData, Long idEmpresa) {
        return cnSecuenciasRepository.getFindAll(idData, idEmpresa)
                .stream()
                .map(cnSecuenciasBuilder::builderResponse)
                .toList();
    }

}
