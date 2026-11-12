-- Datos de prueba del CU 28 Registrar recepción de bolsín.
-- Hibernate recrea las tablas en cada arranque y después se carga este archivo.
-- Usuario logueado: Laura Gómez, Encargada de Bolsines de la Comisión Médica Córdoba.

INSERT INTO comision_medica (codigo, nombre, direccion, email, telefono) VALUES
  (1, 'Comisión Médica Central', 'Av. Corrientes 1000, CABA', 'cm.central@bolsines.test', '011-4000-0001'),
  (2, 'Comisión Médica Córdoba', 'Av. Colón 500, Córdoba', 'cm.cordoba@bolsines.test', '0351-400-0002'),
  (3, 'Comisión Médica Rosario', 'Bv. Oroño 800, Rosario', 'cm.rosario@bolsines.test', '0341-400-0003'),
  (4, 'Comisión Médica Mendoza', 'Av. San Martín 1200, Mendoza', 'cm.mendoza@bolsines.test', '0261-400-0004');

INSERT INTO usuario (id, nombre, contrasena) VALUES
  (1, 'lgomez', 'demo'), (2, 'mperez', 'demo'), (3, 'aruiz', 'demo'), (4, 'jsosa', 'demo');

INSERT INTO empleado (id, nombre, apellido, usuario_id, comision_medica_codigo) VALUES
  (1, 'Laura', 'Gómez', 1, 2),
  (2, 'Martín', 'Pérez', 2, 3),
  (3, 'Ana', 'Ruiz', 3, 1),
  (4, 'Julián', 'Sosa', 4, 4);

-- Sesión activa (sin fecha de fin) de Laura Gómez y una sesión vieja ya cerrada.
INSERT INTO sesion (id, fecha_hora_inicio, fecha_hora_fin, usuario_id) VALUES
  (1, '2026-11-08 08:00:00', '2026-11-08 17:00:00', 2),
  (2, '2026-11-09 08:00:00', NULL, 1);

INSERT INTO tipo_documento (id, nombre, descripcion) VALUES
  (1, 'Expediente', 'Expediente de trámite'),
  (2, 'Dictamen médico', 'Dictamen emitido por la comisión'),
  (3, 'Historia clínica', 'Copia de historia clínica'),
  (4, 'Nota', 'Nota o comunicación interna');

INSERT INTO estado_bolsin (id, nombre, descripcion) VALUES
  (1, 'Abierto', 'Bolsín en armado'), (2, 'Cerrado', 'Bolsín cerrado con precinto'),
  (3, 'Enviado', 'Retirado por el correo'), (4, 'RecibidoEnCMDestino', 'Recibido en la CM destino');

INSERT INTO estado_remito (id, nombre, descripcion) VALUES
  (1, 'Generado', 'Remito generado'), (2, 'EnBolsin', 'Incluido en un bolsín'),
  (3, 'Enviado', 'En un bolsín enviado'), (4, 'RecibidoYAceptado', 'Recibido y aceptado'),
  (5, 'RecibidoYAceptadoParcial', 'Recibido y aceptado parcial');

INSERT INTO bolsin (numero, fecha, numero_precinto, peso, origen_codigo, destino_codigo) VALUES
  (101, '2026-11-05', 5001, 1200, 1, 2),
  (102, '2026-11-05', 5002, 800, 3, 2),
  (103, '2026-11-05', 5003, 650, 4, 2),
  (104, '2026-11-05', 5004, 900, 1, 3),
  (105, '2026-11-05', 5005, 700, 4, 2);

INSERT INTO cambio_estado_bolsin (fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_id, bolsin_numero) VALUES
  ('2026-11-05 10:00:00', '2026-11-05 16:00:00', 1, 3, 101),
  ('2026-11-05 16:00:00', '2026-11-06 08:30:00', 2, 3, 101),
  ('2026-11-06 08:30:00', NULL, 3, 3, 101),
  ('2026-11-05 10:00:00', '2026-11-05 16:00:00', 1, 2, 102),
  ('2026-11-05 16:00:00', '2026-11-06 08:30:00', 2, 2, 102),
  ('2026-11-06 08:30:00', NULL, 3, 2, 102),
  ('2026-11-05 10:00:00', '2026-11-05 16:00:00', 1, 4, 103),
  ('2026-11-05 16:00:00', NULL, 2, 4, 103),
  ('2026-11-05 10:00:00', '2026-11-05 16:00:00', 1, 3, 104),
  ('2026-11-05 16:00:00', '2026-11-06 08:30:00', 2, 3, 104),
  ('2026-11-06 08:30:00', NULL, 3, 3, 104),
  ('2026-11-05 10:00:00', '2026-11-05 16:00:00', 1, 4, 105),
  ('2026-11-05 16:00:00', '2026-11-06 08:30:00', 2, 4, 105),
  ('2026-11-06 08:30:00', NULL, 3, 4, 105);

