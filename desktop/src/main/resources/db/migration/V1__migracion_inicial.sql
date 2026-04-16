-- ============================================================
-- V1__migracion_inicial.sql
-- Crea todas las tablas usadas por el módulo Desktop.
-- Naming: Spring Boot SpringPhysicalNamingStrategy convierte
--         camelCase a snake_case en nombres de tabla y columna.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS tb_paises (
    codigo_pais   VARCHAR(255) NOT NULL,
    pais          VARCHAR(255),
    created_by    VARCHAR(255),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(255),
    modified_date TIMESTAMP,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_by    VARCHAR(255),
    deleted_date  TIMESTAMP,
    CONSTRAINT pk_tb_paises PRIMARY KEY (codigo_pais)
);

CREATE TABLE IF NOT EXISTS tb_formas_pago_sri (
    codigo_forma_pago_sri VARCHAR(255) NOT NULL,
    forma_pago_sri        VARCHAR(255),
    created_by            VARCHAR(255),
    created_date          TIMESTAMP,
    modified_by           VARCHAR(255),
    modified_date         TIMESTAMP,
    deleted               BOOLEAN DEFAULT FALSE,
    deleted_by            VARCHAR(255),
    deleted_date          TIMESTAMP,
    CONSTRAINT pk_tb_formas_pago_sri PRIMARY KEY (codigo_forma_pago_sri)
);

-- ============================================================
-- AD_DATAS
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_datas (
    id_data              BIGSERIAL    NOT NULL,
    data                 VARCHAR(255),
    fecha_creacion       DATE,
    siguiente_id_empresa BIGINT,
    id_configuracion     UUID,
    created_by           VARCHAR(255),
    created_date         TIMESTAMP,
    modified_by          VARCHAR(255),
    modified_date        TIMESTAMP,
    deleted              BOOLEAN DEFAULT FALSE,
    deleted_by           VARCHAR(255),
    deleted_date         TIMESTAMP,
    CONSTRAINT pk_ad_datas PRIMARY KEY (id_data)
);

-- ============================================================
-- AD_EMPRESAS
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_empresas (
    id                                  UUID        NOT NULL,
    id_data                             BIGINT,
    id_empresa                          BIGINT,
    razon_social                        VARCHAR(255),
    ruc                                 VARCHAR(255),
    telefono1                           VARCHAR(255),
    telefono2                           VARCHAR(255),
    ciudad                              VARCHAR(255),
    direccion_matriz                    VARCHAR(255),
    numero                              VARCHAR(255),
    contador_nombre                     VARCHAR(255),
    contador_ruc                        VARCHAR(255),
    representante_nombre                VARCHAR(255),
    representante_tipo_identificacion   VARCHAR(255),
    representante_identificacion        VARCHAR(255),
    email                               VARCHAR(255),
    tipo_contribuyente                  VARCHAR(50),
    obligado_contabilidad               VARCHAR(1),
    devolucion_iva                      VARCHAR(255),
    agente_retencion                    VARCHAR(255),
    contribuyente_especial              VARCHAR(255),
    codigo_sustento                     VARCHAR(255),
    forma_pago_sri                      VARCHAR(255),
    fecha_creacion                      DATE,
    estado                              INTEGER      NOT NULL DEFAULT 0,
    ambiente_factura                    INTEGER      NOT NULL DEFAULT 0,
    ambiente_nota_credito               INTEGER      NOT NULL DEFAULT 0,
    ambiente_nota_debito                INTEGER      NOT NULL DEFAULT 0,
    ambiente_guia_remision              INTEGER      NOT NULL DEFAULT 0,
    ambiente_liquidacion                INTEGER      NOT NULL DEFAULT 0,
    ambiente_comprobante_retencion      INTEGER      NOT NULL DEFAULT 0,
    momento_envio_factura               INTEGER      NOT NULL DEFAULT 0,
    momento_envio_nota_credito          INTEGER      NOT NULL DEFAULT 0,
    momento_envio_nota_debito           INTEGER      NOT NULL DEFAULT 0,
    momento_envio_guia_remision         INTEGER      NOT NULL DEFAULT 0,
    momento_envio_liquidacion           INTEGER      NOT NULL DEFAULT 0,
    momento_envio_comprobante_retencion INTEGER      NOT NULL DEFAULT 0,
    momento_envio                       INTEGER,
    contrasenia_firma                   VARCHAR(255),
    fecha_caducidad_certificado         DATE,
    ruta_archivo_firma                  VARCHAR(255),
    ruta_logo                           VARCHAR(255),
    created_by                          VARCHAR(255),
    created_date                        TIMESTAMP,
    modified_by                         VARCHAR(255),
    modified_date                       TIMESTAMP,
    deleted                             BOOLEAN DEFAULT FALSE,
    deleted_by                          VARCHAR(255),
    deleted_date                        TIMESTAMP,
    CONSTRAINT pk_ad_empresas PRIMARY KEY (id),
    CONSTRAINT uq_ad_empresas_id UNIQUE (id)
);

