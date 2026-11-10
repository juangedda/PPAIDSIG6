# Contexto del proyecto para seguir trabajando (grupo e IA)

**PPAI DSI 2026 "Bolsines" · UTN FRC · 3K5 · Grupo 06 · Estado al 09/11/2026**

Este archivo resume todo lo necesario para retomar el trabajo desde donde quedó. Está escrito
para que cualquier integrante se lo pase a una IA (Claude, ChatGPT, Copilot, Cursor) junto con la
carpeta del proyecto, y la IA siga con las mismas reglas y decisiones.

---

## 0. Cómo usarlo

1. Cloná el repositorio del grupo (o descomprimí el .zip) y levantalo siguiendo
   `COMO_EJECUTAR.txt`.
2. Abrí una conversación con la IA y adjuntá o dejá que lea:
   - este archivo (`CONTEXTO_IA.md`);
   - `AGENTS.md` (reglas del proyecto);
   - `docs/PENDIENTE.txt` (plan de trabajo);
   - `docs/sesiones/Sesion_01_implementacion_CU28.md`, que tiene la tabla mensaje por mensaje
     del diagrama de secuencia contra el código.
3. Pegá este mensaje inicial y completá la tarea:

```
Sos mi asistente para el PPAI de Diseño de Sistemas de Información (UTN FRC 2026, caso
"Bolsines"). Antes de hacer nada, leé CONTEXTO_IA.md, AGENTS.md, docs/PENDIENTE.txt y
docs/sesiones/Sesion_01_implementacion_CU28.md, y respetá todas las reglas que figuran ahí
(sobre todo: el código sigue a los diagramas y los diagramas NO se cambian; todo en español;
100 % orientado a objetos; código corto y sin repeticiones).
Tarea de hoy: <describir la tarea, por ejemplo "implementar el flujo alternativo A3">.
Antes de escribir código, decime qué archivos vas a tocar y por qué.
```

Si usan **Claude Code** o **Cursor** dentro de la carpeta, `AGENTS.md` se puede leer solo.
Igual conviene decirle que lea este archivo.

---

## 1. El trabajo práctico

- **Materia:** Diseño de Sistemas de Información, UTN Facultad Regional Córdoba. Cátedra
  Meles / Massano.
- **Caso:** organismo de comisiones médicas (CM) que se envían documentación en **bolsines**
  por correo. Cada bolsín tiene remitos, y cada remito tiene documentación.
- **Asignación del grupo (par):**
  - **CU 28 "Registrar recepción de bolsín"**;
  - máquina de estados de **Documentación**;
  - patrón de Gamma **State aplicado a la clase Documentación**.
- **Entrega 2 (Diseño), que es la etapa actual:**
  1. Rediseño con el patrón: identificación y justificación, vista estática (clases) y vista
     dinámica (secuencia). **Ya está hecho**: son los diagramas de `docs/diagramas`.
  2. Implementación del rediseño. Se evalúa:
     - que corra el **flujo descripto + al menos 2 flujos alternativos**;
     - la **consistencia con el modelado** (pantalla, gestor, entidades, patrón);
     - la **persistencia en base de datos**;
     - la defensa oral mostrando el patrón en el código.
  3. UX: moodboard, mapa de empatía y user journey de la Persona del Encargado de Bolsines
     (EB), y diseño de las interfaces con patrones de GUI.
  4. PDF de la entrega: `PPAI2026_3K5_G06_E2_Diseño.pdf`, con carátula completa. El código no
     se sube: se defiende.
- **Fuentes de la cátedra:** `docs/catedra/`. Ahí están la ERS v1.2 (con el CU 28 completo en
  4.1), la descripción del caso, las consignas, las consignas de UX y el modelo de dominio.

### CU 28 en una línea por paso

