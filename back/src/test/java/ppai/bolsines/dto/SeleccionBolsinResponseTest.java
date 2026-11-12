package ppai.bolsines.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ppai.bolsines.dto.SeleccionBolsinResponse.DatosDocumentacion;
import ppai.bolsines.dto.SeleccionBolsinResponse.RemitoConDocumentacion;
import ppai.bolsines.dto.SeleccionOpcionResponse.DocumentacionParaMarcar;
import ppai.bolsines.dto.SeleccionOpcionResponse.RemitoParaMarcar;

/** La pantalla agrupa por remito las filas que le manda el gestor. */
class SeleccionBolsinResponseTest {

    /** Flujo descripto: filas [nroRemito, asunto, tipo] de mostrarNumRemitosyDocumentacionAsociada. */
    @Test
    void agrupaLaDocumentacionPorRemito() {
        String[][] filas = {
                { "1001", "Expediente 1", "Expediente" },
                { "1001", "Dictamen", "Dictamen" },
                { "1002", "Historia clínica", "Historia clínica" } };

        assertEquals(List.of(
                new RemitoConDocumentacion(1001, List.of(new DatosDocumentacion("Expediente 1", "Expediente"),
                        new DatosDocumentacion("Dictamen", "Dictamen"))),
                new RemitoConDocumentacion(1002, List.of(new DatosDocumentacion("Historia clínica", "Historia clínica")))),
                RemitoConDocumentacion.agrupar(new int[] { 1001, 1002 }, filas));
    }

    /** Extensión A3, A4 y A5: filas [nroRemito, nroDocumentacion, asunto, tipo, areaCMCDestino]. */
    @Test
    void agrupaLaDocumentacionParaMarcarConSuNumeroYSuArea() {
        String[][] filas = {
                { "1001", "1", "Expediente 1", "Expediente", null },
                { "1001", "2", "Expediente 1", "Expediente", "Mesa de Entradas" }, // mismo asunto: sólo el número las distingue
                { "1002", "3", "Historia clínica", "Historia clínica", null } };

        assertEquals(List.of(
                new RemitoParaMarcar(1001, List.of(new DocumentacionParaMarcar(1, "Expediente 1", "Expediente", null),
                        new DocumentacionParaMarcar(2, "Expediente 1", "Expediente", "Mesa de Entradas"))),
                new RemitoParaMarcar(1002,
                        List.of(new DocumentacionParaMarcar(3, "Historia clínica", "Historia clínica", null)))),
                RemitoParaMarcar.agrupar(filas));
    }
}
