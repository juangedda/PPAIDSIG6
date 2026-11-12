package ppai.bolsines.gestor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ppai.bolsines.modelo.bolsin.Bolsin;
import ppai.bolsines.modelo.bolsin.EstadoBolsin;
import ppai.bolsines.modelo.comisionmedica.ComisionMedica;
import ppai.bolsines.modelo.remito.EstadoRemito;
import ppai.bolsines.modelo.usuario.Empleado;
import ppai.bolsines.modelo.usuario.Usuario;
import ppai.bolsines.notificacion.InterfazNotificacionBolsin;
import ppai.bolsines.notificacion.NotificacionRecepcionBolsin;
import ppai.bolsines.pantalla.PantallaRegistroRecepcionBolsines;
import ppai.bolsines.repositorio.BolsinRepositorio;
import ppai.bolsines.repositorio.EmpleadoRepositorio;
import ppai.bolsines.repositorio.EstadoBolsinRepositorio;
import ppai.bolsines.repositorio.EstadoRemitoRepositorio;
import ppai.bolsines.repositorio.SesionRepositorio;

/**
 * Gestor del CU 28 («control» en el diagrama de clases). Coordina los pasos de la recepción y recuerda
 * lo que selecciona el encargado. Las reglas sobre bolsines, remitos y documentación las resuelven las
 * entidades (Experto). En la secuencia es el objeto :GestorRegistroRecepcionBolsines.
 *
 * El flujo descripto sigue el diagrama de secuencia mensaje por mensaje. Los flujos alternativos A3, A4
 * y A5 (opciones 2, 3 y 4) son una extensión que no está en el diagrama: están al final del archivo y
 * no usan clases nuevas, sólo las del diagrama de clases.
 */
@Service
@RequiredArgsConstructor
public class GestorRegistroRecepcionBolsines {

    /** Opciones del paso 7 del CU (ERS v1.2). La 1 es la del flujo descripto; la 2, 3 y 4 son A3, A4 y A5. */
    private static final String[] OPCIONES_RECEPCION = {
            "El contenido del bolsín es igual al registrado",
            "No se recibe toda la documentación asociada a los remitos",
            "Existe documentación que no corresponde al destino",
            "La documentación se debe redirigir a otra área" };

    // La pantalla conoce al gestor y el gestor a la pantalla: @Lazy corta el ciclo al crear los beans.
    @Lazy
    private final PantallaRegistroRecepcionBolsines pantalla;
    private final Clock reloj;
    private final SesionRepositorio sesionRepositorio;
    private final EmpleadoRepositorio empleadoRepositorio;
    private final BolsinRepositorio bolsinRepositorio;
    private final EstadoBolsinRepositorio estadoBolsinRepositorio;
    private final EstadoRemitoRepositorio estadoRemitoRepositorio;
    private final InterfazNotificacionBolsin interfazNotificacion;

    // Atributos del gestor (diagrama de clases). nroPrecintoCM, origenCM, nroRemito y los datos de la
    // documentación se guardan como arreglos porque son varios.
    private Empleado encargado;
    private ComisionMedica cmUsuario;
    private List<Bolsin> bolsines;
    private int[] nrosPrecinto;
    private String[] origenes;
    private Bolsin bolsinSeleccionado;
    private int[] nrosRemito;
    private String[][] documentacion;
    private String opcRecepcion;
    private LocalDateTime fechaHoraActual;
    private EstadoBolsin estadoRecibidoEnCMDestino;
    private EstadoRemito estadoRecibidoYAceptado;
    // Lo que llamarCasoUso29() prepara para el «include»: una notificación por cada CM de origen.
    private List<NotificacionRecepcionBolsin> notificacionesCU29;
    // Extensión A3, A4 y A5: números de la documentación que marcó el EB (null en el flujo descripto).
    private int[] nrosDocumentacionMarcada;

    // ======================================================== flujo descripto (diagrama de secuencia)

    /**
     * En la secuencia: Pantalla -> Gestor opcRegistrarRecepcionBolsin(). Pasos 1 a 4 del CU:
     * buscarUsuarioLogueado(), buscarCMUsuario(), mostrarCM(nombre), buscarBolsinesEnviados() y
     * mostrarBolsinesEnviadosParaSeleccion(...).
     */
    @Transactional(readOnly = true)
    public void opcRegistrarRecepcionBolsin() {
        finCU(); // descarta un registro anterior que el EB canceló sin terminar (Obs. 1 del CU)
        buscarUsuarioLogueado();
        buscarCMUsuario();
        pantalla.mostrarCM(cmUsuario.getNombre());
        buscarBolsinesEnviados();
        pantalla.mostrarBolsinesEnviadosParaSeleccion(nrosPrecinto, origenes);
    }

