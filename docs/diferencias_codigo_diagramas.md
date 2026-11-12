# Diferencias entre el código y los diagramas originales

PPAI DSI 2026 · 3K5 · Grupo 06 · Actualizado al 12/11/2026

Los diagramas originales de `docs/diagramas/` (**DIAGRAMA CLASES**, **SECUENCIA PATRON** y
**MaquinaEstadosPPAI**) **no se modificaron**. Este archivo reúne las diferencias entre el código y los diagramas. Algunas aparecen al
implementar los flujos alternativos; otras necesitan validación con la tutora. No se presentan
como coincidencias exactas.
El detalle de cada flujo está en `docs/sesiones/Sesion_01` a `Sesion_05`.

## Criterio (acordado el 12/11)

**El flujo descripto sigue el diagrama de secuencia tal cual**: los mismos mensajes, con los mismos
parámetros y entre los mismos objetos. Los flujos alternativos A3, A4 y A5 y la ampliación del CU 29
son un agregado: van por **métodos aparte**, sin cambiar la firma de ningún mensaje dibujado.
Las diferencias que quedan en el flujo descripto son las de la sección B, que ya existían y tienen
motivo técnico. El detalle del cambio está en `docs/sesiones/Sesion_07_revision_y_flujo_del_diagrama.md`.

Categorías:
- **A. Agregados** para A3, A4, A5 y el contenido del CU 29. El diagrama de secuencia sólo modela el
  flujo descripto, así que estos métodos no aparecen en él.
- **B. Justificadas** en el flujo descripto. Son decisiones de implementación con un motivo técnico.
- **C. Erratas del diagrama.** El código sigue a la secuencia.
- **D. No implementado.** Pendiente de decisión.

## A. Agregados por los flujos alternativos y el CU 29

### A.1 Sin clases nuevas en el modelo

Los alternativos usan **sólo las clases del diagrama de clases**. No se agrega ninguna clase al
modelo de dominio: la opción elegida viaja como su número en el paso 7 del CU (`int`) y la
documentación marcada, como sus números (`int[]`). En cada archivo, los métodos de los alternativos
están **al final**, en una sección titulada "Flujos alternativos ... (extensión: no están en el
diagrama de secuencia)".

El EB elige una de las 4 opciones, así que la elección es un `switch` (la alternativa del CU). Hay
dos, cada uno en el experto que corresponde:
- **Gestor** (`solicitarSeleccionDocumentacion`): qué se le pide al EB y, en la opción 4, que el
  destino sea la Central.
- **DetalleRemito** (`actualizarDocumentacion` con la opción): qué transición le envía a su
  documentación marcada (2 → `registrarNoRecibida`, 3 → `recibirYRechazar`, 4 → `redirigir`).

No hay condicionales sobre el **estado**: de eso se sigue encargando el State.

### A.2 Mensajes nuevos (no están en la secuencia)

| Emisor → receptor | Mensaje | Flujo |
|---|---|---|
| Gestor → Pantalla | `solicitarSeleccionDocumentacion(mensaje, documentacion: String[][])` | A3, A4, A5 |
| EB → Pantalla → Gestor | `tomarSeleccionDocumentacion(nrosDocumentacion: int[])` | A3, A4, A5 |
| Gestor → Pantalla | `mostrarRegistroCancelado()` | A6 |
| Gestor → selec:Bolsin → Remito → DetalleRemito | `buscarDocumentacionParaMarcar(): String[][]` (número, asunto, tipo y área) | A3, A4, A5 |
| Gestor → selec:Bolsin → Remito → DetalleRemito | `admiteSeleccion(nroDocumentacion, opcionRecepcion): boolean` | A3, A4, A5 |
| Gestor → selec:Bolsin → ComisionMedica | `esDestinoCentral()` → `esCentral()` | A5 |
| Gestor → selec:Bolsin | `actualizarEstado(fhActual, estadoBolsin, estadoRemito, estadoRemitoParcial, responsable, opcionRecepcion, nrosMarcados)` (sobrecarga; la de 4 parámetros queda igual que en el diagrama) | A3, A4, A5 |
| Bolsin → Remito | `actualizarEstado(estadoRemito, estadoRemitoParcial, fhActual, responsable, opcionRecepcion, nrosMarcados)` (sobrecarga) | A3, A4, A5 |
| Remito → DetalleRemito | `dejaRemitoParcial(opcionRecepcion, nrosMarcados)` y `actualizarDocumentacion(fhActual, responsable, opcionRecepcion, nrosMarcados)` (sobrecarga) | A3, A4, A5 |
| DetalleRemito → Documentacion | `registrarNoRecibida`, `recibirYRechazar` o `redirigir` (están en el diagrama de clases) | A3, A4, A5 |
| Gestor → EstadoRemito | `*esRecibidoYAceptadoParcial()` (sólo en los alternativos) | A3, A4 |
| Gestor → selec:Bolsin | `buscarOrigenesDocumentacion()` y `buscarSituacionDocumentacion(cm)` | CU 29 |
| Remito, Documentacion | `getOrigen()`, `getNumero()`, `getNombreEstado()` | CU 29, A3, A4, A5 |
| Gestor → InterfazNotificacionBolsin | `notificarRecepcionBolsin(NotificacionRecepcionBolsin)`, **una vez por CM de origen** | CU 29 |

