package com.calero.lili.core.modVentas.facturas;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoClienteProveedor;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesEntity;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosItemsRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.core.modTerceros.builder.GeTercerosTipoBuilder;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentas.projection.OneProjection;
import com.calero.lili.core.utils.DateUtils;
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
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VtVentasFacturasExcelService {


    private final VtVentasRepository vtVentasRepository;
    private final GeItemsRepository geItemsRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final GeTercerosTipoRepository geTercerosTipoRepository;
    private final GeTercerosTipoBuilder geTercerosTipoBuilder;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;
    private final GeImpuestosItemsRepository geImpuestosItemsRepository;


    public void cargarExcelFacturas(Long idData, Long idEmpresa,
                                    MultipartFile file, String usuario, String sucursal) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<VtVentaEntity> facturas = new ArrayList<>();


        Optional<AdEmpresasSucursalesEntity> sucursalEntity = adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, sucursal);

        if (sucursalEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("La sucursal {0} no existe ", sucursal));
        }

        boolean isHeader = true;
        for (Sheet sheet : workbook) {

            for (Row row : sheet) {

                if (isRowEmpty(row)) {
                    continue;
                }

                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                VtVentaEntity factura = new VtVentaEntity();
                factura.setIdVenta(UUID.randomUUID());
                factura.setIdData(idData);
                factura.setIdEmpresa(idEmpresa);
                factura.setTipoVenta(TipoVenta.FAC.name());
                factura.setCreatedDate(LocalDateTime.now());
                factura.setCreatedBy(usuario);

                factura.setAmbiente(1);
                factura.setCodigoDocumento("18");
                factura.setAnulada(Boolean.FALSE);
                factura.setFleteInternacional(new BigDecimal("0.00"));
                factura.setFormatoDocumento(FormatoDocumento.E);
                factura.setGastosAduaneros(new BigDecimal("0.00"));
                factura.setGastosTransporteOtros(new BigDecimal("0.00"));
                factura.setLiquidar("S");
                factura.setNumeroItems(1);
                factura.setSeguroInternacional(new BigDecimal("0.00"));
                factura.setTipoEmision(1);
                factura.setTipoIngreso("VL");
                factura.setSucursal(sucursal);
                factura.setExisteComprobante(Boolean.FALSE);

                cabeceraFactura(idData, idEmpresa, row, factura, detalleErrores, linea);
                detalleFactura(idData, idEmpresa, row, factura, detalleErrores, linea);

                facturas.add(factura);
            }

        }

        if (detalleErrores.isEmpty()) {
            vtVentasRepository.saveAll(facturas);
        } else {
            throwErrors(detalleErrores);
        }


    }


    private void detalleFactura(Long idData, Long idEmpresa, Row row, VtVentaEntity factura, List<DetalleError> detalleErrores, int linea) {

        List<VtVentaDetalleEntity> detalles = new ArrayList<>();

        if (Objects.nonNull(row.getCell(11)) && Objects.nonNull(row.getCell(12)) &&
                Objects.nonNull(row.getCell(16)) && Objects.nonNull(row.getCell(18)) &&
                Objects.nonNull(row.getCell(19)) && Objects.nonNull(row.getCell(20)) &&
                Objects.nonNull(row.getCell(21))) {

            VtVentaDetalleEntity detalle = new VtVentaDetalleEntity();

            detalle.setIdVentaDetalle(UUID.randomUUID());
            detalle.setCodigoPrincipal(row.getCell(11).getStringCellValue());
            detalle.setDescripcion(row.getCell(12).getStringCellValue());
            detalle.setCantidad(new BigDecimal(row.getCell(16).getStringCellValue()));
            detalle.setPrecioUnitario(new BigDecimal(row.getCell(18).getStringCellValue()));
            detalle.setDescuento(new BigDecimal(row.getCell(19).getStringCellValue()));
            setearDetalleAdicional(row, linea, detalle, detalleErrores);

            validarValores(factura, row, linea, detalleErrores, detalle);
            Optional<GeItemEntity> itemEntity = geItemsRepository.findByCodigoItem(idData, idEmpresa, row.getCell(11).getStringCellValue());

            if (itemEntity.isPresent()) {
                detalle.setItems(itemEntity.get());
            } else {
                GeItemEntity item = new GeItemEntity();
                item.setIdItem(UUID.randomUUID());
                item.setIdData(idData);
                item.setIdEmpresa(idEmpresa);
                item.setCodigoPrincipal(row.getCell(11).getStringCellValue());
                item.setDescripcion(row.getCell(12).getStringCellValue());
                setearDetalleAdicionalItem(row, linea, item, detalleErrores);
                detalle.setItems(geItemsRepository.save(item));

            }

            detalles.add(detalle);
        }

        factura.setDetalle(detalles);

    }

    private void validarValores(VtVentaEntity factura, Row row, int linea,
                                List<DetalleError> detalleErrores, VtVentaDetalleEntity detalle) {

        List<VtVentaValoresEntity> valoresEntity = new ArrayList<>();

        BigDecimal subtotal = new BigDecimal(row.getCell(16).getStringCellValue())
                .multiply(new BigDecimal(row.getCell(18).getStringCellValue()));

        detalle.setSubtotalItem(subtotal);
        factura.setSubtotal(subtotal);
        factura.setTotalDescuento(new BigDecimal(row.getCell(19).getStringCellValue()));
        BigDecimal subTotalDescuento = subtotal.subtract(factura.getTotalDescuento());

        String clave = row.getCell(20).getStringCellValue() + "-" + row.getCell(21).getStringCellValue();

        Optional<GeImpuestosEntity> impuesto = geImpuestosItemsRepository.findCodigoAndCodigoPorcentaje(clave);

        if (impuesto.isPresent()) {

            VtVentaValoresEntity valor = new VtVentaValoresEntity();
            BigDecimal valorImpuesto = subtotal
                    .multiply(impuesto.get().getTarifa())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            valor.setIdVentaValores(UUID.randomUUID());
            valor.setBaseImponible(subtotal);
            valor.setTarifa(impuesto.get().getTarifa());
            valor.setCodigo(impuesto.get().getCodigo());
            valor.setCodigoPorcentaje(impuesto.get().getCodigoPorcentaje());
            valor.setValor(valorImpuesto);
            valor.setIdData(factura.getIdData());
            valor.setIdEmpresa(factura.getIdEmpresa());

            factura.setTotal(subTotalDescuento.add(valorImpuesto));
            factura.setTotalImpuesto(valorImpuesto);

            List<VtVentaDetalleEntity.Impuestos> impuestos = new ArrayList<>();

            VtVentaDetalleEntity.Impuestos impuestoDetalle = VtVentaDetalleEntity.Impuestos.builder()
                    .codigo(valor.getCodigo())
                    .codigoPorcentaje(valor.getCodigoPorcentaje())
                    .tarifa(valor.getTarifa())
                    .baseImponible(valor.getBaseImponible())
                    .valor(valor.getValor())
                    .build();

            impuestos.add(impuestoDetalle);
            detalle.setImpuesto(impuestos);

            valoresEntity.add(valor);
            factura.setValoresEntity(valoresEntity);


        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_IMPUESTO_ERROR);
            detalleError.setDetalle("Codigo Impuesto : " + row.getCell(20).getStringCellValue() + " Codigo Porcentaje: " + row.getCell(21).getStringCellValue());
            detalleErrores.add(detalleError);
        }


    }

    private void cabeceraFactura(Long idData, Long idEmpresa, Row row, VtVentaEntity
            factura, List<DetalleError> detalleErrores, int linea) {

        factura.setSubtotal(BigDecimal.ZERO);
        factura.setTotalDescuento(BigDecimal.ZERO);
        factura.setTotalImpuesto(BigDecimal.ZERO);
        factura.setTotal(BigDecimal.ZERO);


        if (Objects.nonNull(row.getCell(0)) && Objects.nonNull(row.getCell(1))) {
            Optional<OneProjection> x = vtVentasRepository.findExistBySecuencial(idData, idEmpresa, TipoVenta.FAC.name(),
                    row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());

            if (x.isEmpty()) {
                factura.setSerie(row.getCell(0).getStringCellValue());
                factura.setSecuencial(row.getCell(1).getStringCellValue());
            } else {
                detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_EXISTS));
            }
        }

        if (Objects.nonNull(row.getCell(2))) {

            String fechaEmision = row.getCell(2).getStringCellValue();
            LocalDate fecha = DateUtils.toLocalDate(fechaEmision);

            if (!fecha.isEqual(LocalDate.now())) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_FECHA_EMISION);
                detalleError.setDetalle("Fecha del excel: " + fechaEmision + " Fecha actual: " + DateUtils.toString(LocalDate.now()));
                detalleErrores.add(detalleError);
                return;
            }
            factura.setFechaEmision(DateUtils.toLocalExcelDateTimeFechaDesde(fechaEmision));
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_FECHA_EMISION_NOT_FOUND));
        }


        if (Objects.nonNull(row.getCell(6))) {
            String relacionado = null;
            if (row.getCell(6).getStringCellValue().equals("VERDADERO")) {
                relacionado = "S";
            } else {
                relacionado = "N";
            }
            factura.setRelacionado(relacionado);
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_RELACIONADO_NOT_FOUND));
        }


        guardarInfoCliente(idData, row, factura, detalleErrores, linea);
        guardarInfoAdicional(row, factura, detalleErrores, linea);


    }


    private void guardarInfoCliente(Long idData, Row row, VtVentaEntity
            factura, List<DetalleError> detalleErrores, int linea) {

        if (Objects.nonNull(row.getCell(4)) && Objects.nonNull(row.getCell(3)) &&
                Objects.nonNull(row.getCell(5)) && Objects.nonNull(row.getCell(7)) &&
                Objects.nonNull(row.getCell(10)) && Objects.nonNull(row.getCell(8)) && Objects.nonNull(row.getCell(9))) {

            factura.setEmail(row.getCell(10).getStringCellValue());

            Optional<GeTerceroEntity> cliente = geTercerosRepository
                    .getFindExistByNumeroIdentificacion(idData, row.getCell(4).getStringCellValue());

            if (cliente.isPresent()) {
                factura.setTercero(cliente.get());
            } else {
                GeTerceroEntity tercero = new GeTerceroEntity();

                tercero.setIdData(idData);
                tercero.setIdTercero(UUID.randomUUID());
                tercero.setNumeroIdentificacion(row.getCell(4).getStringCellValue());
                tercero.setTipoIdentificacion(TipoIdentificacion.obtenerTipoIdentificacion(row.getCell(3).getStringCellValue()).name());
                tercero.setTercero(row.getCell(7).getStringCellValue());
                tercero.setDireccion(row.getCell(8).getStringCellValue());
                tercero.setEmail(row.getCell(10).getStringCellValue());
                tercero.setTelefonos(row.getCell(9).getStringCellValue());
                tercero.setTipoClienteProveedor(TipoClienteProveedor.valueOf(row.getCell(5).getStringCellValue()));
                tercero.setCreatedBy(factura.getCreatedBy());
                tercero.setCreatedDate(LocalDateTime.now());

                GeTerceroEntity terceroEntity = geTercerosRepository.save(tercero);
                saveTipoTercero(terceroEntity);
                factura.setTercero(terceroEntity);
            }
        } else {
            detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_INFORMACION_CLIENTE_NOT_FOUND));
        }

    }

    private void saveTipoTercero(GeTerceroEntity terceroEntity) {
        geTercerosTipoRepository.save(geTercerosTipoBuilder.builderClienteEntity(terceroEntity));
    }


    private void guardarInfoAdicional(Row row, VtVentaEntity factura, List<DetalleError> detalleErrores,
                                      int linea) {

        List<InformacionAdicional> lista = new ArrayList<>();

        for (int columna = 27; columna <= 41; columna++) {

            if (Objects.isNull(row.getCell(columna))) {
                continue;
            }

            String valorCelda = row.getCell(columna).getStringCellValue();

            if (!valorCelda.isEmpty()) {
                String[] partes = valorCelda.split(":", 2);

                String nombre = partes[0].trim();
                String valor = partes[1].trim();

                if (nombre.isEmpty() || valor.isEmpty()) {
                    continue;
                }

                InformacionAdicional info = new InformacionAdicional();
                info.setNombre(nombre);
                info.setValor(valor);
                lista.add(info);
            }


        }

        if (!lista.isEmpty()) {
            factura.setInformacionAdicional(lista);
        }

    }


    private void setearDetalleAdicionalItem(Row row, int linea, GeItemEntity item, List<DetalleError> detalleErrores) {

        List<GeItemEntity.DetalleAdicional> lista = new ArrayList<>();

        for (int columna = 13; columna <= 15; columna++) {

            if (Objects.isNull(row.getCell(columna))) {
                continue;
            }

            String valorCelda = row.getCell(columna).getStringCellValue();

            if (!valorCelda.isEmpty()) {
                String[] partes = valorCelda.split(":", 2);

                String nombre = partes[0].trim();
                String valor = partes[1].trim();

                if (nombre.isEmpty() || valor.isEmpty()) {
                    continue;
                }
                GeItemEntity.DetalleAdicional detalle = new GeItemEntity.DetalleAdicional();
                detalle.setNombre(nombre);
                detalle.setValor(valor);

                lista.add(detalle);
            }


        }

        if (!lista.isEmpty()) {
            item.setDetallesAdicionales(lista);
        }

    }

    private void setearDetalleAdicional(Row row, int linea, VtVentaDetalleEntity detalle, List<DetalleError> detalleErrores) {

        List<VtVentaDetalleEntity.DetalleAdicional> lista = new ArrayList<>();

        for (int columna = 13; columna <= 15; columna++) {

            if (Objects.isNull(row.getCell(columna))) {
                continue;
            }

            String valorCelda = row.getCell(columna).getStringCellValue();

            if (!valorCelda.isEmpty()) {
                String[] partes = valorCelda.split(":", 2);

                String nombre = partes[0].trim();
                String valor = partes[1].trim();

                if (nombre.isEmpty() || valor.isEmpty()) {
                    continue;
                }

                VtVentaDetalleEntity.DetalleAdicional adicional = new VtVentaDetalleEntity.DetalleAdicional();

                adicional.setNombre(nombre);
                adicional.setValor(valor);
                lista.add(adicional);
            }

        }

        if (!lista.isEmpty()) {
            detalle.setDetAdicional(lista);
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


    public void cargarExcelVentasImpuestos(Long idData, Long idEmpresa,
                                           MultipartFile file, String usuario, String sucursal) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<VtVentaEntity> facturas = new ArrayList<>();


        Optional<AdEmpresasSucursalesEntity> sucursalEntity = adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, sucursal);

        if (sucursalEntity.isEmpty()) {
            throw new GeneralException(MessageFormat.format("La sucursal {0} no existe ", sucursal));
        }

        boolean isHeader = true;
        for (Sheet sheet : workbook) {

            for (Row row : sheet) {

                if (isRowEmpty(row)) {
                    continue;
                }

                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                VtVentaEntity factura = new VtVentaEntity();
                factura.setIdVenta(UUID.randomUUID());
                factura.setIdData(idData);
                factura.setIdEmpresa(idEmpresa);
                factura.setCreatedDate(LocalDateTime.now());
                factura.setCreatedBy(usuario);

                factura.setAmbiente(1);
                factura.setCodigoDocumento("18");
                factura.setAnulada(Boolean.FALSE);
                factura.setFleteInternacional(new BigDecimal("0.00"));
                factura.setFormatoDocumento(FormatoDocumento.E);
                factura.setGastosAduaneros(new BigDecimal("0.00"));
                factura.setGastosTransporteOtros(new BigDecimal("0.00"));
                factura.setLiquidar("S");
                factura.setNumeroItems(1);
                factura.setSeguroInternacional(new BigDecimal("0.00"));
                factura.setTipoEmision(1);
                factura.setExisteComprobante(Boolean.FALSE);
                factura.setComprobante(null);


                if (Objects.nonNull(row.getCell(2)) && Objects.nonNull(row.getCell(3))) {
                    Optional<OneProjection> x = vtVentasRepository.findExistBySecuencial(idData, idEmpresa, TipoVenta.FAC.name(),
                            row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());

                    if (x.isEmpty()) {
                        factura.setSerie(row.getCell(2).getStringCellValue());
                        factura.setSecuencial(row.getCell(3).getStringCellValue());
                    } else {
                        detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_EXISTS));
                    }
                }

                if (Objects.nonNull(row.getCell(0))) {

                    String tipoVenta = row.getCell(0).getStringCellValue();

                    String[] tipos = {"FAC", "NDB", "NCR"};
                    if (Arrays.asList(tipos).contains(tipoVenta)) {
                        factura.setTipoVenta(tipoVenta);
                    } else {
                        DetalleError detalleError = detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_IMPUESTO_NO_TIPO_DOCUMENTO);
                        detalleError.setDetalle("El tipo de venta:" + tipoVenta + " , no corresponde a ningún tipo de ventas");
                        detalleErrores.add(detalleError);
                    }

                } else {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_IMPUESTO_NO_TIPO_DOCUMENTO));
                }


                if (Objects.nonNull(row.getCell(1))) {

                    String tipoDocumento = row.getCell(1).getStringCellValue();
                    if (tipoDocumento.equals("S")) {
                        factura.setFormatoDocumento(FormatoDocumento.E);
                        factura.setEstadoDocumento(EstadoDocumento.AUT);
                    } else if (tipoDocumento.equals("N")) {
                        factura.setFormatoDocumento(FormatoDocumento.F);
                    }

                } else {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_IMPUESTO_NO_FORMATO_DOCUMENTO));
                }

                if (Objects.nonNull(row.getCell(4))) {
                    factura.setTipoVenta(row.getCell(4).getStringCellValue());
                } else {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_IMPUESTO_NO_TIPO_VENTA));
                }


                if (Objects.nonNull(row.getCell(5)) && Objects.nonNull(row.getCell(6))
                        && Objects.nonNull(row.getCell(7)) && Objects.nonNull(row.getCell(9))) {

                    Optional<GeTerceroEntity> cliente = geTercerosRepository
                            .getFindExistByNumeroIdentificacion(idData, row.getCell(5).getStringCellValue());

                    if (cliente.isPresent()) {
                        factura.setTercero(cliente.get());
                    } else {
                        GeTerceroEntity tercero = new GeTerceroEntity();

                        String tipoIdentificacion = row.getCell(7).getStringCellValue();
                        if (!tipoIdentificacion.contains("0")) {
                            tipoIdentificacion = "0" + tipoIdentificacion;
                        }

                        String tipoCliente = row.getCell(9).getStringCellValue();
                        if (!tipoCliente.isEmpty()) {
                            tercero.setTipoClienteProveedor(TipoClienteProveedor.valueOf(tipoCliente));
                        } else {
                            tercero.setTipoClienteProveedor(null);
                        }
                        tercero.setIdData(idData);
                        tercero.setIdTercero(UUID.randomUUID());
                        tercero.setNumeroIdentificacion(row.getCell(5).getStringCellValue());
                        tercero.setTipoIdentificacion(TipoIdentificacion.obtenerTipoIdentificacion(tipoIdentificacion).name());
                        tercero.setTercero(row.getCell(6).getStringCellValue());
                        tercero.setDireccion("");
                        tercero.setEmail("");
                        tercero.setTelefonos("");
                        tercero.setCreatedBy(factura.getCreatedBy());
                        tercero.setCreatedDate(LocalDateTime.now());

                        GeTerceroEntity terceroEntity = geTercerosRepository.save(tercero);
                        saveTipoTercero(terceroEntity);
                        factura.setTercero(terceroEntity);

                    }
                } else {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_IMPUESTO_INFORMACION_CLIENTE_NOT_FOUND));
                }

                if (Objects.nonNull(row.getCell(8))) {
                    factura.setRelacionado(row.getCell(8).getStringCellValue());
                } else {
                    detalleErrores.add(detalleErrorBuilder.builderDetalleError(linea, EnumError.FACTURA_RELACIONADO_NOT_FOUND));
                }

                valoresFacturaImpuesto(idData, idEmpresa, factura, row);
                facturas.add(factura);

            }

            if (detalleErrores.isEmpty()) {
                vtVentasRepository.saveAll(facturas);
            } else {
                throwErrors(detalleErrores);
            }

        }
    }

    private void valoresFacturaImpuesto(Long idData, Long idEmpresa,
                                        VtVentaEntity factura, Row row) {

        List<VtVentaValoresEntity> valores = new ArrayList<>();

        // NO OBJETO y CERO
        if (Objects.nonNull(row.getCell(11)) && Objects.nonNull(row.getCell(12))) {

            BigDecimal valorNoObjeto = new BigDecimal(row.getCell(11).getStringCellValue());
            if (!valorNoObjeto.equals(BigDecimal.ZERO)) {
                VtVentaValoresEntity noObjeto = new VtVentaValoresEntity();
                noObjeto.setCodigo("2");
                noObjeto.setCodigoPorcentaje("6");
                noObjeto.setBaseImponible(valorNoObjeto);
                noObjeto.setTarifa(new BigDecimal("0.00"));
                noObjeto.setValor(new BigDecimal("0.00"));
                noObjeto.setIdData(idData);
                noObjeto.setIdEmpresa(idEmpresa);
                valores.add(noObjeto);
            }

            BigDecimal valorCero = new BigDecimal(row.getCell(12).getStringCellValue());
            if (!valorCero.equals(BigDecimal.ZERO)) {
                VtVentaValoresEntity cero = new VtVentaValoresEntity();
                cero.setCodigo("2");
                cero.setCodigoPorcentaje("7");
                cero.setBaseImponible(valorCero);
                cero.setTarifa(new BigDecimal("0.00"));
                cero.setValor(new BigDecimal("0.00"));
                cero.setIdData(idData);
                cero.setIdEmpresa(idEmpresa);
                valores.add(cero);
            }

        }

        // BASE 5
        if (Objects.nonNull(row.getCell(13)) && Objects.nonNull(row.getCell(14))) {

            BigDecimal valorIva5 = new BigDecimal(row.getCell(14).getStringCellValue());
            BigDecimal valorBase5 = new BigDecimal(row.getCell(13).getStringCellValue());

            if (!valorIva5.equals(BigDecimal.ZERO) && !valorBase5.equals(BigDecimal.ZERO)) {
                VtVentaValoresEntity valor = new VtVentaValoresEntity();

                valor.setCodigo("2");
                valor.setCodigoPorcentaje("5");
                valor.setTarifa(new BigDecimal("5.00"));
                valor.setBaseImponible(valorBase5);
                valor.setValor(valorIva5);
                valor.setIdEmpresa(idData);
                valor.setIdEmpresa(idEmpresa);
                valores.add(valor);
            }
        }

        // BASE 8
        if (Objects.nonNull(row.getCell(15)) && Objects.nonNull(row.getCell(16))) {

            BigDecimal valorIva8 = new BigDecimal(row.getCell(16).getStringCellValue());
            BigDecimal valorBase8 = new BigDecimal(row.getCell(15).getStringCellValue());

            if (!valorIva8.equals(BigDecimal.ZERO) && !valorBase8.equals(BigDecimal.ZERO)) {
                VtVentaValoresEntity valor = new VtVentaValoresEntity();

                valor.setCodigo("2");
                valor.setCodigoPorcentaje("8");
                valor.setTarifa(new BigDecimal("8.00"));
                valor.setBaseImponible(valorBase8);
                valor.setValor(valorIva8);
                valor.setIdEmpresa(idData);
                valor.setIdEmpresa(idEmpresa);
                valores.add(valor);
            }

        }


        if (Objects.nonNull(row.getCell(17)) && Objects.nonNull(row.getCell(18))) {

            BigDecimal valorIva15 = new BigDecimal(row.getCell(18).getStringCellValue());
            BigDecimal valorBase15 = new BigDecimal(row.getCell(17).getStringCellValue());

            if (!valorIva15.equals(BigDecimal.ZERO) && !valorBase15.equals(BigDecimal.ZERO)) {
                VtVentaValoresEntity valor = new VtVentaValoresEntity();

                valor.setCodigo("2");
                valor.setCodigoPorcentaje("4");
                valor.setTarifa(new BigDecimal("15.00"));
                valor.setBaseImponible(valorBase15);
                valor.setValor(valorIva15);
                valor.setIdEmpresa(idData);
                valor.setIdEmpresa(idEmpresa);
                valores.add(valor);
            }

        }

        factura.setValoresEntity(valores);
    }

}
