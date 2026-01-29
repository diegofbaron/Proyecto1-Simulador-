/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import estructuras.*;
import java.util.Vector;

public class SistemaOperativo {
    private VentanaSimulacion ventana;
    private Lista<PCB> colaListos = new Lista<>();
    private Lista<PCB> colaBloqueados = new Lista<>();
    private Lista<PCB> colaSuspendidos = new Lista<>(); // Representa el Disco (Swap)
    private PCB procesoEnEjecucion = null;
    private Reloj reloj;
    private int contadorCiclos = 0;
    private final int MAX_RAM = 5; 

    public SistemaOperativo(VentanaSimulacion ventana) {
        this.ventana = ventana;
        this.reloj = new Reloj(1000);
        this.reloj.setSistemaOperativo(this);
        generarProcesosPrueba();
    }

    public synchronized void ejecutarCiclo() {
        contadorCiclos++;
        manejarSwap(); 
        verificarBloqueados();
        planificarCPU();

        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.ejecutarInstruccion();
            if (procesoEnEjecucion.haTerminado()) {
                procesoEnEjecucion = null;
            } else if (procesoEnEjecucion.debeBloquearse()) {
                procesoEnEjecucion.setEstado(Estado.BLOQUEADO);
                colaBloqueados.insertar(procesoEnEjecucion);
                procesoEnEjecucion = null;
            }
        }
        actualizarInterfaz();
    }

    private void manejarSwap() {
        int enRam = colaListos.getSize() + colaBloqueados.getSize() + (procesoEnEjecucion != null ? 1 : 0);

        // SWAP OUT: Si RAM llena, mover el último de Listos al Disco
        while (enRam > MAX_RAM && !colaListos.esVacia()) {
            PCB p = colaListos.getUltimo();
            p.setEstado(Estado.LISTO_SUSPENDIDO);
            colaListos.eliminar(p);
            colaSuspendidos.insertar(p);
            enRam--;
        }

        // SWAP IN: Si hay espacio, traer del Disco a la RAM
        while (enRam < MAX_RAM && !colaSuspendidos.esVacia()) {
            PCB p = colaSuspendidos.getpFirst().getContenido();
            p.setEstado(Estado.LISTO);
            colaSuspendidos.eliminar(p);
            colaListos.insertar(p);
            enRam++;
        }
    }

    public synchronized void interrupcionEmergencia() {
        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.setEstado(Estado.BLOQUEADO);
            colaBloqueados.insertar(procesoEnEjecucion);
            procesoEnEjecucion = null;
            actualizarInterfaz();
        }
    }

    private void verificarBloqueados() {
        if (colaBloqueados.esVacia()) return;
        Nodo<PCB> aux = colaBloqueados.getpFirst();
        Lista<PCB> listosParaVolver = new Lista<>();
        while (aux != null) {
            PCB p = aux.getContenido();
            p.incrementarContadorES();
            if (p.getContadorES() >= 3) listosParaVolver.insertar(p);
            aux = aux.getSiguiente();
        }
        Nodo<PCB> n = listosParaVolver.getpFirst();
        while (n != null) {
            PCB p = n.getContenido();
            p.resetContadorES();
            colaBloqueados.eliminar(p);
            colaListos.insertar(p);
            n = n.getSiguiente();
        }
    }

    private void planificarCPU() {
        if (procesoEnEjecucion == null && !colaListos.esVacia()) {
            procesoEnEjecucion = colaListos.getpFirst().getContenido();
            colaListos.eliminar(procesoEnEjecucion);
            procesoEnEjecucion.setEstado(Estado.EJECUCION);
        }
    }

    private void actualizarInterfaz() {
        int ramCount = colaListos.getSize() + colaBloqueados.getSize() + (procesoEnEjecucion != null ? 1 : 0);
        ventana.updateView(
            contadorCiclos, 
            procesoEnEjecucion, 
            getNombres(colaListos), 
            getNombres(colaBloqueados), 
            getNombres(colaSuspendidos), 
            ramCount
        );
    }

    private Vector<String> getNombres(Lista<PCB> lista) {
        Vector<String> v = new Vector<>();
        Nodo<PCB> aux = lista.getpFirst();
        while(aux != null) {
            v.add(aux.getContenido().getNombre() + " [ID:" + aux.getContenido().getId() + "]");
            aux = aux.getSiguiente();
        }
        return v;
    }

    private void generarProcesosPrueba() {
        // Nombres en español para ambiente de satélite
        String[] tareas = {
            "Telemetría", "Cámara_RGB", "Antena_L", "Sensor_Temp", 
            "Giroscopio", "Panel_Solar", "Enlace_Tierra", "Analisis_UV"
        };
        
        for(int i=0; i < tareas.length; i++) {
            // Creamos 8 procesos (esto forzará el SWAP automáticamente ya que el límite es 5)
            colaListos.insertar(new PCB(tareas[i], 15 + (i*2), 1, 100 + (i*10), (i%2==0), 5, 3));
        }
    }

    public void iniciarSimulacion() { reloj.start(); reloj.iniciar(); ventana.deshabilitarBotonInicio(); }
}
    