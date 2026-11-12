package ppai.bolsines.modelo.remito;

import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ppai.bolsines.modelo.documentacion.Documentacion;
import ppai.bolsines.modelo.usuario.Empleado;

/**
 * DetalleRemito del diagrama de secuencia (DetallesRemito en el de clases). Conoce la documentación
 * incluida y, cuando corresponde, el área de destino dentro de la CMC.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetalleRemito {

    @Id
    private int id;
    @Column(name = "area_cmc_destino")
    private String areaCMCDestino;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "documentacion_numero")
    private Documentacion documentacion;

    // ---------------------------------------------------------------- flujo descripto (diagrama)

    /**
     * En la secuencia: Remito -> :DetalleRemito buscarDatosDocumentacion(): String[], que envía getAsunto() y
     * getNombreTipo() a :Documentacion. Devuelve [asunto, tipo].
     */
    public String[] buscarDatosDocumentacion() {
        return new String[] { documentacion.getAsunto(), documentacion.getNombreTipo() };
    }

    /**
     * En la secuencia: Remito -> :DetalleRemito actualizarDocumentacion(fhActual, responsable) y
     * DetalleRemito -> :Documentacion recibirYAceptar(fhActual, responsable).
     */
    public void actualizarDocumentacion(LocalDateTime fhActual, Empleado responsable) {
        documentacion.recibirYAceptar(fhActual, responsable);
    }

    // ------------------------------------------------------ extensión CU 29 (no está en el diagrama)

    /** Devuelve [nroDocumentacion, asunto, estado actual]. */
    public String[] buscarSituacionDocumentacion() {
        return new String[] { String.valueOf(documentacion.getNumero()), documentacion.getAsunto(),
                documentacion.getNombreEstado() };
    }

    // ============================================================================================
    // Flujos alternativos A3, A4 y A5 (extensión: no están en el diagrama de secuencia).
    // opcionRecepcion es el número de la opción del paso 7 del CU (2, 3 o 4).
    // ============================================================================================

    /** Datos para que el EB marque la documentación: [nroDocumentacion, asunto, tipo, areaCMCDestino]. */
    public String[] buscarDocumentacionParaMarcar() {
        return new String[] { String.valueOf(documentacion.getNumero()), documentacion.getAsunto(),
                documentacion.getNombreTipo(), areaCMCDestino };
    }

    /** ¿Es la documentación con ese número? En A5 (opción 4) además tiene que tener el área de la CMC. */
    public boolean admiteSeleccion(int nroDocumentacion, int opcionRecepcion) {
        return documentacion.getNumero() == nroDocumentacion && (opcionRecepcion != 4 || areaCMCDestino != null);
    }

    /**
     * Si su documentación no está marcada, se recibe y acepta como en el flujo descripto. Si está marcada,
     * se le envía la transición de la opción elegida; si no es válida en su estado, el State la rechaza.
     */
    public void actualizarDocumentacion(LocalDateTime fhActual, Empleado responsable, int opcionRecepcion,
            int[] nrosMarcados) {
        if (!estaMarcada(nrosMarcados)) {
            actualizarDocumentacion(fhActual, responsable);
            return;
        }
        switch (opcionRecepcion) {
            case 2 -> documentacion.registrarNoRecibida(fhActual, responsable); // A3
            case 3 -> documentacion.recibirYRechazar(fhActual, responsable);    // A4
            case 4 -> documentacion.redirigir(fhActual, responsable);           // A5
            default -> throw new IllegalArgumentException("La opción " + opcionRecepcion + " no marca documentación");
        }
    }

    /** A3 y A4: el remito queda RecibidoYAceptadoParcial si su documentación está marcada. En A5, no. */
    public boolean dejaRemitoParcial(int opcionRecepcion, int[] nrosMarcados) {
        return opcionRecepcion != 4 && estaMarcada(nrosMarcados);
    }

    private boolean estaMarcada(int[] nrosMarcados) {
        return Arrays.stream(nrosMarcados).anyMatch(nro -> nro == documentacion.getNumero());
    }
}
