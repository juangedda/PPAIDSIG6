package ppai.bolsines.notificacion;

/**
 * CU 29 Notificar recepción de bolsín (incluido en el CU 28). Es el componente que pide el RNF 5:
 * el gestor depende de esta interfaz y la implementación se puede cambiar (log, Gmail, Outlook).
 */
public interface InterfazNotificacionBolsin {

    void notificarRecepcionBolsin(String emailCMOrigen, int nroPrecinto, String nombreCMDestino);
}
