package com.wms.gestionalmaceng01.models;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") 
    private Long idusuario;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "correo", unique = true)
    private String correo;
    @Column(name = "clave")
    private String clave;
    @Column(name = "rol")
    private String rol;
    @Column(name = "estado")
    private boolean activo = true;
    @Column(name = "intentos_fallidos")
    private int intentosFallidos = 0;
    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    public Usuario() {}

    public Usuario(String nombre, String correo, String clave, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
        this.rol = rol;
    }

    public boolean estaBloqueado() {
        if (bloqueadoHasta == null) return false;
        return LocalDateTime.now().isBefore(bloqueadoHasta);
    }

    public long getSegundosBloqueo() {
        if (bloqueadoHasta == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), bloqueadoHasta).getSeconds();
    }

    public void registrarIntentoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 3) {
            this.bloqueadoHasta = LocalDateTime.now().plusSeconds(30);
        }
    }

    public void reiniciarIntentos() {
        this.intentosFallidos = 0;
        this.bloqueadoHasta = null;
    }

    // Getters y Setters
    public Long getIdusuario() { return idusuario; }
    public void setIdusuario(Long idusuario) { this.idusuario = idusuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
}