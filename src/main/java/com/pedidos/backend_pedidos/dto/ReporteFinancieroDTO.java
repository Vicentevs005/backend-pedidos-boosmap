package com.pedidos.backend_pedidos.dto;

public class ReporteFinancieroDTO {
    // Totales de actividad
    private int totalPedidos;
    private int totalItems;

    // Dinero
    private double ingresoBruto;   // Lo que paga Boosmap
    private double ingresoLiquido; // Lo que llega a tu bolsillo (después de impuestos)

    public ReporteFinancieroDTO(int totalPedidos, int totalItems, double ingresoBruto, double ingresoLiquido) {
        this.totalPedidos = totalPedidos;
        this.totalItems = totalItems;
        this.ingresoBruto = ingresoBruto;
        this.ingresoLiquido = ingresoLiquido;
    }

    // Getters (necesarios para que Spring convierta esto a JSON después)
    public int getTotalPedidos() { return totalPedidos; }
    public int getTotalItems() { return totalItems; }
    public double getIngresoBruto() { return ingresoBruto; }
    public double getIngresoLiquido() { return ingresoLiquido; }
}