package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByEstado(String estado);

    List<Movimiento> findByTipoMovimiento(String tipoMovimiento);

    List<Movimiento> findByProductoIdProducto(Integer idProducto);

    @Query("""
SELECT m
FROM Movimiento m
WHERE m.tipoMovimiento='Ingreso'
AND DATE(m.fecha)
BETWEEN :inicio AND :fin
ORDER BY m.fecha DESC
""")
List<Movimiento> buscarEntradasPorFecha(
        LocalDate inicio,
        LocalDate fin
);

@Query("""
SELECT m
FROM Movimiento m
WHERE m.tipoMovimiento='Salida'
AND DATE(m.fecha)
BETWEEN :inicio AND :fin
ORDER BY m.fecha DESC
""")
List<Movimiento> buscarSalidasPorFecha(
        LocalDate inicio,
        LocalDate fin
);

}