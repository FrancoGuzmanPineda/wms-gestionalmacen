package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    /**
     * El formulario de productos no puede modificar directamente el stock.
     *
     * Producto nuevo:
     * - Siempre inicia con stock 0.
     *
     * Producto existente:
     * - Conserva el stock almacenado en la base de datos.
     */
    @Transactional
    public Producto guardar(Producto datosFormulario) {

        if (datosFormulario.getIdProducto() == null) {

            prepararValoresGenerales(datosFormulario);

            /*
             * Aunque alguien manipule el formulario y envíe otro valor,
             * un producto nuevo siempre inicia con stock cero.
             */
            datosFormulario.setStockActual(0);

            return productoRepository.save(datosFormulario);
        }

        Producto productoExistente = productoRepository
                .findById(datosFormulario.getIdProducto())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado."
                        )
                );

        /*
         * Se actualizan únicamente los datos administrativos.
         */
        productoExistente.setCodigo(datosFormulario.getCodigo());
        productoExistente.setNombre(datosFormulario.getNombre());
        productoExistente.setCategoria(datosFormulario.getCategoria());
        productoExistente.setTipo(datosFormulario.getTipo());

        productoExistente.setCosto(
                valorDecimal(datosFormulario.getCosto())
        );

        productoExistente.setPrecioVenta(
                valorDecimal(datosFormulario.getPrecioVenta())
        );

        productoExistente.setStockMinimo(
                valorEnteroNoNegativo(
                        datosFormulario.getStockMinimo()
                )
        );

        productoExistente.setUnidadMedida(
                datosFormulario.getUnidadMedida()
        );

        productoExistente.setEstado(
                estadoValido(datosFormulario.getEstado())
        );

        /*
         * No se ejecuta setStockActual().
         *
         * De esta manera se conserva el stock existente,
         * aunque alguien envíe manualmente stockActual.
         */

        return productoRepository.save(productoExistente);
    }

    public void eliminarLogico(Integer id) {

        Optional<Producto> productoOptional =
                productoRepository.findById(id);

        if (productoOptional.isPresent()) {

            Producto producto = productoOptional.get();
            producto.setEstado("Inactivo");

            productoRepository.save(producto);
        }
    }

    public Producto aumentarStock(
            Integer idProducto,
            Integer cantidad
    ) {
        Producto producto = productoRepository
                .findById(idProducto)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado."
                        )
                );

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a cero."
            );
        }

        int stockActual = producto.getStockActual() == null
                ? 0
                : producto.getStockActual();

        producto.setStockActual(stockActual + cantidad);

        return productoRepository.save(producto);
    }

    public Producto disminuirStock(
            Integer idProducto,
            Integer cantidad
    ) {
        Producto producto = productoRepository
                .findById(idProducto)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado."
                        )
                );

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a cero."
            );
        }

        int stockActual = producto.getStockActual() == null
                ? 0
                : producto.getStockActual();

        if (stockActual < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente."
            );
        }

        producto.setStockActual(stockActual - cantidad);

        return productoRepository.save(producto);
    }

    private void prepararValoresGenerales(Producto producto) {

        producto.setCosto(
                valorDecimal(producto.getCosto())
        );

        producto.setPrecioVenta(
                valorDecimal(producto.getPrecioVenta())
        );

        producto.setStockMinimo(
                valorEnteroNoNegativo(
                        producto.getStockMinimo()
                )
        );

        producto.setEstado(
                estadoValido(producto.getEstado())
        );
    }

    private BigDecimal valorDecimal(BigDecimal valor) {

        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) < 0) {

            return BigDecimal.ZERO;
        }

        return valor;
    }

    private Integer valorEnteroNoNegativo(Integer valor) {

        if (valor == null || valor < 0) {
            return 0;
        }

        return valor;
    }

    private String estadoValido(String estado) {

        if ("Inactivo".equalsIgnoreCase(estado)) {
            return "Inactivo";
        }

        return "Activo";
    }
}