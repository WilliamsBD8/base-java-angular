package com.usco.convocatoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.usco.convocatoria")
@EnableJpaRepositories(basePackages = "com.usco.convocatoria")
public class ConvocatoriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConvocatoriaApplication.class, args);
	}

}
