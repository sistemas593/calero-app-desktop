package com.calero.lili.core.modCargarExcel.services;

import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosItemsRepository;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modComprasItems.GeMedidasItemsEntity;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaRepository;
import com.calero.lili.core.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.core.modComprasItemsGrupos.GeItemsGruposRepository;
import com.calero.lili.core.modComprasItemsMarcas.GeItemsMarcasEntity;
import com.calero.lili.core.modComprasItemsMarcas.GeItemsMarcasRepository;
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasEntity;
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasRepository;
import com.calero.lili.core.utils.validaciones.ValidarCampoAscii;
import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.enums.TipoItemEnum;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelCargaItemsServiceImpl {

    private final DetalleErrorBuilder detalleErrorBuilder;
    private final GeItemsRepository getItemRepository;
    private final GeItemsGruposRepository geItemsGruposRepository;
    private final GeImpuestosItemsRepository geImpuestosItemsRepository;
    private final GeItemsMarcasRepository geItemsMarcasRepository;
    private final GeItemsCategoriaRepository geItemsCategoriaRepository;
    private final GeItemsMedidasRepository geItemsMedidasRepository;


    public void cargarItemsProductos(Long idData, MultipartFile file, Long idEmpresa) throws IOException {


        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<GeItemEntity> listItems = new ArrayList<>();

        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                GeItemEntity item = new GeItemEntity();
                item.setIdItem(UUID.randomUUID());
                item.setIdData(idData);
                item.setIdEmpresa(idEmpresa);

                if (Objects.isNull(row.getCell(0))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CODE_NOT_FOUND));
                } else if (getItemRepository.findByCodigoItem(idData, idEmpresa,
                        row.getCell(0).getStringCellValue()).isPresent()) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CODE_IS_PRESENT));
                }

                if (ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(0).getStringCellValue())) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CODIGO_PRINCIPAL_NOT_VALID_CARACTER));
                } else {
                    item.setCodigoPrincipal(row.getCell(0).getStringCellValue());
                }


                getGrupoEntityProductos(idEmpresa, row, idData, item, detalleErrores, linea);
                getOtherCellsProductos(row, detalleErrores, linea, item);
                getImpuestoProductos(row, detalleErrores, linea, item);
                getMedidaProducto(row, idData, item, detalleErrores, linea);
                getCategoriaProducto(row, idData, item, detalleErrores, linea);
                getMarcaProductos(row, idData, item, detalleErrores, linea);
                item.setTipoItem(TipoItemEnum.PR0.name());
                listItems.add(item);
            }
        }

        if (detalleErrores.isEmpty()) {
            getItemRepository.saveAll(listItems);
        } else {
            throwErrors(detalleErrores);
        }

    }


    private void getImpuestoProductos(Row row, List<DetalleError> detalleErrores, int linea, GeItemEntity item) {
        GeImpuestosEntity geImpuestosEntity = geImpuestosItemsRepository
                .findByCodigoAndCodigoPorcentaje(row.getCell(5).getStringCellValue(),
                        row.getCell(6).getStringCellValue());

        if (Objects.isNull(geImpuestosEntity)) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_IMPUESTO));
        } else {
            List<GeImpuestosEntity> listImpuesto = new ArrayList<>();
            listImpuesto.add(GeImpuestosEntity.builder()
                    .idImpuesto(geImpuestosEntity.getIdImpuesto())
                    .build());
            item.setImpuestos(listImpuesto);
        }
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription())
                .toList();
        throw new ListErrorException(list);
    }

    private void getGrupoEntityProductos(Long idEmpresa, Row row, Long idData, GeItemEntity item, List<DetalleError> detalleErrores, int linea) {
        String nombreGrupo = row.getCell(3).getStringCellValue();
        GeItemGrupoEntity geItemGrupoEntity = geItemsGruposRepository
                .findByNameGrupo(idData, idEmpresa, nombreGrupo);
        if (Objects.nonNull(geItemGrupoEntity)) {
            item.setGrupos(geItemGrupoEntity);
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.GRUPO_NOT_FOUND));
        }
    }

    private void getOtherCellsProductos(Row row, List<DetalleError> detalleErrores, int linea, GeItemEntity item) {


        if (Objects.isNull(row.getCell(1).getStringCellValue())) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CODIGO_BARRAS_NOT_FOUND));
        } else {
            if (ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(1).getStringCellValue())) {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CODIGO_BARRAS_NOT_VALID_CARACTER));
            } else {
                item.setCodigoBarras(row.getCell(1).getStringCellValue());
            }

        }

        if (Objects.isNull(row.getCell(2).getStringCellValue())) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NAME_ITEM_NOT_FOUND));
        } else {
            if (ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(2).getStringCellValue())) {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOMBRE_NOT_VALID_CARACTER));
            } else {
                item.setDescripcion(row.getCell(2).getStringCellValue());
            }

        }

        if (Objects.isNull(row.getCell(4).getStringCellValue())) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.ORDENADOR_NOT_FOUND));
        } else {
            item.setOrdenador(Long.parseLong(row.getCell(4).getStringCellValue()));
        }

        detallesAdicionalesProductos(row, item, detalleErrores);

    }

    private void detallesAdicionalesProductos(Row row, GeItemEntity item, List<DetalleError> detalleErrores) {

        List<GeItemEntity.DetalleAdicional> listDetalleAdicional = new ArrayList<>();

        if (Objects.nonNull(row.getCell(10))) {

            if (ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(10).getStringCellValue())
                    && ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(11).getStringCellValue())) {

                detalleErrores.add(detalleErrorBuilder.builderDetalleError(0, EnumError.DETALLE_NOT_VALID_CARACTER));

            } else {
                listDetalleAdicional.add(GeItemEntity.DetalleAdicional.builder()
                        .nombre(row.getCell(10).getStringCellValue())
                        .valor(row.getCell(11).getStringCellValue())
                        .build());
            }


        }


        if (Objects.nonNull(row.getCell(12))) {

            if (ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(12).getStringCellValue())
                    && ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(13).getStringCellValue())) {

                detalleErrores.add(detalleErrorBuilder.builderDetalleError(0, EnumError.DETALLE_NOT_VALID_CARACTER));

            } else {
                listDetalleAdicional.add(GeItemEntity.DetalleAdicional.builder()
                        .nombre(row.getCell(12).getStringCellValue())
                        .valor(row.getCell(13).getStringCellValue())
                        .build());
            }


        }


        if (Objects.nonNull(row.getCell(14))) {

            if (ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(14).getStringCellValue())
                    && ValidarCampoAscii.contieneAsciiNoPermitido(row.getCell(15).getStringCellValue())) {

                detalleErrores.add(detalleErrorBuilder.builderDetalleError(0, EnumError.DETALLE_NOT_VALID_CARACTER));

            } else {
                listDetalleAdicional.add(GeItemEntity.DetalleAdicional.builder()
                        .nombre(row.getCell(14).getStringCellValue())
                        .valor(row.getCell(15).getStringCellValue())
                        .build());
            }
        }
        item.setDetallesAdicionales(!listDetalleAdicional.isEmpty() ? listDetalleAdicional : null);
    }


    private void getMarcaProductos(Row row, Long idData, GeItemEntity item, List<DetalleError> detalleErrores, int linea) {
        String nombreMarca = row.getCell(7).getStringCellValue();
        GeItemsMarcasEntity geItemsMarcasEntity = geItemsMarcasRepository
                .findByName(idData, nombreMarca);

        if (Objects.nonNull(geItemsMarcasEntity)) {
            item.setMarcas(geItemsMarcasEntity);
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_MARCAS));
        }
    }


    private void getCategoriaProducto(Row row, Long idData, GeItemEntity item, List<DetalleError> detalleErrores, int linea) {
        String nombreCategoria = row.getCell(9).getStringCellValue();
        GeItemsCategoriaEntity categoria = geItemsCategoriaRepository.findByName(idData, nombreCategoria);
        if (Objects.nonNull(categoria)) {
            item.setCategorias(categoria);
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_MEDIDA));
        }
    }

    private void getMedidaProducto(Row row, Long idData, GeItemEntity item, List<DetalleError> detalleErrores, int linea) {
        String nombreMedida = row.getCell(8).getStringCellValue();
        GeItemsMedidasEntity medidas = geItemsMedidasRepository.findByName(idData, nombreMedida);
        if (Objects.nonNull(medidas)) {
            List<GeMedidasItemsEntity> list = new ArrayList<>();
            list.add(GeMedidasItemsEntity.builder()
                    .idItemMedida(UUID.randomUUID())
                    .idUnidadMedida(medidas.getIdUnidadMedida())
                    .build());
            item.setMedidas(list);
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_MARCAS));
        }
    }
}




