package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.enums.CodigoRetencion;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesEntity;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesRepository;
import com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CompraImpuestoDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresCompraImpuestoDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisesRepository;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalEntity;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalRepository;
import com.calero.lili.core.utils.ComprobanteSustentoService;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ValidacionGeneralCpImpuestosService {

    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleErrorBuilder;
    private final AdIvaPorcentajesRepository adIvaPorcentajesRepository;
    private final ComprobanteSustentoService comprobanteSustentoService;
    private final TbPaisesRepository tbPaisesRepository;
    private final TbParaisoFiscalRepository tbParaisoFiscalRepository;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;

    public List<CpImpuestoDetalleError> validacionGeneral(CompraImpuestoDto model) {

        List<CpImpuestoDetalleError> detalleErrores = new ArrayList<>();

        LocalDate fechaEmisionDoc = null;
        LocalDate fechaEmisionRet = null;
        LocalDate fechaRegistro = null;


        String mensajeSerie = validacionDocumentosGeneral.validarSerie(model.getSerie());
        if (!mensajeSerie.isEmpty()) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el documento " + mensajeSerie));
        }

        if (model.getSecuencial().length() != 9) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El secuencial debe ser solo de 9 dígitos"));
        }


        String mensajeNumeroAut = validacionDocumentosGeneral.validarNumeroAutorizacion(model.getNumeroAutorizacion());
        if (!mensajeNumeroAut.isEmpty()) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el documento " + mensajeNumeroAut));
        }


        if (Objects.nonNull(model.getCodigoSustento())) {
            if (!comprobanteSustentoService.validacionCodigos(model.getCodigoDocumento(), model.getCodigoSustento().replace("S", ""))) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La combinación de código de documento: " + model.getCodigoDocumento() +
                        " y código de sustento: " + model.getCodigoSustento() + " es inválida."));
            }
        }

        if (model.getPagoLocExt().equals("01") && Objects.nonNull(model.getPagoExterior())) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No es requerido el pago exterior"));
        }

        if (model.getPagoLocExt().equals("02") && Objects.isNull(model.getPagoExterior())) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No existe pago exterior"));
        }

        validacionGeneralPagoExterior(model.getPagoLocExt(), model.getPagoExterior(), detalleErrores);

        validarValoresImpuestos(model.getValores(), detalleErrores);

        if (Objects.nonNull(model.getCompraImpuestos())) {
            validarValoresRetenciones(model.getCompraImpuestos(), detalleErrores);
        }

        validateIvaPorcentaje(getIntegerTarifaIva(model.getValores()), DateUtils.toLocalDate(model.getFechaEmision()), detalleErrores);


        if (Objects.nonNull(model.getCodigoDocumento())) {
            if (model.getCodigoDocumento().equals("41")) {
                if (Objects.isNull(model.getReembolsos())) {
                    throw new GeneralException("No existe lista de reembolsos");
                }
            }
        }


        if (Objects.nonNull(model.getSerieRetencion())) {
            String mensajeError = validacionDocumentosGeneral.validarSerie(model.getSerieRetencion());
            if (!mensajeError.isEmpty()) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el comprobante de retención " + mensajeSerie));
            }
        }

        if (Objects.nonNull(model.getNumeroAutorizacionRetencion())) {
            String mensajeNumAutRt = validacionDocumentosGeneral.validarNumeroAutorizacion(model.getNumeroAutorizacionRetencion());

            if (!mensajeNumAutRt.isEmpty()) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el comprobante de retención " + mensajeNumAutRt));
            }
        }


        if (Objects.nonNull(model.getFechaEmisionRetencion())
                && Objects.nonNull(model.getFechaEmision())) {

            fechaEmisionDoc = DateUtils.toLocalDate(model.getFechaEmision());
            fechaEmisionRet = DateUtils.toLocalDate(model.getFechaEmisionRetencion());

            if (fechaEmisionDoc.isAfter(fechaEmisionRet)) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La fecha de emisión del documento: " + model.getFechaEmision()
                        + " no puede ser mayor que la fecha de emisión de la retención: " + model.getFechaEmisionRetencion()));
            }
        }


        if (Objects.nonNull(model.getFechaEmision()) && Objects.nonNull(model.getFechaRegistro())) {

            fechaEmisionDoc = DateUtils.toLocalDate(model.getFechaEmision());
            fechaRegistro = DateUtils.toLocalDate(model.getFechaRegistro());

            if (fechaEmisionDoc.isAfter(fechaRegistro)) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La fecha de emisión : " + model.getFechaEmision()
                        + " no puede ser mayor que la fecha de registro: " + model.getFechaRegistro()));
            }
        }


        return detalleErrores;

    }


    public void validateIvaPorcentaje(List<Integer> valores, LocalDate fechaFactura,
                                      List<CpImpuestoDetalleError> detalleErrores) {


        Optional<AdIvaPorcentajesEntity> porcentaje = adIvaPorcentajesRepository.findVigente(fechaFactura);

        if (porcentaje.isEmpty()) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No existe porcentajes de iva para la fecha:" + fechaFactura));
        }


        Set<Integer> tarifasVigentes = new HashSet<>();

        if (Objects.nonNull(porcentaje.get().getIva1()))

            if (porcentaje.get().getIva1() != 0) {
                tarifasVigentes.add(porcentaje.get().getIva1());
            }

        if (Objects.nonNull(porcentaje.get().getIva2())) {
            if (porcentaje.get().getIva2() != 0) {
                tarifasVigentes.add(porcentaje.get().getIva2());
            }
        }

        if (Objects.nonNull(porcentaje.get().getIva3())) {
            if (porcentaje.get().getIva3() != 0) {
                tarifasVigentes.add(porcentaje.get().getIva3());
            }
        }

        Set<Integer> tarifasExentas = Set.of(0, 6, 7); // 0%=0, No Objeto=6, Exento=7

        for (Integer tarifa : valores) {
            if (tarifasExentas.contains(tarifa)) continue;
            if (!tarifasVigentes.contains(tarifa)) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La tarifa de IVA: " + tarifa + " , no está vigente para la fecha: " + fechaFactura));
            }
        }


    }

    private void validacionGeneralPagoExterior(String pagoLocExt, PagoExterior request, List<CpImpuestoDetalleError> detalleErrores) {
        if (pagoLocExt.equals("02")) {

            switch (request.getTipoRegi()) {
                case "01" -> {

                    if (Objects.isNull(request.getPaisEfecPagoGen()) || request.getPaisEfecPagoGen().isEmpty()) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No existe pais al que se realiza el pago en régimen general"));
                    }

                    if (Objects.nonNull(request.getPaisEfecPagoParFis()) ||
                            Objects.nonNull(request.getDenopagoRegFis())) {

                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No debe existir pago para paraiso fiscal " +
                                " ni para denominacion del régimen fiscal preferente, si el tipo de registro es 01"));
                    }

                    Optional<TbPaisEntity> pais = tbPaisesRepository.findById(request.getPaisEfecPago());
                    if (pais.isEmpty()) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El pais con codigo: " + request.getPaisEfecPago() + "no existe"));
                    }


                    if (!request.getPaisEfecPago().equals(request.getPaisEfecPagoGen())) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El pais de pago debe ser igual al pais de pago régimen general"));
                    }


                }
                case "02" -> {

                    if (Objects.isNull(request.getPaisEfecPagoParFis()) || request.getPaisEfecPagoParFis().isEmpty()) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No existe pais al que se realiza el pago en paraiso fiscal"));
                    }

                    if (Objects.nonNull(request.getPaisEfecPagoGen()) ||
                            Objects.nonNull(request.getDenopagoRegFis())) {

                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No debe existir pago para régimen general " +
                                " ni para denominacion del régimen fiscal preferente, si el tipo de registro es 02"));
                    }

                    TbParaisoFiscalEntity entidad = tbParaisoFiscalRepository.findByCodigo(request.getPaisEfecPagoParFis())
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("El paraiso fiscal con codigo {0}, no existe",
                                    request.getPaisEfecPagoParFis())));

                    if (!entidad.getPais().getCodigoPais().equals(request.getPaisEfecPago())) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El pais de pago debe ser igual al pais asignado al paraiso fiscal"));
                    }

                }
                case "03" -> {
                    if (Objects.isNull(request.getDenopagoRegFis()) || request.getDenopagoRegFis().isEmpty()) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No existe pago denominacion del régimen fiscal preferente"));
                    }

                    if (Objects.nonNull(request.getPaisEfecPagoParFis()) ||
                            Objects.nonNull(request.getPaisEfecPagoGen())) {

                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No debe existir pago para paraiso fiscal " +
                                " ni para régimen general, si el tipo de registro es 03"));
                    }

                    Optional<TbPaisEntity> pais = tbPaisesRepository.findById(request.getPaisEfecPago());
                    if (pais.isEmpty()) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El pais con codigo: " + request.getPaisEfecPago() + "no existe"));
                    }

                }
            }


           /* String codigoPais = request.getPagoExterior().getPaisEfecPago();
            tbPaisesRepository.findById(codigoPais)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("El pais con codigo {0}, no existe", codigoPais)));*/

        }
    }


    private List<Integer> getIntegerTarifaIva(List<ValoresCompraImpuestoDto> valores) {
        return valores.stream()
                .map(ValoresCompraImpuestoDto::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }


    private void validarValoresRetenciones(List<CompraImpuestosDto> listCompraImpuesto, List<CpImpuestoDetalleError> detalleErrores) {


        for (CompraImpuestosDto dto : listCompraImpuesto) {

            dto.getImpuestoCodigos().forEach(item -> {


                if (Objects.isNull(item.getCodigo()) && Objects.isNull(item.getCodigoRetencion())) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El codigo y el codigo de retención siempre no existen"));
                }


                if (item.getCodigo().equals(CodigoRetencion.IVA)) {

                    List<String> codigosValidos = Arrays.asList("1", "2", "3", "7", "9", "10", "11");
                    if (!codigosValidos.contains(item.getCodigoRetencion())) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El codigo de retención para el IVA no debe ser: " + item.getCodigoRetencion()));
                    }
                }


                if (item.getBaseImponible().compareTo(BigDecimal.ZERO) < 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible de la retención no puede ser negativa"));
                }


                if (item.getPorcentajeRetener().compareTo(BigDecimal.ZERO) < 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El porcentaje de la retención no puede ser negativa"));
                }

                if (item.getPorcentajeRetener().compareTo(BigDecimal.ZERO) < 0 ||
                        item.getPorcentajeRetener().compareTo(BigDecimal.valueOf(100)) > 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El porcentaje a retener de la retención debe estar entre 0 y 100"));
                }


                if (item.getValorRetenido().compareTo(BigDecimal.ZERO) < 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El valor de la retención no puede ser negativo"));
                }


            });

        }


        // VALORES DE CP IMPUESTOS TAMPOCO PUEDEN SER NEGATIVOS, TODOS LO VALORES

    }

    private void validarValoresImpuestos(List<ValoresCompraImpuestoDto> valores, List<CpImpuestoDetalleError> detalleErrores) {

        valores.forEach(item -> {

            if (item.getBaseImponible().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible no puede ser negativa"));
            }

            if (item.getTarifa().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La tarifa no puede ser negativa"));
            }

            if (item.getValor().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La tarifa no puede ser negativa"));
            }

        });

    }


}
