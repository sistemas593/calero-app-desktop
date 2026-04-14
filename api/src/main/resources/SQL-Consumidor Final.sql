INSERT INTO public.ge_terceros
(id_tercero, id_data, numero_identificacion, tercero, tipo_identificacion, deleted)
VALUES('b0953c54-1c63-4ed2-9ade-d047a8828c27',  1, '9999999999', 'Consumidor Final', 'C', false);


INSERT INTO public.ge_terceros_tipo
(id_tercero_tipo, tipo, id_tercero)
VALUES('70a1c18f-bc10-4114-9731-095a1b66cdd1', 1, 'b0953c54-1c63-4ed2-9ade-d047a8828c27');