package com.proaula.aula.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proaula.aula.document.Bus;
import com.proaula.aula.Service.BusService;

@RestController
@RequestMapping("/api/buses")
public class BusRestController {
    @Autowired
    private BusService busService;

    @GetMapping
    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/mysql")
    public List<Map<String, Object>> getAllBusesWithRuta() {
        List<Bus> buses = busService.getAllBuses();
        return buses.stream().map(bus -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", bus.getId());
            map.put("placa", bus.getPlaca());
            map.put("modelo", bus.getModelo());
            map.put("color", bus.getColor());
            map.put("conductor", bus.getConductor());
            map.put("rutaId", bus.getRutaId());
            map.put("rutaNombre", bus.getRutaNombre());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Bus getBusById(@PathVariable String id) {
        return busService.getBusById(id);
    }

    @PostMapping
    public Bus createBus(@RequestBody Bus bus) {
        return busService.saveBus(bus);
    }

    @PutMapping("/{id}")
    public Bus updateBus(@PathVariable String id, @RequestBody Bus bus) {
        bus.setId(id);
        return busService.saveBus(bus);
    }

    @DeleteMapping("/{id}")
    public void deleteBus(@PathVariable String id) {
        busService.deleteBus(id);
    }
}