-- ============================================================
-- AD_EMPRESAS_SUCURSALES
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_empresas_sucursales (
    id_sucursal   UUID         NOT NULL,
    id_data       BIGINT,
    id_empresa    BIGINT,
    sucursal      VARCHAR(255),
    nombre_sucursal VARCHAR(255),
    bloqueado     BOOLEAN,
    created_by    VARCHAR(255),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(255),
    modified_date TIMESTAMP,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_by    VARCHAR(255),
    deleted_date  TIMESTAMP,
    CONSTRAINT pk_ad_empresas_sucursales PRIMARY KEY (id_sucursal)
);

-- ============================================================
-- AD_EMPRESAS_SERIES
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_empresas_series (
    id_serie                  UUID         NOT NULL,
    id_data                   BIGINT,
    id_empresa                BIGINT,
    serie                     VARCHAR(255),
    nombre_comercial          VARCHAR(255),
    direccion_establecimiento VARCHAR(255),
    ciudad                    VARCHAR(255),
    telefono1                 VARCHAR(255),
    telefono2                 VARCHAR(255),
    created_by                VARCHAR(255),
    created_date              TIMESTAMP,
    modified_by               VARCHAR(255),
    modified_date             TIMESTAMP,
    deleted                   BOOLEAN DEFAULT FALSE,
    deleted_by                VARCHAR(255),
    deleted_date              TIMESTAMP,
    CONSTRAINT pk_ad_empresas_series PRIMARY KEY (id_serie)
);

-- ============================================================
-- AD_EMPRESAS_SERIES_DOCUMENTOS
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_empresas_series_documentos (
    id_documento        UUID         NOT NULL,
    id_data             BIGINT,
    id_empresa          BIGINT,
    documento           VARCHAR(255),
    numero_autorizacion VARCHAR(255),
    secuencial          VARCHAR(255),
    desde               VARCHAR(255),
    hasta               VARCHAR(255),
    fecha_vencimiento   DATE,
    formato_documento   VARCHAR(50),
    id_serie            UUID,
    CONSTRAINT pk_ad_empresas_series_documentos PRIMARY KEY (id_documento),
    CONSTRAINT fk_aesd_serie FOREIGN KEY (id_serie) REFERENCES ad_empresas_series (id_serie)
);

-- ============================================================
-- GE_TERCEROS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_terceros (
    id_tercero            UUID         NOT NULL,
    id_data               BIGINT,
    tipo_identificacion   VARCHAR(1),
    numero_identificacion VARCHAR(15),
    tercero               VARCHAR(300) NOT NULL,
    web                   VARCHAR(255),
    observaciones         TEXT,
    tipo_cliente_proveedor VARCHAR(2),
    ciudad                VARCHAR(60),
    direccion             VARCHAR(300),
    telefonos             VARCHAR(30),
    contacto              VARCHAR(30),
    email                 VARCHAR(150),
    placa                 VARCHAR(255),
    created_by            VARCHAR(255),
    created_date          TIMESTAMP,
    modified_by           VARCHAR(255),
    modified_date         TIMESTAMP,
    deleted               BOOLEAN DEFAULT FALSE,
    deleted_by            VARCHAR(255),
    deleted_date          TIMESTAMP,
    CONSTRAINT pk_ge_terceros PRIMARY KEY (id_tercero),
    CONSTRAINT uq_ge_terceros UNIQUE (id_tercero)
);

