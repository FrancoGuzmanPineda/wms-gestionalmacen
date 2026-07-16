package com.wms.gestionalmaceng01.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "inventario_ubicaciones",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inventario_producto_ubicacion",
                columnNames = {"id_producto", "id_ubicacion"}
        )
)
public class InventarioUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario_ubicacion")
    private Integer idInventarioUbicacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;

    public InventarioUbicacion() {
    }

    public InventarioUbicacion(
            Producto producto,
            Ubicacion ubicacion,
            Integer cantidad
    ) {
        this.producto = producto;
        this.ubicacion = ubicacion;
        this.cantidad = cantidad;
    }

    public Integer getIdInventarioUbicacion() {
        return idInventarioUbicacion;
    }

    public void setIdInventarioUbicacion(Integer idInventarioUbicacion) {
        this.idInventarioUbicacion = idInventarioUbicacion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
