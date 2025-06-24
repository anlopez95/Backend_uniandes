package uniandes.admisiones.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uniandes.admisiones.dto.RegistroVerificacionDTO;
import uniandes.admisiones.repository.VerificacionRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerirficacionServiceTest {

    @Mock
    private VerificacionRepository repository;

    @InjectMocks
    private VerirficacionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consultarPagos_deberiaRetornarResultados() {
        Map<String, Integer> mockResultado = new HashMap<>();
        mockResultado.put("pagaron", 10);
        mockResultado.put("no_pagaron", 5);

        when(repository.consultarPagos("PG-SIST", "202401")).thenReturn(mockResultado);

        Map<String, Integer> resultado = service.consultarPagos("PG-SIST", "202401");

        assertEquals(10, resultado.get("pagaron"));
        assertEquals(5, resultado.get("no_pagaron"));
    }

    @Test
    void consultarPagos_conParametrosInvalidos_deberiaLanzarExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.consultarPagos("", null));

        assertEquals("El código de programa y el periodo académico son obligatorios.", ex.getMessage());
    }

    @Test
    void registrarVerificacion_dtoValido_deberiaLlamarRepositorio() {
        RegistroVerificacionDTO dto = new RegistroVerificacionDTO();
        dto.setPidm(1234L);
        dto.setTerm_code("202401");
        dto.setAppl_no("PG-ADMI");
        dto.setAmr_code("PAGO");
        dto.setReceive_date(java.sql.Date.valueOf(LocalDate.of(2025, 6, 23)));
        dto.setComment("Comentario de prueba");

        service.registrarVerificacion(dto);

        verify(repository, times(1)).ejecutarProcedimientoVerificacion(dto);
    }

    @Test
    void registrarVerificacion_dtoInvalido_deberiaLanzarExcepcion() {
        RegistroVerificacionDTO dto = new RegistroVerificacionDTO(); // dto vacío

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.registrarVerificacion(dto));

        assertTrue(ex.getMessage().contains("Todos los campos son obligatorios"));
    }

    @Test
    void registrarVerificacion_errorRepositorio_deberiaPropagarExcepcion() {
        RegistroVerificacionDTO dto = new RegistroVerificacionDTO();
        dto.setPidm(1234L);
        dto.setTerm_code("202401");
        dto.setAppl_no("PG-ADMI");
        dto.setAmr_code("PAGO");
        dto.setReceive_date(java.sql.Date.valueOf(LocalDate.of(2025, 6, 23)));
        dto.setComment("Comentario de prueba");

        doThrow(new RuntimeException("ORA-20001: Error simulado"))
                .when(repository).ejecutarProcedimientoVerificacion(dto);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.registrarVerificacion(dto));

        assertTrue(ex.getMessage().contains("Error simulado"));
    }
}
