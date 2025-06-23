package uniandes.admisiones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AppConfiguracion {
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactiva CSRF (útil para APIs REST)
            .csrf(csrf -> csrf.disable())

            // Configura las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/operaciones-verificacion/**").permitAll() // acceso libre
                .anyRequest().authenticated() // el resto requiere autenticación
            )

            // Habilita autenticación básica (útil para pruebas rápidas con Postman o curl)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
