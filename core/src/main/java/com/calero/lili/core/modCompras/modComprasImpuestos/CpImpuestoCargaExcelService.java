package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.SustentoCodigos;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesEntity;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.FilterListCompraImpuestoDto;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.ValidarTipoArchivo;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CpImpuestoCargaExcelService {

    private final GeTercerosRepository geTercerosRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleError;
    private final ValidacionGeneralCpImpuestosService service;
    private final CpImpuestosRepository cpImpuestosRepository;


    public void cargarExcelCompraImpuestos(Long idData, Long idEmpresa,
                                           MultipartFile file, String usuario, FilterListCompraImpuestoDto filter) throws IOException {

        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        if (Objects.nonNull(filter.getSucursal())) {
            throw new GeneralException("La sucursal no puede ser nula");
        }

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<CpImpuestosEntity> cpImpuestosEntities = new ArrayList<>();

        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));

        Optional<AdEmpresasSucursalesEntity> sucursalEntity = adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, filter.getSucursal());


        if (sucursalEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("La sucursal {0} no existe ", filter.getSucursal()));
        }

        /*
          Paso único: leer todas las filas en memoria una sola vez, de esta manera se obtiene el codigo de tercero
          en el excel y se manda a buscar en la base de datos
         */

        record FilaExcel(int linea, String[] celdas) {
        }
        List<FilaExcel> filas = new ArrayList<>();
        Set<String> numerosIdentifiacion = new HashSet<>();

        try (InputStream is = file.getInputStream();
             Workbook wb = StreamingReader.builder()
                     .rowCacheSize(500)
                     .bufferSize(65536)
                     .open(is)) {
            for (Sheet sheet : wb) {
                boolean isHeader = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    int lastCell = row.getLastCellNum();
                    String[] celdas = new String[lastCell];
                    for (int i = 0; i < lastCell; i++) {
                        celdas[i] = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : null;
                    }
                    filas.add(new FilaExcel(row.getRowNum() + 1, celdas));

                    if (celdas[0] != null && !celdas[0].isBlank()) {
                        numerosIdentifiacion.add(celdas[0]);
                    }
                }
            }
        }

        /*
          Con los codigos se busca en la base de datos los terceros, y se van almacenando un map, que contiene el codigo
          y la entidad de tercero para posterior asignarse a cada registro en el detalle.
         */

        CpImpuestosEntity cpImpuestos = new CpImpuestosEntity();

        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllNumeroIdentifiacion(idData, new ArrayList<>(numerosIdentifiacion))
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getNumeroIdentificacion, Function.identity()));

        for (FilaExcel fila : filas) {

            cpImpuestos.setIdImpuestos(UUID.randomUUID());
            cpImpuestos.setIdData(idData);
            cpImpuestos.setIdEmpresa(empresa.getIdEmpresa());
            cpImpuestos.setSucursal(filter.getSucursal());
            cpImpuestos.setCreatedBy(usuario);
            cpImpuestos.setCreatedDate(LocalDateTime.now());

            String numeroIdentifiacion = celda(fila.celdas(), 0);

            if (numeroIdentifiacion != null) {
                GeTerceroEntity tercero = mapTercero.get(numeroIdentifiacion);
                cpImpuestos.setTercero(tercero);
                if (tercero == null) {
                    String nombreTercero = celda(fila.celdas(), 1);
                    GeTerceroEntity terceroEntity = new GeTerceroEntity();
                    terceroEntity.setIdTercero(UUID.randomUUID());
                    terceroEntity.setTercero(Objects.nonNull(nombreTercero) ? nombreTercero : null);
                    terceroEntity.setNumeroIdentificacion(numeroIdentifiacion);
                    geTercerosRepository.save(terceroEntity);

                }
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La identificación del tercero no se encuentra");
                detalleErrores.add(detalleError);
            }


            String relacionado = celda(fila.celdas(), 3);
            if (Objects.nonNull(relacionado)) {
                relacionado = relacionado.toLowerCase();
                if (relacionado.equals("verdadero")) {
                    cpImpuestos.setRelacionado("S");
                } else {
                    cpImpuestos.setRelacionado("N");
                }

            } else {
                cpImpuestos.setRelacionado(null);
            }


            String tipoProveedor = celda(fila.celdas(), 4);
            if (Objects.nonNull(tipoProveedor)) {
                cpImpuestos.setTipoProveedor(tipoProveedor);
            } else {
                cpImpuestos.setTipoProveedor(null);
            }

            String tipoContribuyente = celda(fila.celdas(), 5);
            if (Objects.nonNull(tipoContribuyente)) {
                cpImpuestos.setTipoContribuyente(tipoContribuyente);
            } else {
                cpImpuestos.setTipoContribuyente(null);
            }

            String fechaEmision = celda(fila.celdas(), 6);
            if (Objects.nonNull(fechaEmision)) {
                cpImpuestos.setFechaEmision(DateUtils.toLocalDate(fechaEmision));
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de emisión se encuentra");
                detalleErrores.add(detalleError);
            }


            String fechaRegistro = celda(fila.celdas(), 7);
            if (Objects.nonNull(fechaRegistro)) {
                cpImpuestos.setFechaRegistro(DateUtils.toLocalDate(fechaEmision));
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de registro se encuentra");
                detalleErrores.add(detalleError);
            }

            String tipoDocumento = celda(fila.celdas(), 8);
            if (Objects.nonNull(tipoDocumento)) {
                try {
                    DocumentoEnum documentoEnum = DocumentoEnum.getCodigoDocumento(tipoDocumento);
                    cpImpuestos.setDocumento(documentoEnum);
                } catch (Exception exception) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle(exception.getMessage());
                    detalleErrores.add(detalleError);
                }

            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El codigo del documento no se encuentra");
                detalleErrores.add(detalleError);
            }


            String serie = celda(fila.celdas(), 9);
            if (Objects.nonNull(serie)) {
                cpImpuestos.setSerie(serie);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La serie no se encuentra");
                detalleErrores.add(detalleError);
            }

            // TODO PREGUNTAR SOBRE EL SECUENCIAL
            String secuencial = celda(fila.celdas(), 10);
            if (Objects.nonNull(secuencial)) {

                if (secuencial.matches("\\d{9}")) {
                    cpImpuestos.setSecuencial(secuencial);
                } else {
                    cpImpuestos.setSecuencial(String.format("%09d", Integer.parseInt(secuencial)));
                }

            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El secuencial no se encuentra");
                detalleErrores.add(detalleError);
            }


            String numeroAutorizacion = celda(fila.celdas(), 11);
            if (Objects.nonNull(numeroAutorizacion)) {
                cpImpuestos.setNumeroAutorizacion(numeroAutorizacion);
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El número de autorización");
                detalleErrores.add(detalleError);
            }


            String fechaVencimiento = celda(fila.celdas(), 12);
            if (Objects.nonNull(fechaVencimiento)) {
                cpImpuestos.setFechaVencimiento(DateUtils.toLocalDate(fechaVencimiento));
            } else {
                cpImpuestos.setFechaVencimiento(null);
            }

            String codigoSustento = celda(fila.celdas(), 13);
            if (Objects.nonNull(codigoSustento)) {
                try {
                    SustentoCodigos sustentoCodigos = SustentoCodigos.getCodigoDocumento(codigoSustento);
                    cpImpuestos.setCodigoSustento(sustentoCodigos);
                } catch (Exception exception) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle(exception.getMessage());
                    detalleErrores.add(detalleError);
                }

            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de registro se encuentra");
                detalleErrores.add(detalleError);
            }


            String devolucionIva = celda(fila.celdas(), 14);
            if (Objects.nonNull(devolucionIva)) {
                cpImpuestos.setDevolucionIva(devolucionIva);
            } else {
                cpImpuestos.setDevolucionIva(null);
            }

            String concepto = celda(fila.celdas(), 15);
            if (Objects.nonNull(concepto)) {
                cpImpuestos.setConcepto(concepto);
            } else {
                cpImpuestos.setDevolucionIva(null);
            }

            List<CpImpuestosValoresEntity> valores = new ArrayList<>();
            String baseCero = celda(fila.celdas(), 16);

            if (Objects.nonNull(baseCero)) {
                CpImpuestosValoresEntity valoresEntity = new CpImpuestosValoresEntity();
                valoresEntity.setBaseImponible(convetirValor(baseCero));
                valoresEntity.setValor(BigDecimal.ZERO);
                valoresEntity.setTarifa(BigDecimal.ZERO);
                valoresEntity.setCodigoPorcentaje("0");
                valoresEntity.setCodigo("2");
                valores.add(valoresEntity);
            }

            String baseGravada1 = celda(fila.celdas(), 17);

            if (Objects.nonNull(baseGravada1)) {
                CpImpuestosValoresEntity valoresEntity = new CpImpuestosValoresEntity();

                String tarifa = celda(fila.celdas(), 18);

                if (Objects.nonNull(tarifa)) {
                    switch (tarifa) {
                        case "15": {
                            valoresEntity.setCodigoPorcentaje("4");
                            valoresEntity.setCodigo("2");
                            valoresEntity.setTarifa(new BigDecimal("15.00"));
                            valoresEntity.setValor(convetirValor(celda(fila.celdas(), 19)));
                            valoresEntity.setBaseImponible(convetirValor(baseGravada1));
                        }
                        case "8": {
                            valoresEntity.setCodigoPorcentaje("8");
                            valoresEntity.setCodigo("2");
                            valoresEntity.setTarifa(new BigDecimal("8.00"));
                            valoresEntity.setValor(convetirValor(celda(fila.celdas(), 19)));
                            valoresEntity.setBaseImponible(convetirValor(baseGravada1));
                        }

                        case "5": {
                            valoresEntity.setCodigoPorcentaje("5");
                            valoresEntity.setCodigo("2");
                            valoresEntity.setTarifa(new BigDecimal("5.00"));
                            valoresEntity.setValor(convetirValor(celda(fila.celdas(), 19)));
                            valoresEntity.setBaseImponible(convetirValor(baseGravada1));
                        }
                    }
                }
                valores.add(valoresEntity);
            }


            String baseGravada2 = celda(fila.celdas(), 20);

            if (Objects.nonNull(baseGravada2)) {
                CpImpuestosValoresEntity valoresEntity = new CpImpuestosValoresEntity();

                String tarifa = celda(fila.celdas(), 21);

                if (Objects.nonNull(tarifa)) {
                    switch (tarifa) {
                        case "15": {
                            valoresEntity.setCodigoPorcentaje("4");
                            valoresEntity.setCodigo("2");
                            valoresEntity.setTarifa(new BigDecimal("15.00"));
                            valoresEntity.setValor(convetirValor(celda(fila.celdas(), 22)));
                        }
                        case "8": {
                            valoresEntity.setCodigoPorcentaje("8");
                            valoresEntity.setCodigo("2");
                            valoresEntity.setTarifa(new BigDecimal("8.00"));
                            valoresEntity.setValor(convetirValor(celda(fila.celdas(), 22)));
                        }

                        case "5": {
                            valoresEntity.setCodigoPorcentaje("5");
                            valoresEntity.setCodigo("2");
                            valoresEntity.setTarifa(new BigDecimal("5.00"));
                            valoresEntity.setValor(convetirValor(celda(fila.celdas(), 22)));
                        }
                    }
                }
                valores.add(valoresEntity);
            }
            cpImpuestos.setValoresEntity(valores);
            cpImpuestosEntities.add(cpImpuestos);
        }

        if (detalleErrores.isEmpty()) {

            List<CpImpuestoDetalleError> listaErroresValidacion = new ArrayList<>();
            List<DetalleCompras> listaComprobacion = cpImpuestoDetalleError.builderListValidacion(cpImpuestosEntities);

            for (DetalleCompras dto : listaComprobacion) {
                listaErroresValidacion.addAll(service.validacionGeneral(dto));
            }

            if (listaErroresValidacion.isEmpty()) {
                cpImpuestosRepository.saveAll(cpImpuestosEntities);
            } else {
                List<String> list = listaErroresValidacion.stream()
                        .map(CpImpuestoDetalleError::getDetalle)
                        .toList();
                throw new ListErrorException(list);
            }

        } else {
            throwErrors(detalleErrores);
        }

    }

    /**
     * Metodo para obtener la información en formato de String de cada celda del excel, mediante el indíce de la celda
     * en caso de ser vacia, devolvera un null.
     *
     * @param celdas Valor de las celdas del excel
     * @param idx    Indíce de las celdas
     * @return Retorno de un String para saber si la celda esta vacia o si tiene contenido
     */
    private String celda(String[] celdas, int idx) {
        if (idx >= celdas.length) return null;
        String v = celdas[idx];
        return (v != null && !v.isBlank()) ? v : null;
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

    private BigDecimal convetirValor(String valor) {
        valor = valor.trim();
        if (valor.contains(",") && valor.contains(".")) {
            if (valor.lastIndexOf(",") > valor.lastIndexOf(".")) {
                valor = valor.replace(".", "").replace(",", ".");
            } else {
                valor = valor.replace(",", "");
            }
        } else if (valor.contains(",")) {
            valor = valor.replace(",", ".");
        }
        return new BigDecimal(valor);
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                .toList();
        throw new ListErrorException(list);
    }

}
