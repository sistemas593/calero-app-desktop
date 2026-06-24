package com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasRepository;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.builder.TsCajasUsuarioBuilder;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioRequestDto;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TsCajasUsuarioServiceImpl {

    private final TsCajasUsuariosRepository tsCajasUsuariosRepository;
    private final TsCajasUsuarioBuilder tsCajasUsuarioBuilder;
    private final TsCajasRepository tsCajasRepository;

    public TsCajasUsuarioResponseDto create(Long idData, Long idEmpresa,
                                            TsCajasUsuarioRequestDto request, String usuario) {

        TsCajasEntity caja = tsCajasRepository.findById(idData, idEmpresa, request.getIdCajas())
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja con id {0}, no existe", request.getIdCajas())));


        Optional<TsCajasUsuariosEntity> exists = tsCajasUsuariosRepository
                .findByIdCaja(idData, idEmpresa, request.getIdCajas(), request.getIdUsuario());

        if (exists.isPresent()) {
            throw new GeneralException(MessageFormat.format("La caja de usuario con id caja {0} y id usuario {1}" +
                    ", ya existe", request.getIdCajas(), request.getIdUsuario()));
        }

        TsCajasUsuariosEntity cajasUsuario = tsCajasUsuarioBuilder.builderEntity(idData, idEmpresa, request);
        cajasUsuario.setCajas(caja);
        cajasUsuario.setCreatedBy(usuario);
        cajasUsuario.setCreatedDate(LocalDateTime.now());

        return tsCajasUsuarioBuilder.builderResponse(tsCajasUsuariosRepository.save(cajasUsuario));
    }

    public TsCajasUsuarioResponseDto update(Long idData, Long idEmpresa, UUID idCajaUsuario,
                                            TsCajasUsuarioRequestDto request, String usuario) {

        TsCajasEntity caja = tsCajasRepository.findById(idData, idEmpresa, request.getIdCajas())
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja con id {0}, no existe", request.getIdCajas())));


        TsCajasUsuariosEntity exists = tsCajasUsuariosRepository
                .findById(idData, idEmpresa, idCajaUsuario)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja usuario con id {0}, no existe", idCajaUsuario)));


        TsCajasUsuariosEntity cajasUsuario = tsCajasUsuarioBuilder.builderUpdateEntity(request, exists);
        cajasUsuario.setCajas(caja);
        cajasUsuario.setModifiedBy(usuario);
        cajasUsuario.setModifiedDate(LocalDateTime.now());

        return tsCajasUsuarioBuilder.builderResponse(tsCajasUsuariosRepository.save(cajasUsuario));
    }


    public void delete(Long idData, Long idEmpresa, UUID idCajaUsuario, String usuario) {

        TsCajasUsuariosEntity cajasUsuario = tsCajasUsuariosRepository
                .findById(idData, idEmpresa, idCajaUsuario)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja usuario con id {0}, no existe", idCajaUsuario)));

        cajasUsuario.setDelete(Boolean.TRUE);
        cajasUsuario.setDeletedBy(usuario);
        cajasUsuario.setDeletedDate(LocalDateTime.now());

        tsCajasUsuariosRepository.save(cajasUsuario);
    }

    public TsCajasUsuarioResponseDto findById(Long idData, Long idEmpresa, UUID idCajaUsuario) {

        return tsCajasUsuarioBuilder.builderResponse(tsCajasUsuariosRepository
                .findById(idData, idEmpresa, idCajaUsuario)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja usuario con id {0}, no existe", idCajaUsuario))));


    }

    public List<TsCajasUsuarioResponseDto> findAll(Long idData, Long idEmpresa) {

        return tsCajasUsuariosRepository
                .findAll(idData, idEmpresa)
                .stream()
                .map(tsCajasUsuarioBuilder::builderResponse)
                .toList();


    }

}
