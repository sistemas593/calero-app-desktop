package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStDatosRequestDto;
import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStFechaActualizacionCreationRequestDto;
import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStFechaActualizacionGetDto;
import com.calero.lili.core.apiSitac.repositories.StFechaActualizacionRepository;
import com.calero.lili.core.apiSitac.repositories.entities.StFechaActualizacionEntity;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesEntity;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesRepository;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class StFechaActualizacionServiceImpl {

    private final StFechaActualizacionRepository adStFechaActualizacion;
    private final VtClientesConfiguracionesRepository adStEmpresas;

    public AdStFechaActualizacionGetDto update(String idSistema, AdStFechaActualizacionCreationRequestDto request) {
        Optional<StFechaActualizacionEntity> existing = adStFechaActualizacion.findById(idSistema);
        if  (!existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("id number {0} no exists", idSistema ));
        }
         StFechaActualizacionEntity entidad = findFirstById(idSistema);
        entidad.setFechaActualizacion(request.getFechaActualizacion());
        //entidad.setBirthday(DateUtils.toLocalDate(request.getBirthday()));
         StFechaActualizacionEntity updated = adStFechaActualizacion.save(entidad);
        return toDto(updated);
    }

    public AdStFechaActualizacionGetDto findByClave(String clave) {
        StFechaActualizacionEntity entidad = adStFechaActualizacion.findById("sitac")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists sitac" ,"sitac" )));
        //        if (!entidad.getIdSistema().equals(clave)){
//            throw new GeneralException(MessageFormat.format("Id {1} no exists", clave));
//        }

        AdStFechaActualizacionGetDto dto = new AdStFechaActualizacionGetDto();
        dto.setIdSistema(entidad.getIdSistema());
        dto.setFechaActualizacion(entidad.getFechaActualizacion());
        // POR DEFECTO PONGO N PUEDE ENVIAR CORREOS, SI PUEDE ABAJO LE CAMBIO
        dto.setEnviarCorreos("N");

        dto.setLink(entidad.getLink());
        if (!clave.equals("XXXXXXXXXX") ) {
            System.out.println(clave);
            Long idData = Long.valueOf(1);
            Optional<VtClientesConfiguracionesEntity> usuario = adStEmpresas.findById(idData, clave);
            if (usuario.isPresent()) {
                dto.setFechaVencimiento(DateUtils.toString(usuario.get().getFechaVencimiento()));
                dto.setFechaVencimiento(dto.getFechaVencimiento().substring(6,10)+"-"+dto.getFechaVencimiento().substring(3,5)+"-"+dto.getFechaVencimiento().substring(0,2));
                dto.setEnviarCorreos(usuario.get().getEnviarCorreos());

                dto.setModulos(usuario.get().getModulos());
                dto.setRucsActivados(usuario.get().getRucsActivados());
                dto.setClavesPcs(usuario.get().getClavesPcs());
                dto.setTipoBlo("");
                if (usuario.get().getFechaBlo().isBefore(LocalDate.now())){
                    dto.setTipoBlo("");
                    Long tipoBlo = usuario.get().getTipoBlo();
                    if (tipoBlo == 1) {
                        dto.setTipoBlo("FACELEC");
                    } else if (tipoBlo == 9) {
                        dto.setTipoBlo("TOTAL");
                        //default:
                    }
                }
            }
        }else{
            // para version gratuita
            dto.setFechaVencimiento("2050-12-31");
        }

        return dto;
    }

    public AdStFechaActualizacionGetDto findFechaActualizacion() {

        StFechaActualizacionEntity entidad = adStFechaActualizacion.findById("sitac")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists sitac", "" )));

        AdStFechaActualizacionGetDto dto = new AdStFechaActualizacionGetDto();
        dto.setIdSistema(entidad.getIdSistema());
        dto.setFechaActualizacion(entidad.getFechaActualizacion());

        dto.setLink(entidad.getLink());
            // para version gratuita
            dto.setFechaVencimiento("2050-12-31");
            dto.setEnviarCorreos("N");

        return dto;
    }

    public AdStFechaActualizacionGetDto findFechaActualizacionSitacfacturador() {

        StFechaActualizacionEntity entidad = adStFechaActualizacion.findById("sitacfacturador")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists sitacfacturador", "" )));

        AdStFechaActualizacionGetDto dto = new AdStFechaActualizacionGetDto();
        dto.setIdSistema(entidad.getIdSistema());
        dto.setFechaActualizacion(entidad.getFechaActualizacion());

        dto.setLink(entidad.getLink());
        // para version gratuita
        dto.setFechaVencimiento("2050-12-31");
        dto.setEnviarCorreos("N");

        return dto;
    }


    public AdStFechaActualizacionGetDto findByRuc(String ruc, AdStDatosRequestDto request) {
        StFechaActualizacionEntity entidad = adStFechaActualizacion.findById("sitac")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists sitac","" )));
//        if (!entidad.getIdSistema().equals(ruc)){
//            throw new GeneralException(MessageFormat.format("Id {1} no exists", ruc));
//        }

        AdStFechaActualizacionGetDto dto = new AdStFechaActualizacionGetDto();
        dto.setIdSistema(entidad.getIdSistema());
        dto.setFechaActualizacion(entidad.getFechaActualizacion());
        dto.setLink(entidad.getLink());

        if (ruc.equals("1212121212001")) {
            // para version gratuita
            dto.setFechaVencimiento("2050-12-31");
            dto.setEnviarCorreos("N");
            dto.setInfoRuc("Version gratuita");
        }else{
            Long idData = Long.valueOf(1);
            Optional<VtClientesConfiguracionesEntity> usuario = adStEmpresas.findByRuc(idData, ruc);
            if (usuario.isPresent()) {
                dto.setInfoRuc("Ruc encontrado, dentro del periodo");
                dto.setEnviarCorreos(usuario.get().getEnviarCorreos());
                dto.setFechaVencimiento(DateUtils.toString(usuario.get().getFechaVencimiento()));
                dto.setFechaVencimiento(dto.getFechaVencimiento().substring(6,10)+"-"+dto.getFechaVencimiento().substring(3,5)+"-"+dto.getFechaVencimiento().substring(0,2));
                if (LocalDate.now().compareTo(usuario.get().getFechaVencimiento()) > 0){
                    dto.setInfoRuc("Ruc encontrado, fuera del periodo");
                    dto.setLink("");
                    dto.setEnviarCorreos("N");
                }
            }else{
                dto.setInfoRuc("Ruc no registrado");
                dto.setEnviarCorreos("N");
                dto.setFechaVencimiento("2000-01-01");
                dto.setLink("");
            }
        }

        return dto;
    }

    private StFechaActualizacionEntity findFirstById(String idSistema) {
        return adStFechaActualizacion.findById(idSistema)
                .orElseThrow(() -> new RuntimeException(format("Unable to find id {0}", idSistema)));
    }
    private StFechaActualizacionEntity toEntity(String id, AdStFechaActualizacionCreationRequestDto request) {
        StFechaActualizacionEntity entidad = new StFechaActualizacionEntity();
        entidad.setIdSistema(id);
        entidad.setFechaActualizacion(request.getFechaActualizacion());

        //entidad.setBirthday(DateUtils.toLocalDate(request.getBirthday()));
        //entidad.setInsertionDate(LocalDateTime.now());
        //entidad.setIdentificationNumber(request.getIdentificationNumber());
        return entidad;
    }
    private AdStFechaActualizacionGetDto toDto(StFechaActualizacionEntity entity) {
        AdStFechaActualizacionGetDto dto = new AdStFechaActualizacionGetDto();
        dto.setIdSistema(entity.getIdSistema());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        //dto.setId(entity.getId());
        return dto;
    }

}