1. El EB elige "registrar recepción".
2. El sistema muestra la CM del usuario.
3. Lista los bolsines **Enviados** a esa CM (precinto y CM de origen, con filtro).
4. El EB elige uno.
5. El sistema muestra los remitos y la documentación (asunto y tipo).
6. Muestra 4 opciones de recepción.
7. El EB elige la **opción 1** ("el contenido es igual al registrado").
8. El sistema pide confirmación y el EB confirma.
9. El sistema actualiza:
   - bolsín → RecibidoEnCMDestino;
   - remitos → RecibidoYAceptado;
   - documentación → RecibidaYAceptada, con el empleado responsable.
10. Incluye el **CU 29** (notificar a la CM de origen) y fin.

**Flujos alternativos:**

| | Descripción |
|---|---|
| A1 | No se encuentra el precinto |
| A2 | No se encuentra la CM de origen |
| A3 | Opción 2: no llega toda la documentación |
| A4 | Opción 3: hay documentación que no corresponde al destino |
| A5 | Opción 4: hay que redirigir documentación a un área (sólo si la CM destino es la Central) |
| A6 | El EB no confirma |

---

## 2. Reglas del equipo (no negociables)

1. **El código sigue a los diagramas; los diagramas NO se cambian.** Son una entrega pasada con
   correcciones encima. Cada mensaje del diagrama de secuencia
   (`docs/diagramas/SECUENCIA PATRON.pdf`) tiene que existir en el código con el mismo nombre,
   los mismos parámetros y el mismo emisor y receptor. Las pocas excepciones están
   justificadas en la sección 8.
2. Convenciones de la cátedra:
   - Pantalla = «boundary»;
   - Gestor = «control», que es una Fabricación Pura;
   - entidades con comportamiento (Experto);
   - CambioEstado con `fechaHoraInicio`/`fechaHoraFin` (el actual no tiene fin) y responsable;
   - GRASP: Experto, Creador, Controlador, Bajo Acoplamiento y Alta Cohesión.
3. **Todo en español:** clases, métodos, variables, mensajes al usuario y commits.
4. **100 % orientado a objetos.** El código es corto y sin repeticiones: nada de `if`/`switch`
   sobre el nombre de un estado (para eso está el State). Las entidades nunca se devuelven a la
   pantalla: se usan records en `dto`.
5. Inyección por constructor. La hora sale del bean `Clock`.
6. Antes de dar algo por terminado, compilar y probar el flujo en el front.
7. No pegar contraseñas ni claves en el chat con la IA.
8. Respetar la estructura del repositorio de ejemplo de la cátedra
   (`github.com/LiberaeMentis/dsi-implementaciones-uc`). Mapeo:
   - pantalla = `@RestController` con un endpoint por cada mensaje del actor, con el mismo
     nombre;
   - gestor = `@Service`.

Detalle completo: `AGENTS.md`.

---

## 3. Tecnología y cómo correrlo

| Parte | Tecnología |
|---|---|
| Back | Java 21 · Spring Boot 4.1.1 · Spring Data JPA / Hibernate 7 · Lombok · Maven (wrapper `mvnw`) |
| Base de datos | H2 en modo archivo (`back/data/bolsines.mv.db`). Se recrea en cada arranque desde `data.sql` |
| Front | React 19 · Vite 7 · Material UI 7 · Axios. Proxy de `/api` a `:8080` |
| Pruebas | JUnit + Mockito (`EstadoDocumentacionTest`, 8 pruebas del patrón State) |

**Requisitos:** JDK **21** y Node.js 22 LTS. El paso a paso completo para una computadora
nueva (instalación, prueba del CU, consola H2 y problemas comunes) está en
**`COMO_EJECUTAR.txt`**.

```powershell
# Back -> http://localhost:8080   (salud: /actuator/health, base: /h2-console)
cd back
.\mvnw.cmd spring-boot:run          # compilar y probar: .\mvnw.cmd clean install

# Front (otra terminal) -> http://localhost:3000
cd front
npm install
npm run dev
```

Para cortar cualquiera de los dos: Ctrl + C (y `S` si pregunta).

Para entrar a la consola H2 (`/h2-console`):
- JDBC URL: `jdbc:h2:file:./data/bolsines`
- usuario: `sa`
- contraseña: vacía

### Problemas que ya aparecieron y su solución