-- ============================================================
-- GE_TERCEROS_TIPO
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_terceros_tipo (
    id_tercero_tipo UUID    NOT NULL,
    id_tercero      UUID,
    tipo            INTEGER,
    CONSTRAINT pk_ge_terceros_tipo PRIMARY KEY (id_tercero_tipo),
    CONSTRAINT uq_ge_terceros_tipo UNIQUE (id_tercero_tipo),
    CONSTRAINT fk_ge_tercero FOREIGN KEY (id_tercero) REFERENCES ge_terceros (id_tercero)
);

-- ============================================================
-- VT_CLIENTES_CONFIGURACIONES
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_clientes_configuraciones (
    id_configuracion        UUID         NOT NULL,
    clave                   VARCHAR(255),
    fecha_vencimiento       DATE,
    ruc                     VARCHAR(255),
    enviar_correos          VARCHAR(255),
    valor_renovacion        NUMERIC(19, 2),
    factura_emite           VARCHAR(255),
    fecha_emitir_factura    DATE,
    fecha_llamar            DATE,
    respaldos_responsable   VARCHAR(255),
    respaldo_ultimo_oficina DATE,
    conexion_base           TEXT,
    configuraciones         TEXT,
    notas                   TEXT,
    usuarios                VARCHAR(255),
    modulos                 TEXT,
    rucs_activados          TEXT,
    claves_pcs              TEXT,
    tipo_blo                BIGINT,
    fecha_blo               DATE,
    id_tercero              UUID,
    created_by              VARCHAR(255),
    created_date            TIMESTAMP,
    modified_by             VARCHAR(255),
    modified_date           TIMESTAMP,
    deleted                 BOOLEAN DEFAULT FALSE,
    deleted_by              VARCHAR(255),
    deleted_date            TIMESTAMP,
    CONSTRAINT pk_vt_clientes_configuraciones PRIMARY KEY (id_configuracion),
    CONSTRAINT uq_vt_clientes_configuraciones UNIQUE (id_configuracion),
    CONSTRAINT fk_vtcc_tercero FOREIGN KEY (id_tercero) REFERENCES ge_terceros (id_tercero)
);

-- ============================================================
-- GE_IMPUESTOS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_impuestos (
    id_impuesto       BIGSERIAL    NOT NULL,
    codigo            VARCHAR(255),
    codigo_porcentaje VARCHAR(255),
    tarifa            NUMERIC(19, 2),
    CONSTRAINT pk_ge_impuestos PRIMARY KEY (id_impuesto),
    CONSTRAINT uq_ge_impuestos UNIQUE (id_impuesto)
);

-- ============================================================
-- GE_CATEGORIAS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_categorias (
    id_categoria  UUID         NOT NULL,
    id_data       BIGINT,
    categoria     VARCHAR(255),
    nivel         VARCHAR(255),
    created_by    VARCHAR(255),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(255),
    modified_date TIMESTAMP,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_by    VARCHAR(255),
    deleted_date  TIMESTAMP,
    CONSTRAINT pk_ge_categorias PRIMARY KEY (id_categoria),
    CONSTRAINT uq_ge_categorias UNIQUE (id_categoria)
);

-- ============================================================
-- GE_MARCAS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_marcas (
    id_marca      UUID         NOT NULL,
    id_data       BIGINT,
    marca         VARCHAR(255),
    created_by    VARCHAR(255),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(255),
    modified_date TIMESTAMP,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_by    VARCHAR(255),
    deleted_date  TIMESTAMP,
    CONSTRAINT pk_ge_marcas PRIMARY KEY (id_marca),
    CONSTRAINT uq_ge_marcas UNIQUE (id_marca)
);

