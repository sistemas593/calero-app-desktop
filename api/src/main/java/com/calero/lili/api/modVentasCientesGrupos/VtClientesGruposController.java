package com.calero.lili.api.modVentasCientesGrupos;

import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoCreationRequestDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoCreationResponseDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoListFilterDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoReportDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
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

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/clientes/grupos")
@CrossOrigin(originPatterns = "*")

public class VtClientesGruposController {

    private final VtClientesGruposServiceImpl vtClientesGruposService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_CG_CR')")
    public VtClienteGrupoCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                    @RequestBody VtClienteGrupoCreationRequestDto request) {
        return vtClientesGruposService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CG_MO')")
    public VtClienteGrupoCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                    @PathVariable("id") UUID id,
                                                    @RequestBody VtClienteGrupoCreationRequestDto request) {
        return vtClientesGruposService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_CG_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        vtClientesGruposService.delete(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CG_VR')")
    public VtClienteGrupoCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("id") UUID id) {
        return vtClientesGruposService.findFirstById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CG_VR')")
    public List<VtClienteGrupoReportDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                         VtClienteGrupoListFilterDto filters,
                                                         Pageable pageable) {
        return vtClientesGruposService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }


    @PutMapping("predeterminado/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CG_MO')")
    public VtClienteGrupoCreationResponseDto updatePredeterminado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @PathVariable("id") UUID id) {
        return vtClientesGruposService.updatePredeterminado(id, idDataService.getIdData(), idEmpresa);
    }
}
