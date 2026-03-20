package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class BuscarDatosEmpresa {
    private final AdEmpresasRepository adEmpresasRepository;
    private static String projectId = "caleroapp";
    private static String bucketName = "caleroapp-bucket-sgn";

    public DatosEmpresaDto buscarEmpresa(Long idData, Long idEmpresa){
        //String sgn = "data00001/file001.p12";
        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData,idEmpresa);

        if (!empresa.isPresent()) {
            throw new GeneralException(MessageFormat
                    .format("Data {1}, Empresa {1} no existe",idData, idEmpresa));
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
                .build();

    }
}
