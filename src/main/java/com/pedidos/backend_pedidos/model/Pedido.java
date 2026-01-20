package com.pedidos.backend_pedidos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cantidad_items", nullable = false)
    private Integer cantidadItems;

    @Column(nullable = false)
    private LocalDate fecha; // Solo guarda Año-Mes-Día

    // NUEVO CAMPO: true = fin de semana/tarifa alta, false = día normal
    @Column(name = "es_tarifa_alta", nullable = false)
    private boolean esTarifaAlta;

    // --- CONSTRUCTORES ---

    public Pedido() {
    }

    // Este constructor nos permitirá crear pedidos con una fecha específica (pasada o actual)
    public Pedido(Integer cantidadItems, LocalDate fecha) {
        this.cantidadItems = cantidadItems;
        this.fecha = fecha;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(Integer cantidadItems) {
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

}