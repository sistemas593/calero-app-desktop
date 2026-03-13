package com.calero.lili.api.modContabilidad.modAsientos;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.NotFoundException;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import com.calero.lili.api.modContabilidad.modAsientos.builder.CnAsientosBuilder;
import com.calero.lili.api.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.GetDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.GetListDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CnAsientosServiceImpl {

    private final CnAsientosRepository cnAsientosRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final CnAsientosBuilder cnAsientosBuilder;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;

    public ResponseDto create(Long idData, Long idEmpresa, CreationAsientosRequestDto request, String usuario) {


        validarSucursal(request, idData, idEmpresa);
        validarPlanCuenta(idData, idEmpresa, request);

        CnAsientosEntity entity = cnAsientosBuilder.builderEntity(request, idData, idEmpresa);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        CnAsientosEntity cnAsientosEntity = cnAsientosRepository.save(entity);
        return responseApiBuilder.builderResponse(cnAsientosEntity.getIdAsiento().toString());
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationAsientosRequestDto request, String usuario) {


        CnAsientosEntity exists = cnAsientosRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("idVenta {0} no existe", idVenta)));

        validarSucursal(request, idData, idEmpresa);
        validarPlanCuenta(idData, idEmpresa, request);

        CnAsientosEntity update = cnAsientosBuilder
                .builderUpdateEntity(request, exists);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        CnAsientosEntity cnAsientosEntity = cnAsientosRepository.save(update);
        return responseApiBuilder.builderResponse(cnAsientosEntity.getIdAsiento().toString());

    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario) {


        CnAsientosEntity venta = cnAsientosRepository.findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));


        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        cnAsientosRepository.save(venta);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta) {

        CnAsientosEntity cnAsientosEntity = cnAsientosRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no exixte", idVenta)));

        return cnAsientosBuilder.builderResponse(cnAsientosEntity);
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {

        Page<CnAsientosEntity> page = cnAsientosRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), pageable);

        List<GetListDto> dtoList = page.stream().map(cnAsientosBuilder::builderListResponse).toList();

        PaginatedDto paginatedDto = new PaginatedDto();
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


    public void validarPlanCuenta(Long idData, Long idEmpresa, CreationAsientosRequestDto request) {

        for (CreationAsientosRequestDto.DetailDto item : request.getDetalle()) {
            CnPlanCuentaEntity planCuenta = cnPlanCuentasRepository.findByIdCuenta(idData, idEmpresa, item.getIdCuenta())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("La cuenta con id {0} no existe",
                            item.getIdCuenta())));

            if (planCuenta.getMayor()) {
                throw new GeneralException("En las cuentas mayores no se permiten realizar movimientos.");
            }


        }


    }

    private void validarSucursal(CreationAsientosRequestDto request, Long idData, Long idEmpresa) {
        adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, request.getSucursal())
                .orElseThrow(() -> new GeneralException("No existe sucursal"));
    }
}



