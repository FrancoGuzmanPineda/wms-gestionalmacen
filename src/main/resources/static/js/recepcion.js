document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("recepcionForm");
    const productoSelect = document.getElementById("idProducto");
    const ubicacionSelect = document.getElementById("idUbicacion");
    const cantidadInput = document.getElementById("cantidad");
    const productoAyuda = document.getElementById("productoAyuda");
    const ubicacionAyuda = document.getElementById("ubicacionAyuda");
    const botonRegistrar = document.getElementById("btnRegistrarRecepcion");

    const resumenEstado = document.getElementById("resumenEstado");
    const resumenProducto = document.getElementById("resumenProducto");
    const resumenCodigoProducto = document.getElementById("resumenCodigoProducto");
    const resumenUbicacion = document.getElementById("resumenUbicacion");
    const resumenDetalleUbicacion = document.getElementById("resumenDetalleUbicacion");
    const resumenStockTotalActual = document.getElementById("resumenStockTotalActual");
    const resumenStockUbicacionActual = document.getElementById("resumenStockUbicacionActual");
    const resumenCantidadIngreso = document.getElementById("resumenCantidadIngreso");
    const resumenStockUbicacionResultado = document.getElementById("resumenStockUbicacionResultado");
    const resumenStockTotalResultado = document.getElementById("resumenStockTotalResultado");

    let stockUbicacionActual = 0;
    let consultaStockEnCurso = false;
    let consultaStockFallida = false;
    let numeroConsulta = 0;

    const formatearNumero = (valor) =>
        new Intl.NumberFormat("es-PE").format(valor);

    const obtenerOpcionProducto = () =>
        productoSelect?.selectedOptions?.[0] || null;

    const obtenerOpcionUbicacion = () =>
        ubicacionSelect?.selectedOptions?.[0] || null;

    const obtenerCantidadValida = () => {
        const valor = Number.parseInt(cantidadInput?.value || "", 10);
        return Number.isInteger(valor) && valor > 0 ? valor : null;
    };

    const obtenerUnidad = () => {
        const opcion = obtenerOpcionProducto();
        return opcion?.dataset?.unidad || "unidades";
    };

    const actualizarProducto = () => {
        if (!productoSelect || !productoAyuda) {
            return;
        }

        const opcion = obtenerOpcionProducto();
        if (!opcion || !opcion.value) {
            productoAyuda.textContent =
                "Seleccione un producto para consultar su stock total actual.";
            return;
        }

        const stock = Number.parseInt(opcion.dataset.stock || "0", 10) || 0;
        const unidad = opcion.dataset.unidad || "unidades";
        productoAyuda.textContent =
            `Stock total actual: ${formatearNumero(stock)} ${unidad}.`;
    };

    const actualizarUbicacion = () => {
        if (!ubicacionSelect || !ubicacionAyuda) {
            return;
        }

        const opcion = obtenerOpcionUbicacion();
        if (!opcion || !opcion.value) {
            ubicacionAyuda.textContent =
                "Seleccione la ubicación donde quedará registrado el stock.";
            return;
        }

        const tipo = opcion.dataset.tipo || "Sin clasificación";
        const pasillo = opcion.dataset.pasillo || "-";
        ubicacionAyuda.textContent =
            `Pasillo: ${pasillo}. Tipo de ubicación: ${tipo}.`;
    };

    const actualizarResumen = () => {
        const producto = obtenerOpcionProducto();
        const ubicacion = obtenerOpcionUbicacion();
        const cantidad = obtenerCantidadValida();
        const unidad = obtenerUnidad();

        const productoSeleccionado = Boolean(producto?.value);
        const ubicacionSeleccionada = Boolean(ubicacion?.value);

        const stockTotalActual = productoSeleccionado
            ? Number.parseInt(producto.dataset.stock || "0", 10) || 0
            : null;

        if (resumenProducto) {
            resumenProducto.textContent = productoSeleccionado
                ? producto.dataset.nombre || producto.textContent.trim()
                : "Sin seleccionar";
        }

        if (resumenCodigoProducto) {
            resumenCodigoProducto.textContent = productoSeleccionado
                ? `Código: ${producto.dataset.codigo || "-"}`
                : "Código: -";
        }

        if (resumenUbicacion) {
            resumenUbicacion.textContent = ubicacionSeleccionada
                ? ubicacion.dataset.codigo || ubicacion.textContent.trim()
                : "Sin seleccionar";
        }

        if (resumenDetalleUbicacion) {
            const pasillo = ubicacion?.dataset?.pasillo || "-";
            const tipo = ubicacion?.dataset?.tipo || "-";
            resumenDetalleUbicacion.textContent = ubicacionSeleccionada
                ? `Pasillo: ${pasillo} · Tipo: ${tipo}`
                : "Pasillo y tipo: -";
        }

        if (resumenStockTotalActual) {
            resumenStockTotalActual.textContent = stockTotalActual === null
                ? "-"
                : `${formatearNumero(stockTotalActual)} ${unidad}`;
        }

        if (resumenCantidadIngreso) {
            resumenCantidadIngreso.textContent = cantidad === null
                ? "-"
                : `${formatearNumero(cantidad)} ${unidad}`;
        }

        if (resumenStockUbicacionActual) {
            if (!productoSeleccionado || !ubicacionSeleccionada) {
                resumenStockUbicacionActual.textContent = "-";
            } else if (consultaStockEnCurso) {
                resumenStockUbicacionActual.textContent = "Consultando...";
            } else if (consultaStockFallida) {
                resumenStockUbicacionActual.textContent = "No disponible";
            } else {
                resumenStockUbicacionActual.textContent =
                    `${formatearNumero(stockUbicacionActual)} ${unidad}`;
            }
        }

        const puedeCalcular = productoSeleccionado
            && ubicacionSeleccionada
            && cantidad !== null
            && !consultaStockEnCurso
            && !consultaStockFallida;

        if (resumenStockUbicacionResultado) {
            resumenStockUbicacionResultado.textContent = puedeCalcular
                ? `${formatearNumero(stockUbicacionActual + cantidad)} ${unidad}`
                : "-";
        }

        if (resumenStockTotalResultado) {
            resumenStockTotalResultado.textContent = puedeCalcular
                ? `${formatearNumero(stockTotalActual + cantidad)} ${unidad}`
                : "-";
        }

        if (resumenEstado) {
            resumenEstado.classList.remove(
                "preview-status-ready",
                "preview-status-loading",
                "preview-status-error"
            );

            if (!productoSeleccionado || !ubicacionSeleccionada || cantidad === null) {
                resumenEstado.textContent =
                    "Complete producto, ubicación y cantidad para revisar el resultado.";
            } else if (consultaStockEnCurso) {
                resumenEstado.textContent =
                    "Consultando el stock actual de la ubicación seleccionada...";
                resumenEstado.classList.add("preview-status-loading");
            } else if (consultaStockFallida) {
                resumenEstado.textContent =
                    "No fue posible consultar el stock de la ubicación. Puede volver a seleccionar los datos.";
                resumenEstado.classList.add("preview-status-error");
            } else {
                resumenEstado.textContent =
                    "Revise el stock actual y el resultado antes de registrar la recepción.";
                resumenEstado.classList.add("preview-status-ready");
            }
        }
    };

    const consultarStockUbicacion = async () => {
        const idProducto = productoSelect?.value;
        const idUbicacion = ubicacionSelect?.value;
        const consultaActual = ++numeroConsulta;

        stockUbicacionActual = 0;
        consultaStockFallida = false;

        if (!idProducto || !idUbicacion) {
            consultaStockEnCurso = false;
            actualizarResumen();
            return;
        }

        consultaStockEnCurso = true;
        actualizarResumen();

        try {
            const respuesta = await fetch(
                `/recepcion/api/productos/${encodeURIComponent(idProducto)}`
                    + `/ubicaciones/${encodeURIComponent(idUbicacion)}/stock`,
                {
                    headers: {
                        Accept: "application/json"
                    },
                    cache: "no-store"
                }
            );

            if (!respuesta.ok) {
                throw new Error("No se pudo consultar el stock de la ubicación.");
            }

            const cantidad = Number(await respuesta.json());

            if (consultaActual !== numeroConsulta) {
                return;
            }

            stockUbicacionActual = Number.isFinite(cantidad) && cantidad >= 0
                ? cantidad
                : 0;
            consultaStockFallida = false;
        } catch (error) {
            if (consultaActual !== numeroConsulta) {
                return;
            }

            stockUbicacionActual = 0;
            consultaStockFallida = true;
            console.error(error);
        } finally {
            if (consultaActual === numeroConsulta) {
                consultaStockEnCurso = false;
                actualizarResumen();
            }
        }
    };

    productoSelect?.addEventListener("change", () => {
        actualizarProducto();
        consultarStockUbicacion();
    });

    ubicacionSelect?.addEventListener("change", () => {
        actualizarUbicacion();
        consultarStockUbicacion();
    });

    cantidadInput?.addEventListener("input", actualizarResumen);

    form?.addEventListener("submit", (event) => {
        if (!form.checkValidity()) {
            return;
        }

        if (form.dataset.enviando === "true") {
            event.preventDefault();
            return;
        }

        form.dataset.enviando = "true";

        if (botonRegistrar) {
            botonRegistrar.disabled = true;
            botonRegistrar.textContent = "Registrando...";
        }
    });

    actualizarProducto();
    actualizarUbicacion();
    actualizarResumen();
    consultarStockUbicacion();
});
