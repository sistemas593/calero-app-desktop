package com.calero.lili.api.modRRHH;

import com.calero.lili.api.modRRHH.modRRHHCabecera.CargarExcelAnualCabeceraServiceImpl;
import com.calero.lili.api.modRRHH.modRRHHCabecera.RhRolCabeceraService;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.FilterRolCabeceraDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.RolCabeceraDto;
import com.calero.lili.api.modRRHH.modRRHHParametros.CargarExcelParametrosServiceImpl;
import com.calero.lili.api.modRRHH.modRolNovedades.CargarExcelNovedadesServiceImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1.0/")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class RhRolController {


    private final IdDataServiceImpl idDataService;
    private final CargarExcelParametrosServiceImpl cargarExcelParametrosService;
    private final CargarExcelNovedadesServiceImpl cargarExcelNovedadesService;
    private final RhRolCabeceraService rhRolCabeceraService;
    private final CargarExcelAnualCabeceraServiceImpl cargarExcelAnualCabeceraService;

    @PostMapping("cargar-excel/rol-parametros-anuales/{idEmpresa}")
    public void uploadFileParametrosAnuales(@RequestBody MultipartFile file,
                                            @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            cargarExcelParametrosService.cargarParametrosExcel(idDataService.getIdData(), idEmpresa, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("cargar-excel/rol-novedades-mensuales/{idEmpresa}")
    public void uploadFileRolNovedades(@RequestBody MultipartFile file,
                                       @PathVariable("idEmpresa") Long idEmpresa) {

        try {
            cargarExcelNovedadesService.cargarRolParametrosExcel(idDataService.getIdData(), idEmpresa, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("proceso/rol-cabecera/{idEmpresa}")
    public void procesoRolCabecera(@PathVariable("idEmpresa") Long idEmpresa,
                                   FilterRolCabeceraDto model) {

        rhRolCabeceraService.procesarRolCabecera(idDataService.getIdData(), idEmpresa, model);
    }


    @GetMapping("rol-cabecera/reporte/{idEmpresa}")
    public List<RolCabeceraDto> getAllCabecera(@PathVariable("idEmpresa") Long idEmpresa,
                                               FilterRolCabeceraDto model) {

        return rhRolCabeceraService.getAll(idDataService.getIdData(), idEmpresa, model);
    }

    @GetMapping("rol-cabecera/reporte/pdf/{idEmpresa}")
    public ResponseEntity<byte[]> reporteRolCabeceraPdf(@PathVariable("idEmpresa") Long idEmpresa,
                                                        FilterRolCabeceraDto model) {

        byte[] pdf = rhRolCabeceraService.generatePdfCabecera(idDataService.getIdData(), idEmpresa, model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-pagos-empleados" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("rol-cabecera/reporte/excel/{idEmpresa}")
    public void reporteMayorGeneralExportarExcel(HttpServletResponse response,
                                                 @PathVariable("idEmpresa") Long idEmpresa,
                                                 FilterRolCabeceraDto filters) throws IOException {

        rhRolCabeceraService.generateExcelCabecera(idDataService.getIdData(), idEmpresa, response, filters);
    }


    @PostMapping("cargar-excel/rol-cabecera-anuales/{idEmpresa}")
    public void uploadFileCabeceraAnual(@RequestBody MultipartFile file,
                                        @PathVariable("idEmpresa") Long idEmpresa) {

        try {
            cargarExcelAnualCabeceraService.cargarRolCabeceraAnual(idDataService.getIdData(), idEmpresa, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
