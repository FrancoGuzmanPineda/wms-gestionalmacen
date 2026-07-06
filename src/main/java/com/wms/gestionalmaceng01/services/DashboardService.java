package com.wms.gestionalmaceng01.services;

import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.repository.MovimientoRepository;
import com.wms.gestionalmaceng01.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public DashboardService(
            ProductoRepository productoRepository,
            MovimientoRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // Obtiene los indicadores principales del dashboard desde MySQL.
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerIndicadores() {

        List<Producto> productosActivos = productoRepository.findByEstado("Activo");

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        long totalProductos = productosActivos.size();

        int stockTotal = productosActivos.stream()
                .mapToInt(this::stockActual)
                .sum();

        long productosSinStock = productosActivos.stream()
                .filter(producto -> stockActual(producto) <= 0)
                .count();

        long productosStockBajo = productosActivos.stream()
                .filter(this::estaDebajoDelMinimo)
                .count();

        Long entradasDia = movimientoRepository.sumarCantidadPorTipoEntreFechas(
                "Ingreso",
                inicioDia,
                finDia);

        Long salidasDia = movimientoRepository.sumarCantidadPorTipoEntreFechas(
                "Salida",
                inicioDia,
                finDia);

        BigDecimal valorInventario = productosActivos.stream()
                .map(producto -> costo(producto)
                        .multiply(BigDecimal.valueOf(stockActual(producto))))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> indicadores = new HashMap<>();

        indicadores.put("totalProductos", totalProductos);
        indicadores.put("stockTotal", stockTotal);
        indicadores.put("entradasDia", valorLong(entradasDia));
        indicadores.put("salidasDia", valorLong(salidasDia));
        indicadores.put("productosSinStock", productosSinStock);
        indicadores.put("productosStockBajo", productosStockBajo);
        indicadores.put("valorInventario", "S/ " + valorInventario);

        return indicadores;
    }

    // Lista los últimos movimientos para la tabla del dashboard.
    @Transactional(readOnly = true)
    public List<Movimiento> obtenerUltimosMovimientos() {
        return movimientoRepository.findTop10ByEstadoOrderByFechaDesc(
                "Completado");
    }

    // Lista productos que están en stock crítico.
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosCriticos() {
        return productoRepository.findByEstado("Activo")
                .stream()
                .filter(this::estaDebajoDelMinimo)
                .sorted(Comparator.comparingInt(this::stockActual))
                .limit(10)
                .toList();
    }

    // Valida si el producto está por debajo o igual al stock mínimo.
    private boolean estaDebajoDelMinimo(Producto producto) {
        int minimo = stockMinimo(producto);

        return minimo > 0 && stockActual(producto) <= minimo;
    }

    // Evita errores si el stock viene null desde la base de datos.
    private int stockActual(Producto producto) {
        return producto.getStockActual() == null
                ? 0
                : producto.getStockActual();
    }

    // Evita errores si el stock mínimo viene null.
    private int stockMinimo(Producto producto) {
        return producto.getStockMinimo() == null
                ? 0
                : producto.getStockMinimo();
    }

    // Evita errores si el costo viene null.
    private BigDecimal costo(Producto producto) {
        return producto.getCosto() == null
                ? BigDecimal.ZERO
                : producto.getCosto();
    }

    // Evita errores si la suma de movimientos retorna null.
    private long valorLong(Long valor) {
        return valor == null ? 0L : valor;
    }

    // Obtiene los datos reales para los gráficos del dashboard.
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerDatosGraficos() {

        Map<String, Object> datos = new HashMap<>();

        LocalDate hoy = LocalDate.now();

        // Fecha final: mañana a las 00:00 para incluir todo el día actual.
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        // Rango principal de análisis: últimos 30 días.
        LocalDate inicioUltimos30Dias = hoy.minusDays(29);
        LocalDateTime inicio30Dias = inicioUltimos30Dias.atStartOfDay();

        List<Movimiento> movimientosUltimos30Dias = movimientoRepository.findByEstadoAndFechaBetweenOrderByFechaAsc(
                "Completado",
                inicio30Dias,
                fin);

        DateTimeFormatter formatoDia = DateTimeFormatter.ofPattern("dd/MM");

        // ==============================
        // GRÁFICO 1: ENTRADAS VS SALIDAS
        // Últimos 30 días
        // ==============================

        List<String> labelsEntradasSalidas = new ArrayList<>();
        List<Integer> dataEntradas = new ArrayList<>();
        List<Integer> dataSalidas = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate fecha = inicioUltimos30Dias.plusDays(i);

            int totalEntradas = movimientosUltimos30Dias.stream()
                    .filter(movimiento -> perteneceAFecha(movimiento, fecha))
                    .filter(movimiento -> esTipo(movimiento, "Ingreso"))
                    .mapToInt(this::cantidadMovimiento)
                    .sum();

            int totalSalidas = movimientosUltimos30Dias.stream()
                    .filter(movimiento -> perteneceAFecha(movimiento, fecha))
                    .filter(movimiento -> esTipo(movimiento, "Salida"))
                    .mapToInt(this::cantidadMovimiento)
                    .sum();

            labelsEntradasSalidas.add(fecha.format(formatoDia));
            dataEntradas.add(totalEntradas);
            dataSalidas.add(totalSalidas);
        }

        datos.put("graficoEntradasSalidasLabels", labelsEntradasSalidas);
        datos.put("graficoEntradasData", dataEntradas);
        datos.put("graficoSalidasData", dataSalidas);

        // ==============================
        // GRÁFICO 2: ESTADO DEL INVENTARIO
        // Alto / Medio / Bajo
        // ==============================

        List<Producto> productosActivos = productoRepository.findByEstado("Activo");

        int stockAlto = 0;
        int stockMedio = 0;
        int stockBajo = 0;

        for (Producto producto : productosActivos) {
            int stock = stockActual(producto);
            int minimo = stockMinimo(producto);

            if (stock <= minimo) {
                stockBajo++;
            } else if (minimo > 0 && stock <= minimo * 2) {
                stockMedio++;
            } else {
                stockAlto++;
            }
        }

        datos.put("graficoEstadoLabels", List.of("Alto", "Medio", "Bajo"));
        datos.put("graficoEstadoData", List.of(stockAlto, stockMedio, stockBajo));

        // ==============================
        // GRÁFICO 3: TOP 5 PRODUCTOS MÁS MOVIDOS
        // Según movimientos de los últimos 30 días
        // ==============================

        Map<String, Integer> productosMovidos = movimientosUltimos30Dias.stream()
                .filter(movimiento -> movimiento.getProducto() != null)
                .collect(Collectors.groupingBy(
                        movimiento -> movimiento.getProducto().getNombre(),
                        Collectors.summingInt(this::cantidadMovimiento)));

        Map<String, Integer> topProductos = productosMovidos.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (valorAntiguo, valorNuevo) -> valorAntiguo,
                        LinkedHashMap::new));

        datos.put("graficoTopProductosLabels", new ArrayList<>(topProductos.keySet()));
        datos.put("graficoTopProductosData", new ArrayList<>(topProductos.values()));

        // ==============================
        // GRÁFICO 4: TENDENCIA DE MOVIMIENTOS
        // Total de movimientos por día en los últimos 30 días
        // ==============================

        List<String> labelsTendencia = new ArrayList<>();
        List<Integer> dataTendencia = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate fecha = inicioUltimos30Dias.plusDays(i);

            int totalMovimientosDia = movimientosUltimos30Dias.stream()
                    .filter(movimiento -> perteneceAFecha(movimiento, fecha))
                    .mapToInt(this::cantidadMovimiento)
                    .sum();

            labelsTendencia.add(fecha.format(formatoDia));
            dataTendencia.add(totalMovimientosDia);
        }

        datos.put("graficoTendenciaLabels", labelsTendencia);
        datos.put("graficoTendenciaData", dataTendencia);

        return datos;
    }

    // Verifica si un movimiento pertenece a una fecha exacta.
    private boolean perteneceAFecha(Movimiento movimiento, LocalDate fecha) {
        return movimiento.getFecha() != null
                && movimiento.getFecha().toLocalDate().equals(fecha);
    }

    // Verifica si el movimiento es Ingreso o Salida.
    private boolean esTipo(Movimiento movimiento, String tipo) {
        return movimiento.getTipoMovimiento() != null
                && movimiento.getTipoMovimiento().equalsIgnoreCase(tipo);
    }

    // Evita errores si la cantidad viene null.
    private int cantidadMovimiento(Movimiento movimiento) {
        return movimiento.getCantidad() == null
                ? 0
                : movimiento.getCantidad();
    }
}