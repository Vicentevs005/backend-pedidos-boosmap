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
    private static final double ITEM_ALTO = 120.0;           // Si marcas "Tarifa Alta" (Alta demanda)
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
    // --- LÓGICA FINANCIERA CORREGIDA (PICKER) ---
    private double calcularCostoPedido(Pedido pedido) {
        // 1. Determinar si es Domingo
        LocalDate fecha = pedido.getFecha();
        boolean esDomingo = (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY);
        boolean esTarifaAlta = pedido.isEsTarifaAlta();
        int cantidadItems = pedido.getCantidadItems();

        double valorBase;
        double valorPorItem;

        // A. Calcular el valor de cada Item
        if (esTarifaAlta) {
            valorPorItem = 120;
        } else {
            valorPorItem = 80;
        }

        // B. Calcular la Base del Pedido
        if (esDomingo) {
            // Domingos siempre base 1800 (sea alta o baja)
            valorBase = 1800;
        } else {
            // Lunes a Sábado
            if (esTarifaAlta) {
                valorBase = 1400; // Sube base en día de alta demanda
            } else {
                valorBase = 1300; // Base normal
            }
        }

        // C. Fórmula Final
        return valorBase + (cantidadItems * valorPorItem);
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

    // --- MÉTODO DE REPARACIÓN DE DATOS ---
    public void recalcularTodosLosPrecios() {
        // 1. Traer todos los pedidos de la historia
        List<Pedido> todos = pedidoRepository.findAll();

        // 2. Recorrer uno por uno
        for (Pedido p : todos) {
            // 3. Aplicar la fórmula matemática
            double costoReal = calcularCostoPedido(p);
            
            // 4. Actualizar el valor en memoria
            p.setIngresoTotal(costoReal);
            
            // 5. Guardar en base de datos
            pedidoRepository.save(p);
        }
    }

}