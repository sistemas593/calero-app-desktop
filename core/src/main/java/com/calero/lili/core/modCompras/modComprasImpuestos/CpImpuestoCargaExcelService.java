package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.FormaPagoSriEnum;
import com.calero.lili.core.enums.PagoLocalExterior;
import com.calero.lili.core.enums.SustentoCodigos;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesEntity;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTerceroLoteHelper;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.ValidarTipoArchivo;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
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
    private final GeTerceroLoteHelper geTerceroLoteHelper;
    private final AdEmpresasRepository adEmpresasRepository;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleError;
    private final ValidacionGeneralCpImpuestosService service;
    private final CpImpuestosRepository cpImpuestosRepository;
    private final ValidacionValoresCpImpuestosService serviceValores;

    private static final int TOTAL_COLUMNAS_FORMATO_ACTUAL = 62;

    private record FilaExcel(int linea, String[] celdas) {
    }


    public void cargarExcelCompraImpuestos(Long idData, Long idEmpresa,
                                           MultipartFile file, String usuario, String sucursal) throws IOException {

        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));

        Optional<AdEmpresasSucursalesEntity> sucursalEntity = adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, sucursal);


        if (sucursalEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("La sucursal {0} no existe ", sucursal));
        }

        List<FilaExcel> filasCrudas = leerFilasFormatoActual(file);

        procesarFilas(idData, idEmpresa, empresa, sucursal, usuario, filasCrudas);
    }

    /**
     * Sube un excel con el "formato dos" (columnas en otro orden, por ejemplo un reporte de compras
     * exportado de otro sistema). Lee el archivo, traduce cada fila al mismo orden de columnas que ya
     * usa {@link #procesarFilas}, y reutiliza toda la validacion/guardado que ya existe para el formato
     * original, sin duplicarla.
     */
    public void cargarExcelCompraImpuestosFormatoDos(Long idData, Long idEmpresa,
                                                     MultipartFile file, String usuario, String sucursal) throws IOException {

        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));

        Optional<AdEmpresasSucursalesEntity> sucursalEntity = adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, sucursal);


        if (sucursalEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("La sucursal {0} no existe ", sucursal));
        }

        List<FilaExcel> filasCrudas = leerFilasFormatoDos(file);

        procesarFilas(idData, idEmpresa, empresa, sucursal, usuario, filasCrudas);
    }

    /**
     * Lee el excel con el formato original: copia cada celda tal cual, en el mismo orden en que viene
     * en el archivo (que ya coincide con el orden que espera {@link #procesarFilas}).
     */
    private List<FilaExcel> leerFilasFormatoActual(MultipartFile file) throws IOException {

        List<FilaExcel> filas = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        // Se usa WorkbookFactory + DataFormatter (igual que en leerFilasFormatoDos) en vez de
        // StreamingReader + getStringCellValue(). getStringCellValue() exige que la celda sea de
        // tipo texto; en la practica, columnas como las fechas se guardan en el excel como celdas
        // numericas/fecha (no texto), y StreamingReader, en vez de lanzar una excepcion clara para
        // esas celdas, puede devolver el valor de OTRA celda del archivo (lee mal el tipo de celda
        // cuando no es texto). Eso hacia que el aplicativo reportara datos de una columna distinta
        // a la real, con archivos que en realidad estaban bien formados. DataFormatter.formatCellValue()
        // sí soporta todos los tipos de celda (texto, numero, fecha, booleano) de forma segura.
        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)) {
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
                        celdas[i] = valorCelda(row, i, formatter);
                    }

                    int lineaActual = row.getRowNum() + 1;
                    filas.add(new FilaExcel(lineaActual, celdas));
                }
            }
        }

        return filas;
    }

    /**
     * Lee el excel con el "formato dos": se abre con {@link WorkbookFactory} (soporta tanto .xls como
     * .xlsx) y, por cada fila, arma un arreglo de
     * celdas en el MISMO orden que el formato original: traduce la posicion fisica de cada columna de
     * este archivo a la posicion logica que el resto del codigo ya espera. Ningun otro metodo de la
     * clase necesita saber que el archivo de origen tenia otro orden de columnas.
     */
    private List<FilaExcel> leerFilasFormatoDos(MultipartFile file) throws IOException {

        List<FilaExcel> filas = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)) {
            for (Sheet sheet : wb) {
                boolean isHeader = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    String[] celdas = new String[TOTAL_COLUMNAS_FORMATO_ACTUAL];

                    celdas[0] = valorCelda(row, 2, formatter);                       // identificacion          <- idProv
                    celdas[1] = valorCelda(row, 3, formatter);                       // nombre tercero          <- RAZON SOCIAL
                    celdas[2] = valorCelda(row, 1, formatter);                       // TIPO DE IDENTIFIACION
                    celdas[3] = traducirRelacionado(valorCelda(row, 55, formatter)); // relacionado             <- parteRel (SI/NO)
                    celdas[4] = valorCelda(row, 54, formatter);                      // tipo proveedor          <- tipoProv
                    // celdas[5] tipo contribuyente: sin columna equivalente en este formato, queda null
                    celdas[6] = valorCelda(row, 9, formatter);                       // fecha emision           <- fechaEmision
                    celdas[7] = valorCelda(row, 5, formatter);                       // fecha registro          <- fechaRegistro
                    celdas[8] = valorCelda(row, 4, formatter);                       // tipo documento          <- tipoComprobante
                    celdas[9] = concatenarSerie(row, 6, 7, formatter);               // serie                   <- establecimiento + puntoEmision
                    celdas[10] = valorCelda(row, 8, formatter);                      // secuencial              <- secuencial
                    celdas[11] = valorCelda(row, 10, formatter);                     // numero de autorizacion  <- autorizacion
                    // celdas[12] fecha vencimiento: sin columna equivalente en este formato, queda null
                    celdas[13] = valorCelda(row, 0, formatter);                      // codigo sustento         <- codSustento
                    // celdas[14] devolucion iva: sin columna equivalente en este formato, queda null
                    // celdas[15] concepto: sin columna equivalente en este formato, queda null
                    celdas[16] = valorCelda(row, 12, formatter);                     // base 0%                 <- baseImponibleCERO
                    celdas[17] = valorCelda(row, 13, formatter);                     // base gravada (1)        <- baseImpGrav (1)
                    celdas[18] = valorCelda(row, 14, formatter);                     // tarifa (1)              <- PORCENTAJE IVA (1)
                    celdas[19] = valorCelda(row, 15, formatter);                     // valor (1)               <- montoIva (1)
                    celdas[20] = valorCelda(row, 16, formatter);                     // base gravada (2)        <- baseImpGrav (2)
                    celdas[21] = valorCelda(row, 17, formatter);                     // tarifa (2)              <- PORCENTAJE IVA (2)
                    celdas[22] = valorCelda(row, 18, formatter);                     // valor (2)               <- montoIva (2)
                    celdas[45] = valorCelda(row, 49, formatter);                     // doc modificado          <- docModificado
                    celdas[46] = concatenarSerie(row, 50, 51, formatter);            // serie modificado        <- estabModificado + ptoEmiModificado
                    celdas[47] = valorCelda(row, 52, formatter);                     // secuencial modificado   <- secModificado
                    celdas[48] = valorCelda(row, 53, formatter);                     // num. autorizacion mod.  <- autModificado
                    celdas[51] = valorCelda(row, 27, formatter);                     // pago local/exterior     <- pagoLocExt
                    celdas[52] = valorCelda(row, 32, formatter);                     // pais efec. pago         <- paisEfecPago
                    celdas[53] = valorCelda(row, 35, formatter);                     // pago regimen fiscal     <- pagoRegFis
                    celdas[54] = valorCelda(row, 33, formatter);                     // aplic. conv. dob. trib. <- aplicConvDobTrib
                    celdas[55] = valorCelda(row, 34, formatter);                     // pag ext sujeto ret.     <- pagExtSujRetNorLeg
                    celdas[56] = valorCelda(row, 28, formatter);                     // tipo regimen fiscal ext.<- tipoRegi
                    celdas[57] = valorCelda(row, 29, formatter);                     // pais regimen general    <- paisEfecPagoGen
                    celdas[58] = valorCelda(row, 30, formatter);                     // pais paraiso fiscal     <- paisEfecPagoParFis
                    celdas[59] = valorCelda(row, 31, formatter);                     // denominacion regimen    <- denopagoRegFis
                    celdas[60] = valorCelda(row, 36, formatter);                     // forma pago SRI          <- formaPago

                    int lineaActual = row.getRowNum() + 1;
                    filas.add(new FilaExcel(lineaActual, celdas));
                }
            }
        }

        return filas;
    }

    /**
     * Obtiene el valor de una celda como texto, sin importar si en el archivo esta almacenada como texto,
     * numero o fecha (a diferencia de getStringCellValue(), que lanza una excepcion si la celda no es de
     * tipo texto). Devuelve null si la celda no existe o esta vacia.
     */
    private String valorCelda(Row row, int col, DataFormatter formatter) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        String valor = formatter.formatCellValue(cell).trim();
        return valor.isBlank() ? null : valor;
    }

    /**
     * Arma la serie (6 digitos: establecimiento + punto de emision, con padding a 3 digitos cada uno) a
     * partir de dos columnas separadas, tal como vienen en el formato dos.
     */
    private String concatenarSerie(Row row, int colEstablecimiento, int colPuntoEmision, DataFormatter formatter) {
        String establecimiento = valorCelda(row, colEstablecimiento, formatter);
        String puntoEmision = valorCelda(row, colPuntoEmision, formatter);
        if (establecimiento == null || puntoEmision == null) return null;
        try {
            return String.format("%03d%03d", Integer.parseInt(establecimiento.trim()), Integer.parseInt(puntoEmision.trim()));
        } catch (NumberFormatException e) {
            return establecimiento.trim() + puntoEmision.trim();
        }
    }

    /**
     * El formato dos trae "relacionado" como SI/NO en vej del "verdadero"/otro valor que ya interpreta
     * el resto del codigo (ver el bloque de "relacionado" dentro de {@link #procesarFilas}).
     */
    private String traducirRelacionado(String valor) {
        if (valor == null) return null;
        return valor.trim().equalsIgnoreCase("SI") ? "verdadero" : "falso";
    }

    /**
     * Nucleo compartido por ambos formatos: recibe las filas ya traducidas al mismo orden de columnas
     * (sin importar de que formato vinieron), detecta duplicados, busca/crea los terceros, arma cada
     * {@link CpImpuestosEntity}, valida y guarda. Nada de este metodo sabe ni le importa de que archivo
     * salieron las filas.
     */
    private void procesarFilas(Long idData, Long idEmpresa, AdEmpresaEntity empresa, String sucursal,
                               String usuario, List<FilaExcel> filasCrudas) {

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<CpImpuestosEntity> cpImpuestosEntities = new ArrayList<>();

        /*
          Paso unico: recorrer todas las filas ya leidas una sola vez, para detectar duplicados y obtener
          el codigo de tercero, que se manda a buscar en la base de datos.
         */

        List<FilaExcel> filas = new ArrayList<>();
        Set<String> numerosIdentifiacion = new HashSet<>();
        Set<String> clavesDuplicados = new HashSet<>();

        for (FilaExcel fila : filasCrudas) {
            String[] celdas = fila.celdas();

            if (esClaveDuplicadaCompleta(celdas)) {
                String claveDuplicado = claveDuplicado(celdas);
                if (!clavesDuplicados.add(claveDuplicado)) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle(MessageFormat.format(
                            "Registro duplicado: ya existe una fila con identificación {0}, serie {1}, secuencial {2} y número de autorización {3}",
                            celda(celdas, 0), celda(celdas, 9), celda(celdas, 10), celda(celdas, 11)));
                    detalleErrores.add(detalleError);
                    continue;
                }
            }

            filas.add(fila);

            if (celdas[0] != null && !celdas[0].isBlank()) {
                numerosIdentifiacion.add(celdas[0]);
            }
        }

        /*
          Con los codigos se busca en la base de datos los terceros, y se van almacenando un map, que contiene el codigo
          y la entidad de tercero para posterior asignarse a cada registro en el detalle.
         */


        Map<String, GeTerceroEntity> mapTercero =
                geTercerosRepository.findAllNumeroIdentifiacion(idData, new ArrayList<>(numerosIdentifiacion))
                        .stream()
                        .collect(Collectors.toMap(GeTerceroEntity::getNumeroIdentificacion, Function.identity()));

        for (FilaExcel fila : filas) {

            CpImpuestosEntity cpImpuestos = new CpImpuestosEntity();

            cpImpuestos.setIdImpuestos(UUID.randomUUID());
            cpImpuestos.setIdData(idData);
            cpImpuestos.setIdEmpresa(empresa.getIdEmpresa());
            cpImpuestos.setSucursal(sucursal);
            cpImpuestos.setCreatedBy(usuario);
            cpImpuestos.setCreatedDate(LocalDateTime.now());

            String numeroIdentifiacion = celda(fila.celdas(), 0);

            if (numeroIdentifiacion != null) {
                String nombreTercero = celda(fila.celdas(), 1);

                // GeTerceroEntity.tercero es NOT NULL en base de datos: si el tercero todavia no
                // existe (ni en este lote ni en la base) y el excel no trae el nombre, no se puede
                // crear. Se reporta como error de fila en vez de dejar que falle mas adelante al
                // guardar (lo que abortaria TODO el lote con un error generico de base de datos).
                boolean esTerceroNuevo = !mapTercero.containsKey(numeroIdentifiacion);
                if (esTerceroNuevo && (nombreTercero == null || nombreTercero.isBlank())) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("El nombre del tercero es requerido para crear un tercero nuevo");
                    detalleErrores.add(detalleError);
                } else {
                    GeTerceroEntity tercero = geTerceroLoteHelper.obtenerOCrearEnLote(mapTercero, idData, numeroIdentifiacion,
                            () -> {
                                GeTerceroEntity terceroEntity = new GeTerceroEntity();
                                terceroEntity.setIdTercero(UUID.randomUUID());
                                terceroEntity.setIdData(idData);
                                terceroEntity.setTercero(Objects.nonNull(nombreTercero) ? nombreTercero : null);
                                terceroEntity.setNumeroIdentificacion(numeroIdentifiacion);
                                validarTipoIdentifiacion(terceroEntity, fila.celdas(), fila.linea(), detalleErrores);

                                return geTercerosRepository.save(terceroEntity);
                            });
                    cpImpuestos.setTercero(tercero);
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
                try {
                    cpImpuestos.setFechaEmision(DateUtils.toLocalDate(fechaEmision));
                } catch (Exception exception) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("La fecha de emisión '" + fechaEmision + "' no tiene un formato válido (se espera dd/MM/yyyy)");
                    detalleErrores.add(detalleError);
                }
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de emisión se encuentra");
                detalleErrores.add(detalleError);
            }


            String fechaRegistro = celda(fila.celdas(), 7);
            if (Objects.nonNull(fechaRegistro)) {
                try {
                    cpImpuestos.setFechaRegistro(DateUtils.toLocalDate(fechaRegistro));
                } catch (Exception exception) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("La fecha de registro '" + fechaRegistro + "' no tiene un formato válido (se espera dd/MM/yyyy)");
                    detalleErrores.add(detalleError);
                }
            } else {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de registro se encuentra");
                detalleErrores.add(detalleError);
            }

            String tipoDocumento = celda(fila.celdas(), 8);
            if (Objects.nonNull(tipoDocumento)) {
                try {
                    DocumentoEnum documentoEnum = DocumentoEnum.getCodigoDocumento(tipoDocumento);
                    cpImpuestos.setCodigoDocumento(documentoEnum);
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
                detalleError.setDetalle("El número de autorización no se encuentra");
                detalleErrores.add(detalleError);
            }


            String fechaVencimiento = celda(fila.celdas(), 12);
            if (Objects.nonNull(fechaVencimiento)) {
                try {
                    cpImpuestos.setFechaVencimiento(DateUtils.toLocalDate(fechaVencimiento));
                } catch (Exception exception) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("La fecha de vencimiento '" + fechaVencimiento + "' no tiene un formato válido (se espera dd/MM/yyyy)");
                    detalleErrores.add(detalleError);
                }
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
                detalleError.setDetalle("La fecha de registro no se encuentra");
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


            String docModificado = celda(fila.celdas(), 45);
            if (Objects.nonNull(docModificado)) {
                cpImpuestos.setModCodigoDocumento(DocumentoEnum.getCodigoDocumento(docModificado));
            }

            String serieModificado = celda(fila.celdas(), 46);
            if (Objects.nonNull(serieModificado)) {
                cpImpuestos.setModSerie(serieModificado);
            }

            String secuencialModificado = celda(fila.celdas(), 47);
            if (Objects.nonNull(secuencialModificado)) {

                if (secuencialModificado.matches("\\d{9}")) {
                    cpImpuestos.setModSecuencial(secuencialModificado);
                } else {
                    cpImpuestos.setModSecuencial(String.format("%09d", Integer.parseInt(secuencialModificado)));
                }
            }

            String numAutMod = celda(fila.celdas(), 48);
            if (Objects.nonNull(numAutMod)) {
                cpImpuestos.setModNumAutorizacion(numAutMod);
            }

            List<CpImpuestosValoresEntity> valores = new ArrayList<>();

            try {
                String baseCero = celda(fila.celdas(), 16);

                if (Objects.nonNull(baseCero)) {
                    CpImpuestosValoresEntity valoresEntity = new CpImpuestosValoresEntity();

                    valoresEntity.setIdImpuestosValores(UUID.randomUUID());
                    valoresEntity.setIdData(idData);
                    valoresEntity.setIdEmpresa(idEmpresa);


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

                    valoresEntity.setIdImpuestosValores(UUID.randomUUID());
                    valoresEntity.setIdData(idData);
                    valoresEntity.setIdEmpresa(idEmpresa);

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

                    valoresEntity.setIdImpuestosValores(UUID.randomUUID());
                    valoresEntity.setIdData(idData);
                    valoresEntity.setIdEmpresa(idEmpresa);

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
            } catch (Exception exception) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila.linea(), EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("Error en los valores (base/tarifa/IVA) de la fila: " + exception.getMessage());
                detalleErrores.add(detalleError);
            }

            setearPagoLocalExterior(cpImpuestos, fila.celdas(), fila.linea(), detalleErrores);
            setearTotales(cpImpuestos, valores, fila.celdas(), fila.linea(), detalleErrores);
            cpImpuestos.setValoresEntity(valores);
            cpImpuestosEntities.add(cpImpuestos);
        }

        if (detalleErrores.isEmpty()) {

            List<CpImpuestoDetalleError> listaErroresValidacion = new ArrayList<>();
            List<DetalleCompras> listaComprobacion = cpImpuestoDetalleError.builderListValidacion(cpImpuestosEntities);

            for (DetalleCompras dto : listaComprobacion) {
                listaErroresValidacion.addAll(service.validacionGeneral(dto));
            }

            for (CpImpuestosEntity entity : cpImpuestosEntities) {
                serviceValores.validacionValoresGeneral(entity, listaErroresValidacion);
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

    private void validarTipoIdentifiacion(GeTerceroEntity terceroEntity, String[] celdas,
                                          int fila, List<DetalleError> detalleErrores) {

        String tipoIdProv = celda(celdas, 2);

        if (Objects.nonNull(tipoIdProv)) {

            try {
                TipoIdentificacion tipo = TipoIdentificacion.obtenerTipoIdentifiacionPorCodigoCompra(tipoIdProv);
                terceroEntity.setTipoIdentificacion(tipo.name());
            } catch (Exception exception) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("La fecha de registro no se encuentra");
                detalleErrores.add(detalleError);
            }

        } else {

            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(fila, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de registro no se encuentra");
            detalleErrores.add(detalleError);
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

    /**
     * Verifica que las columnas utilizadas para detectar duplicados (identificacion, serie,
     * secuencial y numero de autorizacion) vengan todas completas en la fila.
     */
    private boolean esClaveDuplicadaCompleta(String[] celdas) {
        return celda(celdas, 0) != null && celda(celdas, 9) != null
                && celda(celdas, 10) != null && celda(celdas, 11) != null;
    }

    /**
     * Construye la clave utilizada para detectar registros duplicados en el excel,
     * combinando las columnas 0 (identificacion), 9 (serie), 10 (secuencial) y 11 (numero de autorizacion).
     */
    private String claveDuplicado(String[] celdas) {
        return celda(celdas, 0) + "|" + celda(celdas, 9) + "|" + celda(celdas, 10) + "|" + celda(celdas, 11);
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
        String valorOriginal = valor;
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
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El valor '" + valorOriginal + "' no es un número válido");
        }
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                .toList();
        throw new ListErrorException(list);
    }


    private void setearTotales(CpImpuestosEntity cpImpuestos, List<CpImpuestosValoresEntity> valores,
                               String[] celdas, int linea, List<DetalleError> detalleErrores) {


        BigDecimal subtotal = valores.stream()
                .map(CpImpuestosValoresEntity::getBaseImponible)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalImpuesto = valores.stream()
                .map(CpImpuestosValoresEntity::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String formaPagoSri = celda(celdas, 60);

        cpImpuestos.setSubtotal(subtotal);
        cpImpuestos.setTotalImpuesto(totalImpuesto);
        cpImpuestos.setTotal(subtotal.add(totalImpuesto));

        if (Objects.nonNull(formaPagoSri)) {

            try {
                FormaPagoSriEnum formaPagoSriEnum = FormaPagoSriEnum.getFormaPagoSri(formaPagoSri);
                List<FormasPagoSri> listFormaPago = new ArrayList<>();
                FormasPagoSri formaPago = new FormasPagoSri();
                formaPago.setTotal(cpImpuestos.getTotal());
                formaPago.setFormaPago(formaPagoSriEnum);
                listFormaPago.add(formaPago);
                cpImpuestos.setFormasPagoSri(listFormaPago);

            } catch (Exception exception) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle(exception.getMessage());
                detalleErrores.add(detalleError);
            }

        }


    }


    private void setearPagoLocalExterior(CpImpuestosEntity cpImpuestos, String[] celdas, int linea, List<DetalleError> detalleErrores) {

        String pagoLocExt = celda(celdas, 51);
        if (Objects.nonNull(pagoLocExt)) {
            try {

                PagoLocalExterior pago = PagoLocalExterior.getFormaPagoLocExt(pagoLocExt);

                if (pago.equals(PagoLocalExterior.L)) {
                    cpImpuestos.setPagoLocExt(PagoLocalExterior.L);
                }

                if (pago.equals(PagoLocalExterior.E)) {
                    cpImpuestos.setPagoLocExt(PagoLocalExterior.E);

                    String paisEfecPago = celda(celdas, 52);
                    String pagoRegFis = celda(celdas, 53);
                    String aplicConvDobTrib = celda(celdas, 54);
                    String pagExtSujRetNorLeg = celda(celdas, 55);
                    String tipoRegi = celda(celdas, 56);
                    String paisEfecPagoGen = celda(celdas, 57);
                    String paisEfecPagoParFis = celda(celdas, 58);
                    String denopagoRegFis = celda(celdas, 59);

                    PagoExterior pagoExterior = new PagoExterior();

                    pagoExterior.setPaisEfecPago(paisEfecPago);
                    pagoExterior.setPagoRegFis(pagoRegFis);
                    pagoExterior.setAplicConvDobTrib(aplicConvDobTrib);
                    pagoExterior.setPagExtSujRetNorLeg(pagExtSujRetNorLeg);
                    pagoExterior.setTipoRegi(tipoRegi);
                    pagoExterior.setPaisEfecPagoGen(paisEfecPagoGen);
                    pagoExterior.setPaisEfecPagoParFis(paisEfecPagoParFis);
                    pagoExterior.setDenopagoRegFis(denopagoRegFis);
                    cpImpuestos.setPagoExterior(pagoExterior);

                }
            } catch (Exception exception) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle(exception.getMessage());
                detalleErrores.add(detalleError);
            }
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El codigo de pago local o exterior no se encuentra");
            detalleErrores.add(detalleError);
        }
    }


}
