-- Escenario aislado para probar A5 (redirigir documentación a otra área de la CMC).
-- NO forma parte de los datos de la demo: se carga después de data.sql sólo en las pruebas que lo
-- piden (@Sql) o si se levanta el back con el perfil cmc ("-Dspring-boot.run.profiles=cmc").
-- La EB logueada pasa a ser Ana Ruiz (Comisión Médica Central) y hay un bolsín enviado de Córdoba
-- a la Central. Las áreas de la CMC son datos de prueba del detalle de remito (areaCMCDestino).

UPDATE sesion SET fecha_hora_fin = '2026-11-09 18:00:00' WHERE id = 2;
INSERT INTO sesion (id, fecha_hora_inicio, fecha_hora_fin, usuario_id) VALUES
  (3, '2026-11-10 08:00:00', NULL, 3);

INSERT INTO bolsin (numero, fecha, numero_precinto, peso, origen_codigo, destino_codigo) VALUES
  (106, '2026-11-07', 5006, 950, 2, 1);

INSERT INTO cambio_estado_bolsin (fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_id, bolsin_numero) VALUES
  ('2026-11-07 10:00:00', '2026-11-07 16:00:00', 1, 1, 106),
  ('2026-11-07 16:00:00', '2026-11-08 08:30:00', 2, 1, 106),
  ('2026-11-08 08:30:00', NULL, 3, 1, 106);

INSERT INTO remito (numero, fecha, estado_id, origen_codigo, destino_codigo, bolsin_numero) VALUES
  (1007, '2026-11-06', 3, 2, 1, 106),
  (1008, '2026-11-06', 3, 2, 1, 106);

INSERT INTO documentacion (numero, asunto, fecha_pase, tipo_documento_id, estado_actual) VALUES
  (10, 'Expediente 101/2026 - Sosa, Lía', '2026-11-05', 1, 'EnBolsinEnviado'),
  (11, 'Dictamen apelación - Mena, Raúl', '2026-11-05', 2, 'EnBolsinEnviado'),
  (12, 'Nota de elevación a la CMC', '2026-11-05', 4, 'EnBolsinEnviado');

-- Documentación 10 y 12 con área de la CMC registrada; la 11 sin área.
INSERT INTO detalle_remito (id, area_cmc_destino, documentacion_numero, remito_numero) VALUES
  (10, 'Área de Dictámenes', 10, 1007),
  (11, NULL, 11, 1007),
  (12, 'Mesa de Entradas', 12, 1008);

INSERT INTO cambio_estado_documentacion (fecha_hora_inicio, fecha_hora_fin, estado, responsable_id, documentacion_numero) VALUES
  ('2026-11-05 09:00:00', '2026-11-06 10:00:00', 'Registrada', 1, 10),
  ('2026-11-06 10:00:00', '2026-11-07 11:00:00', 'EnRemito', 1, 10),
  ('2026-11-07 11:00:00', '2026-11-08 08:30:00', 'EnBolsinSaliente', 1, 10),
  ('2026-11-08 08:30:00', NULL, 'EnBolsinEnviado', 1, 10),
  ('2026-11-05 09:00:00', '2026-11-06 10:00:00', 'Registrada', 1, 11),
  ('2026-11-06 10:00:00', '2026-11-07 11:00:00', 'EnRemito', 1, 11),
  ('2026-11-07 11:00:00', '2026-11-08 08:30:00', 'EnBolsinSaliente', 1, 11),
  ('2026-11-08 08:30:00', NULL, 'EnBolsinEnviado', 1, 11),
  ('2026-11-05 09:00:00', '2026-11-06 10:00:00', 'Registrada', 1, 12),
  ('2026-11-06 10:00:00', '2026-11-07 11:00:00', 'EnRemito', 1, 12),
  ('2026-11-07 11:00:00', '2026-11-08 08:30:00', 'EnBolsinSaliente', 1, 12),
  ('2026-11-08 08:30:00', NULL, 'EnBolsinEnviado', 1, 12);

-- Otro bolsín enviado a la Central (precinto 5010, de Rosario), para probar A5 más de una vez.

INSERT INTO bolsin (numero, fecha, numero_precinto, peso, origen_codigo, destino_codigo) VALUES
  (113, '2026-11-09', 5010, 780, 3, 1);

INSERT INTO cambio_estado_bolsin (fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_id, bolsin_numero) VALUES
  ('2026-11-09 10:00:00', '2026-11-09 16:00:00', 1, 2, 113),
  ('2026-11-09 16:00:00', '2026-11-10 08:30:00', 2, 2, 113),
  ('2026-11-10 08:30:00', NULL, 3, 2, 113);

INSERT INTO remito (numero, fecha, estado_id, origen_codigo, destino_codigo, bolsin_numero) VALUES
  (1105, '2026-11-09', 3, 3, 1, 113);

INSERT INTO documentacion (numero, asunto, fecha_pase, tipo_documento_id, estado_actual) VALUES
  (110, 'Expediente 412/2026 - Paredes, Ema', '2026-11-08', 1, 'EnBolsinEnviado'),
  (111, 'Dictamen recurso - Ibáñez, Gael', '2026-11-08', 2, 'EnBolsinEnviado'),
  (112, 'Nota a la Gerencia', '2026-11-08', 4, 'EnBolsinEnviado');

INSERT INTO detalle_remito (id, area_cmc_destino, documentacion_numero, remito_numero) VALUES
  (110, 'Área de Recursos', 110, 1105),
  (111, 'Área de Dictámenes', 111, 1105),
  (112, NULL, 112, 1105);

INSERT INTO cambio_estado_documentacion (fecha_hora_inicio, fecha_hora_fin, estado, responsable_id, documentacion_numero) VALUES
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 2, 110),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 2, 110),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 2, 110),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 2, 110),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 2, 111),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 2, 111),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 2, 111),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 2, 111),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 2, 112),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 2, 112),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 2, 112),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 2, 112);
