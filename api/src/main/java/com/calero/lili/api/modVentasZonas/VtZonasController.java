package com.calero.lili.api.modVentasZonas;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modVentasZonas.dto.VtZonaCreationRequestDto;
import com.calero.lili.api.modVentasZonas.dto.VtZonaGetListDto;
import com.calero.lili.api.modVentasZonas.dto.VtZonaGetOneDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/zonas")
@CrossOrigin(originPatterns = "*")

public class VtZonasController {

    private final VtZonasServiceImpl vtZonasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_ZO_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @RequestBody VtZonaCreationRequestDto request) {
        return vtZonasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_ZO_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("id") UUID id,
                              @RequestBody VtZonaCreationRequestDto request) {
        return vtZonasService.update(idDataService.getIdData(), idEmpresa, id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_ZO_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        vtZonasService.delete(idDataService.getIdData(), idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_ZO_VR')")
    public VtZonaGetOneDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                    @PathVariable("id") UUID id) {
        return vtZonasService.findById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_ZO_VR')")
    public PaginatedDto<VtZonaGetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                          FilterDto filters,
                                                          Pageable pageable) {
        return vtZonasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
