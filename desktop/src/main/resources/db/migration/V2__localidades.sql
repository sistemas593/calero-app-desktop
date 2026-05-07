-- =============================================================================
-- V2 - Módulo Localidades: tb_provincias, tb_cantones, tb_parroquias
--      y referencias en ge_terceros
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- ProvinciaEntity → tb_provincias
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_provincias (
    codigo_provincia    VARCHAR(255)    NOT NULL,
    provincia           VARCHAR(255),

    -- Auditable
    created_by          VARCHAR(255),
    created_date        TIMESTAMP,
    modified_by         VARCHAR(255),
    modified_date       TIMESTAMP,
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_by          VARCHAR(255),
    deleted_date        TIMESTAMP,

    CONSTRAINT pk_tb_provincias PRIMARY KEY (codigo_provincia)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- CantonEntity → tb_cantones
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_cantones (
    codigo_canton       VARCHAR(255)    NOT NULL,
    canton              VARCHAR(255),
    codigo_provincia    VARCHAR(255)    NOT NULL,

    -- Auditable
    created_by          VARCHAR(255),
    created_date        TIMESTAMP,
    modified_by         VARCHAR(255),
    modified_date       TIMESTAMP,
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_by          VARCHAR(255),
    deleted_date        TIMESTAMP,

    CONSTRAINT pk_tb_cantones               PRIMARY KEY (codigo_canton),
    CONSTRAINT fk_tb_cantones_provincia     FOREIGN KEY (codigo_provincia)
        REFERENCES tb_provincias (codigo_provincia)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ─────────────────────────────────────────────────────────────────────────────
-- ParroquiaEntity → tb_parroquias
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_parroquias (
    codigo_parroquia    VARCHAR(255)    NOT NULL,
    parroquia           VARCHAR(255),
    codigo_canton       VARCHAR(255)    NOT NULL,

    -- Auditable
    created_by          VARCHAR(255),
    created_date        TIMESTAMP,
    modified_by         VARCHAR(255),
    modified_date       TIMESTAMP,
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_by          VARCHAR(255),
    deleted_date        TIMESTAMP,

    CONSTRAINT pk_tb_parroquias             PRIMARY KEY (codigo_parroquia),
    CONSTRAINT fk_tb_parroquias_canton      FOREIGN KEY (codigo_canton)
        REFERENCES tb_cantones (codigo_canton)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ─────────────────────────────────────────────────────────────────────────────
-- GeTerceroEntity → ge_terceros
-- Referencias a provincia, cantón y parroquia
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS codigo_provincia   VARCHAR(255),
    ADD COLUMN IF NOT EXISTS codigo_canton      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS codigo_parroquia   VARCHAR(255),
    ADD COLUMN IF NOT EXISTS sexo               VARCHAR(50),
    ADD COLUMN IF NOT EXISTS origen_ingresos    VARCHAR(50),
    ADD COLUMN IF NOT EXISTS estado_civil       VARCHAR(50);


ALTER TABLE ge_terceros
    ADD CONSTRAINT fk_ge_terceros_provincia FOREIGN KEY (codigo_provincia)
        REFERENCES tb_provincias (codigo_provincia)
        ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE ge_terceros
    ADD CONSTRAINT fk_ge_terceros_canton    FOREIGN KEY (codigo_canton)
        REFERENCES tb_cantones (codigo_canton)
        ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE ge_terceros
    ADD CONSTRAINT fk_ge_terceros_parroquia FOREIGN KEY (codigo_parroquia)
        REFERENCES tb_parroquias (codigo_parroquia)
        ON UPDATE CASCADE ON DELETE SET NULL;
