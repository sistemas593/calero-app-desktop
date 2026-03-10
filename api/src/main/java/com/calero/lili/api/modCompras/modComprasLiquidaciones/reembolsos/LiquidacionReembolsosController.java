package com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos;


import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.comprobantes.DocumentoRecibidosServiceImpl;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.FilterListDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.GetListDtoTotalizado;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.dto.ReembolsoRequestDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1.0/liquidaciones/reembolsos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class LiquidacionReembolsosController {


    private final LiquidacionesReembolsosServiceImpl reembolsosService;
    private final DocumentoRecibidosServiceImpl documentoRecibidosService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQR_CR')")
    public ResponseDto create(@Valid @RequestBody ReembolsoRequestDto request) {
        return reembolsosService.create(request);
    }

    @PutMapping("/update/{idReembolso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('LQ_LQR_MO')")
    public ResponseDto update(@PathVariable("idReembolso") UUID idReembolso, @Valid @RequestBody ReembolsoRequestDto request) {
        return reembolsosService.update(idReembolso, request);
    }

    @DeleteMapping("/delete/{idReembolso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('LQ_LQR_EL')")
    public void delete(@PathVariable("idReembolso") UUID idReembolso) {
        reembolsosService.delete(idReembolso);
    }

    @GetMapping("/findById/{idReembolso}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('LQ_LQR_VR')")
    public GetReembolsoDto findById(@PathVariable("idReembolso") UUID idVenta) {
        return reembolsosService.findById(idVenta);
    }


    @GetMapping("/findAllPaginate")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('LQ_LQR_VR')")
    public PaginatedDto<GetReembolsoDto> findAllPaginate(FilterListDto filters, Pageable pageable) {
        return reembolsosService.findAllPaginate(filters, pageable);
    }

    @GetMapping("/reportes")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('LQ_LQR_VR')")
    public GetListDtoTotalizado<GetReembolsoDto> findAllPaginateTotalizado(FilterListDto filters,
                                                                           Pageable pageable) {
        return reembolsosService.findAllPaginateTotalizado(filters, pageable);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAuthority('LQ_LQR_EX')")
    public void exportarExcel(HttpServletResponse response, FilterListDto filter) throws IOException {
        reembolsosService.exportarExcel(response, filter);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAuthority('LQ_LQR_EX')")
    public void exportarPDF(HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {
        reembolsosService.exportarPDF(response, filters);
    }

    @PostMapping("recibidos/files")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQR_IMXML')")
    public CpImpuestosRecibirListCreationResponseDto recibirFiles(@Valid @RequestBody List<MultipartFile> documentos) {
        return documentoRecibidosService.createFilesLiqReembolso(idDataService.getIdData(), documentos);
    }


}
