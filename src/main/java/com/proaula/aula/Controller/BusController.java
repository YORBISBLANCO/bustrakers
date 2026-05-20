package com.proaula.aula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proaula.aula.document.Bus;
import com.proaula.aula.Service.BusService;
import com.proaula.aula.Service.RutaService;

@Controller
public class BusController {
    @Autowired
    private BusService busService;
    @Autowired
    private RutaService rutaService;

    // Vistas para administración (templates/Admin/*)
    @GetMapping("/registro-buses")
    public String registroBuses(Model model) {
        model.addAttribute("bus", new Bus());
        model.addAttribute("rutas", rutaService.getAllRutas());
        return "Admin/registro_de_buses";
    }

    @GetMapping("/registro_de_buses")
    public String registroBusesLegacy() {
        return "redirect:/registro-buses";
    }

    @PostMapping("/registro-buses")
    public String registrarBus(@ModelAttribute Bus bus) {
        if (bus.getRutaId() != null && !bus.getRutaId().trim().isEmpty()) {
            var ruta = rutaService.getRutaById(bus.getRutaId());
            if (ruta != null) {
                bus.setRutaNombre(ruta.getNombre());
            }
        }
        busService.saveBus(bus);
        return "redirect:/index_2";
    }

    @GetMapping("/actualizarbuses")
    public String actualizarBuses(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        return "Admin/actualizarbuses";
    }

    @GetMapping("/actualizar-bus/{id}")
    public String editarBus(@PathVariable String id, Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        model.addAttribute("bus", busService.getBusById(id));
        model.addAttribute("rutas", rutaService.getAllRutas());
        return "Admin/actualizarbuses";
    }

    @PostMapping("/actualizar-bus/{id}")
    public String actualizarBus(@PathVariable String id, @ModelAttribute Bus bus) {
        try {
            bus.setId(id);
            if (bus.getRutaId() != null && !bus.getRutaId().trim().isEmpty()) {
                var ruta = rutaService.getRutaById(bus.getRutaId());
                if (ruta != null) bus.setRutaNombre(ruta.getNombre());
            }
            busService.saveBus(bus);
            return "redirect:/actualizar-bus/" + id;
        } catch (Exception e) {
            // en caso de error, redirigir al listado y podría agregarse un mensaje de error
            return "redirect:/actualizar-bus/" + id;
        }
    }

    @GetMapping("/eliminarbuses")
    public String eliminarBuses(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        return "Admin/eliminarbuses";
    }

    @PostMapping("/eliminar-buses")
    public String eliminarBus(@RequestParam String busId) {
        busService.deleteBus(busId);
        return "redirect:/actualizarbuses";
    }

    @GetMapping("/eliminar-bus/{id}")
    public String eliminarBusPorId(@PathVariable String id) {
        busService.deleteBus(id);
        return "redirect:/actualizarbuses";
    }

    @GetMapping("/actualizar-bus")
    public String actualizarBusRedirect() {
        return "redirect:/actualizarbuses";
    }
}