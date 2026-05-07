package com.calero.lili.core.modCargarExcelDP;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar.CtasXCobrar;
import com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar.DecPat;
import com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar.DetalleCtasXCobrar;
import com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar.DetallePasivo;
import com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar.Pasivo;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modCargarExcelDP.builder.ErrorCargaBuilder;
import com.calero.lili.core.modCargarExcelDP.dto.ErrorCargaDto;
import com.calero.lili.core.utils.validaciones.ValidarIdentificacion;
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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ExcelCtasCobrarServiceImpl {

    private final ErrorCargaBuilder errorCargaBuilder;
    private final ValidarIdentificacion validarIdentificacion;
    private final FormatoValores formatoValores;


    private static final String tipoDeudor = "1";
    private static final String ubicacion = "ECU";
    private static final String partesRelacionadas = "2";
    private static final String pais = "593";
    private static final String tipoAcreedor = "OTR";


    public byte[] cargarDecPat(MultipartFile file) throws IOException {

        if (Objects.isNull(file)) {
            throw new ListErrorException(List.of("Archivo no encontrado o no seleccionado"));
        }

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);


        Map<String, DetalleCtasXCobrar> mapaCtasXCobrar = new HashMap<>();
        Map<String, DetallePasivo> mapDetallePasivo = new HashMap<>();
        List<ErrorCargaDto> errorCarga = new ArrayList<>();


        for (Sheet sheet : workbook) {

            boolean isHeader = true;
            for (Row row : sheet) {
                int linea = row.getRowNum() + 1;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                if (Objects.isNull(row.getCell(1))) {
                    errorCarga.add(errorCargaBuilder.builderError(linea, "", "Número de identificación vacío"));
                    continue;
                }


                String rucOCedula = row.getCell(1).getStringCellValue();
                rucOCedula = rucOCedula.trim();
                String tipoIdentificacion = obtenerTipoIdentificacion(rucOCedula);


                if (tipoIdentificacion.isEmpty()) {
                    errorCarga.add(errorCargaBuilder.builderError(linea, rucOCedula, "Número de identificación no corresponde ni a RUC ni a Cédula"));
                    continue;
                }

                validarIdentifiacion(rucOCedula, tipoIdentificacion, linea, row, errorCarga);

                if (Objects.isNull(row.getCell(3))) {
                    errorCarga.add(errorCargaBuilder.builderError(linea, row.getCell(1).getStringCellValue(), "Valor de la deuda vacío"));
                    continue;
                }

                BigDecimal valorDeuda = convetirValor(row.getCell(3).getStringCellValue());

                if (Objects.isNull(row.getCell(2))) {
                    errorCarga.add(errorCargaBuilder.builderError(linea, row.getCell(1).getStringCellValue(), "El nombre del tercero está vacío"));
                    continue;
                }

                String celdaNombre = row.getCell(2).getStringCellValue();

                String nombreValido = validarNombre(celdaNombre);
                String nombreTruncado = truncarNombreTercero(nombreValido);
                String nombreTercero = nombreTruncado.trim();

                if (row.getCell(0).getStringCellValue().equals("CXC")) {

                    DetalleCtasXCobrar existente = mapaCtasXCobrar.get(rucOCedula);


                    if (existente != null) {

                        BigDecimal valorActual = new BigDecimal(existente.getSaldo());
                        BigDecimal nuevoValor = valorActual.add(valorDeuda);

                        existente.setSaldo(formatoValores.convertirBigDecimalToString(nuevoValor));

                    } else {
                        DetalleCtasXCobrar detalleCtsXCobrar = new DetalleCtasXCobrar();
                        detalleCtsXCobrar.setNombreDeudor(nombreTercero);
                        detalleCtsXCobrar.setTipoDeudor(tipoDeudor);
                        detalleCtsXCobrar.setTipoIdentificacion(tipoIdentificacion);
                        detalleCtsXCobrar.setNumeroIdentificacion(rucOCedula);
                        detalleCtsXCobrar.setUbicacion(ubicacion);
                        detalleCtsXCobrar.setPais(pais);
                        detalleCtsXCobrar.setPartesRelacionadas(partesRelacionadas);
                        detalleCtsXCobrar.setSaldo(formatoValores.convertirBigDecimalToString(valorDeuda));

                        mapaCtasXCobrar.put(rucOCedula, detalleCtsXCobrar);
                    }

                    // CXC CUENTAS POR PAGAR

                } else if (row.getCell(0).getStringCellValue().equals("CXP")) {

                    DetallePasivo existente = mapDetallePasivo.get(rucOCedula);

                    if (existente != null) {
                        BigDecimal valorActual = new BigDecimal(existente.getValorDeuda());
                        BigDecimal nuevoValor = valorActual.add(valorDeuda);

                        existente.setValorDeuda(formatoValores.convertirBigDecimalToString(nuevoValor));
                    } else {

                        DetallePasivo detallePasivo = new DetallePasivo();

                        detallePasivo.setTipoAcreedor(tipoAcreedor);
                        detallePasivo.setDomicilioAcreedor(ubicacion);
                        detallePasivo.setValorDeuda(formatoValores.convertirBigDecimalToString(valorDeuda));
                        detallePasivo.setPaisAcreedor(pais);
                        detallePasivo.setNombreAcreedor(nombreTercero);
                        detallePasivo.setTipoIdentificacionAcreedor(tipoIdentificacion);
                        detallePasivo.setNumeroIdentificacionAcreedor(rucOCedula);
                        detallePasivo.setPartesRelacionadas(partesRelacionadas);
                        mapDetallePasivo.put(rucOCedula, detallePasivo);

                    }


                } else {
                    errorCarga.add(errorCargaBuilder.builderError(linea, row.getCell(1).getStringCellValue(), "El tipo no corresponde con Cuentas por Pagar ni con Pasivos"));
                }


            }

        }

        if (errorCarga.isEmpty()) {

            DecPat decPat = new DecPat();
            CtasXCobrar xCobrar = new CtasXCobrar();
            Pasivo pasivo = new Pasivo();


            List<DetalleCtasXCobrar> detalleCtasXCobrarList = new ArrayList<>(mapaCtasXCobrar.values());

            detalleCtasXCobrarList.sort(Comparator.comparing(DetalleCtasXCobrar::getNombreDeudor, String.CASE_INSENSITIVE_ORDER));

            xCobrar.setDetalleCtasXCobrar(detalleCtasXCobrarList);

            List<DetallePasivo> detallePasivosList = new ArrayList<>(mapDetallePasivo.values());

            detallePasivosList.sort(Comparator.comparing(DetallePasivo::getNombreAcreedor, String.CASE_INSENSITIVE_ORDER));

            pasivo.setDetallePasivo(detallePasivosList);

            decPat.setCtasXCobrar(xCobrar);
            decPat.setPasivo(pasivo);
            decPat.setVersion("1.0");

            return XmlUtils.convertToXmlString(DecPat.class, decPat).getBytes();
        } else {
            List<String> list = errorCarga.stream()
                    .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getIdentificacion() + "   " + detalleError.getMensajeError())
                    .toList();
            throw new ListErrorException(list);
        }
    }


    public void validarIdentifiacion(String numeroIdentificacion,
                                     String tipoIdentificacion, int linea, Row row, List<ErrorCargaDto> errorCarga) {

        try {

            if (tipoIdentificacion.equals("C")) {
                validarIdentificacion.validarCedula(numeroIdentificacion.trim());
            } else {
                validarIdentificacion.validarRuc(numeroIdentificacion.trim());
            }
        } catch (Exception exception) {
            errorCarga.add(errorCargaBuilder.builderError(linea, row.getCell(1).getStringCellValue(), "Número de identificación incorrecto: " + exception.getMessage()));
        }


    }

    public String obtenerTipoIdentificacion(String numeroIdentificacion) {

        if (numeroIdentificacion.length() == 13) {
            return "R";
        }
        if (numeroIdentificacion.length() == 10) {
            return "C";
        }
        return "";

    }

    private BigDecimal convetirValor(String valor) {
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
        return new BigDecimal(valor);
    }


    private String truncarNombreTercero(String nombreTercero) {
        return nombreTercero.length() > 60 ? nombreTercero.substring(0, 60) : nombreTercero;
    }

    private String validarNombre(String celdaNombre) {
        String caracteresQuitados = celdaNombre.replace(".", "")
                .replace("-", "")
                .replace(",", "");

        String texto = Normalizer.normalize(caracteresQuitados, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        texto = texto.trim().replaceAll("\\s+", " ");

        return texto.toUpperCase();

    }

    public Boolean numeroIdentificacionRepetido(List<String> lista, String valor) {
        return Collections.frequency(lista, valor) > 1;
    }

}
