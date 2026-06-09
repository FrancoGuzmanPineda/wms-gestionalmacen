// Variables globales
let rolSeleccionado = null;

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    console.log("✅ login.js cargado correctamente");
    
    // Configurar listeners para roles
    const roles = document.querySelectorAll('.role-option');
    console.log(`📌 Roles encontrados: ${roles.length}`);
    
    roles.forEach(role => {
        role.addEventListener('click', function() {
            // Remover selección anterior
            roles.forEach(r => r.classList.remove('selected'));
            // Seleccionar nuevo rol
            this.classList.add('selected');
            
            const rol = this.dataset.rol;
            rolSeleccionado = rol;
            console.log(`🖱️ Rol seleccionado: ${rol}`);
            
            // Mostrar formulario de login
            document.getElementById('loginFormContainer').style.display = 'block';
            
            // Mostrar botón de admin si corresponde
            const adminActions = document.getElementById('adminActions');
            if (rol === 'ADMIN') {
                adminActions.style.display = 'block';
            } else {
                adminActions.style.display = 'none';
            }
            
            // Cambiar color del header
            const header = document.getElementById('cardHeader');
            const color = this.dataset.color;
            header.className = 'card-header ' + color;
        });
    });
    
    // Configurar envío del formulario
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            if (!rolSeleccionado) {
                e.preventDefault();
                mostrarError('Por favor, selecciona un rol primero');
                return false;
            }
            
            document.getElementById('inputRolOculto').value = rolSeleccionado;

            console.log(`📝 Enviando login con rol: ${rolSeleccionado}`);
            // Aquí puedes agregar el rol al formulario si es necesario
            // this.action = `/login?rol=${rolSeleccionado}`;
        });
    }
});

// Función global para abrir modal de cambio de contraseña
function cambiarPassword() {
    const email = document.getElementById('cambioEmail').value;
    const actual = document.getElementById('actualPassword').value;
    const nueva = document.getElementById('nuevaPassword').value;
    const confirmar = document.getElementById('confirmarPassword').value;
    
    if (!email || !actual || !nueva || !confirmar) {
        alert('Por favor, completa todos los campos');
        return;
    }
    
    if (nueva !== confirmar) {
        alert('Las contraseñas nuevas no coinciden');
        return;
    }
    
    console.log('🔐 Cambiando contraseña para:', email);
    
    // Aquí llamarías a tu API
    fetch('/api/cambiar-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, actual, nueva })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Contraseña actualizada correctamente');
            // Cerrar modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalPassword'));
            modal.hide();
            // Limpiar campos
            document.getElementById('cambioEmail').value = '';
            document.getElementById('actualPassword').value = '';
            document.getElementById('nuevaPassword').value = '';
            document.getElementById('confirmarPassword').value = '';
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al cambiar la contraseña');
    });
}

// Función global para abrir modal de usuario
function abrirModalUsuario() {
    console.log('➕ Abriendo modal para crear usuario');
    const modal = new bootstrap.Modal(document.getElementById('modalUsuario'));
    modal.show();
}

// Función global para crear usuario
function crearUsuario() {
    const nombre = document.getElementById('nuevoNombre').value;
    const email = document.getElementById('nuevoEmail').value;
    const password = document.getElementById('nuevoPassword').value;
    const rol = document.getElementById('nuevoRol').value;
    
    if (!nombre || !email || !password) {
        alert('Por favor, completa todos los campos');
        return;
    }
    
    console.log('👤 Creando usuario:', { nombre, email, rol });
    
    // Aquí llamarías a tu API
    fetch('/api/crear-usuario', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, email, password, rol })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Usuario creado correctamente');
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalUsuario'));
            modal.hide();
            // Limpiar campos
            document.getElementById('nuevoNombre').value = '';
            document.getElementById('nuevoEmail').value = '';
            document.getElementById('nuevoPassword').value = '';
            document.getElementById('nuevoRol').value = 'USER';
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al crear usuario');
    });
}

// Función para mostrar errores
function mostrarError(mensaje) {
    const errorMsg = document.getElementById('errorMsg');
    errorMsg.textContent = mensaje;
    errorMsg.style.display = 'block';
    setTimeout(() => {
        errorMsg.style.display = 'none';
    }, 3000);
}

// Agregar estilos CSS para la clase 'selected' si no existen
const style = document.createElement('style');
style.textContent = `
    .role-option {
        cursor: pointer;
        transition: all 0.3s ease;
    }
    .role-option.selected {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.2);
        border: 2px solid #007bff;
    }
    .role-option.selected i {
        transform: scale(1.1);
    }
`;
document.head.appendChild(style);