package com.pedidos.backend_pedidos.controller;

import com.pedidos.backend_pedidos.dto.ReporteFinancieroDTO;
import com.pedidos.backend_pedidos.model.Pedido;
import com.pedidos.backend_pedidos.repository.PedidoRepository;
import com.pedidos.backend_pedidos.service.PedidoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Permite que el celular y el PC se conecten sin problemas
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
    }

    // 1. GUARDAR (POST)
    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        return pedidoService.guardarPedido(pedido);
    }

    // 2. LISTAR TODOS (GET) -> ¡ESTO ES LO QUE FALTABA PARA EL ERROR 405!
    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // 3. REPORTE FINANCIERO (GET con fechas)
    @GetMapping("/reporte")
    public ReporteFinancieroDTO obtenerReporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return pedidoService.calcularGanancias(inicio, fin);
    }

    // 4. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
    }
}