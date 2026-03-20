package com.calero.lili.core.apiSitac.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileProcessingService {

    @Value("")
    private String basePath;


    public String uploadFile(MultipartFile multipartFile) {
        File dir = new File(basePath + multipartFile.getOriginalFilename());

        if (dir.exists()) {
            return "EXIST";
        }
        String ruta = basePath + multipartFile.getOriginalFilename();
//        Path path = Path.of(ruta);
        Path path = Paths.get(ruta);
        try {
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "CREATED";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "FAILED";
    }


}