- **`ExceptionInInitializerError ... EndPosTable` al compilar:** Maven está usando un JDK más
  nuevo que el 21 y Lombok falla. Hay que apuntar `JAVA_HOME` a un JDK 21:
  ```powershell
  $env:JAVA_HOME = "C:\ruta\al\jdk-21"
  ```
- **IntelliJ marca dependencias sin resolver o tira "SSL peer shut down":**
  - el antivirus (por ejemplo, el escaneo HTTPS de AVG) corta la descarga. Hay que
    desactivarlo un momento o compilar una vez con `mvnw`;
  - en IntelliJ, usar el Maven wrapper y elegir JDK 21 tanto en "JDK for importer" como en
    "Runner".
- **`mvnw` no se reconoce:** los comandos se corren desde la carpeta `back`.

---

## 4. Arquitectura y dónde está cada cosa

Es una sola aplicación en capas, no microservicios: ningún documento de la cátedra los pide y
el repositorio de ejemplo tampoco los usa.

```
back/src/main/java/ppai/bolsines/
├── pantalla/      PantallaRegistroRecepcionBolsines (@RestController, «boundary») + ManejadorErrores
├── gestor/        GestorRegistroRecepcionBolsines (@Service, «control»)
├── modelo/
│   ├── usuario/         Sesion, Usuario, Empleado
│   ├── comisionmedica/  ComisionMedica
│   ├── bolsin/          Bolsin, CambioEstadoBolsin, EstadoBolsin
│   ├── remito/          Remito, DetalleRemito, EstadoRemito
│   └── documentacion/   Documentacion (contexto State), EstadoDocumentacion (abstracta),
│                        9 estados concretos, CambioEstadoDocumentacion, TipoDocumento,
│                        EstadoDocumentacionConverter
├── notificacion/  InterfazNotificacionBolsin + NotificacionBolsinLog (CU 29, por ahora al log)
├── repositorio/   Spring Data JPA
├── dto/           records ...Request / ...Response
└── config/        bean Clock
back/src/main/resources/  application.properties, data.sql
front/src/                App.jsx (wizard de 5 pasos), services/api.js (una función por mensaje),
                          components/pasos/*.jsx, components/layout/MainLayout.jsx
docs/                     catedra/, diagramas/, sesiones/, PENDIENTE.txt
```

### Cómo viaja un mensaje (igual que en el diagrama)

1. El front hace `POST /api/registro-recepcion-bolsin/<mensaje>`; el nombre es el del
   diagrama, por ejemplo `tomarSeleccionBolsin`.
2. La **Pantalla** recibe el mensaje y se lo pasa al **Gestor** con el mismo nombre.
3. El Gestor coordina a las entidades (`buscarRemitos()`, `actualizarBolsin()`, ...).
4. El Gestor le manda a la Pantalla los `mostrarX(...)` del diagrama. La Pantalla guarda lo que
   hay que mostrar y el endpoint se lo devuelve al front como JSON.

Detalles de esta forma de comunicación:

- **Referencia mutua.** La Pantalla y el Gestor se conocen entre sí, y Spring no puede crear
  dos beans que se necesitan en el constructor. Por eso el Gestor recibe la Pantalla con
  `@Lazy`. `back/lombok.config` hace que Lombok copie ese `@Lazy` al constructor.
- **Concurrencia.** La Pantalla y el Gestor guardan estado entre pedidos: es una pantalla para
  un EB. Por eso los 4 endpoints de la Pantalla son `synchronized`. Sin eso, dos pedidos
  simultáneos se mezclaban. Pasaba, por ejemplo, con React en modo desarrollo, que llama dos
  veces al abrir, y la lista de bolsines aparecía vacía. **No sacar el `synchronized`.**

La tabla completa de los 68 mensajes del diagrama, cada uno con su línea de código, está en
`docs/sesiones/Sesion_01_implementacion_CU28.md`.

---

## 5. Patrón State (lo que se defiende)

