package com.wms.gestionalmaceng01.repository;

import com.wms.gestionalmaceng01.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByEstado(String estado);

}