package uniandes.admisiones.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uniandes.admisiones.business.VerirficacionService;
import uniandes.admisiones.dto.RegistroVerificacionDTO;

/**
 * Esta clase contiene los endpoints que expone la aplicación
 * @author Felipe
 */

@RestController
@RequestMapping("/operaciones-verificacion")
public class OperacionesVerificacionController {
	
	@Autowired
    private VerirficacionService service;
	
	@GetMapping("/consulta-pagos")
    public ResponseEntity<?> consultarPagos(@RequestParam String program, @RequestParam String term_code) {
        try {
            var datos = service.consultarPagos(program, term_code);
            Map<String, Object> response = new HashMap<>();
            response.put("program", program);
            response.put("term_code", term_code);
            response.putAll(datos);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", "Error interno del servidor."));
        }
    }

    @PostMapping("/registro-verificacion")
    public ResponseEntity<?> registrar(@RequestBody RegistroVerificacionDTO dto) {
        try {
            service.registrarVerificacion(dto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Registro actualizado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        } catch (RuntimeException  e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }catch (Exception  e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", "Código admisión inválido o aspirante sin aplicación activa"));
        }
    }

}
