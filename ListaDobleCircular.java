package com.mycompany.algoritmopractica4_2025;

public class ListaDobleCircular<T extends Comparable<T>> {
    private NodoDoble<T> inicio;
    private NodoDoble<T> fin;

    public void insertarInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>();
        nuevo.setDato(dato);
        if (inicio == null) {
            inicio = nuevo;
            fin = nuevo;
            inicio.setSig(inicio);
            inicio.setAnt(inicio);
        } else {
            nuevo.setSig(inicio);
            nuevo.setAnt(fin);
            inicio.setAnt(nuevo);
            fin.setSig(nuevo);
            inicio = nuevo;
        }
    }

    public void insertarFin(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>();
        nuevo.setDato(dato);
        if (inicio == null) {
            inicio = nuevo;
            fin = nuevo;
            inicio.setSig(inicio);
            inicio.setAnt(inicio);
        } else {
            nuevo.setSig(inicio);
            nuevo.setAnt(fin);
            fin.setSig(nuevo);
            inicio.setAnt(nuevo);
            fin = nuevo;
        }
    }

    public T eliminarInicio() {
        if (inicio == null) {
            System.out.println("Lista vacía");
            return null;
        }
        NodoDoble<T> eliminado = inicio;
        if (inicio == fin) {
            inicio = null;
            fin = null;
        } else {
            inicio = inicio.getSig();
            inicio.setAnt(fin);
            fin.setSig(inicio);
        }
        return eliminado.getDato();
    }

    public T eliminarFin() {
        if (fin == null) {
            System.out.println("Lista vacía");
            return null;
        }
        NodoDoble<T> eliminado = fin;
        if (inicio == fin) {
            inicio = null;
            fin = null;
        } else {
            fin = fin.getAnt();
            fin.setSig(inicio);
            inicio.setAnt(fin);
        }
        return eliminado.getDato();
    }

    public String mostrarAdelante() {
        if (inicio == null) return "Lista vacía";
        StringBuilder sb = new StringBuilder();
        NodoDoble<T> aux = inicio;
        do {
            sb.append(aux.getDato()).append(" <-> ");
            aux = aux.getSig();
        } while (aux != inicio);
        sb.append("(circular)");
        return sb.toString();
    }

    public String mostrarAtras() {
        if (fin == null) return "Lista vacía";
        StringBuilder sb = new StringBuilder();
        NodoDoble<T> aux = fin;
        do {
            sb.append(aux.getDato()).append(" <-> ");
            aux = aux.getAnt();
        } while (aux != fin);
        sb.append("(circular)");
        return sb.toString();
    }

    public NodoDoble<T> getInicio() {
        return inicio;
    }

    public NodoDoble<T> getFin() {
        return fin;
    }

    public String mostrarRecursivo() {
        if (inicio == null) return "Lista vacía";
        return mostrarRecursivoAux(inicio, inicio);
    }

    private String mostrarRecursivoAux(NodoDoble<T> nodo, NodoDoble<T> inicioLista) {
        String result = nodo.getDato().toString() + " <-> ";
        if (nodo.getSig() == inicioLista) {
            return result + "(circular)";
        } else {
            return result + mostrarRecursivoAux(nodo.getSig(), inicioLista);
        }
    }

    public T eliminaX(T x) {
        if (inicio == null) return null;

        // Si el primer nodo contiene x:
        if (inicio.getDato().equals(x)) {
            return eliminarInicio();
        }

        NodoDoble<T> actual = inicio.getSig();
        while (actual != inicio) {
            if (actual.getDato().equals(x)) {
                // Si se encuentra en el último nodo:
                if (actual == fin) {
                    return eliminarFin();
                }
                // Eliminación en nodo intermedio
                NodoDoble<T> anterior = actual.getAnt();
                NodoDoble<T> siguiente = actual.getSig();
                anterior.setSig(siguiente);
                siguiente.setAnt(anterior);
                return actual.getDato();
            }
            actual = actual.getSig();
        }
        return null;
    }

    public int buscar(T x) {
        if (inicio == null) return -1;
        int pos = 0;
        NodoDoble<T> actual = inicio;
        do {
            if (actual.getDato().equals(x)) {
                return pos;
            }
            pos++;
            actual = actual.getSig();
        } while (actual != inicio);
        return -1;
    }

    public T eliminaPosicion(int posicion) {
        if (inicio == null || posicion < 0) return null;
        if (posicion == 0) {
            return eliminarInicio();
        }
        int pos = 0;
        NodoDoble<T> actual = inicio;
        do {
            if (pos == posicion) {
                if (actual == fin) {
                    return eliminarFin();
                } else {
                    NodoDoble<T> anterior = actual.getAnt();
                    NodoDoble<T> siguiente = actual.getSig();
                    anterior.setSig(siguiente);
                    siguiente.setAnt(anterior);
                    return actual.getDato();
                }
            }
            pos++;
            actual = actual.getSig();
        } while (actual != inicio);
        return null;
    }

    public void ordenarLista() {
        if (inicio == null || inicio.getSig() == inicio) return;
        NodoDoble<T> i = inicio;
        do {
            NodoDoble<T> j = i.getSig();
            while (j != inicio) {
                if (i.getDato().compareTo(j.getDato()) > 0) {
                    T temp = i.getDato();
                    i.setDato(j.getDato());
                    j.setDato(temp);
                }
                j = j.getSig();
            }
            i = i.getSig();
        } while (i.getSig() != inicio);
    }

    public void insertaenPosicion(T dato, int posicion) {
        NodoDoble<T> nuevo = new NodoDoble<>();
        nuevo.setDato(dato);
        if (inicio == null || posicion <= 0) {
            insertarInicio(dato);
            return;
        }
        int pos = 0;
        NodoDoble<T> actual = inicio;
        while (pos < posicion - 1 && actual.getSig() != inicio) {
            actual = actual.getSig();
            pos++;
        }
        nuevo.setSig(actual.getSig());
        nuevo.setAnt(actual);
        actual.getSig().setAnt(nuevo);
        actual.setSig(nuevo);
        if (actual == fin) {
            fin = nuevo;
        }
    }
}