-- ============================================================
-- GE_ITEMS_GRUPOS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_items_grupos (
    id_grupo             UUID         NOT NULL,
    id_data              BIGINT,
    id_empresa           BIGINT,
    grupo                VARCHAR(255),
    tipo_grupo           VARCHAR(50),
    id_cuenta_inventario UUID,
    id_cuenta_ingreso    UUID,
    id_cuenta_costo      UUID,
    id_cuenta_descuento  UUID,
    id_cuenta_devolucion UUID,
    id_cuenta_gasto      UUID,
    created_by           VARCHAR(255),
    created_date         TIMESTAMP,
    modified_by          VARCHAR(255),
    modified_date        TIMESTAMP,
    deleted              BOOLEAN DEFAULT FALSE,
    deleted_by           VARCHAR(255),
    deleted_date         TIMESTAMP,
    CONSTRAINT pk_ge_items_grupos PRIMARY KEY (id_grupo),
    CONSTRAINT uq_ge_items_grupos UNIQUE (id_grupo)
);

-- ============================================================
-- GE_MEDIDAS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_medidas (
    id_unidad_medida UUID         NOT NULL,
    id_data          BIGINT,
    unidad_medida    VARCHAR(255),
    created_by       VARCHAR(255),
    created_date     TIMESTAMP,
    modified_by      VARCHAR(255),
    modified_date    TIMESTAMP,
    deleted          BOOLEAN DEFAULT FALSE,
    deleted_by       VARCHAR(255),
    deleted_date     TIMESTAMP,
    CONSTRAINT pk_ge_medidas PRIMARY KEY (id_unidad_medida),
    CONSTRAINT uq_ge_medidas UNIQUE (id_unidad_medida)
);

-- ============================================================
-- GE_ITEMS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_items (
    id_item              UUID         NOT NULL,
    id_data              BIGINT,
    id_empresa           BIGINT,
    tipo_item            VARCHAR(3),
    codigo_principal     VARCHAR(255),
    codigo_auxiliar      VARCHAR(255),
    codigo_barras        VARCHAR(255),
    descripcion          VARCHAR(300),
    caracteristicas      VARCHAR(255),
    observaciones        VARCHAR(255),
    ordenador            BIGINT,
    ultima_compra        DATE,
    ultima_venta         DATE,
    estado               BIGINT,
    id_grupo             UUID,
    id_marca             UUID,
    id_categoria         UUID,
    detalles_adicionales JSONB,
    created_by           VARCHAR(255),
    created_date         TIMESTAMP,
    modified_by          VARCHAR(255),
    modified_date        TIMESTAMP,
    deleted              BOOLEAN DEFAULT FALSE,
    deleted_by           VARCHAR(255),
    deleted_date         TIMESTAMP,
    CONSTRAINT pk_ge_items PRIMARY KEY (id_item),
    CONSTRAINT uq_ge_items UNIQUE (id_item),
    CONSTRAINT fk_ge_items_grupo     FOREIGN KEY (id_grupo)    REFERENCES ge_items_grupos (id_grupo),
    CONSTRAINT fk_ge_items_marca     FOREIGN KEY (id_marca)    REFERENCES ge_marcas (id_marca),
    CONSTRAINT fk_ge_items_categoria FOREIGN KEY (id_categoria) REFERENCES ge_categorias (id_categoria)
);

-- ============================================================
-- GE_ITEMS_PRECIOS  (@Table name "geItemsPrecios" → ge_items_precios)
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_items_precios (
    id_items_precio UUID         NOT NULL,
    id_data         BIGINT,
    precio1         NUMERIC(19, 2),
    precio2         NUMERIC(19, 2),
    precio3         NUMERIC(19, 2),
    precio4         NUMERIC(19, 2),
    precio5         NUMERIC(19, 2),
    id_item         UUID,
    CONSTRAINT pk_ge_items_precios PRIMARY KEY (id_items_precio),
    CONSTRAINT uq_ge_items_precios UNIQUE (id_items_precio),
    CONSTRAINT fk_ge_items_precios_item FOREIGN KEY (id_item) REFERENCES ge_items (id_item)
);

-- ============================================================
-- GE_ITEMS_MEDIDAS
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_items_medidas (
    id_item_medida   UUID    NOT NULL,
    id_unidad_medida UUID,
    factor           INTEGER,
    id_item          UUID,
    CONSTRAINT pk_ge_items_medidas PRIMARY KEY (id_item_medida),
    CONSTRAINT uq_ge_items_medidas UNIQUE (id_item_medida),
    CONSTRAINT fk_ge_items_medidas_item FOREIGN KEY (id_item) REFERENCES ge_items (id_item)
);

