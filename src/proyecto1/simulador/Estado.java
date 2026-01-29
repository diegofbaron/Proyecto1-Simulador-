/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

public enum Estado {
    NUEVO,
    LISTO,
    EJECUCION,
    BLOQUEADO,
    TERMINADO,
    LISTO_SUSPENDIDO,      // En Memoria Secundaria (Disco / Swap)
    BLOQUEADO_SUSPENDIDO   // En Memoria Secundaria (Disco / Swap)
}