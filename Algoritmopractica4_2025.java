/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.algoritmopractica4_2025;

/**
 *
 * @author trejo
 */
public class Algoritmopractica4_2025 {
    public static void main(String[] args) {
        
        Baraja baraja = new Baraja();
        ListaDobleCircular<Carta>[] columnas = new ListaDobleCircular[8];

        for (int i = 0; i < 8; i++) {
            columnas[i] = new ListaDobleCircular<>();
        }
        
        // 4 columnas de 6 cartas
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                Carta carta = baraja.robarCarta();
                columnas[i].insertarFin(carta);
            }
        }
        
        // 4 columnas de 7 cartas
        for (int i = 4; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                Carta carta = baraja.robarCarta();
                columnas[i].insertarFin(carta);
            }
        }
        
        // crear las 4 fundaciones 
        ListaDobleCircular<Carta>[] fundaciones = new ListaDobleCircular[4];
        for (int i = 0; i < 4; i++) {
            fundaciones[i] = new ListaDobleCircular<>();
        }
        // arreglo para las celdas libres 
        Carta[] celdasLibres = new Carta[4];
        for (int i = 0; i < 4; i++) {
            celdasLibres[i] = new Carta(0, null);
        }

        // Mostrar columnas // pintar en la ui
        for (int i = 0; i < 8; i++) {
            System.out.println("Columna " + (i + 1) + ":");
            System.out.println(columnas[i].mostrarAdelante());
        }
        
    }
}
