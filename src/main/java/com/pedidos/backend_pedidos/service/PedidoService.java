package com.pedidos.backend_pedidos.service;

import com.pedidos.backend_pedidos.dto.ReporteFinancieroDTO;
import com.pedidos.backend_pedidos.model.Pedido;
import com.pedidos.backend_pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    // --- CONFIGURACIÓN DE TARIFAS ---

    // 1. PRECIOS BASE (El pago fijo por ir a buscar el pedido)
    private static final double BASE_DOMINGO = 1800.0;       // Domingo siempre paga esto
    private static final double BASE_SABADO_ALTO = 1400.0;   // Sábado solo si es Alta Demanda
    private static final double BASE_NORMAL = 1300.0;        // Lunes a Viernes, o Sábados normales

    // 2. PRECIOS POR ÍTEM (El pago por cada paquete)
    private static final double ITEM_ALTO = 140.0;           // Si marcas "Tarifa Alta" (Alta demanda)
    private static final double ITEM_NORMAL = 80.0;          // Si NO marcas "Tarifa Alta" (Normal)

    // 3. DATOS FISCALES (Retención SII)
    // CORRECCIÓN: Usamos 15.25% según tu indicación. 
    // Se define como 0.1525 para el cálculo matemático.
    private static final double PORCENTAJE_RETENCION = 0.1525;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // --- MÉTODO PRINCIPAL PARA GUARDAR ---
    public Pedido guardarPedido(Pedido pedido) {
        // Calculamos cuánto vale el pedido antes de guardarlo en la base de datos
        double costoCalculado = calcularCostoPedido(pedido);
        
        // Asignamos el valor al pedido
        pedido.setIngresoTotal(costoCalculado);
        
        // Guardamos
        return pedidoRepository.save(pedido);
    }

    // --- LÓGICA DE CÁLCULO (El cerebro del sistema) ---
    private double calcularCostoPedido(Pedido p) {
        DayOfWeek dia = p.getFecha().getDayOfWeek();
        boolean esAltaDemanda = p.isEsTarifaAlta(); // Esto es el checkbox de la interfaz

        // PASO 1: Determinar precio del ÍTEM
        // Depende SOLO de si hay alta demanda o no.
        // Si marcas el checkbox es 140, si no marcas es 80.
        // (Esto cumple la regla: Domingo sin alta demanda paga items a 80)
        double precioPorItem = esAltaDemanda ? ITEM_ALTO : ITEM_NORMAL;

        // PASO 2: Determinar precio BASE
        // Depende del DÍA y, en caso del sábado, de la demanda.
        double precioBase;

        if (dia == DayOfWeek.SUNDAY) {
            // Regla Suprema: Domingo siempre paga 1800 de base
            precioBase = BASE_DOMINGO;
        } 
        else if (dia == DayOfWeek.SATURDAY && esAltaDemanda) {
            // Sábado con alta demanda paga 1400
            precioBase = BASE_SABADO_ALTO;
        } 
        else {
            // Cualquier otro caso (Lunes-Viernes o Sábado normal) paga 1300
            precioBase = BASE_NORMAL;
        }

        // PASO 3: Suma del precio final
        return precioBase + (p.getCantidadItems() * precioPorItem);
    }

    // --- GENERACIÓN DE REPORTES (Esto no cambia) ---
    public ReporteFinancieroDTO calcularGanancias(LocalDate inicio, LocalDate fin) {
        List<Pedido> pedidos = pedidoRepository.findByFechaBetween(inicio, fin);

        int totalPedidos = pedidos.size();
        int totalItems = pedidos.stream().mapToInt(Pedido::getCantidadItems).sum();
        double ingresoBruto = pedidos.stream().mapToDouble(Pedido::getIngresoTotal).sum();
        
        // CORRECCIÓN MATEMÁTICA:
        // Ingreso Líquido = Bruto - (Bruto * 0.1525)
        // Matemáticamente es igual a: Bruto * (1 - 0.1525)
        double ingresoLiquido = ingresoBruto * (1.0 - PORCENTAJE_RETENCION);

        return new ReporteFinancieroDTO(totalPedidos, totalItems, ingresoBruto, ingresoLiquido);
    }

    // --- ELIMINAR PEDIDO ---
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}