package uniandes.admisiones.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uniandes.admisiones.dto.RegistroVerificacionDTO;

@SpringBootTest
class VerificacionRepositoryTest {

    private VerificacionRepository repository;
    private EntityManager entityManager;
    private StoredProcedureQuery storedProcedureQuery;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        storedProcedureQuery = mock(StoredProcedureQuery.class);

        repository = new VerificacionRepository();
        repository = spy(repository);  // para setear el EntityManager manualmente

        // Forzar inyección manual del EntityManager
        try {
            var field = VerificacionRepository.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repository, entityManager);
        } catch (Exception e) {
            fail("No se pudo inyectar entityManager en el repositorio");
        }
    }

    @Test
    void testEjecutarProcedimientoVerificacion() {
        // Arrange
        RegistroVerificacionDTO dto = new RegistroVerificacionDTO();
        dto.setPidm(1003L);
        dto.setTerm_code("202401");
        dto.setAppl_no("PG-ADMI");
        dto.setAmr_code("PAGO");
        dto.setReceive_date(Date.valueOf(LocalDate.of(2025, 6, 23)));
        dto.setComment("Pago de inscripción $130.000");

        when(entityManager.createStoredProcedureQuery("PU_REGISTRAR_ELEMENTO_VERIFICACION"))
            .thenReturn(storedProcedureQuery);

        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
            .thenReturn(null);  // register devuelve void

        when(storedProcedureQuery.setParameter(anyString(), any()))
            .thenReturn(storedProcedureQuery);

        when(storedProcedureQuery.execute())
            .thenReturn(true);

        // Act
        assertDoesNotThrow(() -> repository.ejecutarProcedimientoVerificacion(dto));

        // Assert: verifica que todos los parámetros hayan sido seteados correctamente
        verify(storedProcedureQuery).setParameter("p_pidm", dto.getPidm());
        verify(storedProcedureQuery).setParameter("p_term_code", dto.getTerm_code());
        verify(storedProcedureQuery).setParameter("p_programa", dto.getAppl_no());
        verify(storedProcedureQuery).setParameter("p_admr_code", dto.getAmr_code());
        verify(storedProcedureQuery).setParameter("p_receive_date", dto.getReceive_date());
        verify(storedProcedureQuery).setParameter("p_comment", dto.getComment());
    }
}
