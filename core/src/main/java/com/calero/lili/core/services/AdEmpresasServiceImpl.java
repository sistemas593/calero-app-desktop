package com.calero.lili.core.services;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.GeneralException;
import com.calero.lili.core.entities.AdDataEntity;
import com.calero.lili.core.entities.AdEmpresaEntity;
import com.calero.lili.core.repositories.AdDataRepository;
import com.calero.lili.core.repositories.AdEmpresasRepository;
import com.calero.lili.core.repositories.AdEmpresasSucursalesRepository;
import com.calero.lili.core.builders.AdDataBuilder;
import com.calero.lili.core.builders.AdEmpresaBuilder;
import com.calero.lili.core.builders.AdEmpresasSucursalesBuilder;
import com.calero.lili.core.dtos.AdEmpresaCreationResponseDto;
import com.calero.lili.core.dtos.AdEmpresaGetListDto;
import com.calero.lili.core.dtos.AdEmpresaGetOneDto;
import com.calero.lili.core.dtos.AdEmpresaListFilterDto;
import com.calero.lili.core.dtos.AdEmpresaRequestDto;
import com.calero.lili.core.dtos.AdEmpresaRucResponseDto;
import com.calero.lili.core.utils.AESUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdEmpresasServiceImpl {

    private final AdEmpresasRepository adEmpresasRepository;
    private final AdDataRepository adDataRepository;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;
    private final AdEmpresaBuilder adEmpresaBuilder;
    private final AdEmpresasSucursalesBuilder adEmpresasSucursalesBuilder;
    private final AdDataBuilder adDataBuilder;
    private final AuditorAware<String> auditorAware;

    @Transactional
    public AdEmpresaCreationResponseDto create(Long idData, AdEmpresaRequestDto request) {
        validacionRucEmpresa(request);
        AdDataEntity existeData = adDataRepository.findById(idData).orElseThrow(() -> new GeneralException(MessageFormat.format("IdData {0} no exists", idData)));
        Long nextIdEmpresa = existeData.getSiguienteIdEmpresa();

        Optional<AdEmpresaEntity> existing = adEmpresasRepository.findById(idData, nextIdEmpresa);
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Empresa con id numero {0} ya existe", nextIdEmpresa));
        }

        AdEmpresaEntity entidad = adEmpresaBuilder.builderEntity(request, idData, nextIdEmpresa);
        entidad.setContraseniaFirma(Objects.nonNull(request.getContraseniaFirma())
                ? AESUtils.encrypt(request.getContraseniaFirma())
                : "");
        entidad = adEmpresasRepository.save(entidad);

        adEmpresasSucursalesRepository.save(adEmpresasSucursalesBuilder.builderEmpresaSucursal(entidad));

        adDataRepository.save(adDataBuilder.builderAdDataCreate(existeData, nextIdEmpresa));
        return adEmpresaBuilder.builderResponseDto(entidad);
    }

    private void validacionRucEmpresa(AdEmpresaRequestDto request) {
        Optional<AdEmpresaEntity> entidad = adEmpresasRepository.findByRuc(request.getRuc());
        if (entidad.isPresent()) {
            throw new GeneralException(MessageFormat.format("El ruc {0} ya se encuentra registrado", request.getRuc()));
        }
    }

    public AdEmpresaCreationResponseDto update(Long idData, Long idEmpresa, AdEmpresaRequestDto request) {
        AdEmpresaEntity existente = adEmpresasRepository.findById(idData, idEmpresa).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", idEmpresa)));

        if (!existente.getRuc().equals(request.getRuc())) {
            validacionRucEmpresa(request);
        }

        AdEmpresaEntity actualizar = adEmpresaBuilder.builderUpdateEntity(request, existente);
        actualizar.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        actualizar.setModifiedDate(LocalDateTime.now());

        actualizar.setContraseniaFirma(Objects.nonNull(request.getContraseniaFirma())
                ? AESUtils.encrypt(request.getContraseniaFirma())
                : existente.getContraseniaFirma());
        adEmpresasRepository.save(actualizar);
        return adEmpresaBuilder.builderResponseDto(actualizar);
    }

    public AdEmpresaGetOneDto findById(Long idData, Long idEmpresa) {
        AdEmpresaEntity entidad = adEmpresasRepository.findById(idData, idEmpresa).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", idEmpresa)));
        return adEmpresaBuilder.builderResponse(entidad);
    }

    public PaginatedDto<AdEmpresaGetListDto> findAllPaginate(Long idData, AdEmpresaListFilterDto filters, Pageable pageable) {
        Page<AdEmpresaEntity> page = adEmpresasRepository.findAllPaginate(idData, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", filters.getEstado(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaGetListDto>();
        List<AdEmpresaGetListDto> dtoList = page.stream().map(adEmpresaBuilder::builderListResponse).toList();
        paginatedDto.setContent(dtoList);

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

    public AdEmpresaRucResponseDto findByRuc(String ruc) {
        AdEmpresaEntity entidad = adEmpresasRepository.findByRuc(ruc)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El ruc {0} de la empresa no existe", ruc)));
        return adEmpresaBuilder.builderRucResponse(entidad);
    }
}
