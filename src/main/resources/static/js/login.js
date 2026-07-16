(function () {
    const form = document.getElementById('loginForm');
    const errorDiv = document.getElementById('errorMsg');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const rememberCheck = document.getElementById('rememberMe');

    mostrarMensajeSegunUrl();
    cargarUsuarioRecordado();

    if (form) {
        form.addEventListener('submit', function (event) {
            const usuarioVacio = !username || !username.value.trim();
            const claveVacia = !password || !password.value.trim();

            actualizarEstadoCampo(username, usuarioVacio);
            actualizarEstadoCampo(password, claveVacia);

            if (usuarioVacio || claveVacia) {
                event.preventDefault();
                mostrarErrorTemporal('Por favor complete usuario y contraseña.');
                return;
            }

            guardarUsuarioRecordado();
        });
    }

    function mostrarMensajeSegunUrl() {
        const parametros = new URLSearchParams(window.location.search);

        if (parametros.has('error')) {
            mostrarErrorTemporal('Usuario o contraseña incorrectos. Intente de nuevo.');
            return;
        }

        if (parametros.has('passwordReset')) {
            mostrarExitoTemporal('Contraseña actualizada correctamente. Ya puede iniciar sesión.');
            return;
        }

        if (parametros.has('logout')) {
            const aviso = document.createElement('div');
            aviso.id = 'logoutMessage';
            aviso.className = 'error-message show';
            aviso.style.background = '#e0f2e9';
            aviso.style.borderLeftColor = '#2c7a4d';
            aviso.style.color = '#1e6b3b';
            aviso.innerHTML = '<i class="fas fa-check-circle"></i><span>Sesión cerrada correctamente. Ingrese nuevamente.</span>';

            const contenedor = document.querySelector('.card-body');
            if (contenedor && form) {
                contenedor.insertBefore(aviso, form);
                setTimeout(() => aviso.remove(), 4000);
            }
        }
    }

    function cargarUsuarioRecordado() {
        if (!rememberCheck || !username) {
            return;
        }

        const usuarioGuardado = localStorage.getItem('savedUsername');
        if (usuarioGuardado) {
            username.value = usuarioGuardado;
            rememberCheck.checked = true;
        }
    }

    function guardarUsuarioRecordado() {
        if (!rememberCheck || !username) {
            return;
        }

        if (rememberCheck.checked && username.value.trim()) {
            localStorage.setItem('savedUsername', username.value.trim());
        } else {
            localStorage.removeItem('savedUsername');
        }
    }

    function mostrarExitoTemporal(mensaje) {
        const aviso = document.createElement('div');
        aviso.className = 'success-message show';
        aviso.innerHTML = '<i class="fas fa-check-circle"></i><span></span>';

        const texto = aviso.querySelector('span');
        if (texto) {
            texto.textContent = mensaje;
        }

        const contenedor = document.querySelector('.card-body');
        if (contenedor && form) {
            contenedor.insertBefore(aviso, form);
            setTimeout(() => aviso.remove(), 5000);
        }
    }

    function actualizarEstadoCampo(campo, tieneError) {
        if (!campo) {
            return;
        }

        campo.style.borderColor = tieneError ? '#e03a3a' : '#e2e8f0';
        campo.style.backgroundColor = tieneError ? '#fff5f5' : '#fefefe';

        if (tieneError) {
            campo.addEventListener('focus', function limpiarError() {
                actualizarEstadoCampo(campo, false);
                campo.removeEventListener('focus', limpiarError);
            });
        }
    }

    function mostrarErrorTemporal(mensaje) {
        if (!errorDiv) {
            return;
        }

        const texto = errorDiv.querySelector('span');
        if (texto) {
            texto.textContent = mensaje;
        }

        errorDiv.classList.add('show');
        setTimeout(() => errorDiv.classList.remove('show'), 5000);
    }
})();