-- ============================================================
-- GE_ITEMS_IMPUESTOS  (tabla de unión ManyToMany)
-- ============================================================

CREATE TABLE IF NOT EXISTS ge_items_impuestos (
    id_item     UUID   NOT NULL,
    id_impuesto BIGINT NOT NULL,
    CONSTRAINT pk_ge_items_impuestos PRIMARY KEY (id_item, id_impuesto),
    CONSTRAINT uq_ge_items_impuestos UNIQUE (id_item, id_impuesto),
    CONSTRAINT fk_gii_item     FOREIGN KEY (id_item)     REFERENCES ge_items (id_item),
    CONSTRAINT fk_gii_impuesto FOREIGN KEY (id_impuesto) REFERENCES ge_impuestos (id_impuesto)
);

-- ============================================================
-- VT_VENDEDORES
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_vendedores (
    id_vendedor   UUID         NOT NULL,
    id_data       BIGINT,
    id_empresa    BIGINT,
    vendedor      VARCHAR(255),
    bloqueado     BOOLEAN,
    firma         VARCHAR(255),
    created_by    VARCHAR(255),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(255),
    modified_date TIMESTAMP,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_by    VARCHAR(255),
    deleted_date  TIMESTAMP,
    CONSTRAINT pk_vt_vendedores PRIMARY KEY (id_vendedor),
    CONSTRAINT uq_vt_vendedores UNIQUE (id_vendedor)
);

-- ============================================================
-- CN_PLAN_CUENTAS
-- ============================================================

CREATE TABLE IF NOT EXISTS cn_plan_cuentas (
    id_cuenta               UUID         NOT NULL,
    id_data                 BIGINT,
    id_empresa              BIGINT,
    id_cuenta_padre         UUID,
    codigo_cuenta           VARCHAR(255),
    codigo_cuenta_original  VARCHAR(255),
    cuenta                  VARCHAR(255),
    mayor                   BOOLEAN,
    nivel                   INTEGER,
    tipo_auxiliar           VARCHAR(255),
    grupo                   INTEGER,
    created_by              VARCHAR(255),
    created_date            TIMESTAMP,
    modified_by             VARCHAR(255),
    modified_date           TIMESTAMP,
    deleted                 BOOLEAN DEFAULT FALSE,
    deleted_by              VARCHAR(255),
    deleted_date            TIMESTAMP,
    CONSTRAINT pk_cn_plan_cuentas PRIMARY KEY (id_cuenta),
    CONSTRAINT uq_cn_plan_cuentas UNIQUE (id_cuenta)
);

-- ============================================================
-- CN_CENTRO_COSTOS
-- ============================================================

CREATE TABLE IF NOT EXISTS cn_centro_costos (
    id_centro_costos              UUID         NOT NULL,
    id_data                       BIGINT,
    id_empresa                    BIGINT,
    id_codigo_centro_costos_padre UUID,
    codigo_centro_costos          VARCHAR(255),
    codigo_centro_costos_original VARCHAR(255),
    centro_costos                 VARCHAR(255),
    mayor                         BOOLEAN,
    nivel                         INTEGER,
    created_by                    VARCHAR(255),
    created_date                  TIMESTAMP,
    modified_by                   VARCHAR(255),
    modified_date                 TIMESTAMP,
    deleted                       BOOLEAN DEFAULT FALSE,
    deleted_by                    VARCHAR(255),
    deleted_date                  TIMESTAMP,
    CONSTRAINT pk_cn_centro_costos PRIMARY KEY (id_centro_costos),
    CONSTRAINT uq_cn_centro_costos UNIQUE (id_centro_costos)
);

