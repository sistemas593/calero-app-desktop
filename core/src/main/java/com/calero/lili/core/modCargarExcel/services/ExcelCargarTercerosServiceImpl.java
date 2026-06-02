package com.calero.lili.core.modCargarExcel.services;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.EstadoCivilEnum;
import com.calero.lili.core.enums.OrigenIngresosEnum;
import com.calero.lili.core.enums.SexoEnum;
import com.calero.lili.core.enums.TipoClienteProveedor;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modCantones.CantonRepository;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaRepository;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTerceros.GeTercerosTipoEntity;
import com.calero.lili.core.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.core.modTerceros.projections.GeTerceroProjection;
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
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> listaErrores = new ArrayList<>();

        List<GeTerceroEntity> tercerosLista = new ArrayList<>();
        List<GeTercerosTipoEntity> tercerosTipoClienteLista = new ArrayList<>();
        List<GeTercerosTipoEntity> tercerosTipoProveedorLista = new ArrayList<>();

        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                GeTerceroEntity cliente = new GeTerceroEntity();


                UUID tokenIdTercero = UUID.randomUUID();
                cliente.setIdTercero(tokenIdTercero);
                cliente.setIdData(idData);
                cliente.setCreatedBy(usuario);
                cliente.setCreatedDate(LocalDateTime.now());

                if (row.getCell(0) == null) {

                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CLIENT_NAME_NOT_FOUND);
                    listaErrores.add(detalle);
                } else {
                    String clienteCelda = row.getCell(0).getStringCellValue();
                    cliente.setTercero(clienteCelda);
                }

                if ((row.getCell(1) == null && (row.getCell(2) != null)) || (row.getCell(1) != null && (row.getCell(2) == null))) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_IDENTIFICACION_NUMERO_IDENTIFICACION);
                    listaErrores.add(detalle);
                }

                if (row.getCell(1) != null && (row.getCell(2) != null)) {

                    String tipoIdentificacionCelda = row.getCell(1).getStringCellValue();
                    String numeroIdentificacionCelda = row.getCell(2).getStringCellValue();

                    Boolean tipoIdentificacionCorrecto = Boolean.TRUE;
                    try {
                        cliente.setTipoIdentificacion(tipoIdentificacionCelda);
                    } catch (IllegalArgumentException e) {
                        tipoIdentificacionCorrecto = Boolean.FALSE;
                        DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_IDENTIFICACION_INCORRECTO);
                        listaErrores.add(detalle);
                    }

                    if (tipoIdentificacionCorrecto) {
                        cliente.setNumeroIdentificacion(numeroIdentificacionCelda);

                        if (cliente.getTipoIdentificacion().equals("R")) {
                            try {
                                validarIdentificacion.validarRuc(cliente.getNumeroIdentificacion());
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.RUC_INCORRECTO);
                                listaErrores.add(detalle);
                            }
                        }

                        if (cliente.getTipoIdentificacion().equals("C")) {
                            try {
                                validarIdentificacion.validarCedula(cliente.getNumeroIdentificacion());
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CEDULA_INCORRECTA);
                                listaErrores.add(detalle);
                            }
                        }

                        Optional<GeTerceroProjection> existe = clientesRepository.findExistByNumeroIdentificacion(idData, cliente.getNumeroIdentificacion());
                        if (existe.isPresent()) {
                            DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.NUMERO_IDENTIFICACION_YA_EXISTE);
                            listaErrores.add(detalle);
                        }

                    }
                }


                if (row.getCell(3) == null) {
                    cliente.setCodigoTercero(null);
                } else {

                    String codigoTercero = row.getCell(3).getStringCellValue();
                    clientesRepository.findExistByCodigoTercero(idData, codigoTercero)
                            .ifPresent(existe -> {
                                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CODIGO_TERCERO_YA_EXISTE);
                                detalle.setDetalle("El código tercero: " + codigoTercero);
                                listaErrores.add(detalle);
                            });
                    cliente.setCodigoTercero(codigoTercero);
                }


                if (row.getCell(4) == null) {
                    cliente.setWeb("");
                } else {
                    String webCelda = row.getCell(4).getStringCellValue();
                    cliente.setWeb(webCelda);
                }

                if (row.getCell(10) == null) {
                    cliente.setObservaciones("");
                } else {
                    String webObservaciones = row.getCell(10).getStringCellValue();
                    cliente.setObservaciones(webObservaciones);
                }

                cliente.setTipoClienteProveedor(null);
                if (row.getCell(11) == null) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_CLIENTE_NOT_FOUND);
                    listaErrores.add(detalle);
                } else {
                    String tipoCliente = row.getCell(11).getStringCellValue();
                    try {
                        cliente.setTipoClienteProveedor(TipoClienteProveedor.valueOf(tipoCliente));
                    } catch (Exception e) {
                        DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO);
                        listaErrores.add(detalle);
                    }
                }


                if (row.getCell(5) == null) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.DIRECCION_NOT_FOUND);
                    listaErrores.add(detalle);
                } else {
                    String direccionCelda = row.getCell(5).getStringCellValue();

                    String ciudadCelda = "";
                    if (row.getCell(6) != null) {
                        ciudadCelda = row.getCell(6).getStringCellValue();
                    }
                    String telefonosCelda = "";
                    if (row.getCell(7) != null) {
                        telefonosCelda = row.getCell(7).getStringCellValue();
                    }
                    String contactoCelda = "";
                    if (row.getCell(8) != null) {
                        contactoCelda = row.getCell(8).getStringCellValue();
                    }
                    String emailCelda = "";
                    if (row.getCell(9) != null) {
                        emailCelda = row.getCell(9).getStringCellValue();
                    }

                    cliente.setDireccion(direccionCelda);
                    cliente.setCiudad(ciudadCelda);
                    cliente.setTelefonos(telefonosCelda);
                    cliente.setContacto(contactoCelda);
                    cliente.setEmail(emailCelda);

                }


                if (row.getCell(19) != null && row.getCell(20) != null) {

                    if (Objects.equals(row.getCell(19).getStringCellValue(), "S")) {

                        GeTercerosTipoEntity tercerosClientes = new GeTercerosTipoEntity();

                        tercerosClientes.setIdTerceroTipo(UUID.randomUUID());
                        tercerosClientes.setTipo(TipoTercero.CLIENTE.getTipo());
                        tercerosClientes.setTercero(GeTerceroEntity.builder()
                                .idTercero(tokenIdTercero)
                                .build());

                        tercerosTipoClienteLista.add(tercerosClientes);
                    }

                    if (Objects.equals(row.getCell(20).getStringCellValue(), "S")) {

                        GeTercerosTipoEntity tercerosProveedores = new GeTercerosTipoEntity();

                        tercerosProveedores.setIdTerceroTipo(UUID.randomUUID());
                        tercerosProveedores.setTipo(TipoTercero.PROVEEDOR.getTipo());
                        tercerosProveedores.setTercero(GeTerceroEntity.builder()
                                .idTercero(tokenIdTercero)
                                .build());
                        tercerosTipoProveedorLista.add(tercerosProveedores);
                    }


                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.ES_TERCERO_ERROR);
                    listaErrores.add(detalle);
                }


                validarNuevaInformacion(cliente, row, listaErrores, linea);


                if (listaErrores.isEmpty()) {
                    tercerosLista.add(cliente);
                }

            }
        }


        long endTimeRead = System.currentTimeMillis();
        log.info("-> Reading finished, time " + (endTimeRead - startTimeRead) + " ms");

        log.info("-> Inserting");


        long startTimeWrite = System.currentTimeMillis();

        if (listaErrores.isEmpty()) {
            clientesRepository.saveAll(tercerosLista);
            geTercerosTipoRepository.saveAll(tercerosTipoClienteLista);
            geTercerosTipoRepository.saveAll(tercerosTipoProveedorLista);

        } else {
            List<String> list = listaErrores.stream().map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle()).toList();
            throw new ListErrorException(list);
        }

        long endTimeWrite = System.currentTimeMillis();
        log.info("-> Write finished, time " + (endTimeWrite - startTimeWrite) + " ms");
    }

    private void validarNuevaInformacion(GeTerceroEntity cliente, Row row, List<DetalleError> listaErrores, int linea) {

        if (row.getCell(12) == null) {
            cliente.setPais(null);
        } else {
            String codigoPais = row.getCell(12).getStringCellValue();
            if (!codigoPais.isEmpty()) {
                Optional<TbPaisEntity> pais = tbPaisesRepository.findById(codigoPais);
                if (pais.isPresent()) {
                    cliente.setPais(pais.get());
                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PAIS_NOT_EXIST);
                    detalle.setDetalle(MessageFormat.format("El codigo del país: {0} ", codigoPais));
                    listaErrores.add(detalle);
                }
            } else {
                cliente.setPais(null);
            }
        }

        if (row.getCell(13) == null) {
            cliente.setProvincia(null);
        } else {
            String codigoProvincia = row.getCell(13).getStringCellValue();
            if (!codigoProvincia.isEmpty()) {
                Optional<ProvinciaEntity> provincia = provinciaRepository.getForFindById(codigoProvincia);
                if (provincia.isPresent()) {
                    cliente.setProvincia(provincia.get());
                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PROVINCIA_NOT_EXIST);
                    detalle.setDetalle(MessageFormat.format("El codigo provincia: {0} ", codigoProvincia));
                    listaErrores.add(detalle);
                }
            } else {
                cliente.setProvincia(null);
            }


        }

        if (row.getCell(14) == null) {
            cliente.setCanton(null);
        } else {

            String codigoCanton = row.getCell(14).getStringCellValue();
            if (!codigoCanton.isEmpty()) {
                Optional<CantonEntity> canton = cantonRepository.getForFindById(codigoCanton);
                if (canton.isPresent()) {
                    cliente.setCanton(canton.get());
                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CANTON_NOT_EXIST);
                    detalle.setDetalle(MessageFormat.format("El codigo canton: {0} ", codigoCanton));
                    listaErrores.add(detalle);
                }
            } else {
                cliente.setCanton(null);
            }


        }


        if (row.getCell(15) == null) {
            cliente.setParroquia(null);
        } else {

            String codigoParroquia = row.getCell(15).getStringCellValue();
            if (!codigoParroquia.isEmpty()) {
                Optional<ParroquiaEntity> parroquia = parroquiaRepository.getForFindById(codigoParroquia);
                if (parroquia.isPresent()) {
                    cliente.setParroquia(parroquia.get());
                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PARROQUIA_NOT_EXIST);
                    detalle.setDetalle(MessageFormat.format("El codigo parroquia: {0} ", codigoParroquia));
                    listaErrores.add(detalle);
                }
            } else {
                cliente.setParroquia(null);
            }

        }

        if (row.getCell(16) == null) {
            cliente.setSexo(null);
        } else {
            String sexo = row.getCell(16).getStringCellValue();
            if (sexo.isEmpty()) {
                cliente.setSexo(null);
            } else {
                cliente.setSexo(SexoEnum.valueOf(sexo));
            }
        }


        if (row.getCell(17) == null) {
            cliente.setEstadoCivil(null);
        } else {
            String estadoCivil = row.getCell(17).getStringCellValue();
            if (estadoCivil.isEmpty()) {
                cliente.setEstadoCivil(null);
            } else {
                cliente.setEstadoCivil(EstadoCivilEnum.valueOf(estadoCivil));
            }

        }

        if (row.getCell(18) == null) {
            cliente.setOrigenIngresos(null);
        } else {
            String origen = row.getCell(18).getStringCellValue();
            if (origen.isEmpty()) {
                cliente.setOrigenIngresos(null);
            } else {
                cliente.setOrigenIngresos(OrigenIngresosEnum.valueOf(origen));
            }

        }

        validacionLocalidades(cliente, listaErrores, row, linea);


    }

    private void validacionLocalidades(GeTerceroEntity tercero, List<DetalleError> listaErrores, Row row, int linea) {


        if (Objects.nonNull(tercero.getProvincia()) && Objects.nonNull(tercero.getCanton())) {

            boolean existe = tercero.getProvincia().getCantones().stream()
                    .map(CantonEntity::getCodigoCanton)
                    .anyMatch(codigo -> codigo.equals(row.getCell(14).getStringCellValue()));

            if (!existe) {

                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CANTON_NOT_EXIST);
                detalle.setDetalle(MessageFormat.format("El codigo del canton {0}, no coincide con la provincia: {1}",
                        row.getCell(13).getStringCellValue(), tercero.getProvincia().getProvincia()));
                listaErrores.add(detalle);
            }

        }


        if (Objects.nonNull(tercero.getCanton()) && Objects.nonNull(tercero.getParroquia())) {

            boolean existe = tercero.getCanton().getParroquias().stream()
                    .map(ParroquiaEntity::getCodigoParroquia)
                    .anyMatch(codigo -> codigo.equals(row.getCell(15).getStringCellValue()));

            if (!existe) {
                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PARROQUIA_NOT_EXIST);
                detalle.setDetalle(MessageFormat.format("El codigo de la parroquia {0}, no coincide con el canton: {1}",
                        row.getCell(14).getStringCellValue(), tercero.getCanton().getCanton()));
                listaErrores.add(detalle);
            }

        }

    }


    public void cargarExcelTerceros(Long idData, MultipartFile file, String usuario) throws IOException {


        if (!ValidarTipoArchivo.validarTipoExcel(file)) {
            throw new GeneralException("El archivo debe ser Excel (.xls o .xlsx)");
        }

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> listaErrores = new ArrayList<>();

        List<GeTerceroEntity> tercerosLista = new ArrayList<>();
        List<GeTercerosTipoEntity> tercerosTipoClienteLista = new ArrayList<>();


        for (Sheet sheet : workbook) {
            boolean isHeader = true;
            for (Row row : sheet) {

                if (isRowEmpty(row)) continue;

                int linea = row.getRowNum() + 1;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                GeTerceroEntity tercero = new GeTerceroEntity();
                tercero.setIdTercero(UUID.randomUUID());
                tercero.setIdData(idData);
                tercero.setCreatedDate(LocalDateTime.now());
                tercero.setCreatedBy(usuario);

                if (Objects.nonNull(row.getCell(0))) {
                    String codigoTercero = row.getCell(0).getStringCellValue();

                    clientesRepository.findExistByCodigoTercero(idData, codigoTercero)
                            .ifPresent(existe -> {
                                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                                detalle.setDetalle("El código tercero: " + codigoTercero + " ya existe");
                                listaErrores.add(detalle);
                            });
                    tercero.setCodigoTercero(codigoTercero);
                } else {
                    tercero.setCodigoTercero(null);
                }


                if (Objects.nonNull(row.getCell(1)) && Objects.nonNull(row.getCell(2))) {

                    String tipoIdentificacionCelda = row.getCell(1).getStringCellValue();
                    String numeroIdentificacionCelda = row.getCell(2).getStringCellValue();

                    Optional<GeTerceroProjection> existe = clientesRepository.findExistByNumeroIdentificacion(tercero.getIdData(), numeroIdentificacionCelda);
                    if (existe.isPresent()) {
                        DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                        detalle.setDetalle("El número de identificación: " + numeroIdentificacionCelda + " ya existe");
                        listaErrores.add(detalle);
                        continue;
                    }

                    setearTipoYNumeroIdentificacionTercero(tercero, linea, listaErrores, tipoIdentificacionCelda, numeroIdentificacionCelda);

                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                    detalle.setDetalle("El tipo de identificación y número de identificación son obligatorios y deben estar completos");
                    listaErrores.add(detalle);
                }


                if (Objects.nonNull(row.getCell(3))) {
                    tercero.setTercero(row.getCell(3).getStringCellValue());
                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                    detalle.setDetalle("No se encontró el nombre del tercero");
                    listaErrores.add(detalle);
                }

                if (Objects.nonNull(row.getCell(4))) {
                    String tipoCliente = row.getCell(4).getStringCellValue();
                    if (!tipoCliente.isEmpty()) {
                        try {
                            tercero.setTipoClienteProveedor(TipoClienteProveedor.valueOf(tipoCliente));
                        } catch (Exception e) {
                            DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                            detalle.setDetalle("El tipo de cliente/proveedor : " + tipoCliente + " es incorrecto, debe ser NATURAL (N) o JURÍDICO (J)");
                            listaErrores.add(detalle);
                        }
                    } else {
                        tercero.setTipoClienteProveedor(null);
                    }


                }

                setearLocalidadesTercero(row, tercero, linea, listaErrores);

                if (Objects.nonNull(row.getCell(8))) {
                    tercero.setDireccion(row.getCell(8).getStringCellValue());
                } else {
                    tercero.setDireccion(null);
                }

                setearInformacionTercero(row, tercero, linea, listaErrores);


                if (Objects.nonNull(row.getCell(12))) {
                    tercero.setObservaciones(row.getCell(12).getStringCellValue());
                } else {
                    tercero.setObservaciones(null);
                }

                tercerosLista.add(tercero);
                setarTipoTerceroLista(tercero, tercerosTipoClienteLista);

            }

        }

        if (listaErrores.isEmpty()) {
            clientesRepository.saveAll(tercerosLista);
            geTercerosTipoRepository.saveAll(tercerosTipoClienteLista);
        } else {
            List<String> list = listaErrores.stream().map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle()).toList();
            throw new ListErrorException(list);
        }
    }

    private void setarTipoTerceroLista(GeTerceroEntity tercero, List<GeTercerosTipoEntity> tercerosTipoClienteLista) {

        GeTercerosTipoEntity tercerosClientes = new GeTercerosTipoEntity();

        tercerosClientes.setIdTerceroTipo(UUID.randomUUID());
        tercerosClientes.setTipo(TipoTercero.CLIENTE.getTipo());
        tercerosClientes.setTercero(GeTerceroEntity.builder()
                .idTercero(tercero.getIdTercero())
                .build());

        tercerosTipoClienteLista.add(tercerosClientes);
    }


    private void setearTipoYNumeroIdentificacionTercero(GeTerceroEntity tercero, int linea, List<DetalleError> listaErrores,
                                                        String tipoIdentificacionCelda, String numeroIdentificacionCelda) {

        switch (tipoIdentificacionCelda) {
            case "R": {
                try {
                    validarIdentificacion.validarRuc(numeroIdentificacionCelda);
                    tercero.setTipoIdentificacion("R");
                    tercero.setNumeroIdentificacion(numeroIdentificacionCelda);
                } catch (Exception exception) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                    detalle.setDetalle("El tipo Ruc debe: " + exception.getMessage());
                    listaErrores.add(detalle);
                }
                break;
            }

            case "C": {
                try {
                    validarIdentificacion.validarCedula(numeroIdentificacionCelda);
                    tercero.setTipoIdentificacion("C");
                    tercero.setNumeroIdentificacion(numeroIdentificacionCelda);
                } catch (Exception exception) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                    detalle.setDetalle("El tipo Cedula debe: " + exception.getMessage());
                    listaErrores.add(detalle);
                }
                break;
            }

            case "P": {
                tercero.setTipoIdentificacion("P");
                tercero.setNumeroIdentificacion(numeroIdentificacionCelda);
                break;
            }

            default: {
                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                detalle.setDetalle("El tipo de identificación: " + tipoIdentificacionCelda + " es incorrecto, debe ser R, C o P");
                listaErrores.add(detalle);
            }

        }
    }

    private void setearLocalidadesTercero(Row row, GeTerceroEntity tercero, int linea, List<DetalleError> listaErrores) {
        String codigoProvincia = "";
        String codigoCanton = "";

        if (Objects.nonNull(row.getCell(5)) && !row.getCell(5).getStringCellValue().isEmpty()) {
            codigoProvincia = row.getCell(5).getStringCellValue();
            if (!codigoProvincia.isEmpty()) {
                Optional<ProvinciaEntity> provincia = provinciaRepository.getForFindById(codigoProvincia);
                if (provincia.isPresent()) {
                    tercero.setProvincia(provincia.get());
                } else {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PROVINCIA_NOT_EXIST);
                    detalle.setDetalle(MessageFormat.format("El codigo provincia: {0} ", codigoProvincia));
                    listaErrores.add(detalle);
                }
            } else {
                tercero.setProvincia(null);
            }

        } else {
            tercero.setProvincia(null);
        }

        if (Objects.nonNull(row.getCell(6)) && !row.getCell(6).getStringCellValue().isEmpty()) {
            codigoCanton = row.getCell(6).getStringCellValue();

            if (Objects.nonNull(tercero.getProvincia())) {
                if (!codigoCanton.isEmpty()) {
                    String busquedaCodigoCanton = codigoProvincia + codigoCanton;
                    Optional<CantonEntity> canton = cantonRepository.getForFindById(busquedaCodigoCanton);
                    if (canton.isPresent()) {
                        tercero.setCanton(canton.get());
                    } else {
                        DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.CANTON_NOT_EXIST);
                        detalle.setDetalle(MessageFormat.format("El codigo canton: {0} ", busquedaCodigoCanton));
                        listaErrores.add(detalle);
                    }
                } else {
                    tercero.setCanton(null);
                }

            } else {
                tercero.setCanton(null);
            }

        } else {
            tercero.setCanton(null);
        }


        if (Objects.nonNull(row.getCell(7)) && !row.getCell(7).getStringCellValue().isEmpty()) {
            String codigoParroquia = row.getCell(7).getStringCellValue();

            if (Objects.nonNull(tercero.getCanton())) {
                if (!codigoParroquia.isEmpty()) {
                    String busquedaCodigoParroquia = codigoProvincia + codigoCanton + codigoParroquia;
                    Optional<ParroquiaEntity> parroquia = parroquiaRepository.getForFindById(busquedaCodigoParroquia);
                    if (parroquia.isPresent()) {
                        tercero.setParroquia(parroquia.get());
                    } else {
                        DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PARROQUIA_NOT_EXIST);
                        detalle.setDetalle(MessageFormat.format("El codigo parroquia: {0} ", busquedaCodigoParroquia));
                        listaErrores.add(detalle);
                    }
                } else {
                    tercero.setParroquia(null);
                }
            } else {
                tercero.setParroquia(null);
            }
        } else {
            tercero.setParroquia(null);
        }
    }

    private void setearInformacionTercero(Row row, GeTerceroEntity tercero, int linea, List<DetalleError> listaErrores) {

        tercero.setEstadoCivil(null);
        tercero.setSexo(null);
        tercero.setOrigenIngresos(null);

        if (Objects.nonNull(row.getCell(9))) {

            String sexo = row.getCell(9).getStringCellValue();
            if (!sexo.isEmpty()) {
                try {
                    tercero.setSexo(SexoEnum.valueOf(sexo));
                } catch (Exception e) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                    detalle.setDetalle("El sexo: " + sexo + " es incorrecto, debe ser M o F");
                    listaErrores.add(detalle);
                }

            }
        }


        if (Objects.nonNull(row.getCell(10))) {

            String estadoCivil = row.getCell(10).getStringCellValue();
            if (!estadoCivil.isEmpty()) {
                try {
                    tercero.setEstadoCivil(EstadoCivilEnum.valueOf(estadoCivil));
                } catch (Exception e) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TERCERO_ERROR);
                    detalle.setDetalle("El estado civil: " + estadoCivil + " es incorrecto, debe ser SOLTERO (S)," +
                            " CASADO (C), DIVORCIADO (D) o VIUDO (V), UNIÓN LIBRE (U)");
                    listaErrores.add(detalle);
                }

            }
        }

        if (Objects.nonNull(row.getCell(11))) {

            String origenIngresos = row.getCell(11).getStringCellValue();
            if (!origenIngresos.isEmpty()) {
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





