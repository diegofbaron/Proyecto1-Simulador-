/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Vector;

public class VentanaSimulacion extends JFrame {
    private JLabel lblClock, lblProc, lblDeadline, lblRam;
    private JProgressBar barCPU, barRAM;
    private JList<String> listReady, listBlocked, listSwap;
    private DefaultListModel<String> modReady, modBlocked, modSwap;
    private JButton btnStart, btnEmergency;

    // Colores de alto contraste para estilo Juego/Espacial
    private final Color COLOR_FONDO = new Color(5, 10, 20);
    private final Color COLOR_PANEL = new Color(15, 20, 35);
    private final Color COLOR_CIAN = new Color(0, 255, 240); 
    private final Color COLOR_BLANCO = new Color(255, 255, 255);
    private final Color COLOR_PELIGRO = new Color(255, 60, 60);

    public VentanaSimulacion() {
        setTitle("PROYECTO 1 - SIMULADOR RTOS");
        setSize(1050, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(COLOR_FONDO);

        // --- RELOJ ---
        lblClock = new JLabel("CICLO: 0", SwingConstants.CENTER);
        lblClock.setBounds(740, 20, 260, 70);
        lblClock.setFont(new Font("Monospaced", Font.BOLD, 45));
        lblClock.setForeground(COLOR_CIAN);
        lblClock.setBorder(new LineBorder(COLOR_CIAN, 2));
        add(lblClock);

        // --- MONITOR RAM ---
        lblRam = new JLabel("MEMORIA RAM EN USO: 0/5", SwingConstants.LEFT);
        lblRam.setBounds(40, 25, 300, 20);
        lblRam.setForeground(COLOR_BLANCO);
        lblRam.setFont(new Font("SansSerif", Font.BOLD, 13));
        add(lblRam);

        barRAM = new JProgressBar(0, 5);
        barRAM.setBounds(40, 50, 350, 25);
        barRAM.setBackground(new Color(40, 40, 45));
        barRAM.setForeground(COLOR_CIAN);
        barRAM.setBorder(new LineBorder(COLOR_BLANCO, 1));
        add(barRAM);

        // --- PANEL CPU ---
        JPanel panelCPU = crearPanelContenedor(350, 110, 350, 180, "PROCESADOR CENTRAL");
        lblProc = new JLabel("IDLE", SwingConstants.CENTER);
        lblProc.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblProc.setForeground(COLOR_BLANCO);
        lblProc.setBounds(25, 40, 300, 30);
        
        barCPU = new JProgressBar(0, 100);
        barCPU.setBounds(25, 85, 300, 30);
        barCPU.setStringPainted(true);
        barCPU.setFont(new Font("SansSerif", Font.BOLD, 12));
        barCPU.setForeground(new Color(0, 120, 255));
        
        lblDeadline = new JLabel("LIMITE: -", SwingConstants.CENTER);
        lblDeadline.setForeground(COLOR_PELIGRO);
        lblDeadline.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblDeadline.setBounds(25, 130, 300, 30);
        
        panelCPU.add(lblProc);
        panelCPU.add(barCPU);
        panelCPU.add(lblDeadline);
        add(panelCPU);

        // --- LISTAS CON TÍTULOS VISIBLES ---
        modReady = new DefaultListModel<>();
        listReady = configurarJList(modReady);
        add(crearScrollConTitulo(listReady, 40, 320, 320, 260, "COLA DE LISTOS (RAM)"));

        modBlocked = new DefaultListModel<>();
        listBlocked = configurarJList(modBlocked);
        add(crearScrollConTitulo(listBlocked, 670, 320, 330, 260, "COLA DE BLOQUEADOS (RAM)"));

        modSwap = new DefaultListModel<>();
        listSwap = configurarJList(modSwap);
        add(crearScrollConTitulo(listSwap, 40, 600, 960, 120, "MEMORIA VIRTUAL (SWAP - DISCO)"));

        // --- BOTONES ---
        btnStart = new JButton("INICIAR MISIÓN");
        btnStart.setBounds(340, 735, 180, 40);
        estilizarBoton(btnStart, COLOR_CIAN);
        add(btnStart);

        btnEmergency = new JButton("INTERRUPCIÓN");
        btnEmergency.setBounds(540, 735, 180, 40);
        estilizarBoton(btnEmergency, COLOR_PELIGRO);
        btnEmergency.setEnabled(false);
        add(btnEmergency);
        
        setLocationRelativeTo(null);
    }

    // Método clave para que el título se vea sí o sí
    private JScrollPane crearScrollConTitulo(JList lista, int x, int y, int w, int h, String titulo) {
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBounds(x, y, w, h);
        scroll.setBackground(COLOR_PANEL);
        scroll.getViewport().setBackground(COLOR_PANEL);
        
        // Creamos un borde de línea y le ponemos el título en blanco
        TitledBorder border = BorderFactory.createTitledBorder(
            new LineBorder(COLOR_CIAN, 1), titulo);
        border.setTitleColor(COLOR_BLANCO); // Forzamos el color blanco
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        
        scroll.setBorder(border);
        return scroll;
    }

    private JPanel crearPanelContenedor(int x, int y, int w, int h, String titulo) {
        JPanel p = new JPanel(null);
        p.setBounds(x, y, w, h);
        p.setBackground(COLOR_PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(new LineBorder(COLOR_CIAN, 1), titulo);
        border.setTitleColor(COLOR_BLANCO);
        p.setBorder(border);
        return p;
    }

    private JList<String> configurarJList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setBackground(COLOR_PANEL);
        list.setForeground(COLOR_CIAN);
        list.setFont(new Font("Monospaced", Font.BOLD, 14));
        list.setSelectionBackground(COLOR_CIAN);
        list.setSelectionForeground(Color.BLACK);
        return list;
    }

    private void estilizarBoton(JButton btn, Color colorBorde) {
        btn.setBackground(new Color(20, 20, 30));
        btn.setForeground(COLOR_BLANCO);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new LineBorder(colorBorde, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void updateView(int c, PCB p, Vector<String> r, Vector<String> b, Vector<String> s, int ram) {
        lblClock.setText("CICLO: " + c);
        lblRam.setText("MEMORIA RAM EN USO: " + ram + "/5");
        barRAM.setValue(ram);
        
        if (p != null) {
            lblProc.setText(p.getNombre().toUpperCase());
            int perc = (int)((double)p.getInstruccionesEjecutadas()/p.getInstruccionesTotales()*100);
            barCPU.setValue(perc);
            lblDeadline.setText("LÍMITE: CICLO " + p.getDeadline());
        } else {
            lblProc.setText("ESPERANDO...");
            barCPU.setValue(0);
            lblDeadline.setText("LÍMITE: -");
        }

        modReady.clear(); for(String st : r) modReady.addElement(">> " + st);
        modBlocked.clear(); for(String st : b) modBlocked.addElement("!! " + st);
        modSwap.clear(); for(String st : s) modSwap.addElement("[DISCO] " + st);
    }

    public void setAcciones(java.awt.event.ActionListener start, java.awt.event.ActionListener em) {
        btnStart.addActionListener(start);
        btnEmergency.addActionListener(em);
    }

    public void deshabilitarBotonInicio() {
        btnStart.setEnabled(false);
        btnStart.setText("SIMULANDO...");
        btnEmergency.setEnabled(true);
    }
}