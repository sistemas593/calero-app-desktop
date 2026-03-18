package com.calero.lili.core.modCargarExcel.services;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.enums.TipoItemEnum;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modComprasItems.GeImpuestosEntity;
import com.calero.lili.core.modComprasItems.GeImpuestosRepository;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.core.modComprasItemsGrupos.GeItemsGruposRepository;
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
public class ExcelCargaServiciosServiceImpl {

    private final DetalleErrorBuilder detalleErrorBuilder;
    private final GeItemsRepository getItemRepository;
    private final GeItemsGruposRepository geItemsGruposRepository;
    private final GeImpuestosRepository geImpuestosRepository;


    public void cargarItemsServicios(Long idData, MultipartFile file, Long idEmpresa) throws IOException {


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
                item.setCodigoPrincipal(row.getCell(0).getStringCellValue());
                getGrupoServicios(idEmpresa, row, idData, item, detalleErrores, linea);
                getOtherCellsServicios(row, detalleErrores, linea, item);
                getImpuestoServicios(row, detalleErrores, linea, item);
                item.setTipoItem(TipoItemEnum.SER.name());
                listItems.add(item);
            }
        }

        if (detalleErrores.isEmpty()) {
            getItemRepository.saveAll(listItems);
        } else {
            throwErrors(detalleErrores);
        }

    }

    private void getImpuestoServicios(Row row, List<DetalleError> detalleErrores, int linea, GeItemEntity item) {
        GeImpuestosEntity geImpuestosEntity = geImpuestosRepository
                .findByCodigoAndCodigoPorcentaje(row.getCell(3).getStringCellValue(),
                        row.getCell(4).getStringCellValue());

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

    private void getGrupoServicios(Long idEmpresa, Row row, Long idData, GeItemEntity item, List<DetalleError> detalleErrores, int linea) {
        String nombreGrupo = row.getCell(2).getStringCellValue();
        GeItemGrupoEntity geItemGrupoEntity = geItemsGruposRepository
                .findByNameGrupo(idData, idEmpresa, nombreGrupo);
        if (Objects.nonNull(geItemGrupoEntity)) {
            item.setGrupos(geItemGrupoEntity);
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.GRUPO_NOT_FOUND));
        }
    }

    private void getOtherCellsServicios(Row row, List<DetalleError> detalleErrores, int linea, GeItemEntity item) {


        if (Objects.isNull(row.getCell(1).getStringCellValue())) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NAME_ITEM_NOT_FOUND));
        } else {
            item.setDescripcion(row.getCell(1).getStringCellValue());
        }

        detallesAdicionalesServicios(row, item);

    }

    private void detallesAdicionalesServicios(Row row, GeItemEntity item) {

        List<GeItemEntity.DetalleAdicional> listDetalleAdicional = new ArrayList<>();

        if (Objects.nonNull(row.getCell(7))) {
            listDetalleAdicional.add(GeItemEntity.DetalleAdicional.builder()
                    .nombre(row.getCell(7).getStringCellValue())
                    .valor(row.getCell(8).getStringCellValue())
                    .build());
        }

        if (Objects.nonNull(row.getCell(9))) {
            listDetalleAdicional.add(GeItemEntity.DetalleAdicional.builder()
                    .nombre(row.getCell(9).getStringCellValue())
                    .valor(row.getCell(10).getStringCellValue())
                    .build());
        }

        if (Objects.nonNull(row.getCell(11))) {
            listDetalleAdicional.add(GeItemEntity.DetalleAdicional.builder()
                    .nombre(row.getCell(11).getStringCellValue())
                    .valor(row.getCell(12).getStringCellValue())
                    .build());
        }
        item.setDetallesAdicionales(!listDetalleAdicional.isEmpty() ? listDetalleAdicional : null);
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription())
                .toList();
        throw new ListErrorException(list);
    }

}
