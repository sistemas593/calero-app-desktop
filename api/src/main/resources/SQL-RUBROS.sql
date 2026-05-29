-- Extensión  para poder generar UUID para los identificadores de las tablas, ejecutar antes que el insert
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO public.rh_rubros
(id_rubro, afecta_iees, afecta_impuesto_renta, codigo, es_obligatorio, id_data, id_empresa, rubro, tipo)
values
(uuid_generate_v4(), false, false, 'ING-001', false, 1, 1, 'Sueldo', 'I'),
(uuid_generate_v4(), false, false, 'ING-002', false, 1, 1, 'Sobresueldo', 'I'),
(uuid_generate_v4(), false, false, 'ING-003', false, 1, 1, 'Horas extras', 'I'),
(uuid_generate_v4(), false, false, 'ING-004', false, 1, 1, 'Horas suplementarias', 'I'),
(uuid_generate_v4(), false, false, 'ING-005', false, 1, 1, 'Comisiones', 'I'),
(uuid_generate_v4(), false, false, 'ING-006', false, 1, 1, 'Bonificación', 'I'),
(uuid_generate_v4(), false, false, 'ING-007', false, 1, 1, 'Movilización', 'I'),
(uuid_generate_v4(), false, false, 'ING-008', false, 1, 1, 'Alimentación', 'I'),
(uuid_generate_v4(), false, false, 'ING-009', false, 1, 1, 'Décimo tercero', 'I'),
(uuid_generate_v4(), false, false, 'ING-010', false, 1, 1, 'Décimo cuarto', 'I'),
(uuid_generate_v4(), false, false, 'ING-011', false, 1, 1, 'Vacaciones', 'I'),
(uuid_generate_v4(), false, false, 'ING-013', false, 1, 1, 'Fondo de reserva', 'I'),
(uuid_generate_v4(), false, false, 'ING-014', false, 1, 1, 'Retroactivos', 'I'),
(uuid_generate_v4(), false, false, 'ING-015', false, 1, 1, 'Otros ingresos gravados de I.Renta (maeteria NO gravada de seguridad social)', 'I'),
(uuid_generate_v4(), false, false, 'ING-016', false, 1, 1, 'Participación utilidades', 'I'),
(uuid_generate_v4(), false, false, 'ING-017', false, 1, 1, 'Otros ingresos en relación de dependencia que no constituyen renta gravada', 'I'),
(uuid_generate_v4(), false, false, 'ING-018', false, 1, 1, 'Salario Digno', 'I'),
(uuid_generate_v4(), false, false, 'DES-001', false, 1, 1, 'Aporte IESS personal', 'E'),
(uuid_generate_v4(), false, false, 'DES-002', false, 1, 1, 'Impuesto a la renta', 'E'),
(uuid_generate_v4(), false, false, 'DES-003', false, 1, 1, 'Préstamo quirografario', 'E'),
(uuid_generate_v4(), false, false, 'DES-004', false, 1, 1, 'Préstamo hipotecario', 'E'),
(uuid_generate_v4(), false, false, 'DES-005', false, 1, 1, 'Multas', 'E'),
(uuid_generate_v4(), false, false, 'DES-006', false, 1, 1, 'Anticipos', 'E'),
(uuid_generate_v4(), false, false, 'DES-007', false, 1, 1, 'Otros descuentos', 'E'),
(uuid_generate_v4(), false, false, 'DES-008', false, 1, 1, 'Impuesto a la renta asumido por el empleador', 'E'),
(uuid_generate_v4(), false, false, 'OTE-001', false, 1, 1, 'Ingresos gravados otros empleadores(Anual)', 'O'),
(uuid_generate_v4(), false, false, 'OTE-002', false, 1, 1, 'IESS otros empleadores  (Anual)', 'O'),
(uuid_generate_v4(), false, false, 'OTE-003', false, 1, 1, 'Retenido y asumido otros empleadores  (Anual)', 'O'),
(uuid_generate_v4(), false, false, 'PRO-001', false, 1, 1, 'Provision decimo tercero', 'P'), -- provision
(uuid_generate_v4(), false, false, 'PRO-002', false, 1, 1, 'Provision decimo cuarto', 'P'),
(uuid_generate_v4(), false, false, 'GTP-001', false, 1, 1, 'Gastos Vivienda', 'G'), -- gastos personales
(uuid_generate_v4(), false, false, 'GTP-002', false, 1, 1, 'Gastos Salud', 'G'),
(uuid_generate_v4(), false, false, 'GTP-003', false, 1, 1, 'Gastos Alimentacion', 'G'),
(uuid_generate_v4(), false, false, 'GTP-004', false, 1, 1, 'Gastos Educacion', 'G'),
(uuid_generate_v4(), false, false, 'GTP-005', false, 1, 1, 'Gastos Vestimenta', 'G'),
(uuid_generate_v4(), false, false, 'GTP-006', false, 1, 1, 'Gastos Turismo', 'G'),
(uuid_generate_v4(), false, false, 'EXO-001', false, 1, 1, 'Exoneracion discapacidad', 'X'),
(uuid_generate_v4(), false, false, 'EXO-002', false, 1, 1, 'Exoneracion tercera edad', 'X');
(uuid_generate_v4(), false, false, 'RET-001', false, 1, 1, 'Impuesto Renta Causado', 'R'),
(uuid_generate_v4(), false, false, 'RET-002', false, 1, 1, 'Impuesto Renta Retenido', 'R'),
(uuid_generate_v4(), false, false, 'BAS-001', false, 1, 1, 'BASE IMPONIBLE', 'B'),
(uuid_generate_v4(), false, false, 'PRO-001', false, 1, 1, 'Provision decimo tercero', 'P'), -- provision
(uuid_generate_v4(), false, false, 'PRO-002', false, 1, 1, 'Provision decimo cuarto', 'P'),
(uuid_generate_v4(), false, false, 'GTP-001', false, 1, 1, 'Gastos Vivienda', 'G'), -- gastos personales
(uuid_generate_v4(), false, false, 'GTP-002', false, 1, 1, 'Gastos Salud', 'G'),
(uuid_generate_v4(), false, false, 'GTP-003', false, 1, 1, 'Gastos Alimentacion', 'G'),
(uuid_generate_v4(), false, false, 'GTP-004', false, 1, 1, 'Gastos Educacion', 'G'),
(uuid_generate_v4(), false, false, 'GTP-005', false, 1, 1, 'Gastos Vestimenta', 'G'),
(uuid_generate_v4(), false, false, 'GTP-006', false, 1, 1, 'Gastos Turismo', 'G'),
(uuid_generate_v4(), false, false, 'EXO-001', false, 1, 1, 'Exoneracion discapacidad', 'X'),
(uuid_generate_v4(), false, false, 'EXO-002', false, 1, 1, 'Exoneracion tercera edad', 'X');