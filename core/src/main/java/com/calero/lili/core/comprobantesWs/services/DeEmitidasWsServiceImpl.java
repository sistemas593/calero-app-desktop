package com.calero.lili.core.comprobantesWs.services;

import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Mensaje;
import com.calero.lili.core.comprobantes.services.DeEmitidasComponentsServiceImpl;
import com.calero.lili.core.comprobantes.services.builder.CampoAutorizacionBuilder;
import com.calero.lili.core.comprobantes.services.dto.FilterEmitidosDto;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.services.AutorizacionServiceImpl;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListExistRequestResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@XmlRootElement
@Slf4j
public class DeEmitidasWsServiceImpl {

    private static final String COLUMNA_CLAVE_ACCESO = "CLAVE_ACCESO";

    @Autowired
    private final AutorizacionServiceImpl autorizacionService;
    private final DeEmitidasComponentsServiceImpl deEmitidasComponentsService;
    private final CampoAutorizacionBuilder campoAutorizacionBuilder;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;

    public CpImpuestosRecibirListExistRequestResponseDto verificarExisteListaClaves(Long idData, Long idEmpresa, CpImpuestosRecibirListExistRequestResponseDto request) {
        log.info("xxxxxx");

        request.getListaClavesAcceso().stream().forEach((c) -> {
            if (c.getClaveAcceso().length() == 49) {

                Boolean existeBdd = deEmitidasComponentsService.verificarExisteDocumentoElectronicoBdd(idData, idEmpresa, c.getClaveAcceso());
                if (existeBdd) {
                    c.setExiste("S");
                } else {
                    c.setExiste("N");
                }
            } else {
                c.setExiste("N");
            }
        });
        return request;
    }


    public CpImpuestosRecibirListCreationResponseDto createListClavesAcceso(Long idData, Long idEmpresa,
                                                                            MultipartFile file, String usuario,
                                                                            FilterEmitidosDto filter) {


        if (filter.getSucursal() == null || filter.getSucursal().isEmpty()) {
            throw new GeneralException("La sucursal es requerida");
        } else {
            adEmpresasSucursalesRepository.findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, filter.getSucursal())
                    .orElseThrow(() -> new GeneralException("La sucursal " + filter.getSucursal() + " no existe para la empresa con id: " + idEmpresa));
        }

        List<String> listaClavesAcceso = extraerClavesAccesoDeArchivo(file);

        log.info("xxxxxx");
        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();

