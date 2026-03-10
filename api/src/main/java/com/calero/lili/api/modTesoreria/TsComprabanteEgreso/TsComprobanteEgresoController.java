package com.calero.lili.api.modTesoreria.TsComprabanteEgreso;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.RequestComprobantesEgresoDto;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.ResponseComprobantesEgresoDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto.ComprobanteIngresoFilterDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(value = "api/v1.0/comprobante-egreso")
@RequiredArgsConstructor
public class TsComprobanteEgresoController {


    private final TsComprobanteEgresoServiceImpl tsComprobanteEgresoService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody RequestComprobantesEgresoDto request) {
        return tsComprobanteEgresoService.create(idDataService.getIdData(), idEmpresa, UUID.randomUUID(), request);
    }

    @PutMapping("{idEmpresa}/{idComprobante}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idComprobante") UUID idComprobante,
                              @RequestBody RequestComprobantesEgresoDto request) {
        return tsComprobanteEgresoService.update(idDataService.getIdData(), idEmpresa, idComprobante, request);
    }

    @DeleteMapping("{idEmpresa}/{idComprobante}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idComprobante") UUID idComprobante) {
        tsComprobanteEgresoService.delete(idDataService.getIdData(), idEmpresa, idComprobante);
    }

    @GetMapping("{idEmpresa}/{idComprobante}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseComprobantesEgresoDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                  @PathVariable("idComprobante") UUID idComprobante) {
        return tsComprobanteEgresoService.findById(idDataService.getIdData(), idEmpresa, idComprobante);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<ResponseComprobantesEgresoDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                       ComprobanteIngresoFilterDto filters,
                                                                       Pageable pageable) {
        return tsComprobanteEgresoService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }


    @PostMapping("pdf/{idEmpresa}/{idComprobante}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createComprobanteEgresoPdf(HttpServletResponse response,
                                           @PathVariable("idEmpresa") Long idEmpresa,
                                           @PathVariable("idComprobante") UUID idComprobante) {
        tsComprobanteEgresoService.generateComprobanteEgresoPdf(idDataService.getIdData(), idEmpresa, idComprobante, response);
    }
}