### A.3 Atributos nuevos

- **Gestor:** `nrosDocumentacionMarcada: int[]` (null en el flujo descripto). La opción elegida se
  guarda en `opcRecepcion`, que ya está en el diagrama de clases.
- **Pantalla:** `documentacionParaMarcar`, la documentación que el EB tiene que marcar.

### A.4 Otras clases que no están en el diagrama

- `InterfazNotificacionBolsin`, `NotificacionBolsinLog` y `NotificacionRecepcionBolsin` (CU 29).
- `ManejadorErrores`: traduce las excepciones a 400 y 409.
- `EstadoDocumentacionConverter`: persistencia del estado por nombre.
- Los records de `dto` y los repositorios: diseño de persistencia y de la interfaz.

## B. Diferencias de implementación en el flujo descripto

| Diagrama | Código | Motivo |
|---|---|---|
| `getNombre()` lo envía Empleado a ComisionMedica, dentro de `obtenerCM(): ComisionMedica` | El Gestor recibe la `ComisionMedica` y le pide `getNombre()` | La firma devuelve `ComisionMedica`, y el Gestor necesita el objeto (`esCmDestino`) y además el nombre. (En `getOrigen()` ya no hay diferencia: desde el 12/11 el Bolsín le pide el nombre a su origen y lo devuelve, como en la secuencia) |
| `Bolsin.buscarRemitos(): Remito[]` | Devuelve `int[]` | Se respeta `*getNumero()` de Bolsín a Remito y el gestor no se acopla a Remito |
| `Bolsin.crearCambioEstado()` sin parámetros | `crearCambioEstado(fhInicio, e, responsable)`, privado | El `new(fhInicio, e, usuarioResponsable)` necesita esos datos |
| `setFechaHoraFin` directo a Actual:CambioEstadoBolsin | Primero busca el actual con `sosActual()` | Es la misma búsqueda que la secuencia dibuja antes |
| `getOpcionesRecepcion(): void` (clases) | Devuelve `String[]` | Se pasa a `mostrarOpcionesRecepcionParaSeleccion` |
| — | `saveAndFlush()`, `findAll()` y las validaciones del orden de los pasos | Diseño de persistencia y robustez. `saveAndFlush` escribe en la base antes de notificar |
| `Bolsin.buscarBolsinesEnviados(cmUsuario)` (clases) | Sin parámetro; la CM se pregunta con `esCmDestino` | Se sigue a la secuencia |
| Atributos del Gestor `nroPrecintoCM`, `origenCM`, `nroRemito`, `asuntoDocumentación`, `tipoDocumentación` | `nrosPrecinto[]`, `origenes[]`, `nrosRemito[]`, `documentacion[][]` | El gestor recuerda listas, no un solo valor |

## C. Diferencias de nombres y firmas entre diagramas

- `tomarConfirmaionRegistro` (le falta una "c").
- `FinCU()` en la secuencia y `finCU()` en clases.
- `esCmDestino` (secuencia) y `esCMDestino` (clases).
- `recibiryAceptar` y `recibiryRechazar` (clases) frente a `recibirYAceptar` (secuencia).
- `mostrarNumRemitosYDocumentaciónAsociada` (clases, con tilde y mayúscula).
- `esRecibidoyAceptado()` y `esRecibidoyAceptada()` figuran en **EstadoBolsin**; corresponden a
  **EstadoRemito**, que es a quien se lo pregunta la secuencia.
- `DetallesRemito` (clases) frente a `:DetalleRemito` (secuencia). El código usa `DetalleRemito`.
- `Gestor.actualizarRemitoyDocumentación()` está en clases, pero no en la secuencia ni en el código.
- `EnBolsinEnviado.recibirYAceptar(...)` figura con retorno `RecibidaYAceptada`; en el abstracto
  es `void`.

## D. No implementado, pendiente de decisión

- **Operaciones del State que pertenecen a otros CU.** El diagrama de clases las declara en
  `EstadoDocumentacion`, en `Documentacion` y en varios estados concretos, y el código no:
  `darDeBaja`, `enviar`, `incluirEnBolsin`, `incluirEnRemito`, `modificarBolsin`,
  `modificarRemito`, `removerDeBolsin`, `removerDeRemito` y `revisarDocumentacion` (más
  `cancelarRemito` en Documentación).
  - El código sólo declara las 4 transiciones del CU 28.
  - AGENTS.md regla 7 dice "declara todos los métodos dependientes del estado".
  - Ver `docs/decisiones_para_la_tutora.md`, punto 10.
- La máquina de estados no cambia. Las transiciones del CU 28 están todas implementadas, incluida
  `RecibidaYRechazada → ParaRedirigir` (sesión de la Fase 1), aunque ningún flujo del CU 28 la usa
  hoy.

## E. Correcciones de los diagramas (revisión del 12/11)