    /**
     * En la secuencia: Pantalla -> Gestor tomarSeleccionBolsin(nroPrecinto: int). Pasos 5 a 7: buscarRemitos(),
     * buscarDocumentacion(), mostrarNumRemitosyDocumentacionAsociada(...), getOpcionesRecepcion() y
     * mostrarOpcionesRecepcionParaSeleccion(...). El bolsín elegido es el "selec:Bolsin" de la secuencia.
     */
    public void tomarSeleccionBolsin(int nroPrecinto) {
        if (bolsines == null) {
            throw new IllegalStateException("Primero hay que elegir la opción de registrar la recepción de un bolsín");
        }
        bolsinSeleccionado = bolsines.stream()
                .filter(b -> b.getNumPrecinto() == nroPrecinto)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró un bolsín enviado con el número de precinto " + nroPrecinto));
        olvidarOpcion(); // la opción y lo marcado correspondían al bolsín anterior
        buscarRemitos();
        buscarDocumentacion();
        pantalla.mostrarNumRemitosyDocumentacionAsociada(nrosRemito, documentacion);
        pantalla.mostrarOpcionesRecepcionParaSeleccion(getOpcionesRecepcion());
    }

    /**
     * En la secuencia: Pantalla -> Gestor tomarSeleccionOpcionRecepcion(opcion: String) y
     * Gestor -> Pantalla solicitarConfirmacionRegistro(). Pasos 8 y 9.
     * Extensión: con las opciones 2, 3 y 4 primero se pide marcar documentación (ver el final del archivo).
     */
    public void tomarSeleccionOpcionRecepcion(String opcion) {
        if (bolsinSeleccionado == null) {
            throw new IllegalStateException("Primero hay que seleccionar un bolsín");
        }
        if (!List.of(getOpcionesRecepcion()).contains(opcion)) {
            throw new IllegalArgumentException("Opción de recepción desconocida: " + opcion);
        }
        olvidarOpcion();
        if (!opcion.equals(OPCIONES_RECEPCION[0])) { // opciones 2, 3 y 4: flujos alternativos A3, A4 y A5
            solicitarSeleccionDocumentacion(opcion);
            return;
        }
        opcRecepcion = opcion;
        pantalla.solicitarConfirmacionRegistro();
    }

    /**
     * En la secuencia: Pantalla -> Gestor tomarConfirmacionRegistro(confirmacion: boolean). Pasos 10 a 12:
     * actualizarBolsin(), llamarCasoUso29(), mostrarEjecucionExitosa(), el «include» al CU 29 y finCU(), en
     * ese orden, como en la secuencia. Con confirmacion = false es el flujo A6 (mostrarRegistroCancelado).
     * Todo corre en una transacción: si la notificación o la base fallan, se revierte todo y el front recibe
     * el error (la respuesta con el éxito sale recién cuando termina este método).
     */
    @Transactional
    public void tomarConfirmacionRegistro(boolean confirmacion) {
        if (opcRecepcion == null) {
            throw new IllegalStateException("Primero hay que elegir una opción de recepción");
        }
        if (!opcRecepcion.equals(OPCIONES_RECEPCION[0]) && nrosDocumentacionMarcada == null) {
            throw new IllegalStateException("Primero hay que seleccionar la documentación");
        }
        if (!confirmacion) { // A6: el EB no confirma
            pantalla.mostrarRegistroCancelado();
            finCU();
            return;
        }
        try {
            actualizarBolsin();
            llamarCasoUso29();
            pantalla.mostrarEjecucionExitosa();
            // «include» 29. Notificar recepción de Bolsín: una notificación por cada CM de origen.
            notificacionesCU29.forEach(interfazNotificacion::notificarRecepcionBolsin);
        } catch (RuntimeException e) {
            finCU(); // la transacción se revierte; se descartan los objetos que quedaron a medio actualizar
            throw e;
        }
        finCU();
    }

    /**
     * En la secuencia: buscarUsuarioLogueado(). Gestor -> Activa:Sesion getUsuario(): Usuario (la Sesion a su
     * vez le envía getUsuario() a :Usuario) y Gestor -> :Empleado *esTuUsuario(u): el "*" es este for.
     * Obtener la sesión activa (la que no tiene fechaHoraFin) con el repositorio es diseño de persistencia.
     */
    private void buscarUsuarioLogueado() {
        Usuario usuario = sesionRepositorio.findFirstByFechaHoraFinIsNull()
                .orElseThrow(() -> new IllegalStateException("No hay una sesión activa"))
                .getUsuario();
        encargado = null;
        for (Empleado empleado : empleadoRepositorio.findAll()) {
            if (empleado.esTuUsuario(usuario)) {
                encargado = empleado;
            }
        }
        if (encargado == null) {
            throw new IllegalStateException("El usuario logueado no corresponde a ningún empleado");
        }
    }

