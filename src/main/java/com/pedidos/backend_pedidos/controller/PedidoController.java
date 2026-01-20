package com.pedidos.backend_pedidos.controller;

import com.pedidos.backend_pedidos.dto.ReporteFinancieroDTO;
import com.pedidos.backend_pedidos.model.Pedido;
import com.pedidos.backend_pedidos.service.PedidoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController // 1. "Soy un Camarero" (Recibo peticiones HTTP)
@RequestMapping("/api/pedidos") // 2. Todas mis URLs empezarán con esta dirección base
public class PedidoController {

    private final PedidoService pedidoService;

    // Inyectamos el servicio (El camarero necesita acceso a la cocina)
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ENDPOINT 1: Guardar un nuevo pedido
    // URL: POST http://localhost:8080/api/pedidos
    // El JSON que envíes desde Swing se convertirá automáticamente en un objeto Pedido
    @PostMapping
    public Pedido guardar(@RequestBody Pedido pedido) {
        return pedidoService.guardarPedido(pedido);
    }

    // ENDPOINT 2: Obtener reporte de ganancias
    // URL: GET http://localhost:8080/api/pedidos/reporte?inicio=2025-01-01&fin=2025-01-31
    @GetMapping("/reporte")
    public ReporteFinancieroDTO obtenerReporte(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        return pedidoService.calcularGanancias(inicio, fin);
    }

    // ENDPOINT 3: Borrar un pedido por ID
    // URL: DELETE http://localhost:8080/api/pedidos/{id}
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        // Usamos el método que ya trae JPA por defecto
        pedidoService.eliminarPedido(id); 
    }

}