> **Aplicadas.** El grupo hizo estas correcciones en `DIAGRAMA CLASES version final` y
> `SECUENCIA PATRON versión final` (docs/diagramas). Con esas versiones, las diferencias 2 y 3 de la
> sección B (`buscarRemitos(): int[]` y `crearCambioEstado` con parámetros) ya no son diferencias.

Se revisaron otra vez los tres diagramas contra el código. **El código sigue el diagrama de secuencia
mensaje por mensaje.** Estas correcciones son sólo de parámetros, retornos o nombres; no cambian la
lógica de la secuencia.

### Diagrama de secuencia

| Mensaje | Hoy dice | Debería decir | Motivo |
|---|---|---|---|
| Gestor → selec:Bolsin | `buscarRemitos(): remito[]` | `buscarRemitos(): int[]` | Devuelve los números (diferencia B) |
| Bolsin → Bolsin | `crearCambioEstado()` | `crearCambioEstado(fhInicio: DateTime, e: EstadoBolsin, responsable: Empleado): CambioEstadoBolsin` | El `new(...)` necesita esos datos (diferencia B) |
| Gestor → :Bolsin | `getOrigen(): ComisionMedica` | `getOrigen(): String` | El Bolsín le pide `getNombre()` a su origen y devuelve el nombre |

### Diagrama de clases

| Clase | Hoy dice | Debería decir |
|---|---|---|
| Bolsín | `buscarBolsinesEnviados(cmUsuario: ComisionMedica): boolean` | `buscarBolsinesEnviados(): boolean` (como en la secuencia) |
| Bolsín | `buscarRemitos(): Remito[]` | `buscarRemitos(): int[]` |
| Bolsín | `getOrigen(): ComisionMedica` | `getOrigen(): String` (devuelve el nombre del origen; el `getNombre()` lo envía el Bolsín, como en la secuencia) |
| Bolsín | `+ crearCambioEstado(): void` | `- crearCambioEstado(fhInicio: DateTime, e: EstadoBolsin, responsable: Empleado): CambioEstadoBolsin` |
| Bolsín | `esCMDestino(cm)` | `esCmDestino(cm)` (como en la secuencia) |
| Gestor | `getOpcionesRecepcion(): void` | `getOpcionesRecepcion(): String[]` |
| Gestor | `actualizarRemitoyDocumentación(): void` | Quitarlo: no está en la secuencia (lo hace `Bolsin.actualizarRemitos`) |
| EnBolsinEnviado | `recibirYAceptar(...): RecibidaYAceptada` | `recibirYAceptar(...): void` (como en la clase abstracta) |
| RecibidaYRechazada | `redirigir(): void` | `redirigir(doc: Documentacion, historial: CambioEstadoDocumentacion[], fhActual: DateTime, responsable: Empleado): void` |
| EstadoBolsin | `esRecibidoyAceptada()` y `esRecibidoyAceptado()` | Quitarlos: son de EstadoRemito (`esRecibidoyAceptado(): boolean` ya está ahí) |
| Documentación | `recibiryAceptar`, `recibiryRechazar` | `recibirYAceptar`, `recibirYRechazar` (como en la secuencia) |

Opcionales:
- EnBolsinEnviado y ParaRedirigir: el parámetro se llama `this: Documentacion` y en la clase abstracta
  `doc: Documentacion`. No hace falta cambiarlo (el diagrama es independiente del lenguaje); sólo daría
  uniformidad con la clase abstracta.
- Gestor: marcar con `-` (privados) los métodos que son mensajes a sí mismo (`buscarUsuarioLogueado`,
  `buscarCMUsuario`, `buscarBolsinesEnviados`, `buscarRemitos`, `buscarDocumentacion`, `getOpcionesRecepcion`,
  `getFechaHoraActual`, `actualizarBolsin`, `llamarCasoUso29`, `finCU`).
- Gestor, atributos: `nroPrecintoCM: int`, `origenCM: String`, `nroRemito: int`, `asuntoDocumentación` y
  `tipoDocumentación` guardan listas en el código (`nrosPrecinto: int[]`, `origenes: String[]`, `nrosRemito: int[]`,
  `documentacion: String[][]`). También guarda `cmUsuario: ComisionMedica` y `bolsinSeleccionado: Bolsin`.

Cambios en el código del 12/11 para seguir la secuencia todavía más de cerca:
- `Bolsin.getOrigen()` devuelve el nombre del origen: el `getNombre()` lo envía el Bolsín, como en el diagrama.
- El orden del final es exactamente el del diagrama: `llamarCasoUso29()` prepara una notificación por CM de
  origen, después `mostrarEjecucionExitosa()`, después el «include» (se envían) y por último `finCU()`. Si la
  notificación falla, la transacción se revierte y el front recibe el error, no el éxito.

Lo que queda como diferencia justificada (sección B): el `getNombre()` dentro de `obtenerCM()` (el Gestor
necesita el objeto CM y su nombre) y el `sosActual()` con el que el Bolsín encuentra a Actual:CambioEstadoBolsin
(es la misma búsqueda dibujada en `buscarBolsinesEnviados()`).

