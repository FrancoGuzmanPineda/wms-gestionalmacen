package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Tipo;
import com.wms.gestionalmaceng01.repository.TipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoService {

    private final TipoRepository tipoRepository;

    public TipoService(TipoRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }

    public List<Tipo> listarTodos() {
        return tipoRepository.findAll();
    }

    public List<Tipo> listarActivos() {
        return tipoRepository.findByEstado("Activo");
    }

    public Optional<Tipo> buscarPorId(Integer id) {
        return tipoRepository.findById(id);
    }

    public Tipo guardar(Tipo tipo) {
        if (tipo.getEstado() == null || tipo.getEstado().isBlank()) {
            tipo.setEstado("Activo");
        }
        return tipoRepository.save(tipo);
    }
}