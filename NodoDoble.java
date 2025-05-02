package com.mycompany.algoritmopractica4_2025;
/**
 *
 * @author trejo
 */
public class NodoDoble<T> {
    private T dato;
    private NodoDoble<T> sig;
    private NodoDoble<T> ant;

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoDoble<T> getSig() {
        return sig;
    }

    public void setSig(NodoDoble<T> sig) {
        this.sig = sig;
    }

    public NodoDoble<T> getAnt(){
        return ant;
    }

    public void setAnt(NodoDoble<T> ant){
        this.ant = ant;
    }

    @Override
    public String toString() {
        return "Nodo{" + "dato=" + dato + ", sig=" + sig + '}';
    }
    
}