package com.wms.gestionalmaceng01.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wms.gestionalmaceng01.models.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Integer> {
}