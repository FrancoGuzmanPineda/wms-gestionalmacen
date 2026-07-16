package com.wms.gestionalmaceng01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wms.gestionalmaceng01.models.Producto;

import jakarta.persistence.LockModeType;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstado(String estado);

    Optional<Producto> findByCodigo(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT producto
        FROM Producto producto
        WHERE producto.idProducto = :idProducto
        """)
    Optional<Producto> buscarPorIdParaActualizar(
            @Param("idProducto") Integer idProducto
    );

    @Query("""
        SELECT p
        FROM Producto p
        WHERE p.estado = 'Activo'
          AND (
                LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%'))
                OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :buscar, '%'))
          )
        ORDER BY p.nombre ASC
        """)
    List<Producto> buscarInventarioActivo(
            @Param("buscar") String buscar
    );
}
