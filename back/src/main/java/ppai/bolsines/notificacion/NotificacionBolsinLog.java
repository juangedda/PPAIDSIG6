package ppai.bolsines.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Implementación de demostración: registra la notificación en el log en lugar de enviar el correo. */
@Component
public class NotificacionBolsinLog implements InterfazNotificacionBolsin {

    private static final Logger log = LoggerFactory.getLogger(NotificacionBolsinLog.class);

    @Override
    public void notificarRecepcionBolsin(String emailCMOrigen, int nroPrecinto, String nombreCMDestino) {
        log.info("CU 29 - Correo a {}: el bolsín con precinto {} fue recibido en {}.",
                emailCMOrigen, nroPrecinto, nombreCMDestino);
    }
}
