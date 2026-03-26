package com.calero.lili.core.modAdminDatas;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminDatas.builder.AdDataBuilder;
import com.calero.lili.core.modAdminDatas.dto.AdDataResponseConfiguracionDto;
import com.calero.lili.core.modAdminDatas.dto.AdDatasCreationRequestDto;
import com.calero.lili.core.modAdminDatas.dto.AdDatasDto;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesServiceImpl;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesGetOneDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdDatasServiceImpl {

    private final AdDataRepository adDataRepository;
    private final AdDataBuilder adDataBuilder;
    private final VtClientesConfiguracionesServiceImpl clientesConfiguracionesService;


    public AdDataResponseConfiguracionDto create(AdDatasCreationRequestDto request, String usuario) {
        AdDataEntity entidad = adDataBuilder.builderEntity(request);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
        adDataRepository.save(entidad);
        clientesConfiguracionesService.saveConfigurationAdData(entidad.getIdData(), entidad.getClave());
        return adDataBuilder.builderResponseConfiguracion(entidad);
    }

    public AdDataResponseConfiguracionDto update(Long idData, AdDatasCreationRequestDto request, String usuario) {
        AdDataEntity entidad = adDataRepository.findByIdData(idData).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} no exists", idData)));

        AdDataEntity update = adDataBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        adDataRepository.save(update);
        return adDataBuilder.builderResponseConfiguracion(entidad);
    }

    public AdDatasDto findByIdData(Long idData) {
        AdDataEntity entidad = adDataRepository.findById(idData).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} no exists", idData)));
        return adDataBuilder.builderResponse(entidad);
    }

    public PaginatedDto<AdDatasDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<AdDataEntity> page = adDataRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<AdDatasDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adDataBuilder::builderResponse)
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

    public VtClientesConfiguracionesGetOneDto findByIdDataConfiguracion(Long idData) {
        AdDataEntity entidad = adDataRepository.findByIdData(idData).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} no exists", idData)));
        return clientesConfiguracionesService.findById(entidad.getIdData(), entidad.getClave());
    }
}
