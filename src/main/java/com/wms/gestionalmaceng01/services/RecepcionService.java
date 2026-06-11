package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.models.Tipo;
import com.wms.gestionalmaceng01.models.Ubicacion;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.MovimientoRepository;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import com.wms.gestionalmaceng01.repository.TipoRepository;
import com.wms.gestionalmaceng01.repository.UbicacionRepository;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecepcionService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final TipoRepository tipoRepository;
    private final UsuarioRepository usuarioRepository;

    public RecepcionService(
            MovimientoRepository movimientoRepository,
            ProductoRepository productoRepository,
            UbicacionRepository ubicacionRepository,
            TipoRepository tipoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.tipoRepository = tipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

  @Transactional
public Movimiento registrarRecepcion(
        Integer idProducto,
        Integer idUbicacion,
        Integer idTipo,
        Long idUsuario,
        Integer cantidad,
        String observacion
) {
    if (cantidad == null || cantidad <= 0) {
        throw new RuntimeException("La cantidad debe ser mayor a cero");
    }

    Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    Ubicacion ubicacion = ubicacionRepository.findById(idUbicacion)
            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));

    Tipo tipo = tipoRepository.findById(idTipo)
            .orElseThrow(() -> new RuntimeException("Tipo no encontrado"));

    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();
    producto.setStockActual(stockActual + cantidad);
    productoRepository.save(producto);

    Movimiento movimiento = new Movimiento();
    movimiento.setProducto(producto);
    movimiento.setUbicacion(ubicacion);
    movimiento.setTipo(tipo);
    movimiento.setUsuario(usuario);
    movimiento.setCantidad(cantidad);
    movimiento.setTipoMovimiento("Ingreso");
    movimiento.setFecha(LocalDateTime.now());
    movimiento.setObservacion(observacion);
    movimiento.setEstado("Completado");

    return movimientoRepository.save(movimiento);
}

public List<Movimiento> listarMovimientosIngreso() {
    return movimientoRepository.findByTipoMovimiento("Ingreso");
}

public List<Movimiento> listarTodosLosMovimientos() {
    return movimientoRepository.findAll();
}
}