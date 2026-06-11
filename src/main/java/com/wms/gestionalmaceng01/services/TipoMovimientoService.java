package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.TipoMovimiento;
import com.wms.gestionalmaceng01.repository.TipoMovimientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoMovimientoService {

    private final TipoMovimientoRepository tipoMovimientoRepository;

    public TipoMovimientoService(TipoMovimientoRepository tipoMovimientoRepository) {
        this.tipoMovimientoRepository = tipoMovimientoRepository;
    }

    public List<TipoMovimiento> listarTodos() {
        return tipoMovimientoRepository.findAll();
    }

    public List<TipoMovimiento> listarActivos() {
        return tipoMovimientoRepository.findByEstado("Activo");
    }

    public Optional<TipoMovimiento> buscarPorId(Integer id) {
        return tipoMovimientoRepository.findById(id);
    }

    public TipoMovimiento guardar(TipoMovimiento tipoMovimiento) {
        if (tipoMovimiento.getEstado() == null || tipoMovimiento.getEstado().isBlank()) {
            tipoMovimiento.setEstado("Activo");
        }

        return tipoMovimientoRepository.save(tipoMovimiento);
    }
}