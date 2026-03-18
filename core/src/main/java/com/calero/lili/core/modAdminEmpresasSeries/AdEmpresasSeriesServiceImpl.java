package com.calero.lili.core.modAdminEmpresasSeries;

import com.calero.lili.core.modAdminEmpresasSeries.builder.AdEmpresasSeriesBuilder;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieCreationRequestDto;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieGetDto;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieGetListDto;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieListFilterDto;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdEmpresasSeriesServiceImpl {

    private final AdEmpresasSeriesRepository adEmpresasSeriesRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final AdEmpresasSeriesBuilder adEmpresasSeriesBuilder;


    public ResponseDto create(Long idData, Long idEmpresa,
                              AdEmpresaSerieCreationRequestDto request, String usuario) {


        adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {0} no existe", idEmpresa)));

        Optional<AdEmpresasSeriesEntity> existeData = adEmpresasSeriesRepository.findBySerie(idData, idEmpresa, request.getSerie());
        if (existeData.isPresent()) {
            throw new GeneralException(MessageFormat.format("Serie {1} ya existe", idEmpresa, request.getSerie()));
        }

        validarListaDocumentos(request);
        AdEmpresasSeriesEntity serie = adEmpresasSeriesBuilder.builderEntity(request, idEmpresa, idData);
        serie.setCreatedBy(usuario);
        serie.setCreatedDate(LocalDateTime.now());
        adEmpresasSeriesRepository.save(serie);
        return responseApiBuilder.builderResponse(serie.getIdSerie().toString());

    }

    private void validarListaDocumentos(AdEmpresaSerieCreationRequestDto request) {

        Set<String> tiposUnicos = new HashSet<>();

        for (AdEmpresaSerieCreationRequestDto.Documentos model : request.getDocumentos()) {

            if (Objects.isNull(model.getFormatoDocumento())) {
                throw new GeneralException("El formatoDocumento no puede ser nulo");
            }


            // Agregar a la lista en caso de ser necesario !List.of("FAC", "NDB", "NCR", "GRM").contains en el if

            // Validar que sea uno permitido
            if ("FAC".equals(model.getDocumento())) {
                throw new GeneralException("Tipo de documento no válido: " + model.getDocumento());
            }

            // Validar duplicados
            if (!tiposUnicos.add(model.getDocumento())) {
                throw new GeneralException("Solo puede existir un documento por tipo: " + model.getDocumento());
            }

        }
    }

    public ResponseDto update(Long idData, Long idEmpresa, UUID id, AdEmpresaSerieCreationRequestDto request,
                              String usuario) {

        AdEmpresasSeriesEntity entidad = adEmpresasSeriesRepository.findById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", request.getSerie()));
        }

        validarListaDocumentos(request);

        AdEmpresasSeriesEntity update = adEmpresasSeriesBuilder
                .builderUpdateEntity(request, entidad);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        adEmpresasSeriesRepository.save(update);
        return responseApiBuilder.builderResponse(entidad.getIdSerie().toString());
    }

    public void delete(Long idData, Long idEmpresa, UUID idSerie, String usuario) {

        AdEmpresasSeriesEntity serie = adEmpresasSeriesRepository.findById(idData, idEmpresa, idSerie)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", idSerie)));

        serie.setDelete(Boolean.TRUE);
        serie.setDeletedBy(usuario);
        serie.setDeletedDate(LocalDateTime.now());

        adEmpresasSeriesRepository.save(serie);
    }

    public AdEmpresaSerieGetDto findById(Long idData, Long idEmpresa, UUID id) {

        AdEmpresasSeriesEntity series = adEmpresasSeriesRepository.findById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return adEmpresasSeriesBuilder.builderResponse(series);
    }

    public PaginatedDto<AdEmpresaSerieGetListDto> findAllPaginate(Long idData, Long idEmpresa, AdEmpresaSerieListFilterDto filters, Pageable pageable) {


        Page<AdEmpresasSeriesEntity> page = adEmpresasSeriesRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);

        List<AdEmpresaSerieGetListDto> dtoList = page.stream().map(adEmpresasSeriesBuilder::builderResponseList).toList();

        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaSerieGetListDto>();
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
}
