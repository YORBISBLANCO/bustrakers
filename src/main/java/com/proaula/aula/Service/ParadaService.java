package com.proaula.aula.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.Parada;
import com.proaula.aula.Repository.mongodb.ParadaRepository;

@Service
public class ParadaService {
    @Autowired
    private ParadaRepository paradaRepository;

    public List<Parada> getAllParadas() {
        return paradaRepository.findAll();
    }

    public List<Parada> getParadasByRutaId(String rutaId) {
        return paradaRepository.findByRutaId(rutaId);
    }

    public List<Parada> getParadasByRutaNombre(String rutaNombre) {
        return paradaRepository.findByRutaNombreOrderByOrdenAsc(rutaNombre);
    }

    public Parada saveParada(Parada parada) {
        return paradaRepository.save(parada);
    }

    public void deleteParada(String id) {
        paradaRepository.deleteById(id);
    }
}
