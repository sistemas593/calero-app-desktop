ALTER TABLE vt_ventas
    ADD COLUMN IF NOT EXISTS existe_comprobante BOOLEAN DEFAULT FALSE;

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS sexo VARCHAR(50);

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS origen_ingresos VARCHAR(50);

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS estado_civil VARCHAR(50);

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS datos_adicionales BOOLEAN DEFAULT FALSE;

ALTER TABLE ge_terceros
    ADD COLUMN IF NOT EXISTS codigo_pais VARCHAR(10);

ALTER TABLE ge_terceros
    ADD CONSTRAINT fk_ge_terceros_pais FOREIGN KEY (codigo_pais)
        REFERENCES tb_paises (codigo_pais)
        ON UPDATE CASCADE ON DELETE SET NULL;


