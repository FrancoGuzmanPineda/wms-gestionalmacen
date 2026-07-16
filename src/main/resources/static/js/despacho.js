document.addEventListener("DOMContentLoaded", () => {
    const productoSelect = document.getElementById("idProducto");
    const ubicacionSelect = document.getElementById("idUbicacion");
    const cantidadInput = document.getElementById("cantidad");
    const stockAyuda = document.getElementById("stockUbicacionAyuda");
    const cantidadAyuda = document.getElementById("cantidadAyuda");
    const botonRegistrar = document.getElementById("btnRegistrarDespacho");

    if (!productoSelect || !ubicacionSelect || !cantidadInput) {
        return;
    }

    const establecerEstadoCarga = (cargando) => {
        productoSelect.disabled = cargando;
        ubicacionSelect.disabled = cargando;
        cantidadInput.disabled = cargando;

        if (botonRegistrar) {
            botonRegistrar.disabled = cargando;
        }
    };

    const restablecerCantidad = () => {
        cantidadInput.removeAttribute("max");
        cantidadInput.value = "";
        cantidadInput.disabled = true;
        cantidadAyuda.textContent =
            "Seleccione una ubicación para conocer el stock disponible.";
    };

    const reemplazarOpciones = (mensaje) => {
        ubicacionSelect.innerHTML = "";
        const opcion = document.createElement("option");
        opcion.value = "";
        opcion.textContent = mensaje;
        ubicacionSelect.appendChild(opcion);
    };

    const actualizarStockSeleccionado = () => {
        const opcion = ubicacionSelect.selectedOptions[0];
        const stockDisponible = Number(opcion?.dataset.stock || 0);

        if (!stockDisponible) {
            restablecerCantidad();
            if (botonRegistrar) {
                botonRegistrar.disabled = true;
            }
            return;
        }

        cantidadInput.disabled = false;
        cantidadInput.max = String(stockDisponible);
        cantidadInput.value = "";
        cantidadAyuda.textContent =
            `Stock disponible en esta ubicación: ${stockDisponible}.`;

        if (botonRegistrar) {
            botonRegistrar.disabled = false;
        }
    };

    const cargarUbicacionesDelProducto = async () => {
        const idProducto = productoSelect.value;
        restablecerCantidad();

        if (!idProducto) {
            reemplazarOpciones("Primero seleccione un producto");
            ubicacionSelect.disabled = true;
            stockAyuda.textContent =
                "Solo se mostrarán ubicaciones con stock disponible.";
            if (botonRegistrar) {
                botonRegistrar.disabled = true;
            }
            return;
        }

        establecerEstadoCarga(true);
        reemplazarOpciones("Cargando ubicaciones...");
        stockAyuda.textContent =
            "Consultando el stock disponible del producto seleccionado.";

        try {
            const respuesta = await fetch(
                `/despacho/api/productos/${encodeURIComponent(idProducto)}/ubicaciones`,
                {
                    method: "GET",
                    headers: { "Accept": "application/json" },
                    cache: "no-store"
                }
            );

            if (!respuesta.ok) {
                throw new Error("No se pudo consultar el stock por ubicación.");
            }

            const ubicaciones = await respuesta.json();

            if (!Array.isArray(ubicaciones) || ubicaciones.length === 0) {
                reemplazarOpciones("El producto no tiene stock disponible");
                ubicacionSelect.disabled = true;
                stockAyuda.textContent =
                    "Registre una recepción para asignar stock a una ubicación.";
                if (botonRegistrar) {
                    botonRegistrar.disabled = true;
                }
                return;
            }

            ubicacionSelect.innerHTML = "";

            ubicaciones.forEach((ubicacion) => {
                const opcion = document.createElement("option");
                opcion.value = ubicacion.idUbicacion;
                opcion.dataset.stock = ubicacion.cantidad;

                const detalle = [ubicacion.codigo, ubicacion.pasillo]
                    .filter(Boolean)
                    .join(" - ");

                opcion.textContent =
                    `${detalle} | Disponible: ${ubicacion.cantidad}`;
                ubicacionSelect.appendChild(opcion);
            });

            ubicacionSelect.disabled = false;
            ubicacionSelect.selectedIndex = 0;
            stockAyuda.textContent =
                "La ubicación se carga según el producto seleccionado.";
            actualizarStockSeleccionado();
        } catch (error) {
            console.error(error);
            reemplazarOpciones("No se pudieron cargar las ubicaciones");
            ubicacionSelect.disabled = true;
            stockAyuda.textContent =
                "Revise la conexión con el servidor e intente nuevamente.";
            if (botonRegistrar) {
                botonRegistrar.disabled = true;
            }
        } finally {
            productoSelect.disabled = false;
        }
    };

    ubicacionSelect.addEventListener("change", actualizarStockSeleccionado);
    productoSelect.addEventListener("change", cargarUbicacionesDelProducto);

    cargarUbicacionesDelProducto();
});

/* ===========================================================
   GENERAR GUÍA DE REMISIÓN ALEATORIA
=========================================================== */

document.addEventListener("DOMContentLoaded", () => {

    const btnGenerarGuia = document.getElementById("btnGenerarGuia");
    const guiaRemisionInput = document.getElementById("guiaRemision");

    if (!btnGenerarGuia || !guiaRemisionInput) return;

    btnGenerarGuia.addEventListener("click", () => {
        const numero = Math.floor(100000 + Math.random() * 900000);
        guiaRemisionInput.value = `GR-${numero}`;
    });

});

/* ===========================================================
   MODAL EDITAR DESPACHO
=========================================================== */

document.addEventListener("DOMContentLoaded", () => {

    const modal = document.getElementById("modalEditar");

    if (!modal) return;

    const cerrar = document.getElementById("cerrarModal");

    const formulario = document.getElementById("formEditar");

    const guia = document.getElementById("editGuia");
    const empresa = document.getElementById("editEmpresa");
    const conductor = document.getElementById("editConductor");
    const placa = document.getElementById("editPlaca");
    const salida = document.getElementById("editSalida");
    const llegada = document.getElementById("editLlegada");
    const estado = document.getElementById("editEstado");
    const observacion = document.getElementById("editObservacion");

    function convertirFecha(fecha){

        if(!fecha) return "";

        return fecha.substring(0,16);

    }

    document.querySelectorAll(".btnEditar").forEach(btn=>{

        btn.addEventListener("click",async()=>{

            const id=btn.dataset.id;

            const res=await fetch("/despacho/buscar/"+id);

            const d=await res.json();

            formulario.action="/despacho/editar/"+id;

            guia.value=d.guiaRemision||"";
            empresa.value=d.empresaDestino||"";
            conductor.value=d.conductor||"";
            placa.value=d.placaVehiculo||"";
            salida.value=convertirFecha(d.fechaSalida);
            llegada.value=convertirFecha(d.fechaLlegada);
            estado.value=d.estado;
            observacion.value=d.observaciones||"";

            modal.style.display="flex";

        });

    });

    cerrar.onclick=function(){

        modal.style.display="none";

    }

    window.onclick=function(e){

        if(e.target===modal){

            modal.style.display="none";

        }

    }

});