package proyecto1.simulador;

/**
 * Hilo que maneja interrupciones de hardware/eventos externos.
 * Cumple con: "El manejo de interrupciones debe realizarse con Threads independientes".
 */
public class HiloInterrupcion extends Thread {
    
    private SistemaOperativo so;
    private String tipoInterrupcion;
    private PCB procesoEmergencia;
    private boolean completado;
    
    public HiloInterrupcion(SistemaOperativo so, String tipoInterrupcion) {
        this.so = so;
        this.tipoInterrupcion = tipoInterrupcion;
        this.procesoEmergencia = null;
        this.completado = false;
        this.setName("ISR-" + tipoInterrupcion);
    }
    
    public HiloInterrupcion(SistemaOperativo so, String tipoInterrupcion, PCB procesoEmergencia) {
        this(so, tipoInterrupcion);
        this.procesoEmergencia = procesoEmergencia;
    }
    
    @Override
    public void run() {
        try {
            // 1. Notificar al SO que se inicia una interrupción
            so.registrarEvento("⚡ INTERRUPCIÓN DETECTADA: " + tipoInterrupcion);
            
            // 2. El SO cambia a modo kernel (suspende proceso actual)
            so.manejarInterrupcion(this);
            
            // 3. Simular tiempo de procesamiento de la ISR
            Thread.sleep(50);
            
            // 4. Si hay proceso de emergencia, agregarlo
            if (procesoEmergencia != null) {
                so.agregarProcesoEmergencia(procesoEmergencia);
            }
            
            // 5. Marcar como completado
            this.completado = true;
            so.registrarEvento("✓ Interrupción " + tipoInterrupcion + " procesada");
            
            // 6. Notificar al SO que la ISR terminó
            so.finalizarInterrupcion(this);
            
        } catch (InterruptedException e) {
            so.registrarEvento("✗ Interrupción " + tipoInterrupcion + " cancelada");
        }
    }
    
    public String getTipoInterrupcion() { return tipoInterrupcion; }
    public PCB getProcesoEmergencia() { return procesoEmergencia; }
    public boolean isCompletado() { return completado; }
}
