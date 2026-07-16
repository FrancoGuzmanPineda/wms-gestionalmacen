package com.wms.gestionalmaceng01.models;

import jakarta.persistence.*;

@Entity
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;

    @Column(name = "codigo_estante", nullable = false, length = 50)
    private String codigoEstante;

    @Column(name = "pasillo", length = 50)
    private String pasillo;

    @Column(name = "tipo_ubicacion", length = 50)
    private String tipoUbicacion;

    @Column(name = "estado", length = 20)
    private String estado = "Activo";

    public Ubicacion() {
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getCodigoEstante() {
        return codigoEstante;
    }

    public void setCodigoEstante(String codigoEstante) {
        this.codigoEstante = codigoEstante;
    }

    public String getPasillo() {
        return pasillo;
    }

    public void setPasillo(String pasillo) {
        this.pasillo = pasillo;
    }

    public String getTipoUbicacion() {
        return tipoUbicacion;
    }

    public void setTipoUbicacion(String tipoUbicacion) {
        this.tipoUbicacion = tipoUbicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}