-- ============================================================
-- VT_CLIENTES_GRUPOS
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_clientes_grupos (
    id_grupo          UUID         NOT NULL,
    id_data           BIGINT,
    id_empresa        BIGINT,
    grupo             VARCHAR(255),
    id_cuenta_credito UUID,
    id_cuenta_anticipo UUID,
    predeterminado    BOOLEAN,
    created_by        VARCHAR(255),
    created_date      TIMESTAMP,
    modified_by       VARCHAR(255),
    modified_date     TIMESTAMP,
    deleted           BOOLEAN DEFAULT FALSE,
    deleted_by        VARCHAR(255),
    deleted_date      TIMESTAMP,
    CONSTRAINT pk_vt_clientes_grupos PRIMARY KEY (id_grupo),
    CONSTRAINT uq_vt_clientes_grupos UNIQUE (id_grupo),
    CONSTRAINT fk_vtcg_cuenta_credito  FOREIGN KEY (id_cuenta_credito)  REFERENCES cn_plan_cuentas (id_cuenta),
    CONSTRAINT fk_vtcg_cuenta_anticipo FOREIGN KEY (id_cuenta_anticipo) REFERENCES cn_plan_cuentas (id_cuenta)
);

-- ============================================================
-- VT_VENTAS  (@Table name "vtVentas" → vt_ventas)
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_ventas (
    id_venta                  UUID         NOT NULL,
    id_data                   BIGINT,
    id_empresa                BIGINT,
    sucursal                  VARCHAR(3),
    tipo_venta                VARCHAR(255),
    serie                     VARCHAR(6),
    secuencial                VARCHAR(9),
    numero_autorizacion       VARCHAR(49),
    fecha_emision             DATE,
    tipo_ingreso              VARCHAR(255),
    codigo_documento          VARCHAR(255),
    liquidar                  VARCHAR(1),
    fecha_anulacion           DATE,
    forma_pago                VARCHAR(50),
    dias_credito              INTEGER,
    fecha_vencimiento         DATE,
    cuotas                    INTEGER,
    guia_remision_serie       VARCHAR(6),
    guia_remision_secuencial  VARCHAR(9),
    formato_documento         VARCHAR(50),
    estado_documento          VARCHAR(50),
    email_estado              INTEGER,
    email                     VARCHAR(255),
    tipo_emision              INTEGER,
    ambiente                  INTEGER,
    fecha_autorizacion        VARCHAR(255),
    clave_acceso              VARCHAR(255),
    mensajes                  JSONB,
    comprobante               TEXT,
    numero_items              INTEGER      NOT NULL DEFAULT 0,
    subtotal                  NUMERIC(19, 2),
    total_descuento           NUMERIC(19, 2),
    total                     NUMERIC(19, 2),
    total_impuesto            NUMERIC(19, 2),
    id_zona                   UUID,
    anulada                   BOOLEAN,
    impresa                   BOOLEAN,
    relacionado               VARCHAR(255),
    informacion_adicional     JSONB,
    formas_pago_sri           JSONB,
    concepto                  VARCHAR(255),
    mod_codigo_documento      VARCHAR(255),
    mod_serie                 VARCHAR(255),
    mod_secuencial            VARCHAR(255),
    mod_fecha_emision         DATE,
    exportacion               JSONB,
    flete_internacional       NUMERIC(19, 2),
    seguro_internacional      NUMERIC(19, 2),
    gastos_aduaneros          NUMERIC(19, 2),
    gastos_transporte_otros   NUMERIC(19, 2),
    sustitutiva_guia_remision JSONB,
    id_tercero                UUID,
    id_vendedor               UUID,
    id_asiento                UUID,
    created_by                VARCHAR(255),
    created_date              TIMESTAMP,
    modified_by               VARCHAR(255),
    modified_date             TIMESTAMP,
    deleted                   BOOLEAN DEFAULT FALSE,
    deleted_by                VARCHAR(255),
    deleted_date              TIMESTAMP,
    CONSTRAINT pk_vt_ventas PRIMARY KEY (id_venta),
    CONSTRAINT uq_vt_ventas UNIQUE (id_venta),
    CONSTRAINT fk_vt_ventas_tercero  FOREIGN KEY (id_tercero)  REFERENCES ge_terceros (id_tercero),
    CONSTRAINT fk_vt_ventas_vendedor FOREIGN KEY (id_vendedor) REFERENCES vt_vendedores (id_vendedor)
);

