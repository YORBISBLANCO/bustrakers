package com.proaula.aula.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Viaje {
    private String origen;
    private String destino;
    private String busPlaca;
    private String busRuta;
    private String fecha;
    private String hora;
    private int pasajeros;
    private long reservadoEn;
}
