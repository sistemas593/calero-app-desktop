package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReporteDatosCrediticiosExcelServiceImpl {

    private final ReporteDatosCrediticiosRepository reporteDatosCrediticiosRepository;
    private final GeTercerosRepository geTercerosRepository;

    public void cargarDatosCrediticios(Long idData, Long idEmpresa,
                                       MultipartFile file, String usuario, String sucursal ) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        // Primer paso: recolectar códigos únicos de los clientes
        Set<String> codigosUnicos = new LinkedHashSet<>();
        boolean isHeader = true;
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (isRowEmpty(row)) continue;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                var cellCodigo = row.getCell(0);
                if (cellCodigo != null) {
                    String codigo = cellCodigo.getStringCellValue();
                    if (codigo != null && !codigo.isBlank()) {
                        codigosUnicos.add(codigo);
                    }
                }
            }
        }

        List<String> codigos = new ArrayList<>(codigosUnicos);
        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllCodigosTercero(idData, codigos)
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getCodigoTercero, Function.identity()));

        // Segundo paso: procesar filas con los datos de la consulta
        Workbook workbook2 = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(file.getInputStream());

        isHeader = true;
        for (Sheet sheet : workbook2) {
            for (Row row : sheet) {
                if (isRowEmpty(row)) continue;

                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }



            }
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            if (row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                return false;
            }
        }
        return true;
    }

}
