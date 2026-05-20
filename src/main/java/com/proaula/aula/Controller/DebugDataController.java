package com.proaula.aula.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.proaula.aula.document.Ruta;
import com.proaula.aula.document.Bus;
import com.proaula.aula.document.Parada;
import com.proaula.aula.Service.RutaService;
import com.proaula.aula.Service.BusService;
import com.proaula.aula.Service.ParadaService;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/debug")
public class DebugDataController {
    
    @Autowired
    private RutaService rutaService;
    
    @Autowired
    private BusService busService;
    
    @Autowired
    private ParadaService paradaService;
    
    /**
     * Endpoint para crear datos de prueba
     * GET /api/debug/crear-datos-prueba
     */
    @GetMapping("/crear-datos-prueba")
    public Map<String, Object> crearDatosPrueba() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // 1. Crear rutas de prueba
            List<Ruta> rutas = new ArrayList<>();
            
            Ruta ruta1 = new Ruta();
            ruta1.setNombre("Ruta Centro - Morros");
            ruta1.setHoraAproximada(LocalTime.of(8, 30));
            ruta1.setBarrios(Arrays.asList("Centro", "Morros", "Bosques"));
            ruta1.setActiva(true);
            ruta1 = rutaService.saveRuta(ruta1);
            rutas.add(ruta1);
            
            // 2. Crear paradas para la ruta1
            List<Parada> paradas = new ArrayList<>();
            
            Parada parada1 = new Parada();
            parada1.setNombre("Terminal Centro");
            parada1.setUbicacion("Calle 10 #5-20");
            parada1.setReferencia("Frente a Carrefour");
            parada1.setOrden(1);
            parada1.setRutaId(ruta1.getId());
            parada1.setRutaNombre(ruta1.getNombre());
            parada1.setActiva(true);
            parada1 = paradaService.saveParada(parada1);
            paradas.add(parada1);
            
            Parada parada2 = new Parada();
            parada2.setNombre("Parada Morros");
            parada2.setUbicacion("Cra 30 #80-50");
            parada2.setReferencia("Esquina con Cra 31");
            parada2.setOrden(2);
            parada2.setRutaId(ruta1.getId());
            parada2.setRutaNombre(ruta1.getNombre());
            parada2.setActiva(true);
            parada2 = paradaService.saveParada(parada2);
            paradas.add(parada2);
            
            Parada parada3 = new Parada();
            parada3.setNombre("Parada Bosques");
            parada3.setUbicacion("Cra 40 #90-10");
            parada3.setReferencia("Parque Bosques");
            parada3.setOrden(3);
            parada3.setRutaId(ruta1.getId());
            parada3.setRutaNombre(ruta1.getNombre());
            parada3.setActiva(true);
            parada3 = paradaService.saveParada(parada3);
            paradas.add(parada3);
            
            // 3. Crear buses para la ruta1
            List<Bus> buses = new ArrayList<>();
            
            Bus bus1 = new Bus();
            bus1.setPlaca("ABC-123");
            bus1.setMarca("Volvo");
            bus1.setModelo("B8R");
            bus1.setColor("Azul");
            bus1.setConductor("Juan Pérez");
            bus1.setRutaId(ruta1.getId());
            bus1.setRutaNombre(ruta1.getNombre());
            bus1.setEstado("activo");
            bus1.setActivo(true);
            bus1 = busService.saveBus(bus1);
            buses.add(bus1);
            
            Bus bus2 = new Bus();
            bus2.setPlaca("XYZ-789");
            bus2.setMarca("Mercedes");
            bus2.setModelo("OH-1628");
            bus2.setColor("Rojo");
            bus2.setConductor("Carlos López");
            bus2.setRutaId(ruta1.getId());
            bus2.setRutaNombre(ruta1.getNombre());
            bus2.setEstado("activo");
            bus2.setActivo(true);
            bus2 = busService.saveBus(bus2);
            buses.add(bus2);
            
            // 4. Actualizar ruta con buses y paradas embebidos
            ruta1.setBuses(buses);
            ruta1.setParadas(paradas);
            ruta1 = rutaService.saveRuta(ruta1);
            
            resultado.put("success", true);
            resultado.put("mensaje", "Datos de prueba creados exitosamente");
            resultado.put("rutaCreada", ruta1.getNombre());
            resultado.put("busesCreados", buses.size());
            resultado.put("paradasCreadas", paradas.size());
            resultado.put("rutaId", ruta1.getId());
            resultado.put("debugUrl", "/debug/ruta/" + ruta1.getId());
            resultado.put("detalleUrl", "/ruta/" + ruta1.getId());
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("error", e.getMessage());
            resultado.put("stackTrace", Arrays.toString(e.getStackTrace()));
        }
        
        return resultado;
    }
    
    /**
     * Endpoint para limpiar datos de prueba
     * GET /api/debug/limpiar-datos
     */
    @GetMapping("/limpiar-datos")
    public Map<String, Object> limpiarDatos() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            rutaService.deleteAllRutas();
            resultado.put("success", true);
            resultado.put("mensaje", "Datos eliminados");
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("error", e.getMessage());
        }
        
        return resultado;
    }
}