-- ============================================================
-- VT_VENTAS_VALORES  (@Table "vtVentasValores" → vt_ventas_valores)
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_ventas_valores (
    id_venta_valores  UUID         NOT NULL,
    codigo            VARCHAR(255),
    codigo_porcentaje VARCHAR(255),
    base_imponible    NUMERIC(19, 2),
    valor             NUMERIC(19, 2),
    tarifa            NUMERIC(19, 2),
    id_data           BIGINT,
    id_empresa        BIGINT,
    id_venta          UUID,
    CONSTRAINT pk_vt_ventas_valores PRIMARY KEY (id_venta_valores),
    CONSTRAINT uq_vt_ventas_valores UNIQUE (id_venta_valores),
    CONSTRAINT fk_vtvv_venta FOREIGN KEY (id_venta) REFERENCES vt_ventas (id_venta)
);

-- ============================================================
-- VT_VENTAS_DETALLE  (@Table "vtVentasDetalle" → vt_ventas_detalle)
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_ventas_detalle (
    id_venta_detalle UUID         NOT NULL,
    codigo_principal VARCHAR(255),
    codigo_auxiliar  VARCHAR(255),
    codigo_barras    VARCHAR(255),
    descripcion      VARCHAR(255),
    cantidad         NUMERIC(19, 2),
    precio_unitario  NUMERIC(19, 2),
    descuento        NUMERIC(19, 2),
    dscto_item       NUMERIC(19, 2),
    subtotal_item    NUMERIC(19, 2),
    id_bodega        INTEGER,
    item_orden       INTEGER,
    unidad_medida    VARCHAR(255),
    id_vendedor      INTEGER,
    impuesto         JSONB,
    det_adicional    JSONB,
    id_item          UUID         NOT NULL,
    id_data          BIGINT,
    id_empresa       BIGINT,
    id_centro_costos UUID,
    id_venta         UUID,
    CONSTRAINT pk_vt_ventas_detalle PRIMARY KEY (id_venta_detalle),
    CONSTRAINT uq_vt_ventas_detalle UNIQUE (id_venta_detalle),
    CONSTRAINT fk_vtvd_item          FOREIGN KEY (id_item)          REFERENCES ge_items (id_item),
    CONSTRAINT fk_vtvd_centro_costos FOREIGN KEY (id_centro_costos) REFERENCES cn_centro_costos (id_centro_costos),
    CONSTRAINT fk_vtvd_venta         FOREIGN KEY (id_venta)         REFERENCES vt_ventas (id_venta)
);


-- ============================================================
-- VT_VENTAS_REEMBOLSOS  (@Table "vtVentasReembolsos" → vt_ventas_reembolsos)
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_ventas_reembolsos (
    id_venta_reembolsos         UUID         NOT NULL,
    tipo_identificacion_reemb   VARCHAR(255),
    numero_identificacion_reemb VARCHAR(255),
    tipo_proveedor_reemb        VARCHAR(50),
    codigo_documento_reemb      VARCHAR(255),
    serie_reemb                 VARCHAR(255),
    secuencial_reemb            VARCHAR(255),
    fecha_emision_reemb         DATE,
    numero_autorizacion_reemb   VARCHAR(255),
    comprobante                 TEXT,
    codigo_pais                 VARCHAR(255),
    id_venta                    UUID,
    created_by                  VARCHAR(255),
    created_date                TIMESTAMP,
    modified_by                 VARCHAR(255),
    modified_date               TIMESTAMP,
    deleted                     BOOLEAN DEFAULT FALSE,
    deleted_by                  VARCHAR(255),
    deleted_date                TIMESTAMP,
    CONSTRAINT pk_vt_ventas_reembolsos PRIMARY KEY (id_venta_reembolsos),
    CONSTRAINT uq_vt_ventas_reembolsos UNIQUE (id_venta_reembolsos),
    CONSTRAINT fk_vtvr_pais  FOREIGN KEY (codigo_pais) REFERENCES tb_paises (codigo_pais),
    CONSTRAINT fk_vtvr_venta FOREIGN KEY (id_venta)    REFERENCES vt_ventas (id_venta)
);

