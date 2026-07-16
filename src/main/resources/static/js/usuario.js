document.addEventListener('DOMContentLoaded', function() {
    const eliminarModal = document.getElementById('eliminarModal');
    
    eliminarModal.addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        const id = button.dataset.id;
        const nombre = button.dataset.nombre;
        
        document.getElementById('usuarioNombreEliminar').textContent = nombre;
        document.getElementById('confirmarEliminar').href = '/usuarios/eliminar/' + id;
    });
});