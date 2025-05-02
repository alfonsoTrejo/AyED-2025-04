package com.mycompany.algoritmopractica4_2025;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JuegoSwing extends JFrame {
    private JuegoLogica juego;
    private JPanel[] panelesColumnas;
    private JButton[] botonesCeldas;
    private JButton[] botonesFundaciones;
    private JButton btnDeshacer;
    private JButton btnPista;

    private enum OrigenTipo { COLUMNA, CELDA }
    private OrigenTipo tipoSeleccionado = null;
    private int indiceSeleccionado = -1;

    public JuegoSwing() {
        juego = new JuegoLogica();
        setTitle("Baker's Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLayout(new BorderLayout(5,5));

        // Panel de controles con emojis
        JPanel panelSuperior = new JPanel(new BorderLayout(5,5));
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnDeshacer = new JButton("↩️");
        btnPista = new JButton("💡");
        btnDeshacer.setToolTipText("Deshacer");
        btnPista.setToolTipText("Pista");
        btnDeshacer.addActionListener(e -> {
            if (juego.puedeDeshacer()) {
                juego.deshacer();
                refrescarUI();
            }
        });
        btnPista.addActionListener(e -> {
            JuegoLogica.Move pista = juego.sugerirMovimiento();
            String msg = (pista != null) ? pista.toString() : "No hay movimientos posibles";
            JOptionPane.showMessageDialog(this, msg);
        });
        panelControles.add(btnDeshacer);
        panelControles.add(btnPista);

        // Panel de celdas y fundaciones
        JPanel panelCeldasFund = new JPanel(new GridLayout(2,1,5,5));
        JPanel celdasPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        JPanel fundPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        botonesCeldas = new JButton[4];
        botonesFundaciones = new JButton[4];
        for (int i = 0; i < 4; i++) {
            JButton c = new JButton("🃏");
            c.setFont(new Font("SansSerif", Font.PLAIN, 24));
            c.setPreferredSize(new Dimension(60,90));
            final int idx = i;
            c.addActionListener(e -> manejarClicCelda(idx));
            botonesCeldas[i] = c;
            celdasPanel.add(c);

            JButton f = new JButton("🂠");
            f.setFont(new Font("SansSerif", Font.PLAIN, 24));
            f.setPreferredSize(new Dimension(60,90));
            final int fIdx = i;
            f.addActionListener(e -> manejarClicFundacion(fIdx));
            botonesFundaciones[i] = f;
            fundPanel.add(f);
        }
        panelCeldasFund.add(celdasPanel);
        panelCeldasFund.add(fundPanel);

        panelSuperior.add(panelControles, BorderLayout.NORTH);
        panelSuperior.add(panelCeldasFund, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel de columnas
        JPanel tablero = new JPanel(new GridLayout(1, 8, 5, 5));
        panelesColumnas = new JPanel[8];
        for (int i = 0; i < 8; i++) {
            JPanel col = new JPanel();
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panelesColumnas[i] = col;
            tablero.add(col);
        }
        add(tablero, BorderLayout.CENTER);

        refrescarUI();
    }

    private void manejarClicColumna(int col) {
        if (tipoSeleccionado == null) {
            if (juego.getColumnas()[col].getFin() != null) {
                tipoSeleccionado = OrigenTipo.COLUMNA;
                indiceSeleccionado = col;
            }
        } else {
            boolean exito = (tipoSeleccionado == OrigenTipo.COLUMNA)
                ? juego.moverEntreColumnas(indiceSeleccionado, col)
                : juego.moverDesdeCeldaALaColumna(indiceSeleccionado, col);
            if (!exito) mostrarError();
            resetSeleccion();
            refrescarUI();
        }
    }

    private void manejarClicCelda(int idx) {
        if (tipoSeleccionado == null) {
            if (juego.getCeldasLibres()[idx] != null) {
                tipoSeleccionado = OrigenTipo.CELDA;
                indiceSeleccionado = idx;
            }
        } else {
            boolean ok = juego.moverACeldaLibre(indiceSeleccionado);
            if (!ok) mostrarError();
            resetSeleccion();
            refrescarUI();
        }
    }

    private void manejarClicFundacion(int idx) {
        if (tipoSeleccionado == null) return;
        boolean exito;
        if (tipoSeleccionado == OrigenTipo.COLUMNA) {
            exito = juego.moverAFundacionDesdeColumna(indiceSeleccionado, idx);
        } else {
            exito = juego.moverAFundacionDesdeCelda(indiceSeleccionado, idx);
        }
        if (!exito) mostrarError();
        resetSeleccion();
        refrescarUI();
    }

    private void refrescarUI() {
        // Columnas
        for (int i = 0; i < 8; i++) {
            JPanel panel = panelesColumnas[i];
            panel.removeAll();
            NodoDoble<Carta> nodo = juego.getColumnas()[i].getInicio();
            if (nodo != null) {
                do {
                    Carta carta = nodo.getDato();
                    JButton b = new JButton(formatoCarta(carta));
                    b.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    b.setPreferredSize(new Dimension(60,90));
                    b.setBackground(Color.WHITE);
                    b.setOpaque(true);
                    b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    if (carta.getPalo() == Carta.Palo.CORAZONES || carta.getPalo() == Carta.Palo.DIAMANTES) {
                        b.setForeground(Color.RED);
                    }
                    final int col = i;
                    b.addActionListener(e -> manejarClicColumna(col));
                    panel.add(b);
                    nodo = nodo.getSig();
                } while (nodo != juego.getColumnas()[i].getInicio());
            }
            panel.revalidate(); panel.repaint();
        }
        // Celdas
        for (int i = 0; i < 4; i++) {
            Carta c = juego.getCeldasLibres()[i];
            botonesCeldas[i].setText(c != null ? formatoCarta(c) : "🃏");
        }
        // Fundaciones
        for (int i = 0; i < 4; i++) {
            NodoDoble<Carta> top = juego.getFundaciones()[i].getFin();
            botonesFundaciones[i].setText(top != null ? formatoCarta(top.getDato()) : "🂠");
        }
        btnDeshacer.setEnabled(juego.puedeDeshacer());
    }

    private String formatoCarta(Carta c) {
        String v;
        switch (c.getValor()) {
            case 1: v = "A"; break;
            case 11: v = "J"; break;
            case 12: v = "Q"; break;
            case 13: v = "K"; break;
            default: v = String.valueOf(c.getValor());
        }
        String s;
        switch (c.getPalo()) {
            case CORAZONES: s = "♥"; break;
            case DIAMANTES: s = "♦"; break;
            case TREBOLES: s = "♣"; break;
            default: s = "♠"; break;
        }
        return v + s;
    }

    private void mostrarError() {
        JOptionPane.showMessageDialog(this, "Movimiento inválido.");
    }

    private void resetSeleccion() {
        tipoSeleccionado = null;
        indiceSeleccionado = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JuegoSwing().setVisible(true));
    }
}
