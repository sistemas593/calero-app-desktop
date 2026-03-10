package com.calero.lili.api.modComprasItems;

import com.calero.lili.api.modComprasItems.builder.GetItemBuilder;
import com.calero.lili.api.modComprasItems.dto.GeItemGetListDto;
import com.calero.lili.api.modComprasItems.dto.GeItemGetOneDto;
import com.calero.lili.api.modComprasItems.dto.GeItemListFilterDto;
import com.calero.lili.api.modComprasItems.dto.GeItemRequestDto;
import com.calero.lili.api.modComprasItems.dto.GeItemRequestListDto;
import com.calero.lili.api.modComprasItems.dto.GeMedidasResponseDto;
import com.calero.lili.api.modComprasItemsMedidas.GeItemsMedidasEntity;
import com.calero.lili.api.modComprasItemsMedidas.GeItemsMedidasRepository;
import com.calero.lili.api.utils.validaciones.ValidarCampoAscii;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.errors.DetallesErrores;
import com.calero.lili.core.dtos.errors.ListCreationResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GeItemsServiceImpl {

    private final GeItemsRepository geItemsRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final GetItemBuilder getItemBuilder;
    private final GeItemsMedidasRepository geItemsMedidasRepository;
    private final AuditorAware<String> auditorAware;

    public GeItemGetListDto create(Long idData, Long idEmpresa, GeItemRequestDto request) {

        ValidarCampoAscii.validarStrings(request);
        adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {1} no existe", idData, idEmpresa)));

        Optional<GeItemEntity> geItemsEntity = geItemsRepository.findByCodigoItem(idData, idEmpresa, request.getCodigoPrincipal());
        if (!geItemsEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("El item con codigo {0} ya existe", request.getCodigoPrincipal()));
        }

        GeItemEntity entity = geItemsRepository.save(getItemBuilder.builderEntity(request, idData, idEmpresa));
        return getItemBuilder.builderListResponse(entity);

    }


    public ListCreationResponseDto createListItems(Long idData, Long idEmpresa, GeItemRequestListDto request) {

        AdEmpresaEntity empresasEntity = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {1} no existe", idData, idEmpresa)));

        ListCreationResponseDto response = new ListCreationResponseDto();
        List<DetallesErrores> detallesErrores = new ArrayList<>();
        List<GeItemRequestDto> listaItems = request.getListaItems();

        System.out.println(listaItems.size());
        IntStream.range(0, listaItems.size())
                .forEach(index -> {
                    GeItemRequestDto requestDto = listaItems.get(index);
                    System.out.println("salio");

                    String valido = "";

                    if (requestDto.getCodigoPrincipal() == null || requestDto.getCodigoPrincipal().isEmpty()) {
                        DetallesErrores detalleError = new DetallesErrores();
                        detalleError.setIndex(index);
                        detalleError.setDescripcion("El codigo de item no existe");
                        detallesErrores.add(detalleError);
                        valido = "N";
                    }

                    if (requestDto.getDescripcion() == null || requestDto.getDescripcion().isEmpty()) {
                        DetallesErrores detalleError = new DetallesErrores();
                        detalleError.setIndex(index);
                        detalleError.setDescripcion("El nombre del item no existe");
                        detallesErrores.add(detalleError);
                        valido = "N";
                    }

                    if (requestDto.getCodigoIva() == null || requestDto.getCodigoIva().isEmpty()) {
                        DetallesErrores detalleError = new DetallesErrores();
                        detalleError.setIndex(index);
                        detalleError.setDescripcion("El codigo de IVA no existe");
                        detallesErrores.add(detalleError);
                        valido = "N";
                    }

                    if (valido.equals("")) {
                        Optional<GeItemEntity> geItemsExist = geItemsRepository.findByCodigoItem(idData, idEmpresa, requestDto.getCodigoPrincipal());
                        if (geItemsExist.isEmpty()) {

                            // AGREGAR
                            GeItemEntity geItemsNew = new GeItemEntity();
                            geItemsNew.setIdData(idData);
                            geItemsNew.setIdEmpresa(idEmpresa);
                            geItemsNew.setIdItem(UUID.randomUUID());
                            geItemsNew.setCodigoPrincipal(requestDto.getCodigoPrincipal());
                            geItemsNew.setCodigoBarras(requestDto.getCodigoBarras());
                            geItemsNew.setDescripcion(requestDto.getDescripcion());

                            List<GeItemEntity.DetalleAdicional> listaDetallesAdicionales = new ArrayList<>();
                            if (requestDto.getNombreDetalleAdicional1() != null
                                    && !requestDto.getNombreDetalleAdicional1().isEmpty()
                                    && requestDto.getValorDetalleAdicional1() != null
                                    && !requestDto.getValorDetalleAdicional1().isEmpty()) {
                                GeItemEntity.DetalleAdicional detalleAdicional = new GeItemEntity.DetalleAdicional();
                                detalleAdicional.setNombre(requestDto.getNombreDetalleAdicional1());
                                detalleAdicional.setValor(requestDto.getValorDetalleAdicional1());
                                listaDetallesAdicionales.add(detalleAdicional);
                            }
                            if (requestDto.getNombreDetalleAdicional2() != null
                                    && !requestDto.getNombreDetalleAdicional2().isEmpty()
                                    && requestDto.getValorDetalleAdicional2() != null
                                    && !requestDto.getValorDetalleAdicional2().isEmpty()) {
                                GeItemEntity.DetalleAdicional detalleAdicional = new GeItemEntity.DetalleAdicional();
                                detalleAdicional.setNombre(requestDto.getNombreDetalleAdicional2());
                                detalleAdicional.setValor(requestDto.getValorDetalleAdicional2());
                                listaDetallesAdicionales.add(detalleAdicional);
                            }
                            if (requestDto.getNombreDetalleAdicional3() != null
                                    && !requestDto.getNombreDetalleAdicional3().isEmpty()
                                    && requestDto.getValorDetalleAdicional3() != null
                                    && !requestDto.getValorDetalleAdicional3().isEmpty()) {
                                GeItemEntity.DetalleAdicional detalleAdicional = new GeItemEntity.DetalleAdicional();
                                detalleAdicional.setNombre(requestDto.getNombreDetalleAdicional3());
                                detalleAdicional.setValor(requestDto.getValorDetalleAdicional3());
                                listaDetallesAdicionales.add(detalleAdicional);
                            }
                            geItemsNew.setDetallesAdicionales(listaDetallesAdicionales);
                            geItemsRepository.save(geItemsNew);

                        } else {

                            // MODIFICAR
//                                List <GeItemEntity.Impuesto> listaImpuesto = new ArrayList<GeItemEntity.Impuesto>();
//                                GeItemEntity.Impuesto impuesto = new GeItemEntity.Impuesto();
//
//                                if (requestDto.getCodigoIva().equals("0")){
//                                    impuesto.setCodigo("2");
//                                    impuesto.setCodigoPorcentaje("0");
//                                    impuesto.setTarifa(String.valueOf(0));
//                                    listaImpuesto.add(impuesto);
//                                }
//                                if (requestDto.getCodigoIva().equals("1")){
//                                    impuesto.setCodigo("2");
//                                    impuesto.setCodigoPorcentaje("1");
//                                    impuesto.setTarifa(String.valueOf(15));
//                                    listaImpuesto.add(impuesto);
//                                }
//                                geItemsExist.get().setImpuestos(listaImpuesto);
//                                geItemsRepository.save(geItemsExist.get());
                        }
                    }
                });
        response.setDetallesErrores(detallesErrores);
        if (detallesErrores.size() > 0) {
            response.setRespuesta("Se encontraron errores");
        } else {
            response.setRespuesta("Exitoso");
        }
        return response;
    }

    public GeItemGetListDto update(Long idData, Long idEmpresa, UUID id, GeItemRequestDto request) {

        ValidarCampoAscii.validarStrings(request);

        GeItemEntity entidad = geItemsRepository.findByIdItem(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        GeItemEntity update = getItemBuilder.builderUpdateEntity(request, entidad);

        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());

        geItemsRepository.save(update);
        return getItemBuilder.builderListResponse(update);
    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        GeItemEntity entidad = geItemsRepository.findByIdItem(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));


        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        geItemsRepository.save(entidad);

    }

    public GeItemGetOneDto findById(Long idData, Long idEmpresa, UUID id) {

        GeItemEntity entidad = geItemsRepository.findByIdItem(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Item con id {0} no existe", id)));
        GeItemGetOneDto model = getItemBuilder.builderResponse(entidad);
        model.setMedidas(getMedidas(entidad, idData));
        return model;
    }

    private List<GeMedidasResponseDto> getMedidas(GeItemEntity entidad, Long idData) {
        return entidad.getMedidas()
                .stream()
                .map(item -> {
                    GeItemsMedidasEntity medida = geItemsMedidasRepository.findById(idData, item.getIdUnidadMedida());
                    if (Objects.nonNull(medida)) {
                        return getItemBuilder.builderResponseMedidas(medida, item.getFactor());
                    } else {
                        return null;
                    }
                }).toList();
    }

    public PaginatedDto<GeItemGetListDto> findAllPaginate(Long idData, Long idEmpresa, GeItemListFilterDto filters, Pageable pageable) {

        Page<GeItemEntity> page = geItemsRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto items = new PaginatedDto<GeItemGetListDto>();

        List<GeItemGetListDto> dtoList = page.stream().map(item -> {
            GeItemGetListDto model = getItemBuilder.builderListResponse(item);
            model.setMedidas(getMedidas(item, idData));
            return model;
        }).toList();

        items.setContent(dtoList);


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

        items.setPaginator(paginated);

        return items;

    }
}
