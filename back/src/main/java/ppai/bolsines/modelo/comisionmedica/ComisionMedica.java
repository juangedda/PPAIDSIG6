package ppai.bolsines.modelo.comisionmedica;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ComisionMedica {

    @Id
    @EqualsAndHashCode.Include
    private int codigo;
    private String nombre;
    private String direccion;
    private String email;
    private String telefono;

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
