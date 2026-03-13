package com.calero.lili.api.modVentas.notasCredito;

import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.api.modVentas.notasCredito.dto.GetNotaCreditoDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class VtVentasNotasCreditoController {

    private final VtVentasNotasCreditoServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("notas-credito/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_NC_CR')")
    public ResponseDto createNotaCredito(@PathVariable("idEmpresa") Long idEmpresa,
                                         @Valid @RequestBody CreationNotaCreditoRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("notas-credito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_NC_MO')")
    public ResponseDto updateNotaCredito(@PathVariable("idEmpresa") Long idEmpresa,
                                         @PathVariable("idVenta") UUID idVenta,
                                         @RequestBody CreationNotaCreditoRequestDto request) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("notas-credito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_NC_EL')")
    public void deleteNotaCredito(@PathVariable("idEmpresa") Long idEmpresa,
                                  @PathVariable("idVenta") UUID idVenta) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("notas-credito/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_NC_VR')")
    public GetNotaCreditoDto findNotaCreditoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                 @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta);
    }


    @GetMapping("notas-credito/mensajes/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_NC_VR')")
    public List<Mensajes> findByMensajesForId(@PathVariable("idEmpresa") Long idEmpresa,
                                              @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.findByMensajes(idDataService.getIdData(), idEmpresa, idVenta);
    }


    @PostMapping("notas-credito/anulada/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAuthority('VT_NC_AN')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta);
    }

    @PostMapping("notas-credito/asiento/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAuthority('VT_NC_CR')")
    public ResponseDto createAsientoVenta(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.createAsientoNotaCredito(idDataService.getIdData(), idEmpresa, idVenta);
    }

}
