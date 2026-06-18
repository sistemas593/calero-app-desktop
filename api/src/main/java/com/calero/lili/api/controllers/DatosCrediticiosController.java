package com.calero.lili.api.controllers;


import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.DatosCrediticiosExcelServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.DatosCrediticiosSaldoExcelServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.ReporteDatosCrediticiosServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.dto.DatosCrediticiosResponseDto;
import com.calero.lili.core.modVentas.reporteCredito.dto.FilterDatosCrediticiosDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/datos-crediticios")
@CrossOrigin(originPatterns = "*")
public class DatosCrediticiosController {


    private final ReporteDatosCrediticiosServiceImpl reporteDatosCrediticiosService;
    private final DatosCrediticiosExcelServiceImpl datosCrediticiosExcelService;
    private final DatosCrediticiosSaldoExcelServiceImpl datosCrediticiosSaldoExcelService;
    private final IdDataServiceImpl idDataService;


    @PostMapping("/excel/{idEmpresa}")
    public void uploadDatosCrediticiosExcel(@RequestParam("file") MultipartFile file,
                                            @PathVariable("idEmpresa") Long idEmpresa,
                                            @RequestParam("periodo") String periodo) {
        try {
            datosCrediticiosExcelService.cargarDatosCrediticios(idDataService.getIdData(), idEmpresa, file, periodo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/saldos/excel/{idEmpresa}")
    public void uploadDatosCrediticiosSaldosExcel(@RequestParam("file") MultipartFile file,
                                                  @PathVariable("idEmpresa") Long idEmpresa,
                                                  @RequestParam("periodo") String periodo) {
        try {
            datosCrediticiosSaldoExcelService.cargarSaldoDatosCrediticios(idDataService.getIdData(), idEmpresa, periodo, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("reporte/{idEmpresa}")
    public ResponseEntity<byte[]> reporteDatosCrediticios(@PathVariable("idEmpresa") Long idEmpresa,
                                                          FilterDatosCrediticiosDto filter) {


        byte[] txt = reporteDatosCrediticiosService.generarTxt(idDataService.getIdData(), idEmpresa, filter); // tu byte[]
        String nombre = "reporte-datos-crediticios" + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre)
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(txt.length)
                .body(txt);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public List<DatosCrediticiosResponseDto> getAll(@PathVariable("idEmpresa") Long idEmpresa) {
        return reporteDatosCrediticiosService.getAll(idDataService.getIdData(), idEmpresa);
    }

    @DeleteMapping("eliminar/{idEmpresa}/{idDatosCrediticios}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idDatosCrediticios") UUID idDatosCrediticios) {
        reporteDatosCrediticiosService.delete(idDataService.getIdData(), idEmpresa, idDatosCrediticios);

    }

}
