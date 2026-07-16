package com.wms.gestionalmaceng01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.wms.gestionalmaceng01.dto.RecepcionForm;
import com.wms.gestionalmaceng01.dto.UbicacionStockResponse;

class PruebaGradleTests {

    @Test
    void junitFuncionaCorrectamente() {
        assertTrue(true);
    }

    @Test
    void recepcionFormConservaLosDatosIngresados() {
        RecepcionForm formulario = new RecepcionForm();
        formulario.setIdProducto(10);
        formulario.setIdUbicacion(4);
        formulario.setCantidad(25);
        formulario.setObservacion("Ingreso de mercadería");

        assertEquals(10, formulario.getIdProducto());
        assertEquals(4, formulario.getIdUbicacion());
        assertEquals(25, formulario.getCantidad());
        assertEquals("Ingreso de mercadería", formulario.getObservacion());
    }

    @Test
    void ubicacionStockResponseExponeElStockDeLaUbicacion() {
        UbicacionStockResponse respuesta = new UbicacionStockResponse(
                4,
                "A-01",
                "A",
                "Almacenamiento",
                35);

        assertEquals(4, respuesta.getIdUbicacion());
        assertEquals("A-01", respuesta.getCodigo());
        assertEquals("A", respuesta.getPasillo());
        assertEquals("Almacenamiento", respuesta.getTipoUbicacion());
        assertEquals(35, respuesta.getCantidad());
    }
}
