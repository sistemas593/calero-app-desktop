package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.utils.AESUtils;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class BuscarDatosEmpresa {
    private final AdEmpresasRepository adEmpresasRepository;
    private static String projectId = "caleroapp";
    private static String bucketName = "caleroapp-bucket-sgn";

    public DatosEmpresaDto buscarEmpresa(Long idData, Long idEmpresa) {
        //String sgn = "data00001/file001.p12";
        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);

        if (empresa.isEmpty()) {
            throw new GeneralException(MessageFormat
                    .format("Data {1}, Empresa {1} no existe", idData, idEmpresa));
        }

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, "data00001/logo-fe-0001.jpg");
        Blob blob = storage.get(blobId);
        if (blob == null || !blob.exists()) {
            throw new GeneralException("El archivo especificado no existe en el bucket.");
        }
        ReadChannel reader = blob.reader();
        InputStream inputStream = Channels.newInputStream(reader);
        byte[] imageBytes = null;
        try {
            imageBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Storage storage1 = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId1 = BlobId.of(bucketName, "data00001/file001.p12");
        Blob blob1 = storage1.get(blobId1);

        if (blob1 == null || !blob1.exists()) {
            throw new GeneralException("El archivo especificado no existe en el bucket.");
        }

        ReadChannel reader1 = blob1.reader();
        InputStream is1 = Channels.newInputStream(reader1);

        return DatosEmpresaDto.builder()
                .pwd(empresa.get().getContraseniaFirma())
                .imageBytes(imageBytes)
                .inputStreamFileSgn(is1)
                .momentoEnvioFactura(empresa.get().getMomentoEnvioFactura())
                .momentoEnvioNotaDebito(empresa.get().getMomentoEnvioNotaDebito())
                .momentoEnvioNotaCredito(empresa.get().getMomentoEnvioNotaCredito())
                .momentoEnvioComprobanteRetencion(empresa.get().getMomentoEnvioComprobanteRetencion())
                .momentoEnvioGuiaRemision(empresa.get().getMomentoEnvioGuiaRemision())
                .momentoEnvioLiquidacion(empresa.get().getMomentoEnvioLiquidacion())
                .build();

    }

    /**
     * Obtiene los datos de la empresa desde archivos locales (modo LOC).
     * Lee el .p12 y el logo desde las rutas almacenadas en la entidad empresa.
     *
     * @param idData    identificador del tenant
     * @param idEmpresa identificador de la empresa
     * @param password  contraseña del certificado .p12 proporcionada por el llamador
     * @return DatosEmpresaDto con el stream del certificado, la contraseña y los bytes del logo
     */
    /**
     * Carga el certificado .p12 y el logo desde cualquier ruta del sistema de archivos local.
     * Las rutas se configuran en la entidad empresa (rutaArchivoFirma, rutaLogo).
     *
     * Ejemplos de rutas válidas:
     *   Windows : C:\Users\Ismael\Documents\firma.p12
     *   Linux   : /home/ismael/firmas/empresa.p12
     *   Relativa: firmas/empresa.p12  (relativa al directorio de trabajo)
     *
     * @param idData    identificador del tenant
     * @param idEmpresa identificador de la empresa
     * @param password  contraseña del certificado .p12
     * @return DatosEmpresaDto con el stream del certificado, la contraseña y los bytes del logo
     */
    /**
     * Lee el .p12 y el logo desde las rutas guardadas en la entidad empresa (BD).
     * La contraseña viene por parámetro; si es nula usa la almacenada en la entidad.
     */
    public DatosEmpresaDto obtenerLocalDatosEmpresa(Long idData, Long idEmpresa) {

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("Data {0}, Empresa {1} no existe", idData, idEmpresa)));

        String pwd = AESUtils.decrypt(empresa.getContraseniaFirma());

        String rutaFirma = empresa.getRutaArchivoFirma();
        if (rutaFirma == null || rutaFirma.isBlank()) {
            throw new GeneralException(
                    "La empresa no tiene configurada la ruta del archivo de firma (.p12). " +
                            "Configure la ruta en la sección 'Firma y Envío' del formulario de empresa.");
        }

        Path pathP12 = Paths.get(rutaFirma);
        if (!Files.exists(pathP12)) {
            throw new GeneralException(MessageFormat.format(
                    "El archivo .p12 no existe en la ruta configurada: {0}", pathP12.toAbsolutePath()));
        }

        InputStream inputStreamFirma;
        try {
            inputStreamFirma = new FileInputStream(pathP12.toFile());
        } catch (Exception e) {
            throw new GeneralException(MessageFormat.format(
                    "Error al leer el archivo .p12: {0}", e.getMessage()));
        }

        // ── 4. Leer logo desde la ruta guardada en BD (opcional) ──────────────
        byte[] imageBytes = null;
        String rutaLogo = empresa.getRutaLogo();
        if (rutaLogo != null && !rutaLogo.isBlank()) {
            Path pathLogo = Paths.get(rutaLogo);
            if (Files.exists(pathLogo)) {
                try {
                    imageBytes = Files.readAllBytes(pathLogo);
                } catch (Exception e) {
                    System.out.println("Advertencia: no se pudo leer el logo: " + e.getMessage());
                }
            }
        }

        return DatosEmpresaDto.builder()
                .inputStreamFileSgn(inputStreamFirma)
                .imageBytes(imageBytes)
                .pwd(pwd)
                .momentoEnvioFactura(empresa.getMomentoEnvioFactura())
                .momentoEnvioNotaDebito(empresa.getMomentoEnvioNotaDebito())
                .momentoEnvioNotaCredito(empresa.getMomentoEnvioNotaCredito())
                .momentoEnvioComprobanteRetencion(empresa.getMomentoEnvioComprobanteRetencion())
                .momentoEnvioGuiaRemision(empresa.getMomentoEnvioGuiaRemision())
                .momentoEnvioLiquidacion(empresa.getMomentoEnvioLiquidacion())
                .build();
    }
}
