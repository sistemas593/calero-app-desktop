package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.adLogs.AdLogsServiceImpl;
import com.calero.lili.core.adLogs.dto.AdLogsDtoResponse;
import com.calero.lili.core.dtos.FilterFechasDto;
import com.calero.lili.core.dtos.PaginatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/logs")
@CrossOrigin(originPatterns = "*")
public class AdLogsController {

    private final AdLogsServiceImpl adLogsService;
    private final IdDataServiceImpl idDataService;


    @GetMapping("paginado/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_CC_VR')")
    public PaginatedDto<AdLogsDtoResponse> findAll(@PathVariable("idEmpresa") Long idEmpresa,
                                                   FilterFechasDto filters,
                                                   Pageable pageable) {
        return adLogsService.findAllPageable(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
