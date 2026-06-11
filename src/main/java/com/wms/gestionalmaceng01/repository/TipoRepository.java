package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TipoRepository extends JpaRepository<Tipo, Integer> {

    List<Tipo> findByEstado(String estado);

}