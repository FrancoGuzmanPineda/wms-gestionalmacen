package com.wms.gestionalmaceng01.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wms.gestionalmaceng01.models.InventarioUbicacion;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.models.Ubicacion;
import com.wms.gestionalmaceng01.repository.InventarioUbicacionRepository;
import com.wms.gestionalmaceng01.repository.ProductoRepository;

@Service
public class InventarioUbicacionService {

    private final InventarioUbicacionRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    public InventarioUbicacionService(
            InventarioUbicacionRepository inventarioRepository,
            ProductoRepository productoRepository
    ) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<InventarioUbicacion> listarDisponiblesPorProducto(
            Integer idProducto
    ) {
        if (idProducto == null) {
            return List.of();
        }

        return inventarioRepository
                .listarDisponiblesPorProducto(idProducto);
    }

    @Transactional(readOnly = true)
    public int obtenerStockPorProductoYUbicacion(
            Integer idProducto,
            Integer idUbicacion
    ) {
        if (idProducto == null || idUbicacion == null) {
            return 0;
        }

        return inventarioRepository
                .buscarCantidadPorProductoYUbicacion(
                        idProducto,
                        idUbicacion
                )
                .orElse(0);
    }

    public Map<Integer, List<InventarioUbicacion>> agruparPorProducto(
            List<Producto> productos
    ) {
        if (productos == null || productos.isEmpty()) {
            return Map.of();
        }

        List<Integer> idsProducto = productos.stream()
                .map(Producto::getIdProducto)
                .filter(id -> id != null)
                .toList();

        if (idsProducto.isEmpty()) {
            return Map.of();
        }

        Map<Integer, List<InventarioUbicacion>> agrupado =
                inventarioRepository
                        .listarPorProductos(idsProducto)
                        .stream()
                        .filter(inventario -> inventario.getCantidad() != null)
                        .filter(inventario -> inventario.getCantidad() > 0)
                        .collect(Collectors.groupingBy(
                                inventario -> inventario
                                        .getProducto()
                                        .getIdProducto(),
                                LinkedHashMap::new,
                                Collectors.toCollection(ArrayList::new)
                        ));

        productos.forEach(producto -> agrupado.putIfAbsent(
                producto.getIdProducto(),
                new ArrayList<>()
        ));

        return agrupado;
    }

    @Transactional
    public void aumentarStock(
            Producto producto,
            Ubicacion ubicacion,
            int cantidad
    ) {
        validarCantidad(cantidad);

        Optional<InventarioUbicacion> inventarioExistente =
                inventarioRepository.buscarParaActualizar(
                        producto.getIdProducto(),
                        ubicacion.getIdUbicacion()
                );

        InventarioUbicacion inventario = inventarioExistente.orElseGet(
                () -> new InventarioUbicacion(producto, ubicacion, 0)
        );

        int stockUbicacion = valorNoNulo(inventario.getCantidad());

        try {
            inventario.setCantidad(Math.addExact(stockUbicacion, cantidad));
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "La cantidad supera el límite permitido para la ubicación."
            );
        }

        inventarioRepository.saveAndFlush(inventario);
        sincronizarStockTotal(producto);
    }

    @Transactional
    public void disminuirStock(
            Producto producto,
            Ubicacion ubicacion,
            int cantidad
    ) {
        validarCantidad(cantidad);

        InventarioUbicacion inventario = inventarioRepository
                .buscarParaActualizar(
                        producto.getIdProducto(),
                        ubicacion.getIdUbicacion()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto no tiene stock registrado en la ubicación "
                                + ubicacion.getCodigoEstante() + "."
                ));

        int stockDisponible = valorNoNulo(inventario.getCantidad());

        if (stockDisponible < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente en la ubicación "
                            + ubicacion.getCodigoEstante()
                            + ". Disponible: "
                            + stockDisponible
            );
        }

        inventario.setCantidad(stockDisponible - cantidad);
        inventarioRepository.saveAndFlush(inventario);
        sincronizarStockTotal(producto);
    }

    private void sincronizarStockTotal(Producto producto) {
        long total = inventarioRepository
                .sumarStockPorProducto(producto.getIdProducto());

        if (total > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "El stock total del producto excede el límite permitido."
            );
        }

        producto.setStockActual((int) total);
        productoRepository.save(producto);
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a cero."
            );
        }
    }

    private int valorNoNulo(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
