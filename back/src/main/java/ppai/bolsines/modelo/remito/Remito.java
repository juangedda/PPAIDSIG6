package ppai.bolsines.modelo.remito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import ppai.bolsines.modelo.usuario.Empleado;

/**
 * Remito del diagrama de clases y del diagrama de secuencia.
 * Conoce sus detalles, su comisión médica de origen y su estado.
 * Por Experto (GRASP), actualiza la documentación que contiene.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Remito {

    @Id
    private int numero;
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoRemito estado;

    @ManyToOne
    @JoinColumn(name = "origen_codigo")
    private ComisionMedica origen;

    @ManyToOne
    @JoinColumn(name = "destino_codigo")
    private ComisionMedica destino;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "remito_numero")
    private List<DetalleRemito> detalleRemito = new ArrayList<>();

    // ---------------------------------------------------------------- flujo descripto (diagrama)

    /** En la secuencia: Bolsin -> :Remito *getNumero(): int. */
    public int getNumero() {
        return numero;
    }

    /**
     * En la secuencia: Bolsin -> :Remito buscarDatosDocumentacion(): String[][] y el loop "Documentacion Remito"
     * con Remito -> :DetalleRemito buscarDatosDocumentacion(): String[]. Una fila [nroRemito, asunto, tipo]
     * por documentación.
     */
    public String[][] buscarDatosDocumentacion() {
        List<String[]> filas = new ArrayList<>();
        for (DetalleRemito detalle : detalleRemito) {
            String[] datos = detalle.buscarDatosDocumentacion();
            filas.add(new String[] { String.valueOf(numero), datos[0], datos[1] });
        }
        return filas.toArray(String[][]::new);
    }

    /**
     * En la secuencia: Bolsin -> :Remito actualizarEstado(estadoRemito: EstadoRemito, fhActual: DateTime,
     * responsable: Empleado); luego setEstado(e) y actualizarDocumentacion(fhActual, responsable).
     */
    public void actualizarEstado(EstadoRemito estadoRemito, LocalDateTime fhActual, Empleado responsable) {
        setEstado(estadoRemito);
        actualizarDocumentacion(fhActual, responsable);
    }

    /** En la secuencia: Remito mensaje a sí mismo setEstado(e: EstadoRemito). */
    public void setEstado(EstadoRemito e) {
        this.estado = e;
    }

    /**
     * En la secuencia: mensaje a sí mismo actualizarDocumentacion(fhActual, responsable) y el loop
     * "ActualizarEstadosDocumentacion" con Remito -> :DetalleRemito actualizarDocumentacion(fhActual, responsable).
     */
    private void actualizarDocumentacion(LocalDateTime fhActual, Empleado responsable) {
        for (DetalleRemito detalle : detalleRemito) {
            detalle.actualizarDocumentacion(fhActual, responsable);
        }
    }

    // ------------------------------------------------------ extensión CU 29 (no está en los diagramas)

    /** La CM que generó el remito (asociación origen del diagrama de clases). */
    public ComisionMedica getOrigen() {
        return origen;
    }

    /** Una fila [nroRemito, nroDocumentacion, asunto, estado] por documentación. */
    public String[][] buscarSituacionDocumentacion() {
        List<String[]> filas = new ArrayList<>();
        for (DetalleRemito detalle : detalleRemito) {
            String[] datos = detalle.buscarSituacionDocumentacion();
            filas.add(new String[] { String.valueOf(numero), datos[0], datos[1], datos[2] });
        }
        return filas.toArray(String[][]::new);
    }

    // ============================================================================================
    // Flujos alternativos A3, A4 y A5 (extensión: no están en el diagrama de secuencia).
    // opcionRecepcion es el número de la opción del paso 7 del CU (2, 3 o 4).
    // ============================================================================================

    /** Filas [nroRemito, nroDocumentacion, asunto, tipo, areaCMCDestino] para que el EB marque documentación. */
    public String[][] buscarDocumentacionParaMarcar() {
        List<String[]> filas = new ArrayList<>();
        for (DetalleRemito detalle : detalleRemito) {
            String[] datos = detalle.buscarDocumentacionParaMarcar();
            filas.add(new String[] { String.valueOf(numero), datos[0], datos[1], datos[2], datos[3] });
        }
        return filas.toArray(String[][]::new);
    }

    /** ¿Contiene esa documentación y admite la opción elegida? */
    public boolean admiteSeleccion(int nroDocumentacion, int opcionRecepcion) {
        return detalleRemito.stream().anyMatch(detalle -> detalle.admiteSeleccion(nroDocumentacion, opcionRecepcion));
    }

    /**
     * Como actualizarEstado del diagrama, con la opción y la documentación marcada: si algún detalle lo deja
     * parcial (A3 y A4), queda RecibidoYAceptadoParcial; si no, RecibidoYAceptado.
     */
    public void actualizarEstado(EstadoRemito estadoRemito, EstadoRemito estadoRemitoParcial, LocalDateTime fhActual,
            Empleado responsable, int opcionRecepcion, int[] nrosMarcados) {
        setEstado(detalleRemito.stream().anyMatch(detalle -> detalle.dejaRemitoParcial(opcionRecepcion, nrosMarcados))
                ? estadoRemitoParcial : estadoRemito);
        for (DetalleRemito detalle : detalleRemito) {
            detalle.actualizarDocumentacion(fhActual, responsable, opcionRecepcion, nrosMarcados);
        }
    }
}