- **Contexto: `Documentacion`.** Tiene `estadoActual` y el historial `cambioEstado`. Sus
  métodos `recibirYAceptar`, `recibirYRechazar`, `registrarNoRecibida` y `redirigir` sólo
  delegan, pasando también los datos del cambio:
  `estadoActual.recibirYAceptar(this, cambioEstado, fhActual, responsable)`.
- **Estado abstracto: `EstadoDocumentacion`** (clase abstracta).
  - Por defecto cada transición **lanza `IllegalStateException`**, que la API devuelve como
    409.
  - Tiene el código común `cambiarEstado(...)`, que:
    1. cierra el CambioEstado actual con `setFechaHoraFin`;
    2. llama a `crearCambioEstado(...)`;
    3. llama a `doc.setEstado(nuevo)` y a `doc.agregarCE(ce)`.

    Reusar ese código es lo que justifica la herencia.
- **Estados concretos:** Registrada, EnRemito, EnBolsinSaliente, EnBolsinEnviado,
  RecibidaYAceptada, RecibidaYRechazada, NoRecibida, ParaRedirigir y DeBaja. Cada uno redefine
  sólo las transiciones que salen de él:
  - `EnBolsinEnviado` puede ir a RecibidaYAceptada, RecibidaYRechazada, NoRecibida o
    ParaRedirigir;
  - `ParaRedirigir` puede ir a RecibidaYAceptada o RecibidaYRechazada.
- **Persistencia del estado:** se guarda el **nombre** del estado en una columna (por ejemplo
  `EnBolsinEnviado`). `EstadoDocumentacionConverter` (JPA `AttributeConverter`) recrea el
  objeto concreto al leer.
- **Lo que ya está listo para A3, A4 y A5:** las 4 transiciones del CU 28 ya están
  implementadas y probadas, aunque hoy el flujo sólo usa `recibirYAceptar`.

---

## 6. Datos de prueba (`data.sql`)

- **CMs:** 1 Central (CABA), 2 Córdoba, 3 Rosario, 4 Mendoza.
- **Empleados:** Laura Gómez (Córdoba), Martín Pérez (Rosario), Ana Ruiz (Central), Julián Sosa
  (Mendoza).
- **Sesión activa:** la que no tiene `fecha_hora_fin`. Hoy es la de Laura Gómez (usuario 1), así
  que **se trabaja como EB de Córdoba**.
- **Bolsines:**

  | Bolsín | Precinto | Origen → Destino | Estado | Remitos y documentación |
  |---|---|---|---|---|
  | 101 | 5001 | Central → Córdoba | Enviado | Remitos 1001 y 1002, documentación 1, 2 y 3 |
  | 102 | 5002 | Rosario → Córdoba | Enviado | Remito 1003 |
  | 103 | 5003 | Mendoza → Córdoba | Cerrado | No aparece en la lista |
  | 104 | 5004 | Central → Rosario | Enviado | No aparece: es de otra CM |
  | 105 | 5005 | Mendoza → Córdoba | Enviado | Remito 1006 |

- **Estados de remito:** ya existe `RecibidoYAceptadoParcial`, que hace falta para A3.
- **Al reiniciar el back, los datos vuelven al estado inicial.**

---

## 7. Estado actual (qué funciona y está verificado)

- [x] Flujo descripto completo, back y front, con persistencia.
- [x] **A1/A2**: filtro sin resultados, resuelto en el front.
- [x] **A6**: no confirma; no modifica nada.
- [x] CU 29 detrás de `InterfazNotificacionBolsin`. Escribe en el log
  `CU 29 - Correo a ...`.
- [x] Las 8 pruebas del State pasan.
- [x] Código alineado mensaje por mensaje con `SECUENCIA PATRON.pdf`.
- [x] Probado en la PC el 09/11, con el back y el front reales: se recibieron los 3 bolsines
  de Córdoba uno por uno, la lista se actualizó bien y al final mostró "No hay bolsines
  enviados". La base quedó correcta.
- [x] Revisado en la base después de confirmar:
  - documentación en RecibidaYAceptada, con el CambioEstado anterior cerrado y responsable;
  - remitos en RecibidoYAceptado;
  - bolsín en RecibidoEnCMDestino.
