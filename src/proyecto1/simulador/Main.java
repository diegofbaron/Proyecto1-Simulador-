/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

public class Main {
    public static void main(String[] args) {
        VentanaSimulacion ventana = new VentanaSimulacion();
        SistemaOperativo so = new SistemaOperativo(ventana);

        // Llamamos al método setAcciones con las dos funciones lambda
        ventana.setAcciones(
            e -> {
                so.iniciarSimulacion();
            }, 
            e -> {
                so.interrupcionEmergencia();
            }
        );

        ventana.setVisible(true);
    }
}