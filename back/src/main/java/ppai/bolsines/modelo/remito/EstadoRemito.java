package ppai.bolsines.modelo.remito;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Estados de remito (sin historial, como en el modelo de dominio). */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EstadoRemito {

    @Id
    private int id;
    private String nombre;
    private String descripcion;

    public boolean esRecibidoyAceptado() {
        return "RecibidoYAceptado".equals(nombre);
    }

    public String getNombre() {
        return nombre;
    }
}
