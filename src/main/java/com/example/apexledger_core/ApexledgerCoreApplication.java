package com.example.apexledger_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//This annotation tells Java that this is a Spring Boot project.
// It automatically sets up all behind the scene configurations needed.
@SpringBootApplication
public class ApexledgerCoreApplication {

	//Main entry point of entire backend application. On clicking "run", Java looks for this method to start the engine.
	public static void main(String[] args) {

		//Spins the embedded Tomcat web server.
		//Initializes endpoints, connects to databse and opens 8080 so that the app can start listening to API requests.
		SpringApplication.run(ApexledgerCoreApplication.class, args);
	}

}
