package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoMovimientoRepository extends JpaRepository<TipoMovimiento, Integer> {

    List<TipoMovimiento> findByEstado(String estado);

    Optional<TipoMovimiento> findByNombreTipoMovimiento(String nombreTipoMovimiento);
}