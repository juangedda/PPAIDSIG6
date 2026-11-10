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

    /** true si el estado actual del bolsín es Enviado. */
    public boolean buscarBolsinesEnviados() {
        for (CambioEstadoBolsin ce : cambioEstado) {
            if (ce.sosActual()) {
                return ce.sosEnviado();
            }
        }
        return false;
    }

    public boolean esCmDestino(ComisionMedica cm) {
        return destino.equals(cm);
    }

    public int getNumPrecinto() {
        return numeroPrecinto;
    }

    public ComisionMedica getOrigen() {
        return origen;
    }

    /** Números de los remitos del bolsín: el bolsín le pide getNumero() a cada remito. */
    public int[] buscarRemitos() {
        return remito.stream().mapToInt(Remito::getNumero).toArray();
    }

    /** Filas [nroRemito, asunto, tipo de documento] de todos los remitos del bolsín. */
    public String[][] buscarDocumentacion() {
        return remito.stream()
                .flatMap(r -> Arrays.stream(r.buscarDatosDocumentacion()))
                .toArray(String[][]::new);
    }

    /**
     * Primero actualiza remitos y documentación; recién después cierra el cambio de estado
     * actual del bolsín (setFechaHoraFin al CE que sosActual) y crea el nuevo (corrección del tutor).
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

    private void actualizarRemitos(LocalDateTime fhActual, EstadoRemito estadoRemito, Empleado responsable) {
        for (Remito r : remito) {
            r.actualizarEstado(estadoRemito, fhActual, responsable);
        }
    }

    private CambioEstadoBolsin crearCambioEstado(LocalDateTime fhInicio, EstadoBolsin e, Empleado responsable) {
        return new CambioEstadoBolsin(fhInicio, e, responsable);
    }
}
