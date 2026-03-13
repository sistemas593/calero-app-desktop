package com.calero.lili.api.adDatasUsuarios;


import com.calero.lili.api.adDatasUsuarios.builder.AdDataUsuarioBuilder;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioCreationRequestDto;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioCreationResponseDto;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioListFilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminDatas.AdDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdDatasUsuariosServiceImpl {

    private final AdDatasUsuariosRepository adDatasUsuariosRepository;
    private final AdDataRepository adDataRepository;
    private final AdDataUsuarioBuilder adDataUsuarioBuilder;

    public AdDataUsuarioCreationResponseDto create(Long idData, AdDataUsuarioCreationRequestDto request, String usuario) {

        adDataRepository.findById(idData)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("IdData {0} no exists", idData)));

        Optional<AdDataUsuarioEntity> existing = adDatasUsuariosRepository.findFirstByIdDataAndIdUsuario(idData, request.getIdUsuario());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat
                    .format("Data {0}, UsuarioSecurity {1} ya existe", idData, request.getIdUsuario()));
        }
        AdDataUsuarioEntity entity = adDataUsuarioBuilder.builderEntity(request, idData);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        return adDataUsuarioBuilder.builderResponse(entity);
    }


    public AdDataUsuarioCreationResponseDto update(Long idData, UUID idRegistro, AdDataUsuarioCreationRequestDto request, String usuario) {
        Optional<AdDataUsuarioEntity> existing = adDatasUsuariosRepository.getForIdDataAndIdRegistro(idRegistro, idData);
        if (existing.isEmpty()) {
            throw new GeneralException(MessageFormat.format("id number {0} no exists", idRegistro));
        }
        AdDataUsuarioEntity entity = adDataUsuarioBuilder.builderUpdateEntity(request, existing.get());
        entity.setModifiedBy(usuario);
        entity.setModifiedDate(LocalDateTime.now());
        return adDataUsuarioBuilder.builderResponse(entity);
    }

    public AdDataUsuarioCreationResponseDto findByIdData(UUID idRegistro) {
        return adDataUsuarioBuilder.builderResponse(adDatasUsuariosRepository.getForIdRegistro(idRegistro)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("No existe registro con el id {0}", idRegistro))));
    }


    public PaginatedDto<AdDataUsuarioCreationResponseDto> findAllPaginate(AdDataUsuarioListFilterDto filters, Pageable pageable) {
        Page<AdDataUsuarioEntity> page = adDatasUsuariosRepository.findAllPaginate(filters.getIdData(), filters.getIdUsuario(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdDataUsuarioCreationResponseDto>();
        paginatedDto.setContent(page.getContent().stream()
                .map(adDataUsuarioBuilder::builderResponse)
                .collect(Collectors.toList()));

        Paginator paginated = new Paginator();
        paginated.setTotalElements(page.getTotalElements());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setNumberOfElements(page.getNumberOfElements());
        paginated.setSize(page.getSize());
        paginated.setFirst(page.isFirst());
        paginated.setLast(page.isLast());
        paginated.setPageNumber(page.getPageable().getPageNumber());
        paginated.setPageSize(page.getPageable().getPageSize());
        paginated.setEmpty(page.isEmpty());
        paginated.setNumber(page.getNumber());

        paginatedDto.setPaginator(paginated);

        return paginatedDto;
    }

}