-- ============================================================
-- VT_VENTAS_REEMBOLSOS_VALORES  (@Table "vtVentasReembolsosValores")
-- ============================================================

CREATE TABLE IF NOT EXISTS vt_ventas_reembolsos_valores (
    id_venta_valores            UUID         NOT NULL,
    codigo                      VARCHAR(255),
    codigo_porcentaje           VARCHAR(255),
    tarifa                      NUMERIC(19, 2),
    base_imponible              NUMERIC(19, 2),
    valor                       NUMERIC(19, 2),
    id_venta_reembolsos         UUID,
    CONSTRAINT pk_vt_ventas_reembolsos_valores PRIMARY KEY (id_venta_valores),
    CONSTRAINT uq_vt_ventas_reembolsos_valores UNIQUE (id_venta_valores),
    CONSTRAINT fk_vtvrv_reembolso FOREIGN KEY (id_venta_reembolsos) REFERENCES vt_ventas_reembolsos (id_venta_reembolsos)
);

-- ============================================================
-- XC_FACTURAS
-- ============================================================

CREATE TABLE IF NOT EXISTS xc_facturas (
    id_factura           UUID          NOT NULL,
    id_data              BIGINT,
    id_empresa           BIGINT,
    sucursal             VARCHAR(255),
    periodo              VARCHAR(255),
    tipo_documento       VARCHAR(255),
    serie                VARCHAR(255),
    secuencial           VARCHAR(255),
    fecha_emision        DATE,
    fecha_vencimiento    DATE,
    valor                NUMERIC(19, 2),
    registros_aplicados  NUMERIC(19, 0),
    pagos                NUMERIC(19, 2),
    retenciones_iva      NUMERIC(19, 2),
    retenciones_renta    NUMERIC(19, 2),
    notas_credito        NUMERIC(19, 2),
    saldo                NUMERIC(19, 2),
    anulada              BOOLEAN,
    id_tercero           UUID,
    created_by           VARCHAR(255),
    created_date         TIMESTAMP,
    modified_by          VARCHAR(255),
    modified_date        TIMESTAMP,
    deleted              BOOLEAN DEFAULT FALSE,
    deleted_by           VARCHAR(255),
    deleted_date         TIMESTAMP,
    CONSTRAINT pk_xc_facturas PRIMARY KEY (id_factura),
    CONSTRAINT uq_xc_facturas UNIQUE (id_factura),
    CONSTRAINT fk_xcf_tercero FOREIGN KEY (id_tercero) REFERENCES ge_terceros (id_tercero)
);

-- ============================================================
-- AD_PROCESO_AUTORIZACION
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_proceso_autorizacion (
    clave_acceso VARCHAR(255) NOT NULL,
    CONSTRAINT pk_ad_proceso_autorizacion PRIMARY KEY (clave_acceso)
);

-- ============================================================
-- AD_IVA_PORCENTAJES
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_iva_porcentajes (
    id_iva_porcentaje BIGSERIAL    NOT NULL,
    iva1              INTEGER,
    iva2              INTEGER,
    iva3              INTEGER,
    fecha_desde       DATE,
    created_by        VARCHAR(255),
    created_date      TIMESTAMP,
    modified_by       VARCHAR(255),
    modified_date     TIMESTAMP,
    deleted           BOOLEAN DEFAULT FALSE,
    deleted_by        VARCHAR(255),
    deleted_date      TIMESTAMP,
    CONSTRAINT pk_ad_iva_porcentajes PRIMARY KEY (id_iva_porcentaje),
    CONSTRAINT uq_ad_iva_porcentajes UNIQUE (id_iva_porcentaje)
);

-- ============================================================
-- AD_LOGS
-- ============================================================

CREATE TABLE IF NOT EXISTS ad_logs (
    id_log         UUID         NOT NULL,
    id_data        BIGINT,
    id_empresa     BIGINT,
    id_documento   UUID,
    mensajes       TEXT,
    fecha_hora     TIMESTAMP,
    serie          VARCHAR(255),
    secuencial     VARCHAR(255),
    tipo_documento VARCHAR(255),
    tipo           VARCHAR(255),
    CONSTRAINT pk_ad_logs PRIMARY KEY (id_log)
);