- [ ] Opciones 2, 3 y 4 de recepción (A3, A4, A5). Hoy contestan 400 con "todavía no está
  implementada".

Con el flujo descripto + A1/A2 + A6 **ya se cumple el mínimo** de la consigna (flujo descripto
+ 2 alternativos). Implementar A3, A4 y A5 es para que "funcione todo" y para lucir el
polimorfismo del State en la defensa.

---

## 8. Diferencias justificadas entre el código y el diagrama

No hay que "corregirlas" en el código. Son decisiones tomadas porque el diagrama no se puede
cambiar y hay cosas que Java no permite hacer tal cual.

1. **`getNombre()` dentro de `obtenerCM()` y de `getOrigen()`.**
   - En el diagrama, Empleado (y Bolsín) le envía `getNombre()` a ComisionMedica dentro de
     `obtenerCM(): ComisionMedica` (y de `getOrigen(): ComisionMedica`).
   - En el código, esos métodos devuelven la `ComisionMedica`, como dice su firma, y el Gestor
     le pide `getNombre()` a esa CM.
   - Motivo: el Gestor necesita el **objeto** CM (para `esCmDestino(cm)`) y también su
     **nombre** (para `mostrarCM`). Un método no puede devolver las dos cosas. Si Empleado
     hiciera el `getNombre()`, tendría que devolver el texto y se rompería la firma. El Gestor
     ya conocía a ComisionMedica, así que no se agrega acoplamiento.
2. **`Bolsin.buscarRemitos()` devuelve `int[]`, no `remito[]`.**
   - Se respetó la flecha `*getNumero()` de Bolsín a Remito y lo que recibe la pantalla
     (`mostrarNumRemitosyDocumentacionAsociada(nrosRemito: int[], ...)`).
   - Si devolviera los objetos Remito, quien llamaría a `getNumero()` sería el Gestor (otra
     flecha), y el Gestor quedaría acoplado a Remito.
3. **`Bolsin.crearCambioEstado(fhInicio, e, responsable)` tiene parámetros; en el diagrama
   no.**
   - El `new(fhInicio, e, usuarioResponsable)` del diagrama necesita esos tres datos, y son
     parámetros de `actualizarEstado(...)`.
   - Sin parámetros, el bolsín tendría que guardar datos temporales como atributos, y eso es
     un mal diseño.
   - La versión de Documentación (`crearCambioEstado(fhInicio, nuevo, responsable)`) sí tiene
     parámetros en el diagrama, así que el código queda coherente con eso.

Otras diferencias menores:
- Para cerrar el CambioEstadoBolsin actual se busca con `sosActual()`, la misma búsqueda que el
  diagrama dibuja antes.
- `getOpcionesRecepcion()` devuelve `String[]`.
- El «include» del CU 29 va antes de `mostrarEjecucionExitosa()`, para no mostrar un éxito si
  la notificación falla.
- `save()`, `findAll()` y las validaciones son del diseño de persistencia y no aparecen en el
  diagrama.

Los errores de tipeo de los diagramas (por ejemplo `tomarConfirmaionRegistro`) se ignoran. El
código usa el nombre correcto.

---

## 9. Lo que falta (en orden) y cómo encararlo

### 9.1 Opciones de recepción 2, 3 y 4 (A3, A4, A5)

Reglas de negocio de la ERS ("Recepción de un Bolsín en una CM Destino"):

| Opción | Documentación marcada por el EB | Resto | Remito |
|---|---|---|---|
| 2: no se recibe toda (A3) | `registrarNoRecibida` → NoRecibida | `recibirYAceptar` | **RecibidoYAceptadoParcial** si le falta alguna; si no, RecibidoYAceptado |
| 3: no corresponde al destino (A4) | `recibirYRechazar` → RecibidaYRechazada (vuelve al origen) | `recibirYAceptar` | Decisión del grupo: lo natural es RecibidoYAceptadoParcial |
| 4: redirigir a un área (A5) | `redirigir` → ParaRedirigir. Sólo si la CM destino es la **Central**; se usa `DetalleRemito.areaCMCDestino` | `recibirYAceptar` | RecibidoYAceptado |

