package com.proaula.aula.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auditoria_logs")
public class AuditoriaLog {

    @Id
    private String id;
    
    private String tipo; 
    
    private String descripcion;
    
    private String usuario; 
    
    private String idEntidad; 
    
    private LocalDateTime fecha;
    
    private String ip; 
    
    private String detalles; 

    // Constructores
    public AuditoriaLog() {
        this.fecha = LocalDateTime.now();
    }

    public AuditoriaLog(String tipo, String descripcion, String usuario, String idEntidad) {
        this();
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.idEntidad = idEntidad;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(String idEntidad) {
        this.idEntidad = idEntidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "AuditoriaLog [id=" + id + ", tipo=" + tipo + ", usuario=" + usuario + ", fecha=" + fecha + "]";
    }
}
