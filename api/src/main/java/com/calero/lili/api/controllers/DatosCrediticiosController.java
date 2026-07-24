package com.calero.lili.api.controllers;


import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modVentas.reporteCredito.DatosCrediticiosExcelServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.DatosCrediticiosRepository;
import com.calero.lili.core.modVentas.reporteCredito.DatosCrediticiosSaldoExcelServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.ReporteDatosCrediticiosServiceImpl;
import com.calero.lili.core.modVentas.reporteCredito.dto.DatosCrediticiosResponseDto;
import com.calero.lili.core.modVentas.reporteCredito.projection.PeriodoProjection;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.nio.charset.Charset;
import java.time.LocalDate;
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
    private final AdEmpresasRepository adEmpresasRepository;
    private final DatosCrediticiosRepository datosCrediticiosRepository;


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


    @GetMapping("reporte/{idEmpresa}/{idDatosCrediticios}")
    public ResponseEntity<byte[]> reporteDatosCrediticios(@PathVariable("idEmpresa") Long idEmpresa,
                                                          @PathVariable("idDatosCrediticios") UUID idDatosCrediticios) {


        Long idData = idDataService.getIdData();

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("No se encontró la empresa con id: " + idEmpresa));


        PeriodoProjection entidad = datosCrediticiosRepository
                .findPeridoById(idData, empresa.getIdEmpresa(), idDatosCrediticios)
                .orElseThrow(() -> new GeneralException("No se encontró cabecera con el id : " + idDatosCrediticios));

        LocalDate fechaPeriodo = DateUtils.toPeriodoDateDinarap(entidad.getPeriodo());
        String periodo = DateUtils.toStringPeriodoFiscal(fechaPeriodo);


        byte[] txt = reporteDatosCrediticiosService.generarTxt(idData, empresa, entidad, idDatosCrediticios, fechaPeriodo);
        String nombre = empresa.getRuc() + periodo + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre)
                .contentType(new MediaType(MediaType.TEXT_PLAIN, Charset.forName("windows-1252")))
                .contentLength(txt.length)
                .body(txt);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<DatosCrediticiosResponseDto> getAll(@PathVariable("idEmpresa") Long idEmpresa, Pageable pageable) {

        Sort sort = pageable.getSort().and(Sort.by("idDatosCrediticios").ascending());

        int pageSize = pageable.getPageSize();
        if (pageSize > 100) {
            pageSize = 100;}
        Pageable pageableConSort = PageRequest.of(pageable.getPageNumber(), pageSize, sort);

        return reporteDatosCrediticiosService.getAll(idDataService.getIdData(), idEmpresa, pageableConSort);
    }

    @DeleteMapping("eliminar/{idEmpresa}/{idDatosCrediticios}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idDatosCrediticios") UUID idDatosCrediticios) {
        reporteDatosCrediticiosService.delete(idDataService.getIdData(), idEmpresa, idDatosCrediticios);

    }

}
