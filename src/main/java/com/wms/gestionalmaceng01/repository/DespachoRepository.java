package com.wms.gestionalmaceng01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wms.gestionalmaceng01.models.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Integer> {

    // Historial ordenado del más reciente al más antiguo
    List<Despacho> findAllByOrderByFechaRegistroDesc();

    // Buscar guía existente para evitar duplicados
    Optional<Despacho> findByGuiaRemision(String guiaRemision);

}