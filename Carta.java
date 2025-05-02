package com.mycompany.algoritmopractica4_2025;


public class Carta implements Comparable<Carta> {
    public enum Palo {
        CORAZONES, DIAMANTES, TREBOLES, PICAS
    }

    private int valor;
    private Palo palo;

    public Carta(int valor, Palo palo) {
        this.valor = valor;
        this.palo = palo;
    }

    public int getValor() {
        return valor;
    }

    public Palo getPalo() {
        return palo;
    }

    @Override
    public int compareTo(Carta otra) {
        if (this.palo == otra.palo) {
            return Integer.compare(this.valor, otra.valor);
        }
        return this.palo.compareTo(otra.palo);
    }

    @Override
    public String toString() {
        String valorStr;
        switch (valor) {
            case 1: valorStr = "A"; break;
            case 11: valorStr = "J"; break;
            case 12: valorStr = "Q"; break;
            case 13: valorStr = "K"; break;
            default: valorStr = String.valueOf(valor);
        }
        return valorStr + " de " + palo.toString();
    }
}
