package com.calero.lili.core.modVentas;

import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosDetalleEntity;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modAsientos.builder.CnAsientosBuilder;
import com.calero.lili.core.modContabilidad.modEnlances.CnEnlacesGeneralesEntity;
import com.calero.lili.core.modContabilidad.modEnlances.CnEnlacesGeneralesRepository;
import com.calero.lili.core.modTerceros.GeTercerosGruposClientesEntity;
import com.calero.lili.core.modTerceros.GeTercerosGruposClientesRepository;
import com.calero.lili.core.modVentasCientesGrupos.VtClienteGrupoEntity;
import com.calero.lili.core.modVentasCientesGrupos.VtClientesGruposRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GenerarAsientoServiceImpl {


    private final CnAsientosBuilder cnAsientosBuilder;
    private final GeTercerosGruposClientesRepository geTercerosGruposClientesRepository;
    private final VtClientesGruposRepository vtClientesGruposRepository;
    private final CnEnlacesGeneralesRepository cnEnlacesGeneralesRepository;

    public CnAsientosEntity generarAsiento(Long idData, Long idEmpresa, VtVentaEntity venta) {





        List<CnAsientosDetalleEntity> detalles = new ArrayList<>();

        CnAsientosEntity asiento = cnAsientosBuilder.builderAsientoVenta(venta);
        CnAsientosDetalleEntity detalleCredito = getAsientoDetalleCredito(idData, idEmpresa, venta);

        Map<UUID, List<VtVentaDetalleEntity>> agrupados =
                venta.getDetalle().stream()
                        .collect(Collectors.groupingBy(
                                det -> det.getItems()
                                        .getGrupos()
                                        .getIdCuentaIngreso()
                        ));

        AtomicInteger contador = new AtomicInteger(1);

        agrupados.forEach((idCuenta, lista) -> {

            BigDecimal total = lista.stream()
                    .map(VtVentaDetalleEntity::getSubtotalItem)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            VtVentaDetalleEntity referencia = lista.get(0);

            CnAsientosDetalleEntity detalleAsiento = cnAsientosBuilder
                    .builderDetalleAsientoVentaAgrupado(referencia, venta, total);

            detalleAsiento.setCuenta(cnAsientosBuilder.builderCuentaIngreso(idCuenta));
            detalleAsiento.setItemOrden(contador.getAndIncrement());

            detalles.add(detalleAsiento);


        });

        if (Objects.nonNull(detalleCredito)) {
            detalleCredito.setItemOrden(contador.getAndIncrement());
            detalles.add(detalleCredito);
        }

        List<CnAsientosDetalleEntity> todosDetalles = detallesAsientoValores(venta, detalles, contador);

        asiento.setDetalleEntity(todosDetalles);
        return asiento;
    }


    private CnAsientosDetalleEntity getAsientoDetalleCredito(Long idData, Long idEmpresa, VtVentaEntity model) {

        // TODO SI LA FACTURA ES DE CONTADO, BUSCAR EN LOS DETALLES DE LOS MOVIMIENTOS DE CAJAS DE BANCOS (PENDIENTE)

        CnAsientosDetalleEntity detalle = new CnAsientosDetalleEntity();

        if (model.getFormaPago().equals(FormaPago.CR)) {

            Optional<GeTercerosGruposClientesEntity> tercerosGruposCliente = geTercerosGruposClientesRepository
                    .findByDataTercero(idData, idEmpresa, model.getTercero().getIdTercero());

            if (tercerosGruposCliente.isPresent()) {

                detalle.setIdAsientoDetalle(UUID.randomUUID());
                detalle.setIdData(idData);
                detalle.setIdEmpresa(idEmpresa);
                detalle.setDetalle(model.getConcepto());
                detalle.setTipoDocumento(model.getTipoVenta());
                detalle.setNumeroDocumento(model.getSerie() + "-" + model.getSecuencial());
                detalle.setFechaDocumento(model.getFechaEmision());
                detalle.setDebe(model.getTotal());
                detalle.setHaber(BigDecimal.ZERO);
                //detalle.setGeItem();
                detalle.setTercero(model.getTercero());
                detalle.setCuenta(tercerosGruposCliente.get().getGrupo().getCuentaCredito());
                //detalle.setCentroCostos();


            } else {
                Optional<VtClienteGrupoEntity> predeterminado = vtClientesGruposRepository
                        .findByIdPredeterminado(idData, idEmpresa, Boolean.TRUE);

                if (predeterminado.isPresent()) {

                    detalle.setIdAsientoDetalle(UUID.randomUUID());
                    detalle.setIdData(idData);
                    detalle.setIdEmpresa(idEmpresa);
                    detalle.setDetalle(model.getConcepto());
                    detalle.setTipoDocumento(model.getTipoVenta());
                    detalle.setNumeroDocumento(model.getSerie() + "-" + model.getSecuencial());
                    detalle.setFechaDocumento(model.getFechaEmision());
                    detalle.setDebe(model.getTotal());
                    detalle.setHaber(BigDecimal.ZERO);
                    //detalle.setGeItem(detalle.getItems());
                    detalle.setTercero(model.getTercero());
                    detalle.setCuenta(predeterminado.get().getCuentaCredito());
                    //detalle.setCentroCostos();
                }
            }

            return detalle;
        }

        return null;
    }


    private List<CnAsientosDetalleEntity> detallesAsientoValores(VtVentaEntity model,
                                                                 List<CnAsientosDetalleEntity> detalles, AtomicInteger contador) {

        model.getValoresEntity().forEach(item -> {

            if (item.getCodigoPorcentaje().equals("4") && item.getCodigo().equals("2")) {
                CnAsientosDetalleEntity detalle = new CnAsientosDetalleEntity();

                CnEnlacesGeneralesEntity enlace = cnEnlacesGeneralesRepository.findByCodigo("IVA_VT_15")
                        .orElseThrow(() -> new GeneralException("El enlace general con codigo IVA_VT_15 no existe"));

                detalle.setIdAsientoDetalle(UUID.randomUUID());
                detalle.setIdData(model.getIdData());
                detalle.setIdEmpresa(model.getIdEmpresa());
                detalle.setDetalle(model.getConcepto());
                detalle.setTipoDocumento(model.getTipoVenta());
                detalle.setNumeroDocumento(model.getSerie() + "-" + model.getSecuencial());
                detalle.setFechaDocumento(model.getFechaEmision());
                detalle.setDebe(BigDecimal.ZERO);
                detalle.setHaber(item.getValor());
                //detalle.setGeItem(detalle.getItems());
                detalle.setTercero(model.getTercero());
                detalle.setCuenta(cnAsientosBuilder.builderCuentaIngreso(enlace.getIdCuenta()));
                //detalle.setCentroCostos();

                detalle.setItemOrden(contador.getAndIncrement());

                detalles.add(detalle);
            }

            if (item.getCodigoPorcentaje().equals("8") && item.getCodigo().equals("2")) {
                CnAsientosDetalleEntity detalle = new CnAsientosDetalleEntity();

                CnEnlacesGeneralesEntity enlace = cnEnlacesGeneralesRepository.findByCodigo("IVA_VT_8")
                        .orElseThrow(() -> new GeneralException("El enlace general con codigo IVA_VT_8 no existe"));

                detalle.setIdAsientoDetalle(UUID.randomUUID());
                detalle.setIdData(model.getIdData());
                detalle.setIdEmpresa(model.getIdEmpresa());
                detalle.setDetalle(model.getConcepto());
                detalle.setTipoDocumento(model.getTipoVenta());
                detalle.setNumeroDocumento(model.getSerie() + "-" + model.getSecuencial());
                detalle.setFechaDocumento(model.getFechaEmision());
                detalle.setDebe(BigDecimal.ZERO);
                detalle.setHaber(item.getValor());
                //detalle.setGeItem(detalle.getItems());
                detalle.setTercero(model.getTercero());
                detalle.setCuenta(cnAsientosBuilder.builderCuentaIngreso(enlace.getIdCuenta()));
                //detalle.setCentroCostos();

                detalle.setItemOrden(contador.getAndIncrement());

                detalles.add(detalle);

            }

            if (item.getCodigoPorcentaje().equals("5") && item.getCodigo().equals("2")) {
                CnAsientosDetalleEntity detalle = new CnAsientosDetalleEntity();

                CnEnlacesGeneralesEntity enlace = cnEnlacesGeneralesRepository.findByCodigo("IVA_VT_5")
                        .orElseThrow(() -> new GeneralException("El enlace general con codigo IVA_VT_5 no existe"));

                detalle.setIdAsientoDetalle(UUID.randomUUID());
                detalle.setIdData(model.getIdData());
                detalle.setIdEmpresa(model.getIdEmpresa());
                detalle.setDetalle(model.getConcepto());
                detalle.setTipoDocumento(model.getTipoVenta());
                detalle.setNumeroDocumento(model.getSerie() + "-" + model.getSecuencial());
                detalle.setFechaDocumento(model.getFechaEmision());
                detalle.setDebe(BigDecimal.ZERO);
                detalle.setHaber(item.getValor());
                //detalle.setGeItem(detalle.getItems());
                detalle.setTercero(model.getTercero());
                detalle.setCuenta(cnAsientosBuilder.builderCuentaIngreso(enlace.getIdCuenta()));
                //detalle.setCentroCostos();

                detalle.setItemOrden(contador.getAndIncrement());

                detalles.add(detalle);
            }
        });

        return detalles;
    }
}
