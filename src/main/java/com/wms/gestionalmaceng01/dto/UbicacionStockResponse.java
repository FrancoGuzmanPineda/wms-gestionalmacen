package com.wms.gestionalmaceng01.dto;

public class UbicacionStockResponse {

    private final Integer idUbicacion;
    private final String codigo;
    private final String pasillo;
    private final String tipoUbicacion;
    private final Integer cantidad;

    public UbicacionStockResponse(
            Integer idUbicacion,
            String codigo,
            String pasillo,
            String tipoUbicacion,
            Integer cantidad
    ) {
        this.idUbicacion = idUbicacion;
        this.codigo = codigo;
        this.pasillo = pasillo;
        this.tipoUbicacion = tipoUbicacion;
        this.cantidad = cantidad;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getPasillo() {
        return pasillo;
    }

    public String getTipoUbicacion() {
        return tipoUbicacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }
}
