package com.mycompany.algoritmopractica4_2025;

import java.util.Stack;

public class JuegoLogica {
    private Baraja baraja;
    private ListaDobleCircular<Carta>[] columnas;
    private ListaDobleCircular<Carta>[] fundaciones;
    private Carta[] celdasLibres;
    private Stack<State> undoStack;

    public JuegoLogica() {
        baraja = new Baraja();
        columnas = new ListaDobleCircular[8];
        fundaciones = new ListaDobleCircular[4];
        celdasLibres = new Carta[4];
        undoStack = new Stack<>();

        for (int i = 0; i < 8; i++) {
            columnas[i] = new ListaDobleCircular<>();
        }
        for (int i = 0; i < 4; i++) {
            fundaciones[i] = new ListaDobleCircular<>();
            celdasLibres[i] = null;
        }

        repartirCartas();
    }

    private void repartirCartas() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                columnas[i].insertarFin(baraja.robarCarta());
            }
        }
        for (int i = 4; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                columnas[i].insertarFin(baraja.robarCarta());
            }
        }
    }

    private void guardarEstado() {
        State s = new State();
        s.columnas = deepCopyLists(columnas);
        s.fundaciones = deepCopyLists(fundaciones);
        s.celdasLibres = celdasLibres.clone();
        undoStack.push(s);
    }

    @SuppressWarnings("unchecked")
    private ListaDobleCircular<Carta>[] deepCopyLists(ListaDobleCircular<Carta>[] src) {
        ListaDobleCircular<Carta>[] copy = new ListaDobleCircular[src.length];
        for (int i = 0; i < src.length; i++) {
            ListaDobleCircular<Carta> newList = new ListaDobleCircular<>();
            NodoDoble<Carta> nodo = src[i].getInicio();
            if (nodo != null) {
                do {
                    newList.insertarFin(nodo.getDato());
                    nodo = nodo.getSig();
                } while (nodo != src[i].getInicio());
            }
            copy[i] = newList;
        }
        return copy;
    }

    public boolean puedeDeshacer() {
        return !undoStack.isEmpty();
    }

    public void deshacer() {
        if (puedeDeshacer()) {
            State s = undoStack.pop();
            columnas = deepCopyLists(s.columnas);
            fundaciones = deepCopyLists(s.fundaciones);
            celdasLibres = s.celdasLibres.clone();
        }
    }

    public Move sugerirMovimiento() {
        // Intentar mover entre columnas
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (canMoveEntreColumnas(i, j)) return new Move(MoveType.COL_COL, i, j);
            }
        }
        // Intentar a celda libre
        for (int i = 0; i < 8; i++) {
            if (canMoveACeldaLibre(i)) {
                for (int c = 0; c < 4; c++) {
                    if (celdasLibres[c] == null) return new Move(MoveType.COL_CEL, i, c);
                }
            }
        }
        // De celda a columna
        for (int c = 0; c < 4; c++) {
            for (int j = 0; j < 8; j++) {
                if (canMoveDesdeCeldaALaColumna(c, j)) return new Move(MoveType.CEL_COL, c, j);
            }
        }
        // A fundación desde columna
        for (int i = 0; i < 8; i++) {
            for (int f = 0; f < 4; f++) {
                if (canMoveAFundacionDesdeColumna(i, f)) return new Move(MoveType.COL_FND, i, f);
            }
        }
        // A fundación desde celda
        for (int c = 0; c < 4; c++) {
            for (int f = 0; f < 4; f++) {
                if (canMoveAFundacionDesdeCelda(c, f)) return new Move(MoveType.CEL_FND, c, f);
            }
        }
        return null;
    }

    // Métodos de verificación (no modifican el estado)
    public boolean canMoveEntreColumnas(int ori, int dest) {
        if (ori < 0 || ori >= 8 || dest < 0 || dest >= 8) return false;
        NodoDoble<Carta> nOri = columnas[ori].getFin();
        if (nOri == null) return false;
        Carta c = nOri.getDato();
        NodoDoble<Carta> nDest = columnas[dest].getFin();
        return nDest == null || (c.getPalo() == nDest.getDato().getPalo() && c.getValor() == nDest.getDato().getValor() - 1);
    }

    public boolean canMoveACeldaLibre(int col) {
        if (col < 0 || col >= 8) return false;
        if (columnas[col].getFin() == null) return false;
        for (Carta c : celdasLibres) if (c == null) return true;
        return false;
    }

    public boolean canMoveDesdeCeldaALaColumna(int celda, int dest) {
        if (celda < 0 || celda >= 4 || dest < 0 || dest >= 8) return false;
        Carta c = celdasLibres[celda];
        if (c == null) return false;
        NodoDoble<Carta> nDest = columnas[dest].getFin();
        return nDest == null || (c.getPalo() == nDest.getDato().getPalo() && c.getValor() == nDest.getDato().getValor() - 1);
    }

    public boolean canMoveAFundacionDesdeColumna(int col, int fund) {
        if (col < 0 || col >= 8 || fund < 0 || fund >= 4) return false;
        NodoDoble<Carta> nOri = columnas[col].getFin();
        if (nOri == null) return false;
        Carta c = nOri.getDato();
        NodoDoble<Carta> nFund = fundaciones[fund].getFin();
        if (nFund == null) return c.getValor() == 1;
        return c.getPalo() == nFund.getDato().getPalo() && c.getValor() == nFund.getDato().getValor() + 1;
    }

    public boolean canMoveAFundacionDesdeCelda(int celda, int fund) {
        if (celda < 0 || celda >= 4 || fund < 0 || fund >= 4) return false;
        Carta c = celdasLibres[celda];
        if (c == null) return false;
        NodoDoble<Carta> nFund = fundaciones[fund].getFin();
        if (nFund == null) return c.getValor() == 1;
        return c.getPalo() == nFund.getDato().getPalo() && c.getValor() == nFund.getDato().getValor() + 1;
    }

    // Métodos de movimiento (guardan estado antes)
    public boolean moverEntreColumnas(int ori, int dest) {
        if (!canMoveEntreColumnas(ori, dest)) return false;
        guardarEstado();
        Carta c = columnas[ori].eliminarFin();
        columnas[dest].insertarFin(c);
        return true;
    }

    public boolean moverACeldaLibre(int col) {
        if (!canMoveACeldaLibre(col)) return false;
        guardarEstado();
        Carta c = columnas[col].eliminarFin();
        for (int i = 0; i < 4; i++) {
            if (celdasLibres[i] == null) {
                celdasLibres[i] = c;
                break;
            }
        }
        return true;
    }

    public boolean moverDesdeCeldaALaColumna(int celda, int dest) {
        if (!canMoveDesdeCeldaALaColumna(celda, dest)) return false;
        guardarEstado();
        Carta c = celdasLibres[celda];
        celdasLibres[celda] = null;
        columnas[dest].insertarFin(c);
        return true;
    }

    public boolean moverAFundacionDesdeColumna(int col, int fund) {
        if (!canMoveAFundacionDesdeColumna(col, fund)) return false;
        guardarEstado();
        Carta c = columnas[col].eliminarFin();
        fundaciones[fund].insertarFin(c);
        return true;
    }

    public boolean moverAFundacionDesdeCelda(int celda, int fund) {
        if (!canMoveAFundacionDesdeCelda(celda, fund)) return false;
        guardarEstado();
        Carta c = celdasLibres[celda];
        celdasLibres[celda] = null;
        fundaciones[fund].insertarFin(c);
        return true;
    }

    public ListaDobleCircular<Carta>[] getColumnas() { return columnas; }
    public ListaDobleCircular<Carta>[] getFundaciones() { return fundaciones; }
    public Carta[] getCeldasLibres() { return celdasLibres; }

    public static class Move {
        public MoveType type;
        public int origen, destino;
        public Move(MoveType t, int o, int d) {
            type = t; origen = o; destino = d;
        }
        @Override
        public String toString() {
            return type + " from " + origen + " to " + destino;
        }
    }

    public enum MoveType { COL_COL, COL_CEL, CEL_COL, COL_FND, CEL_FND }

    private static class State {
        ListaDobleCircular<Carta>[] columnas;
        ListaDobleCircular<Carta>[] fundaciones;
        Carta[] celdasLibres;
    }
}
