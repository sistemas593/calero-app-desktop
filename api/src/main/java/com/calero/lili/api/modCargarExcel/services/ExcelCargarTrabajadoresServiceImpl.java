package com.calero.lili.api.modCargarExcel.services;

import com.calero.lili.api.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.api.modLocalidades.modCantones.CantonRepository;
import com.calero.lili.api.modLocalidades.modProvincias.ProvinciaEntity;
import com.calero.lili.api.modLocalidades.modProvincias.ProvinciaRepository;
import com.calero.lili.api.modRRHH.modRRHHTrabajadores.TrabajadorEntity;
import com.calero.lili.api.modRRHH.modRRHHTrabajadores.TrabajadorRepository;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modTerceros.GeTercerosTipoEntity;
import com.calero.lili.api.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.api.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.api.tablas.tbPaises.TbPaisesRepository;
import com.calero.lili.api.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoTercero;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.utils.DateUtils;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelCargarTrabajadoresServiceImpl {


    private final TrabajadorRepository trabajadorRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final CantonRepository cantonRepository;
    private final ProvinciaRepository provinciaRepository;
    private final TbPaisesRepository tbPaisesRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final GeTercerosTipoRepository geTercerosTipoRepository;


    public void cargarTrabajadores(Long idData, MultipartFile file, Long idEmpresa) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<TrabajadorEntity> listItems = new ArrayList<>();

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


                TrabajadorEntity item = new TrabajadorEntity();
                item.setIdTrabajador(UUID.randomUUID());


                if (Objects.nonNull(row.getCell(3))) {

                    Optional<GeTerceroEntity> tercero = geTercerosRepository.getFindExistByNumeroIdentificacion(idData,
                            row.getCell(3).getStringCellValue());


                    if (tercero.isPresent()) {

                        Optional<TrabajadorEntity> trabajador = trabajadorRepository.getForFindByIdTercero(tercero.get().getIdTercero());

                        if (trabajador.isEmpty()) {
                            tercero.get().getGeTercerosTipoEntities().forEach(x -> {

                                if (x.getTipo().equals(TipoTercero.TRABAJADOR.getTipo())) {
                                    item.setTercero(tercero.get());
                                } else {
                                    GeTercerosTipoEntity geTercerosTipoEntity = new GeTercerosTipoEntity();
                                    geTercerosTipoEntity.setIdTerceroTipo(UUID.randomUUID());
                                    geTercerosTipoEntity.setTipo(TipoTercero.TRABAJADOR.getTipo());
                                    geTercerosTipoEntity.setTercero(tercero.get());

                                    geTercerosTipoRepository.save(geTercerosTipoEntity);
                                }
                            });

                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_IS_PRESENT));
                        }
                    } else {

                        GeTerceroEntity terceroEntity = new GeTerceroEntity();
                        terceroEntity.setIdTercero(UUID.randomUUID());
                        terceroEntity.setIdData(idData);

                        if (Objects.nonNull(row.getCell(3))) {
                            terceroEntity.setNumeroIdentificacion(row.getCell(3).getStringCellValue());
                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.NUMERO_IDENTIFICACION_NOT_FOUND));
                        }

                        if (Objects.nonNull(row.getCell(0)) && Objects.nonNull(row.getCell(1))) {
                            terceroEntity.setTercero(row.getCell(0).getStringCellValue() + row.getCell(1).getStringCellValue());
                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_NOMBRE_NOT_FOUND));
                        }

                        if (Objects.nonNull(row.getCell(2))) {
                            terceroEntity.setTipoIdentificacion(TipoIdentificacion.obtenerTipoIdentificacion(row.getCell(2).getStringCellValue()).name());
                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_TIPO_IDENTIFICACION_NOT_FOUND));
                        }

                        if (Objects.nonNull(row.getCell(4))) {
                            terceroEntity.setDireccion(row.getCell(4).getStringCellValue());
                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_DIRECCION_NOT_FOUND));
                        }


                        if (Objects.nonNull(row.getCell(6))) {
                            terceroEntity.setTelefonos(row.getCell(6).getStringCellValue());
                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_TELEFONO_NOT_FOUND));
                        }

                        if (Objects.nonNull(row.getCell(2))) {
                            terceroEntity.setEmail(row.getCell(7).getStringCellValue());
                        } else {
                            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_CORREO_NOT_FOUND));
                        }


                        if (detalleErrores.isEmpty()) {

                            GeTerceroEntity geTercero = geTercerosRepository.save(terceroEntity);
                            GeTercerosTipoEntity geTercerosTipoEntity = new GeTercerosTipoEntity();
                            geTercerosTipoEntity.setIdTerceroTipo(UUID.randomUUID());
                            geTercerosTipoEntity.setTipo(TipoTercero.TRABAJADOR.getTipo());
                            geTercerosTipoEntity.setTercero(geTercero);
                            geTercerosTipoRepository.save(geTercerosTipoEntity);

                            item.setTercero(geTercero);
                        } else {
                            throwErrors(detalleErrores);
                        }


                    }


                }


                if (Objects.isNull(row.getCell(8))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_CODIGO_SALARIO_NOT_FOUND));
                } else {
                    item.setCodigoSalario(row.getCell(8).getStringCellValue());
                }

                if (Objects.isNull(row.getCell(9))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_CODIGO_ESTAB_NOT_FOUND));
                } else {
                    item.setCodigoEstablecimiento(row.getCell(9).getStringCellValue());
                }

                if (Objects.isNull(row.getCell(10))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_APL_CONVENIO_NOT_FOUND));
                } else {
                    item.setAplicaConvenio(row.getCell(10).getStringCellValue());
                }

                if (Objects.isNull(row.getCell(11))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_TIPO_DISCAPACIDAD_NOT_FOUND));
                } else {

                    validarDiscapacidad(row, detalleErrores, item, linea);
                }


                if (Objects.isNull(row.getCell(15))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_BENEFICIO_PROV_GALAPAGOS_NOT_FOUND));
                } else {
                    item.setBeneficioProvGalapagos(row.getCell(15).getStringCellValue());
                }

                if (Objects.isNull(row.getCell(16))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_ENF_CASTROFICA_NOT_FOUND));
                } else {
                    item.setEnfermedadCatastrofica(row.getCell(16).getStringCellValue());
                }

                if (Objects.isNull(row.getCell(17))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_FECHA_INGRESO_NOT_FOUND));
                } else {
                    String fechaIngreso = row.getCell(17).getStringCellValue();
                    item.setFechaIngreso(fechaIngreso.isEmpty()
                            ? LocalDate.now()
                            : DateUtils.toLocalDate(fechaIngreso));
                }


                validarCanton(row, detalleErrores, item, linea);
                validarProvincia(row, detalleErrores, item, linea);
                validarPais(row, detalleErrores, item, linea);

                if (Objects.isNull(row.getCell(21))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_CODIGO_RESIDENCIA_NOT_FOUND));
                } else {
                    item.setCodigoResidencia(row.getCell(21).getStringCellValue());
                }


                if (Objects.isNull(row.getCell(22))) {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_ESTADO_NOT_FOUND));
                } else {
                    item.setEstado(Integer.parseInt(row.getCell(22).getStringCellValue()));
                }

                listItems.add(item);
            }
        }

        if (detalleErrores.isEmpty()) {
            trabajadorRepository.saveAll(listItems);
        } else {
            throwErrors(detalleErrores);
        }
    }


    private void validarDiscapacidad(Row row, List<DetalleError> detalleErrores, TrabajadorEntity item, int linea) {

        if (row.getCell(11).getStringCellValue().equals("01")
                || row.getCell(11).getStringCellValue().equals("02")) {
            item.setTipoDiscapacidad(row.getCell(11).getStringCellValue());
        } else {

            item.setTipoDiscapacidad(row.getCell(11).getStringCellValue());

            if (Objects.isNull(row.getCell(12))) {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_PORCENTAJE_DISCAPACIDAD_NOT_FOUND));
            } else {
                item.setPorcentajeDiscapacidad(Integer.parseInt(row.getCell(12).getStringCellValue()));
            }

            if (Objects.isNull(row.getCell(13))) {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_TIPO_ID_DISCAPACIDAD_NOT_FOUND));
            } else {
                item.setTipoIdDiscapacidad(row.getCell(13).getStringCellValue());
            }

            if (Objects.isNull(row.getCell(14))) {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_IDENTIFICACION_DISCAPACIDAD_NOT_FOUND));
            } else {
                item.setIdDiscapacidad(row.getCell(14).getStringCellValue());
            }

        }

    }


    private void validarCanton(Row row, List<DetalleError> detalleErrores, TrabajadorEntity item, int linea) {

        if (Objects.nonNull(row.getCell(18))) {
            CantonEntity canton = cantonRepository.getForFindById(row.getCell(18).getStringCellValue());
            if (Objects.nonNull(canton)) {
                item.setCanton(canton);
            } else {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_CANTON_NOT_FOUND));
            }
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.CANTON_NOT_FOUND));
        }

    }


    private void validarProvincia(Row row, List<DetalleError> detalleErrores, TrabajadorEntity item, int linea) {

        if (Objects.nonNull(row.getCell(19))) {
            ProvinciaEntity provincia = provinciaRepository.getForFindById(row.getCell(19).getStringCellValue());
            if (Objects.nonNull(provincia)) {
                item.setProvincia(provincia);
            } else {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.PROVINCIA_NOT_FOUND));
            }
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_PROVINCIA_NOT_FOUND));
        }

    }

    private void validarPais(Row row, List<DetalleError> detalleErrores, TrabajadorEntity item, int linea) {

        if (Objects.nonNull(row.getCell(20))) {
            TbPaisEntity pais = tbPaisesRepository.findById(row.getCell(20).getStringCellValue())
                    .orElseThrow(() -> null);
            if (Objects.nonNull(pais)) {
                item.setPais(pais);
            } else {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.PAIS_NOT_FOUND));
            }
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.TRABAJADOR_PAIS_NOT_FOUND));
        }
    }


    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription())
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

}
