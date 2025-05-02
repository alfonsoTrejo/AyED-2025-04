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
        setSize(1100, 800);
        setLayout(new BorderLayout(10,10));

        // Panel de controles con emojis
        JPanel panelSuperior = new JPanel(new BorderLayout(5,5));
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnDeshacer = new JButton("↩️");
        btnPista = new JButton("💡");
        btnDeshacer.setToolTipText("Deshacer");
        btnPista.setToolTipText("Mostrar pista");
        btnDeshacer.setPreferredSize(new Dimension(50,50));
        btnPista.setPreferredSize(new Dimension(50,50));
        panelControles.add(btnDeshacer);
        panelControles.add(btnPista);

        // Panel de celdas y fundaciones
        JPanel panelCeldasFund = new JPanel(new GridLayout(2,1,5,5));
        JPanel celdasPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        JPanel fundPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        botonesCeldas = new JButton[4];
        botonesFundaciones = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            botonesCeldas[idx] = crearBotonCarta("🃏", e -> manejarClicCelda(idx));
            celdasPanel.add(botonesCeldas[idx]);
            final int fIdx = i;
            botonesFundaciones[fIdx] = crearBotonCarta("🂠", e -> manejarClicFundacion(fIdx));
            fundPanel.add(botonesFundaciones[fIdx]);
        }
        panelCeldasFund.add(celdasPanel);
        panelCeldasFund.add(fundPanel);
        panelCeldasFund.add(fundPanel);

        panelSuperior.add(panelControles, BorderLayout.NORTH);
        panelSuperior.add(panelCeldasFund, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel de columnas
        JPanel tablero = new JPanel(new GridLayout(1, 8, 5, 5));
        panelesColumnas = new JPanel[8];
        for (int i = 0; i < 8; i++) {
            panelesColumnas[i] = crearPanelColumna(i);
            tablero.add(panelesColumnas[i]);
        }
        add(tablero, BorderLayout.CENTER);

        // Listeners
        btnDeshacer.addActionListener(e -> {
            if (juego.puedeDeshacer()) {
                juego.deshacer();
                refrescarUI();
            }
        });
        btnPista.addActionListener(e -> {
            JuegoLogica.Move pista = juego.sugerirMovimiento();
            if (pista != null) mostrarPista(pista);
            else JOptionPane.showMessageDialog(this, "No hay movimientos posibles");
        });

        refrescarUI();
    }

    private JButton crearBotonCarta(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btn.setPreferredSize(new Dimension(40,60));
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel crearPanelColumna(int idx) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        col.setPreferredSize(new Dimension(90, 600));
        // Permitir clic en área vacía de la columna
        col.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                manejarClicColumna(idx);
            }
        });
        return col;
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
        boolean exito = (tipoSeleccionado == OrigenTipo.COLUMNA)
            ? juego.moverAFundacionDesdeColumna(indiceSeleccionado, idx)
            : juego.moverAFundacionDesdeCelda(indiceSeleccionado, idx);
        if (!exito) mostrarError();
        resetSeleccion();
        refrescarUI();
    }

    private void mostrarPista(JuegoLogica.Move pista) {
        Component origenComp = null, destComp = null;
        switch (pista.type) {
            case COL_COL:
                origenComp = panelesColumnas[pista.origen];
                destComp = panelesColumnas[pista.destino];
                break;
            case COL_CEL:
                origenComp = panelesColumnas[pista.origen];
                destComp = botonesCeldas[pista.destino];
                break;
            case CEL_COL:
                origenComp = botonesCeldas[pista.origen];
                destComp = panelesColumnas[pista.destino];
                break;
            case COL_FND:
                origenComp = panelesColumnas[pista.origen];
                destComp = botonesFundaciones[pista.destino];
                break;
            case CEL_FND:
                origenComp = botonesCeldas[pista.origen];
                destComp = botonesFundaciones[pista.destino];
                break;
        }
        highlightComponents(origenComp, destComp);
    }

    private void highlightComponents(Component a, Component b) {
        Color origA = a.getBackground();
        Color origB = b.getBackground();
        a.setBackground(Color.YELLOW);
        b.setBackground(Color.YELLOW);
        Timer t = new Timer(1000, ev -> {
            a.setBackground(origA);
            b.setBackground(origB);
        });
        t.setRepeats(false);
        t.start();
    }

    private void refrescarUI() {
        for (int i = 0; i < 8; i++) {
            JPanel panel = panelesColumnas[i];
            panel.removeAll();
            NodoDoble<Carta> nodo = juego.getColumnas()[i].getInicio();
            if (nodo != null) {
                do {
                    Carta carta = nodo.getDato();
                    JButton b = new JButton(formatoCarta(carta));
                    b.setFont(new Font("SansSerif", Font.BOLD, 20));
                    b.setPreferredSize(new Dimension(80,120));
                    b.setBackground(Color.WHITE);
                    b.setOpaque(true);
                    b.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
                    b.setForeground(
                        (carta.getPalo() == Carta.Palo.CORAZONES || carta.getPalo() == Carta.Palo.DIAMANTES)
                        ? Color.RED : Color.BLACK
                    );
                    final int col = i;
                    b.addActionListener(e -> manejarClicColumna(col));
                    panel.add(b);
                    nodo = nodo.getSig();
                } while (nodo != juego.getColumnas()[i].getInicio());
            }
            panel.revalidate(); panel.repaint();
        }
        for (int i = 0; i < 4; i++) {
            Carta c = juego.getCeldasLibres()[i];
            botonesCeldas[i].setText(c != null ? formatoCarta(c) : "🃏");
        }
        for (int i = 0; i < 4; i++) {
            NodoDoble<Carta> top = juego.getFundaciones()[i].getFin();
            botonesFundaciones[i].setText(top != null ? formatoCarta(top.getDato()) : "🂠");
        }
        btnDeshacer.setEnabled(juego.puedeDeshacer());
        // Notificar si no hay movimientos válidos restantes
        if (juego.sugerirMovimiento() == null) {
            JOptionPane.showMessageDialog(this, "No hay movimientos válidos restantes.");
        }
        if (verificarVictoria()) {
            JOptionPane.showMessageDialog(this, "¡Felicidades, has ganado!");
            System.exit(0);
        }
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

    private boolean verificarVictoria() {
        for (int i = 0; i < 4; i++) {
            NodoDoble<Carta> inicio = juego.getFundaciones()[i].getInicio();
            int count = 0;
            if (inicio != null) {
                NodoDoble<Carta> actual = inicio;
                do {
                    count++;
                    actual = actual.getSig();
                } while (actual != inicio);
            }
            if (count != 13) {
                return false;
            }
        }
        return true;
    }
}
