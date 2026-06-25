 
 /* nosonar: javascript:S3776 */
 (function() {
        // --- Manejo de errores desde Spring Security (parámetro ?error) 
        const urlParams = new URLSearchParams(window.location.search);
        const hasError = urlParams.get('error');
        const errorDiv = document.getElementById('errorMsg');
        
        if (hasError === 'true' || hasError === '') {
            errorDiv.classList.add('show');
            setTimeout(() => {
                if(errorDiv) errorDiv.classList.remove('show');
            }, 5000);
        } else {
            const logoutParam = urlParams.get('logout');
            if(logoutParam !== null) {
                const tempNotice = document.createElement('div');
                tempNotice.className = 'error-message show';
                tempNotice.style.background = "#e0f2e9";
                tempNotice.style.borderLeftColor = "#2c7a4d";
                tempNotice.style.color = "#1e6b3b";
                tempNotice.innerHTML = '<i class="fas fa-check-circle"></i><span>Sesión cerrada correctamente. Ingrese nuevamente.</span>';
                const formContainer = document.querySelector('.card-body');
                if(formContainer && !document.getElementById('logoutMessage')) {
                    tempNotice.id = 'logoutMessage';
                    formContainer.insertBefore(tempNotice, document.getElementById('loginForm'));
                    setTimeout(() => tempNotice.remove(), 4000);
                }
            }
        }

        // --- Validación básica front-end
        const form = document.getElementById('loginForm');
        if(form) {
            form.addEventListener('submit', function(e) {
                const username = document.getElementById('username');
                const password = document.getElementById('password');
                let hasEmpty = false;
                if(!username.value.trim()) {
                    highlightInput(username);
                    hasEmpty = true;
                } else {
                    removeHighlight(username);
                }
                if(!password.value.trim()) {
                    highlightInput(password);
                    hasEmpty = true;
                } else {
                    removeHighlight(password);
                }
                if(hasEmpty) {
                    e.preventDefault();
                    const errBox = document.getElementById('errorMsg');
                    errBox.querySelector('span').innerText = 'Por favor complete usuario y contraseña.';
                    errBox.classList.add('show');
                    setTimeout(() => errBox.classList.remove('show'), 3500);
                    setTimeout(() => {
                        if(errBox.querySelector('span')) 
                            errBox.querySelector('span').innerText = 'Usuario o contraseña incorrectos. Intente de nuevo.';
                    }, 3600);
                    return false;
                }
                
                const formAction = form.getAttribute('action');
                if(formAction === '#' || formAction === '') {
                    e.preventDefault();
                    if(username.value.trim() === 'demo' && password.value.trim() === '123456') {
                        alert('Bienvenido al sistema WMS (Demo). En un entorno real se redirigiría al dashboard.');
                        window.location.href = '/dashboard';
                    } else {
                        const errBox = document.getElementById('errorMsg');
                        errBox.querySelector('span').innerText = 'Credenciales inválidas. Pruebe usuario: demo / contraseña: 123456';
                        errBox.classList.add('show');
                        setTimeout(() => {
                            if(errBox.querySelector('span')) 
                                errBox.querySelector('span').innerText = 'Usuario o contraseña incorrectos. Intente de nuevo.';
                        }, 4000);
                    }
                }
            });
        }
        
        function highlightInput(inputElement) {
            if(!inputElement) return;
            inputElement.style.borderColor = "#e03a3a";
            inputElement.style.backgroundColor = "#fff5f5";
            inputElement.addEventListener('focus', function onFocus() {
                removeHighlight(inputElement);
                inputElement.removeEventListener('focus', onFocus);
            });
        }
        
        function removeHighlight(inputElement) {
            if(!inputElement) return;
            inputElement.style.borderColor = "#e2e8f0";
            inputElement.style.backgroundColor = "#fefefe";
        }
        
        // --- MODAL: "Olvidó su contraseña" completamente funcional y estilizado como en el modelo que enviaste
        const forgotBtn = document.getElementById('forgotPasswordLink');
        const modalElement = document.getElementById('forgotPasswordModal');
        let modalInstance = null;
        
        if(modalElement) {
            modalInstance = new bootstrap.Modal(modalElement);
        }
        
        if(forgotBtn) {
            forgotBtn.addEventListener('click', function(e) {
                e.preventDefault();
                // Limpiar mensajes previos del modal
                const modalErrorDiv = document.getElementById('modalErrorMsg');
                const modalSuccessDiv = document.getElementById('modalSuccessMsg');
                const recoveryInput = document.getElementById('recoveryEmail');
                if(modalErrorDiv) modalErrorDiv.style.display = 'none';
                if(modalSuccessDiv) modalSuccessDiv.style.display = 'none';
                if(recoveryInput) recoveryInput.value = '';
                if(modalInstance) modalInstance.show();
            });
        }
        
        // Lógica de envío del formulario de recuperación (simulación, pero perfectamente integrable con backend Spring Boot)
        const sendRecoveryBtn = document.getElementById('sendRecoveryBtn');
        if(sendRecoveryBtn) {
            sendRecoveryBtn.addEventListener('click', function() {
                const recoveryInput = document.getElementById('recoveryEmail');
                const emailOrUser = recoveryInput ? recoveryInput.value.trim() : '';
                const modalErrorDiv = document.getElementById('modalErrorMsg');
                const modalSuccessDiv = document.getElementById('modalSuccessMsg');
                const modalErrorText = document.getElementById('modalErrorText');
                const modalSuccessText = document.getElementById('modalSuccessText');
                
                // Ocultar mensajes previos
                if(modalErrorDiv) modalErrorDiv.style.display = 'none';
                if(modalSuccessDiv) modalSuccessDiv.style.display = 'none';
                
                if(!emailOrUser) {
                    if(modalErrorText) modalErrorText.innerText = 'Por favor ingrese su correo electrónico o nombre de usuario.';
                    if(modalErrorDiv) modalErrorDiv.style.display = 'block';
                    return;
                }
                
                // Simular envío de enlace de recuperación (en un entorno real, aquí se haría una petición fetch al backend)
                // Para mantener coherencia con el diseño y funcionamiento, se simula una llamada exitosa.
                // Se puede cambiar por una llamada fetch a /api/recuperar-contrasena
                const btnOriginalText = sendRecoveryBtn.innerHTML;
                sendRecoveryBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Enviando...';
                sendRecoveryBtn.disabled = true;
                
                // Simulación de respuesta del servidor (delay de 1.5 segundos)
                setTimeout(() => {
                    // Aquí se integraría con Spring Boot: fetch('/api/recuperar', { method: 'POST', body: JSON.stringify({ contacto: emailOrUser }) })
                    // Por ahora demostración exitosa
                    if(modalSuccessText) modalSuccessText.innerText = `¡Correo enviado! Hemos enviado instrucciones a ${emailOrUser} para restablecer tu contraseña.`;
                    if(modalSuccessDiv) modalSuccessDiv.style.display = 'block';
                    
                    sendRecoveryBtn.innerHTML = btnOriginalText;
                    sendRecoveryBtn.disabled = false;
                    
                    // Opcional: limpiar campo después de éxito y cerrar modal tras unos segundos
                    setTimeout(() => {
                        if(modalInstance) modalInstance.hide();
                        // Mostrar notificación fuera del modal (opcional)
                        const tempToast = document.createElement('div');
                        tempToast.style.position = 'fixed';
                        tempToast.style.bottom = '20px';
                        tempToast.style.right = '20px';
                        tempToast.style.backgroundColor = '#255EAF';
                        tempToast.style.color = 'white';
                        tempToast.style.padding = '12px 20px';
                        tempToast.style.borderRadius = '40px';
                        tempToast.style.fontSize = '0.85rem';
                        tempToast.style.zIndex = '9999';
                        tempToast.style.boxShadow = '0 8px 20px rgba(0,0,0,0.2)';
                        tempToast.innerHTML = '<i class="fas fa-envelope me-2"></i> Revisa tu bandeja de entrada para restablecer tu contraseña.';
                        document.body.appendChild(tempToast);
                        setTimeout(() => tempToast.remove(), 4000);
                    }, 1800);
                    
                    if(recoveryInput) recoveryInput.value = '';
                }, 1500);
            });
        }
        
        // Limpiar mensajes al cerrar modal
        if(modalElement) {
            modalElement.addEventListener('hidden.bs.modal', function() {
                const modalErrorDiv = document.getElementById('modalErrorMsg');
                const modalSuccessDiv = document.getElementById('modalSuccessMsg');
                const recoveryInput = document.getElementById('recoveryEmail');
                if(modalErrorDiv) modalErrorDiv.style.display = 'none';
                if(modalSuccessDiv) modalSuccessDiv.style.display = 'none';
                if(recoveryInput) recoveryInput.value = '';
            });
        }
        
        // Recordarme: persistencia en localStorage para prellenar usuario
        const rememberCheck = document.getElementById('rememberMe');
        const userField = document.getElementById('username');
        if(rememberCheck && userField && localStorage.getItem('savedUsername')) {
            const savedUser = localStorage.getItem('savedUsername');
            if(savedUser) {
                userField.value = savedUser;
                rememberCheck.checked = true;
            }
        }
        
        if(form) {
            form.addEventListener('submit', function() {
                if(rememberCheck && rememberCheck.checked && userField.value.trim()) {
                    localStorage.setItem('savedUsername', userField.value.trim());
                } else if(rememberCheck && !rememberCheck.checked) {
                    localStorage.removeItem('savedUsername');
                }
            });
        }
    })();