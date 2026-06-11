package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Categoria;
import com.wms.gestionalmaceng01.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> listarActivos() {
        return categoriaRepository.findByEstado("Activo");
    }

    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    public Categoria guardar(Categoria categoria) {
        if (categoria.getEstado() == null || categoria.getEstado().isBlank()) {
            categoria.setEstado("Activo");
        }
        return categoriaRepository.save(categoria);
    }

    public void eliminarLogico(Integer id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);

        if (categoriaOptional.isPresent()) {
            Categoria categoria = categoriaOptional.get();
            categoria.setEstado("Inactivo");
            categoriaRepository.save(categoria);
        }
    }
}