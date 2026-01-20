package com.pedidos.backend_pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pedidos.backend_pedidos.model.Pedido;

import java.util.List;
import java.time.LocalDate;

@Repository // Indica que este es el componente que habla con la Base de Datos
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // ¡Aquí está la magia de Spring Data JPA!
    // Solo declarando el método con este nombre específico, 
    // Spring genera automáticamente el SQL: "SELECT * FROM pedidos WHERE fecha BETWEEN ? AND ?"
    
    List<Pedido> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}