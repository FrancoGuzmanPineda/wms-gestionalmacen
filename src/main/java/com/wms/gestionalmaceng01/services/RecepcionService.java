package com.wms.gestionalmaceng01.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.models.TipoMovimiento;
import com.wms.gestionalmaceng01.models.Ubicacion;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.MovimientoRepository;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import com.wms.gestionalmaceng01.repository.TipoMovimientoRepository;
import com.wms.gestionalmaceng01.repository.UbicacionRepository;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Service
public class RecepcionService {

    private static final Set<String> ROLES_AUTORIZADOS =
            Set.of("ADMIN", "SUPERVISOR");

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioUbicacionService inventarioUbicacionService;

    public RecepcionService(
            MovimientoRepository movimientoRepository,
            ProductoRepository productoRepository,
            UbicacionRepository ubicacionRepository,
            TipoMovimientoRepository tipoMovimientoRepository,
            UsuarioRepository usuarioRepository,
            InventarioUbicacionService inventarioUbicacionService
    ) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioUbicacionService = inventarioUbicacionService;
    }

    @Transactional
    public Movimiento registrarRecepcion(
            Integer idProducto,
            Integer idUbicacion,
            String correoUsuario,
            Integer cantidad,
            String observacion
    ) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a cero."
            );
        }

        Producto producto = productoRepository.buscarPorIdParaActualizar(idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto seleccionado no existe."
                ));

        if (!"Activo".equalsIgnoreCase(producto.getEstado())) {
            throw new IllegalArgumentException(
                    "El producto seleccionado está inactivo."
            );
        }

        Ubicacion ubicacion = ubicacionRepository.findById(idUbicacion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La ubicación seleccionada no existe."
                ));

        if (!"Activo".equalsIgnoreCase(ubicacion.getEstado())) {
            throw new IllegalArgumentException(
                    "La ubicación seleccionada está inactiva."
            );
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró al usuario autenticado."
                ));

        if (!usuario.isEstado()) {
            throw new IllegalArgumentException(
                    "El usuario autenticado está inactivo."
            );
        }

        String rol = usuario.getRol() == null
                ? ""
                : usuario.getRol().trim().toUpperCase();

        if (!ROLES_AUTORIZADOS.contains(rol)) {
            throw new IllegalArgumentException(
                    "El usuario no tiene autorización para registrar recepciones."
            );
        }

        TipoMovimiento tipoMovimientoIngreso =
                tipoMovimientoRepository
                        .findByNombreTipoMovimiento("Ingreso")
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe el tipo de movimiento Ingreso."
                        ));

        /*
         * El ingreso aumenta el stock de la ubicación seleccionada.
         * El servicio también recalcula el stock total del producto como
         * la suma de todas sus ubicaciones.
         */
        inventarioUbicacionService.aumentarStock(
                producto,
                ubicacion,
                cantidad
        );

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setUbicacion(ubicacion);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimientoRef(tipoMovimientoIngreso);
        movimiento.setTipoMovimiento("Ingreso");
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(limitarObservacion(observacion));
        movimiento.setEstado("Completado");

        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> listarMovimientosIngreso() {
        return movimientoRepository
                .findByTipoMovimientoOrderByFechaDesc("Ingreso");
    }

    public List<Movimiento> listarTodosLosMovimientos() {
        return movimientoRepository.findAll();
    }

    private String limitarObservacion(String observacion) {
        if (observacion == null || observacion.isBlank()) {
            return null;
        }

        String texto = observacion.trim();

        return texto.length() <= 255
                ? texto
                : texto.substring(0, 255);
    }
}
