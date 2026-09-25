package com.coelhotechne.detection_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DetectionSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DetectionSystemApplication.class, args);
	}

}
