package com.calero.lili.api.modCargarExcel.services;

import com.calero.lili.api.modComprasItemsMarcas.GeItemsMarcasEntity;
import com.calero.lili.api.modComprasItemsMarcas.GeItemsMarcasRepository;
import com.calero.lili.api.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ExcelCargaMarcasServiceImpl {

    private final DetalleErrorBuilder detalleErrorBuilder;
    private final GeItemsMarcasRepository geItemsMarcasRepository;


    public void cargarMarcas(Long idData, MultipartFile file) throws IOException {


        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<GeItemsMarcasEntity> listItems = new ArrayList<>();

        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                GeItemsMarcasEntity item = new GeItemsMarcasEntity();
                item.setIdMarca(UUID.randomUUID());
                item.setIdData(idData);

                if (Objects.isNull(row.getCell(0))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_MARCAS));
                } else if (Objects.nonNull(geItemsMarcasRepository.findByName(idData,
                        row.getCell(0).getStringCellValue()))) {

                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.MARCAS_IS_PRESENT));
                }
                item.setMarca(row.getCell(0).getStringCellValue());
                listItems.add(item);
            }
        }

        if (detalleErrores.isEmpty()) {
            geItemsMarcasRepository.saveAll(listItems);
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
