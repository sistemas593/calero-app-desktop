package com.calero.lili.core.modComprasItems;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.errors.DetallesErrores;
import com.calero.lili.core.dtos.errors.ListCreationResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesEntity;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesRepository;
import com.calero.lili.core.modComprasItems.builder.GetItemBuilder;
import com.calero.lili.core.modComprasItems.dto.GeItemGetListDto;
import com.calero.lili.core.modComprasItems.dto.GeItemGetOneDto;
import com.calero.lili.core.modComprasItems.dto.GeItemListFilterDto;
import com.calero.lili.core.modComprasItems.dto.GeItemRequestDto;
import com.calero.lili.core.modComprasItems.dto.GeItemRequestListDto;
import com.calero.lili.core.modComprasItems.dto.GeMedidasItemsDto;
import com.calero.lili.core.modComprasItems.dto.GeMedidasResponseDto;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaRepository;
import com.calero.lili.core.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.core.modComprasItemsGrupos.GeItemsGruposRepository;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsMarcas.GeItemsMarcasEntity;
import com.calero.lili.core.modComprasItemsMarcas.GeItemsMarcasRepository;
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasEntity;
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasRepository;
import com.calero.lili.core.utils.validaciones.ValidarCampoAscii;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    private static final BigDecimal TARIFA_GENERAL_IVA = BigDecimal.valueOf(15);

    private final GeItemsRepository geItemsRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final GetItemBuilder getItemBuilder;
    private final GeItemsMedidasRepository geItemsMedidasRepository;
    private final GeItemsMarcasRepository geItemsMarcasRepository;
    private final GeItemsGruposRepository geItemsGruposRepository;
    private final GeItemsCategoriaRepository geItemsCategoriaRepository;
    private final AdIvaPorcentajesRepository adIvaPorcentajesRepository;


    @Transactional
    public GeItemGetListDto create(Long idData, Long idEmpresa, GeItemRequestDto request, String usuario) {

        validateDetalleAdicionalSize(request);
        ValidarCampoAscii.validarStrings(request);
        adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {1} no existe", idData, idEmpresa)));

        Optional<GeItemEntity> geItemsEntity = geItemsRepository.findByCodigoItem(idData, idEmpresa, request.getCodigoPrincipal());
        if (!geItemsEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("El item con codigo {0} ya existe", request.getCodigoPrincipal()));
        }


        GeItemEntity entity = getItemBuilder.builderEntity(request, idData, idEmpresa);
        validacionGrupoMarcaCategoria(request, entity);
        validarMedidas(request, entity);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        return getItemBuilder.builderListResponse(geItemsRepository.save(entity));

    }


    public ListCreationResponseDto createListItems(Long idData, Long idEmpresa, GeItemRequestListDto request, String usuario) {

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
                    validateDetalleAdicionalSize(requestDto);
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
                            geItemsNew.setCreatedBy(usuario);
                            geItemsNew.setCreatedDate(LocalDateTime.now());

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

    @Transactional
    public GeItemGetListDto update(Long idData, Long idEmpresa, UUID id, GeItemRequestDto request, String usuario) {

        validateDetalleAdicionalSize(request);
        ValidarCampoAscii.validarStrings(request);

        GeItemEntity entidad = geItemsRepository.findByIdItem(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        GeItemEntity update = getItemBuilder.builderUpdateEntity(request, entidad);
        validacionGrupoMarcaCategoria(request, update);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        geItemsRepository.save(update);
        return getItemBuilder.builderListResponse(update);
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {

        GeItemEntity entidad = geItemsRepository.findByIdItem(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));


        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        geItemsRepository.save(entidad);

    }

    @Transactional(readOnly = true)
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
                    Optional<GeItemsMedidasEntity> medida = geItemsMedidasRepository.findById(idData, item.getIdUnidadMedida());
                    return medida.map(geItemsMedidasEntity ->
                            getItemBuilder.builderResponseMedidas(geItemsMedidasEntity, item.getFactor())).orElse(null);
                }).toList();
    }

    @Transactional(readOnly = true)
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

    private void validateDetalleAdicionalSize(GeItemRequestDto request) {

        if (Objects.nonNull(request.getDetallesAdicionales())) {
            if (request.getDetallesAdicionales().size() > 3) {
                throw new GeneralException("No se pueden agregar mas de 3 detalles adicionales");
            }
        }
    }


    private void validacionGrupoMarcaCategoria(GeItemRequestDto request, GeItemEntity entidad) {

        if (Objects.nonNull(request.getIdGrupo())) {
            GeItemGrupoEntity grupo = geItemsGruposRepository.findByIdGrupo(entidad.getIdData(),
                            entidad.getIdEmpresa(), request.getIdGrupo())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Grupo con id {0} no existe", request.getIdGrupo())));
            entidad.setGrupos(grupo);
        } else {
            entidad.setGrupos(null);
        }


        if (Objects.nonNull(request.getIdMarca())) {
            GeItemsMarcasEntity marca = geItemsMarcasRepository.findById(entidad.getIdData(), request.getIdMarca())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Marca con id {0} no existe", request.getIdMarca())));
            entidad.setMarcas(marca);
        } else {
            entidad.setMarcas(null);
        }

        if (Objects.nonNull(request.getIdCategoria())) {
            GeItemsCategoriaEntity categoria = geItemsCategoriaRepository.findById(entidad.getIdData(), request.getIdCategoria())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Categoria con id {0} no existe", request.getIdCategoria())));
            entidad.setCategorias(categoria);
        } else {
            entidad.setCategorias(null);
        }
    }

    private void validarMedidas(GeItemRequestDto request, GeItemEntity entity) {
        if (Objects.nonNull(request.getMedidas())) {
            List<GeMedidasItemsEntity> listaMedidas = new ArrayList<>();
            for (GeMedidasItemsDto medida : request.getMedidas()) {
                GeItemsMedidasEntity medidaEntity = geItemsMedidasRepository.findById(entity.getIdData(), medida.getIdMedida())
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("Unidad de medida con id {0} no existe", medida.getIdMedida())));

                GeMedidasItemsEntity itemMedida = new GeMedidasItemsEntity();
                itemMedida.setIdItemMedida(UUID.randomUUID());
                itemMedida.setIdUnidadMedida(medidaEntity.getIdUnidadMedida());
                itemMedida.setFactor(medida.getFactor());
                listaMedidas.add(itemMedida);
            }
            entity.setMedidas(listaMedidas);
        } else {
            entity.setMedidas(null);
        }
    }

    // Igual que findById, pero con la tarifa de IVA del item ajustada segun la fecha enviada.
    // Reglas:
    // - Si la tarifa del item no es 15%, el dto se devuelve tal cual (0%, 5%, exento, etc., sin cambios).
    // - Si es 15% y el item no aplica tarifa reducida, el dto se devuelve tal cual (queda en 15%).
    // - Si es 15% y el item aplica tarifa reducida, se busca la tarifa vigente para la fecha enviada:
    //   - Si la tarifa reducida no aplica para esa fecha (no hay registro vigente, o no tiene tarifa
    //     reducida configurada), el dto se devuelve tal cual (queda en 15%).
    //   - Si la tarifa reducida si aplica para esa fecha, se reemplaza la tarifa del impuesto en el dto
    //     por el valor de esa tarifa reducida.
    @Transactional(readOnly = true)
    public GeItemGetOneDto obtenerTarifaIvaVigente(Long idData, Long idEmpresa, UUID idItem, GeItemListFilterDto filters) {

        GeItemEntity entidad = geItemsRepository.findByIdItem(idData, idEmpresa, idItem)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Item con id {0} no existe", idItem)));

        GeItemGetOneDto model = getItemBuilder.builderResponse(entidad);
        model.setMedidas(getMedidas(entidad, idData));

        BigDecimal tarifaItem = obtenerTarifaIvaItem(entidad);

        boolean esTarifaGeneralConReduccion = Objects.nonNull(tarifaItem)
                && tarifaItem.compareTo(TARIFA_GENERAL_IVA) == 0
                && Boolean.TRUE.equals(entidad.getAplicaTarifaReducida());

        if (!esTarifaGeneralConReduccion || Objects.isNull(model.getImpuestos()) || model.getImpuestos().isEmpty()) {
            return model;
        }

        Optional<AdIvaPorcentajesEntity> vigente = adIvaPorcentajesRepository.findVigente(filters.getFechaTarifaIva());

        boolean tarifaReducidaAplicaFecha = vigente.isPresent()
                && Objects.nonNull(vigente.get().getTarifaReducida())
                && vigente.get().getTarifaReducida() != 0;

        if (tarifaReducidaAplicaFecha) {
            model.getImpuestos().get(0).setTarifa(BigDecimal.valueOf(vigente.get().getTarifaReducida()));
        }

        return model;
    }

    // Toma la tarifa de IVA del item. Asume el primer impuesto asociado al item como su IVA
    // (a ajustar si un item puede tener mas de un impuesto asociado y hay que distinguir cual es el IVA).
    private BigDecimal obtenerTarifaIvaItem(GeItemEntity item) {
        if (Objects.isNull(item.getImpuestos()) || item.getImpuestos().isEmpty()) {
            return null;
        }
        GeImpuestosEntity impuesto = item.getImpuestos().get(0);
        return impuesto.getTarifa();
    }
}
