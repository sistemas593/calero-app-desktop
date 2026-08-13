package com.calero.lili.core.modTerceros;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Evita crear GeTerceroEntity duplicados cuando se procesa un LOTE de filas (carga de Excel de
 * documentos: compras, facturas de venta, etc.) donde el mismo numero de identificacion puede
 * aparecer en varias filas del mismo archivo.
 * <p>
 * No reemplaza la construccion de cada GeTerceroEntity (eso varia segun el origen de datos, por
 * eso recibe un factory), solo centraliza el algoritmo anti-duplicados: primero revisa el cache
 * del lote en memoria (evita crear un duplicado si el mismo numero ya se creo en una fila
 * anterior del mismo archivo, dentro de la misma ejecucion), luego la base de datos, y solo si
 * no existe en ninguno de los dos invoca el factory.
 * <p>
 * El factory recibido debe construir Y GUARDAR el tercero nuevo (y cualquier entidad
 * relacionada que corresponda, como GeTercerosTipoEntity), devolviendo la entidad ya
 * persistida. Solo se invoca cuando el numeroIdentificacion no esta ni en el cache del lote ni
 * en la base de datos.
 * <p>
 * No debe usarse en flujos donde un numero repetido dentro del mismo archivo es un error de
 * datos (por ejemplo, la carga de un listado de terceros nuevos) — ahi cada fila debe ser un
 * tercero distinto y el duplicado se debe rechazar, no reutilizar.
 */
@Component
@RequiredArgsConstructor
public class GeTerceroLoteHelper {

    private final GeTercerosRepository geTercerosRepository;

    public GeTerceroEntity obtenerOCrearEnLote(Map<String, GeTerceroEntity> cacheLote,
                                               Long idData,
                                               String numeroIdentificacion,
                                               Supplier<GeTerceroEntity> factoryYGuardado) {

        GeTerceroEntity existente = cacheLote.get(numeroIdentificacion);
        if (existente != null) {
            return existente;
        }

        existente = geTercerosRepository
                .getFindExistByNumeroIdentificacion(idData, numeroIdentificacion)
                .orElseGet(factoryYGuardado);

        cacheLote.put(numeroIdentificacion, existente);
        return existente;
    }
}
