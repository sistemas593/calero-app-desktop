package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.varios.CargarP12ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/certificado")
@CrossOrigin(originPatterns = "*")
public class CargarP12Controller {

    private final CargarP12ServiceImpl cargarP12Service;
    private final IdDataServiceImpl idDataService;

    @PostMapping("/upload/{idEmpresa}")
    public ResponseEntity<String> uploadCertificado(@RequestParam("file") MultipartFile file,
                                                    @PathVariable("idEmpresa") Long idEmpresa) {

        return cargarP12Service.uploadCertificado(idDataService.getIdData().toString(), idEmpresa, file);
    }

    @GetMapping("/ver/{idEmpresa}")
    public void verCertificado(@PathVariable("idEmpresa") Long idEmpresa) throws IOException {

        cargarP12Service.readFile(idDataService.getIdData().toString(), idEmpresa);
    }

}
