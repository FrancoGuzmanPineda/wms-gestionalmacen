package com.wms.gestionalmaceng01.services;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Producto;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DashboardPdfService {

    private final DashboardService dashboardService;

    public DashboardPdfService(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Genera el PDF con información actualizada desde MySQL.
    public byte[] generarPdfDashboard(String usuarioLogueado) {

        try {
            Map<String, Object> indicadores = dashboardService.obtenerIndicadores();
            List<Movimiento> ultimosMovimientos = dashboardService.obtenerUltimosMovimientos();
            List<Producto> productosCriticos = dashboardService.obtenerProductosCriticos();

            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            Document documento = new Document(PageSize.A4.rotate(), 36, 36, 32, 32);
            PdfWriter.getInstance(documento, salida);

            documento.open();

            agregarEncabezado(documento, usuarioLogueado);
            agregarKpis(documento, indicadores);
            agregarAlertas(documento, indicadores);
            agregarTablaMovimientos(documento, ultimosMovimientos);
            agregarTablaProductosCriticos(documento, productosCriticos);

            documento.close();

            return salida.toByteArray();

        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF del dashboard.", e);
        }
    }

    // Encabezado principal del PDF.
    private void agregarEncabezado(Document documento, String usuarioLogueado) throws DocumentException {

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(17, 24, 39));
        Font textoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(75, 85, 99));

        Paragraph titulo = new Paragraph("Dashboard Ejecutivo WMS", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        Paragraph subtitulo = new Paragraph(
                "Reporte generado el " + fecha + " | Usuario: " + texto(usuarioLogueado),
                textoFont);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(18);
        documento.add(subtitulo);
    }

    // Tarjetas KPI principales.
    private void agregarKpis(Document documento, Map<String, Object> indicadores) throws DocumentException {

        agregarTituloSeccion(documento, "Indicadores principales");

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[] { 1, 1, 1 });
        tabla.setSpacingAfter(14);

        agregarCeldaKpi(tabla, "Total de productos", valor(indicadores.get("totalProductos")));
        agregarCeldaKpi(tabla, "Stock total", valor(indicadores.get("stockTotal")));
        agregarCeldaKpi(tabla, "Entradas del dia", valor(indicadores.get("entradasDia")));
        agregarCeldaKpi(tabla, "Salidas del dia", valor(indicadores.get("salidasDia")));
        agregarCeldaKpi(tabla, "Stock bajo", valor(indicadores.get("productosStockBajo")));
        agregarCeldaKpi(tabla, "Valor inventario", valor(indicadores.get("valorInventario")));

        documento.add(tabla);
    }

    // Alertas principales del almacén.
    private void agregarAlertas(Document documento, Map<String, Object> indicadores) throws DocumentException {

        agregarTituloSeccion(documento, "Alertas del almacen");

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[] { 1, 1 });
        tabla.setSpacingAfter(14);

        agregarCeldaAlerta(
                tabla,
                "Productos sin stock",
                "Actualmente existen " + valor(indicadores.get("productosSinStock")) + " productos sin existencias.");

        agregarCeldaAlerta(
                tabla,
                "Productos por debajo del minimo",
                "Actualmente existen " + valor(indicadores.get("productosStockBajo"))
                        + " productos que requieren reposicion.");

        documento.add(tabla);
    }

    // Tabla de últimos movimientos.
    private void agregarTablaMovimientos(Document documento, List<Movimiento> movimientos) throws DocumentException {

        agregarTituloSeccion(documento, "Ultimos movimientos de almacen");

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[] { 1.5f, 2.8f, 1.2f, 1f, 1.8f });
        tabla.setSpacingAfter(16);

        agregarCabecera(tabla, "Fecha");
        agregarCabecera(tabla, "Producto");
        agregarCabecera(tabla, "Tipo");
        agregarCabecera(tabla, "Cantidad");
        agregarCabecera(tabla, "Usuario");

        if (movimientos == null || movimientos.isEmpty()) {
            PdfPCell celda = new PdfPCell(new Phrase("No hay movimientos recientes registrados."));
            celda.setColspan(5);
            celda.setPadding(10);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        } else {
            for (Movimiento movimiento : movimientos) {
                tabla.addCell(celdaNormal(formatearFecha(movimiento.getFecha())));
                tabla.addCell(celdaNormal(
                        movimiento.getProducto() != null ? movimiento.getProducto().getNombre() : "-"));
                tabla.addCell(celdaNormal(texto(movimiento.getTipoMovimiento())));
                tabla.addCell(celdaNormal(valor(movimiento.getCantidad())));
                tabla.addCell(celdaNormal(
                        movimiento.getUsuario() != null ? movimiento.getUsuario().getNombre() : "-"));
            }
        }

        documento.add(tabla);
    }

    // Tabla de productos críticos.
    private void agregarTablaProductosCriticos(Document documento, List<Producto> productos) throws DocumentException {

        agregarTituloSeccion(documento, "Productos con stock critico");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[] { 3f, 1f, 1f, 1.5f });

        agregarCabecera(tabla, "Producto");
        agregarCabecera(tabla, "Stock");
        agregarCabecera(tabla, "Minimo");
        agregarCabecera(tabla, "Estado");

        if (productos == null || productos.isEmpty()) {
            PdfPCell celda = new PdfPCell(new Phrase("No hay productos con stock critico actualmente."));
            celda.setColspan(4);
            celda.setPadding(10);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        } else {
            for (Producto producto : productos) {
                int stock = producto.getStockActual() == null ? 0 : producto.getStockActual();

                tabla.addCell(celdaNormal(texto(producto.getNombre())));
                tabla.addCell(celdaNormal(valor(producto.getStockActual())));
                tabla.addCell(celdaNormal(valor(producto.getStockMinimo())));
                tabla.addCell(celdaNormal(stock <= 0 ? "Sin stock" : "Critico"));
            }
        }

        documento.add(tabla);
    }

    // Título de cada sección.
    private void agregarTituloSeccion(Document documento, String titulo) throws DocumentException {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(17, 24, 39));

        Paragraph parrafo = new Paragraph(titulo, fuente);
        parrafo.setSpacingBefore(8);
        parrafo.setSpacingAfter(8);

        documento.add(parrafo);
    }

    // Celda KPI.
    private void agregarCeldaKpi(PdfPTable tabla, String titulo, String valor) {
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(107, 114, 128));
        Font valorFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new Color(17, 24, 39));

        PdfPCell celda = new PdfPCell();
        celda.setPadding(12);
        celda.setBorderColor(new Color(229, 231, 235));

        Paragraph pTitulo = new Paragraph(titulo, tituloFont);
        Paragraph pValor = new Paragraph(valor, valorFont);

        celda.addElement(pTitulo);
        celda.addElement(pValor);

        tabla.addCell(celda);
    }

    // Celda de alerta.
    private void agregarCeldaAlerta(PdfPTable tabla, String titulo, String descripcion) {
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(185, 28, 28));
        Font textoFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(55, 65, 81));

        PdfPCell celda = new PdfPCell();
        celda.setPadding(10);
        celda.setBorderColor(new Color(229, 231, 235));

        celda.addElement(new Paragraph(titulo, tituloFont));
        celda.addElement(new Paragraph(descripcion, textoFont));

        tabla.addCell(celda);
    }

    // Cabecera de tabla.
    private void agregarCabecera(PdfPTable tabla, String texto) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(new Color(15, 98, 214));
        celda.setPadding(8);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);

        tabla.addCell(celda);
    }

    // Celda normal de tabla.
    private PdfPCell celdaNormal(String texto) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(31, 41, 55));

        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(7);
        celda.setBorderColor(new Color(229, 231, 235));

        return celda;
    }

    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            return "-";
        }

        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String valor(Object valor) {
        return valor == null ? "0" : valor.toString();
    }

    private String texto(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }
}