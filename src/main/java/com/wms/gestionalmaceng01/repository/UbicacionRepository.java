package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    List<Ubicacion> findByEstado(String estado);

    Optional<Ubicacion> findByCodigoEstante(String codigoEstante);

}