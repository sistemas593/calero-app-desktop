ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS codigo_tercero VARCHAR(255);

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS codigo_tercero VARCHAR(255);

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS tipo_personeria VARCHAR(50);


ALTER TABLE ad_empresas
    ADD COLUMN IF NOT EXISTS codigo_dinardap VARCHAR(255);

ALTER TABLE vt_ventas
    ADD COLUMN IF NOT EXISTS origen VARCHAR(255);


ALTER TABLE ge_items_precios
    ADD COLUMN IF NOT EXISTS id_empresa BIGINT;


ALTER TABLE vt_ventas_reembolsos
    ADD COLUMN IF NOT EXISTS fecha_autorizacion_reemb TIMESTAMP;
