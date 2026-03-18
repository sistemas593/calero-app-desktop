package com.calero.lili.core.modCargarExcel.services;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaRepository;
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
public class ExcelCargarCategoriasServiceImpl {


    private final DetalleErrorBuilder detalleErrorBuilder;
    private final GeItemsCategoriaRepository geItemsCategoriaRepository;


    public void cargarCategoria(Long idData, MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<GeItemsCategoriaEntity> listItems = new ArrayList<>();

        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                GeItemsCategoriaEntity item = new GeItemsCategoriaEntity();
                item.setIdCategoria(UUID.randomUUID());
                item.setIdData(idData);

                if (Objects.isNull(row.getCell(1))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_CATEGORIA));

                } else if (Objects.nonNull(geItemsCategoriaRepository.findByName(idData,
                        row.getCell(1).getStringCellValue()))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CATEGORIA_IS_PRESENT));
                }

                item.setCategoria(row.getCell(1).getStringCellValue());

                if (Objects.isNull(row.getCell(0))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_NIVEL_CATEGORIA));
                } else {
                    item.setNivel(row.getCell(0).getStringCellValue());
                }
                listItems.add(item);
            }
        }

        if (detalleErrores.isEmpty()) {
            geItemsCategoriaRepository.saveAll(listItems);
        } else {
            throwErrors(detalleErrores);
        }

    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription())
                .toList();
        throw new ListErrorException(list);
    }


}
