package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstado(String estado);

    Optional<Producto> findByCodigo(String codigo);

}