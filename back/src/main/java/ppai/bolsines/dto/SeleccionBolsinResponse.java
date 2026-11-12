package ppai.bolsines.dto;

import java.util.Arrays;
import java.util.List;

/**
 * Respuesta de tomarSeleccionBolsin: lo que la pantalla recibió en
 * mostrarNumRemitosyDocumentacionAsociada y mostrarOpcionesRecepcionParaSeleccion.
 */
public record SeleccionBolsinResponse(List<RemitoConDocumentacion> remitos, List<String> opciones) {

    public record RemitoConDocumentacion(int nroRemito, List<DatosDocumentacion> documentacion) {

        /** Agrupa las filas [nroRemito, asunto, tipo] por remito. */
        public static List<RemitoConDocumentacion> agrupar(int[] nrosRemito, String[][] documentacion) {
            return Arrays.stream(nrosRemito)
                    .mapToObj(nro -> new RemitoConDocumentacion(nro, Arrays.stream(documentacion)
                            .filter(fila -> fila[0].equals(String.valueOf(nro)))
                            .map(fila -> new DatosDocumentacion(fila[1], fila[2]))
                            .toList()))
                    .toList();
        }
    }

    public record DatosDocumentacion(String asunto, String tipo) {
    }
}
