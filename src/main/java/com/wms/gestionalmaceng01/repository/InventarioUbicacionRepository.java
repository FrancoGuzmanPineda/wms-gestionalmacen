package com.wms.gestionalmaceng01.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wms.gestionalmaceng01.models.InventarioUbicacion;

import jakarta.persistence.LockModeType;

public interface InventarioUbicacionRepository
        extends JpaRepository<InventarioUbicacion, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT inventario
        FROM InventarioUbicacion inventario
        WHERE inventario.producto.idProducto = :idProducto
          AND inventario.ubicacion.idUbicacion = :idUbicacion
        """)
    Optional<InventarioUbicacion> buscarParaActualizar(
            @Param("idProducto") Integer idProducto,
            @Param("idUbicacion") Integer idUbicacion
    );

    @Query("""
        SELECT inventario
        FROM InventarioUbicacion inventario
        JOIN FETCH inventario.producto producto
        JOIN FETCH inventario.ubicacion ubicacion
        WHERE producto.idProducto = :idProducto
          AND inventario.cantidad > 0
          AND UPPER(producto.estado) = 'ACTIVO'
          AND UPPER(ubicacion.estado) = 'ACTIVO'
        ORDER BY ubicacion.codigoEstante ASC
        """)
    List<InventarioUbicacion> listarDisponiblesPorProducto(
            @Param("idProducto") Integer idProducto
    );

    @Query("""
        SELECT inventario
        FROM InventarioUbicacion inventario
        WHERE inventario.producto.idProducto IN :idsProducto
        ORDER BY inventario.producto.nombre ASC,
                 inventario.ubicacion.codigoEstante ASC
        """)
    List<InventarioUbicacion> listarPorProductos(
            @Param("idsProducto") Collection<Integer> idsProducto
    );

    @Query("""
        SELECT inventario.cantidad
        FROM InventarioUbicacion inventario
        WHERE inventario.producto.idProducto = :idProducto
          AND inventario.ubicacion.idUbicacion = :idUbicacion
        """)
    Optional<Integer> buscarCantidadPorProductoYUbicacion(
            @Param("idProducto") Integer idProducto,
            @Param("idUbicacion") Integer idUbicacion
    );

    @Query("""
        SELECT COALESCE(SUM(inventario.cantidad), 0)
        FROM InventarioUbicacion inventario
        WHERE inventario.producto.idProducto = :idProducto
        """)
    Long sumarStockPorProducto(@Param("idProducto") Integer idProducto);
}
