package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.utils.ConstanteReportes;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MayorizacionServiceImpl {


    /**
     * Convierte la lista completa del plan de cuentas de la empresa en un mapa de {@link BalanceValoresDto}
     * indexado por el código de cuenta original.
     * <p>
     * - Inicializa valores numéricos a cero y copia propiedades relevantes desde {@link CnPlanCuentaEntity}.
     * - Marca si la cuenta es mayor y establece el indicador de movimiento en falso.
     *
     * @param cuentas lista de {@link CnPlanCuentaEntity} que representa el plan de cuentas de la empresa
     * @return mapa donde la clave es el código de cuenta original y el valor es el {@link BalanceValoresDto} inicializado
     */
    public Map<String, BalanceValoresDto> getMapBalanceCuentas(List<CnPlanCuentaEntity> cuentas) {
        Map<String, BalanceValoresDto> dtoMap = cuentas.stream()
                .collect(Collectors.toMap(
                        CnPlanCuentaEntity::getCodigoCuentaOriginal,
                        c -> {

                            BalanceValoresDto dto = new BalanceValoresDto();

                            dto.setIdCuenta(c.getIdCuenta());
                            dto.setCodigoCuenta(c.getCodigoCuenta());
                            dto.setCodigoCuentaOriginal(c.getCodigoCuentaOriginal());
                            dto.setCuenta(c.getCuenta());
                            dto.setSaldoInicial(BigDecimal.ZERO);
                            dto.setDebe(BigDecimal.ZERO);
                            dto.setHaber(BigDecimal.ZERO);
                            dto.setSaldoFinal(BigDecimal.ZERO);
                            dto.setEsMayor(c.getMayor());
                            dto.setTieneMovimiento(Boolean.FALSE);
                            dto.setGrupo(c.getGrupo());
                            dto.setEnero(BigDecimal.ZERO);
                            dto.setFebrero(BigDecimal.ZERO);
                            dto.setMarzo(BigDecimal.ZERO);
                            dto.setAbril(BigDecimal.ZERO);
                            dto.setMayo(BigDecimal.ZERO);
                            dto.setJunio(BigDecimal.ZERO);
                            dto.setJulio(BigDecimal.ZERO);
                            dto.setAgosto(BigDecimal.ZERO);
                            dto.setSeptiembre(BigDecimal.ZERO);
                            dto.setOctubre(BigDecimal.ZERO);
                            dto.setNoviembre(BigDecimal.ZERO);
                            dto.setDiciembre(BigDecimal.ZERO);
                            dto.setTotalMayor(Boolean.FALSE);
                            return dto;
                        }
                ));
        return dtoMap;
    }


    /**
     * Combina dos listas de {@link CnPlanCuentaEntity} (plan de cuentas y cuentas mayores) en una única lista
     * sin duplicados, preservando orden por código de cuenta.
     * <p>
     * - Cuando hay duplicados se conserva el primer elemento.
     * - El resultado se ordena por {@code codigoCuenta}.
     *
     * @param listCuentas lista principal de {@link CnPlanCuentaEntity}
     * @param listMayores lista de cuentas mayores que se desean añadir/totalizar
     * @return lista combinada y ordenada de {@link CnPlanCuentaEntity}
     */
    public List<CnPlanCuentaEntity> getCnPlanCuentaMerge(List<CnPlanCuentaEntity> listCuentas, List<CnPlanCuentaEntity> listMayores) {
        List<CnPlanCuentaEntity> merged = Stream.concat(listCuentas.stream(), listMayores.stream())
                .collect(Collectors.toMap(CnPlanCuentaEntity::getIdCuenta, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                .values().stream()
                .sorted(Comparator.comparing(CnPlanCuentaEntity::getCodigoCuenta))
                .toList();
        return merged;
    }

    /**
     * Construye un {@link PaginatedDto} a partir de una lista de {@link BalanceValoresDto} aplicando la
     * paginación y metadatos del {@link Pageable} proporcionado.
     * <p>
     * - Calcula la página usando {@link #getPageBalanceComprobacion(List, Pageable)} y rellena
     * el objeto {@link Paginator} con los metadatos.
     *
     * @param listResponse lista completa de {@link BalanceValoresDto} a paginar
     * @param pageable     parámetros de paginación y ordenamiento
     * @return {@link PaginatedDto} con el contenido y el paginador configurado
     */
    public PaginatedDto<BalanceValoresDto> getPageResponse(List<BalanceValoresDto> listResponse,
                                                           Pageable pageable) {


        PaginatedDto paginatedDto = new PaginatedDto<BalanceValoresDto>();
        Page<BalanceValoresDto> page = getPageBalanceComprobacion(listResponse, pageable);
        paginatedDto.setContent(listResponse);

        Paginator paginated = new Paginator();
        paginated.setTotalElements(page.getTotalElements());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setNumberOfElements(page.getNumberOfElements());
        paginated.setSize(page.getSize());
        paginated.setFirst(page.isFirst());
        paginated.setLast(page.isLast());
        paginated.setPageNumber(page.getPageable().getPageNumber());
        paginated.setPageSize(page.getPageable().getPageSize());
        paginated.setEmpty(page.isEmpty());
        paginated.setNumber(page.getNumber());

        paginatedDto.setPaginator(paginated);

        return paginatedDto;

    }

    /**
     * Obtiene la sublista correspondiente a la página solicitada a partir de la lista completa.
     * <p>
     * - Calcula índices de inicio/fin en base a {@link Pageable#getOffset()} y {@link Pageable#getPageSize()}.
     * - Devuelve un {@link Page} con la sublista y el total original.
     *
     * @param listResponse lista completa de {@link BalanceValoresDto}
     * @param pageable     parámetros de paginación
     * @return {@link Page} conteniendo la página solicitada
     */
    private Page<BalanceValoresDto> getPageBalanceComprobacion(List<BalanceValoresDto> listResponse, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listResponse.size());

        List<BalanceValoresDto> pagina =
                listResponse.subList(Math.min(start, end), end);

        return new PageImpl<>(pagina, pageable, listResponse.size());
    }


    /**
     * Realiza la mayorización: propaga un valor a través de la jerarquía de cuentas indicada.
     * <p>
     * - Para cada código en {@code listaAMayorizar} busca el {@link BalanceValoresDto} en {@code mapCuentasOriginales}.
     * - Marca el registro como con movimiento y acumula {@code valor} en la columna indicada por {@code columna}
     * (por ejemplo: saldoInicial, debe, haber o meses).
     * - También actualiza el {@code saldoFinal} según la columna afectada.
     *
     * @param mapCuentasOriginales mapa de cuentas (clave = código de cuenta original) a actualizar
     * @param listaAMayorizar      lista de códigos a los que debe sumarse el valor (incluye padres y totalizadores)
     * @param columna              nombre de la columna a actualizar (valores definidos en {@link ConstanteReportes})
     * @param valor                valor a sumar (puede ser positivo o negativo) sobre la columna seleccionada
     */
    public void mayorizacion(Map<String, BalanceValoresDto> mapCuentasOriginales, List<String> listaAMayorizar,
                             String columna, BigDecimal valor) {

        for (String codigoCuenta : listaAMayorizar) {

            BalanceValoresDto balance = mapCuentasOriginales.get(codigoCuenta);
            if (Objects.nonNull(balance)) {

                if (balance.getCodigoCuentaOriginal().contains("999999999")) {
                    BalanceValoresDto mayores = mapCuentasOriginales.get(balance.getCodigoCuentaOriginal()
                            .replace("999999999", ""));
                    if (mayores != null) {
                        mayores.setTieneMovimiento(Boolean.TRUE);
                    }
                }

                balance.setTieneMovimiento(Boolean.TRUE);
                switch (columna) {
                    case ConstanteReportes.saldoInicial -> {
                        balance.setSaldoInicial(balance.getSaldoInicial().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }
                    case ConstanteReportes.debe -> {
                        balance.setDebe(balance.getDebe().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.haber -> {
                        balance.setHaber(balance.getHaber().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().subtract(valor));
                    }

                    case ConstanteReportes.saldoFinal -> balance.setSaldoFinal(balance.getSaldoFinal().add(valor));

                    case ConstanteReportes.enero -> {
                        balance.setEnero(balance.getEnero().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.febrero -> {
                        balance.setFebrero(balance.getFebrero().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.marzo -> {
                        balance.setMarzo(balance.getMarzo().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.abril -> {
                        balance.setAbril(balance.getAbril().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }
                    case ConstanteReportes.mayo -> {
                        balance.setMayo(balance.getMayo().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.junio -> {
                        balance.setJunio(balance.getJunio().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.julio -> {
                        balance.setJulio(balance.getJulio().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.agosto -> {
                        balance.setAgosto(balance.getAgosto().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.septiembre -> {
                        balance.setSeptiembre(balance.getSeptiembre().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }
                    case ConstanteReportes.octubre -> {
                        balance.setOctubre(balance.getOctubre().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.noviembre -> {
                        balance.setNoviembre(balance.getNoviembre().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }

                    case ConstanteReportes.diciembre -> {
                        balance.setDiciembre(balance.getDiciembre().add(valor));
                        balance.setSaldoFinal(balance.getSaldoFinal().add(valor));
                    }
                }

            }
        }
    }

    /**
     * Genera una lista de cuentas totalizadoras a partir de las cuentas mayores,
     * creando entradas con sufijo \"999999999\" para representar los totales.
     * <p>
     * - Para cada cuenta mayor crea una nueva {@link CnPlanCuentaEntity} con código modificado y
     * texto de cuenta prefijado como \"Total de ...\".
     * - Si {@code listMayores} está vacío lanza {@link GeneralException}.
     *
     * @param listMayores lista de {@link CnPlanCuentaEntity} marcadas como mayores
     * @return lista de {@link CnPlanCuentaEntity} que representan los totalizadores (con sufijo \"999999999\")
     * @throws GeneralException cuando no existen cuentas mayores en el plan de cuentas
     */
    public List<CnPlanCuentaEntity> obtenerMayorTotalizadores(List<CnPlanCuentaEntity> listMayores) {

        List<CnPlanCuentaEntity> planesCuentaMayor = new ArrayList<>();
        if (!listMayores.isEmpty()) {
            listMayores.forEach(item -> {
                if (item.getMayor()) {

                    CnPlanCuentaEntity cnPlanCuentaEntity = new CnPlanCuentaEntity();
                    cnPlanCuentaEntity.setIdCuenta(UUID.randomUUID());
                    cnPlanCuentaEntity.setMayor(item.getMayor());
                    cnPlanCuentaEntity.setGrupo(item.getGrupo());
                    cnPlanCuentaEntity.setCodigoCuenta(item.getCodigoCuenta() + "999999999");
                    cnPlanCuentaEntity.setCodigoCuentaOriginal(item.getCodigoCuentaOriginal() + "999999999");
                    cnPlanCuentaEntity.setCuenta("Total de " + item.getCuenta());

                    planesCuentaMayor.add(cnPlanCuentaEntity);
                }
            });
            return planesCuentaMayor;

        } else {
            throw new GeneralException("No existen cuentas mayores creadas en el plan de cuentas.");
        }

    }


    /**
     * Construye la lista de códigos (cuenta propia más sus totalizadores padre) que deben recibir la mayorización.
     * <p>
     * - Divide el {@code codigoCuentaOriginal} por segmentos separados por '.' y crea las claves de totalizador
     * agregando el sufijo \"999999999\" para cada nivel padre.
     * - Devuelve la lista que incluye el propio código original como primer elemento.
     *
     * @param codigoCuentaOriginal código de cuenta original (por ejemplo \"1.1.01.01\")
     * @return lista de cadenas con el propio código y los códigos padre terminados en \"999999999\"
     */
    public List<String> obtenerListaCuentasAMayorizar(String codigoCuentaOriginal) {

        List<String> listaCuentas = new ArrayList<>();

        String[] partes = codigoCuentaOriginal.split("\\.");
        StringBuilder acumulado = new StringBuilder();

        listaCuentas.add(codigoCuentaOriginal);
        for (int i = 0; i < partes.length - 1; i++) {
            acumulado.append(partes[i]).append(".");
            listaCuentas.add(acumulado.toString() + "999999999");
        }

        return listaCuentas;
    }


    /**
     * Devuelve una lista ordenada de {@link BalanceValoresDto} a partir del mapa de cuentas,
     * filtrando únicamente aquellas cuentas que tienen movimiento.
     * <p>
     * - Filtra por {@code tieneMovimiento == true} y ordena por {@code codigoCuenta}.
     *
     * @param mapCuentasOriginales mapa de cuentas (clave -> {@link BalanceValoresDto})
     * @return lista ordenada de {@link BalanceValoresDto} que contienen movimiento
     */
    public List<BalanceValoresDto> getBalanceValoresOrdenado(Map<String, BalanceValoresDto> mapCuentasOriginales) {

        List<BalanceValoresDto> movimientos =
                mapCuentasOriginales.values().stream()
                        .filter(dto -> Boolean.TRUE.equals(dto.getTieneMovimiento()))
                        .collect(Collectors.toList());

        return movimientos.stream()
                .sorted(Comparator.comparing(BalanceValoresDto::getCodigoCuenta))
                .toList();
    }

}
