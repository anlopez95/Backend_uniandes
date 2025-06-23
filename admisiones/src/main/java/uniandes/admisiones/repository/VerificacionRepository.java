package uniandes.admisiones.repository;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import uniandes.admisiones.dto.RegistroVerificacionDTO;

/**
 * Clase de tipo repositorio que se encarga de orquestar las consultas empleadas en la aplicación
 * 
 * 		**Se recomienda que si se va a trabajar con callableStatements también sean ubicados aqui o en su defecto en un entityManager dedicado para
 * 		conservar el orden de la aplicación.
 * 
 * @author Felipe
 */

@Repository
public class VerificacionRepository {
	
	 @PersistenceContext
	 private EntityManager entityManager;

	 public Map<String, Integer> consultarPagos(String program, String termCode) {
	        String sql = """
	            WITH ULTIMAS_SOLICITUDES AS (
	                SELECT SARADAP_PIDM, SARADAP_TERM_CODE_ENTRY, SARADAP_PROGRAM_1, MAX(SARADAP_APPL_NO) AS ULTIMA_APLICACION
	                FROM SARADAP
	                WHERE SARADAP_APST_CODE NOT IN ('R', 'X')
	                GROUP BY SARADAP_PIDM, SARADAP_TERM_CODE_ENTRY, SARADAP_PROGRAM_1
	            ),
	            ASPIRANTES_VALIDOS AS (
	                SELECT sa.*
	                FROM SARADAP sa
	                JOIN ULTIMAS_SOLICITUDES us
	                  ON sa.SARADAP_PIDM = us.SARADAP_PIDM
	                 AND sa.SARADAP_TERM_CODE_ENTRY = us.SARADAP_TERM_CODE_ENTRY
	                 AND sa.SARADAP_PROGRAM_1 = us.SARADAP_PROGRAM_1
	                 AND sa.SARADAP_APPL_NO = us.ULTIMA_APLICACION
	                WHERE sa.SARADAP_PROGRAM_1 = :p_program
	                  AND sa.SARADAP_TERM_CODE_ENTRY = :p_term
	            ),
	            PAGOS AS (
	                SELECT DISTINCT SARCHKL_PIDM, SARCHKL_TERM_CODE_ENTRY, SARCHKL_APPL_NO
	                FROM SARCHKL
	                WHERE SARCHKL_ADMR_CODE = 'PAGO'
	                  AND SARCHKL_RECEIVE_DATE IS NOT NULL
	            ),
	            ESTADOS AS (
	                SELECT 'PAGADO' AS ESTADO_PAGO FROM DUAL
	                UNION ALL
	                SELECT 'NO_PAGADO' FROM DUAL
	            ),
	            RESULTADOS AS (
	                SELECT
	                    CASE
	                        WHEN p.SARCHKL_PIDM IS NOT NULL THEN 'PAGADO'
	                        ELSE 'NO_PAGADO'
	                    END AS ESTADO_PAGO,
	                    av.SARADAP_PIDM
	                FROM ASPIRANTES_VALIDOS av
	                LEFT JOIN PAGOS p
	                  ON av.SARADAP_PIDM = p.SARCHKL_PIDM
	                 AND av.SARADAP_TERM_CODE_ENTRY = p.SARCHKL_TERM_CODE_ENTRY
	                 AND av.SARADAP_APPL_NO = p.SARCHKL_APPL_NO
	            )
	            SELECT e.ESTADO_PAGO, COUNT(r.SARADAP_PIDM) AS TOTAL_ASPIRANTES
	            FROM ESTADOS e
	            LEFT JOIN RESULTADOS r ON e.ESTADO_PAGO = r.ESTADO_PAGO
	            GROUP BY e.ESTADO_PAGO
	        """;

	        List<Object[]> resultList = entityManager.createNativeQuery(sql)
	                .setParameter("p_program", program)
	                .setParameter("p_term", termCode)
	                .getResultList();

	        // Mapear los resultados manualmente
	        Map<String, Integer> resultadoFinal = new HashMap<>();
	        for (Object[] row : resultList) {
	            String estado = (String) row[0];
	            int total = ((Number) row[1]).intValue();

	            if ("PAGADO".equalsIgnoreCase(estado)) {
	                resultadoFinal.put("pagaron", total);
	            } else if ("NO_PAGADO".equalsIgnoreCase(estado)) {
	                resultadoFinal.put("no_pagaron", total);
	            }
	        }

	        // Garantizar que ambos campos existan
	        resultadoFinal.putIfAbsent("pagaron", 0);
	        resultadoFinal.putIfAbsent("no_pagaron", 0);

	        return resultadoFinal;
	    }

	    public void ejecutarProcedimientoVerificacion(RegistroVerificacionDTO dto) {
	        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PU_REGISTRAR_ELEMENTO_VERIFICACION");
	        
	        query.registerStoredProcedureParameter("p_pidm", Long.class, ParameterMode.IN);
	        query.registerStoredProcedureParameter("p_term_code", String.class, ParameterMode.IN);
	        query.registerStoredProcedureParameter("p_programa", String.class, ParameterMode.IN);
	        query.registerStoredProcedureParameter("p_admr_code", String.class, ParameterMode.IN);
	        query.registerStoredProcedureParameter("p_receive_date", Date.class, ParameterMode.IN);
	        query.registerStoredProcedureParameter("p_comment", String.class, ParameterMode.IN);

	        query.setParameter("p_pidm", dto.getPidm());
	        query.setParameter("p_term_code", dto.getTerm_code());
	        query.setParameter("p_programa", dto.getAppl_no());
	        query.setParameter("p_admr_code", dto.getAmr_code());
	        query.setParameter("p_receive_date", dto.getReceive_date());
	        query.setParameter("p_comment", dto.getComment());

	        query.execute();
	    }

}
