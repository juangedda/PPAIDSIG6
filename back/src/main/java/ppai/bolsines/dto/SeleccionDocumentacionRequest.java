package ppai.bolsines.dto;

import jakarta.validation.constraints.NotEmpty;

/** Extensión A3, A4 y A5: números de la documentación que marcó el EB. */
public record SeleccionDocumentacionRequest(@NotEmpty int[] nrosDocumentacion) {
}
