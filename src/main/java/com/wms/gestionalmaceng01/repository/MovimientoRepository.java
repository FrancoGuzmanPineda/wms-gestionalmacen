package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByEstado(String estado);

    List<Movimiento> findByTipoMovimientoOrderByFechaDesc(String tipoMovimiento);

    List<Movimiento> findTop10ByTipoMovimientoOrderByFechaDesc(String tipoMovimiento);

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

    @Query("""
    SELECT COALESCE(SUM(m.cantidad), 0)
    FROM Movimiento m
    WHERE m.tipoMovimiento = :tipoMovimiento
    AND m.estado = 'Completado'
    AND m.fecha >= :inicio
    AND m.fecha < :fin
    """)
    Long sumarCantidadPorTipoEntreFechas(
            @Param("tipoMovimiento") String tipoMovimiento,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );


    List<Movimiento> findTop10ByEstadoOrderByFechaDesc(String estado);
    List<Movimiento> findByEstadoAndFechaBetweenOrderByFechaAsc(
        String estado,
        LocalDateTime inicio,
        LocalDateTime fin
);
}