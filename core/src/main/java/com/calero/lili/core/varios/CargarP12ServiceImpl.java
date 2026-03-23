package com.calero.lili.core.varios;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;


@Service
public class CargarP12ServiceImpl {

    private static final String projectId = "caleroapp";
    private static final String bucketName = "caleroapp-bucket-sgn";
    private static final String objectName = "sample1.txt";


    public ResponseEntity<String> uploadCertificado(String idData, Long idEmpresa, MultipartFile file) {

        // 1. Validar si el archivo está vacío
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: El archivo está vacío.");
        }

        // 2. Validar la extensión (.p12 o .pfx que son comunes en Ecuador)
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.toLowerCase().endsWith(".p12") || fileName.toLowerCase().endsWith(".pfx"))) {
            return ResponseEntity.badRequest().body("Error: Solo se permiten archivos de firma electrónica (.p12 o .pfx).");
        }

        // 3. Validar el Content-Type (MIME type)
        // El tipo estándar para certificados PKCS12 es "application/x-pkcs12"
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/x-pkcs12")) {
            // Nota: A veces los navegadores lo envían como "application/octet-stream",
            // podrías ser flexible aquí o estricto según tu necesidad.
        }

        // 4. Validar el tamaño (un .p12 rara vez pesa más de 50KB)
        long maxSize = 100 * 1024; // 100 KB
        if (file.getSize() > maxSize) {
            return ResponseEntity.badRequest().body("Error: El archivo es demasiado grande para ser un certificado válido.");
        }

        try {
            // Aquí llamas a tu lógica de subida a Google Cloud
            uploadFileP12(file, idEmpresa, idData);
            return ResponseEntity.ok("Certificado de " + fileName + " validado y subido con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir a Google Cloud: " + e.getMessage());
        }
    }


    private void uploadFileP12(MultipartFile file, Long idEmpresa, String idData) throws IOException {


        idData = StringUtils.leftPad(idData, 5, '0');
        String idEmp = idEmpresa.toString();
        idEmp = StringUtils.leftPad(idEmp, 3, '0');
        // Crear cliente GCS
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();

        // Nombre del objeto en el bucket (usa el nombre original del archivo)
        String objectName = file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, "data" + idData + "/file" + idEmp + ".p12");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        // Subir directamente el contenido del MultipartFile
        storage.create(blobInfo, file.getBytes());

        System.out.println("File " + objectName + " uploaded to bucket " + bucketName);
    }

    // upload file to GCS
    public static void uploadFile() throws IOException {
        // Create a new GCS client
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        // The blob ID indentifies the newly created blob, which consists of a bucket name and an object
        // name
        BlobId blobId = BlobId.of(bucketName, "data00001/" + objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        // The filepath on our local machine that we want to upload
        String filePath = "sample1.txt";

        // upload the file and print the status
        storage.createFrom(blobInfo, Paths.get(filePath));
        System.out
                .println("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
    }

    // download file from GCS
    public static void downloadFile() throws IOException {
        // we'll download the same file to another file path
        String filePath = "/tmp/sample_downloaded.txt";

        // Create a new GCS client and get the blob object from the blob ID
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        Blob blob = storage.get(blobId);

        // download the file and print the status
        blob.downloadTo(Paths.get(filePath));
        System.out.println("File " + objectName + " downloaded to " + filePath);
    }

    // read contents of the file
    public static void readFile() throws IOException {
        // Create a new GCS client and get the blob object from the blob ID
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, "data00001" + "/file001" + ".p12");
        Blob blob = storage.get(blobId);

        // read the contents of the file and print it
        String contents = new String(blob.getContent());
        System.out.println("Contents of file " + objectName + ": " + contents);
    }

    // delete an existing file from GCS
    public static void deleteFile() throws IOException {
        // Create a new GCS client and get the blob object from the blob ID
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        Blob blob = storage.get(blobId);

        if (blob == null) {
            System.out.println("File " + objectName + " does not exist in bucket " + bucketName);
            return;
        }

        // delete the file and print the status
        blob.delete();
        System.out.println("File " + objectName + " deleted from bucket " + bucketName);
    }

    // list all files in a folder or bucket
    public static void listFiles() throws IOException {
        // Create a new GCS client and get the blob object from the blob ID
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        System.out.println("Files in bucket " + bucketName + ":");
        // list all the blobs in the bucket
        for (Blob blob : storage
                .list(bucketName, BlobListOption.currentDirectory(), BlobListOption.prefix("data00001/"))
                .iterateAll()) {
            System.out.println(blob.getName());
        }
    }
}

