package com.calero.lili.api.modContabilidad.services;

import com.calero.lili.api.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoAsiento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesEntity;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosDetalleEntity;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosRepository;
import com.calero.lili.api.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelCargarAsientosServiceImpl {


    private final CnAsientosRepository cnAsientosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;


    public void cargarAsientos(Long idData, Long idEmpresa, MultipartFile file, FilterListDto request, String usuario) throws IOException {


        List<DetalleError> detalleErrores = new ArrayList<>();
        List<CnAsientosEntity> listItems = new ArrayList<>();

        obtenerAsientos(idData, idEmpresa, request, detalleErrores);

        AdEmpresasSucursalesEntity sucursales = adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, request.getSucursal())
                .orElseThrow(() -> new GeneralException("No existe sucursal"));

        List<CnPlanCuentaEntity> listaCuentas = cnPlanCuentasRepository.findByListCuentas(idData,
                idEmpresa, leerColumnaConTitulo(file, 4).stream().distinct().toList());

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);


        for (Sheet sheet : workbook) {

            boolean isHeader = true;
            for (Row row : sheet) {

                if (isRowEmpty(row)) {
                    continue;
                }

                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                CnAsientosEntity item = new CnAsientosEntity();
                item.setIdAsiento(UUID.randomUUID());
                item.setIdData(idData);
                item.setIdEmpresa(idEmpresa);
                item.setCreatedBy(usuario);
                item.setCreatedDate(LocalDateTime.now());


                item.setSucursal(sucursales.getSucursal());
                item.setTipoAsiento(TipoAsiento.valueOf(row.getCell(0).getStringCellValue()));
                item.setNumeroAsiento(row.getCell(1).getStringCellValue());
                getAsientoContable(row, detalleErrores, linea, item, listaCuentas);

                listItems.add(item);
            }
        }

        List<CnAsientosEntity> normalizado = validacionAsientos(listItems, detalleErrores);
        if (detalleErrores.isEmpty()) {
            cnAsientosRepository.saveAll(normalizado);
        } else {
            throwErrors(detalleErrores);
        }


    }

    private List<CnAsientosEntity> validacionAsientos(List<CnAsientosEntity> listItems, List<DetalleError> detalleErrores) {

        Map<AsientoKey, List<CnAsientosEntity>> asientosAgrupados =
                listItems.stream()
                        .collect(Collectors.groupingBy(
                                a -> new AsientoKey(a.getNumeroAsiento(), a.getTipoAsiento(), a.getFechaAsiento())
                        ));

        List<CnAsientosEntity> normalizado = normalizarAsiento(asientosAgrupados);
        validarDebeyHaber(normalizado, detalleErrores);
        return normalizado;
    }

    private List<CnAsientosEntity> normalizarAsiento(Map<AsientoKey, List<CnAsientosEntity>> asientosAgrupados) {
        List<CnAsientosEntity> asientosFinales = new ArrayList<>();

        for (List<CnAsientosEntity> grupo : asientosAgrupados.values()) {

            CnAsientosEntity asiento = grupo.get(0);

            List<CnAsientosDetalleEntity> detallesUnificados =
                    grupo.stream()
                            .flatMap(a -> a.getDetalleEntity().stream())
                            .toList();

            asiento.setDetalleEntity(detallesUnificados);
            asientosFinales.add(asiento);
        }

        return asientosFinales;
    }


    private void validarDebeyHaber(List<CnAsientosEntity> asientosAgrupados,
                                   List<DetalleError> detalleErrores) {

        for (CnAsientosEntity asientosEntity : asientosAgrupados) {

            BigDecimal totalDebe = BigDecimal.ZERO;
            BigDecimal totalHaber = BigDecimal.ZERO;

            for (CnAsientosDetalleEntity detalle : asientosEntity.getDetalleEntity()) {
                if (detalle.getDebe() != null) {
                    totalDebe = totalDebe.add(detalle.getDebe());
                }
                if (detalle.getHaber() != null) {
                    totalHaber = totalHaber.add(detalle.getHaber());
                }
            }

            if (totalDebe.compareTo(totalHaber) != 0) {
                detalleErrores.add(
                        new DetalleError(
                                1,
                                EnumError.DEBE_HABER_NO_CUADRAN,
                                "Número de asiento: " + asientosEntity.getNumeroAsiento() +
                                        " Tipo asiento: " + asientosEntity.getTipoAsiento() +
                                        " Total Debe: " + totalDebe +
                                        " Total Haber: " + totalHaber
                        )
                );
            }
        }
    }


    private record AsientoKey(String numeroAsiento, TipoAsiento tipoAsiento, LocalDate fechaAsiento) {
    }


    private void getAsientoContable(Row row, List<DetalleError> detalleErrores, int linea, CnAsientosEntity item,
                                    List<CnPlanCuentaEntity> listCuentas) {

        if (Objects.isNull(row.getCell(2))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FECHA_ASIENTO_NOT_FOUND));
        } else {
            Date date = row.getCell(2).getDateCellValue();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            item.setFechaAsiento(localDate);
        }

        if (Objects.isNull(row.getCell(5))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CONCEPTO_ASIENTO_NOT_FOUND));
        } else {
            item.setConcepto(row.getCell(5).getStringCellValue());
        }

        getDetalleAsientoContable(row, detalleErrores, linea, item, listCuentas);

    }

    private void getDetalleAsientoContable(Row row, List<DetalleError> detalleErrores,
                                           int linea, CnAsientosEntity item,
                                           List<CnPlanCuentaEntity> listCuentas) {


        List<CnAsientosDetalleEntity> detalles = new ArrayList<>();

        CnAsientosDetalleEntity cnAsientosDetalle = new CnAsientosDetalleEntity();
        cnAsientosDetalle.setIdAsientoDetalle(UUID.randomUUID());

        cnAsientosDetalle.setIdData(item.getIdData());
        cnAsientosDetalle.setIdEmpresa(item.getIdEmpresa());

        if (Objects.isNull(row.getCell(3))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.ITEM_ASIENTO_NOT_FOUND));
        } else {
            cnAsientosDetalle.setItemOrden(Integer.parseInt(row.getCell(3).getStringCellValue()));
        }

        if (Objects.isNull(row.getCell(6))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_DOCUMENTO_ASIENTO_NOT_FOUND));
        } else {
            cnAsientosDetalle.setTipoDocumento(row.getCell(6).getStringCellValue());
        }

        if (Objects.isNull(row.getCell(7))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NUMERO_DOCUMENTO_ASIENTO_NOT_FOUND));
        } else {
            cnAsientosDetalle.setNumeroDocumento(row.getCell(7).getStringCellValue());
        }

        if (Objects.isNull(row.getCell(8))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FECHA_DOCUMENTO_ASIENTO_NOT_FOUND));
        } else {
            Date date = row.getCell(8).getDateCellValue();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            cnAsientosDetalle.setFechaDocumento(localDate);

        }

        if (Objects.isNull(row.getCell(9))) {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.DETALLE_ASIENTO_NOT_FOUND));
        } else {
            cnAsientosDetalle.setDetalle(row.getCell(9).getStringCellValue());
        }

        setearDebeHaber(row, detalleErrores, linea, cnAsientosDetalle);


        addPlanCuenta(row, detalleErrores, linea, cnAsientosDetalle, item, listCuentas);
        detalles.add(cnAsientosDetalle);
        item.setDetalleEntity(detalles);

    }

    private void setearDebeHaber(Row row, List<DetalleError> detalleErrores,
                                 int linea, CnAsientosDetalleEntity cnAsientosDetalle) {


        if (Objects.nonNull(row.getCell(10)) && Objects.nonNull(row.getCell(11))) {

            String debe = row.getCell(10).getStringCellValue();
            if (debe.contains(",")) {
                debe = debe.replace(",", ".");
            }

            BigDecimal valorDebe = new BigDecimal(debe);


            String haber = row.getCell(11).getStringCellValue();
            if (haber.contains(",")) {
                haber = haber.replace(",", ".");
            }

            BigDecimal valorHaber = new BigDecimal(haber);

            cnAsientosDetalle.setDebe(valorDebe);
            cnAsientosDetalle.setHaber(valorHaber);


        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.DEBE_HABER_ASIENTO_NOT_FOUND));
        }

    }

    private void addPlanCuenta(Row row, List<DetalleError> detalleErrores, int linea,
                               CnAsientosDetalleEntity cnAsientosDetalle,
                               CnAsientosEntity item, List<CnPlanCuentaEntity> listCuentas) {

        CnPlanCuentaEntity planCuenta = listCuentas.stream()
                .filter(cnPlanCuentaEntity -> cnPlanCuentaEntity.getCodigoCuenta()
                        .equals(row.getCell(4).getStringCellValue()))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(planCuenta)) {
            cnAsientosDetalle.setCuenta(planCuenta);
        } else {
            DetalleError detalleError = new DetalleError(linea, EnumError.PLAN_CUENTA_NOT_FOUND,
                    "Codigo cuenta: " + row.getCell(4).getStringCellValue());
            detalleErrores.add(detalleError);
        }

    }


    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                .toList();
        throw new ListErrorException(list);
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


    public List<String> leerColumnaConTitulo(MultipartFile file, Integer columna) throws IOException {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            return StreamSupport.stream(sheet.spliterator(), false)
                    .skip(1) // salta el encabezado
                    .map(row -> formatter.formatCellValue(row.getCell(columna))) // columna 4
                    .filter(valor -> !valor.isBlank())
                    .toList();
        }
    }

    private Map<String, Set<String>> leerDosColumnasAgrupadas(MultipartFile file) throws IOException {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            return StreamSupport.stream(sheet.spliterator(), false)
                    .skip(1) // encabezado
                    .map(row -> Map.entry(
                            formatter.formatCellValue(row.getCell(0)),
                            formatter.formatCellValue(row.getCell(1))
                    ))
                    .filter(e -> !e.getKey().isBlank() && !e.getValue().isBlank())
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,                       // GROUP BY
                            Collectors.mapping(
                                    Map.Entry::getValue,
                                    Collectors.toSet()               // DISTINCT
                            )
                    ));
        }


    }


    public void obtenerAsientos(Long idData, Long idEmpresa, FilterListDto model, List<DetalleError> detalleErrores) {

        Long totalAsientos = cnAsientosRepository.countByEmpresaAndFechaBetween(idData, idEmpresa,
                model.getFechaEmisionDesde(), model.getFechaEmisionHasta());

        if (totalAsientos != 0) {
            detalleErrores.add(new DetalleError(0,
                    EnumError.ASIENTO_IS_PRESENT,
                    "Fecha Inicial: " + model.getFechaEmisionDesde() +
                            " Fecha Final: " + model.getFechaEmisionHasta()
            ));
            throwErrors(detalleErrores);
        }


    }


}
