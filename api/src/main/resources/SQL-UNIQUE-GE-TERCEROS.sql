-- =====================================================================================
-- Unicidad de ge_terceros por (id_data, numero_identificacion)
-- =====================================================================================
-- Contexto: la columna numero_identificacion nunca tuvo una restricción real en la base
-- de datos (el `unique=true` en GeTerceroEntity.java estaba comentado, y ddl-auto=update
-- nunca la generó). Toda la prevención de duplicados dependía solo del código de
-- aplicación, que tenía varios bugs ya corregidos por separado. Este script agrega la
-- restricción real, como última línea de defensa contra condiciones de carrera que
-- ningún chequeo en código puede evitar (dos requests casi simultáneos).
--
-- Se usa un ÍNDICE ÚNICO PARCIAL (WHERE deleted = false) en vez de un UNIQUE CONSTRAINT
-- normal, porque el sistema hace soft-delete (columna "deleted"): un tercero eliminado
-- lógicamente debe poder "liberar" su número de identificación para que se pueda volver
-- a crear un tercero nuevo con ese mismo número. Un UNIQUE CONSTRAINT normal (el que
-- generaría Hibernate vía ddl-auto con @UniqueConstraint) NO distingue eliminados de
-- activos y rompería ese caso legítimo. Por eso este script no se puede reemplazar por
-- una anotación @UniqueConstraint en la entidad, y hay que aplicarlo a mano.
--
-- PASO 1 (obligatorio, correr primero): auditar duplicados existentes.
-- Si esta consulta devuelve filas, el CREATE UNIQUE INDEX de más abajo va a fallar
-- (Postgres no permite crear un índice único sobre datos que ya lo violan). Hay que
-- decidir qué copia de cada duplicado se conserva y cuál se marca deleted = true (o se
-- reasignan las referencias que tenga en otras tablas) ANTES de seguir al paso 2.
-- =====================================================================================

SELECT id_data, numero_identificacion, count(*) AS copias, array_agg(id_tercero) AS ids
FROM ge_terceros
WHERE deleted = false
  AND numero_identificacion IS NOT NULL
GROUP BY id_data, numero_identificacion
HAVING count(*) > 1
ORDER BY copias DESC;

-- =====================================================================================
-- PASO 2: recién cuando el PASO 1 no devuelva filas (o ya se resolvieron a mano los
-- duplicados que aparecieron), correr esto. CONCURRENTLY evita bloquear la tabla
-- mientras se construye el índice (importante si esto se corre contra producción con la
-- app corriendo). No se puede envolver en una transacción junto con otras sentencias.
-- =====================================================================================

CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS uk_ge_terceros_iddata_numident
    ON ge_terceros (id_data, numero_identificacion)
    WHERE deleted = false;

-- Verificación rápida después de crearlo:
-- SELECT indexname, indexdef FROM pg_indexes WHERE indexname = 'uk_ge_terceros_iddata_numident';
