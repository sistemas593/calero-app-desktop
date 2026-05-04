package com.calero.lili.core.modCargarExcelDP;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ExcelCtasCobrarServiceImpl {

    private final ErrorCargaBuilder errorCargaBuilder;
    private final ValidarIdentificacion validarIdentificacion;


    private static final String tipoDeudor = "1";
    private static final String ubicacion = "ECU";
    private static final String partesRelacionadas = "2";
    private static final String pais = "593";
    private static final String tipoAcreedor = "OTR";


    public byte[] cargarDecPat(MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);


        List<DetalleCtasXCobrar> detalleCtasXCobrarList = new ArrayList<>();
        List<DetallePasivo> detallePasivosList = new ArrayList<>();
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
                String tipoIdentificacion = obtenerTipoIdentificacion(rucOCedula);

                if (tipoIdentificacion.isEmpty()) {
                    errorCarga.add(errorCargaBuilder.builderError(linea, rucOCedula, "Número de identificación no corresponde ni a RUC ni a Cédula"));
                    continue;
                }

                validarIdentifiacion(rucOCedula, tipoIdentificacion, linea, row, errorCarga);

                if (row.getCell(0).getStringCellValue().equals("CXC")) {

                    // CXC CUENTAS POR PAGAR
                    DetalleCtasXCobrar detalleCtsXCobrar = new DetalleCtasXCobrar();
                    detalleCtsXCobrar.setNombreDeudor(row.getCell(2).getStringCellValue());
                    detalleCtsXCobrar.setTipoDeudor(tipoDeudor);
                    detalleCtsXCobrar.setTipoIdentificacion(tipoIdentificacion);
                    detalleCtsXCobrar.setNumeroIdentificacion(rucOCedula);
                    detalleCtsXCobrar.setUbicacion(ubicacion);
                    detalleCtsXCobrar.setPais(pais);
                    detalleCtsXCobrar.setPartesRelacionadas(partesRelacionadas);
                    detalleCtsXCobrar.setSaldo(normalizarNumero(row.getCell(3).getStringCellValue()));

                    detalleCtasXCobrarList.add(detalleCtsXCobrar);

                } else if (row.getCell(0).getStringCellValue().equals("CXP")) {

                    DetallePasivo detallePasivo = new DetallePasivo();

                    detallePasivo.setTipoAcreedor(tipoAcreedor);
                    detallePasivo.setDomicilioAcreedor(ubicacion);
                    detallePasivo.setValorDeuda(normalizarNumero(row.getCell(3).getStringCellValue()));
                    detallePasivo.setPaisAcreedor(pais);
                    detallePasivo.setNombreAcreedor(row.getCell(2).getStringCellValue());
                    detallePasivo.setTipoIdentificacionAcreedor(tipoIdentificacion);
                    detallePasivo.setNumeroIdentificacionAcreedor(rucOCedula);
                    detallePasivo.setPartesRelacionadas(partesRelacionadas);

                    detallePasivosList.add(detallePasivo);

                } else {
                    errorCarga.add(errorCargaBuilder.builderError(linea, row.getCell(1).getStringCellValue(), "El tipo no corresponde con Cuentas por Pagar ni con Pasivos"));
                }


            }

        }

        if (errorCarga.isEmpty()) {

            DecPat decPat = new DecPat();
            CtasXCobrar xCobrar = new CtasXCobrar();
            Pasivo pasivo = new Pasivo();

            xCobrar.setDetalleCtasXCobrar(detalleCtasXCobrarList);
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

        if (numeroIdentificacion.trim().length() == 13) {
            return "R";
        }
        if (numeroIdentificacion.trim().length() == 10) {
            return "C";
        }
        return "";

    }

    public static String normalizarNumero(String valor) {
        if (valor == null) return null;

        return valor
                .replace(".", "")   // quita miles
                .replace(",", "."); // cambia decimal
    }

}
