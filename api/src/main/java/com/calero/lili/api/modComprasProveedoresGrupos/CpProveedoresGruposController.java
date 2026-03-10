package com.calero.lili.api.modComprasProveedoresGrupos;

import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoCreationRequestDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoCreationResponseDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoListFilterDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoReportDto;
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
@RequestMapping("api/v1.0/proveedores/grupos")
@CrossOrigin(originPatterns = "*")

public class CpProveedoresGruposController {
    private final CpProveedoresGruposServiceImpl cpProveedoresGruposService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CP_PG_CR')")
    public CpProveedorGrupoCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @RequestBody CpProveedorGrupoCreationRequestDto request) {
        return cpProveedoresGruposService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PG_MO')")
    public CpProveedorGrupoCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("id") UUID id,
                                                      @RequestBody CpProveedorGrupoCreationRequestDto request) {
        return cpProveedoresGruposService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CP_PG_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        cpProveedoresGruposService.delete(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PG_VR')")
    public CpProveedorGrupoCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                        @PathVariable("id") UUID id) {
        return cpProveedoresGruposService.findFirstById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PG_VR')")
    public List<CpProveedorGrupoReportDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                           CpProveedorGrupoListFilterDto filters,
                                                           Pageable pageable) {

        return cpProveedoresGruposService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }


    // TODO PREGUNTAR SI VA SER EL MODIFICAR O VA SER OTRO PERMISO
    @PutMapping("predeterminado/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PG_MO')")
    public CpProveedorGrupoCreationResponseDto updatePredeterminado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                    @PathVariable("id") UUID id) {

        return cpProveedoresGruposService.updatePredeterminado(id, idDataService.getIdData(), idEmpresa);
    }

}
