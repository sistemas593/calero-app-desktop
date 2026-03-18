package com.calero.lili.core.modAdminEmpresasSeries.builder;

import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieCreationRequestDto;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieGetDto;
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieGetListDto;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class AdEmpresasSeriesBuilder {

    public AdEmpresasSeriesEntity builderEntity(AdEmpresaSerieCreationRequestDto model, Long idEmpresa, Long idData) {
        return AdEmpresasSeriesEntity.builder()
                .idSerie(UUID.randomUUID())
                .idEmpresa(idEmpresa)
                .idData(idData)
                .serie(model.getSerie())
                .nombreComercial(model.getNombreComercial())
                .direccionEstablecimiento(model.getDireccionEstablecimiento())
                .ciudad(model.getCiudad())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .documentosEntity(builderList(model.getDocumentos(), idEmpresa, idData, model.getSerie()))
                .build();
    }

    public AdEmpresasSeriesEntity builderUpdateEntity(AdEmpresaSerieCreationRequestDto model, AdEmpresasSeriesEntity entidad) {
        return AdEmpresasSeriesEntity.builder()
                .idSerie(entidad.getIdSerie())
                .idEmpresa(entidad.getIdEmpresa())
                .idData(entidad.getIdData())
                .serie(model.getSerie())
                .nombreComercial(model.getNombreComercial())
                .direccionEstablecimiento(model.getDireccionEstablecimiento())
                .ciudad(model.getCiudad())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .documentosEntity(builderList(model.getDocumentos(), entidad.getIdEmpresa(), entidad.getIdData(), model.getSerie()))
                .build();
    }

    private List<AdEmpresasSeriesDocumentosEntity> builderList(List<AdEmpresaSerieCreationRequestDto.Documentos> list, Long idEmpresa, Long idData, String serie) {
        List<AdEmpresasSeriesDocumentosEntity> response = new ArrayList<>();
        for (AdEmpresaSerieCreationRequestDto.Documentos model : list) {
            response.add(AdEmpresasSeriesDocumentosEntity.builder()
                    .idData(idData)
                    .idEmpresa(idEmpresa)
                    .idDocumento(UUID.randomUUID())
                    .documento(model.getDocumento())
                    .numeroAutorizacion(model.getNumeroAutorizacion())
                    .secuencial(model.getSecuencial())
                    .formatoDocumento(model.getFormatoDocumento())
                    .desde(model.getDesde())
                    .hasta(model.getHasta())
                    .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                            ? DateUtils.toLocalDate(model.getFechaVencimiento()) : null)
                    .build());
        }
        return response;
    }

    public AdEmpresaSerieGetDto builderResponse(AdEmpresasSeriesEntity model) {
        return AdEmpresaSerieGetDto.builder()
                .idSerie(model.getIdSerie())
                .serie(model.getSerie())
                .nombreComercial(model.getNombreComercial())
                .direccionEstablecimiento(model.getDireccionEstablecimiento())
                .ciudad(model.getCiudad())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .documentos(builderListDocumentosResponse(model.getDocumentosEntity()))
                .build();
    }

    private List<AdEmpresaSerieGetDto.Documentos> builderListDocumentosResponse(List<AdEmpresasSeriesDocumentosEntity> list) {
        return list.stream()
                .map(this::builderResponseDocumentos)
                .toList();
    }

    private AdEmpresaSerieGetDto.Documentos builderResponseDocumentos(AdEmpresasSeriesDocumentosEntity model) {
        return AdEmpresaSerieGetDto.Documentos.builder()
                .idDocumento(model.getIdDocumento())
                .documento(model.getDocumento())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .secuencial(model.getSecuencial())
                .formatoDocumento(model.getFormatoDocumento())
                .desde(model.getDesde())
                .hasta(model.getHasta())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .build();
    }

    public AdEmpresaSerieGetListDto builderResponseList(AdEmpresasSeriesEntity model) {
        return AdEmpresaSerieGetListDto.builder()
                .idSerie(model.getIdSerie())
                .serie(model.getSerie())
                .nombreComercial(model.getNombreComercial())
                .direccionEstablecimiento(model.getDireccionEstablecimiento())
                .ciudad(model.getCiudad())
                .telefono1(model.getTelefono1())
                .telefono2(model.getTelefono2())
                .documentos(builderListResponseListDocumentos(model.getDocumentosEntity()))
                .build();
    }

    private List<AdEmpresaSerieGetListDto.Documentos> builderListResponseListDocumentos(List<AdEmpresasSeriesDocumentosEntity> list) {
        return list.stream()
                .map(this::builderDocumentosList)
                .toList();
    }

    private AdEmpresaSerieGetListDto.Documentos builderDocumentosList(AdEmpresasSeriesDocumentosEntity model) {
        return AdEmpresaSerieGetListDto.Documentos.builder()
                .documento(model.getDocumento())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .secuencial(model.getSecuencial())
                .formatoDocumento(model.getFormatoDocumento())
                .desde(model.getDesde())
                .hasta(model.getHasta())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .build();
    }


}
