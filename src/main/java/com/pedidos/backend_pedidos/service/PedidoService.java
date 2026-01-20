package com.pedidos.backend_pedidos.service;

import com.pedidos.backend_pedidos.dto.ReporteFinancieroDTO;
import com.pedidos.backend_pedidos.model.Pedido;
import com.pedidos.backend_pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service // "Cocinero": Aquí ocurre la lógica
public class PedidoService {

    // --- CONSTANTES DE TARIFAS (CONFIGURACIÓN) ---
    
    // Tarifas Fin de Semana (Confirmadas por ti)
    private static final double TARIFA_BASE_SABADO = 1400;
    private static final double TARIFA_BASE_DOMINGO = 1800;
    private static final double TARIFA_ITEM_FINDE = 140;

    // Tarifas Días Normales (PENDIENTES - Ponemos 0 o un estimado por ahora)
    private static final double TARIFA_BASE_NORMAL = 1000; // <--- ¡ACTUALIZA ESTO CUANDO TE RESPONDAN!
    private static final double TARIFA_ITEM_NORMAL = 100;  // <--- ¡ACTUALIZA ESTO CUANDO TE RESPONDAN!

    // Retención SII 2025 (15%)
    private static final double RETENCION_IMPUESTO = 0.1525;

    private final PedidoRepository pedidoRepository;

    // Inyección de dependencias: El Servicio pide acceso al Repositorio
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // 1. Guardar un pedido
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // 2. Calcular Ganancias en un rango de fechas
    public ReporteFinancieroDTO calcularGanancias(LocalDate inicio, LocalDate fin) {
        // A. Buscamos los pedidos en la base de datos
        List<Pedido> pedidos = pedidoRepository.findByFechaBetween(inicio, fin);

        // B. Inicializamos contadores
        int totalPedidos = 0;
        int totalItems = 0;
        double sumaBruta = 0;

        // C. Recorremos cada pedido para calcular su valor individual
        for (Pedido p : pedidos) {
            totalPedidos++;
            totalItems += p.getCantidadItems();
            
            // Calculamos cuánto valió ESTE pedido específico
            sumaBruta += calcularCostoPedido(p);
        }

        // D. Calculamos el líquido final (Bruto - Retención)
        double descuento = sumaBruta * RETENCION_IMPUESTO;
        double sumaLiquida = sumaBruta - descuento;

        // E. Empaquetamos todo en el DTO y lo devolvemos
        return new ReporteFinancieroDTO(totalPedidos, totalItems, sumaBruta, sumaLiquida);
    }

    // Método privado auxiliar corregido
    private double calcularCostoPedido(Pedido p) {
        DayOfWeek dia = p.getFecha().getDayOfWeek();
        
        // Valores por defecto (Tarifa Normal)
        double pagoBase = TARIFA_BASE_NORMAL;
        double pagoPorItem = TARIFA_ITEM_NORMAL;

        // Lógica: Solo aplicamos precios especiales si el usuario marcó "Tarifa Alta"
        // Y ADEMÁS es fin de semana.
        if (p.isEsTarifaAlta()) {
            if (dia == DayOfWeek.SATURDAY) {
                pagoBase = TARIFA_BASE_SABADO;       // 1400
                pagoPorItem = TARIFA_ITEM_FINDE;     // 140
            } else if (dia == DayOfWeek.SUNDAY) {
                pagoBase = TARIFA_BASE_DOMINGO;      // 1800
                pagoPorItem = TARIFA_ITEM_FINDE;     // 140
            }
        }

        // Si es Lunes-Viernes, O es un Sábado/Domingo "malo" (sin tarifa alta),
        // el código de arriba no entra en los IF y se queda con los valores base normales.

        return pagoBase + (p.getCantidadItems() * pagoPorItem);
    }

    // 3. Eliminar un pedido
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

}