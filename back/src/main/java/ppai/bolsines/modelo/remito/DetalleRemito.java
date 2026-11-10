package ppai.bolsines.modelo.remito;

import java.time.LocalDateTime;

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

    /** Devuelve [asunto, tipo de documento]. */
    public String[] buscarDatosDocumentacion() {
        return new String[] { documentacion.getAsunto(), documentacion.getNombreTipo() };
    }

    public void actualizarDocumentacion(LocalDateTime fhActual, Empleado responsable) {
        documentacion.recibirYAceptar(fhActual, responsable);
    }
}