        listaClavesAcceso.stream().forEach((claveAcceso) -> {

            if (claveAcceso.length() == 49) {
                if ((claveAcceso.startsWith("07", 8))) {

                    Boolean existeBdd = deEmitidasComponentsService.verificarExisteDocumentoElectronicoBdd(idData, idEmpresa, claveAcceso);
                    if (existeBdd.equals(Boolean.FALSE)) {
                        AutorizacionRequestDto autorizacion = new AutorizacionRequestDto();
                        autorizacion.setClaveAcceso(claveAcceso);
                        autorizacion.setAmbiente("2");

                        RespuestaComprobante result = null;
                        try {
                            result = autorizacionService.consulta(autorizacion);
                        } catch (Exception exception) {
                            log.info("El ws del SRI no esta disponible: " + exception.getMessage());
                        }

                        System.out.println("Comprobantes: " + result.getNumeroComprobantes());
                        System.out.println("Clave de acceso: " + result.getClaveAccesoConsultada());
                        System.out.println("Autorizaciones: " + result.getAutorizaciones());
                        System.out.println(result.getAutorizaciones().getAutorizacion());
                        List<autorizacion.ws.sri.gob.ec.Autorizacion> listaAutorizaciones = result.getAutorizaciones().getAutorizacion();
                        if (result.getNumeroComprobantes() == null) {
                            listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                                    .claveAcceso(claveAcceso)
                                    .error("Clave de acceso no encontrada")
                                    .exitoso("N")
                                    .build());
                        } else {
                            Autorizacion autorizacionDto = ProcesarClavesAutorizadoSri(listaAutorizaciones, listaRespuestas);
                            if (autorizacionDto.getEstado() != null) {

                                String message = deEmitidasComponentsService.guardarComprobante(idData, idEmpresa,
                                        campoAutorizacionBuilder.builder(autorizacionDto), filter.getSucursal(), usuario);

                                if (!message.isEmpty()) {
                                    listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                                            .claveAcceso(claveAcceso)
                                            .exitoso("N")
                                            .error(message)
                                            .build());
                                }
                            }
                        }
                    }
                } else {
                    listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                            .claveAcceso(claveAcceso)
                            .exitoso("N")
                            .error("Solo se permite guardar comprobantes de retencion emitidos")
                            .build());

                }
            } else {
                listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                        .claveAcceso(claveAcceso)
                        .exitoso("N")
                        .error("Clave de acceso incorrecta")
                        .build());
            }

        });

        return CpImpuestosRecibirListCreationResponseDto
                .builder()
                .resultados(listaRespuestas)
                .build();
    }

    /**
     * Lee el archivo tabulado (formato de exportacion del SRI: cabecera + filas separadas por TAB)
     * y extrae los valores de la columna CLAVE_ACCESO, sin duplicados y en el orden en que aparecen.
     */
    private List<String> extraerClavesAccesoDeArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException("El archivo con las claves de acceso es requerido");
        }

        Set<String> clavesAcceso = new LinkedHashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String encabezado = reader.readLine();
            if (encabezado == null || encabezado.trim().isEmpty()) {
                throw new GeneralException("El archivo esta vacio");
            }

            String[] columnas = encabezado.split("\t", -1);
            int indiceClaveAcceso = -1;
            for (int i = 0; i < columnas.length; i++) {
                if (COLUMNA_CLAVE_ACCESO.equalsIgnoreCase(columnas[i].trim())) {
                    indiceClaveAcceso = i;
                    break;
                }
            }

            if (indiceClaveAcceso == -1) {
                throw new GeneralException("El archivo no contiene la columna " + COLUMNA_CLAVE_ACCESO);
            }

            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linea.split("\t", -1);
                if (campos.length > indiceClaveAcceso) {
                    String claveAcceso = campos[indiceClaveAcceso].trim();
                    if (!claveAcceso.isEmpty()) {
                        clavesAcceso.add(claveAcceso);
                    }
                }
            }
        } catch (IOException exception) {
            throw new GeneralException("Error al leer el archivo con las claves de acceso: " + exception.getMessage());
        }

        if (clavesAcceso.isEmpty()) {
            throw new GeneralException("No se encontraron claves de acceso en el archivo");
        }

        return new ArrayList<>(clavesAcceso);
    }


    public Autorizacion ProcesarClavesAutorizadoSri(List<autorizacion.ws.sri.gob.ec.Autorizacion> listaAutorizaciones, List<CpImpuestosRecibirResponseDto> listaRespuestas) {
        Autorizacion autorizacionDto = new Autorizacion();
        for (autorizacion.ws.sri.gob.ec.Autorizacion autorizacion : listaAutorizaciones) {

            if (autorizacion.getEstado().toUpperCase().compareTo("AUTORIZADO") == 0) {
                System.out.println("ENCONTRO UNO AUTORIZADO");

                autorizacionDto.setEstado(autorizacion.getEstado());
                autorizacionDto.setNumeroAutorizacion(autorizacion.getNumeroAutorizacion());
                autorizacionDto.setFechaAutorizacion(autorizacion.getFechaAutorizacion().toString());
                autorizacionDto.setAmbiente(autorizacion.getAmbiente());
                autorizacionDto.setComprobante((new StringBuilder()).append(autorizacion.getComprobante()).toString());

                Mensaje Det1 = new Mensaje();
                Det1.setIdentificador("60");
                Det1.setMensaje("ESTE PROCESO FUE REALIZADO EN EL AMBIENTE DE PRUEBAS");
                Det1.setTipo("INFORMATIVO");

                List<Mensaje> listaMensaje = new ArrayList<Mensaje>();
                listaMensaje.add(Det1);
                autorizacionDto.setMensaje(listaMensaje);

            }
        }
        return autorizacionDto;
    }

}
