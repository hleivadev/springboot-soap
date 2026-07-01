package com.soap.springbootsoap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.soap.client.SoapClient;
import com.soap.exception.CalculatorServiceException;

// "com.soap.client" is intentionally not scanned: SoapClient has no
// @Component/@Service annotation, it's built manually as a @Bean in SoapConfig
// (it needs its marshaller/unmarshaller wired by hand, not by field injection).
@SpringBootApplication(scanBasePackages = { "com.soap.springbootsoap", "com.soap.config" })
public class SpringbootsoapApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringbootsoapApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringbootsoapApplication.class, args);
    }

    @Bean
    CommandLineRunner init(SoapClient soapClient) {
        return args -> {
            try {
                LOGGER.info("2 + 2 = {}", soapClient.add(2, 2));
                LOGGER.info("10 - 4 = {}", soapClient.subtract(10, 4));
                LOGGER.info("6 x 7 = {}", soapClient.multiply(6, 7));
                LOGGER.info("20 / 5 = {}", soapClient.divide(20, 5));
            } catch (CalculatorServiceException ex) {
                LOGGER.error("No se pudo completar la demo del cliente SOAP: {}", ex.getMessage());
            }
        };
    }

}
