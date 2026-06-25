package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstado(String estado);

    Optional<Producto> findByCodigo(String codigo);

    @Query("""
SELECT p
FROM Producto p
WHERE
LOWER(p.nombre) LIKE LOWER(CONCAT('%',:buscar,'%'))
OR
LOWER(p.codigo) LIKE LOWER(CONCAT('%',:buscar,'%'))
""")
List<Producto> buscarInventario(String buscar);



}