    /**
     * En la secuencia: buscarCMUsuario() y Gestor -> Logueado:Empleado obtenerCM(): ComisionMedica. El
     * getNombre() lo envía el gestor (en opcRegistrarRecepcionBolsin), porque necesita la CM y su nombre.
     */
    private void buscarCMUsuario() {
        cmUsuario = encargado.obtenerCM();
    }

    /**
     * En la secuencia: buscarBolsinesEnviados() y el loop "EstadosBolsin [mientras haya bolsines]":
     * Gestor -> :Bolsin buscarBolsinesEnviados(): boolean, esCmDestino(cm), getNumPrecinto() y getOrigen().
     */
    private void buscarBolsinesEnviados() {
        bolsines = new ArrayList<>();
        for (Bolsin bolsin : bolsinRepositorio.findAll()) {
            if (bolsin.buscarBolsinesEnviados() && bolsin.esCmDestino(cmUsuario)) {
                bolsines.add(bolsin);
            }
        }
        nrosPrecinto = bolsines.stream().mapToInt(Bolsin::getNumPrecinto).toArray();
        origenes = bolsines.stream().map(Bolsin::getOrigen).toArray(String[]::new);
    }

    /** En la secuencia: buscarRemitos() y Gestor -> selec:Bolsin buscarRemitos(). */
    private void buscarRemitos() {
        nrosRemito = bolsinSeleccionado.buscarRemitos();
    }

    /** En la secuencia: buscarDocumentacion() y Gestor -> selec:Bolsin buscarDocumentacion(): String[][]. */
    private void buscarDocumentacion() {
        documentacion = bolsinSeleccionado.buscarDocumentacion();
    }

    /** En la secuencia: getOpcionesRecepcion(). Las 4 opciones del paso 7 del CU. */
    private String[] getOpcionesRecepcion() {
        return OPCIONES_RECEPCION.clone();
    }

