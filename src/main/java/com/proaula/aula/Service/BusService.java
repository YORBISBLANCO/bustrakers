package com.proaula.aula.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.Bus;
import com.proaula.aula.Repository.mongodb.BusRepository;

@Service
public class BusService {
    @Autowired
    private BusRepository busRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBusById(String id) {
        return busRepository.findById(id).orElse(null);
    }

    public Bus saveBus(Bus bus) {
        return busRepository.save(bus);
    }

    public void deleteBus(String id) {
        busRepository.deleteById(id);
    }
    
    public long count() {
        return busRepository.count();
    }

    public List<Bus> getBusesByRutaId(String rutaId) {
        return busRepository.findByRutaId(rutaId);
    }

    public long countActiveBuses() {
        return busRepository.findByActivoTrue().size();
    }
}