INSERT INTO remito (numero, fecha, estado_id, origen_codigo, destino_codigo, bolsin_numero) VALUES
  (1001, '2026-11-04', 3, 1, 2, 101),
  (1002, '2026-11-04', 3, 1, 2, 101),
  (1003, '2026-11-04', 3, 3, 2, 102),
  (1004, '2026-11-04', 2, 4, 2, 103),
  (1005, '2026-11-04', 3, 1, 3, 104),
  (1006, '2026-11-04', 3, 4, 2, 105);

INSERT INTO documentacion (numero, asunto, fecha_pase, tipo_documento_id, estado_actual) VALUES
  (1, 'Expediente 1/2026 - Pérez, Juan', '2026-11-03', 1, 'EnBolsinEnviado'),
  (2, 'Dictamen incapacidad - Gómez, Ana', '2026-11-03', 2, 'EnBolsinEnviado'),
  (3, 'Historia clínica - Ríos, Pedro', '2026-11-03', 3, 'EnBolsinEnviado'),
  (4, 'Expediente 77/2026 - Díaz, Carla', '2026-11-03', 1, 'EnBolsinEnviado'),
  (5, 'Nota de elevación', '2026-11-03', 4, 'EnBolsinEnviado'),
  (6, 'Dictamen - Luna, Marcos', '2026-11-03', 2, 'EnBolsinSaliente'),
  (7, 'Expediente 90/2026 - Vera, Sol', '2026-11-03', 1, 'EnBolsinEnviado'),
  (8, 'Historia clínica - Paz, Eva', '2026-11-03', 3, 'EnBolsinEnviado'),
  (9, 'Expediente 12/2026 - Rey, Hugo', '2026-11-03', 1, 'EnBolsinEnviado');

INSERT INTO detalle_remito (id, area_cmc_destino, documentacion_numero, remito_numero) VALUES
  (1, NULL, 1, 1001),
  (2, NULL, 2, 1001),
  (3, NULL, 3, 1002),
  (4, NULL, 4, 1003),
  (5, NULL, 5, 1003),
  (6, NULL, 6, 1004),
  (7, NULL, 7, 1005),
  (8, NULL, 8, 1006),
  (9, NULL, 9, 1006);

-- Historial completo de cada documentación: Registrada, EnRemito, EnBolsinSaliente y,
-- si el bolsín ya salió, EnBolsinEnviado (el actual es el que no tiene fecha de fin).
INSERT INTO cambio_estado_documentacion (fecha_hora_inicio, fecha_hora_fin, estado, responsable_id, documentacion_numero) VALUES
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 3, 1),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 3, 1),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 3, 1),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 3, 1),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 3, 2),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 3, 2),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 3, 2),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 3, 2),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 3, 3),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 3, 3),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 3, 3),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 3, 3),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 2, 4),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 2, 4),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 2, 4),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 2, 4),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 2, 5),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 2, 5),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 2, 5),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 2, 5),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 4, 6),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 4, 6),
  ('2026-11-05 11:00:00', NULL, 'EnBolsinSaliente', 4, 6),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 3, 7),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 3, 7),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 3, 7),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 3, 7),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 4, 8),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 4, 8),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 4, 8),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 4, 8),
  ('2026-11-03 09:00:00', '2026-11-04 10:00:00', 'Registrada', 4, 9),
  ('2026-11-04 10:00:00', '2026-11-05 11:00:00', 'EnRemito', 4, 9),
  ('2026-11-05 11:00:00', '2026-11-06 08:30:00', 'EnBolsinSaliente', 4, 9),
  ('2026-11-06 08:30:00', NULL, 'EnBolsinEnviado', 4, 9);

-- Bolsines extra para probar más veces el CU con la EB de Córdoba (precintos 5007, 5008 y 5009).
-- Usan numeración propia (bolsín 110+, remito 1100+, documentación 100+) para no chocar con los
-- fixtures de las pruebas (datos-cmc.sql y datos-varios-origenes.sql).

