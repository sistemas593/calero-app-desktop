package com.calero.lili.api.controllers;

import com.calero.lili.core.modCargarExcelDP.ExcelCtasCobrarServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/dec-pat")
@CrossOrigin(originPatterns = "*")
public class DecPatController {

    private final ExcelCtasCobrarServiceImpl excelCtasCobrarService;

    @PostMapping("cargar-excel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> create(@RequestBody MultipartFile file) throws IOException {

        byte[] xml = excelCtasCobrarService.cargarDecPat(file);
        String nombre = "DP" + LocalDate.now().getYear() + ".xml";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre)
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(xml.length)
                .body(xml);
    }


}
