package com.calero.lili.api.modTesoreria.TsComprobanteIngreso;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.ComprobanteIngresoFilterDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.RequestCreationComprobanteIngresoDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.ResponseComprobanteIngresoDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/comprobante-ingreso")
@RequiredArgsConstructor
public class TsComprobanteIngresoController {

    private final TsComprobanteIngresoServiceImpl tsComprobanteIngresoService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa, @Valid @RequestBody RequestCreationComprobanteIngresoDto request) {
        return tsComprobanteIngresoService.create(idDataService.getIdData(), idEmpresa, UUID.randomUUID(), request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idComprobante}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idComprobante") UUID idComprobante,
                              @RequestBody RequestCreationComprobanteIngresoDto request) {
        return tsComprobanteIngresoService.update(idDataService.getIdData(), idEmpresa, idComprobante, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idComprobante}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idComprobante") UUID idComprobante) {
        tsComprobanteIngresoService.delete(idDataService.getIdData(), idEmpresa, idComprobante, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idComprobante}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseComprobanteIngresoDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                  @PathVariable("idComprobante") UUID idComprobante) {
        return tsComprobanteIngresoService.findById(idDataService.getIdData(), idEmpresa, idComprobante);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<ResponseComprobanteIngresoDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                       ComprobanteIngresoFilterDto filters,
                                                                       Pageable pageable) {
        return tsComprobanteIngresoService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);

    }


    @PostMapping("pdf/{idEmpresa}/{idComprobante}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createComprobanteIngresoPdf(HttpServletResponse response,
                                            @PathVariable("idEmpresa") Long idEmpresa,
                                            @PathVariable("idComprobante") UUID idComprobante) {
        tsComprobanteIngresoService.generateComprobanteIngresoPdf(idDataService.getIdData(), idEmpresa, idComprobante, response);

    }

}