INSERT INTO bolsin (numero, fecha, numero_precinto, peso, origen_codigo, destino_codigo) VALUES
  (110, '2026-11-09', 5007, 1100, 3, 2),
  (111, '2026-11-09', 5008, 600, 1, 2),
  (112, '2026-11-09', 5009, 850, 4, 2);

INSERT INTO cambio_estado_bolsin (fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_id, bolsin_numero) VALUES
  ('2026-11-09 10:00:00', '2026-11-09 16:00:00', 1, 2, 110),
  ('2026-11-09 16:00:00', '2026-11-10 08:30:00', 2, 2, 110),
  ('2026-11-10 08:30:00', NULL, 3, 2, 110),
  ('2026-11-09 10:00:00', '2026-11-09 16:00:00', 1, 3, 111),
  ('2026-11-09 16:00:00', '2026-11-10 08:30:00', 2, 3, 111),
  ('2026-11-10 08:30:00', NULL, 3, 3, 111),
  ('2026-11-09 10:00:00', '2026-11-09 16:00:00', 1, 4, 112),
  ('2026-11-09 16:00:00', '2026-11-10 08:30:00', 2, 4, 112),
  ('2026-11-10 08:30:00', NULL, 3, 4, 112);

INSERT INTO remito (numero, fecha, estado_id, origen_codigo, destino_codigo, bolsin_numero) VALUES
  (1100, '2026-11-09', 3, 3, 2, 110),
  (1101, '2026-11-09', 3, 3, 2, 110),
  (1102, '2026-11-09', 3, 1, 2, 111),
  (1103, '2026-11-09', 3, 4, 2, 112),
  (1104, '2026-11-09', 3, 1, 2, 112);

INSERT INTO documentacion (numero, asunto, fecha_pase, tipo_documento_id, estado_actual) VALUES
  (100, 'Expediente 210/2026 - Molina, Rita', '2026-11-08', 1, 'EnBolsinEnviado'),
  (101, 'Dictamen - Quiroga, Tomás', '2026-11-08', 2, 'EnBolsinEnviado'),
  (102, 'Historia clínica - Aguirre, Nora', '2026-11-08', 3, 'EnBolsinEnviado'),
  (103, 'Nota de remisión', '2026-11-08', 4, 'EnBolsinEnviado'),
  (104, 'Expediente 305/2026 - Bustos, Leo', '2026-11-08', 1, 'EnBolsinEnviado'),
  (105, 'Dictamen - Ortiz, Clara', '2026-11-08', 2, 'EnBolsinEnviado'),
  (106, 'Historia clínica - Funes, Iván', '2026-11-08', 3, 'EnBolsinEnviado'),
  (107, 'Expediente 8/2026 - Toledo, Mía', '2026-11-08', 1, 'EnBolsinEnviado');

INSERT INTO detalle_remito (id, area_cmc_destino, documentacion_numero, remito_numero) VALUES
  (100, NULL, 100, 1100),
  (101, NULL, 101, 1100),
  (102, NULL, 102, 1101),
  (103, NULL, 103, 1102),
  (104, NULL, 104, 1102),
  (105, NULL, 105, 1103),
  (106, NULL, 106, 1103),
  (107, NULL, 107, 1104);

INSERT INTO cambio_estado_documentacion (fecha_hora_inicio, fecha_hora_fin, estado, responsable_id, documentacion_numero) VALUES
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 2, 100),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 2, 100),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 2, 100),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 2, 100),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 2, 101),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 2, 101),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 2, 101),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 2, 101),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 2, 102),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 2, 102),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 2, 102),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 2, 102),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 3, 103),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 3, 103),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 3, 103),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 3, 103),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 3, 104),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 3, 104),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 3, 104),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 3, 104),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 4, 105),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 4, 105),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 4, 105),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 4, 105),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 4, 106),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 4, 106),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 4, 106),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 4, 106),
  ('2026-11-08 09:00:00', '2026-11-09 10:00:00', 'Registrada', 1, 107),
  ('2026-11-09 10:00:00', '2026-11-09 15:00:00', 'EnRemito', 1, 107),
  ('2026-11-09 15:00:00', '2026-11-10 08:30:00', 'EnBolsinSaliente', 1, 107),
  ('2026-11-10 08:30:00', NULL, 'EnBolsinEnviado', 1, 107);
