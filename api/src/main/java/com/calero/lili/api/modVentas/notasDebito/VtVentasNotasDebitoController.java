package com.calero.lili.api.modVentas.notasDebito;

import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modVentas.notasDebito.dto.CreationNotaDebitoRequestDto;
import com.calero.lili.api.modVentas.notasDebito.dto.GetNotaDebitoDto;
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

public class VtVentasNotasDebitoController {
    private final VtVentasNotasDebitoServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("notas-debito/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_ND_CR')")
    public ResponseDto createNotaDebito(@PathVariable("idEmpresa") Long idEmpresa,
                                        @Valid @RequestBody CreationNotaDebitoRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("notas-debito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_ND_MO')")
    public ResponseDto updateNotaDebito(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("idVenta") UUID idVenta,
            @RequestBody CreationNotaDebitoRequestDto request) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("notas-debito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_ND_EL')")
    public void deleteNotaDebito(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("idVenta") UUID idVenta) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("notas-debito/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_ND_VR')")
    public GetNotaDebitoDto findNotaDebitoById(@PathVariable("idEmpresa") Long idEmpresa,
                                               @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta);
    }


    @GetMapping("notas-debito/mensajes/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_ND_VR')")
    public List<Mensajes> findByIdMensajes(@PathVariable("idEmpresa") Long idEmpresa,
                                           @PathVariable("idVenta") UUID idVenta) {

        return vtVentasService.findByIdMensajes(idDataService.getIdData(), idEmpresa, idVenta);
    }

    @PostMapping("notas-debito/anulada/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAuthority('VT_ND_AN')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta);
    }


}