    /** En la secuencia: getFechaHoraActual(): datetime. La hora sale del bean Clock. */
    private LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now(reloj);
    }

    /**
     * En la secuencia: actualizarBolsin(). Loop de estados del bolsín con *esRecibidoEnCMDestino(), loop de
     * estados de remito con *esRecibidoyAceptado(), getFechaHoraActual() y Gestor -> selec:Bolsin
     * actualizarEstado(fhActual, estadoBolsin, estadoRemito, responsable).
     * Extensión: si el EB marcó documentación (A3, A4, A5), se usa actualizarBolsinConDocumentacionMarcada.
     */
    private void actualizarBolsin() {
        for (EstadoBolsin estado : estadoBolsinRepositorio.findAll()) {
            if (estado.esRecibidoEnCMDestino()) {
                estadoRecibidoEnCMDestino = estado;
            }
        }
        for (EstadoRemito estado : estadoRemitoRepositorio.findAll()) {
            if (estado.esRecibidoyAceptado()) {
                estadoRecibidoYAceptado = estado;
            }
        }
        fechaHoraActual = getFechaHoraActual();
        if (nrosDocumentacionMarcada == null) {
            bolsinSeleccionado.actualizarEstado(fechaHoraActual, estadoRecibidoEnCMDestino, estadoRecibidoYAceptado,
                    encargado);
        } else {
            actualizarBolsinConDocumentacionMarcada();
        }
        // Persistencia (no es un mensaje del diagrama). saveAndFlush escribe todo ahora, antes de
        // llamarCasoUso29(): un error de base revierte la transacción sin haber notificado.
        bolsinRepositorio.saveAndFlush(bolsinSeleccionado);
    }

    /**
     * En la secuencia: llamarCasoUso29(). Prepara lo que se le envía al CU 29 en el «include»: una
     * notificación por cada CM de origen de los remitos, cada una sólo con la situación de su documentación
     * (CU 28 paso 12). El gestor depende de la interfaz del CU 29, no de cómo se envía.
     */
    private void llamarCasoUso29() {
        notificacionesCU29 = new ArrayList<>();
        for (ComisionMedica cmOrigen : bolsinSeleccionado.buscarOrigenesDocumentacion()) {
            notificacionesCU29.add(NotificacionRecepcionBolsin.de(cmOrigen.getEmail(), cmOrigen.getNombre(),
                    bolsinSeleccionado.getNumPrecinto(), cmUsuario.getNombre(),
                    bolsinSeleccionado.buscarSituacionDocumentacion(cmOrigen)));
        }
    }

    /** En la secuencia: FinCU() (finCU() en el diagrama de clases). Olvida todo lo que eligió el EB. */
    private void finCU() {
        bolsines = null;
        nrosPrecinto = null;
        origenes = null;
        bolsinSeleccionado = null;
        nrosRemito = null;
        documentacion = null;
        fechaHoraActual = null;
        notificacionesCU29 = null;
        olvidarOpcion();
    }

    private void olvidarOpcion() {
        opcRecepcion = null;
        nrosDocumentacionMarcada = null;
    }

    // ============================================================================================
    // Flujos alternativos A3, A4 y A5 (extensión: no están en el diagrama de secuencia).
    // Usan sólo las clases del diagrama de clases. El EB elige una de las 4 opciones del paso 7: el
    // switch es la alternativa ("alt") del caso de uso. De las transiciones se sigue encargando el State.
    // ============================================================================================

    /** Número de la opción en el paso 7 del CU: 1 a 4. */
    private int getNumeroOpcion(String opcion) {
        return List.of(OPCIONES_RECEPCION).indexOf(opcion) + 1;
    }

    /**
     * Opciones 2, 3 y 4: se le pide al EB que marque la documentación. A5 sólo corresponde si la CM destino
     * es la Central (regla de negocio "Recepción de un Bolsín en una CM Destino").
     */
    private void solicitarSeleccionDocumentacion(String opcion) {
        String mensaje = switch (getNumeroOpcion(opcion)) {
            case 2 -> "Seleccione la documentación que no se recibió.";
            case 3 -> "Seleccione la documentación que no corresponde a esta comisión médica.";
            case 4 -> {
                if (!bolsinSeleccionado.esDestinoCentral()) {
                    throw new IllegalStateException("La opción \"" + opcion + "\" sólo corresponde cuando "
                            + "el bolsín llega a la Comisión Médica Central. Este bolsín llega a "
                            + cmUsuario.getNombre() + ".");
                }
                yield "Seleccione la documentación que se debe redirigir a otra área de la Comisión Médica Central.";
            }
            default -> throw new IllegalArgumentException("Opción de recepción desconocida: " + opcion);
        };
        opcRecepcion = opcion;
        pantalla.solicitarSeleccionDocumentacion(mensaje, bolsinSeleccionado.buscarDocumentacionParaMarcar());
    }

    /**
     * EB -> Pantalla -> Gestor tomarSeleccionDocumentacion(nrosDocumentacion). Cada número tiene que ser de
     * documentación del bolsín que admita la opción elegida (se le pregunta al bolsín); si alguno no, no se
     * guarda nada. Después se pide la confirmación, como en el flujo descripto.
     */
    public void tomarSeleccionDocumentacion(int[] nrosDocumentacion) {
        if (opcRecepcion == null || opcRecepcion.equals(OPCIONES_RECEPCION[0])) {
            throw new IllegalStateException("La opción de recepción elegida no pide seleccionar documentación");
        }
        if (nrosDocumentacion == null || nrosDocumentacion.length == 0) {
            throw new IllegalArgumentException("Seleccione al menos una documentación");
        }
        for (int nro : nrosDocumentacion) {
            if (!bolsinSeleccionado.admiteSeleccion(nro, getNumeroOpcion(opcRecepcion))) {
                throw new IllegalArgumentException(getNumeroOpcion(opcRecepcion) == 4
                        ? "La documentación N.º " + nro + " no se puede redirigir: no tiene registrada un área de la "
                                + "Comisión Médica Central. Sólo se puede marcar la documentación que muestra su área."
                        : "La documentación N.º " + nro + " no es de este bolsín");
            }
        }
        nrosDocumentacionMarcada = nrosDocumentacion.clone();
        pantalla.solicitarConfirmacionRegistro();
    }

    /** Como el actualizarEstado del diagrama, con la opción, lo marcado y el estado parcial del remito. */
    private void actualizarBolsinConDocumentacionMarcada() {
        EstadoRemito estadoRecibidoYAceptadoParcial = null;
        for (EstadoRemito estado : estadoRemitoRepositorio.findAll()) {
            if (estado.esRecibidoYAceptadoParcial()) {
                estadoRecibidoYAceptadoParcial = estado;
            }
        }
        bolsinSeleccionado.actualizarEstado(fechaHoraActual, estadoRecibidoEnCMDestino, estadoRecibidoYAceptado,
                estadoRecibidoYAceptadoParcial, encargado, getNumeroOpcion(opcRecepcion), nrosDocumentacionMarcada);
    }
}
