package com.calero.lili.core.modImpuestosAnexos.retencionFuente;

import com.calero.lili.core.builder.retencion.RdepBuilder;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modImpuestosAnexos.builder.RetencionFuenteBuilder;
import com.calero.lili.core.modImpuestosAnexos.retencion.Rdep;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@AllArgsConstructor
public class GenerarTalonResumenPdfServiceImpl {

    private final RdepBuilder rdepBuilder;
    private final RetencionFuenteBuilder retencionFuenteBuilder;
    private AdEmpresasRepository adEmpresasRepository;




    public byte[] generarReporteTalonResumenRetencionFuentePDF(Long idData, Long idEmpresa, ValoresTalonResumenDto dto) {

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La empresa con id {0} no existe", idEmpresa)));

        try {
            InputStream reporteStream = new ClassPathResource("reporte_talon_rdep.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections
                    .singletonList(retencionFuenteBuilder.builderTalonResumen(empresa, dto)));
            Map<String, Object> parametros = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception exception) {
            throw new GeneralException(exception.getMessage());
        }
    }




    public void generarReporteTalonResumenRetencionFuenteXml(RetencionFuenteXmlDto model,
                                                             HttpServletResponse response) {

        try {
            if (Objects.isNull(model)) {
                throw new GeneralException("No se ha encontrado información para generar el XML");
            }
            generarXml(rdepBuilder.builderRdep(model), response);
        } catch (Exception exception) {
            throw new GeneralException(exception.getMessage());
        }

    }

    private void generarXml(Rdep rdep, HttpServletResponse response) {
        try {

            String fileName = "RDEP" + LocalDate.now().getYear() + ".xml";
            String zipFileName = "RDEP" + LocalDate.now().getYear() + ".zip";

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);

                JAXBContext context = JAXBContext.newInstance(Rdep.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.encoding", "UTF-8");
                marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));

                OutputStream out = response.getOutputStream();
                marshaller.marshal(rdep, zipOut);
                out.flush();

                zipOut.closeEntry();
            }


            System.out.println("XML Generado con exito a partir de objeto: xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
