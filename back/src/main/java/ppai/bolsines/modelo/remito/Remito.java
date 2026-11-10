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

    public int getNumero() {
        return numero;
    }

    /** Una fila [nroRemito, asunto, tipo de documento] por cada documentación del remito. */
    public String[][] buscarDatosDocumentacion() {
        List<String[]> filas = new ArrayList<>();
        for (DetalleRemito detalle : detalleRemito) {
            String[] datos = detalle.buscarDatosDocumentacion();
            filas.add(new String[] { String.valueOf(numero), datos[0], datos[1] });
        }
        return filas.toArray(String[][]::new);
    }

    public void actualizarEstado(EstadoRemito estadoRemito, LocalDateTime fhActual, Empleado responsable) {
        setEstado(estadoRemito);
        actualizarDocumentacion(fhActual, responsable);
    }

    public void setEstado(EstadoRemito e) {
        this.estado = e;
    }

    private void actualizarDocumentacion(LocalDateTime fhActual, Empleado responsable) {
        for (DetalleRemito detalle : detalleRemito) {
            detalle.actualizarDocumentacion(fhActual, responsable);
        }
    }
}
