package com.wms.gestionalmaceng01.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wms.gestionalmaceng01.models.Despacho;
import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.models.TipoMovimiento;
import com.wms.gestionalmaceng01.models.Ubicacion;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.DespachoRepository;
import com.wms.gestionalmaceng01.repository.MovimientoRepository;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import com.wms.gestionalmaceng01.repository.TipoMovimientoRepository;
import com.wms.gestionalmaceng01.repository.UbicacionRepository;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Service
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final InventarioUbicacionService inventarioUbicacionService;

    public DespachoService(
            DespachoRepository despachoRepository,
            MovimientoRepository movimientoRepository,
            ProductoRepository productoRepository,
            UbicacionRepository ubicacionRepository,
            UsuarioRepository usuarioRepository,
            TipoMovimientoRepository tipoMovimientoRepository,
            InventarioUbicacionService inventarioUbicacionService
    ) {
        this.despachoRepository = despachoRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
        this.inventarioUbicacionService = inventarioUbicacionService;
    }

    public List<Despacho> listar() {
        return despachoRepository.findAll();
    }

    @Transactional
    public Despacho registrarDespacho(
            Integer idProducto,
            Integer idUbicacion,
            String correoUsuario,
            Despacho despacho
    ) {
        Integer cantidad = despacho.getCantidad();

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a cero."
            );
        }

        Producto producto = productoRepository.buscarPorIdParaActualizar(idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado."
                ));

        if (!"Activo".equalsIgnoreCase(producto.getEstado())) {
            throw new IllegalArgumentException(
                    "El producto seleccionado está inactivo."
            );
        }

        Ubicacion ubicacion = ubicacionRepository.findById(idUbicacion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ubicación no encontrada."
                ));

        if (!"Activo".equalsIgnoreCase(ubicacion.getEstado())) {
            throw new IllegalArgumentException(
                    "La ubicación seleccionada está inactiva."
            );
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario autenticado no encontrado."
                ));

        if (!usuario.isEstado()) {
            throw new IllegalArgumentException(
                    "El usuario autenticado está inactivo."
            );
        }

        TipoMovimiento tipoSalida = tipoMovimientoRepository
                .findByNombreTipoMovimiento("Salida")
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el tipo de movimiento Salida."
                ));

        /*
         * La salida se valida contra la ubicación seleccionada, no solamente
         * contra el stock total del producto. Si no hay suficientes unidades
         * en esa ubicación, la operación se rechaza.
         */
        inventarioUbicacionService.disminuirStock(
                producto,
                ubicacion,
                cantidad
        );

        LocalDateTime fechaOperacion = LocalDateTime.now();

        despacho.setProducto(producto);
        despacho.setUbicacion(ubicacion);
        despacho.setUsuario(usuario);
        despacho.setFechaSalida(fechaOperacion);
        despacho.setEstado("Completado");

        Despacho despachoGuardado = despachoRepository.save(despacho);

        Movimiento movimiento = new Movimiento();
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(fechaOperacion);
        movimiento.setTipoMovimiento("Salida");
        movimiento.setTipoMovimientoRef(tipoSalida);
        movimiento.setProducto(producto);
        movimiento.setUbicacion(ubicacion);
        movimiento.setUsuario(usuario);
        movimiento.setObservacion(construirObservacion(despacho));
        movimiento.setEstado("Completado");

        movimientoRepository.save(movimiento);

        return despachoGuardado;
    }

    private String construirObservacion(Despacho despacho) {
        StringBuilder observacion = new StringBuilder(
                "Despacho registrado"
        );

        if (despacho.getEmpresaDestino() != null
                && !despacho.getEmpresaDestino().isBlank()) {
            observacion.append(" | Destino: ")
                    .append(despacho.getEmpresaDestino().trim());
        }

        if (despacho.getGuiaRemision() != null
                && !despacho.getGuiaRemision().isBlank()) {
            observacion.append(" | Guía: ")
                    .append(despacho.getGuiaRemision().trim());
        }

        if (despacho.getObservaciones() != null
                && !despacho.getObservaciones().isBlank()) {
            observacion.append(" | ")
                    .append(despacho.getObservaciones().trim());
        }

        String resultado = observacion.toString();
        return resultado.length() <= 255
                ? resultado
                : resultado.substring(0, 255);
    }
}
