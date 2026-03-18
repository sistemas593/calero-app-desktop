package com.calero.lili.core.tablas.tbPaises;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TbPaisesServiceImpl {

    private final TbPaisesRepository tbRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final TbPaisBuilder tbPaisBuilder;

    public ResponseDto create(TbPaisCreationRequestDto request) {
        Optional<TbPaisEntity> existing = tbRepository.findById(request.getCodigoPais());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Documento {0} ya existe", request.getCodigoPais()));
        }
        return responseApiBuilder.builderResponse(tbRepository.save(tbPaisBuilder.builderEntity(request)).getCodigoPais());
    }

    public ResponseDto update(String id, TbPaisCreationRequestDto request) {
        TbPaisEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return responseApiBuilder.builderResponse(tbRepository.save(tbPaisBuilder
                .builderUpdateEntity(request, entidad)).getCodigoPais());
    }

    public void delete(String codigoDocumento) {
        tbRepository.deleteById(codigoDocumento);
    }

    public TbPaisGetOneDto findById(String id) {
        TbPaisEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return tbPaisBuilder.builderResponse(entidad);
    }

    public List<TbPaisGetListDto> findAll(FilterDto filters, Pageable pageable) {
        return tbRepository.findAll(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "")
                .stream()
                .map(tbPaisBuilder::builderResponseList)
                .toList();
    }

}
