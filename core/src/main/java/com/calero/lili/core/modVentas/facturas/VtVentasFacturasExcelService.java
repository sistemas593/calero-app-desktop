package com.calero.lili.core.modVentas.facturas;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTerceros.GeTercerosTipoRepository;
import com.calero.lili.core.modTerceros.builder.GeTercerosTipoBuilder;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VtVentasFacturasExcelService {


    // validar los campos de tercero tipo no se estan guardando y al guardar no duplicar los tipos.


    private final VtVentasRepository vtVentasRepository;
    private final GeItemsRepository geItemsRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final GeTercerosTipoRepository geTercerosTipoRepository;
    private final GeTercerosTipoBuilder geTercerosTipoBuilder;


    public void cargarExcelFacturas(Long idData, Long idEmpresa, MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

        List<DetalleError> detalleErrores = new ArrayList<>();
        List<VtVentaEntity> facturas = new ArrayList<>();

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
            factura.setFechaEmision(DateUtils.toLocalDate(row.getCell(2).getStringCellValue()));
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


            factura.setNumeroIdentificacion(row.getCell(4).getStringCellValue());
            factura.setTipoIdentificacion(TipoIdentificacion.obtenerTipoIdentificacion(row.getCell(3).getStringCellValue()).name());
            factura.setTipoCliente(row.getCell(5).getStringCellValue());
            factura.setTerceroNombre(row.getCell(7).getStringCellValue());
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
