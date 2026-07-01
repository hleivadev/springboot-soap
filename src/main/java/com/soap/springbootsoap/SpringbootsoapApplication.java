package com.soap.springbootsoap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.soap.client.SoapClient;
import com.soap.wsdl.AddResponse;

@SpringBootApplication(scanBasePackages = {"com.soap.springbootsoap", "com.soap.client", "com.soap.config"})
public class SpringbootsoapApplication {

	private static final Logger LOOGER = LoggerFactory.getLogger(SpringbootsoapApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootsoapApplication.class, args);
	}
	@Bean
	CommandLineRunner init(SoapClient soapClient){
		return args -> {
			AddResponse addResponse = soapClient.getAddResponse(2, 2);
			LOOGER.info("El resultado de la suma de los numeros  {} y {} es {}", 2 , 2 ,addResponse.getAddResult() );
		};

	}

}
