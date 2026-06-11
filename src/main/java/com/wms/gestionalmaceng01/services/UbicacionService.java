package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Ubicacion;
import com.wms.gestionalmaceng01.repository.UbicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    public List<Ubicacion> listarTodos() {
        return ubicacionRepository.findAll();
    }

    public List<Ubicacion> listarActivos() {
        return ubicacionRepository.findByEstado("Activo");
    }

    public Optional<Ubicacion> buscarPorId(Integer id) {
        return ubicacionRepository.findById(id);
    }

    public Ubicacion guardar(Ubicacion ubicacion) {
        if (ubicacion.getEstado() == null || ubicacion.getEstado().isBlank()) {
            ubicacion.setEstado("Activo");
        }
        return ubicacionRepository.save(ubicacion);
    }

    public void eliminarLogico(Integer id) {
        Optional<Ubicacion> ubicacionOptional = ubicacionRepository.findById(id);

        if (ubicacionOptional.isPresent()) {
            Ubicacion ubicacion = ubicacionOptional.get();
            ubicacion.setEstado("Inactivo");
            ubicacionRepository.save(ubicacion);
        }
    }
}