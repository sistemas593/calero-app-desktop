package com.calero.lili.core.modCargarExcel.services;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.EstadoCivilEnum;
import com.calero.lili.core.enums.OrigenIngresosEnum;
import com.calero.lili.core.enums.SexoEnum;
import com.calero.lili.core.enums.TipoPersoneria;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modLocalidades.modCantones.CantonRepository;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaRepository;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTerceros.GeTercerosTipoEntity;
import com.calero.lili.core.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisesRepository;
import com.calero.lili.core.utils.ValidarTipoArchivo;
import com.calero.lili.core.utils.validaciones.ValidarIdentificacion;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelCargarTercerosServiceImpl {

    private final GeTercerosRepository clientesRepository;
    private final ValidarIdentificacion validarIdentificacion;
    private final GeTercerosTipoRepository geTercerosTipoRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final ProvinciaRepository provinciaRepository;
    private final CantonRepository cantonRepository;
    private final ParroquiaRepository parroquiaRepository;
    private final TbPaisesRepository tbPaisesRepository;

    public void carga(Long idData, MultipartFile file, String usuario) throws IOException {

        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        long startTimeRead = System.currentTimeMillis();

        // Paso único: leer todas las filas en memoria una sola vez
        record FilaExcel(int linea, String[] celdas) {
        }
        List<FilaExcel> filas = new ArrayList<>();
        Set<String> codigosLocalidades = new HashSet<>();
        Set<String> codigosPaises = new HashSet<>();
        Set<String> identificaciones = new HashSet<>();
        Set<String> codigosTercero = new HashSet<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(500)
                     .bufferSize(65536)
                     .open(is)) {
            for (Sheet sheet : workbook) {
                boolean isHeader = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    int lastCell = row.getLastCellNum();
                    String[] celdas = new String[Math.max(lastCell, 21)];
                    for (int i = 0; i < lastCell; i++) {
                        celdas[i] = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : null;
                    }
                    filas.add(new FilaExcel(row.getRowNum() + 1, celdas));

                    if (celdas[2] != null && !celdas[2].isBlank()) identificaciones.add(celdas[2]);
                    if (celdas[3] != null && !celdas[3].isBlank()) codigosTercero.add(celdas[3]);
                    if (celdas[12] != null && !celdas[12].isBlank()) codigosPaises.add(celdas[12]);

                    String prov = celdas[13], cant = celdas[14], parr = celdas[15];
                    if (prov != null && !prov.isBlank() && cant != null && !cant.isBlank() && parr != null && !parr.isBlank()) {
                        codigosLocalidades.add(prov + cant + parr);
                    }
                }
            }
        }

        // Bulk queries — una sola consulta por categoría
        Map<String, ParroquiaEntity> parroquias = codigosLocalidades.isEmpty() ? Map.of() :
                parroquiaRepository.findAllByCodigoParroquia(new ArrayList<>(codigosLocalidades))
                        .stream().collect(Collectors.toMap(ParroquiaEntity::getCodigoParroquia, Function.identity()));

        Map<String, TbPaisEntity> paises = codigosPaises.isEmpty() ? Map.of() :
                tbPaisesRepository.findAllByCodigoPaises(new ArrayList<>(codigosPaises))
                        .stream().collect(Collectors.toMap(TbPaisEntity::getCodigoPais, Function.identity()));

        Set<String> identificacionesExistentes = identificaciones.isEmpty() ? Set.of() :
                new HashSet<>(clientesRepository.findAllExistByNumeroIdentificacion(idData, new ArrayList<>(identificaciones)));

        Set<String> codigosTerceroExistentes = codigosTercero.isEmpty() ? Set.of() :
                new HashSet<>(clientesRepository.findAllExistByCodigoTercero(idData, new ArrayList<>(codigosTercero)));

        // Procesar filas desde memoria
        List<DetalleError> listaErrores = new ArrayList<>();
        List<GeTerceroEntity> tercerosLista = new ArrayList<>();
        List<GeTercerosTipoEntity> tercerosTipoClienteLista = new ArrayList<>();
        List<GeTercerosTipoEntity> tercerosTipoProveedorLista = new ArrayList<>();

        for (FilaExcel fila : filas) {
            String[] celdas = fila.celdas();
            int linea = fila.linea();

            GeTerceroEntity cliente = new GeTerceroEntity();
            UUID tokenIdTercero = UUID.randomUUID();
            cliente.setIdTercero(tokenIdTercero);
            cliente.setIdData(idData);
            cliente.setCreatedBy(usuario);
            cliente.setCreatedDate(LocalDateTime.now());

            if (celda(celdas, 0) == null) {
                listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CLIENT_NAME_NOT_FOUND));
            } else {
                cliente.setTercero(celda(celdas, 0));
            }

            String tipoId = celda(celdas, 1);
            String numId = celda(celdas, 2);

            if ((tipoId == null && numId != null) || (tipoId != null && numId == null)) {
                listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_IDENTIFICACION_NUMERO_IDENTIFICACION));
            }

            if (tipoId != null && numId != null) {
                boolean tipoIdentificacionCorrecto = true;
                try {
                    cliente.setTipoIdentificacion(tipoId);
                } catch (IllegalArgumentException e) {
                    tipoIdentificacionCorrecto = false;
                    listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_IDENTIFICACION_INCORRECTO));
                }

                if (tipoIdentificacionCorrecto) {
                    cliente.setNumeroIdentificacion(numId);

                    if (cliente.getTipoIdentificacion().equals("R")) {
                        try {
                            validarIdentificacion.validarRuc(numId);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.RUC_INCORRECTO));
                        }
                    }

                    if (cliente.getTipoIdentificacion().equals("C")) {
                        try {
                            validarIdentificacion.validarCedula(numId);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CEDULA_INCORRECTA));
                        }
                    }

                    if (identificacionesExistentes.contains(numId)) {
                        listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NUMERO_IDENTIFICACION_YA_EXISTE));
                    }
                }
            }

            String codTercero = celda(celdas, 3);
            if (codTercero == null) {
                cliente.setCodigoTercero(null);
            } else {
                if (codigosTerceroExistentes.contains(codTercero)) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CODIGO_TERCERO_YA_EXISTE);
                    detalle.setDetalle("El código tercero: " + codTercero);
                    listaErrores.add(detalle);
                }
                cliente.setCodigoTercero(codTercero);
            }

            cliente.setWeb(celda(celdas, 4) != null ? celda(celdas, 4) : "");
            cliente.setObservaciones(celda(celdas, 10) != null ? celda(celdas, 10) : "");

            cliente.setTipoPersoneria(null);
            String tipoPersoneria = celda(celdas, 11);
            if (tipoPersoneria == null) {
                listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_CLIENTE_NOT_FOUND));
            } else {
                try {
                    cliente.setTipoPersoneria(TipoPersoneria.valueOf(tipoPersoneria));
                } catch (Exception e) {
                    listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO));
                }
            }

            String direccion = celda(celdas, 5);
            if (direccion == null) {
                listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.DIRECCION_NOT_FOUND));
            } else {
                cliente.setDireccion(direccion);
                cliente.setCiudad(celda(celdas, 6) != null ? celda(celdas, 6) : "");
                cliente.setTelefonos(celda(celdas, 7) != null ? celda(celdas, 7) : "");
                cliente.setContacto(celda(celdas, 8) != null ? celda(celdas, 8) : "");
                cliente.setEmail(celda(celdas, 9) != null ? celda(celdas, 9) : "");
            }

            String esCliente = celda(celdas, 19);
            String esProveedor = celda(celdas, 20);
            if (esCliente != null && esProveedor != null) {
                if (Objects.equals(esCliente, "S")) {
                    GeTercerosTipoEntity tercerosClientes = new GeTercerosTipoEntity();
                    tercerosClientes.setIdTerceroTipo(UUID.randomUUID());
                    tercerosClientes.setTipo(TipoTercero.CLIENTE.getTipo());
                    tercerosClientes.setTercero(GeTerceroEntity.builder().idTercero(tokenIdTercero).build());
                    tercerosTipoClienteLista.add(tercerosClientes);
                }
                if (Objects.equals(esProveedor, "S")) {
                    GeTercerosTipoEntity tercerosProveedores = new GeTercerosTipoEntity();
                    tercerosProveedores.setIdTerceroTipo(UUID.randomUUID());
                    tercerosProveedores.setTipo(TipoTercero.PROVEEDOR.getTipo());
                    tercerosProveedores.setTercero(GeTerceroEntity.builder().idTercero(tokenIdTercero).build());
                    tercerosTipoProveedorLista.add(tercerosProveedores);
                }
            } else {
                listaErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.ES_TERCERO_ERROR));
            }

            validarNuevaInformacion(parroquias, paises, cliente, celdas, listaErrores, linea);

            if (listaErrores.isEmpty()) {
                tercerosLista.add(cliente);
            }
        }

        long endTimeRead = System.currentTimeMillis();


        long startTimeWrite = System.currentTimeMillis();

        if (listaErrores.isEmpty()) {
            clientesRepository.saveAll(tercerosLista);
            geTercerosTipoRepository.saveAll(tercerosTipoClienteLista);
            geTercerosTipoRepository.saveAll(tercerosTipoProveedorLista);
        } else {
            List<String> list = listaErrores.stream()
                    .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                    .toList();
            throw new ListErrorException(list);
        }

        long endTimeWrite = System.currentTimeMillis();
    }

    private void validarNuevaInformacion(Map<String, ParroquiaEntity> parroquias, Map<String, TbPaisEntity> paises,
                                         GeTerceroEntity cliente, String[] celdas, List<DetalleError> listaErrores, int linea) {
        setearLocalidadesTercero(parroquias, paises, celdas, cliente, linea, listaErrores);
        setearInformacionTercero(celdas, cliente, linea, listaErrores);
    }

    private void setearLocalidadesTercero(Map<String, ParroquiaEntity> parroquias, Map<String, TbPaisEntity> paises,
                                          String[] celdas, GeTerceroEntity tercero, int linea, List<DetalleError> listaErrores) {

        String codigoPais = celda(celdas, 12);
        if (codigoPais != null) {
            tercero.setPais(paises.get(codigoPais));
        } else {
            tercero.setPais(null);
        }

        String prov = celda(celdas, 13);
        String cant = celda(celdas, 14);
        String parr = celda(celdas, 15);

        if (prov != null && cant != null && parr != null) {
            ParroquiaEntity parroquia = parroquias.get(prov + cant + parr);
            if (Objects.nonNull(parroquia)) {
                tercero.setParroquia(parroquia);
                tercero.setCanton(parroquia.getCanton());
                tercero.setProvincia(parroquia.getCanton().getProvincia());
            } else {
                tercero.setProvincia(null);
                tercero.setCanton(null);
                tercero.setParroquia(null);
            }
        } else {
            tercero.setProvincia(null);
            tercero.setCanton(null);
            tercero.setParroquia(null);
        }
    }

    private void setearInformacionTercero(String[] celdas, GeTerceroEntity tercero, int linea, List<DetalleError> listaErrores) {

        tercero.setEstadoCivil(null);
        tercero.setSexo(null);
        tercero.setOrigenIngresos(null);

        String sexo = celda(celdas, 16);
        if (sexo != null) {
            try {
                tercero.setSexo(SexoEnum.valueOf(sexo));
            } catch (Exception e) {
                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                detalle.setDetalle("El sexo: " + sexo + " es incorrecto, debe ser M o F");
                listaErrores.add(detalle);
            }
        }

        String estadoCivil = celda(celdas, 17);
        if (estadoCivil != null) {
            try {
                tercero.setEstadoCivil(EstadoCivilEnum.valueOf(estadoCivil));
            } catch (Exception e) {
                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                detalle.setDetalle("El estado civil: " + estadoCivil + " es incorrecto, debe ser SOLTERO (S)," +
                        " CASADO (C), DIVORCIADO (D) o VIUDO (V), UNIÓN LIBRE (U)");
                listaErrores.add(detalle);
            }
        }

        String origenIngresos = celda(celdas, 18);
        if (origenIngresos != null) {
            try {
                tercero.setOrigenIngresos(OrigenIngresosEnum.valueOf(origenIngresos));
            } catch (Exception e) {
                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                detalle.setDetalle("El origen de ingresos: " + origenIngresos + " es incorrecto," +
                        " debe ser EMPLEADO PÚBLICO (B), EMPLEADO PRIVADO (V)," +
                        " INDEPENDIENTE (I), AMA DE CASA O ESTUDIANTE (A), RENTISTA (R)," +
                        " JUBILADO (H) o REMESAS DEL EXTERIOR (M)");
                listaErrores.add(detalle);
            }
        }
    }

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

}





