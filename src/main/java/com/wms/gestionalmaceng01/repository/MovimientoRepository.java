package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByEstado(String estado);

    List<Movimiento> findByTipoMovimiento(String tipoMovimiento);

    List<Movimiento> findByProductoIdProducto(Integer idProducto);

}