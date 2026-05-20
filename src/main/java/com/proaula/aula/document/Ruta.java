package com.proaula.aula.document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.proaula.aula.document.Bus;
import com.proaula.aula.document.Parada;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "rutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {
    @Id
    private String id;

    @NotBlank
    private String nombre;
    
    private LocalTime horaAproximada;

    private List<String> barrios;
    private List<Bus> buses = new ArrayList<>();
    private List<Parada> paradas = new ArrayList<>();

    private long fechaCreacion = System.currentTimeMillis();
    private boolean activa = true;

    // Getters y setters manuales
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalTime getHoraAproximada() { return horaAproximada; }
    public void setHoraAproximada(LocalTime horaAproximada) { this.horaAproximada = horaAproximada; }

    public List<String> getBarrios() { return barrios; }
    public void setBarrios(List<String> barrios) { this.barrios = barrios; }

    public List<Bus> getBuses() { return buses; }
    public void setBuses(List<Bus> buses) { this.buses = buses; }

    public List<Parada> getParadas() { return paradas; }
    public void setParadas(List<Parada> paradas) { this.paradas = paradas; }

    public long getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(long fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
