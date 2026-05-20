package com.proaula.aula.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.Bus;
import com.proaula.aula.Repository.mongodb.BusRepository;

@Service
public class BusUnificadoService {

    @Autowired
    private BusRepository busRepository;

    /**
     * Obtiene estadísticas de buses combinando datos de ambas bases de datos
     * @return Mapa con estadísticas: total, totalMySQL, totalMongoDB
     */
    public Map<String, Long> obtenerEstadisticasBuses() {
        Map<String, Long> estadisticas = new HashMap<>();

        // Por ahora solo MongoDB (ya que MySQL fue migrado)
        long totalMongoDB = busRepository.count();
        long totalMySQL = 0; // Los buses de MySQL fueron migrados a MongoDB

        estadisticas.put("total", totalMongoDB);
        estadisticas.put("totalMySQL", totalMySQL);
        estadisticas.put("totalMongoDB", totalMongoDB);

        return estadisticas;
    }

    /**
     * Obtiene todos los buses combinando ambas bases de datos
     * @return Lista unificada de buses
     */
    public List<Bus> obtenerTodosBuses() {
        // Por ahora solo MongoDB
        return busRepository.findAll();
    }

    /**
     * Busca buses por placa o modelo
     * @param termino Término de búsqueda
     * @return Lista de buses que coinciden
     */
    public List<Bus> buscarBuses(String termino) {
        List<Bus> resultados = new ArrayList<>();

        if (termino != null && !termino.trim().isEmpty()) {
            String terminoLower = termino.toLowerCase();
            List<Bus> todosBuses = obtenerTodosBuses();

            for (Bus bus : todosBuses) {
                if ((bus.getPlaca() != null && bus.getPlaca().toLowerCase().contains(terminoLower)) ||
                    (bus.getModelo() != null && bus.getModelo().toLowerCase().contains(terminoLower))) {
                    resultados.add(bus);
                }
            }
        } else {
            resultados = obtenerTodosBuses();
        }

        return resultados;
    }
}