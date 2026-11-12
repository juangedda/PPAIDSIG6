# Sesión 07: revisión de la rama y flujo descripto igual al diagrama

PPAI DSI 2026 · 3K5 · Grupo 06 · Fecha: 12/11/2026

Se revisó la rama `francisco-cu28`, que completó A3, A4, A5 y la ampliación del CU 29. Funcionaba
y sus 25 pruebas pasaban, pero había dos problemas con respecto a lo que pide la consigna. El
grupo acordó un criterio y se corrigió el código.

## 1. Qué se encontró

1. **El flujo descripto ya no coincidía con el diagrama de secuencia.** Para llevar la
   documentación marcada hasta `DetalleRemito`, se agregaron parámetros a toda la cadena, también
   en los mensajes dibujados:

   | Método | Diagrama | Rama |
   |---|---|---|
   | `Bolsin.actualizarEstado` | 4 parámetros | 8 |
   | `Bolsin.actualizarRemitos` | 3 | 7 |
   | `Remito.actualizarEstado` | 3 | 7 |
   | `Remito.actualizarDocumentacion` | 2 | 5 |
   | `DetalleRemito.actualizarDocumentacion` | 2 | 5 |
   | `DetalleRemito.buscarDatosDocumentacion` | `[asunto, tipo]` | `[nro, asunto, tipo, área]` |

2. **Tres arreglos paralelos.** La selección viajaba en tres arreglos `int[]`
   (`nrosNoRecibida`, `nrosRechazada`, `nrosParaRedirigir`), y `DetalleRemito` elegía la
   transición con un `if / else if` según en qué arreglo estaba el número. Ahora viaja un solo
   arreglo con lo marcado y el número de la opción.
3. **El Gestor no le preguntaba a los objetos.** Validaba la documentación recorriendo el
   `String[][]` por posición (`fila[1]`, `fila[4]`). Ahora se lo pregunta al bolsín
   (`admiteSeleccion`), que se lo pregunta a sus remitos y detalles (Experto).

## 2. Criterio acordado

**El flujo descripto sigue el diagrama de secuencia tal cual.** Los alternativos A3, A4 y A5 son
un "plus": van por métodos aparte, al final de cada archivo, no cambian la firma de ningún mensaje
dibujado y **no agregan clases** que no estén en el diagrama de clases y el modelo de dominio.

## 3. Cómo quedó

### Flujo descripto (exactamente el diagrama)

```
Gestor  -> selec:Bolsin : actualizarEstado(fhActual, estadoBolsin, estadoRemito, responsable)
Bolsin  -> Bolsin       : actualizarRemitos(fhActual, estadoRemito, responsable)
Bolsin  -> :Remito      : *actualizarEstado(estadoRemito, fhActual, responsable)
Remito  -> Remito       : setEstado(e) y actualizarDocumentacion(fhActual, responsable)
Remito  -> :DetalleRemito : *actualizarDocumentacion(fhActual, responsable)
DetalleRemito -> :Documentacion : recibirYAceptar(fhActual, responsable)   -> State
```

`DetalleRemito.buscarDatosDocumentacion()` volvió a devolver `[asunto, tipo]`, con sólo
`getAsunto()` y `getNombreTipo()`, como en el diagrama.

### Alternativos A3, A4 y A5 (agregado, sin clases nuevas)

> **Corrección del mismo 12/11.** Una primera versión usaba dos clases nuevas (`MarcaRecepcion` y
> `DocumentacionMarcada`). Se quitaron porque no están en el diagrama de clases ni en el modelo de
> dominio del PPAI: los alternativos no pueden agregar objetos. Lo que sigue es la versión vigente.

- **Sólo las clases del diagrama.** La opción viaja como su número en el paso 7 del CU (`int`:
  2, 3 o 4) y lo marcado, como los números de documentación (`int[]`).
- **Separados del flujo normal.** En cada archivo (Pantalla, Gestor, Bolsín, Remito, DetalleRemito,
  Documentación, EstadoRemito, ComisionMedica) los métodos de los alternativos están al final, bajo
  el título "Flujos alternativos ... (extensión: no están en el diagrama de secuencia)".
