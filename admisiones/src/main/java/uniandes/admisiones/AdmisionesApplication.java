package uniandes.admisiones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "uniandes.admisiones")
public class AdmisionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdmisionesApplication.class, args);
	}

}
