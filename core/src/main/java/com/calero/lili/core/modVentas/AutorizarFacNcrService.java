package com.calero.lili.core.modVentas;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.adLogs.dto.AdLogsRequestDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.services.BuscarDatosEmpresa;
import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AutorizarFacNcrService {


    private final VtVentasRepository vtVentaRepository;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final AdLogsBuilder adLogsBuilder;
    private ProcesarDocumentosServiceImpl procesarDocumentosService;

    public List<VtVentaEntity> getDocumentosAutorizar(Long idData, Long idEmpresa) {
        return vtVentaRepository.obtenerTodosParaAutorizar(idData, idEmpresa);
    }


    public void procesarDocumento(Long idData, Long idEmpresa, VtVentaEntity factura) {
        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(idData, idEmpresa);
        datosEmpresaDto.setOrigenDatos("LOC");
        AdLogsRequestDto log = adLogsBuilder.builderVentasDocumentos(factura, Boolean.FALSE);
        procesarDocumentosService.procesarFacNcNd(factura, log, datosEmpresaDto);
    }

}
