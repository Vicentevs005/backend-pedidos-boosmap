package com.pedidos.backend_pedidos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cantidadItems;
    private LocalDate fecha;
    private boolean esTarifaAlta;
    private double ingresoTotal; 

    public Pedido() {
    }

    public Pedido(int cantidadItems, LocalDate fecha, boolean esTarifaAlta) {
        this.cantidadItems = cantidadItems;
        this.fecha = fecha;
        this.esTarifaAlta = esTarifaAlta;
        this.ingresoTotal = 0.0; // Inicializamos en 0
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(int cantidadItems) {
        this.cantidadItems = cantidadItems;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isEsTarifaAlta() {
        return esTarifaAlta;
    }

    public void setEsTarifaAlta(boolean esTarifaAlta) {
        this.esTarifaAlta = esTarifaAlta;
    }
    
    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}