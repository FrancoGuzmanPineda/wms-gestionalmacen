package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.repository.MovimientoRepository;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public ReporteService(
            MovimientoRepository movimientoRepository,
            ProductoRepository productoRepository
    ) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    public List<Movimiento> obtenerEntradas(
            LocalDate inicio,
            LocalDate fin
    ) {

        if (inicio != null && fin != null) {
            return movimientoRepository
                    .buscarEntradasPorFecha(inicio, fin);
        }

        return movimientoRepository
                .findByTipoMovimiento("Ingreso");
    }

    public List<Movimiento> obtenerSalidas(
            LocalDate inicio,
            LocalDate fin
    ) {

        if (inicio != null && fin != null) {
            return movimientoRepository
                    .buscarSalidasPorFecha(inicio, fin);
        }

        return movimientoRepository
                .findByTipoMovimiento("Salida");
    }

    public List<Producto> obtenerInventario(String buscar) {

        if (buscar != null && !buscar.isBlank()) {
            return productoRepository.buscarInventario(buscar);
        }

        return productoRepository.findAll();
    }
}