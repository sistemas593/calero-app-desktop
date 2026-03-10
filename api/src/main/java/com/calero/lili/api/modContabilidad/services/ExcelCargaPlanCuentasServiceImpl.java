package com.calero.lili.api.modContabilidad.services;

import com.calero.lili.api.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
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
public class ExcelCargaPlanCuentasServiceImpl {

    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final CpPlanCuentasConfiguracionPadresImpl cpPlanCuentasConfiguracionService;

    public void cargarPlanDeCuentas(Long idData, MultipartFile file, Long idEmpresa) throws IOException {

// BUSCAR TODAS LAS CUENTAS QUE EXISTAN DE LA EMPRESA (CONTEO DE CUENTAS)
        // SI QUE EXISTEN LAS CUENTAS, SI EXISTE AL MENOS UNA NO SE REALIZA EL PROCESO, Y SE ENVIA UN ERROR
        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<CnPlanCuentaEntity> listItems = new ArrayList<>();

        Long totalCuentas = cnPlanCuentasRepository.countByIdDataAndIdEmpresa(idData, idEmpresa);
        if (totalCuentas != 0) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(0, EnumError.CUENTA_IS_PRESENT));
            throwErrors(detalleErrores);
        }

        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                CnPlanCuentaEntity item = new CnPlanCuentaEntity();
                item.setIdCuenta(UUID.randomUUID());
                item.setIdData(idData);
                item.setIdEmpresa(idEmpresa);

                if (Objects.isNull(row.getCell(0))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_CUENTA));
                } else {
                    String codigoCuenta = row.getCell(0).getStringCellValue().replace(".", "");
                    item.setCodigoCuentaOriginal(row.getCell(0).getStringCellValue());
                    item.setCodigoCuenta(codigoCuenta);
                    item.setGrupo(validarNumeroGrupo(codigoCuenta));
                    item.setMayor(row.getCell(0).getStringCellValue().endsWith("."));
                    item.setNivel(getNivel(row.getCell(0).getStringCellValue(), item.getMayor()));
                }
                getNombreCuenta(row, detalleErrores, linea, item);
                listItems.add(item);
            }
        }

        if (detalleErrores.isEmpty()) {
            cpPlanCuentasConfiguracionService.asignarPadres(listItems);
            cnPlanCuentasRepository.saveAll(listItems);

        } else {
            throwErrors(detalleErrores);
        }
    }

    private void getNombreCuenta(Row row, List<DetalleError> detalleErrores, int linea, CnPlanCuentaEntity item) {
        if (Objects.isNull(row.getCell(1))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NOT_FOUND_NOMBRE_CUENTA));
        } else {
            item.setCuenta(row.getCell(1).getStringCellValue());
        }
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription())
                .toList();
        throw new ListErrorException(list);
    }

    private Integer getNivel(String cuentaOriginal, Boolean mayor) {
        int nivel = cuentaOriginal.length() - cuentaOriginal.replace(".", "").length();
        if (!mayor) {
            return nivel + 1;
        }
        return nivel;
    }

    private Integer validarNumeroGrupo(String grupo) {
        return Integer.parseInt(grupo.substring(0, 1));
    }


}
