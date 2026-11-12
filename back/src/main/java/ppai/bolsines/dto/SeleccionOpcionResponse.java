package ppai.bolsines.dto;

import java.util.Arrays;
import java.util.List;

/**
 * Respuesta de tomarSeleccionOpcionRecepcion. En el flujo descripto, el pedido de confirmación.
 * Extensión A3, A4 y A5: si la opción pide marcar documentación, el pedido de la pantalla y la
 * documentación del bolsín agrupada por remito, con su número y el área de la CMC (si tiene).
 */
public record SeleccionOpcionResponse(String mensaje, boolean seleccionarDocumentacion,
        List<RemitoParaMarcar> remitos) {

    public record RemitoParaMarcar(int nroRemito, List<DocumentacionParaMarcar> documentacion) {

        /** Agrupa las filas [nroRemito, nroDocumentacion, asunto, tipo, areaCMCDestino] por remito. */
        public static List<RemitoParaMarcar> agrupar(String[][] documentacion) {
            return Arrays.stream(documentacion)
                    .map(fila -> fila[0])
                    .distinct()
                    .map(nro -> new RemitoParaMarcar(Integer.parseInt(nro), Arrays.stream(documentacion)
                            .filter(fila -> fila[0].equals(nro))
                            .map(fila -> new DocumentacionParaMarcar(Integer.parseInt(fila[1]), fila[2], fila[3],
                                    fila[4]))
                            .toList()))
                    .toList();
        }
    }

    public record DocumentacionParaMarcar(int nroDocumentacion, String asunto, String tipo, String areaCMCDestino) {
    }
}
