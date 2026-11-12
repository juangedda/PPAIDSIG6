package ppai.bolsines.modelo.bolsin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ppai.bolsines.modelo.comisionmedica.ComisionMedica;
import ppai.bolsines.modelo.remito.EstadoRemito;
import ppai.bolsines.modelo.remito.Remito;
import ppai.bolsines.modelo.usuario.Empleado;

/**
 * Bolsín del diagrama de clases. En la secuencia aparece como :Bolsin cuando se
 * buscan los enviados y como selec:Bolsin cuando el encargado elige uno.
 * Por Experto (GRASP), conoce su origen, destino, remitos e historial y puede
 * coordinar la actualización de los remitos al registrar la recepción.
 * Los métodos del flujo descripto son los del diagrama; los de A3, A4, A5 y CU 29 están aparte.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bolsin {

    @Id
    private int numero;
    private LocalDate fecha;
    private int numeroPrecinto;
    private int peso;

    @ManyToOne
    @JoinColumn(name = "origen_codigo")
    private ComisionMedica origen;

    @ManyToOne
    @JoinColumn(name = "destino_codigo")
    private ComisionMedica destino;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "bolsin_numero")
    private List<Remito> remito = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "bolsin_numero")
    private List<CambioEstadoBolsin> cambioEstado = new ArrayList<>();

    // ---------------------------------------------------------------- flujo descripto (diagrama)

    /**
     * En la secuencia: Gestor -> :Bolsin buscarBolsinesEnviados(): boolean; el bolsín envía *sosActual() a
     * :CambioEstadoBolsin y sosEnviado() al Actual, que se lo pregunta a :EstadoBolsin.
     * Diferencia: en el diagrama de clases recibe (cmUsuario: ComisionMedica); acá, como en la secuencia,
     * no recibe nada y la CM se pregunta aparte con esCmDestino.
     */
    public boolean buscarBolsinesEnviados() {
        for (CambioEstadoBolsin ce : cambioEstado) {
            if (ce.sosActual()) {
                return ce.sosEnviado();
            }
        }
        return false;
    }

    /** En la secuencia: Gestor -> :Bolsin esCmDestino(cm: ComisionMedica): boolean (esCMDestino en el diagrama de clases). */
    public boolean esCmDestino(ComisionMedica cm) {
        return destino.equals(cm);
    }

    /** En la secuencia: Gestor -> :Bolsin getNumPrecinto(): int. */
    public int getNumPrecinto() {
        return numeroPrecinto;
    }

    /**
     * En la secuencia: Gestor -> :Bolsin getOrigen() y Bolsin -> :ComisionMedica getNombre(): String.
     * Devuelve el nombre de la CM de origen, que es lo que muestra la pantalla.
     */
    public String getOrigen() {
        return origen.getNombre();
    }

    /**
     * En la secuencia: Gestor -> selec:Bolsin buscarRemitos() y Bolsin -> :Remito *getNumero(): int.
     * Diferencia: el diagrama de clases dice que devuelve Remito[]; acá devuelve int[] (los números),
     * porque el *getNumero() lo envía el bolsín y así el gestor no se acopla a Remito.
     */
    public int[] buscarRemitos() {
        return remito.stream().mapToInt(Remito::getNumero).toArray();
    }

    /**
     * En la secuencia: Gestor -> selec:Bolsin buscarDocumentacion(): String[][]; loop "Remitos del Bolsin" con
     * Bolsin -> :Remito buscarDatosDocumentacion(). Cada fila es [nroRemito, asunto, tipo].
     */
    public String[][] buscarDocumentacion() {
        return remito.stream()
                .flatMap(r -> Arrays.stream(r.buscarDatosDocumentacion()))
                .toArray(String[][]::new);
    }

    /**
     * En la secuencia: Gestor -> selec:Bolsin actualizarEstado(fhActual: DateTime, estadoBolsin: EstadoBolsin,
     * estadoRemito: EstadoRemito, responsable: Empleado). Orden de la secuencia (corrección del tutor):
     * primero actualizarRemitos(...) (remitos y documentación); recién después setFechaHoraFin al
     * Actual:CambioEstadoBolsin (buscado con sosActual()) y crearCambioEstado(), que crea el
     * Nuevo:CambioEstadoBolsin.
     */
    public void actualizarEstado(LocalDateTime fhActual, EstadoBolsin estadoBolsin, EstadoRemito estadoRemito,
            Empleado responsable) {
        actualizarRemitos(fhActual, estadoRemito, responsable);
        for (CambioEstadoBolsin ce : cambioEstado) {
            if (ce.sosActual()) {
                ce.setFechaHoraFin(fhActual);
            }
        }
        cambioEstado.add(crearCambioEstado(fhActual, estadoBolsin, responsable));
    }

    /**
     * En la secuencia: mensaje a sí mismo actualizarRemitos(fhActual, estadoRemito, responsable) y el loop
     * "Actualizar Estado Remitos" con Bolsin -> :Remito actualizarEstado(estadoRemito, fhActual, responsable).
     */
    private void actualizarRemitos(LocalDateTime fhActual, EstadoRemito estadoRemito, Empleado responsable) {
        for (Remito r : remito) {
            r.actualizarEstado(estadoRemito, fhActual, responsable);
        }
    }

    /**
     * En la secuencia: mensaje a sí mismo crearCambioEstado() y new(fhInicio, e, usuarioResponsable) ->
     * Nuevo:CambioEstadoBolsin. Recibe los datos que necesita el new(...), para no guardarlos como atributos
     * temporales. Por Creador (GRASP), el bolsín contiene su historial, así que crea sus cambios de estado.
     */
    private CambioEstadoBolsin crearCambioEstado(LocalDateTime fhInicio, EstadoBolsin e, Empleado responsable) {
        return new CambioEstadoBolsin(fhInicio, e, responsable);
    }

    // ------------------------------------------------------ extensión CU 29 (no está en los diagramas)

    /** CM de origen de los remitos del bolsín; cada una recibe su propia notificación. */
    public List<ComisionMedica> buscarOrigenesDocumentacion() {
        return remito.stream().map(Remito::getOrigen).distinct().toList();
    }

    /** Filas [nroRemito, nroDocumentacion, asunto, estado] sólo de los remitos que envió esa CM. */
    public String[][] buscarSituacionDocumentacion(ComisionMedica cmOrigen) {
        return remito.stream()
                .filter(r -> r.getOrigen().equals(cmOrigen))
                .flatMap(r -> Arrays.stream(r.buscarSituacionDocumentacion()))
                .toArray(String[][]::new);
    }

    // ============================================================================================
    // Flujos alternativos A3, A4 y A5 (extensión: no están en el diagrama de secuencia).
    // opcionRecepcion es el número de la opción del paso 7 del CU (2, 3 o 4).
    // ============================================================================================

    /** A5: la opción de redirigir sólo corresponde si el destino es la CMC. Se lo pregunta a su destino. */
    public boolean esDestinoCentral() {
        return destino.esCentral();
    }

    /** Filas [nroRemito, nroDocumentacion, asunto, tipo, areaCMCDestino] para que el EB marque documentación. */
    public String[][] buscarDocumentacionParaMarcar() {
        return remito.stream()
                .flatMap(r -> Arrays.stream(r.buscarDocumentacionParaMarcar()))
                .toArray(String[][]::new);
    }

    /** ¿Contiene esa documentación y admite la opción elegida? */
    public boolean admiteSeleccion(int nroDocumentacion, int opcionRecepcion) {
        return remito.stream().anyMatch(r -> r.admiteSeleccion(nroDocumentacion, opcionRecepcion));
    }

    /**
     * Como actualizarEstado del diagrama, con la opción elegida y la documentación que marcó el EB. Los remitos con
     * documentación que no se aceptó quedan con estadoRemitoParcial. El bolsín queda igual que en el
     * flujo descripto.
     */
    public void actualizarEstado(LocalDateTime fhActual, EstadoBolsin estadoBolsin, EstadoRemito estadoRemito,
            EstadoRemito estadoRemitoParcial, Empleado responsable, int opcionRecepcion, int[] nrosMarcados) {
        for (Remito r : remito) {
            r.actualizarEstado(estadoRemito, estadoRemitoParcial, fhActual, responsable, opcionRecepcion, nrosMarcados);
        }
        for (CambioEstadoBolsin ce : cambioEstado) {
            if (ce.sosActual()) {
                ce.setFechaHoraFin(fhActual);
            }
        }
        cambioEstado.add(crearCambioEstado(fhActual, estadoBolsin, responsable));
    }
}
