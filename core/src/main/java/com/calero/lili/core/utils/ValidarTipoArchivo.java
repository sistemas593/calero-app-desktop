package com.calero.lili.core.utils;


import org.springframework.web.multipart.MultipartFile;

public class ValidarTipoArchivo {

    public static boolean validarTipoExcel(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return false;
        }

        String nombreArchivo = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Validar extensión
        boolean extensionValida = nombreArchivo != null &&
                (nombreArchivo.toLowerCase().endsWith(".xlsx")
                        || nombreArchivo.toLowerCase().endsWith(".xls"));

        // Validar content type
        boolean mimeValido =
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
                        || "application/vnd.ms-excel".equals(contentType);

        return extensionValida && mimeValido;

    }
}
