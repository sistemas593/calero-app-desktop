package com.calero.lili.core.modCargarExcel.services;

import com.calero.lili.core.enums.EstadoCivilEnum;
import com.calero.lili.core.enums.OrigenIngresosEnum;
import com.calero.lili.core.enums.SexoEnum;
import com.calero.lili.core.enums.TipoClienteProveedor;
import com.calero.lili.core.errors.exceptions.GeneralException;
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
import com.calero.lili.core.utils.validaciones.ValidarIdentificacion;
import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
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

    public void carga(Long idData, MultipartFile file) throws IOException {

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
                    cliente.setWeb("");
                } else {
                    String webCelda = row.getCell(3).getStringCellValue();
                    cliente.setWeb(webCelda);
                }

                if (row.getCell(9) == null) {
                    cliente.setObservaciones("");
                } else {
                    String webObservaciones = row.getCell(9).getStringCellValue();
                    cliente.setObservaciones(webObservaciones);
                }

                cliente.setTipoClienteProveedor(null);
                if (row.getCell(10) == null) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO_CLIENTE_NOT_FOUND);
                    listaErrores.add(detalle);
                } else {
                    String tipoCliente = row.getCell(10).getStringCellValue();
                    try {
                        // TipoTerceroPerSoc.fromCodigo(tipoCliente);
                        cliente.setTipoClienteProveedor(TipoClienteProveedor.valueOf(tipoCliente));
                    } catch (Exception e) {
                        DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.TIPO);
                        listaErrores.add(detalle);
                    }
                }


                if (row.getCell(4) == null) {
                    DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.DIRECCION_NOT_FOUND);
                    listaErrores.add(detalle);
                } else {
                    String direccionCelda = row.getCell(4).getStringCellValue();

                    String ciudadCelda = "";
                    if (row.getCell(5) != null) {
                        ciudadCelda = row.getCell(7).getStringCellValue();
                    }
                    String telefonosCelda = "";
                    if (row.getCell(6) != null) {
                        telefonosCelda = row.getCell(8).getStringCellValue();
                    }
                    String contactoCelda = "";
                    if (row.getCell(7) != null) {
                        contactoCelda = row.getCell(7).getStringCellValue();
                    }
                    String emailCelda = "";
                    if (row.getCell(8) != null) {
                        emailCelda = row.getCell(8).getStringCellValue();
                    }

                    cliente.setDireccion(direccionCelda);
                    cliente.setCiudad(ciudadCelda);
                    cliente.setTelefonos(telefonosCelda);
                    cliente.setContacto(contactoCelda);
                    cliente.setEmail(emailCelda);

                }


                if (row.getCell(18) != null && row.getCell(19) != null) {

                    if (Objects.equals(row.getCell(19).getStringCellValue(), "S")) {

                        GeTercerosTipoEntity tercerosClientes = new GeTercerosTipoEntity();

                        tercerosClientes.setIdTerceroTipo(UUID.randomUUID());
                        tercerosClientes.setTipo(TipoTercero.CLIENTE.getTipo());
                        tercerosClientes.setTercero(GeTerceroEntity.builder()
                                .idTercero(tokenIdTercero)
                                .build());

                        tercerosTipoClienteLista.add(tercerosClientes);
                    }

                    if (Objects.equals(row.getCell(19).getStringCellValue(), "S")) {

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

        if (row.getCell(11) == null) {
            cliente.setPais(null);
        } else {
            String codigoPais = row.getCell(11).getStringCellValue();
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

        if (row.getCell(12) == null) {
            cliente.setProvincia(null);
        } else {
            String codigoProvincia = row.getCell(12).getStringCellValue();
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

        if (row.getCell(13) == null) {
            cliente.setCanton(null);
        } else {

            String codigoCanton = row.getCell(13).getStringCellValue();
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


        if (row.getCell(14) == null) {
            cliente.setParroquia(null);
        } else {

            String codigoParroquia = row.getCell(14).getStringCellValue();
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

        if (row.getCell(15) == null) {
            cliente.setSexo(null);
        } else {
            String sexo = row.getCell(15).getStringCellValue();
            if (sexo.isEmpty()) {
                cliente.setSexo(null);
            } else {
                cliente.setSexo(SexoEnum.valueOf(sexo));
            }
        }


        if (row.getCell(16) == null) {
            cliente.setEstadoCivil(null);
        } else {
            String estadoCivil = row.getCell(16).getStringCellValue();
            if (estadoCivil.isEmpty()) {
                cliente.setEstadoCivil(null);
            } else {
                cliente.setEstadoCivil(EstadoCivilEnum.valueOf(estadoCivil));
            }

        }

        if (row.getCell(17) == null) {
            cliente.setOrigenIngresos(null);
        } else {
            String origen = row.getCell(17).getStringCellValue();
            if (origen.isEmpty()) {
                cliente.setOrigenIngresos(null);
            } else {
                cliente.setOrigenIngresos(OrigenIngresosEnum.valueOf(origen));
            }

        }

        validacionLocalidades(cliente, listaErrores, row, linea);


    }

    private void validacionLocalidades(@NotNull GeTerceroEntity tercero, List<DetalleError> listaErrores, Row row, int linea) {


        if (Objects.nonNull(tercero.getProvincia()) && Objects.nonNull(tercero.getCanton())) {

            boolean existe = tercero.getProvincia().getCantones().stream()
                    .map(CantonEntity::getCodigoCanton)
                    .anyMatch(codigo -> codigo.equals(row.getCell(13).getStringCellValue()));

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
                    .anyMatch(codigo -> codigo.equals(row.getCell(14).getStringCellValue()));

            if (!existe) {
                DetalleError detalle = detalleErrorBuilder.builderDetalleError(linea, EnumError.PARROQUIA_NOT_EXIST);
                detalle.setDetalle(MessageFormat.format("El codigo de la parroquia {0}, no coincide con el canton: {1}",
                        row.getCell(14).getStringCellValue(), tercero.getCanton().getCanton()));
                listaErrores.add(detalle);
            }

        }

    }

}



