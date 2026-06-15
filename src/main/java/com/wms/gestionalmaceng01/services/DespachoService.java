package com.wms.gestionalmaceng01.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wms.gestionalmaceng01.models.Despacho;
import com.wms.gestionalmaceng01.repository.DespachoRepository;

@Service
public class DespachoService {

    private final DespachoRepository despachoRepository;

    public DespachoService(DespachoRepository despachoRepository) {
        this.despachoRepository = despachoRepository;
    }

    public List<Despacho> listar() {
        return despachoRepository.findAll();
    }

    public Despacho guardar(Despacho despacho) {
        return despachoRepository.save(despacho);
    }
}