En todos los casos el bolsín pasa a RecibidoEnCMDestino y se notifica con el CU 29.

**Propuesta de implementación**, sin tocar el flujo descripto que ya está alineado:

1. **Pantalla:** un mensaje nuevo del EB, por ejemplo
   `tomarSeleccionDocumentacion(nrosDocumentacion: int[])`. Se pide después de elegir la
   opción 2, 3 o 4 y antes de confirmar. Endpoint POST con el mismo nombre.
2. **DTO:** agregar el número de documentación a `DatosDocumentacion`, para que el front pueda
   marcar cuáles.
3. **Gestor:**
   - guarda la opción y los números marcados;
   - en `actualizarBolsin()` le pasa al bolsín qué documentación va por "la otra"
     transición.
4. **Entidades (Experto):** cada `DetalleRemito` decide qué mensaje mandarle a su
   `Documentacion` según si está marcada. El `Remito` decide si queda parcial.
   - La lógica de transición **ya está en el State**: no hace falta ningún `if` sobre estados.
   - Para no meter `if`/`switch` sobre el texto de la opción, se puede modelar cada opción
     como un objeto. Esto es opcional y conviene consultarlo con la tutora antes.
5. **A5 necesita datos:** agregar en `data.sql` un bolsín Enviado con destino Central (CM 1) y
   cambiar la sesión activa a Ana Ruiz (usuario 3), o agregar una forma de elegir el usuario
   para la demo.
6. **Front:** en `OpcionRecepcion.jsx`, si la opción no es la 1, mostrar la lista de
   documentación con checkboxes antes de pasar a confirmar.
7. **Pruebas:** agregar pruebas como las de `EstadoDocumentacionTest` y probar el flujo
   completo en el front. Revisar la base en `/h2-console`.

Como estos mensajes no están en el diagrama, en la defensa se presentan como **extensión de los
flujos alternativos**. El flujo descripto sigue exactamente igual al diagrama.

### 9.2 Resto del plan (`docs/PENDIENTE.txt`)

- **Repositorio del grupo** en GitHub (se sube el 09/11). El `.gitignore` ya deja afuera
  `back/target`, `back/data` y `front/node_modules`.
- Probar el arranque en una computadora limpia siguiendo `COMO_EJECUTAR.txt`.
- **UX:** fotos del moodboard, mapa de empatía y user journey. Diseño de pantallas con patrones
  de GUI (plantilla: clasificación, patrón, motivación, reglas de oro, heurísticas,
  aplicación). Ajustar `front/src/theme.js` al moodboard; hoy es provisorio.
- **PDF de la Entrega 2** con carátula completa (ver `docs/catedra/...Consignas.pdf`).
- **Defensa:** todos tienen que poder explicar el State en el código (sección 5) y el
  recorrido del diagrama (`docs/sesiones/Sesion_01_...`).

---

## 10. Para la IA: cómo trabajar en este proyecto

- Leé primero el diagrama correspondiente en `docs/diagramas/` (PDF) y la sección del CU en
  `docs/catedra/2026_DSI_Bolsines_ERS_V1.2.pdf`.
- **No renombres** métodos que ya coinciden con el diagrama. Si un método nuevo no está en el
  diagrama, decilo explícitamente y justificalo.
- Mantené el estilo del código existente:
  - nombres en español;
  - comentarios cortos;
  - records para los DTO;
  - Lombok `@RequiredArgsConstructor` y `@NoArgsConstructor(access = PROTECTED)` en las
    entidades;
  - sin getters/setters que nadie usa.
- Después de cada cambio:
  1. `.\mvnw.cmd clean install` (compila y corre las pruebas);
  2. levantar el back y el front;
  3. probar el flujo;
  4. mirar la base.
- Actualizá `docs/PENDIENTE.txt` y agregá un archivo nuevo en `docs/sesiones/` con lo que se
  hizo en la sesión.
