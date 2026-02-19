/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

public class Lista<T> {
    private Nodo<T> pFirst;
    private Nodo<T> pLast;
    private int size;

    public Lista() {
        this.pFirst = null;
        this.pLast = null;
        this.size = 0;
    }

    public boolean esVacia() { return pFirst == null; }

    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (esVacia()) { 
            pFirst = nuevo; 
            pLast = nuevo;
        } else {
            pLast.setSiguiente(nuevo);
            pLast = nuevo;
        }
        size++;
    }
    
    public void insertarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (esVacia()) {
            pFirst = nuevo;
            pLast = nuevo;
        } else {
            nuevo.setSiguiente(pFirst);
            pFirst = nuevo;
        }
        size++;
    }

    public void eliminar(T dato) {
        if (esVacia()) return;
        if (pFirst.getContenido() == dato) {
            pFirst = pFirst.getSiguiente();
            if (pFirst == null) pLast = null;
            size--;
            return;
        }
        Nodo<T> aux = pFirst;
        while (aux.getSiguiente() != null) {
            if (aux.getSiguiente().getContenido() == dato) {
                if (aux.getSiguiente() == pLast) {
                    pLast = aux;
                }
                aux.setSiguiente(aux.getSiguiente().getSiguiente());
                size--;
                return;
            }
            aux = aux.getSiguiente();
        }
    }
    
    public T extraerPrimero() {
        if (esVacia()) return null;
        T dato = pFirst.getContenido();
        pFirst = pFirst.getSiguiente();
        if (pFirst == null) pLast = null;
        size--;
        return dato;
    }

    public T getUltimo() {
        if (esVacia()) return null;
        return pLast.getContenido();
    }
    
    public T getPrimero() {
        if (esVacia()) return null;
        return pFirst.getContenido();
    }
    
    public T getEnPosicion(int index) {
        if (index < 0 || index >= size) return null;
        Nodo<T> aux = pFirst;
        for (int i = 0; i < index; i++) {
            aux = aux.getSiguiente();
        }
        return aux.getContenido();
    }
    
    public void limpiar() {
        pFirst = null;
        pLast = null;
        size = 0;
    }
    
    public String[] toStringArray() {
        String[] arr = new String[size];
        Nodo<T> aux = pFirst;
        int i = 0;
        while (aux != null) {
            arr[i++] = aux.getContenido().toString();
            aux = aux.getSiguiente();
        }
        return arr;
    }

    public int getSize() { return size; }
    public Nodo<T> getpFirst() { return pFirst; }
}