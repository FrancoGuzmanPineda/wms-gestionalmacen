package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByEstado("Activo");
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public Producto guardar(Producto producto) {
        if (producto.getEstado() == null || producto.getEstado().isBlank()) {
            producto.setEstado("Activo");
        }

        if (producto.getStockActual() == null) {
            producto.setStockActual(0);
        }

        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(0);
        }

        return productoRepository.save(producto);
    }

    public void eliminarLogico(Integer id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            producto.setEstado("Inactivo");
            productoRepository.save(producto);
        }
    }

    public Producto aumentarStock(Integer idProducto, Integer cantidad) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();
        producto.setStockActual(stockActual + cantidad);

        return productoRepository.save(producto);
    }

    public Producto disminuirStock(Integer idProducto, Integer cantidad) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();

        if (stockActual < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        producto.setStockActual(stockActual - cantidad);

        return productoRepository.save(producto);
    }
}