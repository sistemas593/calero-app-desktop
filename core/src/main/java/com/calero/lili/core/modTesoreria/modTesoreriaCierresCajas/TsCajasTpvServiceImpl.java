package com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.builder.TsCajasTpvBuilder;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvCreationResponseDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvListFilterDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsCajasTpvServiceImpl {

    private final TsCajasTpvRepository bcCajasTpvRepository;
    private final TsCajasTpvBuilder tsCajasTpvBuilder;


    public BcCajaTpvCreationResponseDto create(Long idData, Long idEmpresa, BcCajaTpvCreationRequestDto request, String usuario) {

        return tsCajasTpvBuilder.builderResponse(bcCajasTpvRepository.save(tsCajasTpvBuilder
                .builderEntity(request, idData, idEmpresa)));
    }

    public BcCajaTpvCreationResponseDto update(Long idData, Long idEmpresa, UUID id, BcCajaTpvCreationRequestDto request, String usuario) {

        TsCajasTpvEntity entidad = bcCajasTpvRepository.findByIdCajaTpv(idData, idEmpresa, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        return tsCajasTpvBuilder.builderResponse(bcCajasTpvRepository.save(tsCajasTpvBuilder
                .builderUpdateEntity(request, entidad)));
    }

    public void delete(Long idData, Long idEmpresa, Long id, String usuario) {

        bcCajasTpvRepository.deleteByIdDataAndEmpresaAndidCajaTpv(idData, idEmpresa, id);
    }

    public BcCajaTpvCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        TsCajasTpvEntity entidad = bcCajasTpvRepository.findByIdCajaTpv(idData, idEmpresa, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return tsCajasTpvBuilder.builderResponse(entidad);
    }


    public PaginatedDto<BcCajaTpvCreationResponseDto> findAllPaginate(Long idData, Long idEmpresa, BcCajaTpvListFilterDto filters, Pageable pageable) {

        Page<TsCajasTpvEntity> page = bcCajasTpvRepository.findAllPaginate(idData, idEmpresa, pageable);
        PaginatedDto paginatedDto = new PaginatedDto<BcCajaTpvReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(tsCajasTpvBuilder::builderResponse)
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
