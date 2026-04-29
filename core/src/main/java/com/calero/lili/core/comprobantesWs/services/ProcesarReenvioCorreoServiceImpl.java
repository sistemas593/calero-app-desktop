package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.apiSitac.services.EmailSender;
import com.calero.lili.core.apiSitac.services.GenerarBody;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.EnvioCorreoDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.dto.FilterEmailDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modClientesConfiguraciones.dto.StCorreoRequestDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.LiquidacionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiasRepository;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ProcesarReenvioCorreoServiceImpl {
    private final VtVentasRepository vtVentaRepository;
    private final VtGuiasRepository vtGuiasRepository;
    private final LiquidacionesRepository liquidacionesRepository;
    private final ComprasRetencionesRepository comprasRetencionesRepository;

    private final SetearCorreoServiceImpl procesarEnvioCorreoService;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final GenerarBody generarBody;
    private final AdMailsConfigRepository adConfigRepository;
    private final EmailSender emailSender;

    public void procesarVentasReenvioCorreo(Long idData, Long idEmpresa, UUID id, FilterEmailDto filter) {

        System.out.println("Reenviando correo");

        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        VtVentaEntity venta1 = vtVentaRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
        envioCorreoDto.setComprobante(venta1.getComprobante());
        envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacion());
        envioCorreoDto.setFechaAutorizacion(DateUtils.toLocalDateTimeString(venta1.getFechaAutorizacion()));
        envioCorreoDto.setNombreReceptor(venta1.getTercero().getTercero());

        switch (venta1.getTipoVenta()) {
            case "FAC":
                envioCorreoDto.setCodigoDocumento("01");
                break;
            case "NCR":
                envioCorreoDto.setCodigoDocumento("04");
                break;
            case "NDB":
                envioCorreoDto.setCodigoDocumento("05");
        }

        envioCorreoDto.setSecuencial(venta1.getSecuencial());
        envioCorreoDto.setSerie(venta1.getSerie());
        envioCorreoDto.setFechaEmision(DateUtils.toStringFechaEmision(venta1.getFechaEmision()));
        envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
        envioCorreoDto.setEmail(filter.getCorreo());

        StCorreoRequestDto request = procesarEnvioCorreoService.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
        AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
        String jsonBody = generarBody.generarBodyCorreo(request, adConfigMailEntity);
        emailSender.send(jsonBody, adConfigMailEntity);
        System.out.println("Correo reenviado");

    }

    public void procesarGuiasRemisionReenvioCorreo(Long idData, Long idEmpresa, UUID id, FilterEmailDto filter) {

        System.out.println("Reenviando correo");
        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        VtGuiaEntity venta1 = vtGuiasRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
        envioCorreoDto.setComprobante(venta1.getComprobante());
        envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacion());
        envioCorreoDto.setFechaAutorizacion(DateUtils.toLocalDateTimeString(venta1.getFechaAutorizacion()));
        envioCorreoDto.setNombreReceptor("");
        envioCorreoDto.setCodigoDocumento(venta1.getCodigoDocumento());
        envioCorreoDto.setSecuencial(venta1.getSecuencial());
        envioCorreoDto.setSerie(venta1.getSerie());
        envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmision()));
        envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
        envioCorreoDto.setEmail(filter.getCorreo());

        StCorreoRequestDto request = procesarEnvioCorreoService.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
        AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
        String jsonBody = generarBody.generarBodyCorreo(request, adConfigMailEntity);
        emailSender.send(jsonBody, adConfigMailEntity);
        System.out.println("Correo reenviado");

    }

    public void procesarLiquidacionesReenvioCorreo(Long idData, Long idEmpresa, UUID id, FilterEmailDto filter) {

        System.out.println("Reenviando correo");
        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        CpLiquidacionesEntity venta1 = liquidacionesRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
        envioCorreoDto.setComprobante(venta1.getComprobante());
        envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacion());
        envioCorreoDto.setFechaAutorizacion(DateUtils.toLocalDateTimeString(venta1.getFechaAutorizacion()));
        envioCorreoDto.setNombreReceptor("");
        envioCorreoDto.setCodigoDocumento("03");
        envioCorreoDto.setSecuencial(venta1.getSecuencial());
        envioCorreoDto.setSerie(venta1.getSerie());
        envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmision()));
        envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
        envioCorreoDto.setEmail(filter.getCorreo());

        StCorreoRequestDto request = procesarEnvioCorreoService.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
        AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
        String jsonBody = generarBody.generarBodyCorreo(request, adConfigMailEntity);
        emailSender.send(jsonBody, adConfigMailEntity);
        System.out.println("Correo reenviado");

    }

    public void procesarComprobantesRetencionReenvioCorreo(Long idData, Long idEmpresa, UUID id, FilterEmailDto filter) {

        System.out.println("Reenviando correo");
        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        CpRetencionesEntity venta1 = comprasRetencionesRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
        envioCorreoDto.setComprobante(venta1.getComprobante());
        envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacionRetencion());
        envioCorreoDto.setFechaAutorizacion(DateUtils.toLocalDateTimeString(venta1.getFechaAutorizacion()));
        envioCorreoDto.setNombreReceptor("");
        envioCorreoDto.setCodigoDocumento(venta1.getCodigoDocumento());
        envioCorreoDto.setSecuencial(venta1.getSecuencialRetencion());
        envioCorreoDto.setSerie(venta1.getSerieRetencion());
        envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmisionRetencion()));
        envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
        envioCorreoDto.setEmail(filter.getCorreo());

        StCorreoRequestDto request = procesarEnvioCorreoService.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
        AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
        String jsonBody = generarBody.generarBodyCorreo(request, adConfigMailEntity);
        emailSender.send(jsonBody, adConfigMailEntity);
        System.out.println("Correo reenviado");

    }


}
