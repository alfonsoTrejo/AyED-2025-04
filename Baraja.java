package com.mycompany.algoritmopractica4_2025;

import java.util.Random;

public class Baraja {
    private ListaDobleCircular<Carta> cartas;

    public Baraja() {
        cartas = new ListaDobleCircular<>();
        inicializarBaraja();
        barajar();
    }

    private void inicializarBaraja() {
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (int valor = 1; valor <= 13; valor++) {
                cartas.insertarFin(new Carta(valor, palo));
            }
        }
    }

    public void barajar() {
        Random rand = new Random();
        int total = 52;
        NodoDoble<Carta> actual = cartas.getInicio();

        for (int i = 0; i < total; i++) {
            int pasos = rand.nextInt(total);
            NodoDoble<Carta> otro = cartas.getInicio();
            for (int j = 0; j < pasos; j++) {
                otro = otro.getSig();
            }

            Carta temp = actual.getDato();
            actual.setDato(otro.getDato());
            otro.setDato(temp);

            actual = actual.getSig();
        }
    }

    public Carta robarCarta() {
        return cartas.eliminarInicio();
    }

    public void mostrarBaraja() {
        System.out.println(cartas.mostrarAdelante());
    }
    public void ordenar() {
        cartas.ordenarLista();
    }
}