- **Un `switch` porque el EB elige una de las 4 opciones** (paso 7 del CU):

  | Opción | Qué pide el Gestor | Transición que envía DetalleRemito | ¿Remito parcial? |
  |---|---|---|---|
  | 2 (A3) | "Seleccione la documentación que no se recibió." | `registrarNoRecibida` | Sí |
  | 3 (A4) | "Seleccione la documentación que no corresponde a esta comisión médica." | `recibirYRechazar` | Sí |
  | 4 (A5) | Sólo si el destino es la CMC; la documentación tiene que tener área | `redirigir` | No |

  El `switch` es sobre la opción que eligió el EB, no sobre el estado: si la transición no es
  válida en el estado actual, la rechaza el State.
- Métodos aparte (sobrecargas o con nombre propio):
  - `Bolsin.actualizarEstado(..., estadoRemitoParcial, responsable, opcionRecepcion, nrosMarcados)`;
  - `Remito.actualizarEstado(..., opcionRecepcion, nrosMarcados)`;
  - `DetalleRemito.actualizarDocumentacion(..., opcionRecepcion, nrosMarcados)`: a la no marcada le
    aplica el `actualizarDocumentacion` del diagrama (`recibirYAceptar`);
  - `buscarDocumentacionParaMarcar()` y `admiteSeleccion(nro, opcion)` en Bolsín, Remito y
    DetalleRemito.
- **Gestor:** en `tomarSeleccionOpcionRecepcion`, si la opción no es la 1 se llama a
  `solicitarSeleccionDocumentacion(opcion)` (al final del archivo). En `actualizarBolsin()`, sin
  marcas se usa el `actualizarEstado` del diagrama, y con marcas, la sobrecarga.
- **Pantalla:** un solo `solicitarSeleccionDocumentacion(mensaje, documentacion)`. La documentación
  para marcar (con número y área) llega en la respuesta de `tomarSeleccionOpcionRecepcion`.

### A5 más fácil de mostrar

Hay un perfil nuevo, `cmc` (`application-cmc.properties`), que carga `datos-cmc.sql` después de
`data.sql`:

```powershell
cd back
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=cmc"
```

## 4. Verificación

- 26 pruebas, todas pasan (también después de quitar las clases nuevas). Se adaptaron dos pruebas
  de integración y se sumó una para la agrupación del flujo descripto.
- Por HTTP:
  - flujo descripto;
  - A3 con el 5001 (la documentación 2 quedó NoRecibida y el remito 1001 parcial);
  - opción 4 con un destino que no es la CMC (409 con mensaje).
  - Repetido después de quitar las clases nuevas: A3 con el 5001 y la opción 4 rechazada; el CU 29
    informó la documentación 2 como NoRecibida.
- En el front con Chrome:
  - A3 completo;
  - A5 con el perfil `cmc` (la documentación 10 quedó ParaRedirigir y el CU 29 avisó a Córdoba).

## 5. Ajustes finales del mismo día (revisión de los diagramas)

Se compararon otra vez los tres diagramas con el código y se acercaron dos detalles a la secuencia:
- `Bolsin.getOrigen()` devuelve el nombre: el `getNombre()` a la ComisionMedica lo envía el Bolsín, como
  en el diagrama (antes lo enviaba el Gestor).
- El final de `tomarConfirmacionRegistro` sigue el orden dibujado: `llamarCasoUso29()` prepara una
  notificación por CM de origen, `mostrarEjecucionExitosa()`, el «include» las envía y `finCU()`.

Las correcciones de parámetros y retornos que se pueden hacer en los diagramas están en
`docs/diferencias_codigo_diagramas.md`, sección E. También se generó la guía de estudio
`Mapa_del_codigo_CU28.pdf` (raíz del repositorio), con la ubicación de cada mensaje en el código.

## 6. Las sesiones 02 a 06

Describen cómo se hicieron los alternativos con la versión anterior (arreglos y parámetros
agregados). Quedan como historia. **El diseño vigente es el de esta sesión.**
