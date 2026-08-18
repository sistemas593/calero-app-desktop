package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modImpuestosAnexos.ats.PagoExterior;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisesRepository;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalEntity;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalRepository;
import com.calero.lili.core.utils.ComprobanteSustentoService;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.TipoIdentificacionDocumentoValidacionService;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ValidacionGeneralCpImpuestosService {

    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleErrorBuilder;
    private final ComprobanteSustentoService comprobanteSustentoService;
    private final TbPaisesRepository tbPaisesRepository;
    private final TbParaisoFiscalRepository tbParaisoFiscalRepository;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;
    private final TipoIdentificacionDocumentoValidacionService tpIdentDocValidacion;

    public List<CpImpuestoDetalleError> validacionGeneral(DetalleCompras model) {

        List<CpImpuestoDetalleError> detalleErrores = new ArrayList<>();

        LocalDate fechaEmisionDoc = null;
        LocalDate fechaEmisionRet = null;
        LocalDate fechaRegistro = null;

        if (!tpIdentDocValidacion.validacionIdentDoc(model.getTpIdProv(), model.getTipoComprobante())) {
            System.out.println(model.getIdProv());
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El tipo de identificacion: "
                    + TipoIdentificacion.valueOf(model.getTpIdProv()).getIdentificacion() + " no se puede relacionar con el documento: " + DocumentoEnum.getCodigoDocumento(model.getTipoComprobante()).getNombre()));
        }

        if (Objects.nonNull(model.getEstablecimiento()) && Objects.nonNull(model.getPuntoEmision())) {
            String serie = model.getEstablecimiento() + model.getPuntoEmision();
            String mensajeSerie = validacionDocumentosGeneral.validarSerie(serie);
            if (!mensajeSerie.isEmpty()) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el documento " + mensajeSerie));
            }
        } else {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La serie del documento no puede estar vacía"));
        }


        if (model.getSecuencial().length() != 9) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El secuencial debe ser solo de 9 dígitos"));
        }


        String mensajeNumeroAut = validacionDocumentosGeneral.validarNumeroAutorizacion(model.getAutorizacion());
        if (!mensajeNumeroAut.isEmpty()) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el documento " + mensajeNumeroAut));
        }

        if (Objects.nonNull(model.getPagoExterior())) {
            validacionGeneralPagoExterior(model.getPagoExterior().getPagoLocExt(), model.getPagoExterior(), detalleErrores);
        }

        if (Objects.nonNull(model.getEstabRetencion1()) && Objects.nonNull(model.getPtoEmiRetencion1())) {
            String serieRetencion = model.getEstabRetencion1() + model.getPtoEmiRetencion1();
            String mensajeError = validacionDocumentosGeneral.validarSerie(serieRetencion);
            if (!mensajeError.isEmpty()) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el comprobante de retención " + mensajeError));
            }
        }

        if (Objects.nonNull(model.getAutRetencion1())) {
            String mensajeNumAutRt = validacionDocumentosGeneral.validarNumeroAutorizacion(model.getAutRetencion1());

            if (!mensajeNumAutRt.isEmpty()) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("En el comprobante de retención " + mensajeNumAutRt));
            }
        }


        if (Objects.nonNull(model.getFechaEmiRet1())
                && Objects.nonNull(model.getFechaEmision())) {

            fechaEmisionDoc = DateUtils.toLocalDate(model.getFechaEmision());
            fechaEmisionRet = DateUtils.toLocalDate(model.getFechaEmiRet1());

            if (fechaEmisionDoc.isAfter(fechaEmisionRet)) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La fecha de emisión del documento: " + model.getFechaEmision()
                        + " no puede ser mayor que la fecha de emisión de la retención: " + model.getFechaEmiRet1()));
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


    public void validarExisteReembolso(DetalleCompras model, List<CpImpuestoDetalleError> detalleErrores) {
        if (Objects.nonNull(model.getCodSustento())) {
            if (!comprobanteSustentoService.validacionCodigos(model.getTipoComprobante(), model.getCodSustento())) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La combinación de código de documento: " + model.getTipoComprobante() +
                        " y código de sustento: " + model.getCodSustento() + " es inválida."));
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


}
