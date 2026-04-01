package com.calero.lili.core.modComprasItemsImpuesto;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modComprasItemsImpuesto.builder.GeImpuestosBuilder;
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoRequestDto;
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@AllArgsConstructor
public class GeImpuestoServiceImpl {

    private final GeImpuestosBuilder geImpuestosBuilder;
    private final GeImpuestosRepository geImpuestoRepository;


    public GeImpuestoResponseDto create(GeImpuestoRequestDto model) {
        return geImpuestosBuilder.builderResponse(geImpuestoRepository
                .save(geImpuestosBuilder.builderEntity(model)));
    }


    public GeImpuestoResponseDto update(Long idImpuesto, GeImpuestoRequestDto model) {
        GeImpuestosEntity entidad = geImpuestoRepository.findById(idImpuesto)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id impuesto no existe {0}", idImpuesto)));
        return geImpuestosBuilder.builderResponse(geImpuestoRepository.save(geImpuestosBuilder
                .builderUpdateEntity(entidad, model)));
    }


    public GeImpuestoResponseDto findById(Long idImpuesto) {
        return geImpuestosBuilder.builderResponse(geImpuestoRepository.findById(idImpuesto)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("Id impuesto no existe {0}", idImpuesto))));
    }


    public List<GeImpuestoResponseDto> findAll() {
        return geImpuestoRepository.findAll()
                .stream()
                .map(geImpuestosBuilder::builderResponse)
                .toList();
    }


    public void delete(Long idImpuesto) {

        GeImpuestosEntity entity = geImpuestoRepository.findById(idImpuesto)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("Id impuesto no existe {0}", idImpuesto)));
        geImpuestoRepository.delete(entity);

    }
}
