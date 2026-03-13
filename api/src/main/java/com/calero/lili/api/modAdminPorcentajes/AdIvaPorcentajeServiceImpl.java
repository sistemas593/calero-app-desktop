package com.calero.lili.api.modAdminPorcentajes;

import com.calero.lili.api.modAdminPorcentajes.builder.AdIvaPorcentajeBuilder;
import com.calero.lili.api.modAdminPorcentajes.dto.AdIvaPorcentajesDto;
import com.calero.lili.api.modAdminPorcentajes.dto.AdIvaPorcentajesResponseDto;
import com.calero.lili.api.modAdminPorcentajes.dto.FilterListDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioReportDto;
import com.calero.lili.api.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdIvaPorcentajeServiceImpl {

    private final AdIvaPorcentajesRepository adIvaPorcentajesRepository;
    private final AdIvaPorcentajeBuilder adIvaPorcentajeBuilder;


    public AdIvaPorcentajesResponseDto create(AdIvaPorcentajesDto request, String usuario) {
        AdIvaPorcentajesEntity entidad = adIvaPorcentajeBuilder.builderEntity(request);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
        return adIvaPorcentajeBuilder.builderResponse(adIvaPorcentajesRepository.save(entidad));
    }


    public AdIvaPorcentajesResponseDto update(Long idPorcentaje, AdIvaPorcentajesDto request, String usuario) {

        AdIvaPorcentajesEntity entidad = adIvaPorcentajesRepository.findByIdPorcentaje(idPorcentaje)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El iva porcentaje con id {0}, no existe", idPorcentaje)));

        AdIvaPorcentajesEntity update = adIvaPorcentajeBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        return adIvaPorcentajeBuilder.builderResponse(adIvaPorcentajesRepository.save(entidad));
    }


    public AdIvaPorcentajesResponseDto findById(Long idPorcentaje) {
        AdIvaPorcentajesEntity entidad = adIvaPorcentajesRepository.findByIdPorcentaje(idPorcentaje)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El iva porcentaje con id {0}, no existe", idPorcentaje)));
        return adIvaPorcentajeBuilder.builderResponse(entidad);
    }

    public void delete(Long idPorcentaje, String usuario) {

        AdIvaPorcentajesEntity entidad = adIvaPorcentajesRepository.findByIdPorcentaje(idPorcentaje)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El iva porcentaje con id {0}, no existe", idPorcentaje)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setModifiedBy(usuario);
        entidad.setModifiedDate(LocalDateTime.now());
        adIvaPorcentajesRepository.save(entidad);
    }


    public PaginatedDto<AdIvaPorcentajesResponseDto> findAllPaginate(FilterListDto filtro, Pageable pageable) {

        Page<AdIvaPorcentajesEntity> page = adIvaPorcentajesRepository.findAllPaginate(filtro.getFechaDesde(),
                filtro.getFechaHasta(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdUsuarioReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adIvaPorcentajeBuilder::builderResponse)
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


    public void validateIvaPorcentaje(List<Integer> valores, LocalDate fechaFactura) {


        AdIvaPorcentajesEntity porcentaje = adIvaPorcentajesRepository.findVigente(fechaFactura)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe porcentajes de iva para la fecha: {0}", fechaFactura)));


        Set<Integer> tarifasVigentes = new HashSet<>();

        if (Objects.nonNull(porcentaje.getIva1()))

            if (porcentaje.getIva1() != 0) {
                tarifasVigentes.add(porcentaje.getIva1());
            }

        if (Objects.nonNull(porcentaje.getIva2())) {
            if (porcentaje.getIva2() != 0) {
                tarifasVigentes.add(porcentaje.getIva2());
            }
        }

        if (Objects.nonNull(porcentaje.getIva3())) {
            if (porcentaje.getIva3() != 0) {
                tarifasVigentes.add(porcentaje.getIva3());
            }
        }

        for (Integer tarifa : valores) {
            if (!tarifasVigentes.contains(tarifa)) {
                throw new GeneralException(MessageFormat
                        .format("La tarifa de IVA: {0}, no está vigente para la fecha {1}", tarifa, fechaFactura));
            }
        }


    }

}
