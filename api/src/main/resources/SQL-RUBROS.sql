-- Extensión  para poder generar UUID para los identificadores de las tablas, ejecutar antes que el insert
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO public.rh_rubros
(id_rubro, afecta_iees, afecta_impuesto_renta, codigo, es_obligatorio, id_data, id_empresa, rubro, tipo)
values
('7a2c6e5b-2d94-4c41-9baf-5f13d1d83f01', false, false, 'ING-001', false, 1, 1, 'Sueldo', 'I'),
('e4b98d2a-5b3c-45f2-93d8-cb6d72dbe4f9', false, false, 'ING-002', false, 1, 1, 'Sobresueldo', 'I'),
('8c7f0d1e-91a8-4cb3-b8a6-42c3dfb9e5a4', false, false, 'ING-003', false, 1, 1, 'Horas extras', 'I'),
('1fd2c4b7-3e68-40f7-a9d5-8bcde1a7f632', false, false, 'ING-004', false, 1, 1, 'Horas suplementarias', 'I'),
('b9a7d834-ef5b-47f1-82c1-34d5a6e9c0b7', false, false, 'ING-005', false, 1, 1, 'Comisiones', 'I'),
('4c8e9a51-2f37-4c5d-90be-17d8e2a9f6c4', false, false, 'ING-006', false, 1, 1, 'Bonificación', 'I'),
('d5b9c7a1-6e8f-4d20-a35b-9f2e1c7d4a88', false, false, 'ING-007', false, 1, 1, 'Movilización', 'I'),
('90a3f4c8-1bde-49d5-8e21-6c7b5d4f9a30', false, false, 'ING-008', false, 1, 1, 'Alimentación', 'I'),
('3e7b0d5a-f8c1-4b97-a6d2-e1c54f9a7b62', false, false, 'ING-009', false, 1, 1, 'Décimo tercero', 'I'),
('a4d7c9e2-5f8b-4e61-b0d3-28f9a1c7e5d4', false, false, 'ING-010', false, 1, 1, 'Décimo cuarto', 'I'),
('c6f8e2a9-0b15-45d7-9c3e-71d4a8f2b5c6', false, false, 'ING-011', false, 1, 1, 'Vacaciones', 'I'),
('2b4d9e7f-a8c5-4932-8f61-d5a7c3e9b104', false, false, 'ING-013', false, 1, 1, 'Fondo de reserva', 'I'),
('f7a1b3c5-9d6e-49a8-b2c4-15e7d9f0a632', false, false, 'ING-014', false, 1, 1, 'Retroactivos', 'I'),
('6d2e8f4a-c5b7-4d19-93a1-e8c2f5b7d904', false, false, 'ING-015', false, 1, 1, 'Otros ingresos gravados de I.Renta (maeteria NO gravada de seguridad social)', 'I'),
('95c1d7a4-3e8f-4b26-a5d9-72b4c8e1f603', false, false, 'ING-016', false, 1, 1, 'Participación utilidades', 'I'),
('1a9d5c7b-6f4e-4d83-b2a7-c5e8f1d90364', false, false, 'ING-017', false, 1, 1, 'Otros ingresos en relación de dependencia que no constituyen renta gravada', 'I'),
('e2b6c9f1-7a43-4d5e-98b2-3f1d7a6c5e80', false, false, 'ING-018', false, 1, 1, 'Salario Digno', 'I'),
('5f3a8d2c-1e74-4b90-a6d1-f2c8b5e7d943', false, false, 'DES-001', false, 1, 1, 'Aporte IESS personal', 'E'),
('b1d4e7f8-9a25-4c63-8d0b-4e7a2c5f1d96', false, false, 'DES-002', false, 1, 1, 'Impuesto a la renta', 'E'),
('8e5a1c7d-2f49-43b6-91d8-a3c7e5f2b104', false, false, 'DES-003', false, 1, 1, 'Préstamo quirografario', 'E'),
('d9c2b5a7-6e18-41f3-84da-5b7e9c1f2a63', false, false, 'DES-004', false, 1, 1, 'Préstamo hipotecario', 'E'),
('47a1e9d5-c8b2-4f60-93d7-e5a2c1b8f946', false, false, 'DES-005', false, 1, 1, 'Multas', 'E'),
('fa3d7b9c-5e12-4a84-b6d1-9c2e5f7a3408', false, false, 'DES-006', false, 1, 1, 'Anticipos', 'E'),
('2c5e8a1f-7d94-4b37-a9c6-1f3d8e5b7a20', false, false, 'DES-007', false, 1, 1, 'Otros descuentos', 'E'),
('7d9f2c5a-e4b1-4d68-80c3-b5a7e1f9d246', false, false, 'DES-008', false, 1, 1, 'Impuesto a la renta asumido por el empleador', 'E'),
('c1a8e5d3-9f62-4b47-a0d5-8c2f7e1b9463', false, false, 'OTE-001', false, 1, 1, 'Ingresos gravados otros empleadores(Anual)', 'O'),
('3f7c1a9e-5d84-4e62-b1a7-d9c5f2e80431', false, false, 'OTE-002', false, 1, 1, 'IESS otros empleadores  (Anual)', 'O'),
('9b2e5f7a-c143-46d8-85a1-2e7c9d5b604f', false, false, 'OTE-003', false, 1, 1, 'Retenido y asumido otros empleadores  (Anual)', 'O'),
('a7e5d1c4-8f29-4b63-90d2-f1c7a5e8b346', false, false, 'GTP-001', false, 1, 1, 'Gastos Vivienda', 'G'), -- gastos personales
('0d9c2f7a-b5e1-4a84-93c6-5f2d8a1e74b9', false, false, 'GTP-002', false, 1, 1, 'Gastos Salud', 'G'),
('f1b7e4c9-2d65-48a3-b9e0-1c5a7d8f3462', false, false, 'GTP-003', false, 1, 1, 'Gastos Alimentacion', 'G'),
('68a2d5f1-c9e7-4b40-85d3-a1f7c2e9b564', false, false, 'GTP-004', false, 1, 1, 'Gastos Educacion', 'G'),
('b5f9a3e2-1c74-4d68-90e1-d7a2c5b8f943', false, false, 'GTP-005', false, 1, 1, 'Gastos Vestimenta', 'G'),
('29e7c1d5-a8f3-4b96-b2d4-6c1a9e5f7408', false, false, 'GTP-006', false, 1, 1, 'Gastos Turismo', 'G'),
('c7e2a5d9-4f18-4b63-a1d7-f5c9e2b80416', false, false, 'RET-001', false, 1, 1, 'Impuesto Renta Causado', 'R'),
('15b9d7e4-a3c6-4f28-8de1-7a2c5f9b6430', false, false, 'RET-002', false, 1, 1, 'Impuesto Renta Retenido', 'R'),
('e6c1f8a5-2d79-4b34-91a7-c5e2d8f74613', false, false, 'BAS-001', false, 1, 1, 'BASE IMPONIBLE', 'B'),
('4a7d2e9c-b1f5-46d8-83c2-e1a7f5b96430', false, false, 'PRO-001', false, 1, 1, 'Provision decimo tercero', 'P'), -- provision
('9c5f1a7e-8d24-4b63-a9e1-2f7c5d8b1460', false, false, 'PRO-002', false, 1, 1, 'Provision decimo cuarto', 'P'),
('f4e8b2d7-1c95-49a3-b6d0-a7e1c5f28463', false, false, 'EXO-001', false, 1, 1, 'Exoneracion discapacidad', 'X'),
('2e7a9c5d-f184-4b61-83d2-c9f5a7e146b8', false, false, 'EXO-002', false, 1, 1, 'Exoneracion tercera edad', 'X');