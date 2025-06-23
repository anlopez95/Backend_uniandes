package uniandes.admisiones.business;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.admisiones.dto.RegistroVerificacionDTO;
import uniandes.admisiones.repository.VerificacionRepository;

/**
 * Clase contenedora de la lógica de negocio que se empleará como core y orquestador de procesos
 * 
 * @author Felipe
 */

@Service
@Slf4j
public class VerirficacionService {

	@Autowired
	private VerificacionRepository repository;

	@Autowired
	public void VerificacionService(VerificacionRepository repository) {
		this.repository = repository;
	}

	/**
	 * Método encargado de realizar la validacion d epagos
	 * @param program
	 * @param termCode
	 * @return
	 */
	public Map<String, Integer> consultarPagos(String program, String termCode) {

		if (program == null || program.isBlank() || termCode == null || termCode.isBlank()) {
			throw new IllegalArgumentException("El código de programa y el periodo académico son obligatorios.");
		}

		try {
			return repository.consultarPagos(program, termCode);
		} catch (Exception ex) {
			log.error("Error consultando pagos para programa={} y periodo={}", program, termCode, ex);
			throw new RuntimeException("Ocurrió un error al consultar los pagos.");
		}

	}

	/**
	 * Método encargado de la validacion de registros
	 * @param dto
	 */
	public void registrarVerificacion(RegistroVerificacionDTO dto) {
		validarDTO(dto);

		try {
			repository.ejecutarProcedimientoVerificacion(dto);
		} catch (Exception ex) {
			String mensajeLimpio = limpiarMensajeOracle(ex);
			log.error("Error registrando verificación para PIDM={}", dto.getPidm(), ex);
			throw new RuntimeException(mensajeLimpio);
		}
	}

	/**
	 * Método encargado de validar la informacion suministrada en el DTO de ingreso
	 * 
	 * @param dto
	 */
	private void validarDTO(RegistroVerificacionDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("El cuerpo de la solicitud no puede ser nulo.");
		}

		if (dto.getPidm() == null || dto.getTerm_code() == null || dto.getTerm_code().isBlank()
				|| dto.getAppl_no() == null || dto.getAppl_no().isBlank() || dto.getAmr_code() == null
				|| dto.getAmr_code().isBlank() || dto.getReceive_date() == null) {

			throw new IllegalArgumentException(
					"Todos los campos son obligatorios: pidm, termCode, program, admrCode, receiveDate.");
		}
	}
	
	private String limpiarMensajeOracle(Throwable ex) {
	    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

	    if (mensaje == null) return "Error desconocido.";

	    // Detecta "ORA-20xxx" y extrae lo que viene después del primer ":"
	    int idx = mensaje.indexOf("ORA-20");
	    if (idx >= 0) {
	        int sep = mensaje.indexOf(":", idx);
	        if (sep != -1 && sep + 2 < mensaje.length()) {
	            return mensaje.substring(sep + 2).split("\n")[0].trim();
	        }
	    }

	    // Si no detectó ORA, devolver mensaje plano
	    return mensaje.trim();
	}

}
