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
import ppai.bolsines.pantalla.PantallaRegistroRecepcionBolsines;
import ppai.bolsines.repositorio.BolsinRepositorio;
import ppai.bolsines.repositorio.EmpleadoRepositorio;
import ppai.bolsines.repositorio.EstadoBolsinRepositorio;
import ppai.bolsines.repositorio.EstadoRemitoRepositorio;
import ppai.bolsines.repositorio.SesionRepositorio;

/**
 * «control» del CU 28 Registrar recepción de bolsín. Coordina el caso de uso, recuerda lo que el
 * Encargado de Bolsines va seleccionando y le manda a la pantalla los mensajes mostrarX() del
 * diagrama de secuencia.
 */
@Service
@RequiredArgsConstructor
public class GestorRegistroRecepcionBolsines {

    private static final String OPCION_CONTENIDO_IGUAL = "El contenido del bolsín es igual al registrado";
    private static final String[] OPCIONES_RECEPCION = {
            OPCION_CONTENIDO_IGUAL,
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

    // Atributos del gestor en el diagrama de clases.
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

    @Transactional(readOnly = true)
    public void opcRegistrarRecepcionBolsin() {
        finCU(); // descarta un registro anterior que el EB canceló sin terminar (Obs. 1 del CU)
        buscarUsuarioLogueado();
        buscarCMUsuario();
        pantalla.mostrarCM(cmUsuario.getNombre());
        buscarBolsinesEnviados();
        pantalla.mostrarBolsinesEnviadosParaSeleccion(nrosPrecinto, origenes);
    }

    public void tomarSeleccionBolsin(int nroPrecinto) {
        if (bolsines == null) {
            throw new IllegalStateException("Primero hay que elegir la opción de registrar la recepción de un bolsín");
        }
        bolsinSeleccionado = bolsines.stream()
                .filter(b -> b.getNumPrecinto() == nroPrecinto)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró un bolsín enviado con el número de precinto " + nroPrecinto));
        buscarRemitos();
        buscarDocumentacion();
        pantalla.mostrarNumRemitosyDocumentacionAsociada(nrosRemito, documentacion);
        pantalla.mostrarOpcionesRecepcionParaSeleccion(getOpcionesRecepcion());
    }

    public void tomarSeleccionOpcionRecepcion(String opcion) {
        if (bolsinSeleccionado == null) {
            throw new IllegalStateException("Primero hay que seleccionar un bolsín");
        }
        if (!List.of(OPCIONES_RECEPCION).contains(opcion)) {
            throw new IllegalArgumentException("Opción de recepción desconocida: " + opcion);
        }
        if (!OPCION_CONTENIDO_IGUAL.equals(opcion)) {
            throw new IllegalArgumentException("Esa opción de recepción todavía no está implementada");
        }
        opcRecepcion = opcion;
        pantalla.solicitarConfirmacionRegistro();
    }

    @Transactional
    public void tomarConfirmacionRegistro(boolean confirmacion) {
        if (opcRecepcion == null) {
            throw new IllegalStateException("Primero hay que elegir una opción de recepción");
        }
        if (!confirmacion) { // A6: el EB no confirma
            pantalla.mostrarRegistroCancelado();
            finCU();
            return;
        }
        actualizarBolsin();
        llamarCasoUso29();
        pantalla.mostrarEjecucionExitosa();
        finCU();
    }

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

    private void buscarCMUsuario() {
        cmUsuario = encargado.obtenerCM();
    }

    private void buscarBolsinesEnviados() {
        bolsines = new ArrayList<>();
        for (Bolsin bolsin : bolsinRepositorio.findAll()) {
            if (bolsin.buscarBolsinesEnviados() && bolsin.esCmDestino(cmUsuario)) {
                bolsines.add(bolsin);
            }
        }
        nrosPrecinto = bolsines.stream().mapToInt(Bolsin::getNumPrecinto).toArray();
        origenes = bolsines.stream().map(b -> b.getOrigen().getNombre()).toArray(String[]::new);
    }

    private void buscarRemitos() {
        nrosRemito = bolsinSeleccionado.buscarRemitos();
    }

    private void buscarDocumentacion() {
        documentacion = bolsinSeleccionado.buscarDocumentacion();
    }

    private String[] getOpcionesRecepcion() {
        return OPCIONES_RECEPCION.clone();
    }

    private LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now(reloj);
    }

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
        bolsinSeleccionado.actualizarEstado(fechaHoraActual, estadoRecibidoEnCMDestino, estadoRecibidoYAceptado,
                encargado);
        bolsinRepositorio.save(bolsinSeleccionado); // persistencia: no es un mensaje del diagrama
    }

    /** «include» CU 29 Notificar recepción de bolsín a la comisión médica de origen. */
    private void llamarCasoUso29() {
        interfazNotificacion.notificarRecepcionBolsin(bolsinSeleccionado.getOrigen().getEmail(),
                bolsinSeleccionado.getNumPrecinto(), cmUsuario.getNombre());
    }

    private void finCU() {
        bolsines = null;
        nrosPrecinto = null;
        origenes = null;
        bolsinSeleccionado = null;
        nrosRemito = null;
        documentacion = null;
        opcRecepcion = null;
        fechaHoraActual = null;
    }
}
